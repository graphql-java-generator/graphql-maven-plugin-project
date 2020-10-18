package com.graphql_java_generator.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.Resource;

import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;
import com.graphql_java_generator.plugin.test.helper.GraphqlTestHelper;

import graphql.language.Document;
import graphql.mavenplugin_notscannedbyspring.Basic_Server_SpringConfiguration;
import graphql.parser.Parser;

/**
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
class DocumentParser_basic_Test {

	private ApplicationContext ctx;
	private GraphqlTestHelper graphqlTestHelper;
	GraphQLConfigurationTestHelper pluginConfiguration;
	GraphQLDocumentParser documentParser;

	private Parser parser = new Parser();

	private Document doc;

	@BeforeEach
	void loadApplicationContext() throws IOException {
		ctx = new AnnotationConfigApplicationContext(Basic_Server_SpringConfiguration.class);
		documentParser = ctx.getBean(GraphQLDocumentParser.class);
		pluginConfiguration = ctx.getBean(GraphQLConfigurationTestHelper.class);
		graphqlTestHelper = ctx.getBean(GraphqlTestHelper.class);

		graphqlTestHelper.checkSchemaStringProvider("basic.graphqls");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_parseOneDocument_basic() {
		// Preparation
		Resource resource = ctx.getResource("/basic.graphqls");
		doc = parser.parseDocument(graphqlTestHelper.readSchema(resource));

		// Go, go, go
		documentParser.parseOneDocument(doc);

		// Verification
		int nbClasses = (documentParser.queryType == null ? 0 : 1) + (documentParser.subscriptionType == null ? 0 : 1)
				+ (documentParser.mutationType == null ? 0 : 1) + documentParser.objectTypes.size()
				+ documentParser.enumTypes.size() + documentParser.interfaceTypes.size();
		assertEquals(2, nbClasses);
	}
}
