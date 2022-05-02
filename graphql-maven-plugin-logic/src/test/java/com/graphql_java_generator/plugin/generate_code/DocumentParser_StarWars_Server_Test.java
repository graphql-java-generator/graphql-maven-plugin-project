package com.graphql_java_generator.plugin.generate_code;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;
import com.graphql_java_generator.plugin.language.DataFetcher;
import com.graphql_java_generator.plugin.language.impl.ObjectType;

import graphql.mavenplugin_notscannedbyspring.StarWars_Server_SpringConfiguration;

/**
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
class DocumentParser_StarWars_Server_Test {

	AbstractApplicationContext ctx = null;
	GenerateCodeDocumentParser documentParser;
	GraphQLConfiguration pluginConfiguration;

	@BeforeEach
	void loadApplicationContext() throws IOException {
		ctx = new AnnotationConfigApplicationContext(StarWars_Server_SpringConfiguration.class);
		documentParser = ctx.getBean(GenerateCodeDocumentParser.class);
		pluginConfiguration = ctx.getBean(GraphQLConfiguration.class);

		documentParser.parseGraphQLSchemas();
	}

	/** Tests the Data Fetchers that are listed during parsing */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_initDataFetchers() {

		// DataFetchers are aggregated into DataFetchersDelegate (one DataFetchersDelegate per type in the graphQL
		// schema
		// that needs at least one DataFetcher)
		assertEquals(6, documentParser.dataFetchersDelegates.size(), "nb of data fetchers Delegate in server mode");
		assertEquals(13, documentParser.dataFetchers.size(), "nb of data fetchers in server mode");

		int i = 0;
		// dataFetcher, dataFetcherName, owningType, fieldName, returnedTypeName, list, sourceName
		checkDataFetcher(documentParser.dataFetchers.get(i++), "hero", "QueryType", "hero", "Character", 0, null);
		checkDataFetcher(documentParser.dataFetchers.get(i++), "characters", "QueryType", "characters", "Character", 1,
				null);
		checkDataFetcher(documentParser.dataFetchers.get(i++), "human", "QueryType", "human", "Human", 0, null);
		checkDataFetcher(documentParser.dataFetchers.get(i++), "droid", "QueryType", "droid", "Droid", 0, null);

		// Mutation
		checkDataFetcher(documentParser.dataFetchers.get(i++), "createHuman", "MutationType", "createHuman", "Human", 0,
				null);
		checkDataFetcher(documentParser.dataFetchers.get(i++), "addFriend", "MutationType", "addFriend", "Character", 0,
				null);

		// Subscription
		checkDataFetcher(documentParser.dataFetchers.get(i++), "newCharacter", "SubscriptionType", "newCharacter",
				"Character", 0, null);

		checkDataFetcher(documentParser.dataFetchers.get(i++), "friends", "Human", "friends", "Character", 1, "Human");
		checkDataFetcher(documentParser.dataFetchers.get(i++), "appearsIn", "Human", "appearsIn", "Episode", 1,
				"Human");

		checkDataFetcher(documentParser.dataFetchers.get(i++), "friends", "Droid", "friends", "Character", 1, "Droid");
		checkDataFetcher(documentParser.dataFetchers.get(i++), "appearsIn", "Droid", "appearsIn", "Episode", 1,
				"Droid");

		checkDataFetcher(documentParser.dataFetchers.get(i++), "friends", "Character", "friends", "Character", 1,
				"Character");
		checkDataFetcher(documentParser.dataFetchers.get(i++), "appearsIn", "Character", "appearsIn", "Episode", 1,
				"Character");
	}

	private void checkDataFetcher(DataFetcher dataFetcher, String dataFetcherName, String owningType, String fieldName,
			String returnedTypeName, int list, String graphQLOriginType) {
		assertEquals(dataFetcherName, dataFetcher.getName(), "dataFetcherName");
		assertEquals(owningType, dataFetcher.getField().getOwningType().getName(), "owningType");
		assertEquals(returnedTypeName, dataFetcher.getField().getType().getName(), "returnedTypeName");
		assertEquals(list, dataFetcher.getField().getFieldTypeAST().getListDepth(), "list");
		assertEquals(fieldName, dataFetcher.getField().getName(), "fieldName");
		if (graphQLOriginType == null)
			assertNull(dataFetcher.getGraphQLOriginType(), "graphQLOriginType");
		else
			assertEquals(graphQLOriginType, dataFetcher.getGraphQLOriginType().getClassSimpleName(),
					"graphQLOriginType");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_initListOfImplementations() {
		assertEquals(1, documentParser.getInterfaceTypes().size(), "Only one interface");
		List<ObjectType> implementingTypes = documentParser.getInterfaceTypes().get(0).getImplementingTypes();
		assertEquals(2, implementingTypes.size(), "2 types for this interface");
		assertEquals("Human", implementingTypes.get(0).getName());
		assertEquals("Droid", implementingTypes.get(1).getName());
	}

}
