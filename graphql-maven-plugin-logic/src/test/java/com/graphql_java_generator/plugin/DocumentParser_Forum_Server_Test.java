package com.graphql_java_generator.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CompletableFuture;

import javax.annotation.Resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.graphql_java_generator.plugin.language.BatchLoader;
import com.graphql_java_generator.plugin.language.DataFetcher;
import com.graphql_java_generator.plugin.language.DataFetchersDelegate;
import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.Relation;
import com.graphql_java_generator.plugin.language.RelationType;
import com.graphql_java_generator.plugin.language.Type;
import com.graphql_java_generator.plugin.language.impl.ObjectType;
import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;

import graphql.mavenplugin_notscannedbyspring.Forum_Server_SpringConfiguration;

/**
 * 
 * @author etienne-sf
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { Forum_Server_SpringConfiguration.class })
class DocumentParser_Forum_Server_Test {

	@Autowired
	GraphQLDocumentParser documentParser;
	@Resource
	GraphQLConfigurationTestHelper pluginConfiguration;

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
		assertEquals("@Entity\n@GraphQLObjectType(\"Topic\")", topic.getAnnotation(), "Entity annotation");
		int i = 0;
		checkFieldAnnotation(topic.getFields().get(i++), "id",
				"@Id\n\t@GeneratedValue\n\t@GraphQLScalar(fieldName = \"id\", graphQLTypeName = \"ID\", list = false, javaClass = UUID.class)");
		checkFieldAnnotation(topic.getFields().get(i++), "date",
				"@GraphQLScalar(fieldName = \"date\", graphQLTypeName = \"Date\", list = false, javaClass = Date.class)");
		checkFieldAnnotation(topic.getFields().get(i++), "author",
				"@Transient\n\t@GraphQLNonScalar(fieldName = \"author\", graphQLTypeName = \"Member\", list = false, javaClass = Member.class)");
		checkFieldAnnotation(topic.getFields().get(i++), "publiclyAvailable",
				"@GraphQLScalar(fieldName = \"publiclyAvailable\", graphQLTypeName = \"Boolean\", list = false, javaClass = Boolean.class)");
		checkFieldAnnotation(topic.getFields().get(i++), "nbPosts",
				"@GraphQLScalar(fieldName = \"nbPosts\", graphQLTypeName = \"Int\", list = false, javaClass = Integer.class)");
		checkFieldAnnotation(topic.getFields().get(i++), "title",
				"@GraphQLScalar(fieldName = \"title\", graphQLTypeName = \"String\", list = false, javaClass = String.class)");
		checkFieldAnnotation(topic.getFields().get(i++), "content",
				"@GraphQLScalar(fieldName = \"content\", graphQLTypeName = \"String\", list = false, javaClass = String.class)");
		checkFieldAnnotation(topic.getFields().get(i++), "posts",
				"@Transient\n\t@GraphQLNonScalar(fieldName = \"posts\", graphQLTypeName = \"Post\", list = true, javaClass = Post.class)");
	}

	private void checkFieldAnnotation(Field field, String name, String annotation) {
		String msg = "Check annotation for field " + field.getName() + " (server mode)";
		assertEquals(name, field.getName(), msg + " [name]");
		assertEquals(annotation, field.getAnnotation(), msg + " [annotation]");
	}

	@Test
	@DirtiesContext
	void test_getDataFetchersDelegate() {
		// Check, to start on a proper base
		assertEquals(7, documentParser.dataFetchersDelegates.size());

		// No DataFetchersDelegate creation
		Type type = new ObjectType("my.package", "Test");
		DataFetchersDelegate dfd = documentParser.getDataFetchersDelegate(type, false);
		assertNull(dfd, "No DataFetchersDelegate creation");
		assertEquals(7, documentParser.dataFetchersDelegates.size());

		// With DataFetchersDelegate creation
		type = new ObjectType("my.package", "Test2");
		dfd = documentParser.getDataFetchersDelegate(type, true);
		assertNotNull(dfd, "With DataFetchersDelegate creation");
		assertEquals(type, dfd.getType());
		assertEquals(8, documentParser.dataFetchersDelegates.size());
	}

	/** Tests the Data Fetchers that are listed during parsing */
	@Test
	@DirtiesContext
	void test_initDataFetchers() {
		assertEquals(15, documentParser.dataFetchers.size(), "nb of data fetchers in server mode");

		int i = 0;
		//
		// Verification of the data fetchers
		//
		// dataFetcher, dataFetcherName, owningType, fieldName, returnedTypeName, list, completableFuture, sourceName,
		// list of input parameters
		checkDataFetcher(documentParser.dataFetchers.get(i++), "boards", "QueryType", "boards", "Board", true, false,
				null);
		checkDataFetcher(documentParser.dataFetchers.get(i++), "nbBoards", "QueryType", "nbBoards", "Int", false, false,
				null);
		checkDataFetcher(documentParser.dataFetchers.get(i++), "topics", "QueryType", "topics", "Topic", true, false,
				null, "boardName");
		DataFetcher dataFetcher = checkDataFetcher(documentParser.dataFetchers.get(i++), "findTopics", "QueryType",
				"findTopics", "Topic", true, false, null, "boardName", "keyword");
		// Let's check the input parameters for this dataFetcher
		assertFalse(dataFetcher.getField().getInputParameters().get(0).isList());
		assertTrue(dataFetcher.getField().getInputParameters().get(1).isList());

		checkDataFetcher(documentParser.dataFetchers.get(i++), "createBoard", "MutationType", "createBoard", "Board",
				false, false, null, "name", "publiclyAvailable");
		checkDataFetcher(documentParser.dataFetchers.get(i++), "createTopic", "MutationType", "createTopic", "Topic",
				false, false, null, "topic");
		checkDataFetcher(documentParser.dataFetchers.get(i++), "createPost", "MutationType", "createPost", "Post",
				false, false, null, "post");
		checkDataFetcher(documentParser.dataFetchers.get(i++), "createPosts", "MutationType", "createPosts", "Post",
				true, false, null, "spam");

		checkDataFetcher(documentParser.dataFetchers.get(i++), "subscribeToNewPost", "SubscriptionType",
				"subscribeToNewPost", "Post", false, false, null, "boardName");

		checkDataFetcher(documentParser.dataFetchers.get(i++), "topics", "Board", "topics", "Topic", true, false,
				"Board", "since");
		checkDataFetcher(documentParser.dataFetchers.get(i++), "author", "Topic", "author", "Member", false, false,
				"Topic");
		checkDataFetcher(documentParser.dataFetchers.get(i++), "author", "Topic", "author", "Member", false, true,
				"Topic");
		checkDataFetcher(documentParser.dataFetchers.get(i++), "posts", "Topic", "posts", "Post", true, false, "Topic",
				"memberId", "memberName", "since");
		checkDataFetcher(documentParser.dataFetchers.get(i++), "author", "Post", "author", "Member", false, false,
				"Post");
		checkDataFetcher(documentParser.dataFetchers.get(i++), "author", "Post", "author", "Member", false, true,
				"Post");

		//
		// Verification of the data fetchers delegates : QueryType, MutationType and 4 objects
		//
		assertEquals(7, documentParser.dataFetchersDelegates.size(), "data fetchers delegates");
		i = 0;
		int j = 0;

		// Delegate for QueryType
		assertEquals("DataFetchersDelegateQueryType", documentParser.dataFetchersDelegates.get(i).getName(),
				"delegate name " + i);
		assertEquals(4, documentParser.dataFetchersDelegates.get(i).getDataFetchers().size(),
				"nb DataFetcher for delegate " + i);
		assertEquals("boards", documentParser.dataFetchersDelegates.get(i).getDataFetchers().get(j).getName(),
				"Name of DataFetcher " + j++ + " for delegate " + i);
		assertEquals("nbBoards", documentParser.dataFetchersDelegates.get(i).getDataFetchers().get(j).getName(),
				"Name of DataFetcher " + j++ + " for delegate " + i);
		assertEquals("topics", documentParser.dataFetchersDelegates.get(i).getDataFetchers().get(j).getName(),
				"Name of DataFetcher " + j++ + " for delegate " + i);
		assertEquals("findTopics", documentParser.dataFetchersDelegates.get(i).getDataFetchers().get(j).getName(),
				"Name of DataFetcher " + j++ + " for delegate " + i);
		//
		// Delegate for Board
		i += 3; // We skip the mutation and the subscription
		j = 0;
		assertEquals("DataFetchersDelegateBoard", documentParser.dataFetchersDelegates.get(i).getName(),
				"delegate name " + i);
		assertEquals(1, documentParser.dataFetchersDelegates.get(i).getDataFetchers().size(),
				"nb DataFetcher for delegate " + i);
		assertEquals("topics", documentParser.dataFetchersDelegates.get(i).getDataFetchers().get(0).getName(),
				"Name of DataFetcher " + j++ + " for delegate " + i);
		//
		// Delegate for Topic
		i += 1;
		j = 0;
		assertEquals("DataFetchersDelegateTopic", documentParser.dataFetchersDelegates.get(i).getName(),
				"delegate name " + i);
		assertEquals(3, documentParser.dataFetchersDelegates.get(i).getDataFetchers().size(),
				"nb DataFetcher for delegate " + i);
		assertEquals("author", documentParser.dataFetchersDelegates.get(i).getDataFetchers().get(j).getName(),
				"Name of DataFetcher " + j++ + " for delegate " + i);
		assertEquals("author", documentParser.dataFetchersDelegates.get(i).getDataFetchers().get(j).getName(),
				"Name of DataFetcher " + j++ + " for delegate " + i);
		assertEquals("posts", documentParser.dataFetchersDelegates.get(i).getDataFetchers().get(j).getName(),
				"Name of DataFetcher " + j++ + " for delegate " + i);
		//
		// Delegate for Post
		i += 1;
		j = 0;
		assertEquals("DataFetchersDelegatePost", documentParser.dataFetchersDelegates.get(i).getName(),
				"delegate name " + i);
		assertEquals(2, documentParser.dataFetchersDelegates.get(i).getDataFetchers().size(),
				"nb DataFetcher for delegate " + i);
		assertEquals("author", documentParser.dataFetchersDelegates.get(i).getDataFetchers().get(j).getName(),
				"Name of DataFetcher " + j++ + " for delegate " + i);
		assertEquals("author", documentParser.dataFetchersDelegates.get(i).getDataFetchers().get(j).getName(),
				"Name of DataFetcher " + j++ + " for delegate " + i);
	}

	/**
	 * 
	 * @param dataFetcher
	 *            The {@link DataFetcher} to be tested
	 * @param dataFetcherName
	 *            The expected name
	 * @param owningType
	 *            The expected owning type for the field to be fetched
	 * @param fieldName
	 *            The expected field name
	 * @param returnedTypeName
	 *            The expected return type name
	 * @param list
	 *            true if the return type is a list
	 * @param completableFuture
	 *            true if the DataFetcher uses a BatchLoader (that is, if the method should return a
	 *            {@link CompletableFuture} that will be loaded in a second time, by a Data Loader)
	 * @param sourceName
	 *            The expected source name, that is: the name of the object which contains the field to fetch (if it is
	 *            a GraphQLobject), or null if the field is a field of a query, a mutation or a subscription
	 * @param inputParameters
	 *            The expected list of parameters for this Data Fetcher
	 * @return
	 */
	private DataFetcher checkDataFetcher(DataFetcher dataFetcher, String dataFetcherName, String owningType,
			String fieldName, String returnedTypeName, boolean list, boolean completableFuture, String sourceName,
			String... inputParameters) {
		assertEquals(dataFetcherName, dataFetcher.getName(), "dataFetcherName");
		assertEquals(owningType, dataFetcher.getField().getOwningType().getName(), "owningType");
		assertEquals(returnedTypeName, dataFetcher.getField().getType().getName(), "returnedTypeName");
		assertEquals(list, dataFetcher.getField().isList(), "list");
		assertEquals(completableFuture, dataFetcher.isCompletableFuture(), "completableFuture");
		assertEquals(fieldName, dataFetcher.getField().getName(), "fieldName");
		assertEquals(sourceName, dataFetcher.getGraphQLOriginType(), "sourceName");

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
		assertEquals("DataFetchersDelegateMember", batchLoader.getDataFetchersDelegate().getName());
		assertEquals(1, batchLoader.getDataFetchersDelegate().getBatchLoaders().size());
		assertEquals(batchLoader, batchLoader.getDataFetchersDelegate().getBatchLoaders().get(0),
				"The only BatchLoader in this DataFetchersDelegate is this one");

		batchLoader = documentParser.batchLoaders.get(++i);
		assertEquals("Board", batchLoader.getType().getName());
		assertEquals("DataFetchersDelegateBoard", batchLoader.getDataFetchersDelegate().getName());
		assertEquals(1, batchLoader.getDataFetchersDelegate().getBatchLoaders().size());
		assertEquals(batchLoader, batchLoader.getDataFetchersDelegate().getBatchLoaders().get(0),
				"The only BatchLoader in this DataFetchersDelegate is this one");

		batchLoader = documentParser.batchLoaders.get(++i);
		assertEquals("Topic", batchLoader.getType().getName());
		assertEquals("DataFetchersDelegateTopic", batchLoader.getDataFetchersDelegate().getName());
		assertEquals(1, batchLoader.getDataFetchersDelegate().getBatchLoaders().size());
		assertEquals(batchLoader, batchLoader.getDataFetchersDelegate().getBatchLoaders().get(0),
				"The only BatchLoader in this DataFetchersDelegate is this one");

		batchLoader = documentParser.batchLoaders.get(++i);
		assertEquals("Post", batchLoader.getType().getName());
		assertEquals("DataFetchersDelegatePost", batchLoader.getDataFetchersDelegate().getName());
		assertEquals(1, batchLoader.getDataFetchersDelegate().getBatchLoaders().size());
		assertEquals(batchLoader, batchLoader.getDataFetchersDelegate().getBatchLoaders().get(0),
				"The only BatchLoader in this DataFetchersDelegate is this one");
	}

	@Test
	@DirtiesContext
	void test_initListOfImplementations() {
		assertEquals(0, documentParser.interfaceTypes.size(), "No interface");
	}

	@Test
	@DirtiesContext
	void test_inputTypesAnnotationInServerMode() {
		checkInputTypeAnnotations((ObjectType) documentParser.getType("TopicPostInput"));
		checkInputTypeAnnotations((ObjectType) documentParser.getType("PostInput"));
		checkInputTypeAnnotations((ObjectType) documentParser.getType("TopicInput"));
	}

	void checkInputTypeAnnotations(ObjectType o) {
		assertEquals("@GraphQLInputType(\"" + o.getName() + "\")", o.getAnnotation());
		for (Field f : o.getFields()) {
			assertTrue(f.getAnnotation().contains("@GraphQL"));
		}
	}

}
