package com.graphql_java_generator.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.plugin.test.helper.GenerateRelaySchemaConfigurationTestHelper;
import com.graphql_java_generator.plugin.test.helper.MavenTestHelper;

import generate_relay_schema.mavenplugin_notscannedbyspring.AbstractSpringConfiguration;
import generate_relay_schema.mavenplugin_notscannedbyspring.Forum_Client_SpringConfiguration;
import graphql.language.Definition;
import graphql.language.Document;
import graphql.language.FieldDefinition;
import graphql.language.ObjectTypeDefinition;
import graphql.language.OperationTypeDefinition;
import graphql.language.ScalarTypeDefinition;
import graphql.language.SchemaDefinition;
import graphql.parser.Parser;

/**
 * 
 * @author etienne-sf
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { Forum_Client_SpringConfiguration.class })
class GenerateRelaySchema_Forum_Test {

	@Autowired
	DocumentParser documentParser;

	@Autowired
	GenerateRelaySchema generateRelaySchema;

	@Autowired
	GenerateRelaySchemaConfigurationTestHelper configuration;

	@Autowired
	GraphqlUtils graphqlUtils;

	MavenTestHelper mavenTestHelper = new MavenTestHelper();
	Document generatedDocument;

	/**
	 * This is not a unit test. It's more a full integration test, that checks that the GraphQL generated from the forum
	 * schema is complete.<BR/>
	 * As we won't generate the schema for each check, this method is the unique test. And it throws all other checks on
	 * the generated schema.
	 * 
	 * @throws IOException
	 */
	@Test
	@DirtiesContext
	void testGenerateRelaySchema() throws IOException {

		// Go, go, go
		generateRelaySchema.generateRelaySchema();

		// Verification
		File generatedSchemaFile = new File(getTargetFolder(), getSchemaFileName());
		StringWriter writer = new StringWriter();
		InputStream inputStream = new FileInputStream(generatedSchemaFile);
		IOUtils.copy(inputStream, writer, StandardCharsets.UTF_8);
		generatedDocument = new Parser().parseDocument(writer.toString());

		for (Document doc : documentParser.documents) {
			for (Definition<?> node : doc.getDefinitions()) {
				if (node instanceof ObjectTypeDefinition) {
					checkObject((ObjectTypeDefinition) node);
				} else if (node instanceof ScalarTypeDefinition) {
					checkScalarType((ScalarTypeDefinition) node);
				} else if (node instanceof SchemaDefinition) {
					checkSchema((SchemaDefinition) node);
				} else {
					fail("non managed node type: " + node.getClass().getSimpleName());
				}
			}
		}

		fail("Not finished (directive, including directive on fields, types...)");
	}

	private void checkScalarType(ScalarTypeDefinition sourceNode) {
		getNodeFromGeneratedSchema(sourceNode.getName(), ScalarTypeDefinition.class);
		// No other test: a scalar definition in a GraphQL schema contains just the scalar name.
	}

	private void checkSchema(SchemaDefinition sourceNode) {
		int nbOperations = 0;

		for (OperationTypeDefinition opDef : sourceNode.getOperationTypeDefinitions()) {
			nbOperations += 1;
			switch (opDef.getName()) {
			case "query":
				assertEquals(opDef.getTypeName().getName(), documentParser.queryType.getName());
				break;
			case "mutation":
				assertEquals(opDef.getTypeName().getName(), documentParser.mutationType.getName());
				break;
			case "subscription":
				assertEquals(opDef.getTypeName().getName(), documentParser.subscriptionType.getName());
				break;
			default:
				throw new RuntimeException(
						"Unexpected OperationTypeDefinition while reading schema: " + opDef.getName());
			}// switch
		} // for

		// All found operations are of the correct type.
		// Let's check that the total number of operation is correct. If yes, we're done.
		int nbExpectedOperations = (documentParser.queryType == null ? 0 : 1)
				+ (documentParser.mutationType == null ? 0 : 1) + (documentParser.subscriptionType == null ? 0 : 1);
		assertEquals(nbExpectedOperations, nbOperations);
	}

	/**
	 * Checks that the given object exists in the generated schema
	 * 
	 * @param objectDef
	 */
	private void checkObject(ObjectTypeDefinition sourceNode) {
		ObjectTypeDefinition generatedObject = getNodeFromGeneratedSchema(sourceNode.getName(),
				ObjectTypeDefinition.class);

		for (FieldDefinition sourceField : sourceNode.getFieldDefinitions()) {
			for (FieldDefinition generatedField : generatedObject.getFieldDefinitions()) {
				// We found a field of the correct name. Let's check it's metadata
				fail("To be implemented: checkField(sourceField, generatedField)");
				// assertEquals(sourceField.getType().get, );
			}
			fail("In the object '" + sourceNode.getName() + "', the field '" + sourceField.getName()
					+ "' doesn't exist in the generated schema");
		}

		fail("field not tested");
	}

	// private void checkField(FieldDefinition sourceField, FieldDefinition generatedField) {
	// TypeName typeName = null;
	// if (graphqlUtils.invokeMethod("getType", fieldDef) instanceof TypeName) {
	// typeName = (TypeName) graphqlUtils.invokeMethod("getType", fieldDef);
	// } else if (graphqlUtils.invokeMethod("getType", fieldDef) instanceof NonNullType) {
	// field.setMandatory(true);
	// Node<?> node = ((NonNullType) graphqlUtils.invokeMethod("getType", fieldDef)).getType();
	// if (node instanceof TypeName) {
	// typeName = (TypeName) node;
	// } else if (node instanceof ListType) {
	// Node<?> subNode = ((ListType) node).getType();
	// field.setList(true);
	// if (subNode instanceof TypeName) {
	// typeName = (TypeName) subNode;
	// } else if (subNode instanceof NonNullType) {
	// typeName = (TypeName) ((NonNullType) subNode).getType();
	// field.setItemMandatory(true);
	// } else {
	// throw new RuntimeException("Case not found (subnode of a ListType). The node is of type "
	// + subNode.getClass().getName() + " (for field " + field.getName() + ")");
	// }
	// } else {
	// throw new RuntimeException("Case not found (subnode of a NonNullType). The node is of type "
	// + node.getClass().getName() + " (for field " + field.getName() + ")");
	// }
	// } else if (graphqlUtils.invokeMethod("getType", fieldDef) instanceof ListType) {
	// field.setList(true);
	// Node<?> node = ((ListType) graphqlUtils.invokeMethod("getType", fieldDef)).getType();
	// if (node instanceof TypeName) {
	// typeName = (TypeName) node;
	// } else if (node instanceof NonNullType) {
	// typeName = (TypeName) ((NonNullType) node).getType();
	// field.setItemMandatory(true);
	// } else {
	// throw new RuntimeException("Case not found (subnode of a ListType). The node is of type "
	// + node.getClass().getName() + " (for field " + field.getName() + ")");
	// }
	// }
	//
	// }

	/**
	 * This method retrieves the node of the given name and the given class, in the generated schema
	 * 
	 * @param <T>
	 * @param searchedName
	 *            The name of the node to be found
	 * @param clazz
	 *            The node type that is searched
	 * @return The node of type <I>clazz</I> and of name <I>searchedName</I> that has been defined in the generated
	 *         schema.
	 * @throws AssertionFailedError
	 *             If the node has not been found (will mark the JUnit test as KO)
	 */
	private <T extends Definition<?>> T getNodeFromGeneratedSchema(String searchedName, Class<T> clazz) {
		// Each object in the source schema must be present in the generated schema
		for (T node : generatedDocument.getDefinitionsOfType(clazz)) {
			String foundName = (String) graphqlUtils.invokeMethod("getName", node);
			if (searchedName.contentEquals(foundName)) {
				return node;
			}
		}

		fail("The node of type '" + clazz.getSimpleName() + "' and of name '" + searchedName
				+ "' doesn't exist in the generated schema");
		return null;
	}

	/** The folder where the generated GraphQL schema will be written */
	File

			getTargetFolder() {
		return new File(mavenTestHelper.getModulePathFile(), AbstractSpringConfiguration.ROOT_UNIT_TEST_FOLDER
				+ Forum_Client_SpringConfiguration.class.getSimpleName());
	}

	/** The file name for the generated GraphQL schema */
	String getSchemaFileName() {
		return "forum.graphqls";
	}
}
