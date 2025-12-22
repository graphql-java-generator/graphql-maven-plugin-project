package com.graphql_java_generator.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.graphql_java_generator.plugin.test.helper.GraphqlTestHelper;
import com.graphql_java_generator.plugin.test.helper.MavenTestHelper;

import graphql.mavenplugin_notscannedbyspring.MavenResourceSchemaStringProviderTest_Server_SpringConfiguration;

@Execution(ExecutionMode.CONCURRENT)
class MavenResourceSchemaStringProviderTest {

	AbstractApplicationContext ctx = null;
	private ResourceSchemaStringProvider resourceSchemaStringProvider;
	protected GraphqlTestHelper graphqlTestHelper;
	protected MavenTestHelper mavenTestHelper;

	@BeforeEach
	void loadApplicationContext() throws IOException {
		ctx = new AnnotationConfigApplicationContext(
				MavenResourceSchemaStringProviderTest_Server_SpringConfiguration.class);
		mavenTestHelper = ctx.getBean(MavenTestHelper.class);
		graphqlTestHelper = ctx.getBean(GraphqlTestHelper.class);
		resourceSchemaStringProvider = ctx.getBean(ResourceSchemaStringProvider.class);

		graphqlTestHelper.checkSchemaStringProvider("MavenResourceSchemaStringProviderTest/*.graphqls");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	public void testSchemaStrings() throws IOException {
		// Preparation

		// Go, go, go
		List<String> strings = resourceSchemaStringProvider.schemaStrings();

		// Verification
		assertEquals(2, strings.size(), "Nb schemas found");
		assertEquals(
				mavenTestHelper.readFile("/src/test/resources/MavenResourceSchemaStringProviderTest/file0.graphqls"),
				strings.get(0), "First file content");
		assertEquals(
				mavenTestHelper.readFile("/src/test/resources/MavenResourceSchemaStringProviderTest/file1.graphqls"),
				strings.get(1), "First file content");
	}

}
