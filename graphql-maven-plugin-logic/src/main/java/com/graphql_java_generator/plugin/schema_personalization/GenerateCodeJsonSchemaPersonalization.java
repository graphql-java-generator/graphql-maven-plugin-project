/**
 * 
 */
package com.graphql_java_generator.plugin.schema_personalization;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import javax.json.JsonReader;
import javax.ws.rs.ProcessingException;

import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonValidationService;
import org.leadpony.justify.api.ProblemHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql_java_generator.plugin.GenerateCodeDocumentParser;
import com.graphql_java_generator.plugin.conf.CommonConfiguration;
import com.graphql_java_generator.plugin.conf.GenerateCodeCommonConfiguration;
import com.graphql_java_generator.plugin.conf.GenerateServerCodeConfiguration;
import com.graphql_java_generator.plugin.conf.PluginMode;
import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.impl.FieldImpl;
import com.graphql_java_generator.plugin.language.impl.ObjectType;

/**
 * This tool contains the logic which allows the plugin user to personnalize the code generation. It allows:
 * <UL>
 * <LI>Add specific fields. For instance field used for foreign keys</LI>
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

	static final String JSON_SCHEMA_FILENAME = "schema_personalization.schema.json";

	@Autowired
	GenerateCodeDocumentParser documentParser;

	@Autowired
	CommonConfiguration configuration;

	/** The class where the content of the configuration file will be loaded */
	SchemaPersonalization schemaPersonalization = null;

	/** The number of parsing errors, in the json user file */
	int nbErrors = 0;

	/**
	 * This is the 'main' method for this class: it loads the schema personalization from the json user file, and update
	 * what the {@link GenerateCodeDocumentParser} has already loaded according to the user's needs.
	 */
	public void applySchemaPersonalization() {
		try {
			if (!(configuration instanceof GenerateCodeCommonConfiguration)
					|| !((GenerateCodeCommonConfiguration) configuration).getMode().equals(PluginMode.server)) {
				configuration.getLog().debug(
						"The plugin configuration is not in server mode: no schema personalization is to be applied");
			} else {
				// First step: we load the schema personalization
				if (getSchemaPersonalization() != null) {

					// Then, we apply what has been loaded from the json file
					for (EntityPersonalization objectPers : schemaPersonalization.getEntityPersonalizations()) {
						ObjectType objectType = findObjectTypeFromName(objectPers.getName());

						// Should we add an annotation ?
						if (objectPers.getAddAnnotation() != null) {
							objectType.addAnnotation(objectPers.getAddAnnotation());
						}

						// Should we replace the annotation ?
						if (objectPers.getReplaceAnnotation() != null) {
							objectType.addAnnotation(objectPers.getReplaceAnnotation(), true);
						}

						// Let's add all new fields
						for (com.graphql_java_generator.plugin.schema_personalization.Field field : objectPers
								.getNewFields()) {
							// There must not be any field of that name in that object
							if (checkIfFieldExists(objectType, field.getName())) {
								throw new RuntimeException("The object " + objectType.getName()
										+ " already has a field of name " + field.getName());
							}
							// Ok, we can add this new field
							FieldImpl newField = FieldImpl.builder().documentParser(documentParser).build();
							newField.setName(field.getName());
							newField.setOwningType(objectType);
							newField.setGraphQLTypeName(field.getType());
							if (field.getId() != null) {
								newField.setId(field.getId());
							}
							if (field.getList() != null) {
								newField.setList(field.getList());
							}
							if (field.getMandatory() != null) {
								newField.setMandatory(field.getMandatory());
							}
							if (field.getAddAnnotation() != null) {
								newField.addAnnotation(field.getAddAnnotation());
							}
							if (field.getReplaceAnnotation() != null) {
								// We replace the annotation, even if there was an addAnnotation in the json file
								newField.addAnnotation(field.getReplaceAnnotation(), true);
							}
							objectType.getFields().add(newField);
						} // for newFields

						// Let's add personalize existing fields
						for (com.graphql_java_generator.plugin.schema_personalization.Field field : objectPers
								.getFieldPersonalizations()) {
							// Ok, we can add the field to personalize. This will throw an exception if not found
							FieldImpl existingField = (FieldImpl) findFieldFromName(objectType, field.getName());

							existingField.setName(field.getName());
							if (field.getType() != null) {
								existingField.setGraphQLTypeName(field.getType());
							}
							if (field.getId() != null) {
								existingField.setId(field.getId());
							}
							if (field.getList() != null) {
								existingField.setList(field.getList());
							}
							if (field.getMandatory() != null) {
								existingField.setMandatory(field.getMandatory());
							}
							if (field.getAddAnnotation() != null) {
								existingField.addAnnotation(field.getAddAnnotation());
							}
							if (field.getReplaceAnnotation() != null) {
								// We replace the annotation, even if there was an addAnnotation in the json file
								existingField.addAnnotation(field.getReplaceAnnotation(), true);
							}
						} // for personalize existing fields
					}
				}
			}
		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException("Can't apply schema personalization, due to: " + e.getMessage(), e);
		}
	}

	/**
	 * Retrieves the schema personalization
	 * 
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	SchemaPersonalization getSchemaPersonalization() throws IOException, URISyntaxException {
		if (schemaPersonalization == null) {
			schemaPersonalization = loadGraphQLSchemaPersonalization();
		}
		return schemaPersonalization;
	}

	/**
	 * Let's load the schema personalization from the configuration json file.
	 * 
	 * @return
	 * 
	 * @throws IOException
	 * @throws ProcessingException
	 *             When the JSON file is not valid
	 * @throws URISyntaxException
	 *             If we can't process the JSON URIs. It would be an internal error.
	 */
	public SchemaPersonalization loadGraphQLSchemaPersonalization() throws IOException, URISyntaxException {

		if (((GenerateServerCodeConfiguration) configuration).getSchemaPersonalizationFile() == null) {
			return null;
		} else {

			// Let's check that the JSON is valid

			JsonValidationService service = JsonValidationService.newInstance();
			// Reads the JSON schema
			JsonSchema schema = service.readSchema(getClass().getResourceAsStream("/" + JSON_SCHEMA_FILENAME));
			// Problem handler which will print problems found
			ProblemHandler handler = service.createProblemPrinter(this::logParsingError);
			// Reads the JSON instance by javax.json.JsonReader
			nbErrors = 0;
			try (JsonReader reader = service.createReader(
					new FileInputStream(
							((GenerateServerCodeConfiguration) configuration).getSchemaPersonalizationFile()),
					schema, handler)) {
				// JsonValue value =
				reader.readValue();
				// Do something useful here
			}
			if (nbErrors > 0) {
				throw new RuntimeException("The json file '" + ((GenerateServerCodeConfiguration) configuration)
						.getSchemaPersonalizationFile().getAbsolutePath() + "' is invalid. See the logs for details");
			}

			// Let's read the flow definition
			configuration.getLog().info("Loading file " + ((GenerateServerCodeConfiguration) configuration)
					.getSchemaPersonalizationFile().getAbsolutePath());
			ObjectMapper objectMapper = new ObjectMapper();
			SchemaPersonalization ret;
			try (InputStream isFlowJson = new FileInputStream(
					((GenerateServerCodeConfiguration) configuration).getSchemaPersonalizationFile())) {
				ret = objectMapper.readValue(isFlowJson, SchemaPersonalization.class);
			}
			return ret;
		}
	}// loadFlow

	public void logParsingError(String error) {
		configuration.getLog().error(error);
		nbErrors += 1;
	}

	/**
	 * Find an object type from its name, within the objectTypes parsed by DocumentParser
	 * 
	 * @param name
	 * @return
	 */
	ObjectType findObjectTypeFromName(String name) {
		for (ObjectType objectType : documentParser.getObjectTypes()) {
			if (objectType.getName().equals(name)) {
				// We're done
				return objectType;
			}
		}
		throw new RuntimeException("ObjectType named '" + name + "' not found");
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
