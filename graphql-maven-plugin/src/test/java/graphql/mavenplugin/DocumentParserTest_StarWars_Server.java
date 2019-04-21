package graphql.mavenplugin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import graphql.mavenplugin.language.DataFetcher;
import graphql.mavenplugin.language.impl.ObjectType;
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
		assertEquals(10, documentParser.dataFetchers.size(), "nb of data fetchers in server mode");

		int i = 0;
		// dataFetcher, dataFetcherName, owningType, fieldName, returnedTypeName, list
		checkDataFetcher(documentParser.dataFetchers.get(i++), "QueryTypeHero", "QueryType", "hero", "Character",
				false);
		checkDataFetcher(documentParser.dataFetchers.get(i++), "QueryTypeCharacters", "QueryType", "characters",
				"Character", true);
		checkDataFetcher(documentParser.dataFetchers.get(i++), "QueryTypeHuman", "QueryType", "human", "Human", false);
		checkDataFetcher(documentParser.dataFetchers.get(i++), "QueryTypeDroid", "QueryType", "droid", "Droid", false);

		checkDataFetcher(documentParser.dataFetchers.get(i++), "HumanFriends", "Human", "friends", "Character", true);
		checkDataFetcher(documentParser.dataFetchers.get(i++), "HumanAppearsIn", "Human", "appearsIn", "Episode", true);

		checkDataFetcher(documentParser.dataFetchers.get(i++), "DroidFriends", "Droid", "friends", "Character", true);
		checkDataFetcher(documentParser.dataFetchers.get(i++), "DroidAppearsIn", "Droid", "appearsIn", "Episode", true);

		checkDataFetcher(documentParser.dataFetchers.get(i++), "CharacterFriends", "Character", "friends", "Character",
				true);
		checkDataFetcher(documentParser.dataFetchers.get(i++), "CharacterAppearsIn", "Character", "appearsIn",
				"Episode", true);
	}

	private void checkDataFetcher(DataFetcher dataFetcher, String dataFetcherName, String owningType, String fieldName,
			String returnedTypeName, boolean list) {
		assertEquals(dataFetcherName, dataFetcher.getName(), "dataFetcherName");
		assertEquals(owningType, dataFetcher.getField().getOwningType().getName(), "owningType");
		assertEquals(returnedTypeName, dataFetcher.getField().getType().getName(), "returnedTypeName");
		assertEquals(list, dataFetcher.getField().isList(), "list");
		assertEquals(fieldName, dataFetcher.getField().getName(), "fieldName");
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
