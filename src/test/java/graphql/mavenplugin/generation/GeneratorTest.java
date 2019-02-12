package graphql.mavenplugin.generation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import graphql.language.Definition;
import graphql.language.Document;
import graphql.language.ObjectTypeDefinition;
import graphql.mavenplugin.SpringConfiguration;
import graphql.parser.Parser;

/**
 * 
 * @author EtienneSF
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SpringConfiguration.class })
class GeneratorTest {

	final static String BASE_PACKAGE = "org.graphql.maven.generated";

	@Autowired
	private ApplicationContext ctx;

	private Generator generator;
	private Parser parser;

	private Document doc;

	@BeforeEach
	void setUp() throws Exception {
		generator = new Generator();
		generator.basePackage = BASE_PACKAGE;
		parser = new Parser();

		// By default, we parse the allGraphQLCases, as it contains all the cases managed by the plugin. It's the most
		// used in the latter unit tests.
		Resource resource = ctx.getResource("/allGraphQLCases.graphqls");
		doc = parser.parseDocument(readSchema(resource));
	}

	@Test
	void testGenerateTargetFiles() throws MojoExecutionException {
		// Preparation
		Document basic = parser.parseDocument(readSchema(ctx.getResource("/helloworld.graphqls")));
		Document helloWorld = parser.parseDocument(readSchema(ctx.getResource("/helloworld.graphqls")));
		generator.documents = new ArrayList<Document>();
		generator.documents.add(basic);
		generator.documents.add(helloWorld);

		// Go, go, go
		int i = generator.generateTargetFiles();

		// Verification
		assertEquals(3, i, "3 classes expected");
	}

	@Test
	void test_generateForOneDocument_basic() {
		// Preparation
		Resource resource = ctx.getResource("/basic.graphqls");
		doc = parser.parseDocument(readSchema(resource));

		// Go, go, go
		int i = generator.generateForOneDocument(doc);

		// Verification
		assertEquals(1, i, "One class is generated");
	}

	@Test
	void test_generateForOneDocument_helloworld() {
		// Preparation
		Resource resource = ctx.getResource("/helloworld.graphqls");
		doc = parser.parseDocument(readSchema(resource));

		// Go, go, go
		int i = generator.generateForOneDocument(doc);

		// Verification
		assertEquals(2, i, "Two classes are generated");
	}

	@Test
	void test_generateForOneDocument_allGrapQLCases() {
		// Go, go, go
		int i = generator.generateForOneDocument(doc);

		// Verification
		assertEquals(6, i, "Six classes are generated");
	}

	@Test
	void test_addObjectType_noImplement() {
		// Preparation
		ObjectTypeDefinition def = null;
		for (Definition<?> node : doc.getDefinitions()) {
			if (node instanceof ObjectTypeDefinition
					&& ((ObjectTypeDefinition) node).getName().equals("allFieldCases")) {
				def = (ObjectTypeDefinition) node;
			}
		} // for
		assertNotNull(def, "We should have found our test case");
		// To be sure to properly find our parsed object type, we empty the generator objects list.
		generator.objectTypes = new ArrayList<ObjectType>();

		// Go, go, go
		int i = generator.addObjectType(def);

		// Verification
		assertEquals(1, i, "Exactly one type is added to the list (response)");
		assertEquals(1, generator.objectTypes.size(), "Exactly one type is added to the list (list)");

		ObjectType type = generator.objectTypes.get(0);
		assertEquals("allFieldCases", type.getName(), "The name is allFieldCases");
		assertEquals(10, type.getFields().size(), "Number of fields");

		int j = 0; // The first field is 0, see ++j below

		// checkField(field, fieldDescForJUnitMessage, name, list, mandatory, itemMandatory, typeName, clazz)
		// id: ID!
		checkField(type, j++, "id", false, true, null, "ID", String.class.getName());
		// name: String!
		checkField(type, j++, "name", false, true, null, "String", String.class.getName());
		// forname: String
		checkField(type, j++, "forname", false, false, null, "String", String.class.getName());
		// age: int!
		checkField(type, j++, "age", false, true, null, "int", int.class.getName());
		// nbComments: int
		checkField(type, j++, "nbComments", false, false, null, "int", Integer.class.getName());
		// comments: [String]
		checkField(type, j++, "comments", true, false, false, "String", String.class.getName());
		// booleans: [boolean!]
		checkField(type, j++, "booleans", true, false, true, "boolean", boolean.class.getName());
		// aliases: [String]!
		checkField(type, j++, "aliases", true, true, false, "String", String.class.getName());
		// planets: [String!]!
		checkField(type, j++, "planets", true, true, true, "String", String.class.getName());
		// friends: [Human!]
		checkField(type, j++, "friends", true, false, true, "Human", BASE_PACKAGE + ".Human");
	}

	@Test
	void test_addObjectType_withImplement() {
		fail("not yet implemented");
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private String readSchema(Resource resource) {
		StringWriter writer = new StringWriter();
		try (InputStream inputStream = resource.getInputStream()) {
			IOUtils.copy(inputStream, writer, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new IllegalStateException("Cannot read graphql schema from resource " + resource, e);
		}
		return writer.toString();
	}

	private void checkField(ObjectType type, int j, String name, boolean list, boolean mandatory, Boolean itemMandatory,
			String typeName, String classname) {
		Field field = type.getFields().get(j);
		String fieldDescForJUnitMessage = "Field n°" + j;

		assertEquals(name, field.getName(), "field name is " + name + " (for " + fieldDescForJUnitMessage + ")");
		assertEquals(list, field.isList(), "field list is " + list + " (for " + fieldDescForJUnitMessage + ")");
		assertEquals(mandatory, field.isMandatory(),
				"field mandatory is " + mandatory + " (for " + fieldDescForJUnitMessage + ")");
		if (itemMandatory != null) {
			assertEquals(itemMandatory, field.isItemMandatory(),
					"field itemMandatory is " + itemMandatory + " (for " + fieldDescForJUnitMessage + ")");
		}

		FieldType fieldType = field.getType();
		assertEquals(typeName, fieldType.getName(),
				"type name is " + typeName + " (for " + fieldDescForJUnitMessage + ")");
		assertEquals(classname, fieldType.getJavaClassName(),
				"Class for field type is " + classname + " (for " + fieldDescForJUnitMessage + ")");
	}

}
