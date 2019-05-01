package graphql.java.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import graphql.java.client.domain.Character;
import graphql.java.client.domain.CharacterImpl;
import graphql.java.client.domain.Episode;
import graphql.java.client.request.InputParameter;
import graphql.java.client.request.ResponseDefinition;
import graphql.java.client.request.ResponseDefinition;
import graphql.java.client.response.GraphQLResponseParseException;

/**
 * 
 * @author EtienneSF
 */
class QueryExecutorImplTest {

	QueryExecutorImpl queryExecutorImpl;

	@BeforeEach
	void setUp() throws Exception {
		queryExecutorImpl = new QueryExecutorImpl();
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
	 */
	@Test
	void test_buildRequest_ID_characters() {
		// Preparation
		String queryName = "hero";
		String id = "1";
		List<InputParameter> parameters = new ArrayList<>();
		parameters.add(new InputParameter("id", id));

		// The response should contain id and name
		ResponseDefinition responseDef = new ResponseDefinition(QueryExecutor.GRAPHQL_MARKER);
		responseDef.addResponseField("id");
		responseDef.addResponseField("name");

		// Go, go, go
		String request = queryExecutorImpl.buildRequest(queryName, parameters, responseDef);

		// Verification
		assertEquals("{hero(id: \\\"1\\\") {id name}}", request);
	}

	/**
	 * Build a request with one parameter (ID), and a {@link Character} as the response.
	 */
	@Test
	void test_buildRequest_EpisodeID_characters() {
		// Preparation
		String queryName = "hero";
		String id = "this is an id";
		List<InputParameter> parameters = new ArrayList<>();
		parameters.add(new InputParameter("episode", Episode.NEWHOPE));
		parameters.add(new InputParameter("id", id));

		// The response should contain id and name
		ResponseDefinition responseDef = new ResponseDefinition(QueryExecutor.GRAPHQL_MARKER);
		responseDef.addResponseField("id");
		responseDef.addResponseField("name");

		// Go, go, go
		String request = queryExecutorImpl.buildRequest(queryName, parameters, responseDef);

		// Verification
		assertEquals("{hero(episode: NEWHOPE, id: \\\"this is an id\\\") {id name}}", request);
	}

	/**
	 * Build a request with one parameter (ID), and a {@link Character} as the response.
	 */
	@Test
	void test_buildRequest_Episode_idNameAppearsInFriendsName() {
		// Preparation
		String queryName = "hero";
		List<InputParameter> parameters = new ArrayList<>();
		parameters.add(new InputParameter("episode", Episode.NEWHOPE));

		// The response should contain id and name
		ResponseDefinition responseDef = new ResponseDefinition(QueryExecutor.GRAPHQL_MARKER);
		responseDef.addResponseField("id");
		responseDef.addResponseField("name");
		responseDef.addResponseField("appearsIn");
		ResponseDefinition subResponseDef = responseDef.addResponseEntity("friends");
		subResponseDef.addResponseField("name");

		// Go, go, go
		String request = queryExecutorImpl.buildRequest(queryName, parameters, responseDef);

		// Verification
		assertEquals("{hero(episode: NEWHOPE) {id name appearsIn friends{name}}}", request);
	}

	@Test
	void test_parseResponse_KO() {
		// Preparation
		Exception exception;
		String queryName = "hero";
		List<InputParameter> parameters = new ArrayList<>();
		parameters.add(new InputParameter("episode", Episode.NEWHOPE));
		// The response should contain id and name
		ResponseDefinition responseDef = new ResponseDefinition(QueryExecutor.GRAPHQL_MARKER);
		responseDef.addResponseField("id");
		responseDef.addResponseField("name");
		responseDef.addResponseField("appearsIn");
		ResponseDefinition subResponseDef = responseDef.addResponseEntity("friends");
		subResponseDef.addResponseField("name");

		assertThrows(NullPointerException.class, () -> queryExecutorImpl.parseResponse(null, "queryName",
				new ResponseDefinition(QueryExecutor.GRAPHQL_MARKER), Character.class));

		exception = assertThrows(JsonParseException.class, () -> queryExecutorImpl.parseResponse("invalid JSON",
				queryName, new ResponseDefinition(QueryExecutor.GRAPHQL_MARKER), Character.class));
		assertTrue(exception.getMessage().contains("invalid"));

		exception = assertThrows(GraphQLResponseParseException.class, () -> queryExecutorImpl.parseResponse(
				"{\"wrongTag\":{\"hero\":{\"id\":\"An id\",\"name\":\"A hero's name\",\"appearsIn\":[\"NEWHOPE\",\"JEDI\"],\"friends\":null}}}",
				queryName, new ResponseDefinition(QueryExecutor.GRAPHQL_MARKER), Character.class));
		assertTrue(exception.getMessage().contains("'data'"));

		exception = assertThrows(GraphQLResponseParseException.class, () -> queryExecutorImpl.parseResponse(
				"{\"data\":{\"wrongAlias\":{\"id\":\"An id\",\"name\":\"A hero's name\",\"appearsIn\":[\"NEWHOPE\",\"JEDI\"],\"friends\":null}}}",
				queryName, new ResponseDefinition(QueryExecutor.GRAPHQL_MARKER), Character.class));
		assertTrue(exception.getMessage().contains("'hero'"));

		exception = assertThrows(UnrecognizedPropertyException.class, () -> queryExecutorImpl.parseResponse(
				"{\"data\":{\"hero\":{\"wrongTag\":\"An id\",\"name\":\"A hero's name\",\"appearsIn\":[\"NEWHOPE\",\"JEDI\"],\"friends\":null}}}",
				queryName, new ResponseDefinition(QueryExecutor.GRAPHQL_MARKER), CharacterImpl.class));
		assertTrue(exception.getMessage().contains("wrongTag"));

		exception = assertThrows(InvalidFormatException.class, () -> queryExecutorImpl.parseResponse(
				"{\"data\":{\"hero\":{\"id\":\"An id\",\"name\":\"A hero's name\",\"appearsIn\":[\"WRONG_EPISODE\",\"JEDI\"],\"friends\":null}}}",
				queryName, new ResponseDefinition(QueryExecutor.GRAPHQL_MARKER), CharacterImpl.class));
		assertTrue(exception.getMessage().contains("WRONG_EPISODE"));
	}

	@Test
	void test_parseResponse_OK_noFriends() throws GraphQLResponseParseException, IOException {
		// Preparation
		String queryName = "hero";
		List<InputParameter> parameters = new ArrayList<>();
		parameters.add(new InputParameter("episode", Episode.NEWHOPE));

		// The response should contain id and name
		ResponseDefinition responseDef = new ResponseDefinition(QueryExecutor.GRAPHQL_MARKER);
		responseDef.addResponseField("id");
		responseDef.addResponseField("name");
		responseDef.addResponseField("appearsIn");
		ResponseDefinition subResponseDef = responseDef.addResponseEntity("friends");
		subResponseDef.addResponseField("name");

		String rawResponse = "{\"data\":{\"hero\":{\"id\":\"An id\",\"name\":\"A hero's name\",\"appearsIn\":[\"NEWHOPE\",\"JEDI\"],\"friends\":null}}}";

		// Go, go, go
		Object response = queryExecutorImpl.parseResponse(rawResponse, queryName, responseDef, CharacterImpl.class);

		// Verification
		assertTrue(response instanceof Character, "response instanceof Character");
		Character character = (Character) response;
		assertEquals("An id", character.getId(), "id");
		assertEquals("A hero's name", character.getName(), "name");
		List<Episode> episodes = character.getAppearsIn();
		assertEquals(2, episodes.size(), "2 episodes");
		assertEquals(Episode.NEWHOPE, episodes.get(0), "First episode");
		assertEquals(Episode.JEDI, episodes.get(1), "Second episode");
		assertNull(character.getFriends(), "He has no friends!  :(  ");
	}

	@Test
	void test_parseResponse_OK_emptyListOfFriends() throws GraphQLResponseParseException, IOException {
		// Preparation
		String queryName = "hero";
		List<InputParameter> parameters = new ArrayList<>();
		parameters.add(new InputParameter("episode", Episode.NEWHOPE));

		// The response should contain id and name
		ResponseDefinition responseDef = new ResponseDefinition(QueryExecutor.GRAPHQL_MARKER);
		ResponseDefinition subResponseDef = responseDef.addResponseEntity("friends");
		subResponseDef.addResponseField("name");

		String rawResponse = "{\"data\":{\"hero\":{\"friends\":[]}}}";

		// Go, go, go
		Object response = queryExecutorImpl.parseResponse(rawResponse, queryName, responseDef, CharacterImpl.class);

		// Verification
		assertTrue(response instanceof Character, "response instanceof Character");
		Character character = (Character) response;
		assertNotNull(character.getFriends(), "He has perhaps has friends...");
		assertEquals(0, character.getFriends().size(), "Oh no! He has no friends!  :(  ");
	}

	@Test
	void test_parseResponse_OK_listOfFriends() throws GraphQLResponseParseException, IOException {
		// Preparation
		String queryName = "hero";

		// The response should contain id and name
		ResponseDefinition responseDef = new ResponseDefinition(QueryExecutor.GRAPHQL_MARKER);
		ResponseDefinition subResponseDef = responseDef.addResponseEntity("friends");
		subResponseDef.addResponseField("name");

		String rawResponse = "{\"data\":{\"hero\":{\"friends\":[{\"name\":\"name350518\"},{\"name\":\"name381495\"}]}}}";

		// Go, go, go
		Object response = queryExecutorImpl.parseResponse(rawResponse, queryName, responseDef, CharacterImpl.class);

		// Verification
		assertTrue(response instanceof Character, "response instanceof Character");
		Character character = (Character) response;
		assertNotNull(character.getFriends(), "He has perhaps has friends...");
		assertEquals(2, character.getFriends().size(), "Cool! He has 2 friends!  :)  ");
		assertEquals("name350518", character.getFriends().get(0).getName(), "First friend's name");
		assertEquals("name381495", character.getFriends().get(1).getName(), "Second friend's name");
	}
}
