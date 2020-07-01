package com.graphql_java_generator.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cedarsoftware.util.DeepEquals;
import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.plugin.test.helper.GenerateRelaySchemaConfigurationTestHelper;
import com.graphql_java_generator.plugin.test.helper.MavenTestHelper;

import generate_relay_schema.mavenplugin_notscannedbyspring.AbstractSpringConfiguration;
import generate_relay_schema.mavenplugin_notscannedbyspring.Forum_Client_SpringConfiguration;
import generate_relay_schema.mavenplugin_notscannedbyspring.GeneratedForum_Client_SpringConfiguration;
import graphql.language.Definition;
import graphql.language.Document;
import graphql.language.FieldDefinition;
import graphql.language.ListType;
import graphql.language.Node;
import graphql.language.NonNullType;
import graphql.language.ObjectTypeDefinition;
import graphql.language.OperationTypeDefinition;
import graphql.language.ScalarTypeDefinition;
import graphql.language.SchemaDefinition;
import graphql.language.TypeName;
import graphql.parser.Parser;
import lombok.EqualsAndHashCode;

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

	@EqualsAndHashCode // This will generate the equals method, used later in the unit tests. Note: this won't deep
						// compare the collections.
	@Deprecated
	class FieldProperties {
		String name;
		boolean mandatory = false;
		boolean list = false;
		boolean itemMandatory = false;
		List<FieldProperties> props = new ArrayList<>();
	}

	/**
	 * This is not a unit test. It's more a full integration test, that checks that the GraphQL generated from the forum
	 * schema is complete.<BR/>
	 * It's too complex to compare the GraphQL AST, especially to manage things like the <I>extends</I> keyword. So the
	 * principle is:
	 * <UL>
	 * <LI>Load the source GraphQL schemas in a source {@link DocumentParser}</LI>
	 * <LI>Generate the schema</LI>
	 * <LI>Load the generated schema in a target {@link DocumentParser}</LI>
	 * <LI>Deep compare the source and the target {@link DocumentParser} with a generic tool</LI>
	 * </UL>
	 * Doing this insure that the code is stable even if the {@link DocumentParser} implementation changes. And as this
	 * implementation is heavily tested, we an consider that it is complete, out of some known and documented
	 * limitations
	 * 
	 * @throws IOException
	 */
	@Test
	@DirtiesContext
	void testGenerateRelaySchema() throws IOException {

		// Go, go, go
		generateRelaySchema.generateRelaySchema();

		// Let's load the content of the generated schema in a new DocumentParser
		AbstractApplicationContext ctx = new AnnotationConfigApplicationContext(
				GeneratedForum_Client_SpringConfiguration.class);
		// Let's log the current configuration (this will do something only when in debug mode)
		ctx.getBean(GenerateRelaySchemaConfiguration.class).logConfiguration();
		//
		GenerateRelaySchemaDocumentParser generatedDocumentParser = ctx
				.getBean(GenerateRelaySchemaDocumentParser.class);
		generatedDocumentParser.parseDocuments();
		//
		ctx.close();

		// Let's check the two DocumentParser instances, to check they are the same
		assertTrue(DeepEquals.deepEquals(documentParser, generatedDocumentParser));
		
		Un ajout vers la javadoc serait bien
		
		fail("missing the source and target DocumentParser comparison");
	}

	/**
	 * This method is kept for a while.
	 * 
	 * @throws IOException
	 */
	@Deprecated
	private void compareGraphQLDefinitions() throws IOException {

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

	@Deprecated
	private void checkScalarType(ScalarTypeDefinition sourceNode) {
		getNodeFromGeneratedSchema(sourceNode.getName(), ScalarTypeDefinition.class);
		// No other test: a scalar definition in a GraphQL schema contains just the scalar name.
	}

	@Deprecated
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

	@Deprecated
	private void checkObject(ObjectTypeDefinition sourceNode) {
		ObjectTypeDefinition generatedObject = getNodeFromGeneratedSchema(sourceNode.getName(),
				ObjectTypeDefinition.class);

		for (FieldDefinition sourceField : sourceNode.getFieldDefinitions()) {
			for (FieldDefinition generatedField : generatedObject.getFieldDefinitions()) {
				// We found a field of the correct name. Let's check it's metadata
				checkField(sourceField, generatedField);
			}
			fail("In the object '" + sourceNode.getName() + "', the field '" + sourceField.getName()
					+ "' doesn't exist in the generated schema");
		}

		fail("field not tested");
	}

	@Deprecated
	private void checkField(FieldDefinition sourceField, FieldDefinition generatedField) {
		FieldProperties sourceFieldProperties = getFieldProperties(sourceField);
		FieldProperties generatedFieldProperties = getFieldProperties(generatedField);
		assertEquals(sourceFieldProperties, generatedFieldProperties);
	}

	/**
	 * Reads all the properties from the given GraphQL definition into the proper structure, for easier comparison
	 * afterward
	 */

	@Deprecated
	private FieldProperties getFieldProperties(FieldDefinition fieldDef) {
		FieldProperties ret = new FieldProperties();
		TypeName typeName = null;

		if (graphqlUtils.invokeMethod("getType", fieldDef) instanceof TypeName) {
			typeName = (TypeName) graphqlUtils.invokeMethod("getType", fieldDef);
		} else if (graphqlUtils.invokeMethod("getType", fieldDef) instanceof NonNullType) {
			ret.mandatory = true;
			Node<?> node = ((NonNullType) graphqlUtils.invokeMethod("getType", fieldDef)).getType();
			if (node instanceof TypeName) {
				typeName = (TypeName) node;
			} else if (node instanceof ListType) {
				Node<?> subNode = ((ListType) node).getType();
				ret.list = true;
				if (subNode instanceof TypeName) {
					typeName = (TypeName) subNode;
				} else if (subNode instanceof NonNullType) {
					typeName = (TypeName) ((NonNullType) subNode).getType();
					ret.itemMandatory = true;
				} else {
					throw new RuntimeException("Case not found (subnode of a ListType). The node is of type "
							+ subNode.getClass().getName() + " (for field " + fieldDef.getName() + ")");
				}
			} else {
				throw new RuntimeException("Case not found (subnode of a NonNullType). The node is of type "
						+ node.getClass().getName() + " (for field " + fieldDef.getName() + ")");
			}
		} else if (graphqlUtils.invokeMethod("getType", fieldDef) instanceof ListType) {
			ret.list = true;
			Node<?> node = ((ListType) graphqlUtils.invokeMethod("getType", fieldDef)).getType();
			if (node instanceof TypeName) {
				typeName = (TypeName) node;
			} else if (node instanceof NonNullType) {
				typeName = (TypeName) ((NonNullType) node).getType();
				ret.itemMandatory = true;
			} else {
				throw new RuntimeException("Case not found (subnode of a ListType). The node is of type "
						+ node.getClass().getName() + " (for field " + fieldDef.getName() + ")");
			}
		}

		ret.name = typeName.getName();
		assertNotNull(ret.name);

		return ret;
	}

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

	@Deprecated
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
	@Deprecated
	File getTargetFolder() {
		return new File(mavenTestHelper.getModulePathFile(), AbstractSpringConfiguration.ROOT_UNIT_TEST_FOLDER
				+ Forum_Client_SpringConfiguration.class.getSimpleName());
	}

	/** The file name for the generated GraphQL schema */
	@Deprecated
	String getSchemaFileName() {
		return "forum.graphqls";
	}
}
