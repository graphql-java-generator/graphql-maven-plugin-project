package com.graphql_java_generator.plugin.generate_code;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;
import com.graphql_java_generator.plugin.test.helper.GraphqlTestHelper;

import graphql.mavenplugin_notscannedbyspring.Basic_Server_SpringConfiguration;

/**
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
class DocumentParser_basic_Test {

	private ApplicationContext ctx;
	private GraphqlTestHelper graphqlTestHelper;
	GraphQLConfigurationTestHelper pluginConfiguration;
	GenerateCodeDocumentParser documentParser;

	@BeforeEach
	void loadApplicationContext() throws IOException {
		ctx = new AnnotationConfigApplicationContext(Basic_Server_SpringConfiguration.class);
		documentParser = ctx.getBean(GenerateCodeDocumentParser.class);
		pluginConfiguration = ctx.getBean(GraphQLConfigurationTestHelper.class);
		graphqlTestHelper = ctx.getBean(GraphqlTestHelper.class);

		graphqlTestHelper.checkSchemaStringProvider("basic.graphqls");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_parseOneDocument_basic() throws IOException {
		// Preparation

		// Go, go, go
		documentParser.parseGraphQLSchemas();

		// Verification
		int nbClasses = (documentParser.getQueryType() == null ? 0 : 1)
				+ (documentParser.getSubscriptionType() == null ? 0 : 1)
				+ (documentParser.getMutationType() == null ? 0 : 1) + documentParser.getObjectTypes().size()
				+ documentParser.getEnumTypes().size() + documentParser.getInterfaceTypes().size();
		assertEquals(2, nbClasses);
	}
}
