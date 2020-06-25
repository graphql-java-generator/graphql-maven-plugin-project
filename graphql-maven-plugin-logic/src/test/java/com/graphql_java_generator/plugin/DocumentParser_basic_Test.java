package com.graphql_java_generator.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.graphql_java_generator.plugin.test.helper.GraphqlTestHelper;
import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;

import graphql.language.Document;
import graphql.mavenplugin_notscannedbyspring.Basic_Server_SpringConfiguration;
import graphql.parser.Parser;

/**
 * 
 * @author etienne-sf
 */
@SpringJUnitConfig(classes = { Basic_Server_SpringConfiguration.class })
class DocumentParser_basic_Test {

	@javax.annotation.Resource
	private ApplicationContext ctx;
	@javax.annotation.Resource
	private GraphqlTestHelper graphqlTestHelper;
	@javax.annotation.Resource
	GraphQLConfigurationTestHelper pluginConfiguration;

	@Autowired
	GraphQLDocumentParser documentParser;

	private Parser parser = new Parser();

	private Document doc;

	@BeforeEach
	void setUp() throws Exception {
		graphqlTestHelper.checkSchemaStringProvider("basic.graphqls");
	}

	@Test
	@DirtiesContext
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
