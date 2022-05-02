package com.graphql_java_generator.plugin.schema_personalization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.graphql_java_generator.plugin.DocumentParser;
import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;
import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.impl.ObjectType;
import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;
import com.graphql_java_generator.plugin.test.helper.MavenTestHelper;

import graphql.mavenplugin_notscannedbyspring.Forum_Server_SpringConfiguration;

@Execution(ExecutionMode.CONCURRENT)
class JsonSchemaPersonalization_Forum_Server_Test {

	AbstractApplicationContext ctx = null;
	GraphQLConfiguration pluginConfiguration;
	GenerateCodeJsonSchemaPersonalization jsonSchemaPersonalization;
	DocumentParser documentParser;

	@Resource
	MavenTestHelper mavenTestHelper;

	File userJsonFile;

	@BeforeEach
	void loadApplicationContext() {
		ctx = new AnnotationConfigApplicationContext(Forum_Server_SpringConfiguration.class);
		documentParser = ctx.getBean(DocumentParser.class);
		jsonSchemaPersonalization = ctx.getBean(GenerateCodeJsonSchemaPersonalization.class);
		mavenTestHelper = ctx.getBean(MavenTestHelper.class);
		pluginConfiguration = ctx.getBean(GraphQLConfiguration.class);
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
				mavenTestHelper.getModulePathFile(), "src/test/resources/schema_personalization/forum_OK.json");

		jsonSchemaPersonalization.configuration = pluginConfiguration;

		// Go, go, go
		documentParser.parseGraphQLSchemas();

		// Verification
		ObjectType member = jsonSchemaPersonalization.findObjectTypeFromName("Member");
		assertEquals("@Entity\n@GraphQLObjectType(\"Member\")\n@MyAdditionalAnnotation", member.getAnnotation(),
				"member annotation");
		//
		Field age = jsonSchemaPersonalization.findFieldFromName(member, "age");
		assertEquals("int", age.getGraphQLTypeSimpleName(), "age type");
		assertTrue(age.isId(), "age id");
		assertEquals(0, age.getFieldTypeAST().getListDepth(), "age list");
		assertFalse(age.getFieldTypeAST().isMandatory(), "age mandatory");
		assertEquals("@Annotation1", age.getAnnotation(), "age annotation");
		//
		Field age2 = jsonSchemaPersonalization.findFieldFromName(member, "age2");
		assertEquals("int", age2.getGraphQLTypeSimpleName(), "age2 type");
		assertFalse(age2.isId(), "age2 id");
		assertEquals(1, age2.getFieldTypeAST().getListDepth(), "age2 list");
		assertTrue(age2.getFieldTypeAST().isMandatory(), "age2 mandatory");
		assertEquals("@Annotation1", age2.getAnnotation(), "age2 annotation");

		ObjectType board = jsonSchemaPersonalization.findObjectTypeFromName("Board");
		assertEquals("@com.me.MyReplacementAnnotation", board.getAnnotation(), "board annotation");
		//
		Field id = jsonSchemaPersonalization.findFieldFromName(board, "id");
		assertEquals("@NotId\n\t@AnotherAnnotation", id.getAnnotation(), "board.id annotation");
		//
		Field name = jsonSchemaPersonalization.findFieldFromName(board, "name");
		assertEquals(
				"@GraphQLScalar(fieldName = \"name\", graphQLTypeSimpleName = \"String\", javaClass = java.lang.String.class)\n\t@Column(name=\"column_name\")",
				name.getAnnotation(), "board.name annotation");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testApplySchemaPersonalization_wrongEntityName() {
		// Preparation
		((GraphQLConfigurationTestHelper) pluginConfiguration).schemaPersonalizationFile = new File(
				mavenTestHelper.getModulePathFile(),
				"src/test/resources/schema_personalization/forum_KO_WrongEntity.json");

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
				"src/test/resources/schema_personalization/forum_KO_WrongFieldName.json");

		// Go, go, go
		RuntimeException e = assertThrows(RuntimeException.class, () -> documentParser.parseGraphQLSchemas());
		assertTrue(e.getMessage().contains("this field does not exist"),
				"expected the wrong field name in: " + e.getMessage());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testApplySchemaPersonalization_fieldNameAlreadyExist() {
		// Preparation
		((GraphQLConfigurationTestHelper) pluginConfiguration).schemaPersonalizationFile = new File(
				mavenTestHelper.getModulePathFile(),
				"src/test/resources/schema_personalization/forum_KO_FieldNameAlreadyExist.json");

		// Go, go, go
		RuntimeException e = assertThrows(RuntimeException.class, () -> documentParser.parseGraphQLSchemas());
		assertTrue(e.getMessage().contains("email"), "expected the wrong field name in: " + e.getMessage());
	}

}
