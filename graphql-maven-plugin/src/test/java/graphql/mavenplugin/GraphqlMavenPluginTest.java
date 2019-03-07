package graphql.mavenplugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import graphql.language.Document;
import graphql.language.FieldDefinition;
import graphql.language.InputValueDefinition;
import graphql.language.Node;
import graphql.language.NonNullType;
import graphql.language.ObjectTypeDefinition;
import graphql.language.TypeName;
import graphql.mavenplugin.test.helper.GraphqlTestHelper;
import graphql.mavenplugin_notscannedbyspring.HelloWorldSpringConfiguration;

/**
 * @author EtienneSF
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { HelloWorldSpringConfiguration.class })
class GraphqlMavenPluginTest {

	@Autowired
	GraphqlMavenPlugin graphqlMavenPlugin;

	@Autowired
	List<Document> documents;

	@Autowired
	GraphqlTestHelper graphqlTestHelper;

	@BeforeEach
	public void beforeAll() {
		graphqlTestHelper.checkSchemaStringProvider("helloworld.graphqls");
	}

	@Test
	void testDocuments_helloworld() throws MojoExecutionException {
		// Preparation

		// Go, go, go

		// Verification
		assertNotNull(documents, "documents should be returned");
		assertEquals(1, documents.size(), "documents should contain one doc");

		Document doc = documents.get(0);
		assertEquals(1, doc.getDefinitions().size(), "One definition");

		Node<?> node = doc.getDefinitions().get(0);
		assertTrue(node instanceof ObjectTypeDefinition, "The def is a ObjectTypeDefinition");
		ObjectTypeDefinition query = (ObjectTypeDefinition) node;
		assertEquals("Query", query.getName(), "the object type is a query");
		assertEquals(3, query.getFieldDefinitions().size(), "There are three queries");

		/////////////////////////////////////////////////////////////////////////////////////////////////////// :
		// Verifications for helloWorld

		FieldDefinition fieldDef = query.getFieldDefinitions().get(0);
		assertEquals("helloWorld", fieldDef.getName(), "the query name is helloWorld");
		assertEquals("String", ((TypeName) fieldDef.getType()).getName(), "echo is of type String");
		assertEquals(0, fieldDef.getInputValueDefinitions().size(), "echo has no input");

		/////////////////////////////////////////////////////////////////////////////////////////////////////// :
		// Verifications for echoWithName

		fieldDef = query.getFieldDefinitions().get(1);
		assertEquals("echoWithName", fieldDef.getName(), "the query name is echoWithName");
		assertEquals("String", ((TypeName) fieldDef.getType()).getName(), "echoWithName is of type String");
		assertEquals(1, fieldDef.getInputValueDefinitions().size(), "echoWithName has one input");

		InputValueDefinition inputValueDef = fieldDef.getInputValueDefinitions().get(0);
		assertTrue(inputValueDef.getType() instanceof NonNullType, "The echoWithName parameter is mandatory");
		NonNullType inputType = (NonNullType) inputValueDef.getType();
		assertEquals("String", ((TypeName) inputType.getType()).getName(), "echoWithName is of type String");

		/////////////////////////////////////////////////////////////////////////////////////////////////////// :
		// Verifications for echoWithOptionalName

		fieldDef = query.getFieldDefinitions().get(2);
		assertEquals("echoWithOptionalName", fieldDef.getName(), "the query name is echoWithOptionalName");
		assertEquals("String", ((TypeName) fieldDef.getType()).getName(), "echoWithOptionalName is of type String");
		assertEquals(1, fieldDef.getInputValueDefinitions().size(), "echoWithOptionalName has one input");

		inputValueDef = fieldDef.getInputValueDefinitions().get(0);
		assertTrue(inputValueDef.getType() instanceof TypeName, "The echoWithOptionalName parameter is optional");
		assertEquals("String", ((TypeName) inputValueDef.getType()).getName(),
				"echoWithOptionalName is of type String");
	}

}
