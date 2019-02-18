/**
 * 
 */
package graphql.mavenplugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.springframework.stereotype.Component;

import graphql.language.AbstractNode;
import graphql.language.Definition;
import graphql.language.Document;
import graphql.language.EnumTypeDefinition;
import graphql.language.FieldDefinition;
import graphql.language.InputValueDefinition;
import graphql.language.InterfaceTypeDefinition;
import graphql.language.ListType;
import graphql.language.Node;
import graphql.language.NonNullType;
import graphql.language.ObjectTypeDefinition;
import graphql.language.OperationTypeDefinition;
import graphql.language.SchemaDefinition;
import graphql.language.StringValue;
import graphql.language.TypeName;
import graphql.mavenplugin.language.Field;
import graphql.mavenplugin.language.FieldType;
import graphql.mavenplugin.language.ObjectType;
import graphql.parser.Parser;
import lombok.Getter;

/**
 * This class generates the Java classes, from the documents. These documents are read from the
 * graphql-spring-boot-starter code, in injected here thanks to spring's magik.<BR/>
 * There is no validity check: we trust the information in the Document, as it is read by the graphql {@link Parser}.
 * <BR/>
 * The graphQL-java library maps both FieldDefinition and InputValueDefinition in very similar structures, which are
 * actually trees. These structures are too hard too read in a Velocity template, and we need to parse down to a
 * properly structures way for that.
 * 
 * @author EtienneSF
 */
@Component
public class DocumentParser {

	final String DEFAULT_QUERY_NAME = "Query";
	final String DEFAULT_MUTATION_NAME = "Mutation";
	final String DEFAULT_SUBSCRIPTION_NAME = "Subscription";

	/////////////////////////////////////////////////////////////////////////////////////////////
	// All the maven parameters are exposed as Spring Beans

	/** @See GraphqlMavenPlugin#basePackage */
	@Resource
	String basePackage;

	/** The maven logging system */
	@Resource
	Log log;

	/////////////////////////////////////////////////////////////////////////////////////////////
	// Internal attributes for this class

	@Resource
	List<Document> documents;

	/**
	 * All the Query Types for this Document. There may be several ones, if more than one graphqls files have been
	 * merged
	 */
	@Getter
	List<ObjectType> queryTypes = new ArrayList<>();
	/**
	 * All the Subscription Types for this Document. There may be several ones, if more than one graphqls files have
	 * been merged
	 */
	@Getter
	List<ObjectType> subscriptionTypes = new ArrayList<>();
	/**
	 * All the Mutation Types for this Document. There may be several ones, if more than one graphqls files have been
	 * merged
	 */
	@Getter
	List<ObjectType> mutationTypes = new ArrayList<>();

	/** All the {@link ObjectType} which have been read during the reading of the documents */
	@Getter
	List<ObjectType> objectTypes = new ArrayList<ObjectType>();

	/**
	 * maps for all scalers, when it is NOT mandatory. The key is the type name. The value is the class to use in the
	 * java code
	 */
	Map<String, String> nonMandatoryScalars = new HashMap<>();

	/**
	 * maps for all scalers, when they are mandatory. The key is the type name. The value is the class to use in the
	 * java code
	 */
	Map<String, String> mandatoryScalars = new HashMap<>();

	public DocumentParser() {
		// Add of all scalars, when non mandatory
		nonMandatoryScalars.put("ID", String.class.getName());
		nonMandatoryScalars.put("String", String.class.getName());
		nonMandatoryScalars.put("boolean", Boolean.class.getName());
		nonMandatoryScalars.put("int", Integer.class.getName());
		nonMandatoryScalars.put("float", Float.class.getName());

		// Add of all scalars, when mandatory
		mandatoryScalars.put("ID", String.class.getName());
		mandatoryScalars.put("String", String.class.getName());
		mandatoryScalars.put("boolean", boolean.class.getName());
		mandatoryScalars.put("int", int.class.getName());
		mandatoryScalars.put("float", float.class.getName());
	}

	/**
	 * The main method of the class: it executes the generation of the given documents
	 * 
	 * @param documents
	 *            The graphql definition schema, from which the code is to be generated
	 * @return
	 */
	public int parseDocuments() {
		return documents.stream().mapToInt(this::parseOneDocument).sum();
	}

	/**
	 * Generates the target classes for the given graphql schema definition
	 * 
	 * @param document
	 */
	int parseOneDocument(Document document) {
		// List of all the names of the query types. There should be only one. But we're ready for more (for instance if
		// several schema files have been merged)
		List<String> queryObjectNames = new ArrayList<>();
		// List of all the names of the mutation types. There should be only one. But we're ready for more (for instance
		// if several schema files have been merged)
		List<String> mutationObjectNames = new ArrayList<>();
		// List of all the names of the subscription types. There should be only one. But we're ready for more (for
		// instance if several schema files have been merged)
		List<String> subscriptionObjectNames = new ArrayList<>();

		// Looks for a schema definitions, to list the defined queries, mutations and subscriptions (should be only one
		// of each), but we're ready for more. (for instance if several schema files have been merged)
		for (Definition<?> node : document.getDefinitions()) {
			if (node instanceof SchemaDefinition) {
				readSchemaDefinition((SchemaDefinition) node, queryObjectNames, mutationObjectNames,
						subscriptionObjectNames);
			} // if
		} // for

		for (Definition<?> node : document.getDefinitions()) {
			if (node instanceof ObjectTypeDefinition) {
				// Let's check what kind of ObjectDefinition we have
				String name = ((ObjectTypeDefinition) node).getName();
				if (queryObjectNames.contains(name) || DEFAULT_QUERY_NAME.equals(name)) {
					queryTypes.add(readObjectType((ObjectTypeDefinition) node));
				} else if (queryObjectNames.contains(name) || DEFAULT_MUTATION_NAME.equals(name)) {
					mutationTypes.add(readObjectType((ObjectTypeDefinition) node));
				} else if (queryObjectNames.contains(name) || DEFAULT_SUBSCRIPTION_NAME.equals(name)) {
					subscriptionTypes.add(readObjectType((ObjectTypeDefinition) node));
				} else {
					objectTypes.add(readObjectType((ObjectTypeDefinition) node));
				}
			} else if (node instanceof EnumTypeDefinition) {
				log.warn("EnumTypeDefinition not managed");
			} else if (node instanceof InterfaceTypeDefinition) {
				log.warn("InterfaceTypeDefinition not managed");
			} else if (node instanceof SchemaDefinition) {
				// No action, we already parsed it
			} else {
				throw new RuntimeException("Unknown node type: " + node.getClass().getName());
			}
		} // for

		return queryTypes.size() + subscriptionTypes.size() + mutationTypes.size() + objectTypes.size();
	}

	/**
	 * @param schemaDef
	 * @param queryObjectNames
	 * @param mutationObjectNames
	 * @param subscriptionObjectNames
	 * 
	 */
	void readSchemaDefinition(SchemaDefinition schemaDef, List<String> queryObjectNames,
			List<String> mutationObjectNames, List<String> subscriptionObjectNames) {
		for (OperationTypeDefinition opDef : schemaDef.getOperationTypeDefinitions()) {
			TypeName type = (TypeName) opDef.getType();
			switch (opDef.getName()) {
			case "query":
				queryObjectNames.add(type.getName());
				break;
			case "mutation":
				mutationObjectNames.add(type.getName());
				break;
			case "subscription":
				subscriptionObjectNames.add(type.getName());
				break;
			default:
				throw new RuntimeException(
						"Unexpected OperationTypeDefinition while reading schema: " + opDef.getName());
			}// switch
		} // for
	}

	/**
	 * Add an object type to the object type list
	 * 
	 * @param node
	 * @return
	 */
	ObjectType readObjectType(ObjectTypeDefinition node) {
		// Let's check if it's a real object, or part of a schema (query, subscription, mutation) definition

		ObjectType objectType = new ObjectType();

		objectType.setName(node.getName());

		// Let's read all its fields
		objectType.setFields(node.getFieldDefinitions().stream().map(this::getField).collect(Collectors.toList()));

		return objectType;
	}

	/**
	 * Reads one graphql {@link FieldDefinition}, and maps it into a {@link Field}.
	 * 
	 * @param fieldDef
	 * @return
	 * @throws MojoExecutionException
	 */
	Field getField(FieldDefinition fieldDef) {

		Field field = readFieldTypeDefinition(fieldDef);

		// Let's read all its input parameters
		field.setInputParameters(fieldDef.getInputValueDefinitions().stream().map(this::readFieldTypeDefinition)
				.collect(Collectors.toList()));

		return field;
	}

	/**
	 * Reads an {@link InputValueDefinition}, and returns the {@link Field} field created from this definition
	 * 
	 * @param inputValueDef
	 * @return
	 */
	Field readInputValueDefinition(InputValueDefinition inputValueDef) {
		throw new RuntimeException("not yet implemented");
	}

	/**
	 * Reads a field, which can be either agraphql {@link FieldDefinition} or an {@link InputValueDefinition}, and maps
	 * it into a {@link Field}. The graphQL-java library maps both FieldDefinition and InputValueDefinition in very
	 * similar structures, which are actually trees. These structures are too hard too read in a Velocity template, and
	 * we need to parse down to a properly structures way for that.
	 * 
	 * @param fieldDef
	 * @param field
	 * @return
	 */
	Field readFieldTypeDefinition(AbstractNode<?> fieldDef) {
		Field field = new Field();
		FieldType type = new FieldType();
		field.setType(type);

		field.setName((String) exec("getName", fieldDef));

		// Let's default value to false
		field.setMandatory(false);
		field.setList(false);
		field.setItemMandatory(false);

		String nameOfTheType = null;
		TypeName typeName = null;
		if (exec("getType", fieldDef) instanceof TypeName) {
			typeName = (TypeName) exec("getType", fieldDef);
		} else if (exec("getType", fieldDef) instanceof NonNullType) {
			field.setMandatory(true);
			Node<?> node = ((NonNullType) exec("getType", fieldDef)).getType();
			if (node instanceof TypeName) {
				typeName = (TypeName) node;
			} else if (node instanceof ListType) {
				Node<?> subNode = ((ListType) node).getType();
				field.setList(true);
				if (subNode instanceof TypeName) {
					typeName = (TypeName) subNode;
				} else if (subNode instanceof NonNullType) {
					typeName = (TypeName) ((NonNullType) subNode).getType();
					field.setItemMandatory(true);
				} else {
					throw new RuntimeException("Case not found (subnode of a ListType). The node is of type "
							+ subNode.getClass().getName() + " (for field " + field.getName() + ")");
				}
			} else {
				throw new RuntimeException("Case not found (subnode of a NonNullType). The node is of type "
						+ node.getClass().getName() + " (for field " + field.getName() + ")");
			}
		} else if (exec("getType", fieldDef) instanceof ListType) {
			field.setList(true);
			Node<?> node = ((ListType) exec("getType", fieldDef)).getType();
			if (node instanceof TypeName) {
				typeName = (TypeName) node;
			} else if (node instanceof NonNullType) {
				typeName = (TypeName) ((NonNullType) node).getType();
				field.setItemMandatory(true);
			} else {
				throw new RuntimeException("Case not found (subnode of a ListType). The node is of type "
						+ node.getClass().getName() + " (for field " + field.getName() + ")");
			}
		}
		nameOfTheType = typeName.getName();

		type.setName(nameOfTheType);

		// For Scalar types, the actual Java type depends on whether the item is mandatory or not (int is mandatory
		// whereas Integer can be null)
		if (field.isList())
			type.setJavaClassFullName(getFieldTypeClassFrom(nameOfTheType, field.isItemMandatory()));
		else
			type.setJavaClassFullName(getFieldTypeClassFrom(nameOfTheType, field.isMandatory()));

		// For InputValueDefinition, we may have a defaut value
		if (fieldDef instanceof InputValueDefinition) {
			Object defaultValue = ((InputValueDefinition) fieldDef).getDefaultValue();
			if (defaultValue != null) {
				if (defaultValue instanceof StringValue) {
					field.setDefaultValue(((StringValue) defaultValue).getValue());
				} else {
					throw new RuntimeException("DefaultValue of type " + defaultValue.getClass().getName()
							+ " is not managed (for field " + field.getName() + ")");
				}
			}
		}

		return field;
	}

	/**
	 * Calls the 'methodName' method on the given object
	 * 
	 * @param methodName
	 *            The name of the method name
	 * @param node
	 *            The given node, on which the 'methodName' method is to be called
	 * @return
	 */
	Object exec(String methodName, AbstractNode<?> node) {
		try {
			Method getType = node.getClass().getDeclaredMethod(methodName);
			return getType.invoke(node);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException("Error when trying to execute '" + methodName + "' on '"
					+ node.getClass().getName() + "': " + e.getMessage(), e);
		}
	}

	/**
	 * Returns the Java class name from a given type name
	 * 
	 * @param type
	 * @param mandatory
	 * @return
	 */
	String getFieldTypeClassFrom(String type, boolean mandatory) {
		String classname = null;
		if (mandatory) {
			classname = mandatoryScalars.get(type);
		} else {
			classname = nonMandatoryScalars.get(type);
		}

		if (classname == null) {
			// It's not a scaler. So either the schema is invalid (but it has been correctly parsed by graphql) or it is
			// an Object Type defined in the schema.
			// So, we're in the second case, and this will be confirmed during the projet compilation.
			classname = getGeneratedFieldFullClassName(type);
		}

		return classname;
	}

	/**
	 * Reads one graphql {@link FieldDefinition}, and maps it into a {@link FieldType}
	 * 
	 * @param fieldDef
	 * @return
	 */
	FieldType getType(FieldDefinition fieldDef) {
		return null;
	}

	/**
	 * A utility method, which maps an object type to the class full name of the Java class which will be generated for
	 * this object type. This utility method is based on the {@link #basePackage} maven attribute, available in this
	 * class
	 * 
	 * @param name
	 */
	String getGeneratedFieldFullClassName(String name) {
		return basePackage + "." + name;
	}

}
