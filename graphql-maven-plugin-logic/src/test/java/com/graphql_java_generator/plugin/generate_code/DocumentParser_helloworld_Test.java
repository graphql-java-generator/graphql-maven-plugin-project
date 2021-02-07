package com.graphql_java_generator.plugin.generate_code;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.Resource;

import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;
import com.graphql_java_generator.plugin.test.helper.GraphqlTestHelper;

import graphql.language.Document;
import graphql.mavenplugin_notscannedbyspring.HelloWorld_Server_SpringConfiguration;
import graphql.parser.Parser;

/**
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
class DocumentParser_helloworld_Test {

	AbstractApplicationContext ctx;
	GraphqlTestHelper graphqlTestHelper;
	GraphQLConfiguration pluginConfiguration;
	GenerateCodeDocumentParser documentParser;

	private Parser parser = new Parser();

	@BeforeEach
	void loadApplicationContext() {
		ctx = new AnnotationConfigApplicationContext(HelloWorld_Server_SpringConfiguration.class);
		documentParser = ctx.getBean(GenerateCodeDocumentParser.class);
		graphqlTestHelper = ctx.getBean(GraphqlTestHelper.class);
		pluginConfiguration = ctx.getBean(GraphQLConfiguration.class);

		graphqlTestHelper.checkSchemaStringProvider("helloworld.graphqls");
	}

	@AfterEach
	void cleanUp() {
		if (ctx != null) {
			ctx.close();
		}
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_parseOneDocument_helloworld() {
		// Preparation
		Resource resource = ctx.getResource("/helloworld.graphqls");
		Document doc = parser.parseDocument(graphqlTestHelper.readSchema(resource));

		// Go, go, go
		documentParser.parseOneDocument(doc);

		// Verification
		int nbClasses = (documentParser.getQueryType() == null ? 0 : 1)
				+ (documentParser.getSubscriptionType() == null ? 0 : 1)
				+ (documentParser.getMutationType() == null ? 0 : 1) + documentParser.getObjectTypes().size()
				+ documentParser.getEnumTypes().size() + documentParser.getInterfaceTypes().size();
		assertEquals(2, nbClasses, "Two classes ares generated (the query and the object for the query)");
	}

}
