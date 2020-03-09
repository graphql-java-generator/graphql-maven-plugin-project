package com.graphql_java_generator.client.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Board;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.QueryType;

class BuilderTest {

	@Test
	public void test_withQueryResponseDef_withHardCodedParameters_Forum()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Go, go, go
		String queryResponseDef = "{id name publiclyAvailable "
				+ " topics{id date author{id name email type} nbPosts posts(memberName: \"Me!\", since: ?sinceParam) {date author{name email type}}}}";
		ObjectResponse response = new QueryType("http://localhost:8180/graphql").getBoardsResponseBuilder()
				.withQueryResponseDef(queryResponseDef).build();

		// Verifications
		assertEquals("boards", response.field.name);
		assertEquals(Board.class, response.getFieldClass());
		assertNull(response.field.alias);

		assertEquals(4, response.scalarFields.size(), " (with the added __typename field)");
		int i = 0;
		assertEquals("id", response.scalarFields.get(i++).name);
		assertEquals("name", response.scalarFields.get(i++).name);
		assertEquals("publiclyAvailable", response.scalarFields.get(i++).name);

		assertEquals(1, response.subObjects.size());
		assertEquals("topics", response.subObjects.get(0).getFieldName());

		// topics has two sub-objects: author and posts
		assertEquals(2, response.subObjects.get(0).subObjects.size());
		assertEquals("author", response.subObjects.get(0).subObjects.get(0).getFieldName());
		assertEquals("posts", response.subObjects.get(0).subObjects.get(1).getFieldName());

		// Check of the input parameters
		List<InputParameter> postsInputParameters = response.subObjects.get(0).subObjects.get(1).getInputParameters();
		assertEquals(2, postsInputParameters.size());
		i = 0;
		// First parameter is hard coded
		assertEquals("memberName", postsInputParameters.get(i).getName());
		assertEquals("Me!", postsInputParameters.get(i).getValue());
		assertEquals("\\\"Me!\\\"", postsInputParameters.get(i).getValueForGraphqlQuery(null));
		assertEquals(null, postsInputParameters.get(i).bindParameterName);
		assertTrue(postsInputParameters.get(i).mandatory);
		i = 1;
		// The second parameter is a bind variable
		Map<String, Object> bindParameterValues = new HashMap<>();
		bindParameterValues.put("sinceParam", new Date(1903 - 1900, 02 - 1, 1));
		assertEquals("since", postsInputParameters.get(i).getName());
		assertEquals(null, postsInputParameters.get(i).getValue());
		assertEquals("\\\"1903-02-01\\\"", postsInputParameters.get(i).getValueForGraphqlQuery(bindParameterValues));
		assertEquals("sinceParam", postsInputParameters.get(i).bindParameterName);
		assertFalse(postsInputParameters.get(i).mandatory);
	}

	@Test
	public void test_withQueryResponseDef_withBindVariableParameters_Forum()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		Map<String, Object> map = new HashMap<>();
		map.put("memberName", "a member Name");
		map.put("sinceParam", "1900/10/24");

		// Go, go, go
		String queryResponseDef = "{id name publiclyAvailable topics{id date author{id name email type} nbPosts "
				+ "posts(memberName: ?memberName, since: &sinceParam) {date author{name email type}}}}";
		ObjectResponse response = new QueryType("http://localhost:8180/graphql").getBoardsResponseBuilder()
				.withQueryResponseDef(queryResponseDef).build();

		// Verifications
		assertEquals("boards", response.field.name);
		assertEquals(Board.class, response.getFieldClass());
		assertNull(response.field.alias);

		assertEquals(4, response.scalarFields.size(), " (with the added __typename field)");
		int i = 0;
		assertEquals("id", response.scalarFields.get(i++).name);
		assertEquals("name", response.scalarFields.get(i++).name);
		assertEquals("publiclyAvailable", response.scalarFields.get(i++).name);

		assertEquals(1, response.subObjects.size());
		assertEquals("topics", response.subObjects.get(0).getFieldName());

		// topics has two sub-objects: author and posts
		assertEquals(2, response.subObjects.get(0).subObjects.size());
		assertEquals("author", response.subObjects.get(0).subObjects.get(0).getFieldName());
		assertEquals("posts", response.subObjects.get(0).subObjects.get(1).getFieldName());

		// Check of the input parameters
		List<InputParameter> postsInputParameters = response.subObjects.get(0).subObjects.get(1).getInputParameters();
		assertEquals(2, postsInputParameters.size());
		i = 0;
		// First parameter is hard coded
		assertEquals("memberName", postsInputParameters.get(i).getName());
		assertEquals(null, postsInputParameters.get(i).getValue());
		assertEquals("\\\"a member Name\\\"", postsInputParameters.get(i).getValueForGraphqlQuery(map));
		assertEquals("memberName", postsInputParameters.get(i).bindParameterName);
		assertFalse(postsInputParameters.get(i).mandatory);
		i = 1;
		// The second parameter is a bind variable
		Map<String, Object> bindParameterValues = new HashMap<>();
		bindParameterValues.put("sinceParam", new Date(2020 - 1900, 5 - 1, 3));
		assertEquals("since", postsInputParameters.get(i).getName());
		assertEquals(null, postsInputParameters.get(i).getValue());
		assertEquals("\\\"2020-05-03\\\"", postsInputParameters.get(i).getValueForGraphqlQuery(bindParameterValues));
		assertEquals("sinceParam", postsInputParameters.get(i).bindParameterName);
		assertTrue(postsInputParameters.get(i).mandatory);
	}

}
