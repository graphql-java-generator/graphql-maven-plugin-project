package graphql.mavenplugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
import graphql.mavenplugin.language.Relation;
import graphql.mavenplugin.language.RelationType;
import graphql.mavenplugin.test.helper.GraphqlTestHelper;
import graphql.mavenplugin_notscannedbyspring.AllGraphQLCases_Server_SpringConfiguration;
import graphql.parser.Parser;

/**
 * 
 * @author EtienneSF
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { AllGraphQLCases_Server_SpringConfiguration.class })
class DocumentParserTest_Forum {

	@Autowired
	private ApplicationContext ctx;
	@Autowired
	private GraphqlTestHelper graphqlTestHelper;
	@Autowired
	String basePackage;

	private DocumentParser documentParser;
	private Parser parser;

	private Document doc;

	@BeforeEach
	void setUp() throws Exception {
		documentParser = new DocumentParser();
		documentParser.basePackage = basePackage;
		documentParser.log = new SystemStreamLog();
		parser = new Parser();

		// By default, we parse the allGraphQLCases, as it contains all the cases managed by the plugin. It's the most
		// used in the latter unit tests.
		Resource resource = ctx.getResource("/allGraphQLCases.graphqls");
		doc = parser.parseDocument(graphqlTestHelper.readSchema(resource));
	}

	@Test
	void test_initRelations() {
		// Preparation
		assertNotNull(documentParser.relations, "relations initialized");
		assertEquals(0, documentParser.relations.size(), "relations initialized to an empty list");

		// Go, go, go
		documentParser.initRelations();

		// Verification
		assertEquals(4, documentParser.relations.size(), "nb relations found");

		// The Relation are found in the order of their declaratioon in the GraphQL schema definition.
		// Let's check that
		int i = 0;
		// checkRelation(documentParser.relations.get(i++), "Member", "type", RelationType.ManyToOne, true, null);
		checkRelation(documentParser.relations.get(i++), "Board", "topics", RelationType.OneToMany, true, null);
		checkRelation(documentParser.relations.get(i++), "Topic", "author", RelationType.ManyToOne, true, null);
		checkRelation(documentParser.relations.get(i++), "Topic", "posts", RelationType.OneToMany, true, null);
		checkRelation(documentParser.relations.get(i++), "Post", "author", RelationType.ManyToOne, true, null);
	}

	/**
	 * Checks one relation, with the given parameters
	 * 
	 * @param relation
	 * @param object
	 * @param field
	 * @param relationType
	 * @param ownerSide
	 * @param mappedyBy
	 */
	void checkRelation(Relation relation, String object, String field, RelationType relationType, boolean ownerSide,
			String mappedyBy) {
		String msg = object + "." + field;
		assertEquals(object, relation.getObjectType().getName(), msg + " [object]");
		assertEquals(field, relation.getField().getName(), msg + " [field]");
		assertEquals(relationType, relation.getRelationType(), msg + " [relationType]");
		assertEquals(ownerSide, relation.isOwnerSide(), msg + " [ownerSide]");

		if (mappedyBy == null) {
			assertNull(relation.getMappedyBy(), msg + " [mappedyBy]");
		} else {
			assertEquals(mappedyBy, relation.getMappedyBy().getName(), msg + " [mappedyBy]");
		}
	}
}
