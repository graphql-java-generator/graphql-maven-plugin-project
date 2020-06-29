package com.graphql_java_generator.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.graphql_java_generator.plugin.language.DataFetcher;
import com.graphql_java_generator.plugin.language.DataFetchersDelegate;
import com.graphql_java_generator.plugin.language.EnumValue;
import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.Type;
import com.graphql_java_generator.plugin.language.impl.DataFetcherImpl;
import com.graphql_java_generator.plugin.language.impl.DataFetchersDelegateImpl;
import com.graphql_java_generator.plugin.language.impl.EnumType;
import com.graphql_java_generator.plugin.language.impl.InterfaceType;
import com.graphql_java_generator.plugin.language.impl.ObjectType;

import graphql.language.ArrayValue;
import graphql.language.BooleanValue;
import graphql.language.Definition;
import graphql.language.DirectiveDefinition;
import graphql.language.Document;
import graphql.language.EnumTypeDefinition;
import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.language.ObjectField;
import graphql.language.ObjectTypeDefinition;
import graphql.language.ObjectValue;
import graphql.language.SchemaDefinition;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.mavenplugin_notscannedbyspring.AllGraphQLCases_Server_SpringConfiguration;

/**
 * 
 * @author etienne-sf
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { AllGraphQLCases_Server_SpringConfiguration.class })
class DocumentParser_allGraphQLCases_Server_Test {

	@Resource
	private GraphQLDocumentParser graphQLDocumentParser;

	@Resource
	private GraphQLConfiguration pluginConfiguration;

	@Resource
	List<Document> documents;

	@BeforeEach
	void setUp() throws Exception {
		//
	}

	@Test
	@DirtiesContext
	void test_parseOneDocument_allGraphQLCases() {
		// Go, go, go
		int i = graphQLDocumentParser.parseDocuments();

		// Verification
		assertEquals(29, i, "Nb java files are generated");
		assertEquals(6, graphQLDocumentParser.directives.size(), "Nb directives");
		assertEquals(19, graphQLDocumentParser.objectTypes.size(), "Nb objects");
		assertEquals(4, graphQLDocumentParser.customScalars.size(), "Nb custom scalars");
		assertEquals(4, graphQLDocumentParser.interfaceTypes.size(), "Nb interfaces");
		assertEquals(3, graphQLDocumentParser.enumTypes.size(), "Nb enums");
		assertNotNull(graphQLDocumentParser.queryType, "One query");
		assertNotNull(graphQLDocumentParser.mutationType, "One mutation");
		assertNotNull(graphQLDocumentParser.subscriptionType, "One subscription");

		assertEquals("query", graphQLDocumentParser.queryType.getRequestType());
		assertEquals("mutation", graphQLDocumentParser.mutationType.getRequestType());
		assertEquals("subscription", graphQLDocumentParser.subscriptionType.getRequestType());

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Checks if the boolean completableFuture is set correctly:
		// - the first one is the basic one
		// - the second one is the "dataLoader" one
		DataFetcherImpl dataFetcher = findDataFetcher("DataFetchersDelegateAllFieldCases", "oneWithIdSubType", 1);
		assertFalse(dataFetcher.isCompletableFuture(), "oneWithIdSubType: the standard one");

		dataFetcher = findDataFetcher("DataFetchersDelegateAllFieldCases", "oneWithIdSubType", 2);
		assertTrue(dataFetcher.isCompletableFuture(), "oneWithIdSubType: the dataLoader one");
		//
		dataFetcher = findDataFetcher("DataFetchersDelegateAllFieldCases", "listWithIdSubTypes", 1);
		assertFalse(dataFetcher.isCompletableFuture(), "listWithIdSubTypes (only standard dataFethcher here)");
		//
		dataFetcher = findDataFetcher("DataFetchersDelegateAllFieldCases", "oneWithoutIdSubType", 1);
		assertFalse(dataFetcher.isCompletableFuture(), "oneWithoutIdSubType (only standard dataFethcher here)");
		//
		dataFetcher = findDataFetcher("DataFetchersDelegateAllFieldCases", "listWithoutIdSubTypes", 1);
		assertFalse(dataFetcher.isCompletableFuture(), "listWithoutIdSubTypes (only standard dataFethcher here)");

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Checks if input types for the AllFieldCases object are correctly read
		//
		ObjectType objectType = (ObjectType) graphQLDocumentParser.getType("AllFieldCases");
		int j = 0;
		// checkField(type, j, name, list, mandatory, itemMandatory, typeName, classname)
		// checkInputParameter(type, j, numParam, name, list, mandatory, itemMandatory, typeName, classname,
		// defaultValue)
		//
		// id: ID!
		checkField(objectType, j, "id", false, true, null, "ID", "java.util.UUID");
		checkNbInputParameter(objectType, j, 0);
		j += 1;
		// name: String!
		checkField(objectType, j, "name", false, true, null, "String", "java.lang.String");
		checkNbInputParameter(objectType, j, 0);
		j += 1;
		// forname(uppercase: Boolean, textToAppendToTheForname: String): String
		checkField(objectType, j, "forname", false, false, null, "String", "java.lang.String");
		checkNbInputParameter(objectType, j, 2);
		checkInputParameter(objectType, j, 0, "uppercase", false, false, null, "Boolean", "java.lang.Boolean", null);
		checkInputParameter(objectType, j, 1, "textToAppendToTheForname", false, false, null, "String",
				"java.lang.String", null);
		j += 1;
		// age: Long!
		checkField(objectType, j, "age", false, true, null, "Long", "java.lang.Long");
		checkNbInputParameter(objectType, j, 1);
		checkInputParameter(objectType, j, 0, "unit", false, false, null, "Unit",
				pluginConfiguration.getPackageName() + ".Unit", new graphql.language.EnumValue("YEAR"));
		j += 1;
		// aFloat: Float
		checkField(objectType, j, "aFloat", false, false, null, "Float", "java.lang.Double");
		checkNbInputParameter(objectType, j, 0);
		j += 1;
		// date: Date
		checkField(objectType, j, "date", false, false, null, "Date", "java.util.Date");
		checkNbInputParameter(objectType, j, 0);
		j += 1;
		// dates: [Date]!
		checkField(objectType, j, "dates", true, true, false, "Date", "java.util.Date");
		checkNbInputParameter(objectType, j, 0);
		j += 1;
		// nbComments: Int
		checkField(objectType, j, "nbComments", false, false, null, "Int", "java.lang.Integer");
		checkNbInputParameter(objectType, j, 0);
		j += 1;
		// comments: [String]
		checkField(objectType, j, "comments", true, false, false, "String", "java.lang.String");
		checkNbInputParameter(objectType, j, 0);
		j += 1;
		// booleans: [Boolean!]
		checkField(objectType, j, "booleans", true, false, true, "Boolean", "java.lang.Boolean");
		checkNbInputParameter(objectType, j, 0);
		j += 1;
		// aliases: [String]!
		checkField(objectType, j, "aliases", true, true, false, "String", "java.lang.String");
		checkNbInputParameter(objectType, j, 0);
		j += 1;
		// planets: [String!]!
		checkField(objectType, j, "planets", true, true, true, "String", "java.lang.String");
		checkNbInputParameter(objectType, j, 0);
		j += 1;
		// friends: [Human!]
		checkField(objectType, j, "friends", true, false, true, "Human",
				pluginConfiguration.getPackageName() + ".Human");
		checkNbInputParameter(objectType, j, 0);
		j += 1;
		// oneWithIdSubType: AllFieldCasesWithIdSubtype
		checkField(objectType, j, "oneWithIdSubType", false, false, null, "AllFieldCasesWithIdSubtype",
				pluginConfiguration.getPackageName() + ".AllFieldCasesWithIdSubtype");
		checkNbInputParameter(objectType, j, 0);
		j += 1;
		// listWithIdSubTypes(nbItems: Long!, date: Date, dates: [Date]!, uppercaseName: Boolean,
		// textToAppendToTheForname: String): [AllFieldCasesWithIdSubtype]
		checkField(objectType, j, "listWithIdSubTypes", true, false, false, "AllFieldCasesWithIdSubtype",
				pluginConfiguration.getPackageName() + ".AllFieldCasesWithIdSubtype");
		checkNbInputParameter(objectType, j, 5);
		checkInputParameter(objectType, j, 0, "nbItems", false, true, null, "Long", "java.lang.Long", null);
		checkInputParameter(objectType, j, 1, "date", false, false, null, "Date", "java.util.Date", null);
		checkInputParameter(objectType, j, 2, "dates", true, true, false, "Date", "java.util.Date", null);
		checkInputParameter(objectType, j, 3, "uppercaseName", false, false, null, "Boolean", "java.lang.Boolean",
				null);
		checkInputParameter(objectType, j, 4, "textToAppendToTheForname", false, false, null, "String",
				"java.lang.String", null);
		j += 1;
		// oneWithoutIdSubType(input: FieldParameterInput): AllFieldCasesWithoutIdSubtype
		checkField(objectType, j, "oneWithoutIdSubType", false, false, false, "AllFieldCasesWithoutIdSubtype",
				pluginConfiguration.getPackageName() + ".AllFieldCasesWithoutIdSubtype");
		checkNbInputParameter(objectType, j, 1);
		checkInputParameter(objectType, j, 0, "input", false, false, null, "FieldParameterInput",
				pluginConfiguration.getPackageName() + ".FieldParameterInput", null);
		j += 1;
		// listWithoutIdSubTypes(nbItems: Int!, input: FieldParameterInput, textToAppendToTheForname: String):
		// [AllFieldCasesWithoutIdSubtype]
		checkField(objectType, j, "listWithoutIdSubTypes", true, false, false, "AllFieldCasesWithoutIdSubtype",
				pluginConfiguration.getPackageName() + ".AllFieldCasesWithoutIdSubtype");
		checkNbInputParameter(objectType, j, 3);
		checkInputParameter(objectType, j, 0, "nbItems", false, true, null, "Long", "java.lang.Long", null);
		checkInputParameter(objectType, j, 1, "input", false, false, null, "FieldParameterInput",
				pluginConfiguration.getPackageName() + ".FieldParameterInput", null);
		checkInputParameter(objectType, j, 2, "textToAppendToTheForname", false, false, null, "String",
				"java.lang.String", null);
		j += 1;

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Checks of type implementing multiples interfaces
		objectType = (ObjectType) graphQLDocumentParser.getType("Human");
		//
		assertEquals(4, objectType.getImplementz().size());
		assertTrue(objectType.getImplementz().contains("Character"));
		assertTrue(objectType.getImplementz().contains("Commented"));
		assertTrue(objectType.getImplementz().contains("WithID"));
		assertTrue(objectType.getImplementz().contains("AnyCharacter"));// This is an union
		//
		InterfaceType interfaceType = (InterfaceType) graphQLDocumentParser.getType("WithID");
		assertEquals(3, interfaceType.getImplementingTypes().size());
		assertEquals("AllFieldCases", interfaceType.getImplementingTypes().get(0).getName());
		assertEquals("Human", interfaceType.getImplementingTypes().get(1).getName());
		assertEquals("Droid", interfaceType.getImplementingTypes().get(2).getName());

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Checks of directive parsing
		i = 0;
		assertEquals("skip", graphQLDocumentParser.directives.get(i++).getName());
		assertEquals("include", graphQLDocumentParser.directives.get(i++).getName());
		assertEquals("defer", graphQLDocumentParser.directives.get(i++).getName());
		assertEquals("deprecated", graphQLDocumentParser.directives.get(i++).getName());
		assertEquals("testDirective", graphQLDocumentParser.directives.get(i++).getName());
		assertEquals("anotherTestDirective", graphQLDocumentParser.directives.get(i++).getName());

		// On Scalar
		checkDirectivesOnType(graphQLDocumentParser.getType("Date"), true, "on Scalar", null, null, null, null, null,
				null, null, true);
		checkDirectivesOnType(graphQLDocumentParser.getType("Long"), false, null, null, null, null, null, null, null,
				null, false);

		// On schema
		// Currently not managed (schema is not stored, and no java classes is generated afteward for the schema)

		// On enum
		checkDirectivesOnType(graphQLDocumentParser.getType("Episode"), true, "on Enum", "69", 666, (float) 666.666,
				true, "00000000-0000-0000-0000-000000000002", null, "2001-02-28", false);
		checkDirectivesOnType(graphQLDocumentParser.getType("Unit"), false, null, null, null, null, null, null, null,
				null, false);
		// On enum item
		checkDirectivesOnEnumValue(graphQLDocumentParser.getType("Episode"), "DOES_NOT_EXIST", true, "on Enum", "-1",
				true);
		checkDirectivesOnEnumValue(graphQLDocumentParser.getType("Episode"), "JEDI", false, null, null, false);
		checkDirectivesOnEnumValue(graphQLDocumentParser.getType("Episode"), "EMPIRE", false, null, null, true);
		// On interface
		checkDirectivesOnType(graphQLDocumentParser.getType("WithID"), true, "on Interface", "666", null, null, null,
				null, null, null, false);
		checkDirectivesOnType(graphQLDocumentParser.getType("Character"), true, "on Character interface", null, null,
				null, null, null, null, null, true);
		// On interface field
		checkDirectivesOnField(graphQLDocumentParser.getType("Character"), "name", true, "on interface field", null,
				true, 0);
		checkDirectivesOnField(graphQLDocumentParser.getType("Character"), "appearsIn", false, null, null, true, 0);
		// On union
		// checkDirectivesOnType(documentParser.getType("AnyCharacter"), true, "on Union", null, false);
		// On input type
		checkDirectivesOnType(graphQLDocumentParser.getType("AllFieldCasesInput"), true, "on Input Type", null, null,
				null, null, null, null, null, false);
		// On input type field
		checkDirectivesOnField(graphQLDocumentParser.getType("AllFieldCasesInput"), "id", true, "on Input Field", null,
				false, 0);
		checkDirectivesOnField(graphQLDocumentParser.getType("AllFieldCasesInput"), "name", false, null, null, false,
				0);
		// On type
		checkDirectivesOnType(graphQLDocumentParser.getType("AllFieldCases"), true, "on Object", null, null, null, null,
				null, null, null, true);
		// On type field
		checkDirectivesOnField(graphQLDocumentParser.getType("AllFieldCases"), "id", true, "on Field", null, false, 0);
		checkDirectivesOnField(graphQLDocumentParser.getType("AllFieldCases"), "name", false, null, null, false, 0);
		// On input parameter
		checkDirectivesOnInputParameter(graphQLDocumentParser.getType("AllFieldCases"), "forname", "uppercase", true,
				"on Argument", null, false);
		checkDirectivesOnInputParameter(graphQLDocumentParser.getType("AllFieldCases"), "forname",
				"textToAppendToTheForname", false, null, null, false);
	}

	/**
	 * Check that a Directive for an object, field, scalar (...) has been properly parsed
	 * 
	 * @param type
	 * @param containsTestDirective
	 *            true if this type contains the testDirective
	 * @param value
	 *            Value of the 'value' field of the testDirective
	 * @param anotherValue
	 *            Value of the 'anotherValue' field of the testDirective
	 * @param containsAnotherTestDirective
	 *            true if this type contains the anotherTestDirective
	 */
	private void checkDirectivesOnType(Type type, boolean containsTestDirective, String value, String anotherValue,
			Integer anInt, Float aFloat, Boolean aBoolean, String anID, String anEnumName, String aCustomScalarDate,
			boolean containsAnotherTestDirective) {

		int nbDirectives = (containsTestDirective ? 1 : 0) + (containsAnotherTestDirective ? 1 : 0);
		assertEquals(nbDirectives, type.getAppliedDirectives().size());
		if (containsTestDirective) {
			assertEquals("testDirective", type.getAppliedDirectives().get(0).getDirective().getName());
			// Check of the arguments
			assertEquals(value,
					((StringValue) type.getAppliedDirectives().get(0).getArgumentValues().get("value")).getValue());
			if (anotherValue != null)
				assertEquals(anotherValue,
						((StringValue) type.getAppliedDirectives().get(0).getArgumentValues().get("anotherValue"))
								.getValue());
			if (anInt != null)
				assertEquals(BigInteger.valueOf(anInt),
						((IntValue) type.getAppliedDirectives().get(0).getArgumentValues().get("anInt")).getValue());
			if (aFloat != null)
				assertEquals(aFloat, ((FloatValue) type.getAppliedDirectives().get(0).getArgumentValues().get("aFloat"))
						.getValue().floatValue());
			if (aBoolean != null)
				assertEquals(aBoolean,
						((BooleanValue) type.getAppliedDirectives().get(0).getArgumentValues().get("aBoolean"))
								.isValue());
			if (anID != null)
				assertEquals(anID,
						((StringValue) type.getAppliedDirectives().get(0).getArgumentValues().get("anID")).getValue());
			if (anEnumName != null)
				assertEquals(anEnumName, ((graphql.language.EnumValue) type.getAppliedDirectives().get(0)
						.getArgumentValues().get("anEnum")).getName());
			if (aCustomScalarDate != null)
				assertEquals(aCustomScalarDate,
						((StringValue) type.getAppliedDirectives().get(0).getArgumentValues().get("aCustomScalarDate"))
								.getValue());
		}
		if (containsAnotherTestDirective) {
			assertEquals("anotherTestDirective", type.getAppliedDirectives().get(1).getDirective().getName());
		}
	}

	/**
	 * Check that a Directive for an object, field, scalar (...) has been properly parsed
	 * 
	 * @param type
	 * @param fieldName
	 *            The name of the field, within the given type
	 * @param containsTestDirective
	 *            true if this type contains the testDirective
	 * @param value
	 *            Value of the 'value' field of the testDirective
	 * @param anotherValue
	 *            Value of the 'anotherValue' field of the testDirective
	 * @param containsAnotherTestDirective
	 *            true if this type contains the anotherTestDirective
	 */
	private void checkDirectivesOnField(Type type, String fieldName, boolean containsTestDirective, String value,
			String anotherValue, boolean containsAnotherTestDirective, int nbOtherDirectives) {

		Field field = null;
		for (Field f : type.getFields()) {
			if (f.getName().equals(fieldName)) {
				field = f;
				break;
			}
		}
		if (field == null) {
			fail("Could not find the field '" + fieldName + "' on type '" + type.getName() + "'");
		}

		int nbDirectives = (containsTestDirective ? 1 : 0) + (containsAnotherTestDirective ? 1 : 0) + nbOtherDirectives;
		assertEquals(nbDirectives, field.getAppliedDirectives().size());
		if (containsTestDirective) {
			assertEquals("testDirective", field.getAppliedDirectives().get(0).getDirective().getName());
			// check arguments
			assertEquals(value,
					((StringValue) field.getAppliedDirectives().get(0).getArgumentValues().get("value")).getValue());
			if (anotherValue != null)
				assertEquals(anotherValue, field.getAppliedDirectives().get(0).getArgumentValues().get("anotherValue"));
		}
		if (containsAnotherTestDirective) {
			int index = containsTestDirective ? 1 : 0;
			assertEquals("anotherTestDirective", field.getAppliedDirectives().get(index).getDirective().getName());
		}
	}

	/**
	 * Check that a Directive for an object, field, scalar (...) has been properly parsed
	 * 
	 * @param type
	 * @param enumValueName
	 *            The name of the field, within the given type
	 * @param containsTestDirective
	 *            true if this type contains the testDirective
	 * @param value
	 *            Value of the 'value' field of the testDirective
	 * @param anotherValue
	 *            Value of the 'anotherValue' field of the testDirective
	 * @param containsAnotherTestDirective
	 *            true if this type contains the anotherTestDirective
	 */
	private void checkDirectivesOnEnumValue(Type type, String enumValueName, boolean containsTestDirective,
			String value, String anotherValue, boolean containsAnotherTestDirective) {

		EnumValue enumValue = null;
		for (EnumValue f : ((EnumType) type).getValues()) {
			if (f.getName().equals(enumValueName)) {
				enumValue = f;
				break;
			}
		}
		if (enumValue == null) {
			fail("Could not find the enum value '" + enumValueName + "' on enum '" + type.getName() + "'");
		}

		int nbDirectives = (containsTestDirective ? 1 : 0) + (containsAnotherTestDirective ? 1 : 0);
		assertEquals(nbDirectives, enumValue.getAppliedDirectives().size());
		if (containsTestDirective) {
			assertEquals("testDirective", enumValue.getAppliedDirectives().get(0).getDirective().getName());
			// check arguments
			assertEquals(value, ((StringValue) enumValue.getAppliedDirectives().get(0).getArgumentValues().get("value"))
					.getValue());
			if (anotherValue != null)
				assertEquals(anotherValue,
						((StringValue) enumValue.getAppliedDirectives().get(0).getArgumentValues().get("anotherValue"))
								.getValue());
		}
		if (containsAnotherTestDirective) {
			int index = containsTestDirective ? 1 : 0;
			assertEquals("anotherTestDirective", enumValue.getAppliedDirectives().get(index).getDirective().getName());
		}
	}

	/**
	 * Check that a Directive for an object, field, scalar (...) has been properly parsed
	 * 
	 * @param type
	 * @param fieldName
	 *            The name of the field, within the given type
	 * @param containsTestDirective
	 *            true if this type contains the testDirective
	 * @param value
	 *            Value of the 'value' field of the testDirective
	 * @param anotherValue
	 *            Value of the 'anotherValue' field of the testDirective
	 * @param containsAnotherTestDirective
	 *            true if this type contains the anotherTestDirective
	 */
	private void checkDirectivesOnInputParameter(Type type, String fieldName, String parameterName,
			boolean containsTestDirective, String value, Integer anotherValue, boolean containsAnotherTestDirective) {

		// First, we find the field
		Field field = null;
		for (Field f : type.getFields()) {
			if (f.getName().equals(fieldName)) {
				field = f;
				break;
			}
		}
		if (field == null) {
			fail("Could not find the field '" + fieldName + "' on type '" + type.getName() + "'");
		}

		// Second, we find the parameter
		Field parameter = null;
		for (Field p : field.getInputParameters()) {
			if (p.getName().contentEquals(parameterName)) {
				parameter = p;
				break;
			}
		}
		if (parameter == null) {
			fail("Could not find the parameter '" + parameterName + "' for the field '" + fieldName + "' on type '"
					+ type.getName() + "'");
		}

		int nbDirectives = (containsTestDirective ? 1 : 0) + (containsAnotherTestDirective ? 1 : 0);
		assertEquals(nbDirectives, parameter.getAppliedDirectives().size());
		if (containsTestDirective) {
			assertEquals("testDirective", parameter.getAppliedDirectives().get(0).getDirective().getName());
			// check arguments
			assertEquals(value, ((StringValue) parameter.getAppliedDirectives().get(0).getArgumentValues().get("value"))
					.getValue());
			if (anotherValue != null)
				assertEquals(BigInteger.valueOf(anotherValue),
						parameter.getAppliedDirectives().get(0).getArgumentValues().get("anotherValue"));
		}
		if (containsAnotherTestDirective) {
			int index = containsTestDirective ? 1 : 0;
			assertEquals("anotherTestDirective", field.getAppliedDirectives().get(index).getDirective().getName());
		}
	}

	@Test
	@DirtiesContext
	private void test_addObjectType_noImplement() {
		// Preparation
		String objectName = "AllFieldCases";
		ObjectTypeDefinition def = null;
		for (Definition<?> node : documents.get(0).getDefinitions()) {
			if (node instanceof ObjectTypeDefinition && ((ObjectTypeDefinition) node).getName().equals(objectName)) {
				def = (ObjectTypeDefinition) node;
			}
		} // for
		assertNotNull(def, "We should have found our test case (" + objectName + ")");
		// We need to parse the whole document, to get the types map filled.
		graphQLDocumentParser.parseDocuments();
		// To be sure to properly find our parsed object type, we empty the documentParser objects list.
		graphQLDocumentParser.objectTypes = new ArrayList<>();

		// Go, go, go
		ObjectType type = graphQLDocumentParser.readObjectTypeDefinition(def);

		// Verification
		assertEquals(objectName, type.getName(), "Checks the name");
		assertEquals(0, type.getImplementz().size(), "No implementation");
		assertEquals(14, type.getFields().size(), "Number of fields");

		int j = 0; // The first field is 0, see ++j below

		// checkField(type, j, name, list, mandatory, itemMandatory, typeName, classname)
		// id: ID!
		checkField(type, j++, "id", false, true, null, "UUID", UUID.class.getName());
		// name: String!
		checkField(type, j++, "name", false, true, null, "String", String.class.getName());
		// forname: String
		checkField(type, j++, "forname", false, false, null, "String", String.class.getName());
		// age: int!
		checkField(type, j++, "age", false, true, null, "Int", Integer.class.getName());
		// nbComments: int
		checkField(type, j++, "nbComments", false, false, null, "Int", Integer.class.getName());
		// comments: [String]
		checkField(type, j++, "comments", true, false, false, "String", String.class.getName());
		// booleans: [boolean!]
		checkField(type, j++, "booleans", true, false, true, "Boolean", Boolean.class.getName());
		// aliases: [String]!
		checkField(type, j++, "aliases", true, true, false, "String", String.class.getName());
		// planets: [String!]!
		checkField(type, j++, "planets", true, true, true, "String", String.class.getName());
		// friends: [Human!]
		checkField(type, j++, "friends", true, false, true, "Human", pluginConfiguration.getPackageName() + ".Human");
		// oneWithIdSubType: AllFieldCasesWithIdSubtype
		checkField(type, j++, "oneWithIdSubType", false, false, null, "AllFieldCasesWithIdSubtype",
				pluginConfiguration.getPackageName() + ".AllFieldCasesWithIdSubtype");
		// listWithIdSubTypes(uppercaseName: Boolean = True, textToAppendToTheForname: String):
		// [AllFieldCasesWithIdSubtype]
		checkField(type, j++, "listWithIdSubTypes", true, false, false, "AllFieldCasesWithIdSubtype",
				pluginConfiguration.getPackageName() + ".AllFieldCasesWithIdSubtype");
		// oneWithoutIdSubType: AllFieldCasesWithoutIdSubtype
		checkField(type, j++, "oneWithoutIdSubType", false, false, null, "AllFieldCasesWithoutIdSubtype",
				pluginConfiguration.getPackageName() + ".AllFieldCasesWithoutIdSubtype");
		// listWithoutIdSubTypes(uppercaseName: Boolean = True, textToAppendToTheForname: String):
		// [AllFieldCasesWithoutIdSubtype]
		checkField(type, j++, "listWithoutIdSubTypes", true, false, false, "AllFieldCasesWithoutIdSubtype",
				pluginConfiguration.getPackageName() + ".AllFieldCasesWithoutIdSubtype");

	}

	@Test
	@DirtiesContext
	void test_addObjectType_withImplement() {
		// Preparation
		String objectName = "Human";
		ObjectTypeDefinition def = null;
		for (Definition<?> node : documents.get(0).getDefinitions()) {
			if (node instanceof ObjectTypeDefinition && ((ObjectTypeDefinition) node).getName().equals(objectName)) {
				def = (ObjectTypeDefinition) node;
			}
		} // for
		assertNotNull(def, "We should have found our test case (" + objectName + ")");
		// We need to parse the whole document, to get the types map filled.
		graphQLDocumentParser.parseDocuments();
		// To be sure to properly find our parsed object type, we empty the documentParser objects list.
		graphQLDocumentParser.objectTypes = new ArrayList<>();

		// Go, go, go
		ObjectType type = graphQLDocumentParser.readObjectTypeDefinition(def);

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Verification
		assertEquals(objectName, type.getName(), "Checks the name");

		// Implementation
		assertEquals(3, type.getImplementz().size(), "Two implementations");
		assertEquals("Character", type.getImplementz().get(0), "First implementation");
		assertEquals("Commented", type.getImplementz().get(1), "Second implementation");
		assertEquals("WithID", type.getImplementz().get(2), "Second implementation");

		// Field
		assertEquals(8, type.getFields().size(), "Number of fields");

		int j = 0; // The first field is 0, see ++j below

		// checkField(type, j, name, list, mandatory, itemMandatory, typeName, classname)
		// id: ID!
		checkField(type, j++, "id", false, true, null, "ID", UUID.class.getName());
		// name: String!
		checkField(type, j++, "name", false, true, null, "String", String.class.getName());
		// bestFriend: Character
		checkField(type, j++, "bestFriend", false, false, null, "Character",
				pluginConfiguration.getPackageName() + ".Character");
		// friends: [Character]
		checkField(type, j++, "friends", true, false, false, "Character",
				pluginConfiguration.getPackageName() + ".Character");
		// nbComments: int
		checkField(type, j++, "nbComments", false, false, null, "Int", Integer.class.getName());
		// comments: [String]
		checkField(type, j++, "comments", true, false, false, "String", String.class.getName());
		// appearsIn: [Episode]!
		checkField(type, j++, "appearsIn", true, true, false, "Episode",
				pluginConfiguration.getPackageName() + ".Episode");
		// homePlanet: String
		checkField(type, j++, "homePlanet", false, false, null, "String", String.class.getName());
	}

	@Test
	@DirtiesContext
	void test_readSchemaDefinition() {
		// Preparation
		List<String> queries = new ArrayList<>();
		List<String> mutations = new ArrayList<>();
		List<String> subscriptions = new ArrayList<>();
		String objectName = "schema";
		SchemaDefinition schema = null;
		for (Definition<?> node : documents.get(0).getDefinitions()) {
			if (node instanceof SchemaDefinition) {
				schema = (SchemaDefinition) node;
				break;
			}
		} // for
		assertNotNull(schema, "We should have found our test case (" + objectName + ")");
		// To be sure to properly find our parsed object type, we empty the documentParser objects list.
		graphQLDocumentParser.objectTypes = new ArrayList<>();

		// Go, go, go
		graphQLDocumentParser.readSchemaDefinition(schema, queries, mutations, subscriptions);

		// Verification
		assertEquals(1, queries.size(), "Nb queries");
		assertEquals("MyQueryType", queries.get(0), "the query");

		assertEquals(1, mutations.size(), "Nb mutations");
		assertEquals("AnotherMutationType", mutations.get(0), "the mutation");

		assertEquals(1, subscriptions.size(), "Nb subscriptions");
		assertEquals("TheSubscriptionType", subscriptions.get(0), "the subscription");
	}

	@Test
	@DirtiesContext
	void test_readObjectType_QueryType() {
		// Preparation
		String objectName = "MyQueryType";
		ObjectTypeDefinition def = null;
		for (Definition<?> node : documents.get(0).getDefinitions()) {
			if (node instanceof ObjectTypeDefinition && ((ObjectTypeDefinition) node).getName().equals(objectName)) {
				def = (ObjectTypeDefinition) node;
			}
		} // for
		assertNotNull(def, "We should have found our test case (" + objectName + ")");
		// We need to parse the whole document, to get the types map filled.
		graphQLDocumentParser.parseDocuments();

		//
		// We need this ObjectValue for one of the next tests:
		@SuppressWarnings("rawtypes")
		List<Value> values = new ArrayList<>();
		values.add(new graphql.language.EnumValue("JEDI"));
		values.add(new graphql.language.EnumValue("NEWHOPE"));
		List<ObjectField> objectFields = new ArrayList<>();
		objectFields.add(new ObjectField("name", new StringValue("droid's name")));
		objectFields.add(new ObjectField("appearsIn", new ArrayValue(values)));
		ObjectValue objectValue = new ObjectValue(objectFields);

		// Go, go, go
		ObjectType type = graphQLDocumentParser.getQueryType();

		// Verification
		assertEquals("MyQueryType", type.getName());
		assertEquals(14, type.getFields().size());

		int j = 0; // The first query is 0, see ++j below

		// Each query is actually a field. So we use :
		// checkField(type, j, name, list, mandatory, itemMandatory, typeName, classname)
		//
		// withoutParameters: [Character]!
		checkField(type, j, "withoutParameters", true, true, false, "Character",
				pluginConfiguration.getPackageName() + ".Character");
		j += 1;
		// withOneOptionalParam(character: Character): Character
		checkField(type, j, "withOneOptionalParam", false, false, null, "Character",
				pluginConfiguration.getPackageName() + ".Character");
		checkInputParameter(type, j, 0, "character", false, false, null, "CharacterInput",
				pluginConfiguration.getPackageName() + ".CharacterInput", null);
		j += 1;
		// withOneMandatoryParam(character: Character!): Character
		checkField(type, j, "withOneMandatoryParam", false, false, false, "Character",
				pluginConfiguration.getPackageName() + ".Character");
		checkInputParameter(type, j, 0, "character", false, true, null, "CharacterInput",
				pluginConfiguration.getPackageName() + ".CharacterInput", null);
		j += 1;
		// withOneMandatoryParamDefaultValue(nbResultat: Int! = 13): Character!
		checkField(type, j, "withOneMandatoryParamDefaultValue", false, true, false, "Character",
				pluginConfiguration.getPackageName() + ".Character");
		checkInputParameter(type, j, 0, "nbResultat", false, true, null, "Int", "java.lang.Integer",
				new IntValue(BigInteger.valueOf(13)));
		j += 1;
		// withTwoMandatoryParamDefaultVal(theHero: DroidInput! = {name: "droid's name", appearsIn:[JEDI,NEWHOPE]}, num:
		// Int = 45): Droid!
		checkField(type, j, "withTwoMandatoryParamDefaultVal", false, true, null, "Droid",
				pluginConfiguration.getPackageName() + ".Droid");
		checkInputParameter(type, j, 0, "theHero", false, true, null, "DroidInput",
				pluginConfiguration.getPackageName() + ".DroidInput", objectValue);
		checkInputParameter(type, j, 1, "num", false, false, null, "Int", "java.lang.Integer",
				new IntValue(BigInteger.valueOf(45)));
		j += 1;
		// withEnum(episode: Episode!): Character
		checkField(type, j, "withEnum", false, false, null, "Character",
				pluginConfiguration.getPackageName() + ".Character");
		checkInputParameter(type, j, 0, "episode", false, true, null, "Episode",
				pluginConfiguration.getPackageName() + ".Episode", new graphql.language.EnumValue("NEWHOPE"));
		j += 1;
		// withList(name: String!, friends: [Character]!): [Characters]
		checkField(type, j, "withList", true, false, false, "Character",
				pluginConfiguration.getPackageName() + ".Character");
		checkInputParameter(type, j, 0, "firstName", false, true, null, "String", String.class.getName(), null);
		checkInputParameter(type, j, 1, "characters", true, true, false, "CharacterInput",
				pluginConfiguration.getPackageName() + ".CharacterInput", null);

	}

	@Test
	@DirtiesContext
	void test_readEnumType() {
		// Preparation
		String objectName = "Episode";
		EnumTypeDefinition def = null;
		for (Definition<?> node : documents.get(0).getDefinitions()) {
			if (node instanceof EnumTypeDefinition && ((EnumTypeDefinition) node).getName().equals(objectName)) {
				def = (EnumTypeDefinition) node;
			}
		} // for
		assertNotNull(def, "We should have found our test case (" + objectName + ")");
		// We need to read the directives first
		graphQLDocumentParser.postConstruct();
		graphQLDocumentParser.documents.get(0).getDefinitions().stream().filter(n -> (n instanceof DirectiveDefinition))
				.forEach(node -> graphQLDocumentParser.directives
						.add(graphQLDocumentParser.readDirectiveDefinition((DirectiveDefinition) node)));
		// To be sure to properly find our parsed object type, we empty the documentParser objects list.
		graphQLDocumentParser.queryType = null;

		// Go, go, go
		EnumType type = graphQLDocumentParser.readEnumType(def);

		// Verification
		assertEquals(objectName, type.getName(), "The name is " + objectName);
		assertEquals(4, type.getValues().size(), "Number of values");

		int i = 0;
		assertEquals("NEWHOPE", type.getValues().get(i++).getName());
		assertEquals("EMPIRE", type.getValues().get(i++).getName());
		assertEquals("JEDI", type.getValues().get(i++).getName());
		assertEquals("DOES_NOT_EXIST", type.getValues().get(i++).getName());
	}

	@Test
	@DirtiesContext
	void test_addObjectType_MutationType() {
		// Preparation
		String objectName = "AnotherMutationType";
		ObjectTypeDefinition def = null;
		for (Definition<?> node : documents.get(0).getDefinitions()) {
			if (node instanceof ObjectTypeDefinition && ((ObjectTypeDefinition) node).getName().equals(objectName)) {
				def = (ObjectTypeDefinition) node;
			}
		} // for
		assertNotNull(def, "We should have found our test case (" + objectName + ")");
		// We need to parse the whole document, to get the types map filled.
		graphQLDocumentParser.parseDocuments();
		// To be sure to properly find our parsed object type, we empty the documentParser objects list.
		graphQLDocumentParser.mutationType = null;

		// Go, go, go
		ObjectType type = graphQLDocumentParser.readObjectTypeDefinition(def);

		// Verification
		assertEquals(objectName, type.getName());
		assertEquals(2, type.getFields().size());

		int j = 0;
		// Each mutation is actually a field. So we use :
		// checkField(type, j, name, list, mandatory, itemMandatory, typeName, classname)
		// checkInputParameter(type, j, numParam, name, list, mandatory, itemMandatory, typeName, classname,
		// defaultValue)
		//
		// createHuman(human: Human!): Human!
		checkField(type, j, "createHuman", false, true, null, "Human", pluginConfiguration.getPackageName() + ".Human");
		checkNbInputParameter(type, j, 1);
		checkInputParameter(type, j, 0, "human", false, true, null, "HumanInput",
				pluginConfiguration.getPackageName() + ".HumanInput", null);
		//
		j += 1;
		checkField(type, j, "createAllFieldCases", false, true, null, "AllFieldCases",
				pluginConfiguration.getPackageName() + ".AllFieldCases");
		checkNbInputParameter(type, j, 1);
		checkInputParameter(type, j, 0, "input", false, true, null, "AllFieldCasesInput",
				pluginConfiguration.getPackageName() + ".AllFieldCasesInput", null);
	}

	@Test
	@DirtiesContext
	void test_addObjectType_SubscriptionType() {
		// Preparation
		String objectName = "TheSubscriptionType";
		ObjectTypeDefinition def = null;
		for (Definition<?> node : documents.get(0).getDefinitions()) {
			if (node instanceof ObjectTypeDefinition && ((ObjectTypeDefinition) node).getName().equals(objectName)) {
				def = (ObjectTypeDefinition) node;
			}
		} // for
		assertNotNull(def, "We should have found our test case (" + objectName + ")");
		// We need to parse the whole document, to get the types map filled.
		graphQLDocumentParser.parseDocuments();
		// To be sure to properly find our parsed object type, we empty the documentParser objects list.
		graphQLDocumentParser.subscriptionType = null;

		// Go, go, go
		ObjectType type = graphQLDocumentParser.readObjectTypeDefinition(def);

		// Verification
		assertEquals(objectName, type.getName());
		assertEquals(1, type.getFields().size());

		int j = 0;
		// Each mutation is actually a field. So we use :
		// checkField(type, j, name, list, mandatory, itemMandatory, typeName, classname)
		// checkInputParameter(type, j, numParam, name, list, mandatory, itemMandatory, typeName, classname,
		// defaultValue)
		//
		// subscribeNewHumanForEpisode(episode: Episode! = NEWHOPE): Human!
		checkField(type, j, "subscribeNewHumanForEpisode", false, true, null, "Human",
				pluginConfiguration.getPackageName() + ".Human");
		checkNbInputParameter(type, j, 1);
		checkInputParameter(type, j, 0, "episode", false, true, null, "Episode",
				pluginConfiguration.getPackageName() + ".Episode", null);
		j += 1;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 * @param delegateName
	 *            The name of the delegate in which we search this data fetcher
	 * @param name
	 *            The name of the searched data fetcher
	 * @param occurrenceNumber
	 *            The occurrence number that is searched: 1 for the first occurrence, 2 for the second one...
	 * @return
	 */
	private DataFetcherImpl findDataFetcher(String delegateName, String name, int occurrenceNumber) {
		DataFetchersDelegateImpl delegate = findDataFetcherDelegate(delegateName);
		for (DataFetcher fetcher : delegate.getDataFetchers()) {
			if (fetcher.getName().equals(name)) {
				if (--occurrenceNumber == 0)
					return (DataFetcherImpl) fetcher;
			}
		}
		fail("DataFetcherImpl '" + delegateName + "." + name + "' not found");
		return null;
	}

	private DataFetchersDelegateImpl findDataFetcherDelegate(String name) {
		for (DataFetchersDelegate delegate : graphQLDocumentParser.dataFetchersDelegates) {
			if (delegate.getName().equals(name))
				return (DataFetchersDelegateImpl) delegate;
		}
		fail("DataFetchersDelegateImpl '" + name + "' not found");
		return null;
	}

	private void checkField(ObjectType type, int j, String name, boolean list, boolean mandatory, Boolean itemMandatory,
			String typeName, String classname) {
		Field field = type.getFields().get(j);
		String fieldDescForJUnitMessage = "Field n°" + j + " (" + name + ")";

		assertEquals(name, field.getName(), "field name is " + name + " (for " + fieldDescForJUnitMessage + ")");
		assertEquals(list, field.isList(), "field list is " + list + " (for " + fieldDescForJUnitMessage + ")");
		assertEquals(mandatory, field.isMandatory(),
				"field mandatory is " + mandatory + " (for " + fieldDescForJUnitMessage + ")");
		if (itemMandatory != null) {
			assertEquals(itemMandatory, field.isItemMandatory(),
					"field itemMandatory is " + itemMandatory + " (for " + fieldDescForJUnitMessage + ")");
		}

		Type fieldType = field.getType();
		assertEquals(typeName, fieldType.getName(),
				"type name is " + typeName + " (for " + fieldDescForJUnitMessage + ")");
		assertEquals(classname, fieldType.getClassFullName(),
				"Class for field type is " + classname + " (for " + fieldDescForJUnitMessage + ")");
	}

	private void checkNbInputParameter(ObjectType type, int j, int nbInputParameters) {
		assertEquals(nbInputParameters, type.getFields().get(j).getInputParameters().size(),
				"field " + type.getFields().get(j).getName() + " should have " + nbInputParameters + " parameter");
	}

	private void checkInputParameter(ObjectType type, int j, int numParam, String name, boolean list, boolean mandatory,
			Boolean itemMandatory, String typeName, String classname, Value<?> defaultValue) {
		Field inputValue = type.getFields().get(j).getInputParameters().get(numParam);

		String intputParamDescForJUnitMessage = "Field n°" + j + " / input param n°" + numParam;

		assertEquals(name, inputValue.getName(),
				type.getName() + " - name is " + name + " (for " + intputParamDescForJUnitMessage + ")");
		assertEquals(list, inputValue.isList(),
				type.getName() + " - list is " + list + " (for " + intputParamDescForJUnitMessage + ")");
		assertEquals(mandatory, inputValue.isMandatory(),
				type.getName() + " - mandatory is " + mandatory + " (for " + intputParamDescForJUnitMessage + ")");
		if (itemMandatory != null) {
			assertEquals(itemMandatory, inputValue.isItemMandatory(), type.getName() + " - itemMandatory is "
					+ itemMandatory + " (for " + intputParamDescForJUnitMessage + ")");
		}

		Type fieldType = inputValue.getType();
		assertEquals(typeName, fieldType.getName(),
				"name is " + typeName + " (for " + intputParamDescForJUnitMessage + ")");
		assertEquals(classname, fieldType.getClassFullName(),
				"Class type is " + classname + " (for " + intputParamDescForJUnitMessage + ")");

		checkValue(defaultValue, inputValue.getDefaultValue(), intputParamDescForJUnitMessage);
	}

	private void checkValue(Value<?> defaultValue, Value<?> inputValue, String action) {
		if (defaultValue == null) {
			assertNull(inputValue, "Default Value is <" + defaultValue + "> (for " + action + ")");
		} else if (defaultValue instanceof StringValue) {
			assertEquals(((StringValue) defaultValue).getValue(), ((StringValue) inputValue).getValue(),
					"Default Value is <" + defaultValue + "> (for " + action + ")");
		} else if (defaultValue instanceof IntValue) {
			assertEquals(((IntValue) defaultValue).getValue(), ((IntValue) inputValue).getValue(),
					"Default Value is <" + defaultValue + "> (for " + action + ")");
		} else if (defaultValue instanceof graphql.language.EnumValue) {
			assertEquals(((graphql.language.EnumValue) defaultValue).getName(),
					((graphql.language.EnumValue) inputValue).getName(),
					"Default Value is <" + defaultValue + "> (for " + action + ")");
		} else if (defaultValue instanceof ArrayValue) {
			// Same number of items
			assertEquals(((ArrayValue) defaultValue).getValues().size(), ((ArrayValue) inputValue).getValues().size());
			// Check for each items. They must be in the same order.
			for (int i = 0; i < ((ArrayValue) inputValue).getValues().size(); i += 1) {
				checkValue(((ArrayValue) defaultValue).getValues().get(i), ((ArrayValue) inputValue).getValues().get(i),
						action + "-" + i);
			}
		} else if (defaultValue instanceof ObjectValue) {
			assertEquals(((ObjectValue) defaultValue).getObjectFields().size(),
					((ObjectValue) inputValue).getObjectFields().size(), "same number of fields");
			// Then check of each field
			for (ObjectField f1 : ((ObjectValue) defaultValue).getObjectFields()) {
				boolean found = false;
				for (ObjectField f2 : ((ObjectValue) inputValue).getObjectFields()) {
					if (f2.getName().contentEquals(f1.getName())) {
						found = true;
						checkValue(f1.getValue(), f2.getValue(), action + "-" + f1.getName());
						break;
					}
				} // for f2
				if (!found) {
					fail("Could not find the " + f1.getName() + " field while " + action);
				}
			} // for f1
		} else {
			fail(defaultValue.getClass().getName() + " is not managed in unit tests");
		}
	}
}
