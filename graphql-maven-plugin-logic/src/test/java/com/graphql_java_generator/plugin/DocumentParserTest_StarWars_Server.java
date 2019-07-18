package com.graphql_java_generator.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.graphql_java_generator.plugin.DocumentParser;
import com.graphql_java_generator.plugin.language.DataFetcher;
import com.graphql_java_generator.plugin.language.impl.ObjectType;

import graphql.mavenplugin_notscannedbyspring.StarWars_Server_SpringConfiguration;

/**
 * 
 * @author EtienneSF
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { StarWars_Server_SpringConfiguration.class })
class DocumentParserTest_StarWars_Server {

	@Autowired
	DocumentParser documentParser;

	@BeforeEach
	void setUp() throws Exception {
		documentParser.parseDocuments();
	}

	/** Tests the Data Fetchers that are listed during parsing */
	@Test
	@DirtiesContext
	void test_initDataFetchers() {
		assertEquals(12, documentParser.dataFetchers.size(), "nb of data fetchers in server mode");

		int i = 0;
		// dataFetcher, dataFetcherName, owningType, fieldName, returnedTypeName, list
		checkDataFetcher(documentParser.dataFetchers.get(i++), "hero", "QueryType", "hero", "Character", false, null);
		checkDataFetcher(documentParser.dataFetchers.get(i++), "characters", "QueryType", "characters", "Character",
				true, null);
		checkDataFetcher(documentParser.dataFetchers.get(i++), "human", "QueryType", "human", "Human", false, null);
		checkDataFetcher(documentParser.dataFetchers.get(i++), "droid", "QueryType", "droid", "Droid", false, null);

		checkDataFetcher(documentParser.dataFetchers.get(i++), "createHuman", "MutationType", "createHuman", "Human",
				false, null);
		checkDataFetcher(documentParser.dataFetchers.get(i++), "addFriend", "MutationType", "addFriend", "Character",
				false, null);

		checkDataFetcher(documentParser.dataFetchers.get(i++), "friends", "Human", "friends", "Character", true,
				"Human");
		checkDataFetcher(documentParser.dataFetchers.get(i++), "appearsIn", "Human", "appearsIn", "Episode", true,
				"Human");

		checkDataFetcher(documentParser.dataFetchers.get(i++), "friends", "Droid", "friends", "Character", true,
				"Droid");
		checkDataFetcher(documentParser.dataFetchers.get(i++), "appearsIn", "Droid", "appearsIn", "Episode", true,
				"Droid");

		checkDataFetcher(documentParser.dataFetchers.get(i++), "friends", "Character", "friends", "Character", true,
				"CharacterImpl");
		checkDataFetcher(documentParser.dataFetchers.get(i++), "appearsIn", "Character", "appearsIn", "Episode", true,
				"CharacterImpl");
	}

	private void checkDataFetcher(DataFetcher dataFetcher, String dataFetcherName, String owningType, String fieldName,
			String returnedTypeName, boolean list, String sourceName) {
		assertEquals(dataFetcherName, dataFetcher.getName(), "dataFetcherName");
		assertEquals(owningType, dataFetcher.getField().getOwningType().getName(), "owningType");
		assertEquals(returnedTypeName, dataFetcher.getField().getType().getName(), "returnedTypeName");
		assertEquals(list, dataFetcher.getField().isList(), "list");
		assertEquals(fieldName, dataFetcher.getField().getName(), "fieldName");
		assertEquals(sourceName, dataFetcher.getSourceName(), "sourceName");
	}

	@Test
	@DirtiesContext
	void test_initListOfImplementations() {
		assertEquals(1, documentParser.interfaceTypes.size(), "Only one interface");
		List<ObjectType> implementingTypes = documentParser.interfaceTypes.get(0).getImplementingTypes();
		assertEquals(3, implementingTypes.size(), "3 types for this interface");
		assertEquals("Human", implementingTypes.get(0).getName());
		assertEquals("Droid", implementingTypes.get(1).getName());
		assertEquals("CharacterImpl", implementingTypes.get(2).getName());
	}

}
