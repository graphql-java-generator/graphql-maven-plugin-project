package graphql.mavenplugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.SystemStreamLog;
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
import graphql.language.SchemaDefinition;
import graphql.mavenplugin.language.Field;
import graphql.mavenplugin.language.FieldType;
import graphql.mavenplugin.language.ObjectType;
import graphql.parser.Parser;

/**
 * 
 * @author EtienneSF
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SpringConfiguration.class })
class DocumentParserTest {

	final static String BASE_PACKAGE = "org.graphql.mavenplugin.test.generated";

	@Autowired
	private ApplicationContext ctx;

	private DocumentParser documentParser;
	private Parser parser;

	private Document doc;

	@BeforeEach
	void setUp() throws Exception {
		documentParser = new DocumentParser();
		documentParser.basePackage = BASE_PACKAGE;
		documentParser.log = new SystemStreamLog();
		parser = new Parser();

		// By default, we parse the allGraphQLCases, as it contains all the cases managed by the plugin. It's the most
		// used in the latter unit tests.
		Resource resource = ctx.getResource("/allGraphQLCases.graphqls");
		doc = parser.parseDocument(readSchema(resource));
	}

	@Test
	void test_parseDocuments() throws MojoExecutionException {
		// Preparation
		Document basic = parser.parseDocument(readSchema(ctx.getResource("/helloworld.graphqls")));
		Document helloWorld = parser.parseDocument(readSchema(ctx.getResource("/helloworld.graphqls")));
		documentParser.documents = new ArrayList<Document>();
		documentParser.documents.add(basic);
		documentParser.documents.add(helloWorld);

		// Go, go, go
		int i = documentParser.parseDocuments();

		// Verification
		assertEquals(3, i, "3 classes expected");
	}

	@Test
	void test_parseOneDocument_basic() {
		// Preparation
		Resource resource = ctx.getResource("/basic.graphqls");
		doc = parser.parseDocument(readSchema(resource));

		// Go, go, go
		int i = documentParser.parseOneDocument(doc);

		// Verification
		assertEquals(2, i, "One class is generated");
	}

	@Test
	void test_parseOneDocument_helloworld() {
		// Preparation
		Resource resource = ctx.getResource("/helloworld.graphqls");
		doc = parser.parseDocument(readSchema(resource));

		// Go, go, go
		int i = documentParser.parseOneDocument(doc);

		// Verification
		assertEquals(1, i, "Two classes are generated");
	}

	@Test
	void test_parseOneDocument_allGrahpQLCases() {
		// Go, go, go
		int i = documentParser.parseOneDocument(doc);

		// Verification
		assertEquals(6, i, "Six classes are generated");
	}

	@Test
	void test_addObjectType_noImplement() {
		// Preparation
		String objectName = "allFieldCases";
		ObjectTypeDefinition def = null;
		for (Definition<?> node : doc.getDefinitions()) {
			if (node instanceof ObjectTypeDefinition && ((ObjectTypeDefinition) node).getName().equals(objectName)) {
				def = (ObjectTypeDefinition) node;
			}
		} // for
		assertNotNull(def, "We should have found our test case (" + objectName + ")");
		// To be sure to properly find our parsed object type, we empty the documentParser objects list.
		documentParser.objectTypes = new ArrayList<ObjectType>();

		// Go, go, go
		ObjectType type = documentParser.readObjectType(def);

		// Verification
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

	@Test
	void test_readSchemaDefinition() {
		// Preparation
		List<String> queries = new ArrayList<>();
		List<String> mutations = new ArrayList<>();
		List<String> subscriptions = new ArrayList<>();
		String objectName = "schema";
		SchemaDefinition schema = null;
		for (Definition<?> node : doc.getDefinitions()) {
			if (node instanceof SchemaDefinition) {
				schema = (SchemaDefinition) node;
				break;
			}
		} // for
		assertNotNull(schema, "We should have found our test case (" + objectName + ")");
		// To be sure to properly find our parsed object type, we empty the documentParser objects list.
		documentParser.objectTypes = new ArrayList<ObjectType>();

		// Go, go, go
		documentParser.readSchemaDefinition(schema, queries, mutations, subscriptions);

		// Verification
		assertEquals(1, queries.size(), "Nb queries");
		assertEquals("MyQueryType", queries.get(0), "the query");

		assertEquals(1, mutations.size(), "Nb mutations");
		assertEquals("AnotherMutationType", mutations.get(0), "the mutation");

		assertEquals(1, subscriptions.size(), "Nb subscriptions");
		assertEquals("TheSubscriptionType", subscriptions.get(0), "the subscription");
	}

	@Test
	void test_addObjectType_QueryType() {
		// Preparation
		String objectName = "MyQueryType";
		ObjectTypeDefinition def = null;
		for (Definition<?> node : doc.getDefinitions()) {
			if (node instanceof ObjectTypeDefinition && ((ObjectTypeDefinition) node).getName().equals(objectName)) {
				def = (ObjectTypeDefinition) node;
			}
		} // for
		assertNotNull(def, "We should have found our test case (" + objectName + ")");
		// To be sure to properly find our parsed object type, we empty the documentParser objects list.
		documentParser.queryTypes = new ArrayList<ObjectType>();

		// Go, go, go
		ObjectType type = documentParser.readObjectType(def);

		// Verification
		assertEquals("MyQueryType", type.getName(), "The name is MyQueryType");
		assertEquals(5, type.getFields().size(), "Number of queries");

		int j = 0; // The first query is 0, see ++j below

		// Each query is actually a field. So we use :
		// checkField(field, fieldDescForJUnitMessage, name, list, mandatory, itemMandatory, typeName, clazz)
		//
		// withoutParameters: [Character]!
		checkField(type, j, "withoutParameters", true, true, false, "Character", BASE_PACKAGE + ".Character");
		j += 1;
		// withOneOptionalParam(character: Character): Character
		checkField(type, j, "withOneOptionalParam", false, false, null, "Character", BASE_PACKAGE + ".Character");
		checkInputParameter(type, j, 0, "character", false, false, null, "Character", BASE_PACKAGE + ".Character",
				null);
		j += 1;
		// withOneMandatoryParam(character: Character!): Character
		checkField(type, j, "withOneMandatoryParam", false, false, false, "Character", BASE_PACKAGE + ".Character");
		checkInputParameter(type, j, 0, "character", false, true, null, "Character", BASE_PACKAGE + ".Character", null);
		j += 1;
		// withOneMandatoryParamDefaultValue(character: Character! = "no one"): Character!
		checkField(type, j, "withOneMandatoryParamDefaultValue", false, true, false, "Character",
				BASE_PACKAGE + ".Character");
		checkInputParameter(type, j, 0, "character", false, true, null, "Character", BASE_PACKAGE + ".Character",
				"no one");
		j += 1;
		// withTwoMandatoryParamDefaultVal(theHero: Droid! = "A droid", index: int = "Not a number, but ok !!"): Droid!
		checkField(type, j, "withTwoMandatoryParamDefaultVal", false, true, null, "Droid", BASE_PACKAGE + ".Droid");
		checkInputParameter(type, j, 0, "theHero", false, true, null, "Droid", BASE_PACKAGE + ".Droid", "A droid");
		checkInputParameter(type, j, 1, "index", false, false, null, "int", "java.lang.Integer",
				"Not a number, but ok !!");
		j += 1;
	}

	@Test
	void test_addObjectType_MutationType() {
		fail("not tested");
	}

	@Test
	void test_addObjectType_SubscriptionType() {
		fail("not tested");
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

	private void checkInputParameter(ObjectType type, int j, int numParam, String name, boolean list, boolean mandatory,
			Boolean itemMandatory, String typeName, String classname, String defaultValue) {
		Field inputValue = type.getFields().get(j).getInputParameters().get(numParam);

		String intputParamDescForJUnitMessage = "Field n°" + j + " / input param n°" + numParam;

		assertEquals(name, inputValue.getName(), "name is " + name + " (for " + intputParamDescForJUnitMessage + ")");
		assertEquals(list, inputValue.isList(), "list is " + list + " (for " + intputParamDescForJUnitMessage + ")");
		assertEquals(mandatory, inputValue.isMandatory(),
				"mandatory is " + mandatory + " (for " + intputParamDescForJUnitMessage + ")");
		if (itemMandatory != null) {
			assertEquals(itemMandatory, inputValue.isItemMandatory(),
					"itemMandatory is " + itemMandatory + " (for " + intputParamDescForJUnitMessage + ")");
		}

		FieldType fieldType = inputValue.getType();
		assertEquals(typeName, fieldType.getName(),
				"name is " + typeName + " (for " + intputParamDescForJUnitMessage + ")");
		assertEquals(classname, fieldType.getJavaClassName(),
				"Class type is " + classname + " (for " + intputParamDescForJUnitMessage + ")");

		assertEquals(defaultValue, inputValue.getDefaultValue(),
				"Default Value is <" + defaultValue + "> (for " + intputParamDescForJUnitMessage + ")");
	}
}
