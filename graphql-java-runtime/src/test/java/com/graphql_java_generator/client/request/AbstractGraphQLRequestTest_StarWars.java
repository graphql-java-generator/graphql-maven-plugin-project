package com.graphql_java_generator.client.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.graphql_java_generator.client.domain.starwars.Character;
import com.graphql_java_generator.client.domain.starwars.GraphQLRequest;
import com.graphql_java_generator.client.domain.starwars.Human;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

class AbstractGraphQLRequestTest_StarWars {

	@Test
	void testWithField_Simple_OK() throws GraphQLRequestPreparationException {
		// Go, go, go
		GraphQLRequest graphQLRequest = new GraphQLRequest(
				"{human(id:\"00000000-0000-0000-0000-000000000031\") {name}}");

		// Verification
		assertEquals("query", graphQLRequest.getQuery().name);
		assertEquals(1, graphQLRequest.getQuery().fields.size(), "nb queries");

		QueryField human = graphQLRequest.getQuery().fields.get(0);
		assertEquals("human", human.name);
		assertEquals(null, human.alias);
		assertEquals(Human.class, human.clazz, "name");
		assertEquals(2, human.fields.size(), "nb fields (with __typename)");
		int i = 0;
		assertEquals("name", human.fields.get(i).name, "name");
		assertEquals(null, human.fields.get(i).alias, "alias");
		assertEquals(0, human.fields.get(i).fields.size(), "sub fields");
		assertEquals(String.class, human.fields.get(i).clazz, "name");
		i += 1;
		assertEquals("__typename", human.fields.get(i).name, "name");
		assertEquals(null, human.fields.get(i).alias, "alias");
		assertEquals(0, human.fields.get(i).fields.size(), "sub fields");
		assertEquals(String.class, human.fields.get(i).clazz, "name");
	}

	@Test
	void test_AbstractGraphQLRequest_oneQuery()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		String query1 = "  \n\r\t  {human(id:\"00000000-0000-0000-0000-000000000031\") {id name friends {name appearsIn}}}  \n\r\t ";
		String query2 = "  \n\r\t query \n\r\t  {human(id:&theHumanId) {id name friends {name appearsIn}}}  \n\r\t ";

		String expected = "{\"query\":\""//
				+ "query{human(id:\\\"00000000-0000-0000-0000-000000000031\\\") {id name __typename friends {name appearsIn __typename}}}\"" //
				+ ",\"variables\":null,\"operationName\":null}";

		Map<String, Object> params = new HashMap<>();
		params.put("theHumanId", "00000000-0000-0000-0000-000000000031");

		// Go, go, go
		String request1 = new GraphQLRequest(query1).buildRequest(null);
		String request2 = new GraphQLRequest(query2).buildRequest(params);

		// Verification
		assertEquals(expected, request1);
		assertEquals(expected, request2);
	}

	@Test
	void test_AbstractGraphQLRequest_introspectionQuery()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		String query1 = "  \n\r\t   {__schema {types{kind name}}}  \n\r\t ";
		String query2 = "  \n\r\t query \n\r\t  {__schema {types{kind name}}} \n\r\t ";

		String expected = "{\"query\":\"query{__schema {types{kind name}}}\",\"variables\":null,\"operationName\":null}";

		// Go, go, go
		String request1 = new GraphQLRequest(query1).buildRequest(null);
		String request2 = new GraphQLRequest(query2).buildRequest(null);

		// Verification
		assertEquals(expected, request1);
		assertEquals(expected, request2);
	}

	@Test
	void testWithField_KO_FieldPresentTwoTimes() throws GraphQLRequestPreparationException {
		GraphQLRequestPreparationException e;

		// Go, go go
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new GraphQLRequest("{human(id:\"00000000-0000-0000-0000-000000000031\") {id name name}}"));
		assertTrue(e.getMessage().contains("<name>"));

		e = assertThrows(GraphQLRequestPreparationException.class, () -> new GraphQLRequest(
				"{human(id:\"00000000-0000-0000-0000-000000000031\") {id name validAlias:name}}"));
		assertTrue(e.getMessage().contains("<name>"));
	}

	@Test
	void testWithField_KO_NonExistingField() throws GraphQLRequestPreparationException {
		GraphQLRequestPreparationException e;

		// Go, go go
		e = assertThrows(GraphQLRequestPreparationException.class, () -> new GraphQLRequest(
				"{human(id:\"00000000-0000-0000-0000-000000000031\") {id name itDoesntExist}}"));
		assertTrue(e.getMessage().contains("<itDoesntExist>"));

		e = assertThrows(GraphQLRequestPreparationException.class, () -> new GraphQLRequest(
				"{human(id:\"00000000-0000-0000-0000-000000000031\") {id name validAlias:itDoesntExist}}"));
		assertTrue(e.getMessage().contains("<itDoesntExist>"));
	}

	@Test
	void testWithField_KO_InvalidIdentifier() throws GraphQLRequestPreparationException {
		GraphQLRequestPreparationException e;

		// Most important check: only attribute of the clazz class may be added.
		e = assertThrows(GraphQLRequestPreparationException.class, () -> new GraphQLRequest(
				"{human(id:\"00000000-0000-0000-0000-000000000031\") {id name fieldNotInThisClass}}"));
		assertTrue(e.getMessage().contains("fieldNotInThisClass"));

		// Various types of checks, for invalid identifiers
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new GraphQLRequest("{human(id:\"00000000-0000-0000-0000-000000000031\") {id name qdqd'qdsq}}"));
		assertTrue(e.getMessage().contains("qdqd'qdsq"));

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new GraphQLRequest("{human(id:\"00000000-0000-0000-0000-000000000031\") {id name qdqd.qdsq}}"));
		assertTrue(e.getMessage().contains("qdqd.qdsq"));

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new GraphQLRequest("{human(id:\"00000000-0000-0000-0000-000000000031\") {id name qdqdqdsq.}}"));
		assertTrue(e.getMessage().contains("qdqdqdsq."));

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new GraphQLRequest("{human(id:\"00000000-0000-0000-0000-000000000031\") {id name .qdqdqdsq}}"));
		assertTrue(e.getMessage().contains(".qdqdqdsq"));

		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new GraphQLRequest("{human(id:\"00000000-0000-0000-0000-000000000031\") {id name qdqdqdsq*}}"));
		assertTrue(e.getMessage().contains("qdqdqdsq*"));
	}

	@Test
	void testWithFieldWithAlias_OK() throws GraphQLRequestPreparationException {
		// Go, go, go
		GraphQLRequest graphQLRequest = new GraphQLRequest(
				"{human(id:\"00000000-0000-0000-0000-000000000031\") {alias:name}}");

		// Verification
		assertEquals("human", graphQLRequest.getQuery().name);
		assertEquals(2, graphQLRequest.getQuery().fields.size(), "nb fields (with __typename)");
		assertEquals("human", graphQLRequest.getQuery().name);
		assertEquals(2, graphQLRequest.getQuery().fields.size(), "nb fields (with __typename)");
		assertEquals("id", graphQLRequest.getQuery().fields.get(0).name, "name");
		assertEquals("idAlias", graphQLRequest.getQuery().fields.get(0).alias, "alias");
	}

	@Test
	void testWithFieldWithAlias_KO() throws GraphQLRequestPreparationException {
		// KO on the name
		GraphQLRequestPreparationException e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new GraphQLRequest(
						"{human(id:\"00000000-0000-0000-0000-000000000031\") {validAlias:notAnExistingAttribute}}"));
		assertTrue(e.getMessage().contains("notAnExistingAttribute"));

		// KO on the alias
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new GraphQLRequest("{human(id:\"00000000-0000-0000-0000-000000000031\") {qdqd/qdsq:id}}"));
		assertTrue(e.getMessage().contains("qdqd/qdsq"));
	}

	@Test
	void testWithSubObject_OK() throws GraphQLRequestPreparationException {
		// Go, go, go
		GraphQLRequest graphQLRequest = new GraphQLRequest(
				"{human(id:\"00000000-0000-0000-0000-000000000031\") \n\r\t   {\n\r\t  friends  \n\r\t  {  \n\r\t  id \n\r\t  name\n\r\t  } \n\r\t }  \n\r\t } \n\r\t ");

		// Verification
		assertEquals(1, graphQLRequest.getQuery().fields.size(), "one query in the list");
		//
		QueryField human = graphQLRequest.getQuery().fields.get(0);
		assertEquals("human", human.name, "query fieldName");
		assertEquals(null, human.alias, "query fieldAlias");
		assertEquals(Human.class, human.clazz, "query clazz");
		assertEquals(2, human.fields.size());
		assertEquals("friends", human.fields.get(0).name);
		assertEquals(null, human.alias, "subobject fieldAlias");
		assertEquals(Human.class, human.clazz, "subobject clazz");
		assertEquals("__typename", human.fields.get(1).name);
		//
		QueryField friends = human.fields.get(0);
		assertEquals("friends", friends.name, "subobject fieldName");
		assertEquals(null, friends.alias, "subobject fieldAlias");
		assertEquals(Character.class, friends.clazz, "subobject clazz");
		assertEquals(3, friends.fields.size());
		int i = 0;
		assertEquals("id", friends.fields.get(i++).name);
		assertEquals("name", friends.fields.get(i++).name);
		assertEquals("__typename", friends.fields.get(i++).name);
	}

	@Test
	void testWithSubObject_KO_fieldPresentTwoTimes() throws GraphQLRequestPreparationException {
		// Preparation
		GraphQLRequestPreparationException e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new GraphQLRequest(
						"{human(id:\"00000000-0000-0000-0000-000000000031\"){friends{name} friends{id}}"));
		assertTrue(e.getMessage().contains("<friends>"));
	}

	@Test
	void testWithSubObject_withAlias_OK() throws GraphQLRequestPreparationException {
		// Go, go, go
		GraphQLRequest graphQLRequest = new GraphQLRequest(
				"{human(id:\"00000000-0000-0000-0000-000000000031\"){aValidAlias:friends{name id}}}");

		// Verification
		assertEquals(1, graphQLRequest.getQuery().fields.size(), "one query in the list");
		//
		QueryField human = graphQLRequest.getQuery().fields.get(0);
		assertEquals("human", human.name, "query fieldName");
		assertEquals(null, human.alias, "query fieldAlias");
		assertEquals(Human.class, human.clazz, "query clazz");
		assertEquals(2, human.fields.size());
		//
		QueryField friends = human.fields.get(0);
		assertEquals("friends", friends.name);
		assertEquals("aValidAlias", friends.alias, "subobject fieldAlias");
		assertEquals(Character.class, friends.clazz, "subobject clazz");
		assertEquals("__typename", human.fields.get(1).name);
		//
		assertEquals(3, friends.fields.size());
		int i = 0;
		assertEquals("name", friends.fields.get(i++).name);
		assertEquals("id", friends.fields.get(i++).name);
		assertEquals("__typename", friends.fields.get(i++).name);
	}

	// /**
	// * When requesting a non scalar field F, without specifying subfields, than all F's scalar fields are
	// automatically
	// * added
	// */
	// @Test
	// public void test_withQueryResponseDef_emptyQuery() throws GraphQLRequestPreparationException {
	//
	// // Go, go, go
	// humanResponseDefBuilder.withQueryResponseDef("");
	//
	// // Verification
	// // Verification
	// assertEquals(5, humanResponseDefBuilder.objectResponse.scalarFields.size(),
	// "all scalar fields (with __typename)");
	// //
	// // field name check
	// int i = 0;
	// assertEquals("id", humanResponseDefBuilder.objectResponse.scalarFields.get(i++).name);
	// assertEquals("name", humanResponseDefBuilder.objectResponse.scalarFields.get(i++).name);
	// assertEquals("appearsIn", humanResponseDefBuilder.objectResponse.scalarFields.get(i++).name);
	// assertEquals("homePlanet", humanResponseDefBuilder.objectResponse.scalarFields.get(i++).name);
	// assertEquals("__typename", humanResponseDefBuilder.objectResponse.scalarFields.get(i++).name);
	//
	// // No non scalar
	// assertEquals(0, humanResponseDefBuilder.objectResponse.subObjects.size(), "no non scalar fields");
	// }
	//
	// @Test
	// public void test_withQueryResponseDef_nullQuery() throws GraphQLRequestPreparationException {
	//
	// // Go, go, go
	// humanResponseDefBuilder.withQueryResponseDef(null);
	//
	// // Verification
	// // Verification
	// assertEquals(5, humanResponseDefBuilder.objectResponse.scalarFields.size(),
	// "all scalar fields (with __typename)");
	// //
	// // field name check
	// int i = 0;
	// assertEquals("id", humanResponseDefBuilder.objectResponse.scalarFields.get(i++).name);
	// assertEquals("name", humanResponseDefBuilder.objectResponse.scalarFields.get(i++).name);
	// assertEquals("appearsIn", humanResponseDefBuilder.objectResponse.scalarFields.get(i++).name);
	// assertEquals("homePlanet", humanResponseDefBuilder.objectResponse.scalarFields.get(i++).name);
	// assertEquals("__typename", humanResponseDefBuilder.objectResponse.scalarFields.get(i++).name);
	//
	// // No non scalar
	// assertEquals(0, humanResponseDefBuilder.objectResponse.subObjects.size(), "no non scalar fields");
	// }
	//
	// @Test
	// public void test_withQueryResponseDef_StarWars() throws GraphQLRequestPreparationException {
	//
	// // Go, go, go
	// humanResponseDefBuilder
	// .withQueryResponseDef("{id friends{ id nameAlias:name amis : friends{id name} appearsIn} name } ");
	//
	// // Verification
	// ObjectResponse respDef = humanResponseDefBuilder.build();
	// assertEquals("human", respDef.field.name, "field name");
	// //
	// assertEquals(3, respDef.scalarFields.size(), "nb scalar fields (with the added __typename field)");
	// assertEquals("id", respDef.scalarFields.get(0).name, "name scalarFields 0");
	// assertNull(respDef.scalarFields.get(0).alias, "alias scalarFields 0");
	// assertEquals("name", respDef.scalarFields.get(1).name, "name scalarFields 1");
	// assertNull(respDef.scalarFields.get(1).alias, "alias scalarFields 1");
	// //
	// assertEquals(1, respDef.subObjects.size(), "nb subobjects");
	//
	// ObjectResponse friends1 = respDef.subObjects.get(0);
	// assertEquals("friends", friends1.field.name);
	// assertEquals(null, friends1.field.alias);
	// //
	// assertEquals(4, friends1.scalarFields.size(), "friends1: nb scalar fields (with the added __typename field)");
	// assertEquals("id", friends1.scalarFields.get(0).name, "friends1: name scalarFields 0");
	// assertNull(friends1.scalarFields.get(0).alias, "friends1: alias scalarFields 0");
	// assertEquals("name", friends1.scalarFields.get(1).name, "friends1: name scalarFields 1");
	// assertEquals("nameAlias", friends1.scalarFields.get(1).alias, "friends1: alias scalarFields 1");
	// assertEquals("appearsIn", friends1.scalarFields.get(2).name, "friends1: name scalarFields 2");
	// assertNull(friends1.scalarFields.get(2).alias, "friends1: alias scalarFields 2");
	// //
	// assertEquals(1, friends1.subObjects.size(), "friends1: nb subobjects");
	//
	// ObjectResponse friends2 = friends1.subObjects.get(0);
	// assertEquals("friends", friends2.field.name);
	// assertEquals("amis", friends2.field.alias);
	// //
	// assertEquals(3, friends2.scalarFields.size(), " (with the added __typename field)");
	// assertEquals("id", friends2.scalarFields.get(0).name);
	// assertNull(friends2.scalarFields.get(0).alias);
	// assertEquals("name", friends2.scalarFields.get(1).name);
	// assertNull(friends2.scalarFields.get(1).alias);
	// //
	// assertEquals(0, friends2.subObjects.size());
	// }
	//
	// @Test
	// public void test_withQueryResponseDef_withHardCodedParameters_Forum()
	// throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
	// // Go, go, go
	// String queryResponseDef = "{id name publiclyAvailable "
	// + " topics{id date author{id name email type} nbPosts posts(memberName: \"Me!\", since: ?sinceParam) {date
	// author{name email type}}}}";
	//
	// ObjectResponse response = new com.graphql_java_generator.client.domain.forum.QueryType(
	// "http://localhost:8180/graphql").getBoardsResponseBuilder().withQueryResponseDef(queryResponseDef).build();
	//
	// // Verifications
	// assertEquals("boards", response.fieldName);
	// assertEquals(Board.class, response.getFieldClass());
	// assertNull(response.fieldAlias);
	// assertNull(response.getFieldAlias());
	//
	// assertEquals(5, response.scalarFields.size(), " (with the added __typename field)");
	// int i = 0;
	// assertEquals("id", response.scalarFields.get(i++).name);
	// assertEquals("name", response.scalarFields.get(i++).name);
	// assertEquals("publiclyAvailable", response.scalarFields.get(i++).name);
	//
	// assertEquals(1, response.subObjects.size());
	// assertEquals("topics", response.subObjects.get(0).getFieldName());
	//
	// // topics has two sub-objects: author and posts
	// assertEquals(2, response.subObjects.get(0).subObjects.size());
	// assertEquals("author", response.subObjects.get(0).subObjects.get(0).getFieldName());
	// assertEquals("posts", response.subObjects.get(0).subObjects.get(1).getFieldName());
	//
	// // Check of the input parameters
	// List<InputParameter> postsInputParameters = response.subObjects.get(0).subObjects.get(1).getInputParameters();
	// assertEquals(2, postsInputParameters.size());
	// i = 0;
	// // First parameter is hard coded
	// assertEquals("memberName", postsInputParameters.get(i).getName());
	// assertEquals("Me!", postsInputParameters.get(i).getValue());
	// assertEquals("\\\"Me!\\\"", postsInputParameters.get(i).getValueForGraphqlQuery(null));
	// assertEquals(null, postsInputParameters.get(i).bindParameterName);
	// assertTrue(postsInputParameters.get(i).mandatory);
	// i = 1;
	// // The second parameter is a bind variable
	// Map<String, Object> bindParameterValues = new HashMap<>();
	// bindParameterValues.put("sinceParam", new GregorianCalendar(1903, 02 - 1, 1).getTime());
	// assertEquals("since", postsInputParameters.get(i).getName());
	// assertEquals(null, postsInputParameters.get(i).getValue());
	// assertEquals("\\\"1903-02-01\\\"", postsInputParameters.get(i).getValueForGraphqlQuery(bindParameterValues));
	// assertEquals("sinceParam", postsInputParameters.get(i).bindParameterName);
	// assertFalse(postsInputParameters.get(i).mandatory);
	// }
	//
	// @Test
	// public void test_withQueryResponseDef_withBindVariableParameters_Forum()
	// throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
	// // Preparation
	// Map<String, Object> map = new HashMap<>();
	// map.put("memberName", "a member Name");
	// map.put("sinceParam", "1900/10/24");
	//
	// // Go, go, go
	// String queryResponseDef = "{id name publiclyAvailable topics{id date author{id name email type} nbPosts "
	// + "posts(memberName: ?memberName, since: &sinceParam) {date author{name email type}}}}";
	// ObjectResponse response = new com.graphql_java_generator.client.domain.forum.QueryType(
	// "http://localhost:8180/graphql").getBoardsResponseBuilder().withQueryResponseDef(queryResponseDef)
	// .build();
	//
	// // Verifications
	// assertEquals("boards", response.fieldName);
	// assertEquals(Board.class, response.getFieldClass());
	// assertNull(response.fieldAlias);
	// assertNull(response.getFieldAlias());
	//
	// assertEquals(4, response.scalarFields.size(), " (with the added __typename field)");
	// int i = 0;
	// assertEquals("id", response.scalarFields.get(i++).name);
	// assertEquals("name", response.scalarFields.get(i++).name);
	// assertEquals("publiclyAvailable", response.scalarFields.get(i++).name);
	//
	// assertEquals(1, response.subObjects.size());
	// assertEquals("topics", response.subObjects.get(0).getFieldName());
	//
	// // topics has two sub-objects: author and posts
	// assertEquals(2, response.subObjects.get(0).subObjects.size());
	// assertEquals("author", response.subObjects.get(0).subObjects.get(0).getFieldName());
	// assertEquals("posts", response.subObjects.get(0).subObjects.get(1).getFieldName());
	//
	// // Check of the input parameters
	// List<InputParameter> postsInputParameters = response.subObjects.get(0).subObjects.get(1).getInputParameters();
	// assertEquals(2, postsInputParameters.size());
	// i = 0;
	// // First parameter is hard coded
	// assertEquals("memberName", postsInputParameters.get(i).getName());
	// assertEquals(null, postsInputParameters.get(i).getValue());
	// assertEquals("\\\"a member Name\\\"", postsInputParameters.get(i).getValueForGraphqlQuery(map));
	// assertEquals("memberName", postsInputParameters.get(i).bindParameterName);
	// assertFalse(postsInputParameters.get(i).mandatory);
	// i = 1;
	// // The second parameter is a bind variable
	// Map<String, Object> bindParameterValues = new HashMap<>();
	// bindParameterValues.put("sinceParam", new GregorianCalendar(2020, 5 - 1, 3).getTime());
	// assertEquals("since", postsInputParameters.get(i).getName());
	// assertEquals(null, postsInputParameters.get(i).getValue());
	// assertEquals("\\\"2020-05-03\\\"", postsInputParameters.get(i).getValueForGraphqlQuery(bindParameterValues));
	// assertEquals("sinceParam", postsInputParameters.get(i).bindParameterName);
	// assertTrue(postsInputParameters.get(i).mandatory);
	// }
	//
	// @Test
	// public void test_withQueryResponseDef_KO() throws GraphQLRequestPreparationException {
	// GraphQLRequestPreparationException e;
	//
	// // Errors with { or }
	// humanResponseDefBuilder = new Builder(QueryType.class, "human");
	// assertThrows(GraphQLRequestPreparationException.class,
	// () -> humanResponseDefBuilder
	// .withQueryResponseDef("id friends{ id nameAlias:name amis : friends{id name} appearsIn} name "),
	// "missing a '{'");
	//
	// humanResponseDefBuilder = new Builder(QueryType.class, "human");
	// assertThrows(GraphQLRequestPreparationException.class, () -> humanResponseDefBuilder
	// .withQueryResponseDef("{id friends{ id nameAlias:name amis : friends{id name} appearsIn} name "),
	// "missing a '}'");
	//
	// humanResponseDefBuilder = new Builder(QueryType.class, "human");
	// assertThrows(GraphQLRequestPreparationException.class,
	// () -> humanResponseDefBuilder.withQueryResponseDef(
	// "{id friends{ id nameAlias:name amis : friends{id name}{id} appearsIn} name "),
	// "a { without leading field name");
	//
	// humanResponseDefBuilder = new Builder(QueryType.class, "human");
	// assertThrows(GraphQLRequestPreparationException.class,
	// () -> humanResponseDefBuilder.withQueryResponseDef("{id friends name "),
	// "No field definition for friends");
	//
	// humanResponseDefBuilder = new Builder(QueryType.class, "human");
	// assertThrows(GraphQLRequestPreparationException.class,
	// () -> humanResponseDefBuilder.withQueryResponseDef("{id friends{} name "),
	// "Empty field definition for friends");
	//
	// // Field present two times
	// humanResponseDefBuilder = new Builder(QueryType.class, "human");
	// e = assertThrows(GraphQLRequestPreparationException.class, () -> humanResponseDefBuilder
	// .withQueryResponseDef("{id friends{ id nameAlias:name amis : friends{id name} appearsIn} name id } "));
	// assertTrue(e.getMessage().contains("<id>"), e.getMessage());
	//
	// // Wrong field name
	// humanResponseDefBuilder = new Builder(QueryType.class, "human");
	// e = assertThrows(GraphQLRequestPreparationException.class, () -> humanResponseDefBuilder.withQueryResponseDef(
	// "{id friends{ id nameAlias:name amis : friends{id notAFieldName} appearsIn} name } "));
	// assertTrue(e.getMessage().contains("<notAFieldName>"), e.getMessage());
	//
	// // Bad alias name
	// humanResponseDefBuilder = new Builder(QueryType.class, "human");
	// e = assertThrows(GraphQLRequestPreparationException.class, () -> humanResponseDefBuilder.withQueryResponseDef(
	// "{id friends{ id name*Alias:name amis : friends{id notAFieldName} appearsIn} name } "));
	// assertTrue(e.getMessage().contains("<name*Alias>"), e.getMessage());
	//
	// // We're not ready yet for field parameters
	// humanResponseDefBuilder = new Builder(QueryType.class, "human");
	// e = assertThrows(GraphQLRequestPreparationException.class,
	// () -> humanResponseDefBuilder.withQueryResponseDef(
	// "{id friends{ id(since) nameAlias:name amis : friends{id name} appearsIn} name "),
	// "missing a '}'");
	// assertTrue(e.getMessage().contains("("), e.getMessage());
	// }
	//
	// @Test
	// void testBuild_NoFields() throws GraphQLRequestPreparationException {
	// // Go, go, go
	// ObjectResponse resp = humanResponseDefBuilder.build();
	//
	// // Verification
	// assertEquals(5, resp.scalarFields.size(), "all scalar fields (with the added __typename field)");
	// //
	// // field name check
	// int i = 0;
	// assertEquals("id", resp.scalarFields.get(i++).name);
	// assertEquals("name", resp.scalarFields.get(i++).name);
	// assertEquals("appearsIn", resp.scalarFields.get(i++).name);
	// assertEquals("homePlanet", resp.scalarFields.get(i++).name);
	// assertEquals("__typename", resp.scalarFields.get(i++).name);
	//
	// // No non scalar
	// assertEquals(0, resp.subObjects.size(), "no non scalar fields");
	// }
	//
	// @Test
	// void testBuild_scalarInputParameters() throws GraphQLRequestPreparationException {
	// // Go, go, go
	// MyQueryType queryType = new MyQueryType("http://localhost");
	// ObjectResponse objectResponse = queryType.getABreakResponseBuilder()
	// .withQueryResponseDef("{case(test: DOUBLE)}").build();
	//
	// // Verification
	// assertEquals(2, objectResponse.scalarFields.size(), " (with the added __typename field)");
	//
	// Field field = objectResponse.scalarFields.get(0);
	// assertEquals("case", field.name);
	// assertEquals(1, field.inputParameters.size());
	// assertEquals("test", field.inputParameters.get(0).getName());
	// assertEquals(_extends.DOUBLE, field.inputParameters.get(0).getValue());
	// }

	@Test
	void testAbstractGraphQLRequest() {
		fail("Not yet finished");
	}
}
