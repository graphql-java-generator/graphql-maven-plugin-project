package graphql.mavenplugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import graphql.mavenplugin.language.DataFetcher;
import graphql.mavenplugin.language.Field;
import graphql.mavenplugin.language.Relation;
import graphql.mavenplugin.language.RelationType;
import graphql.mavenplugin.language.Type;
import graphql.mavenplugin_notscannedbyspring.Forum_Server_SpringConfiguration;

/**
 * 
 * @author EtienneSF
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { Forum_Server_SpringConfiguration.class })
class DocumentParserTest_Forum_Server {

	@Autowired
	DocumentParser documentParser;

	@BeforeEach
	void setUp() throws Exception {
		documentParser.parseDocuments();
	}

	@Test
	@DirtiesContext
	void test_initRelations() {

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

	/** Tests the annotation. We're in Server mode, thanks to the Spring Configuration used for this test */
	@Test
	@DirtiesContext
	void test_addAnnotations_server() {
		// Preparation
		Type topic = documentParser.objectTypes.stream().filter(o -> o.getName().equals("Topic")).findFirst().get();

		// Verification
		assertEquals("@Entity", topic.getAnnotation(), "Entity annotation");
		int i = 0;
		checkFieldAnnotation(topic.getFields().get(i++), "id", "@Id\n	@GeneratedValue");
		checkFieldAnnotation(topic.getFields().get(i++), "date", "");
		checkFieldAnnotation(topic.getFields().get(i++), "author", "@Transient");
		checkFieldAnnotation(topic.getFields().get(i++), "publiclyAvailable", "");
		checkFieldAnnotation(topic.getFields().get(i++), "nbPosts", "");
		checkFieldAnnotation(topic.getFields().get(i++), "title", "");
		checkFieldAnnotation(topic.getFields().get(i++), "content", "");
		checkFieldAnnotation(topic.getFields().get(i++), "posts", "@Transient");
	}

	private void checkFieldAnnotation(Field field, String name, String annotation) {
		String msg = "Check annotation for field " + field.getName() + " (server mode)";
		assertEquals(name, field.getName(), msg + " [name]");
		assertEquals(annotation, field.getAnnotation(), msg + " [annotation]");
	}

	/** Tests the Data Fetchers that are listed during parsing */
	@Test
	@DirtiesContext
	void test_initDataFetchers() {
		assertEquals(9, documentParser.dataFetchers.size(), "nb of data fetchers in server mode");

		int i = 0;
		//
		// Verification of the data fetchers
		//
		// dataFetcher, dataFetcherName, owningType, fieldName, returnedTypeName, list, list of input parameters
		checkDataFetcher(documentParser.dataFetchers.get(i++), "QueryTypeBoards", "QueryType", "boards", "Board", true,
				null);
		checkDataFetcher(documentParser.dataFetchers.get(i++), "QueryTypeTopics", "QueryType", "topics", "Topic", true,
				null, "boardName");

		checkDataFetcher(documentParser.dataFetchers.get(i++), "MutationTypeCreateBoard", "MutationType", "createBoard",
				"Board", false, null, "name", "publiclyAvailable");
		checkDataFetcher(documentParser.dataFetchers.get(i++), "MutationTypeCreateTopic", "MutationType", "createTopic",
				"Topic", false, null, "authorId", "publiclyAvailable", "title", "content");
		checkDataFetcher(documentParser.dataFetchers.get(i++), "MutationTypeCreatePost", "MutationType", "createPost",
				"Post", false, null, "authorId", "publiclyAvailable", "title", "content");

		checkDataFetcher(documentParser.dataFetchers.get(i++), "BoardTopics", "Board", "topics", "Topic", true, "Board",
				"since");
		checkDataFetcher(documentParser.dataFetchers.get(i++), "TopicAuthor", "Topic", "author", "Member", false,
				"Topic");
		checkDataFetcher(documentParser.dataFetchers.get(i++), "TopicPosts", "Topic", "posts", "Post", true, "Topic",
				"since");
		checkDataFetcher(documentParser.dataFetchers.get(i++), "PostAuthor", "Post", "author", "Member", false, "Post");

		//
		// Verification of the data fetchers delegates
		//
		assertEquals(5, documentParser.dataFetcherDelegates.size(), "data fetchers delegates");
		i = 0;
		int j = 0;

		// Delegate for QueryType
		assertEquals("QueryTypeDataFetchersDelegate", documentParser.dataFetcherDelegates.get(i).getName(),
				"delegate name " + i);
		assertEquals(2, documentParser.dataFetcherDelegates.get(i).getDataFetchers().size(),
				"nb DataFetcher for delegate " + i);
		assertEquals("QueryTypeBoards", documentParser.dataFetcherDelegates.get(i).getDataFetchers().get(0).getName(),
				"Name of DataFetcher " + j++ + " for delegate " + i);
		assertEquals("QueryTypeTopics", documentParser.dataFetcherDelegates.get(i).getDataFetchers().get(1).getName(),
				"Name of DataFetcher " + j++ + " for delegate " + i);
		//
		// Delegate for Board
		i += 2;
		j = 0;
		assertEquals("BoardDataFetchersDelegate", documentParser.dataFetcherDelegates.get(i).getName(),
				"delegate name " + i);
		assertEquals(1, documentParser.dataFetcherDelegates.get(i).getDataFetchers().size(),
				"nb DataFetcher for delegate " + i);
		assertEquals("BoardTopics", documentParser.dataFetcherDelegates.get(i).getDataFetchers().get(0).getName(),
				"Name of DataFetcher " + j++ + " for delegate " + i);
		//
		// Delegate for Topic
		i += 1;
		j = 0;
		assertEquals("TopicDataFetchersDelegate", documentParser.dataFetcherDelegates.get(i).getName(),
				"delegate name " + i);
		assertEquals(2, documentParser.dataFetcherDelegates.get(i).getDataFetchers().size(),
				"nb DataFetcher for delegate " + i);
		assertEquals("TopicAuthor", documentParser.dataFetcherDelegates.get(i).getDataFetchers().get(0).getName(),
				"Name of DataFetcher " + j++ + " for delegate " + i);
		assertEquals("TopicPosts", documentParser.dataFetcherDelegates.get(i).getDataFetchers().get(1).getName(),
				"Name of DataFetcher " + j++ + " for delegate " + i);
		//
		// Delegate for Board
		i += 1;
		j = 0;
		assertEquals("PostDataFetchersDelegate", documentParser.dataFetcherDelegates.get(i).getName(),
				"delegate name " + i);
		assertEquals(1, documentParser.dataFetcherDelegates.get(i).getDataFetchers().size(),
				"nb DataFetcher for delegate " + i);
		assertEquals("PostAuthor", documentParser.dataFetcherDelegates.get(i).getDataFetchers().get(0).getName(),
				"Name of DataFetcher " + j++ + " for delegate " + i);

	}

	private void checkDataFetcher(DataFetcher dataFetcher, String dataFetcherName, String owningType, String fieldName,
			String returnedTypeName, boolean list, String sourceName, String... inputParameters) {
		assertEquals(dataFetcherName, dataFetcher.getName(), "dataFetcherName");
		assertEquals(owningType, dataFetcher.getField().getOwningType().getName(), "owningType");
		assertEquals(returnedTypeName, dataFetcher.getField().getType().getName(), "returnedTypeName");
		assertEquals(list, dataFetcher.getField().isList(), "list");
		assertEquals(fieldName, dataFetcher.getField().getName(), "fieldName");
		assertEquals(sourceName, dataFetcher.getSourceName(), "sourceName");

		// Check of the data fetcher input parameters
		assertEquals(inputParameters.length, dataFetcher.getField().getInputParameters().size(),
				"Nb input parameters for Data Fetcher " + dataFetcherName);
		int i = 0;
		for (String inputParamName : inputParameters) {
			assertEquals(inputParamName, dataFetcher.getField().getInputParameters().get(i).getName(),
					"param " + i + " for Data Fetcher " + dataFetcherName);
			i += 1;
		}
	}

	@Test
	@DirtiesContext
	void test_initListOfImplementations() {
		assertEquals(0, documentParser.interfaceTypes.size(), "No interface");
	}
}
