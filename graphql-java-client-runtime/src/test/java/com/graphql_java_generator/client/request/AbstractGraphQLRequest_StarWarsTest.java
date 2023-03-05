package com.graphql_java_generator.client.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.graphql_java_generator.client.SpringContextBean;
import com.graphql_java_generator.client.request.AbstractGraphQLRequest.Payload;
import com.graphql_java_generator.domain.client.allGraphQLCases.Character;
import com.graphql_java_generator.domain.client.allGraphQLCases.CharacterInput;
import com.graphql_java_generator.domain.client.allGraphQLCases.Episode;
import com.graphql_java_generator.domain.client.allGraphQLCases.GraphQLRequest;
import com.graphql_java_generator.domain.client.allGraphQLCases.MyQueryTypeExecutorMySchema;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

@Execution(ExecutionMode.CONCURRENT)
class AbstractGraphQLRequest_StarWarsTest {

	MyQueryTypeExecutorMySchema queryType;
	Builder withOneMandatoryParamDefBuilder;
	CharacterInput characterInput;
	Map<String, Object> paramsWithOneMandatoryParam;

	@SuppressWarnings("unchecked")
	@BeforeEach
	void setup() throws GraphQLRequestPreparationException {
		queryType = new MyQueryTypeExecutorMySchema();
		withOneMandatoryParamDefBuilder = queryType.getWithOneMandatoryParamResponseBuilder();

		@SuppressWarnings("unchecked")
		List<Episode> episodes = (List<Episode>) (Object) (Arrays.asList(Arrays.array(Episode.EMPIRE, Episode.JEDI)));

		characterInput = CharacterInput.builder().withName("a test name").withAppearsIn(episodes).build();
		paramsWithOneMandatoryParam = new HashMap<>();
		paramsWithOneMandatoryParam.put("character", characterInput);
		paramsWithOneMandatoryParam.put("myQueryTypeWithOneMandatoryParamCharacter", characterInput);

		ApplicationContext applicationContext = mock(ApplicationContext.class);
		when(applicationContext.getBean(anyString(), any(Class.class))).thenReturn(null);
		SpringContextBean.setApplicationContext(applicationContext);
	}

	@Test
	void testWithField_Simple_OK() throws GraphQLRequestPreparationException {
		// Go, go, go
		GraphQLRequest graphQLRequest = new GraphQLRequest(
				"{withOneMandatoryParam(character:{name:\"another name\",appearsIn:[]}) {name}}");

		// Verification
		assertEquals(0, ((AbstractGraphQLRequest) graphQLRequest).aliasFields.size());
		assertEquals("query", graphQLRequest.getQuery().name);
		assertEquals(1, graphQLRequest.getQuery().fields.size(), "nb queries");

		QueryField withOneMandatoryParam = graphQLRequest.getQuery().fields.get(0);
		assertEquals("withOneMandatoryParam", withOneMandatoryParam.name);
		assertEquals(null, withOneMandatoryParam.alias);
		assertEquals(Character.class, withOneMandatoryParam.clazz, "name");
		assertEquals(2, withOneMandatoryParam.fields.size(), "nb fields (with __typename)");
		int i = 0;
		assertEquals("name", withOneMandatoryParam.fields.get(i).name, "name");
		assertEquals(null, withOneMandatoryParam.fields.get(i).alias, "alias");
		assertEquals(0, withOneMandatoryParam.fields.get(i).fields.size(), "sub fields");
		assertEquals(String.class, withOneMandatoryParam.fields.get(i).clazz, "name");
		i += 1;
		assertEquals("__typename", withOneMandatoryParam.fields.get(i).name, "name");
		assertEquals(null, withOneMandatoryParam.fields.get(i).alias, "alias");
		assertEquals(0, withOneMandatoryParam.fields.get(i).fields.size(), "sub fields");
		assertEquals(String.class, withOneMandatoryParam.fields.get(i).clazz, "name");
	}

	@Test
	void test_AbstractGraphQLRequest_oneQuery()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, JsonProcessingException {
		// Preparation
		String query1 = "  \n\r\t  {withOneMandatoryParam(character:{name:\"a test name\",appearsIn:[EMPIRE,JEDI]}) {id name friends {name appearsIn}}}  \n\r\t ";
		String query2 = "  \n\r\t query \n\r\t  {withOneMandatoryParam(character:&character) {id name friends {name appearsIn}}}  \n\r\t ";

		String expected1 = "query{withOneMandatoryParam(character:{name:\"a test name\",appearsIn:[EMPIRE,JEDI]}){id name friends{name appearsIn __typename} __typename}}";
		String expected2 = "query{withOneMandatoryParam(character:{name:\"a test name\",appearsIn:[EMPIRE, JEDI]}){id name friends{name appearsIn __typename} __typename}}";

		// Go, go, go
		Payload payload1 = new GraphQLRequest(query1).getPayload(null);
		Payload payload2 = new GraphQLRequest(query2).getPayload(paramsWithOneMandatoryParam);

		// Verification
		AbstractGraphQLRequest_allGraphQLCasesTest.checkPayload(payload1, expected1, null, null);
		AbstractGraphQLRequest_allGraphQLCasesTest.checkPayload(payload2, expected2, null, null);
	}

	@Test
	void test_AbstractGraphQLRequest_introspectionQuery()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, JsonProcessingException {
		// Preparation
		String query1 = "  \n\r\t   {__schema {types{kind name}}}  \n\r\t ";
		String query2 = "  \n\r\t query \n\r\t  {__schema {types{kind name}}} \n\r\t ";

		String expected = "query{__schema{types{kind name __typename} __typename}}";

		// Go, go, go
		Payload payload1 = new GraphQLRequest(query1).getPayload(null);
		Payload payload2 = new GraphQLRequest(query2).getPayload(null);

		// Verification
		AbstractGraphQLRequest_allGraphQLCasesTest.checkPayload(payload1, expected, null, null);
		AbstractGraphQLRequest_allGraphQLCasesTest.checkPayload(payload2, expected, null, null);
	}

	@Test
	void testWithField_KO_FieldPresentTwoTimes() throws GraphQLRequestPreparationException {
		GraphQLRequestPreparationException e;

		// Go, go go
		e = assertThrows(GraphQLRequestPreparationException.class, () -> new GraphQLRequest(
				"{withOneOptionalParam(character:{name:\"another name\",appearsIn:[]}) {id name name}}"));
		assertTrue(e.getMessage().contains("'name'"), "The received message is: " + e.getMessage());

		e = assertThrows(GraphQLRequestPreparationException.class, //
				() -> new GraphQLRequest(
						"{withOneOptionalParam(character:{name:\"another name\",appearsIn:[]}) {id name name:name}}"));
		assertTrue(e.getMessage().contains("'name'"));
	}

	@Test
	void testWithField_KO_NonExistingField() throws GraphQLRequestPreparationException {
		GraphQLRequestPreparationException e;

		// Go, go go
		e = assertThrows(GraphQLRequestPreparationException.class, //
				() -> new GraphQLRequest(
						"{withOneOptionalParam(character:{name:\"another name\",appearsIn:[]}) {id name itDoesntExist}}"));
		assertTrue(e.getMessage().contains("'itDoesntExist'"));

		e = assertThrows(GraphQLRequestPreparationException.class, //
				() -> new GraphQLRequest(
						"{withOneOptionalParam(character:{name:\"another name\",appearsIn:[]}) {id name validAlias:itDoesntExist}}"));
		assertTrue(e.getMessage().contains("'itDoesntExist'"));
	}

	@Test
	void testWithField_KO_InvalidIdentifier() throws GraphQLRequestPreparationException {
		GraphQLRequestPreparationException e;

		// Most important check: only attribute of the clazz class may be added.
		e = assertThrows(GraphQLRequestPreparationException.class, () -> new GraphQLRequest(
				"{withOneOptionalParam(character:{name:\"another name\",appearsIn:[]}) {id name fieldNotInThisClass}}"));
		assertTrue(e.getMessage().contains("fieldNotInThisClass"));

		// Various types of checks, for invalid identifiers
		e = assertThrows(GraphQLRequestPreparationException.class, () -> new GraphQLRequest(
				"{withOneOptionalParam(character:{name:\"another name\",appearsIn:[]}) {id name qdqd'qdsq}}"));
		assertTrue(e.getMessage().contains("qdqd'qdsq"));

		e = assertThrows(GraphQLRequestPreparationException.class, () -> new GraphQLRequest(
				"{withOneOptionalParam(character:{name:\"another name\",appearsIn:[]}) {id name qdqd.qdsq}}"));
		assertTrue(e.getMessage().contains("qdqd.qdsq"));

		e = assertThrows(GraphQLRequestPreparationException.class, () -> new GraphQLRequest(
				"{withOneOptionalParam(character:{name:\"another name\",appearsIn:[]}) {id name qdqdqdsq.}}"));
		assertTrue(e.getMessage().contains("qdqdqdsq."));

		e = assertThrows(GraphQLRequestPreparationException.class, () -> new GraphQLRequest(
				"{withOneOptionalParam(character:{name:\"another name\",appearsIn:[]}) {id name .qdqdqdsq}}"));
		assertTrue(e.getMessage().contains(".qdqdqdsq"));

		e = assertThrows(GraphQLRequestPreparationException.class, () -> new GraphQLRequest(
				"{withOneOptionalParam(character:{name:\"another name\",appearsIn:[]}) {id name qdqdqdsq*}}"));
		assertTrue(e.getMessage().contains("qdqdqdsq*"));
	}

	@Test
	void testWithFieldWithAlias_OK() throws GraphQLRequestPreparationException {
		// Go, go, go
		GraphQLRequest graphQLRequest = new GraphQLRequest(
				"{withOneOptionalParam(character:{name:\"another name\",appearsIn:[]}) {alias:name}}");

		// Verification
		assertEquals("query", graphQLRequest.getQuery().name);
		assertEquals(1, graphQLRequest.getQuery().fields.size(), "nb queries");

		QueryField withOneOptionalParam = graphQLRequest.getQuery().fields.get(0);
		assertEquals("withOneOptionalParam", withOneOptionalParam.name);
		assertEquals(null, withOneOptionalParam.alias);
		assertEquals(Character.class, withOneOptionalParam.clazz, "name");
		assertEquals(2, withOneOptionalParam.fields.size(), "nb fields (with __typename)");
		int i = 0;
		assertEquals("name", withOneOptionalParam.fields.get(i).name, "name");
		assertEquals("alias", withOneOptionalParam.fields.get(i).alias, "alias");
		assertEquals(0, withOneOptionalParam.fields.get(i).fields.size(), "sub fields");
		assertEquals(String.class, withOneOptionalParam.fields.get(i).clazz, "name");
		i += 1;
		assertEquals("__typename", withOneOptionalParam.fields.get(i).name, "name");
		assertEquals(null, withOneOptionalParam.fields.get(i).alias, "alias");
		assertEquals(0, withOneOptionalParam.fields.get(i).fields.size(), "sub fields");
		assertEquals(String.class, withOneOptionalParam.fields.get(i).clazz, "name");
	}

	@Test
	void testWithFieldWithAlias_KO() throws GraphQLRequestPreparationException {
		// KO on the name
		GraphQLRequestPreparationException e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new GraphQLRequest(
						"{withOneOptionalParam(character:{name:\"another name\",appearsIn:[]}) {validAlias:notAnExistingAttribute}}"));
		assertTrue(e.getMessage().contains("notAnExistingAttribute"));

		// KO on the alias
		e = assertThrows(GraphQLRequestPreparationException.class, () -> new GraphQLRequest(
				"{withOneOptionalParam(character:{name:\"another name\",appearsIn:[]}) {qdqd/qdsq:id}}"));
		assertTrue(e.getMessage().contains("qdqd/qdsq"));
	}

	@Test
	void testWithSubObject_OK() throws GraphQLRequestPreparationException {
		// Go, go, go
		GraphQLRequest graphQLRequest = new GraphQLRequest(
				"{withOneOptionalParam(character:{name:\"another name\",appearsIn:[]}) \n\r\t   {\n\r\t  friends  \n\r\t  {  \n\r\t  id \n\r\t  name\n\r\t  } \n\r\t }  \n\r\t } \n\r\t ");

		// Verification
		assertEquals(1, graphQLRequest.getQuery().fields.size(), "one query in the list");
		//
		QueryField withOneOptionalParam = graphQLRequest.getQuery().fields.get(0);
		assertEquals("withOneOptionalParam", withOneOptionalParam.name, "query fieldName");
		assertEquals(null, withOneOptionalParam.alias, "query fieldAlias");
		assertEquals(Character.class, withOneOptionalParam.clazz, "query clazz");
		assertEquals(2, withOneOptionalParam.fields.size());
		assertEquals("friends", withOneOptionalParam.fields.get(0).name);
		assertEquals(null, withOneOptionalParam.alias, "subobject fieldAlias");
		assertEquals(Character.class, withOneOptionalParam.clazz, "subobject clazz");
		assertEquals("__typename", withOneOptionalParam.fields.get(1).name);
		//
		QueryField friends = withOneOptionalParam.fields.get(0);
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
						"{withOneOptionalParam(character:{name:\"another name\",appearsIn:[]}){friends{name} friends{id}}"));
		assertTrue(e.getMessage().contains("'friends'"));
	}

	@Test
	void testWithSubObject_withAlias_OK() throws GraphQLRequestPreparationException {
		// Go, go, go
		GraphQLRequest graphQLRequest = new GraphQLRequest(
				"{withOneOptionalParam(character:{name:\"another name\",appearsIn:[]}){aValidAlias:friends{name id}}}");

		// Verification
		assertEquals(1, graphQLRequest.getQuery().fields.size(), "one query in the list");
		//
		QueryField withOneOptionalParam = graphQLRequest.getQuery().fields.get(0);
		assertEquals("withOneOptionalParam", withOneOptionalParam.name, "query fieldName");
		assertEquals(null, withOneOptionalParam.alias, "query fieldAlias");
		assertEquals(Character.class, withOneOptionalParam.clazz, "query clazz");
		assertEquals(2, withOneOptionalParam.fields.size());
		//
		QueryField friends = withOneOptionalParam.fields.get(0);
		assertEquals("friends", friends.name);
		assertEquals("aValidAlias", friends.alias, "subobject fieldAlias");
		assertEquals(Character.class, friends.clazz, "subobject clazz");
		assertEquals("__typename", withOneOptionalParam.fields.get(1).name);
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
		checkEmptyWithOneMandatoryParamResponseBuilderQuery(withOneMandatoryParamDefBuilder.build(), "no query");

		// Empty query
		checkEmptyWithOneMandatoryParamResponseBuilderQuery(
				withOneMandatoryParamDefBuilder.withQueryResponseDef("").build(), "Empty query");

		// Null query
		checkEmptyWithOneMandatoryParamResponseBuilderQuery(
				withOneMandatoryParamDefBuilder.withQueryResponseDef("").build(), "Null query");
	}

	private void checkEmptyWithOneMandatoryParamResponseBuilderQuery(AbstractGraphQLRequest graphQLRequest,
			String test) {
		assertEquals(1, graphQLRequest.query.fields.size(), "all scalar fields (with __typename)");
		QueryField withOneMandatoryParam = graphQLRequest.query.fields.get(0);
		assertEquals("withOneMandatoryParam", withOneMandatoryParam.getName(), "check query name, for test: " + test);
		assertEquals(4, withOneMandatoryParam.fields.size(), "all scalar fields (with __typename), for test: " + test);
		//
		// field's name check
		List<String> names = withOneMandatoryParam.fields.stream().map(h -> h.getName()).collect(Collectors.toList());
		assertTrue(names.contains("id"));
		assertTrue(names.contains("name"));
		assertTrue(names.contains("appearsIn"));
		assertTrue(names.contains("__typename"));
	}

	/**
	 * When requesting a non scalar field that is an interface type, without specifying subfields, than all F's scalar
	 * fields are automatically added
	 */
	@Test
	public void test_withQueryResponseDef_interface_emptyQuery() throws GraphQLRequestPreparationException {

		// no query
		checkWithTwoMandatoryParamDefaultValResponseBuilder(
				queryType.getWithTwoMandatoryParamDefaultValResponseBuilder().build(), "no query");

		// Empty query
		checkWithTwoMandatoryParamDefaultValResponseBuilder(
				queryType.getWithTwoMandatoryParamDefaultValResponseBuilder().withQueryResponseDef("").build(),
				"Empty query");

		// Null query
		checkWithTwoMandatoryParamDefaultValResponseBuilder(
				queryType.getWithTwoMandatoryParamDefaultValResponseBuilder().withQueryResponseDef("").build(),
				"Null query");

	}

	private void checkWithTwoMandatoryParamDefaultValResponseBuilder(AbstractGraphQLRequest graphQLRequest,
			String test) {
		assertEquals(1, graphQLRequest.query.fields.size(), "all scalar fields (with __typename)");
		QueryField withTwoMandatoryParamDefaultVal = graphQLRequest.query.fields.get(0);
		assertEquals("withTwoMandatoryParamDefaultVal", withTwoMandatoryParamDefaultVal.getName(),
				"check query name, for test: " + test);
		//
		// field name check
		//
		// The fields order is strange, and changes overtime. So we just check the number of expected fields, and that
		// each of them exits.
		// It seems to be linked with the fact that this is an interface ???
		assertEquals(5, withTwoMandatoryParamDefaultVal.fields.size(),
				"all scalar fields (with __typename), for test: " + test);
		checkContainsField(withTwoMandatoryParamDefaultVal.fields, "id");
		checkContainsField(withTwoMandatoryParamDefaultVal.fields, "name");
		checkContainsField(withTwoMandatoryParamDefaultVal.fields, "appearsIn");
		checkContainsField(withTwoMandatoryParamDefaultVal.fields, "primaryFunction");
		checkContainsField(withTwoMandatoryParamDefaultVal.fields, "__typename");
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

	@SuppressWarnings("unchecked")
	@Test
	public void test_withQueryResponseDef_StarWars()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, JsonProcessingException {

		// Go, go, go
		AbstractGraphQLRequest graphQLRequest = withOneMandatoryParamDefBuilder
				.withQueryResponseDef("{id friends{ id nameAlias:name amis : friends{id name} appearsIn} name } ")
				.build();

		// Verification
		assertEquals(1, graphQLRequest.query.fields.size(), "nb queries");
		QueryField human = graphQLRequest.query.fields.get(0);
		assertEquals("withOneMandatoryParam", human.name, "field name");
		//
		Map<String, Object> params = new HashMap<>();
		params.put("myQueryTypeWithOneMandatoryParamCharacter",
				CharacterInput.builder().withName("a test name")
						.withAppearsIn(
								(List<Episode>) (Object) Arrays.asList(Arrays.array(Episode.JEDI, Episode.EMPIRE)))
						.build());
		Payload payload = graphQLRequest.getPayload(params);
		AbstractGraphQLRequest_allGraphQLCasesTest.checkPayload(payload, ""//
				+ "query{withOneMandatoryParam(character:{name:\"a test name\",appearsIn:[JEDI, EMPIRE]}){id friends{id nameAlias:name amis:friends{id name __typename} appearsIn __typename} name __typename}}", //
				null, null);
	}

	@Test
	public void test_withQueryResponseDef_KO() throws GraphQLRequestPreparationException {
		GraphQLRequestPreparationException e;

		// Errors with { or }
		assertThrows(GraphQLRequestPreparationException.class,
				() -> queryType.getWithListResponseBuilder()
						.withQueryResponseDef("id friends{ id nameAlias:name amis : friends{id name} appearsIn} name "),
				"missing a '{'");

		assertThrows(GraphQLRequestPreparationException.class, () -> queryType.getWithListResponseBuilder()
				.withQueryResponseDef("{id friends{ id nameAlias:name amis : friends{id name} appearsIn} name "),
				"missing a '}'");

		assertThrows(GraphQLRequestPreparationException.class,
				() -> queryType.getWithListResponseBuilder().withQueryResponseDef(
						"{id friends{ id nameAlias:name amis : friends{id name}{id} appearsIn} name "),
				"a { without leading field name");

		assertThrows(GraphQLRequestPreparationException.class,
				() -> queryType.getWithListResponseBuilder().withQueryResponseDef("{id friends name "),
				"No field definition for friends");

		assertThrows(GraphQLRequestPreparationException.class,
				() -> queryType.getWithListResponseBuilder().withQueryResponseDef("{id friends{} name "),
				"Empty field definition for friends");

		// Field present two times
		e = assertThrows(GraphQLRequestPreparationException.class, () -> queryType.getWithListResponseBuilder()
				.withQueryResponseDef("{id friends{ id nameAlias:name amis : friends{id name} appearsIn} name id } "));
		assertTrue(e.getMessage().contains("'id'"), e.getMessage());

		// Wrong field name
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> queryType.getWithListResponseBuilder().withQueryResponseDef(
						"{id friends{ id nameAlias:name amis : friends{id notAFieldName} appearsIn} name } "));
		assertTrue(e.getMessage().contains("'notAFieldName'"), e.getMessage());

		// Bad alias name
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> queryType.getWithListResponseBuilder().withQueryResponseDef(
						"{id friends{ id name*Alias:name amis : friends{id notAFieldName} appearsIn} name } "));
		assertTrue(e.getMessage().contains("'name*Alias'"), e.getMessage());

		// We're not ready yet for field parameters
		e = assertThrows(GraphQLRequestPreparationException.class,
				() -> queryType.getWithListResponseBuilder().withQueryResponseDef(
						"{id friends{ id(since) nameAlias:name amis : friends{id name} appearsIn} name "),
				"missing a '}'");
		assertTrue(e.getMessage().contains("("), e.getMessage());
	}

	@Test
	void testBuild_NoFields() throws GraphQLRequestPreparationException {
		// Go, go, go
		AbstractGraphQLRequest graphQLRequest = queryType.getWithListResponseBuilder().build();

		// Verification
		assertEquals(1, graphQLRequest.query.fields.size());
		QueryField human = graphQLRequest.query.fields.get(0);
		assertEquals("withList", human.name);
		assertEquals(4, human.fields.size(), "all scalar fields (with the added __typename field)");
		//
		// field's name check
		int i = 0;
		List<String> names = human.fields.stream().map(h -> h.getName()).collect(Collectors.toList());
		assertTrue(names.contains("id"));
		assertTrue(names.contains("name"));
		assertTrue(names.contains("appearsIn"));
		assertTrue(names.contains("__typename"));
	}

}
