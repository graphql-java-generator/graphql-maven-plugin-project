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
import graphql.language.EnumValue;
import graphql.language.EnumValueDefinition;
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
import graphql.language.Type;
import graphql.language.TypeName;
import graphql.mavenplugin.language.EnumType;
import graphql.mavenplugin.language.Field;
import graphql.mavenplugin.language.InterfaceType;
import graphql.mavenplugin.language.ObjectType;
import graphql.mavenplugin.language.ScalarType;
import graphql.parser.Parser;
import kotlin.reflect.jvm.internal.impl.protobuf.WireFormat.FieldType;
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
	List<ObjectType> objectTypes = new ArrayList<>();

	/** All the {@link InterfaceTypeDefinition} which have been read during the reading of the documents */
	@Getter
	List<InterfaceType> interfaceTypes = new ArrayList<>();

	/** All the {@link ObjectType} which have been read during the reading of the documents */
	@Getter
	List<EnumType> enumTypes = new ArrayList<>();

	/** All the {@link Type}s that have been parsed, added by the default scalars */
	Map<String, graphql.mavenplugin.language.Type> types = new HashMap<>();

	/**
	 * maps for all scalers, when they are mandatory. The key is the type name. The value is the class to use in the
	 * java code
	 */
	List<ScalarType> scalars = new ArrayList<>();

	public DocumentParser() {
		// Add of all predefined scalars
		scalars.add(new ScalarType("ID", "java.lang", "String"));
		scalars.add(new ScalarType("String", "java.lang", "String"));
		scalars.add(new ScalarType("boolean", "java.lang", "Boolean"));
		scalars.add(new ScalarType("int", "java.lang", "Integer"));
		scalars.add(new ScalarType("float", "java.lang", "Float"));
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
				} else if (mutationObjectNames.contains(name) || DEFAULT_MUTATION_NAME.equals(name)) {
					mutationTypes.add(readObjectType((ObjectTypeDefinition) node));
				} else if (subscriptionObjectNames.contains(name) || DEFAULT_SUBSCRIPTION_NAME.equals(name)) {
					subscriptionTypes.add(readObjectType((ObjectTypeDefinition) node));
				} else {
					objectTypes.add(readObjectType((ObjectTypeDefinition) node));
				}
			} else if (node instanceof EnumTypeDefinition) {
				enumTypes.add(readEnumType((EnumTypeDefinition) node));
			} else if (node instanceof InterfaceTypeDefinition) {
				interfaceTypes.add(readInterfaceType((InterfaceTypeDefinition) node));
			} else if (node instanceof SchemaDefinition) {
				// No action, we already parsed it
			} else {
				throw new RuntimeException("Unknown node type: " + node.getClass().getName());
			}
		} // for

		defineDefaultInterfaceImplementationClassName();

		fillTypesMap();

		return queryTypes.size() + subscriptionTypes.size() + mutationTypes.size() + objectTypes.size()
				+ enumTypes.size() + interfaceTypes.size();
	}

	/**
	 * Fill the {@link #types} map, from all the types (object, interface, enum, scalars) that are valid for this
	 * schema. This allow to get the properties from their type, as only their type's name is known when parsing the
	 * schema.
	 */
	void fillTypesMap() {
		scalars.stream().forEach(s -> types.put(s.getName(), s));
		objectTypes.stream().forEach(o -> types.put(o.getName(), o));
		interfaceTypes.stream().forEach(i -> types.put(i.getName(), i));
		enumTypes.stream().forEach(e -> types.put(e.getName(), e));
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
			TypeName type = opDef.getTypeName();
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
	 * Read an object type from it graphql definition
	 * 
	 * @param node
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	ObjectType readObjectType(ObjectTypeDefinition node) {
		// Let's check if it's a real object, or part of a schema (query, subscription, mutation) definition

		ObjectType objectType = new ObjectType(basePackage);

		objectType.setName(node.getName());

		// Let's read all its fields
		objectType.setFields(node.getFieldDefinitions().stream().map(this::getField).collect(Collectors.toList()));

		// Let's read all the other object types that this one implements
		for (Type type : node.getImplements()) {
			if (type instanceof TypeName) {
				objectType.getImplementz().add(((TypeName) type).getName());
			} else if (type instanceof EnumValue) {
				objectType.getImplementz().add(((EnumValue) type).getName());
			} else {
				throw new RuntimeException("Non managed object type '" + type.getClass().getName()
						+ "' when listing implementations for the object '" + node.getName() + "'");
			}
		} // for

		return objectType;
	}

	/**
	 * Read an object type from it graphql definition
	 * 
	 * @param node
	 * @return
	 */
	InterfaceType readInterfaceType(InterfaceTypeDefinition node) {
		// Let's check if it's a real object, or part of a schema (query, subscription, mutation) definition

		InterfaceType interfaceType = new InterfaceType(basePackage);

		interfaceType.setName(node.getName());

		// Let's read all its fields
		interfaceType.setFields(node.getFieldDefinitions().stream().map(this::getField).collect(Collectors.toList()));

		return interfaceType;
	}

	/**
	 * Reads an enum definition, and create the relevant {@link EnumType}
	 * 
	 * @param node
	 * @return
	 */
	EnumType readEnumType(EnumTypeDefinition node) {
		EnumType enumType = new EnumType(basePackage);
		enumType.setName(node.getName());
		for (EnumValueDefinition enumValDef : node.getEnumValueDefinitions()) {
			enumType.getValues().add(enumValDef.getName());
		} // for
		return enumType;
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
		Field field = new Field(this);

		field.setName((String) exec("getName", fieldDef));

		// Let's default value to false
		field.setMandatory(false);
		field.setList(false);
		field.setItemMandatory(false);

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

		// We have the type. But we may not have parsed it yet. So we just write its name. And will get the
		// graphql.mavenplugin.language.Type when generating the code.
		field.setTypeName(typeName.getName());

		// For InputValueDefinition, we may have a defaut value
		if (fieldDef instanceof InputValueDefinition) {
			Object defaultValue = ((InputValueDefinition) fieldDef).getDefaultValue();
			if (defaultValue != null) {
				if (defaultValue instanceof StringValue) {
					field.setDefaultValue(((StringValue) defaultValue).getValue());
				} else if (defaultValue instanceof EnumValue) {
					field.setDefaultValue(((EnumValue) defaultValue).getName());
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

	/**
	 * This method add an {@link ObjectType} for each GraphQL interface, to the list of objects to create. The name of
	 * the objet is typically the name of the interface, suffixed by "Impl". A test is done to insure that there is no
	 * "name collision", that is: that InterfaceNameImpl doesn't exist. If there is a collision, the method attempts to
	 * suffix Impl1, then Impl2... until there is no collision.<BR/>
	 * Note: this is useful only for the client code generation (not for the server one)
	 */
	void defineDefaultInterfaceImplementationClassName() {
		String objectName = "interface name to define";
		for (InterfaceType i : interfaceTypes) {
			String defaultName = i.getName() + "Impl";
			int count = 0;
			boolean nameFound = true;

			while (nameFound) {
				objectName = defaultName + (count == 0 ? "" : count);
				count += 1;
				nameFound = false;
				for (ObjectType o : objectTypes) {
					if (o.getName().equals(objectName)) {
						nameFound = true;
					}
				} // for (ObjectType)
			} // while

			// We've found a non used name for the interface implementation.
			ObjectType o = new ObjectType(basePackage);
			o.setName(objectName);
			List<String> interfaces = new ArrayList<>();
			interfaces.add(i.getName());
			o.setImplementz(interfaces);
			o.setFields(i.getFields());
			objectTypes.add(o);

			i.setDefaultImplementation(o);

		} // for
	}

	/**
	 * Returns the type for the given name
	 * 
	 * @param typeName
	 * @return
	 */
	public graphql.mavenplugin.language.Type getType(String typeName) {
		return types.get(typeName);
	}
}
