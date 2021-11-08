package com.graphql_java_generator.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.graphql_java_generator.client.request.InputParameter;
import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.domain.client.starwars.Character;
import com.graphql_java_generator.domain.client.starwars.Droid;
import com.graphql_java_generator.domain.client.starwars.Episode;
import com.graphql_java_generator.domain.client.starwars.Human;
import com.graphql_java_generator.domain.client.starwars.QueryType;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.exception.GraphQLResponseParseException;

/**
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
class QueryExecutorImpl_StarWars_Test {

	RequestExecutionImpl queryExecutorImpl;
	QueryType queryType;

	@BeforeEach
	void setUp() throws Exception {
		queryType = new QueryType("http://localhost:8180/graphql");
		queryExecutorImpl = new RequestExecutionImpl("http://localhost:8180/graphql");
	}

	@Disabled
	@Test
	void test_execute() {
		fail("Not yet implemented");
	}

	@Disabled
	@Test
	void test_execute_withAlias() {
		fail("Not yet implemented");
	}

	/**
	 * Build a request with one parameter (ID), and a {@link Character} as the response.
	 * 
	 * @throws GraphQLRequestPreparationException
	 * @throws GraphQLRequestExecutionException
	 */
	@Test
	void test_buildRequest_ID_characters() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("queryTypeHeroId", "1");

		// The response should contain id and name
		ObjectResponse objectResponse = queryType.getHeroResponseBuilder()
				.withQueryResponseDef(" {   id      name   }   ")
				// .withInputParameter(InputParameter.newBindParameter("id", "queryTypeHeroId", false, null))//
				// .withField("id").withField("name").
				.build();

		// Go, go, go
		String request = objectResponse.buildRequestAsString(parameters);

		// Verification
		assertEquals("{\"query\":\"query{hero{id name __typename}}\"}", request);

		// Go, go, go
		Map<String, String> map = objectResponse.buildRequestAsMap(null);

		// Verification
		QueryExecutorImpl_allGraphqlCases_Test.checkRequestMap(map, "query{hero{id name __typename}}", null, null);
	}

	/**
	 * Build a request with one parameter (ID), and a {@link Character} as the response.
	 * 
	 * @throws GraphQLRequestPreparationException
	 * @throws GraphQLRequestExecutionException
	 */
	@Test
	void test_buildRequest_EpisodeID_characters()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("queryTypeHeroEpisode", Episode.NEWHOPE);
		parameters.put("queryTypeHeroId", "this is an id");

		// The response should contain id and name
		ObjectResponse objectResponse = queryType.getHeroResponseBuilder()
				.withQueryResponseDef(" {   id      name   }   ")
				// .withInputParameter(InputParameter.newBindParameter("episode", "queryTypeHeroEpisode", false,
				// null))//
				// .withInputParameter(InputParameter.newBindParameter("id", "queryTypeHeroId", false, null))//
				// .withField("id").withField("name")
				.build();

		// Go, go, go
		String request = objectResponse.buildRequestAsString(parameters);

		// Verification
		assertEquals("{\"query\":\"query{hero(episode:NEWHOPE){id name __typename}}\"}", request);

		// Go, go, go
		Map<String, String> map = objectResponse.buildRequestAsMap(parameters);

		// Verification
		QueryExecutorImpl_allGraphqlCases_Test.checkRequestMap(map, "query{hero(episode:NEWHOPE){id name __typename}}",
				null, null);
	}

	/**
	 * Build a request with one parameter (ID), and a {@link Character} as the response.
	 * 
	 * @throws GraphQLRequestPreparationException
	 * @throws GraphQLRequestExecutionException
	 */
	@Test
	void test_buildRequest_Episode_idNameAppearsInFriendsName()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("queryTypeHeroEpisode", Episode.NEWHOPE);

		// The response should contain id and name
		ObjectResponse objectResponse = queryType.getHeroResponseBuilder()
				.withQueryResponseDef("{ id\nname\rappearsIn\tfriends{name}}")
				// .withInputParameter(InputParameter.newBindParameter("episode", "queryTypeHeroEpisode", false,
				// null))//
				// .withField("id").withField("name").withField("appearsIn")
				// .withSubObject(new Builder(Character.class, "friends").withField("name").build())
				.build();

		// Go, go, go
		String request = objectResponse.buildRequestAsString(parameters);

		// Verification
		assertEquals(
				"{\"query\":\"query{hero(episode:NEWHOPE){id name appearsIn friends{name __typename} __typename}}\"}",
				request);
	}

	/**
	 * Build a request with one parameter (ID), and a {@link Character} as the response.
	 * 
	 * @throws GraphQLRequestPreparationException
	 * @throws GraphQLRequestExecutionException
	 */
	@Test
	void test_buildRequest_Episode_idNameAppearsInFriendsName_noFieldParameter()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		Map<String, Object> parameters = new HashMap<>();// The map remains empty

		// The response should contain id and name
		ObjectResponse objectResponse = queryType.getHeroResponseBuilder()
				.withQueryResponseDef("{ id name appearsIn friends{name}}")
				// .withInputParameter(InputParameter.newBindParameter("episode", "queryTypeHeroEpisode", false,
				// null))//
				// .withField("id").withField("name").withField("appearsIn")
				// .withSubObject(new Builder(Character.class, "friends").withField("name").build())
				.build();

		// Go, go, go
		String request = objectResponse.buildRequestAsString(parameters);

		// Verification
		assertEquals("{\"query\":\"query{hero{id name appearsIn friends{name __typename} __typename}}\"}", request);
	}

	/**
	 * Build a request with one parameter (ID), and a {@link Character} as the response.
	 * 
	 * @throws GraphQLRequestPreparationException
	 * @throws GraphQLRequestExecutionException
	 */
	@Test
	void test_buildRequest_Episode_idNameAppearsInFriendsName_nullMap()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		// The response should contain id and name
		ObjectResponse objectResponse = queryType.getHeroResponseBuilder().withQueryResponseDef(
				"\t\n\r {\t\n\r id\t\n\r name\t\n\r appearsIn\t\n\r friends\t\n\r {\t\n\r name\t\n\r }\t\n\r }\t\n\r ")
				// .withInputParameter(InputParameter.newBindParameter("episode", "queryTypeHeroEpisode", false,
				// null))//
				// .withField("id").withField("name").withField("appearsIn")
				// .withSubObject(new Builder(Character.class, "friends").withField("name").build())
				.build();

		// Go, go, go
		String request = objectResponse.buildRequestAsString(null); // No map given (null instead)

		// Verification
		assertEquals("{\"query\":\"query{hero{id name appearsIn friends{name __typename} __typename}}\"}", request);
	}

	/**
	 * Build a request with one parameter (ID), and a {@link Character} as the response.
	 * 
	 * @throws GraphQLRequestPreparationException
	 * @throws GraphQLRequestExecutionException
	 */
	@Test
	void test_buildRequest_Episode_idNameAppearsInFriendsName_nullMap_MissingMandatoryParameter()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		// The response should contain id and name
		ObjectResponse objectResponse = queryType.getDroidResponseBuilder()
				.withQueryResponseDef(" { id name appearsIn friends { name } } ")
				// .withInputParameter(InputParameter.newBindParameter("episode", "queryTypeHeroEpisode", true, null))//
				// .withField("id").withField("name").withField("appearsIn")
				// .withSubObject(new Builder(Character.class, "friends").withField("name").build())
				.build();

		// Go, go, go
		GraphQLRequestExecutionException e = assertThrows(GraphQLRequestExecutionException.class,
				() -> objectResponse.buildRequestAsString(new HashMap<>())); // Empty map given
		GraphQLRequestExecutionException e2 = assertThrows(GraphQLRequestExecutionException.class,
				() -> objectResponse.buildRequestAsString(null)); // No map given (null instead)

		// Verification
		assertTrue(e.getMessage().contains("queryTypeDroidId"));
		assertTrue(e2.getMessage().contains("queryTypeDroidId"));
	}

	@Test
	void test_parseResponse_KO() throws GraphQLRequestPreparationException {
		// Preparation
		Exception exception;
		List<InputParameter> parameters = new ArrayList<>();
		parameters.add(InputParameter.newHardCodedParameter("episode", Episode.NEWHOPE, "Episode", false, 0, false));
		// The response should contain id and name
		ObjectResponse objectResponse = queryType.getHeroResponseBuilder()
				.withQueryResponseDef(" { id name appearsIn friends { name } } ")
				// .withField("id").withField("name").withField("appearsIn")
				// .withSubObject(new Builder(Character.class, "friends").withField("name").build())
				.build();

		assertThrows(IllegalArgumentException.class,
				() -> parseResponseForStarWarsSchema(null, objectResponse, Character.class));

		exception = assertThrows(JsonParseException.class,
				() -> parseResponseForStarWarsSchema("invalid JSON", objectResponse, Character.class));
		assertTrue(exception.getMessage().contains("invalid"));

		exception = assertThrows(GraphQLResponseParseException.class, () -> parseResponseForStarWarsSchema(
				"{\"wrongTag\":{\"hero\":{\"id\":\"An id\",\"name\":\"A hero's name\",\"appearsIn\":[\"NEWHOPE\",\"JEDI\"],\"friends\":null}}}",
				objectResponse, QueryType.class));
		assertTrue(exception.getMessage().contains("'data'"));

		exception = assertThrows(UnrecognizedPropertyException.class, () -> parseResponseForStarWarsSchema(
				"{\"data\":{\"wrongAlias\":{\"id\":\"An id\",\"name\":\"A hero's name\",\"appearsIn\":[\"NEWHOPE\",\"JEDI\"],\"friends\":null}}}",
				objectResponse, QueryType.class));
		assertTrue(exception.getMessage().contains("wrongAlias"));

		exception = assertThrows(UnrecognizedPropertyException.class, () -> parseResponseForStarWarsSchema(
				"{\"data\":{\"hero\":{\"wrongTag\":\"An id\",\"name\":\"A hero's name\",\"appearsIn\":[\"NEWHOPE\",\"JEDI\"],\"friends\":null,\"__typename\":\"Droid\"}}}",
				objectResponse, QueryType.class));
		assertTrue(exception.getMessage().contains("wrongTag"));

		exception = assertThrows(InvalidFormatException.class, () -> parseResponseForStarWarsSchema(
				"{\"data\":{\"hero\":{\"id\":\"An id\",\"name\":\"A hero's name\",\"appearsIn\":[\"WRONG_EPISODE\",\"JEDI\"],\"friends\":null,\"__typename\":\"Droid\"}}}",
				objectResponse, QueryType.class));
		assertTrue(exception.getMessage().contains("WRONG_EPISODE"));
	}

	@Test
	void test_parseResponse_OK_noFriends()
			throws GraphQLResponseParseException, IOException, GraphQLRequestPreparationException {
		// Preparation
		List<InputParameter> parameters = new ArrayList<>();
		parameters.add(InputParameter.newHardCodedParameter("episode", Episode.NEWHOPE, "Episode", false, 0, false));

		// The response should contain id and name
		ObjectResponse objectResponse = queryType.getHeroResponseBuilder()
				.withQueryResponseDef(" { id appearsIn friends { name } } ")
				// .withField("id").withField("appearsIn").withSubObject(new Builder(Character.class,
				// "friends").build())//
				// .withField("name")
				.build();

		String rawResponse = "{\"data\":{\"hero\":{\"id\":\"An id\",\"name\":\"A hero's name\",\"appearsIn\":[\"NEWHOPE\",\"JEDI\"],\"friends\":null, \"__typename\": \"Human\"}}}";

		// Go, go, go
		Object response = parseResponseForStarWarsSchema(rawResponse, objectResponse, QueryType.class);

		// Verification
		assertTrue(response instanceof QueryType, "response instanceof QueryTypeHero");
		Character character = ((QueryType) response).getHero();
		assertTrue(character instanceof Human, "character instanceof QueryTypeHero");
		assertEquals(Human.class.getName(), character.getClass().getName());
		assertEquals("An id", character.getId(), "id");
		assertEquals("A hero's name", character.getName(), "name");
		List<Episode> episodes = character.getAppearsIn();
		assertEquals(2, episodes.size(), "2 episodes");
		assertEquals(Episode.NEWHOPE, episodes.get(0), "First episode");
		assertEquals(Episode.JEDI, episodes.get(1), "Second episode");
		assertNull(character.getFriends(), "He has no friends!  :(  ");
	}

	@Test
	void test_parseResponse_OK_emptyListOfFriends()
			throws GraphQLResponseParseException, IOException, GraphQLRequestPreparationException {
		// Preparation
		List<InputParameter> parameters = new ArrayList<>();
		parameters.add(InputParameter.newHardCodedParameter("episode", Episode.NEWHOPE, "Episode", false, 0, false));

		// The response should contain id and name
		ObjectResponse objectResponse = queryType.getHeroResponseBuilder()
				.withQueryResponseDef(" { id appearsIn friends { name } } ")
				// .withField("id").withField("appearsIn").withSubObject(new Builder(Character.class,
				// "friends").build())//
				// .withField("name")
				.build();

		String rawResponse = "{\"data\":{\"hero\":{\"friends\":[], \"__typename\": \"Droid\"}}}";

		// Go, go, go
		Object response = parseResponseForStarWarsSchema(rawResponse, objectResponse, QueryType.class);

		// Verification
		assertTrue(response instanceof QueryType, "response instanceof QueryType");
		Character character = ((QueryType) response).getHero();
		assertTrue(character instanceof Droid, "character instanceof Droid");
		assertEquals(Droid.class.getName(), character.getClass().getName());
		assertNotNull(character.getFriends(), "He has perhaps has friends...");
		assertEquals(0, character.getFriends().size(), "Oh no! He has no friends!  :(  ");
	}

	@Test
	void test_parseResponse_OK_listOfFriends()
			throws GraphQLResponseParseException, IOException, GraphQLRequestPreparationException {
		// Preparation

		// The response should contain id and name
		ObjectResponse objectResponse = queryType.getHeroResponseBuilder()
				.withQueryResponseDef(" { id appearsIn friends { name } } ")
				// .withField("id").withField("appearsIn").withSubObject(new Builder(Character.class,
				// "friends").build())//
				// .withField("name")
				.build();

		String rawResponse = "{\"data\":{\"hero\":{\"__typename\": \"Droid\", \"friends\":[{\"name\":\"name350518\", \"__typename\": \"Human\"},{\"name\":\"name381495\", \"__typename\": \"Droid\"}]}}}";

		// Go, go, go
		QueryType response = parseResponseForStarWarsSchema(rawResponse, objectResponse, QueryType.class);

		// Verification
		Character hero = response.getHero();
		assertTrue(hero instanceof Character, "response instanceof Character");
		Character character = hero;
		assertEquals(Droid.class.getName(), character.getClass().getName());
		assertNotNull(character.getFriends(), "He has perhaps has friends...");
		assertEquals(2, character.getFriends().size(), "Cool! He has 2 friends!  :)  ");
		assertEquals("name350518", character.getFriends().get(0).getName(), "First friend's name");
		assertEquals(Human.class.getName(), character.getFriends().get(0).getClass().getName());
		assertEquals("name381495", character.getFriends().get(1).getName(), "Second friend's name");
		assertEquals(Droid.class.getName(), character.getFriends().get(1).getClass().getName());
	}

	/**
	 * Parse the GraphQL server response, and map it to the objects, generated from the GraphQL schema.
	 * 
	 * @param <T>
	 * 
	 * @param rawResponse
	 * @param objectResponse
	 * @return
	 * @throws GraphQLResponseParseException
	 * @throws IOException
	 */
	<T> T parseResponseForStarWarsSchema(String rawResponse, ObjectResponse objectResponse, Class<T> valueType)
			throws GraphQLResponseParseException, IOException {

		// Let's read this response with Jackson
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(rawResponse);

		// The main node should be unique, named data, and be a container
		if (node.size() != 1)
			throw new GraphQLResponseParseException(
					"The response should contain one root element, but it contains " + node.size() + " elements");

		JsonNode data = node.get("data");
		if (data == null)
			throw new GraphQLResponseParseException("Could not retrieve the 'data' node");

		return mapper.treeToValue(data, valueType);
	}

}
