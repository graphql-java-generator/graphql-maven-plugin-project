package graphql.java.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import graphql.java.client.domain.Episode;
import graphql.java.client.request.InputParameter;
import graphql.java.client.request.ResponseDefinition;
import graphql.java.client.request.ResponseDefinitionImpl;

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

	@Test
	void test_execute() {
		fail("Not yet implemented");
	}

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
		ID id = new ID("1");
		List<InputParameter> parameters = new ArrayList<>();
		parameters.add(new InputParameter("id", id));

		// The response should contain id and name
		ResponseDefinition responseDef = new ResponseDefinitionImpl(QueryExecutor.GRAPHQL_MARKER);
		responseDef.addResponseField("id");
		responseDef.addResponseField("name");

		// Go, go, go
		String request = queryExecutorImpl.buildRequest(queryName, parameters, responseDef);

		// Verification
		assertEquals("{hero(id: \"1\") {id name}}", request);
	}

	/**
	 * Build a request with one parameter (ID), and a {@link Character} as the response.
	 */
	@Test
	void test_buildRequest_EpisodeID_characters() {
		// Preparation
		String queryName = "hero";
		ID id = new ID("this is an id");
		List<InputParameter> parameters = new ArrayList<>();
		parameters.add(new InputParameter("episode", Episode.NEWHOPE));
		parameters.add(new InputParameter("id", id));

		// The response should contain id and name
		ResponseDefinition responseDef = new ResponseDefinitionImpl(QueryExecutor.GRAPHQL_MARKER);
		responseDef.addResponseField("id");
		responseDef.addResponseField("name");

		// Go, go, go
		String request = queryExecutorImpl.buildRequest(queryName, parameters, responseDef);

		// Verification
		assertEquals("{hero(episode: NEWHOPE, id: \"this is an id\") {id name}}", request);
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
		ResponseDefinition responseDef = new ResponseDefinitionImpl(QueryExecutor.GRAPHQL_MARKER);
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
}
