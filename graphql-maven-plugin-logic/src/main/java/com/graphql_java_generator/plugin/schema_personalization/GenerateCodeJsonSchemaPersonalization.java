/**
 * 
 */
package com.graphql_java_generator.plugin.schema_personalization;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql_java_generator.plugin.DocumentParser;
import com.graphql_java_generator.plugin.conf.CommonConfiguration;
import com.graphql_java_generator.plugin.conf.GenerateCodeCommonConfiguration;
import com.graphql_java_generator.plugin.generate_code.GenerateCodeDocumentParser;
import com.graphql_java_generator.plugin.generate_schema.GenerateGraphQLSchemaPluginExecutor;
import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.FieldTypeAST;
import com.graphql_java_generator.plugin.language.Type;
import com.graphql_java_generator.plugin.language.impl.AbstractType;
import com.graphql_java_generator.plugin.language.impl.FieldImpl;
import com.graphql_java_generator.plugin.language.impl.ObjectType;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

/**
 * This tool contains the logic which allows the plugin user to personnalize the code generation. It allows to:
 * <UL>
 * <LI>Add specific fields. For instance, fields used for foreign keys</LI>
 * <LI>Add or replace entity annotation. For instance remove the JPA Entity annotation on a generated Entity. In wich
 * can, the developper can inherit from the generated entity, and oerride everything</LI>
 * <LI>Add or replace field annotation. For instance to change the JPA behavior, specify a column name for a
 * field...</LI>
 * </UL>
 * 
 * @author etienne-sf
 */
@Component
public class GenerateCodeJsonSchemaPersonalization {

	private static final Logger logger = LoggerFactory.getLogger(GenerateGraphQLSchemaPluginExecutor.class);

	static final String JSON_SCHEMA_FILENAME = "schema_personalization.schema.json";

	@Autowired
	DocumentParser documentParser;

	@Autowired
	CommonConfiguration configuration;

	/** The class where the content of the configuration file will be loaded */
	SchemaPersonalization schemaPersonalization = null;

	/**
	 * This is the 'main' method for this class: it loads the schema personalization from the json user file, and update
	 * what the {@link GenerateCodeDocumentParser} has already loaded according to the user's needs.
	 */
	public void applySchemaPersonalization() {

		// First step: we load the schema personalization
		if (getSchemaPersonalization() != null) {

			// Then, we apply what has been loaded from the json file
			for (EntityPersonalization objectPers : schemaPersonalization.getEntityPersonalizations()) {
				for (AbstractType objectType : findGraphQLTypesFromName(objectPers.getName())) {

					// Should we add an annotation ?
					if (objectPers.getAddAnnotation() != null) {
						objectType.addAnnotation(objectPers.getAddAnnotation());
					}

					// Should we add implemented interfaces ?
					if (objectPers.getAddInterface() != null) {
						for (String anInterface : objectPers.getAddInterface().split(",")) {
							objectType.getAdditionalInterfaces().add(anInterface.trim());
						}
					}

					// Only Types and Interfaces may have field personalization
					if (!(objectType instanceof ObjectType)) {
						// There should be no field personalization
						if (objectPers.getNewFields().size() > 0 || objectPers.getFieldPersonalizations().size() > 0) {
							throw new RuntimeException("The '" + objectType.getName() + "' is a "
									+ objectType.getClass().getSimpleName()
									+ ". As such, it doesn't accept field personalization. Please check the content of you schema personalization file");
						}
					} else {
						// Let's add all new fields
						for (com.graphql_java_generator.plugin.schema_personalization.FieldPersonalization field : objectPers
								.getNewFields()) {
							// The field's name must be valid GraphQL name
							checkGraphQLName(field.getName());

							// There must not be any field of that name in that object
							if (checkIfFieldExists((ObjectType) objectType, field.getName())) {
								throw new RuntimeException("The object " + objectType.getName()
										+ " already has a field of name " + field.getName());
							}
							// The field's type must exist.
							try {
								documentParser.getType(field.getType());
							} catch (RuntimeException e) {
								throw new RuntimeException("Error while applying the schema personalization file. The '"
										+ field.getType() + "' of the field '" + field.getName()
										+ "': unknown type (not a standard GraphQL type, nor a type defined in the GraphQL schema)",
										e);
							}

							// Ok, we can add this new field
							FieldImpl newField;
							if (field.getList() != null && field.getList()) {
								// The new field is a list
								FieldTypeAST listItem = FieldTypeAST.builder().graphQLTypeSimpleName(field.getType())
										.build();
								FieldTypeAST list = FieldTypeAST.builder().listDepth(1).listItemFieldTypeAST(listItem)
										.mandatory(field.getMandatory()).build();
								newField = FieldImpl.builder().documentParser(documentParser).name(field.getName())
										.owningType(objectType).fieldTypeAST(list).build();
							} else {
								// The new field is not a list
								newField = FieldImpl.builder().documentParser(documentParser).name(field.getName())
										.id(field.getId()).owningType(objectType)
										.fieldTypeAST(FieldTypeAST.builder().graphQLTypeSimpleName(field.getType())
												.mandatory(field.getMandatory()).build())//
										.build();
							}

							if (field.getAddAnnotation() != null) {
								newField.addAnnotation(field.getAddAnnotation());
							}
							objectType.getFields().add(newField);
						} // for newFields

						// Let's add personalize existing fields
						for (com.graphql_java_generator.plugin.schema_personalization.FieldPersonalization field : objectPers
								.getFieldPersonalizations()) {
							// Ok, we can add the field to personalize. This will throw an exception if not found
							FieldImpl existingField = (FieldImpl) findFieldFromName((ObjectType) objectType,
									field.getName());

							existingField.setName(field.getName());
							if (field.getList() != null
									&& (field.getList() != (existingField.getFieldTypeAST().getListDepth() > 0))) {
								// The list attribute changed
								if (field.getList()) {
									// It's now a list (and it wasn't before)
									FieldTypeAST list = FieldTypeAST.builder().listDepth(1)
											.listItemFieldTypeAST(existingField.getFieldTypeAST()).build();
									existingField.setFieldTypeAST(list);
								} else {
									// It's no more a list
									existingField
											.setFieldTypeAST(existingField.getFieldTypeAST().getListItemFieldTypeAST());
								}
							}
							if (field.getType() != null) {
								// The field's type must exist.
								try {
									documentParser.getType(field.getType());
								} catch (RuntimeException e) {
									throw new RuntimeException(
											"Error while applying the schema personalization file. The '"
													+ field.getType() + "' of the field '" + field.getName()
													+ "': unknown type (not a standard GraphQL type, nor a type defined in the GraphQL schema)",
											e);
								}
								existingField.getFieldTypeAST().setGraphQLTypeSimpleName(field.getType());
							}
							if (field.getId() != null) {
								existingField.setId(field.getId());
							}
							if (field.getMandatory() != null) {
								existingField.getFieldTypeAST().setMandatory(field.getMandatory());
							}
							if (field.getAddAnnotation() != null) {
								existingField.addAnnotation(field.getAddAnnotation());
							}
						} // for personalize existing fields
					}
				}
			}
		}

	}

	private void checkGraphQLName(String name) {
		final Pattern p = Pattern.compile("[A-Za-z][A-Za-z0-9]*");
		Matcher m = p.matcher(name);
		if (!m.matches()) {
			throw new RuntimeException("Error while applying schema personalization, for field '" + name
					+ "': a field name must start by a letter, and may contain only letters and figures");
		}
	}

	/**
	 * Retrieves the schema personalization
	 * 
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	SchemaPersonalization getSchemaPersonalization() {
		if (schemaPersonalization == null) {
			schemaPersonalization = loadGraphQLSchemaPersonalization();
		}
		return schemaPersonalization;
	}

	/**
	 * Let's load the schema personalization from the configuration json file.
	 * 
	 * @return
	 */
	public SchemaPersonalization loadGraphQLSchemaPersonalization() {

		if (((GenerateCodeCommonConfiguration) configuration).getSchemaPersonalizationFile() == null) {
			return null;
		}

		try {

			// Let's check that the JSON is valid
			JsonSchema schema;
			try (InputStream schemaStream = getClass().getResourceAsStream("/" + JSON_SCHEMA_FILENAME)) {
				JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909);
				schema = factory.getSchema(schemaStream);
			}

			File jsonFile = ((GenerateCodeCommonConfiguration) configuration).getSchemaPersonalizationFile();
			logger.info("Loading file " + jsonFile.getAbsolutePath());
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode json = objectMapper.readTree(jsonFile);

			// Validation of the json schema
			Set<ValidationMessage> errors = schema.validate(json);
			if (errors.size() > 0) {
				errors.forEach(e -> logger.error("Erreur while validating the {} json file: {}", jsonFile.getName(),
						e.getMessage()));
				throw new RuntimeException("The json file '" + ((GenerateCodeCommonConfiguration) configuration)
						.getSchemaPersonalizationFile().getAbsolutePath() + "' is invalid. See the logs for details");
			}

			// Let's read the flow definition
			SchemaPersonalization ret;
			try (InputStream isFlowJson = new FileInputStream(
					((GenerateCodeCommonConfiguration) configuration).getSchemaPersonalizationFile())) {
				ret = objectMapper.readValue(isFlowJson, SchemaPersonalization.class);
			}
			return ret;
		} catch (

		Exception e) {
			throw new RuntimeException("Error while reading the schema personalization file ("
					+ ((GenerateCodeCommonConfiguration) configuration).getSchemaPersonalizationFile().getAbsolutePath()
					+ "): " + e.getMessage(), e);
		}
	}// loadFlow

	/**
	 * Find one or more object types from a name, within the objectTypes parsed by DocumentParser. <br/>
	 * If the name is an actual name of a type, union, interface (...) defined in the GraphQL schema, then exactly one
	 * result is expected. If it's not the case, a {@link RuntimeException} is thrown.<br/>
	 * If the name is a category, then a list is expected. If the list is empty, a warning is logged. Vali, like
	 * "[interfaces]", "[unions
	 * 
	 * @param name
	 *            If the name is within square brackets, then a category is expected. Valid categories for this method
	 *            are: "[enums]", "[input types]", "[interfaces]", "[types]" and "[unions]". If the name is not within
	 *            square brackets, then it must match an existing item defined in the GraphQL schema.
	 * @return
	 * @throws NullPointerException
	 *             If the provided name is null
	 * @throws RuntimeException
	 *             When an invalid name is provided, either an invalid category (like "[doesn't exist]") or a name that
	 *             doesn't match an existing item defined in the GraphQL schema.
	 */
	List<AbstractType> findGraphQLTypesFromName(String name) {
		List<AbstractType> ret = new ArrayList<>();

		if (name.startsWith("[") && name.endsWith("]")) {
			switch (name) {
			case "[enums]":
				ret.addAll(documentParser.getEnumTypes());
				return ret;
			case "[input types]":
				documentParser.getObjectTypes().stream().filter(t -> t.isInputType()).forEach(t -> ret.add(t));
				return ret;
			case "[interfaces]":
				ret.addAll(documentParser.getInterfaceTypes());
				return ret;
			case "[types]":
				documentParser.getObjectTypes().stream().filter(t -> !t.isInputType()).forEach(t -> ret.add(t));
				return ret;
			case "[unions]":
				ret.addAll(documentParser.getUnionTypes());
				return ret;
			default:
				throw new RuntimeException("Schema personalization error for name '" + name
						+ "': this type of personalization has not been recognized");
			}
		} else {
			Type type = documentParser.getTypes().get(name);
			if (type == null) {
				throw new RuntimeException(
						"Schema personalization error: no item in the GraphQL schema matched the provided name: "
								+ name);
			} else if (!(type instanceof AbstractType)) {
				// Only custom scalars are not instances of AbstractType
				throw new RuntimeException("Schema personalization error: the type " + name
						+ " is a custom scalar. No schema personalization is available for custom scalars.");
			} else {
				ret.add((AbstractType) type);
				return ret;
			}
		}
	}

	/**
	 * Retrieves a field of the given name from the given objectType
	 * 
	 * @param objectType
	 * @param fieldName
	 * @return
	 */
	Field findFieldFromName(ObjectType objectType, String fieldName) {
		for (Field field : objectType.getFields()) {
			if (field.getName().equals(fieldName)) {
				return field;
			}
		}
		throw new RuntimeException(
				"Field'" + fieldName + "' has not been found in object '" + objectType.getName() + "'");
	}

	/**
	 * Checks whether the given object contains a field with the given name
	 * 
	 * @param objectType
	 * @param fieldName
	 * @return
	 */
	boolean checkIfFieldExists(ObjectType objectType, String fieldName) {
		try {
			findFieldFromName(objectType, fieldName);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}
}
