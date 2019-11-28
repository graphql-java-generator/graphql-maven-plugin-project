package com.graphql_java_generator.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.graphql_java_generator.plugin.language.BatchLoader;
import com.graphql_java_generator.plugin.language.DataFetcher;
import com.graphql_java_generator.plugin.language.DataFetcherDelegate;
import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.Relation;
import com.graphql_java_generator.plugin.language.RelationType;
import com.graphql_java_generator.plugin.language.Type;
import com.graphql_java_generator.plugin.language.impl.ObjectType;

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

	@Test
	@DirtiesContext
	void getDataFetcherDelegate() {
		// Check, to start on a proper base
		assertEquals(6, documentParser.dataFetcherDelegates.size());

		// No DataFetcherDelegate creation
		Type type = new ObjectType("my.package", "Test", PluginMode.server);
		DataFetcherDelegate dfd = documentParser.getDataFetcherDelegate(type, false);
		assertNull(dfd, "No DataFetcherDelegate creation");
		assertEquals(6, documentParser.dataFetcherDelegates.size());

		// With DataFetcherDelegate creation
		type = new ObjectType("my.package", "Test2", PluginMode.server);
		dfd = documentParser.getDataFetcherDelegate(type, true);
		assertNotNull(dfd, "With DataFetcherDelegate creation");
		assertEquals(type, dfd.getType());
		assertEquals(7, documentParser.dataFetcherDelegates.size());
	}

	/** Tests the Data Fetchers that are listed during parsing */
	@Test
	@DirtiesContext
	void test_initDataFetchers() {
		assertEquals(10, documentParser.dataFetchers.size(), "nb of data fetchers in server mode");

		int i = 0;
		//
		// Verification of the data fetchers
		//
		// dataFetcher, dataFetcherName, owningType, fieldName, returnedTypeName, list, list of input parameters
		checkDataFetcher(documentParser.dataFetchers.get(i++), "boards", "QueryType", "boards", "Board", true, null);
		checkDataFetcher(documentParser.dataFetchers.get(i++), "topics", "QueryType", "topics", "Topic", true, null,
				"boardName");
		DataFetcher dataFetcher = checkDataFetcher(documentParser.dataFetchers.get(i++), "findTopics", "QueryType",
				"findTopics", "Topic", true, null, "boardName", "keyword");
		// Let's check the input parameters for this dataFetcher
		assertFalse(dataFetcher.getField().getInputParameters().get(0).isList());
		assertTrue(dataFetcher.getField().getInputParameters().get(1).isList());

		checkDataFetcher(documentParser.dataFetchers.get(i++), "createBoard", "MutationType", "createBoard", "Board",
				false, null, "name", "publiclyAvailable");
		checkDataFetcher(documentParser.dataFetchers.get(i++), "createTopic", "MutationType", "createTopic", "Topic",
				false, null, "authorId", "publiclyAvailable", "title", "content");
		checkDataFetcher(documentParser.dataFetchers.get(i++), "createPost", "MutationType", "createPost", "Post",
				false, null, "authorId", "publiclyAvailable", "title", "content");

		checkDataFetcher(documentParser.dataFetchers.get(i++), "topics", "Board", "topics", "Topic", true, "Board",
				"since");
		checkDataFetcher(documentParser.dataFetchers.get(i++), "author", "Topic", "author", "Member", false, "Topic");
		checkDataFetcher(documentParser.dataFetchers.get(i++), "posts", "Topic", "posts", "Post", true, "Topic",
				"since");
		checkDataFetcher(documentParser.dataFetchers.get(i++), "author", "Post", "author", "Member", false, "Post");

		//
		// Verification of the data fetchers delegates : QueryType, MutationType and 4 objects
		//
		assertEquals(6, documentParser.dataFetcherDelegates.size(), "data fetchers delegates");
		i = 0;
		int j = 0;

		// Delegate for QueryType
		assertEquals("QueryTypeDataFetchersDelegate", documentParser.dataFetcherDelegates.get(i).getName(),
				"delegate name " + i);
		assertEquals(3, documentParser.dataFetcherDelegates.get(i).getDataFetchers().size(),
				"nb DataFetcher for delegate " + i);
		assertEquals("boards", documentParser.dataFetcherDelegates.get(i).getDataFetchers().get(0).getName(),
				"Name of DataFetcher " + j++ + " for delegate " + i);
		assertEquals("topics", documentParser.dataFetcherDelegates.get(i).getDataFetchers().get(1).getName(),
				"Name of DataFetcher " + j++ + " for delegate " + i);
		assertEquals("findTopics", documentParser.dataFetcherDelegates.get(i).getDataFetchers().get(2).getName(),
				"Name of DataFetcher " + j++ + " for delegate " + i);
		//
		// Delegate for Board
		i += 2;
		j = 0;
		assertEquals("BoardDataFetchersDelegate", documentParser.dataFetcherDelegates.get(i).getName(),
				"delegate name " + i);
		assertEquals(1, documentParser.dataFetcherDelegates.get(i).getDataFetchers().size(),
				"nb DataFetcher for delegate " + i);
		assertEquals("topics", documentParser.dataFetcherDelegates.get(i).getDataFetchers().get(0).getName(),
				"Name of DataFetcher " + j++ + " for delegate " + i);
		//
		// Delegate for Topic
		i += 1;
		j = 0;
		assertEquals("TopicDataFetchersDelegate", documentParser.dataFetcherDelegates.get(i).getName(),
				"delegate name " + i);
		assertEquals(2, documentParser.dataFetcherDelegates.get(i).getDataFetchers().size(),
				"nb DataFetcher for delegate " + i);
		assertEquals("author", documentParser.dataFetcherDelegates.get(i).getDataFetchers().get(0).getName(),
				"Name of DataFetcher " + j++ + " for delegate " + i);
		assertEquals("posts", documentParser.dataFetcherDelegates.get(i).getDataFetchers().get(1).getName(),
				"Name of DataFetcher " + j++ + " for delegate " + i);
		//
		// Delegate for Post
		i += 1;
		j = 0;
		assertEquals("PostDataFetchersDelegate", documentParser.dataFetcherDelegates.get(i).getName(),
				"delegate name " + i);
		assertEquals(1, documentParser.dataFetcherDelegates.get(i).getDataFetchers().size(),
				"nb DataFetcher for delegate " + i);
		assertEquals("author", documentParser.dataFetcherDelegates.get(i).getDataFetchers().get(0).getName(),
				"Name of DataFetcher " + j++ + " for delegate " + i);

	}

	private DataFetcher checkDataFetcher(DataFetcher dataFetcher, String dataFetcherName, String owningType,
			String fieldName, String returnedTypeName, boolean list, String sourceName, String... inputParameters) {
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

		return dataFetcher;
	}

	/** Tests the Batch Loader that are listed during parsing */
	@Test
	@DirtiesContext
	void test_initBatchLoaders() {
		assertEquals(4, documentParser.batchLoaders.size());

		int i = -1;
		BatchLoader batchLoader = documentParser.batchLoaders.get(++i);
		assertEquals("Member", batchLoader.getType().getName());
		assertEquals("MemberDataFetchersDelegate", batchLoader.getDataFetcherDelegate().getName());
		assertEquals(1, batchLoader.getDataFetcherDelegate().getBatchLoaders().size());
		assertEquals(batchLoader, batchLoader.getDataFetcherDelegate().getBatchLoaders().get(0),
				"The only BatchLoader in this DataFetcherDelegate is this one");

		batchLoader = documentParser.batchLoaders.get(++i);
		assertEquals("Board", batchLoader.getType().getName());
		assertEquals("BoardDataFetchersDelegate", batchLoader.getDataFetcherDelegate().getName());
		assertEquals(1, batchLoader.getDataFetcherDelegate().getBatchLoaders().size());
		assertEquals(batchLoader, batchLoader.getDataFetcherDelegate().getBatchLoaders().get(0),
				"The only BatchLoader in this DataFetcherDelegate is this one");

		batchLoader = documentParser.batchLoaders.get(++i);
		assertEquals("Topic", batchLoader.getType().getName());
		assertEquals("TopicDataFetchersDelegate", batchLoader.getDataFetcherDelegate().getName());
		assertEquals(1, batchLoader.getDataFetcherDelegate().getBatchLoaders().size());
		assertEquals(batchLoader, batchLoader.getDataFetcherDelegate().getBatchLoaders().get(0),
				"The only BatchLoader in this DataFetcherDelegate is this one");

		batchLoader = documentParser.batchLoaders.get(++i);
		assertEquals("Post", batchLoader.getType().getName());
		assertEquals("PostDataFetchersDelegate", batchLoader.getDataFetcherDelegate().getName());
		assertEquals(1, batchLoader.getDataFetcherDelegate().getBatchLoaders().size());
		assertEquals(batchLoader, batchLoader.getDataFetcherDelegate().getBatchLoaders().get(0),
				"The only BatchLoader in this DataFetcherDelegate is this one");
	}

	@Test
	@DirtiesContext
	void test_initListOfImplementations() {
		assertEquals(0, documentParser.interfaceTypes.size(), "No interface");
	}
}
