/**
 * 
 */
package graphql.mavenplugin.generation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.maven.plugin.MojoExecutionException;
import org.springframework.stereotype.Component;

import graphql.language.Definition;
import graphql.language.Document;
import graphql.language.FieldDefinition;
import graphql.language.ListType;
import graphql.language.Node;
import graphql.language.NonNullType;
import graphql.language.ObjectTypeDefinition;
import graphql.language.TypeName;
import graphql.parser.Parser;

/**
 * This class generates the Java classes, from the documents. These documents are read from the
 * graphql-spring-boot-starter code, in injected here thanks to spring's magik.<BR/>
 * There is no validity check: we trust the information in the Document, as it is read by the graphql {@link Parser}.
 * 
 * @author EtienneSF
 */
@Component
public class Generator {

	@Resource
	List<Document> documents;

	/** All the {@link ObjectType} which have been read during the reading of the documents */
	List<ObjectType> objectTypes = new ArrayList<ObjectType>();

	/**
	 * maps for all scalers, when it is NOT mandatory. The key is the type name. The value is the class to use in the
	 * java code
	 */
	Map<String, Class<?>> nonMandatoryScalars = new HashMap<>();

	/**
	 * maps for all scalers, when they are mandatory. The key is the type name. The value is the class to use in the
	 * java code
	 */
	Map<String, Class<?>> mandatoryScalars = new HashMap<>();

	/**
	 * The main method of the class: it executes the generation of the given documents
	 * 
	 * @param documents
	 *            The graphql definition schema, from which the code is to be generated
	 * @return
	 */
	public int generateTargetFiles() {
		return documents.stream().mapToInt(this::generateForOneDocument).sum();
	}

	public Generator() {
		// Add of all scalars, when non mandatory
		nonMandatoryScalars.put("ID", String.class);
		nonMandatoryScalars.put("String", String.class);
		nonMandatoryScalars.put("boolean", Boolean.class);
		nonMandatoryScalars.put("int", Integer.class);
		nonMandatoryScalars.put("float", Float.class);

		// Add of all scalars, when mandatory
		mandatoryScalars.put("ID", String.class);
		mandatoryScalars.put("String", String.class);
		mandatoryScalars.put("boolean", boolean.class);
		mandatoryScalars.put("int", int.class);
		mandatoryScalars.put("float", float.class);
	}

	/**
	 * Generates the target classes for the given graphql schema definition
	 * 
	 * @param document
	 */
	int generateForOneDocument(Document document) {
		int i = 0;
		for (Definition<?> node : document.getDefinitions()) {
			if (node instanceof ObjectTypeDefinition) {
				i += addObjectType((ObjectTypeDefinition) node);
			}
		} // for
		return i;
	}

	/**
	 * Add an object type to the object type list
	 * 
	 * @param node
	 * @return
	 */
	int addObjectType(ObjectTypeDefinition node) {
		ObjectType objectType = new ObjectType();

		objectType.setName(node.getName());

		// Let's read all its fields
		objectType.setFields(node.getFieldDefinitions().stream().map(this::getField).collect(Collectors.toList()));

		objectTypes.add(objectType);
		return 1;
	}

	/**
	 * Reads one graphql {@link FieldDefinition}, and maps it into a {@link Field}
	 * 
	 * @param fieldDef
	 * @return
	 * @throws MojoExecutionException
	 */
	Field getField(FieldDefinition fieldDef) {
		Field field = new Field();
		FieldType type = new FieldType();
		field.setType(type);

		field.setName(fieldDef.getName());

		// Let's default value to false
		field.setMandatory(false);
		field.setList(false);
		field.setItemMandatory(false);

		// Let's get the relevant TypeName which may several step down in the hierarchy
		TypeName typeName = null;
		if (fieldDef.getType() instanceof TypeName) {
			typeName = (TypeName) fieldDef.getType();
		} else if (fieldDef.getType() instanceof NonNullType) {
			field.setMandatory(true);
			Node<?> node = ((NonNullType) fieldDef.getType()).getType();
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
							+ subNode.getClass().getName());
				}
			} else {
				throw new RuntimeException(
						"Case not found (subnode of a NonNullType). The node is of type " + node.getClass().getName());
			}
		} else if (fieldDef.getType() instanceof ListType) {
			field.setList(true);
			Node<?> node = ((ListType) fieldDef.getType()).getType();
			if (node instanceof TypeName) {
				typeName = (TypeName) node;
			} else if (node instanceof NonNullType) {
				typeName = (TypeName) ((NonNullType) node).getType();
				field.setItemMandatory(true);
			} else {
				throw new RuntimeException(
						"Case not found (subnode of a ListType). The node is of type " + node.getClass().getName());
			}
		}

		type.setName(typeName.getName());
		if (field.isList())
			type.setJavaClass(getFieldTypeClassFrom(typeName, field.isItemMandatory()));
		else
			type.setJavaClass(getFieldTypeClassFrom(typeName, field.isMandatory()));

		return field;
	}

	/**
	 * Returns the Java class from a given type name
	 * 
	 * @param type
	 * @param mandatory
	 * @return
	 */
	Class<?> getFieldTypeClassFrom(TypeName type, boolean mandatory) {
		Class<?> clazz = null;
		if (mandatory) {
			clazz = mandatoryScalars.get(type.getName());
		} else {
			clazz = nonMandatoryScalars.get(type.getName());
		}

		if (clazz == null) {
			throw new RuntimeException("Unexpected graphql type: " + type.getName());
		}

		return clazz;
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

}
