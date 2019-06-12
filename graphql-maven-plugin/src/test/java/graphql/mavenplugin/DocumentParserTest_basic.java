package graphql.mavenplugin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import graphql.language.Document;
import graphql.mavenplugin.test.helper.GraphqlTestHelper;
import graphql.mavenplugin_notscannedbyspring.Basic_Server_SpringConfiguration;
import graphql.parser.Parser;

/**
 * 
 * @author EtienneSF
 */
@SpringJUnitConfig(classes = { Basic_Server_SpringConfiguration.class })
class DocumentParserTest_basic {

	@Autowired
	private ApplicationContext ctx;
	@Autowired
	private GraphqlTestHelper graphqlTestHelper;
	@Autowired
	String packageName;

	@Autowired
	DocumentParser documentParser;

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
		int i = documentParser.parseOneDocument(doc);

		// Verification
		assertEquals(2, i);
	}
}
