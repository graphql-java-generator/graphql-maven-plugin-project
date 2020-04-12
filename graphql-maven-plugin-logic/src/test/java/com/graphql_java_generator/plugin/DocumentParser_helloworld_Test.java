package com.graphql_java_generator.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.graphql_java_generator.plugin.test.helper.GraphqlTestHelper;

import graphql.language.Document;
import graphql.mavenplugin_notscannedbyspring.HelloWorld_Server_SpringConfiguration;
import graphql.parser.Parser;

/**
 * 
 * @author etienne-sf
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { HelloWorld_Server_SpringConfiguration.class })
class DocumentParser_helloworld_Test {

	@Autowired
	private ApplicationContext ctx;
	@Autowired
	private GraphqlTestHelper graphqlTestHelper;
	@javax.annotation.Resource
	PluginConfiguration pluginConfiguration;

	@Autowired
	DocumentParser documentParser;

	private Parser parser = new Parser();

	@BeforeEach
	void setUp() throws Exception {
		graphqlTestHelper.checkSchemaStringProvider("helloworld.graphqls");
	}

	@Test
	@DirtiesContext
	void test_parseDocuments() {
		// Preparation
		Document basic = parser.parseDocument(graphqlTestHelper.readSchema(ctx.getResource("/helloworld.graphqls")));
		Document helloWorld = parser
				.parseDocument(graphqlTestHelper.readSchema(ctx.getResource("/helloworld.graphqls")));
		documentParser.documents = new ArrayList<Document>();
		documentParser.documents.add(basic);
		documentParser.documents.add(helloWorld);

		// Go, go, go
		int i = documentParser.parseDocuments();

		// Verification
		assertEquals(4, i, "3 classes expected (basic + helloworld)");
	}

	@Test
	@DirtiesContext
	void test_parseOneDocument_helloworld() {
		// Preparation
		Resource resource = ctx.getResource("/helloworld.graphqls");
		Document doc = parser.parseDocument(graphqlTestHelper.readSchema(resource));

		// Go, go, go
		documentParser.parseOneDocument(doc);

		// Verification
		int nbClasses = documentParser.queryTypes.size() + documentParser.subscriptionTypes.size()
				+ documentParser.mutationTypes.size() + documentParser.objectTypes.size()
				+ documentParser.enumTypes.size() + documentParser.interfaceTypes.size();
		assertEquals(2, nbClasses, "Two classes ares generated (the query and the object for the query)");
	}

}
