package graphql.mavenplugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import javax.annotation.Resource;

import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import graphql.mavenplugin.language.Relation;
import graphql.mavenplugin.language.RelationType;
import graphql.mavenplugin.test.helper.MavenTestHelper;
import graphql.mavenplugin_notscannedbyspring.Forum_Server_SpringConfiguration;

@ExtendWith(SpringExtension.class)
@SpringJUnitConfig(classes = { Forum_Server_SpringConfiguration.class })
class CodeGeneratorTest_Forum {

	@Resource
	String basePackage;
	@Resource
	Log log;
	@Resource
	MavenTestHelper mavenTestHelper;

	@javax.annotation.Resource
	protected DocumentParser documentParser;
	@javax.annotation.Resource
	protected CodeGenerator codeGenerator;

	@BeforeEach
	void setUp() throws Exception {
		documentParser.parseDocuments();
	}

	@Test
	void test_initRelations() {
		// Preparation
		assertNotNull(codeGenerator.relations, "relations initialized");
		assertEquals(0, codeGenerator.relations.size(), "relations initialized to an empty list");

		// Go, go, go
		codeGenerator.initRelations();

		// Verification
		assertEquals(4, codeGenerator.relations.size(), "nb relations found");

		// The Relation are found in the order of their declaratioon in the GraphQL schema definition.
		// Let's check that
		int i = 0;
		// checkRelation(codeGenerator.relations.get(i++), "Member", "type", RelationType.ManyToOne, true, null);
		checkRelation(codeGenerator.relations.get(i++), "Board", "topics", RelationType.OneToMany, true, null);
		checkRelation(codeGenerator.relations.get(i++), "Topic", "author", RelationType.ManyToOne, true, null);
		checkRelation(codeGenerator.relations.get(i++), "Topic", "posts", RelationType.OneToMany, true, null);
		checkRelation(codeGenerator.relations.get(i++), "Post", "author", RelationType.ManyToOne, true, null);
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
