package com.graphql_java_generator.plugin.schema_personalization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.graphql_java_generator.plugin.DocumentParser;
import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;
import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.impl.AbstractType;
import com.graphql_java_generator.plugin.language.impl.EnumType;
import com.graphql_java_generator.plugin.language.impl.InterfaceType;
import com.graphql_java_generator.plugin.language.impl.ObjectType;
import com.graphql_java_generator.plugin.language.impl.UnionType;
import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;
import com.graphql_java_generator.plugin.test.helper.MavenTestHelper;

import graphql.mavenplugin_notscannedbyspring.AllGraphQLCases_Server_SpringConfiguration;

@Execution(ExecutionMode.CONCURRENT)
class JsonSchemaPersonalization_allGraphQL_Server_Test {

	AbstractApplicationContext ctx = null;
	GraphQLConfiguration pluginConfiguration;
	GenerateCodeJsonSchemaPersonalization jsonSchemaPersonalization;
	DocumentParser documentParser;

	@Autowired
	MavenTestHelper mavenTestHelper;

	File userJsonFile;

	@BeforeEach
	void loadApplicationContext() {
		ctx = new AnnotationConfigApplicationContext(AllGraphQLCases_Server_SpringConfiguration.class);
		documentParser = ctx.getBean(DocumentParser.class);
		jsonSchemaPersonalization = ctx.getBean(GenerateCodeJsonSchemaPersonalization.class);
		mavenTestHelper = ctx.getBean(MavenTestHelper.class);
		pluginConfiguration = ctx.getBean(GraphQLConfiguration.class);
		((GraphQLConfigurationTestHelper) pluginConfiguration).schemaFileFolder = new File(
				mavenTestHelper.getModulePathFile(),
				"../graphql-maven-plugin-samples/graphql-maven-plugin-samples-allGraphQLCases-client/src/graphqls/allGraphQLCases");
	}

	@AfterEach
	void cleanUp() {
		if (ctx != null) {
			ctx.close();
		}
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testApplySchemaPersonalization_OK() throws IOException {
		// Preparation
		((GraphQLConfigurationTestHelper) pluginConfiguration).schemaPersonalizationFile = new File(
				mavenTestHelper.getModulePathFile(),
				"src/test/resources/schema_personalization/allGraphQLCases_OK.json");

		jsonSchemaPersonalization.configuration = pluginConfiguration;

		// Go, go, go
		documentParser.parseGraphQLSchemas();

		// Verification

		//////////////////////////////////////////////////////////////////////////////////////////////
		// Human
		ObjectType human = (ObjectType) jsonSchemaPersonalization.findGraphQLTypesFromName("Human").get(0);
		assertEquals("@Entity\n@GraphQLObjectType(\"Human\")\n@MyAdditionalAnnotation", human.getAnnotation(),
				"Huma, annotation");
		//
		Set<String> interfaces = human.getAdditionalInterfaces();
		assertEquals(2, interfaces.size());
		assertTrue(interfaces.contains("interface1"));
		assertTrue(interfaces.contains("interface2"));
		//
		Field age = jsonSchemaPersonalization.findFieldFromName(human, "age");
		assertEquals("Int", age.getGraphQLTypeSimpleName(), "age type");
		assertTrue(age.isId(), "age id");
		assertEquals(0, age.getFieldTypeAST().getListDepth(), "age list");
		assertFalse(age.getFieldTypeAST().isMandatory(), "age mandatory");
		assertEquals("@Annotation1", age.getAnnotation(), "age annotation");
		//
		Field age2 = jsonSchemaPersonalization.findFieldFromName(human, "age2");
		assertEquals("Int", age2.getGraphQLTypeSimpleName(), "age2 type");
		assertFalse(age2.isId(), "age2 id");
		assertEquals(1, age2.getFieldTypeAST().getListDepth(), "age2 list");
		assertTrue(age2.getFieldTypeAST().isMandatory(), "age2 mandatory");
		assertEquals("@Annotation1", age2.getAnnotation(), "age2 annotation");

		//////////////////////////////////////////////////////////////////////////////////////////////
		// Droid
		ObjectType droid = (ObjectType) jsonSchemaPersonalization.findGraphQLTypesFromName("Droid").get(0);
		assertEquals("@Entity\n@GraphQLObjectType(\"Droid\")\n@com.me.MyReplacementAnnotation", droid.getAnnotation(),
				"Droid annotation");
		//
		assertEquals(0, droid.getAdditionalInterfaces().size());
		//
		Field id = jsonSchemaPersonalization.findFieldFromName(droid, "id");
		assertEquals("@Id\n"//
				+ "\t@GeneratedValue\n"//
				+ "\t@GraphQLScalar( fieldName = \"id\", graphQLTypeSimpleName = \"ID\", javaClass = java.util.UUID.class, listDepth = 0)\n"
				+ "\t@NotId\n" //
				+ "\t@AnotherAnnotation", id.getAnnotation(), "droid.id annotation");
		//
		Field name = jsonSchemaPersonalization.findFieldFromName(droid, "name");
		assertEquals(
				"@GraphQLScalar( fieldName = \"name\", graphQLTypeSimpleName = \"String\", javaClass = java.lang.String.class, listDepth = 0)\n\t@Column(name=\"column_name\")",
				name.getAnnotation(), "droid.name annotation");

		//////////////////////////////////////////////////////////////////////////////////////////////
		// DroidInput
		ObjectType droidInput = (ObjectType) jsonSchemaPersonalization.findGraphQLTypesFromName("DroidInput").get(0);
		assertEquals(1, droidInput.getAdditionalInterfaces().size());
		assertEquals("interface3", droidInput.getAdditionalInterfaces().stream().findFirst().get());

		//////////////////////////////////////////////////////////////////////////////////////////////
		// extends
		EnumType extendsEnum = (EnumType) jsonSchemaPersonalization.findGraphQLTypesFromName("extends").get(0);
		assertEquals("@com.me.MyReplacementAnnotation", extendsEnum.getAnnotation(), "extends annotation");
		//
		interfaces = extendsEnum.getAdditionalInterfaces();
		assertEquals(1, interfaces.size());
		assertEquals("interface4", interfaces.stream().findFirst().get());

		//////////////////////////////////////////////////////////////////////////////////////////////
		// AnyCharacter
		UnionType anyCharacter = (UnionType) jsonSchemaPersonalization.findGraphQLTypesFromName("AnyCharacter").get(0);
		assertEquals("@com.me.MyReplacementAnnotation", anyCharacter.getAnnotation(), "AnyCharacter annotation");
		//
		interfaces = anyCharacter.getAdditionalInterfaces();
		assertEquals(1, interfaces.size());
		assertEquals("interface5", interfaces.stream().findFirst().get());

		//////////////////////////////////////////////////////////////////////////////////////////////
		// Character
		InterfaceType character = (InterfaceType) jsonSchemaPersonalization.findGraphQLTypesFromName("Character")
				.get(0);
		assertEquals("@GraphQLInterfaceType(\"Character\")\n@com.me.MyReplacementAnnotation", character.getAnnotation(),
				"Huma, annotation");
		//
		interfaces = character.getAdditionalInterfaces();
		assertEquals(1, interfaces.size());
		assertEquals("interface6", interfaces.stream().findFirst().get());
		//
		age = jsonSchemaPersonalization.findFieldFromName(character, "age");
		assertEquals("Int", age.getGraphQLTypeSimpleName(), "age type");
		assertTrue(age.isId(), "age id");
		assertEquals(0, age.getFieldTypeAST().getListDepth(), "age list");
		assertFalse(age.getFieldTypeAST().isMandatory(), "age mandatory");
		assertEquals("@Annotation1", age.getAnnotation(), "age annotation");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testApplySchemaPersonalization_wrongEntityName() {
		// Preparation
		((GraphQLConfigurationTestHelper) pluginConfiguration).schemaPersonalizationFile = new File(
				mavenTestHelper.getModulePathFile(),
				"src/test/resources/schema_personalization/allGraphQLCases_KO_WrongEntity.json");

		// Go, go, go
		RuntimeException e = assertThrows(RuntimeException.class, () -> documentParser.parseGraphQLSchemas());
		assertTrue(e.getMessage().contains("This entity does not exist"),
				"expected the wrong entity name in: " + e.getMessage());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testApplySchemaPersonalization_wrongFieldName() {
		// Preparation
		((GraphQLConfigurationTestHelper) pluginConfiguration).schemaPersonalizationFile = new File(
				mavenTestHelper.getModulePathFile(),
				"src/test/resources/schema_personalization/allGraphQLCases_KO_WrongFieldName.json");

		// Go, go, go
		RuntimeException e = assertThrows(RuntimeException.class, () -> documentParser.parseGraphQLSchemas());
		assertTrue(e.getMessage().contains("this field does not exist"),
				"expected the wrong field name in: " + e.getMessage());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testApplySchemaPersonalization_BadFieldName() {
		// Preparation
		((GraphQLConfigurationTestHelper) pluginConfiguration).schemaPersonalizationFile = new File(
				mavenTestHelper.getModulePathFile(),
				"src/test/resources/schema_personalization/allGraphQLCases_KO_BadFieldName.json");

		// Go, go, go
		RuntimeException e = assertThrows(RuntimeException.class, () -> documentParser.parseGraphQLSchemas());
		assertTrue(e.getMessage().contains("name with a space"), "expected the bad field name in: " + e.getMessage());
		assertTrue(
				e.getMessage()
						.contains("a field name must start by a letter, and may contain only letters and figures"),
				"expected the bad field name in: " + e.getMessage());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testApplySchemaPersonalization_wrongFieldType() {
		// Preparation
		((GraphQLConfigurationTestHelper) pluginConfiguration).schemaPersonalizationFile = new File(
				mavenTestHelper.getModulePathFile(),
				"src/test/resources/schema_personalization/allGraphQLCases_KO_WrongFieldType.json");

		// Go, go, go
		RuntimeException e = assertThrows(RuntimeException.class, () -> documentParser.parseGraphQLSchemas());
		assertTrue(e.getMessage().contains("'age2'"), "expected the wrong field name in: " + e.getMessage());
		assertTrue(e.getMessage().contains("'int'"), "expected the wrong field type in: " + e.getMessage());
		assertTrue(
				e.getMessage().contains(
						"unknown type (not a standard GraphQL type, nor a type defined in the GraphQL schema)"),
				"expected the wrong field type in: " + e.getMessage());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testApplySchemaPersonalization_fieldNameAlreadyExist() {
		// Preparation
		((GraphQLConfigurationTestHelper) pluginConfiguration).schemaPersonalizationFile = new File(
				mavenTestHelper.getModulePathFile(),
				"src/test/resources/schema_personalization/allGraphQLCases_KO_FieldNameAlreadyExist.json");

		// Go, go, go
		RuntimeException e = assertThrows(RuntimeException.class, () -> documentParser.parseGraphQLSchemas());
		assertTrue(e.getMessage().contains("listWithoutIdSubTypes"),
				"expected the wrong field name in: " + e.getMessage());
	}

	@Test
	void test_findGraphQLTypesFromName() throws IOException {
		RuntimeException re;
		List<AbstractType> types;

		// Preparation
		// ((GraphQLConfigurationTestHelper) pluginConfiguration).schemaPersonalizationFile = new File(
		// mavenTestHelper.getModulePathFile(),
		// "src/test/resources/schema_personalization/allGraphQLCases_OK.json");

		jsonSchemaPersonalization.configuration = pluginConfiguration;

		// Go, go, go
		documentParser.parseGraphQLSchemas();

		// Verifications
		assertThrows(NullPointerException.class, () -> jsonSchemaPersonalization.findGraphQLTypesFromName(null));

		re = assertThrows(RuntimeException.class,
				() -> jsonSchemaPersonalization.findGraphQLTypesFromName("[Bad category]"));
		assertTrue(re.getMessage().contains("[Bad category]"),
				"The message must contain the erroneus category, but is: " + re.getMessage());
		assertTrue(re.getMessage().contains("this type of personalization has not been recognized"),
				"The message must be the good one, but is: " + re.getMessage());

		re = assertThrows(RuntimeException.class,
				() -> jsonSchemaPersonalization.findGraphQLTypesFromName("Doesn't exist"));
		assertTrue(re.getMessage().contains("Doesn't exist"), "The message must contain the erroneus name");
		assertTrue(re.getMessage().contains("no item in the GraphQL schema matched the provided name"),
				"The message must be the good one, but is: " + re.getMessage());

		assertTrue(jsonSchemaPersonalization.findGraphQLTypesFromName("[input types]").size() > 0);
		jsonSchemaPersonalization.findGraphQLTypesFromName("[input types]").stream()//
				.forEach(t -> {
					assertTrue(t instanceof ObjectType, t.getName() + " should be an ObjectType");
					assertTrue(t.isInputType(), t.getName() + " should be an input type");
				});

		assertTrue(jsonSchemaPersonalization.findGraphQLTypesFromName("[interfaces]").size() > 0);
		jsonSchemaPersonalization.findGraphQLTypesFromName("[interfaces]").stream()
				.forEach(t -> assertTrue(t instanceof InterfaceType, t.getName() + " should be an InterfaceType"));

		assertTrue(jsonSchemaPersonalization.findGraphQLTypesFromName("[types]").size() > 0);
		jsonSchemaPersonalization.findGraphQLTypesFromName("[types]").stream()//
				.forEach(t -> {
					assertTrue(t instanceof ObjectType, t.getName() + " should be an ObjectType");
					assertFalse(t.isInputType(), t.getName() + " should not be an input type");
				});

		assertTrue(jsonSchemaPersonalization.findGraphQLTypesFromName("[unions]").size() > 0);
		jsonSchemaPersonalization.findGraphQLTypesFromName("[unions]").stream()
				.forEach(t -> assertTrue(t instanceof UnionType, t.getName() + " should be an UnionType"));

		types = jsonSchemaPersonalization.findGraphQLTypesFromName("Human");
		assertEquals(1, types.size());
		assertEquals("Human", types.get(0).getName());

		types = jsonSchemaPersonalization.findGraphQLTypesFromName("AllFieldCasesInput");
		assertEquals(1, types.size());
		assertEquals("AllFieldCasesInput", types.get(0).getName());

		types = jsonSchemaPersonalization.findGraphQLTypesFromName("WithID");
		assertEquals(1, types.size());
		assertEquals("WithID", types.get(0).getName());

		types = jsonSchemaPersonalization.findGraphQLTypesFromName("Unit");
		assertEquals(1, types.size());
		assertEquals("Unit", types.get(0).getName());

		types = jsonSchemaPersonalization.findGraphQLTypesFromName("AnyCharacter");
		assertEquals(1, types.size());
		assertEquals("AnyCharacter", types.get(0).getName());
	}

	@Test
	void checkDoubleInterface() throws IOException {
		// Preparation
		((GraphQLConfigurationTestHelper) pluginConfiguration).schemaPersonalizationFile = new File(
				mavenTestHelper.getModulePathFile(),
				"src/test/resources/schema_personalization/allGraphQLCases_OK.json");

		jsonSchemaPersonalization.configuration = pluginConfiguration;

		// Go, go, go
		documentParser.parseGraphQLSchemas();

		// Verification
		ObjectType human = (ObjectType) documentParser.getType("Human");
		Set<String> interfaces = human.getAdditionalInterfaces();
		assertEquals(2, interfaces.size());
		assertTrue(interfaces.contains("interface1"));
		assertTrue(interfaces.contains("interface2"));
	}

}
