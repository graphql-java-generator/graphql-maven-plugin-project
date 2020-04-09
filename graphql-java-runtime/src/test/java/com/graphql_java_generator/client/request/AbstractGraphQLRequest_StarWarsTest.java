package com.graphql_java_generator.client.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.graphql_java_generator.client.domain.starwars.Character;
import com.graphql_java_generator.client.domain.starwars.GraphQLRequest;
import com.graphql_java_generator.client.domain.starwars.Human;
import com.graphql_java_generator.client.domain.starwars.QueryType;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

@Execution(ExecutionMode.CONCURRENT)
class AbstractGraphQLRequest_StarWarsTest {

	QueryType queryType;
	Builder humanResponseDefBuilder;
	Map<String, Object> paramsHuman;

	@BeforeEach
	void setup() throws GraphQLRequestPreparationException {
		queryType = new QueryType("http://localhost/graphql");
		humanResponseDefBuilder = queryType.getHumanResponseBuilder();
		paramsHuman = new HashMap<>();
		paramsHuman.put("queryTypeHumanId", "00000000-0000-0000-0000-000000000666");
	}

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
				+ "query{human(id:\\\"00000000-0000-0000-0000-000000000031\\\"){id name friends{name appearsIn __typename} __typename}}\"" //
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

		String expected = "{\"query\":\"query{__schema{types{kind name __typename} __typename}}\",\"variables\":null,\"operationName\":null}";

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
		assertEquals("query", graphQLRequest.getQuery().name);
		assertEquals(1, graphQLRequest.getQuery().fields.size(), "nb queries");

		QueryField human = graphQLRequest.getQuery().fields.get(0);
		assertEquals("human", human.name);
		assertEquals(null, human.alias);
		assertEquals(Human.class, human.clazz, "name");
		assertEquals(2, human.fields.size(), "nb fields (with __typename)");
		int i = 0;
		assertEquals("name", human.fields.get(i).name, "name");
		assertEquals("alias", human.fields.get(i).alias, "alias");
		assertEquals(0, human.fields.get(i).fields.size(), "sub fields");
		assertEquals(String.class, human.fields.get(i).clazz, "name");
		i += 1;
		assertEquals("__typename", human.fields.get(i).name, "name");
		assertEquals(null, human.fields.get(i).alias, "alias");
		assertEquals(0, human.fields.get(i).fields.size(), "sub fields");
		assertEquals(String.class, human.fields.get(i).clazz, "name");
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

	/**
	 * When requesting a non scalar field that is an object type, without specifying subfields, than all F's scalar
	 * fields are automatically added
	 */
	@Test
	public void test_withQueryResponseDef_object_emptyQuery() throws GraphQLRequestPreparationException {
		// no query
		checkEmptyHumanQuery(humanResponseDefBuilder.build(), "no query");

		// Empty query
		checkEmptyHumanQuery(humanResponseDefBuilder.withQueryResponseDef("").build(), "Empty query");

		// Null query
		checkEmptyHumanQuery(humanResponseDefBuilder.withQueryResponseDef("").build(), "Null query");
	}

	private void checkEmptyHumanQuery(AbstractGraphQLRequest graphQLRequest, String test) {
		assertEquals(1, graphQLRequest.query.fields.size(), "all scalar fields (with __typename)");
		QueryField human = graphQLRequest.query.fields.get(0);
		assertEquals("human", human.getName(), "check query name, for test: " + test);
		assertEquals(5, human.fields.size(), "all scalar fields (with __typename), for test: " + test);
		//
		// field name check
		int i = 0;
		assertEquals("id", human.fields.get(i++).name, "check field n°" + (i - 1) + "'s name, for test: " + test);
		assertEquals("name", human.fields.get(i++).name, "check field n°" + (i - 1) + "'s name, for test: " + test);
		assertEquals("appearsIn", human.fields.get(i++).name,
				"check field n°" + (i - 1) + "'s name, for test: " + test);
		assertEquals("homePlanet", human.fields.get(i++).name,
				"check field n°" + (i - 1) + "'s name, for test: " + test);
		assertEquals("__typename", human.fields.get(i++).name,
				"check field n°" + (i - 1) + "'s name, for test: " + test);
	}

	/**
	 * When requesting a non scalar field that is an interface type, without specifying subfields, than all F's scalar
	 * fields are automatically added
	 */
	@Test
	public void test_withQueryResponseDef_interface_emptyQuery() throws GraphQLRequestPreparationException {

		// no query
		checkEmptyHeroQuery(queryType.getHeroResponseBuilder().build(), "no query");

		// Empty query
		checkEmptyHeroQuery(queryType.getHeroResponseBuilder().withQueryResponseDef("").build(), "Empty query");

		// Null query
		checkEmptyHeroQuery(queryType.getHeroResponseBuilder().withQueryResponseDef("").build(), "Null query");

	}

	private void checkEmptyHeroQuery(AbstractGraphQLRequest graphQLRequest, String test) {
		assertEquals(1, graphQLRequest.query.fields.size(), "all scalar fields (with __typename)");
		QueryField hero = graphQLRequest.query.fields.get(0);
		assertEquals("hero", hero.getName(), "check query name, for test: " + test);
		//
		// field name check
		//
		// The fields order is strange, and changes overtime. So we just check the number of expected fields, and that
		// each of them exits.
		// It seems to be linked with the fact that this is an interface ???
		assertEquals(4, hero.fields.size(), "all scalar fields (with __typename), for test: " + test);
		checkContainsField(hero.fields, "id");
		checkContainsField(hero.fields, "name");
		checkContainsField(hero.fields, "appearsIn");
		checkContainsField(hero.fields, "__typename");
	}

	private void checkContainsField(List<QueryField> fields, String fieldName) {
		for (QueryField f : fields) {
			if (fieldName.equals(f.getName())) {
				// It's ok. Let's stop there.
				return;
			}
		} // for

		fail("The field '" + fieldName + "' has not been found");
	}

	@Test
	public void test_withQueryResponseDef_StarWars()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		// Go, go, go
		AbstractGraphQLRequest graphQLRequest = humanResponseDefBuilder
				.withQueryResponseDef("{id friends{ id nameAlias:name amis : friends{id name} appearsIn} name } ")
				.build();

		// Verification
		assertEquals(1, graphQLRequest.query.fields.size(), "nb queries");
		QueryField human = graphQLRequest.query.fields.get(0);
		assertEquals("human", human.name, "field name");
		//
		assertEquals(
				"{\"query\":\"query{human(id:\\\"00000000-0000-0000-0000-000000000666\\\"){id friends{id nameAlias:name amis:friends{id name __typename} appearsIn __typename} name __typename}}\",\"variables\":null,\"operationName\":null}",
				graphQLRequest.buildRequest(paramsHuman));
	}

	@Test
	public void test_withQueryResponseDef_KO() throws GraphQLRequestPreparationException {
		GraphQLRequestPreparationException e;

		// Errors with { or }
		assertThrows(GraphQLRequestPreparationException.class,
				() -> queryType.getHumanResponseBuilder()
						.withQueryResponseDef("id friends{ id nameAlias:name amis : friends{id name} appearsIn} name "),
				"missing a '{'");

		assertThrows(GraphQLRequestPreparationException.class, () -> queryType.getHumanResponseBuilder()
				.withQueryResponseDef("{id friends{ id nameAlias:name amis : friends{id name} appearsIn} name "),
				"missing a '}'");

		assertThrows(GraphQLRequestPreparationException.class,
				() -> queryType.getHumanResponseBuilder().withQueryResponseDef(
						"{id friends{ id nameAlias:name amis : friends{id name}{id} appearsIn} name "),
				"a { without leading field name");

		assertThrows(GraphQLRequestPreparationException.class,
				() -> queryType.getHumanResponseBuilder().withQueryResponseDef("{id friends name "),
				"No field definition for friends");

		assertThrows(GraphQLRequestPreparationException.class,
				() -> queryType.getHumanResponseBuilder().withQueryResponseDef("{id friends{} name "),
				"Empty field definition for friends");

		// Field present two times
		e = assertThrows(GraphQLRequestPreparationException.class, () -> queryType.getHumanResponseBuilder()
				.withQueryResponseDef("{id friends{ id nameAlias:name amis : friends{id name} appearsIn} name id } "));
		assertTrue(e.getMessage().contains("<id>"), e.getMessage());

		// Wrong field name
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> queryType.getHumanResponseBuilder().withQueryResponseDef(
						"{id friends{ id nameAlias:name amis : friends{id notAFieldName} appearsIn} name } "));
		assertTrue(e.getMessage().contains("<notAFieldName>"), e.getMessage());

		// Bad alias name
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> queryType.getHumanResponseBuilder().withQueryResponseDef(
						"{id friends{ id name*Alias:name amis : friends{id notAFieldName} appearsIn} name } "));
		assertTrue(e.getMessage().contains("<name*Alias>"), e.getMessage());

		// We're not ready yet for field parameters
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> queryType.getHumanResponseBuilder().withQueryResponseDef(
						"{id friends{ id(since) nameAlias:name amis : friends{id name} appearsIn} name "),
				"missing a '}'");
		assertTrue(e.getMessage().contains("("), e.getMessage());
	}

	@Test
	void testBuild_NoFields() throws GraphQLRequestPreparationException {
		// Go, go, go
		AbstractGraphQLRequest graphQLRequest = queryType.getHumanResponseBuilder().build();

		// Verification
		assertEquals(1, graphQLRequest.query.fields.size());
		QueryField human = graphQLRequest.query.fields.get(0);
		assertEquals("human", human.name);
		assertEquals(5, human.fields.size(), "all scalar fields (with the added __typename field)");
		//
		// field name check
		int i = 0;
		assertEquals("id", human.fields.get(i++).name);
		assertEquals("name", human.fields.get(i++).name);
		assertEquals("appearsIn", human.fields.get(i++).name);
		assertEquals("homePlanet", human.fields.get(i++).name);
		assertEquals("__typename", human.fields.get(i++).name);
	}

}
