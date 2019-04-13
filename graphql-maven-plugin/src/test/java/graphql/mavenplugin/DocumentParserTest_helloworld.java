package graphql.mavenplugin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

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

import graphql.language.Document;
import graphql.mavenplugin.test.helper.GraphqlTestHelper;
import graphql.mavenplugin_notscannedbyspring.HelloWorld_Server_SpringConfiguration;
import graphql.parser.Parser;

/**
 * 
 * @author EtienneSF
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { HelloWorld_Server_SpringConfiguration.class })
class DocumentParserTest_helloworld {

	@Autowired
	private ApplicationContext ctx;
	@Autowired
	private GraphqlTestHelper graphqlTestHelper;
	@Autowired
	String basePackage;

	private DocumentParser documentParser;
	private Parser parser;

	@BeforeEach
	void setUp() throws Exception {
		graphqlTestHelper.checkSchemaStringProvider("helloworld.graphqls");

		documentParser = new DocumentParser();
		documentParser.basePackage = basePackage;
		documentParser.log = new SystemStreamLog();
		parser = new Parser();
	}

	@Test
	void test_parseDocuments() throws MojoExecutionException {
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
		assertEquals(3, i, "3 classes expected");
	}

	@Test
	void test_parseOneDocument_helloworld() {
		// Preparation
		Resource resource = ctx.getResource("/helloworld.graphqls");
		Document doc = parser.parseDocument(graphqlTestHelper.readSchema(resource));

		// Go, go, go
		int i = documentParser.parseOneDocument(doc);

		// Verification
		assertEquals(1, i, "Two classes are generated");
	}

}
