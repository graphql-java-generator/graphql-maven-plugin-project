package com.graphql_java_generator.client.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.graphql_java_generator.client.domain.allGraphQLCases.AnotherMutationType;
import com.graphql_java_generator.client.domain.allGraphQLCases.Episode;
import com.graphql_java_generator.client.domain.allGraphQLCases.GraphQLRequest;
import com.graphql_java_generator.client.domain.allGraphQLCases.HumanInput;
import com.graphql_java_generator.client.domain.allGraphQLCases.MyQueryType;
import com.graphql_java_generator.client.domain.allGraphQLCases._extends;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

@Execution(ExecutionMode.CONCURRENT)
class AbstractGraphQLRequest_allGraphQLCasesTest {

	HumanInput input;
	Map<String, Object> params = new HashMap<>();

	@BeforeEach
	void setup() {
		input = new HumanInput();
		input.setName("a new name");
		List<Episode> episodes = new ArrayList<>();
		episodes.add(Episode.JEDI);
		episodes.add(Episode.EMPIRE);
		episodes.add(Episode.NEWHOPE);
		input.setAppearsIn(episodes);

		params.put("humanInput", input);
		params.put("value", "the mutation value");
		params.put("anotherValue", "the other mutation value");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testBuild_scalarInputParameters() throws GraphQLRequestPreparationException {
		// Go, go, go
		MyQueryType queryType = new MyQueryType("http://localhost");
		@SuppressWarnings("deprecation")
		AbstractGraphQLRequest graphQLRequest = queryType.getABreakResponseBuilder()
				.withQueryResponseDef("{case(test: DOUBLE)}").build();

		// Verification
		assertEquals(1, graphQLRequest.query.fields.size());
		QueryField aBreak = graphQLRequest.query.fields.get(0);
		assertEquals("aBreak", aBreak.name);
		assertEquals(2, aBreak.fields.size(), " (with the added __typename field)");

		QueryField field = aBreak.fields.get(0);
		assertEquals("case", field.name);
		assertEquals(1, field.inputParameters.size());
		assertEquals("test", field.inputParameters.get(0).getName());
		assertEquals(_extends.DOUBLE, field.inputParameters.get(0).getValue());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testBuild_Partial_createHuman() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		AnotherMutationType mutationType = new AnotherMutationType("http://localhost/graphql");
		params = new HashMap<>();
		params.put("anotherMutationTypeCreateHumanHuman", input);
		params.put("value", "the mutation value");
		params.put("anotherValue", "the other mutation value");

		// Go, go, go
		@SuppressWarnings("deprecation")
		AbstractGraphQLRequest graphQLRequest = mutationType.getCreateHumanResponseBuilder()
				.withQueryResponseDef("{id name appearsIn friends {id name}}}").build();

		// Verification
		assertEquals("{\"query\":\"mutation" //
				+ "{createHuman(human:{name:\\\"a new name\\\",appearsIn:[JEDI,EMPIRE,NEWHOPE]})"//
				+ "{id name appearsIn friends{id name __typename} __typename}}" //
				+ "\",\"variables\":null,\"operationName\":null}", //
				graphQLRequest.buildRequest(params));
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testBuild_Full_createHuman_withBuilder()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		AnotherMutationType mutationType = new AnotherMutationType("http://localhost/graphql");

		// Go, go, go
		@SuppressWarnings("deprecation")
		AbstractGraphQLRequest graphQLRequest = mutationType.getResponseBuilder().withQueryResponseDef(//
				"mutation { createHuman (human: &humanInput) @testDirective(value:&value, anotherValue:?anotherValue)   "//
						+ "{id name appearsIn friends {id name}}}"//
		).build();

		// Verification
		assertEquals("{\"query\":\"mutation" //
				+ "{createHuman(human:{name:\\\"a new name\\\",appearsIn:[JEDI,EMPIRE,NEWHOPE]}) @testDirective(value:\\\"the mutation value\\\",anotherValue:\\\"the other mutation value\\\")"//
				+ "{id name appearsIn friends{id name __typename} __typename}}" //
				+ "\",\"variables\":null,\"operationName\":null}", //
				graphQLRequest.buildRequest(params));
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testBuild_Full_createHuman() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation

		// Go, go, go
		GraphQLRequest graphQLRequest = new GraphQLRequest(//
				"mutation {createHuman (human: &humanInput) @testDirective(value:&value, anotherValue:?anotherValue)   "//
						+ "{id name appearsIn friends {id name}}}"//
		);

		// Verification
		assertEquals("{\"query\":\"mutation" //
				+ "{createHuman(human:{name:\\\"a new name\\\",appearsIn:[JEDI,EMPIRE,NEWHOPE]}) @testDirective(value:\\\"the mutation value\\\",anotherValue:\\\"the other mutation value\\\")"//
				+ "{id name appearsIn friends{id name __typename} __typename}}" //
				+ "\",\"variables\":null,\"operationName\":null}", //
				graphQLRequest.buildRequest(params));
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testBuild_Full_createHuman_WithHardCodedParameters()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		params = new HashMap<>();
		params.put("value", "the directive value");
		params.put("anotherValue", "the other directive value");

		// Go, go, go
		String request = "mutation mut1 {"//
				+ "createHuman (human:  {name: \"a name with a string that contains a \\\", two { { and a } \", friends: [], appearsIn: [JEDI,NEWHOPE]} )"//
				+ "@testDirective(value:?value, anotherValue:?anotherValue, anArray  : [  \"a string that contains [ [ and ] that should be ignored\" ,  \"another string\" ] , \r\n"
				+ "anObject:{    name: \"a name\" , appearsIn:[],friends : [{name:\"subname\",appearsIn:[],type:\"\"}],type:\"type\"})   {id name appearsIn friends {id name}}}";
		GraphQLRequest graphQLRequest = new GraphQLRequest(request);

		// Verification
		assertEquals("{\"query\":\"mutation mut1" //
				+ "{createHuman(human:{name: \\\"a name with a string that contains a \\\\\\\", two { { and a } \\\", friends: [], appearsIn: [JEDI,NEWHOPE]})"
				+ " @testDirective(value:\\\"the directive value\\\",anotherValue:\\\"the other directive value\\\","
				+ "anArray:[  \\\"a string that contains [ [ and ] that should be ignored\\\" ,  \\\"another string\\\" ],"
				+ "anObject:{    name: \\\"a name\\\" , appearsIn:[],friends : [{name:\\\"subname\\\",appearsIn:[],type:\\\"\\\"}],type:\\\"type\\\"})"//
				+ "{id name appearsIn friends{id name __typename} __typename}}" //
				+ "\",\"variables\":null,\"operationName\":null}", //
				graphQLRequest.buildRequest(params));
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testBuild_Full_createHuman_KOInput1()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		params = new HashMap<>();
		params.put("value", "the directive value");
		params.put("anotherValue", "the other directive value");

		// Go, go, go
		GraphQLRequestPreparationException e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new GraphQLRequest(//
						"mutation {createHuman (human:  {name: \"a name\", friends: [], appearsIn: [JEDI,NEWHOPE], type: \"a type\")"
								+ "@testDirective(value:&value, anotherValue:?anotherValue, anArray  : [  \"a string\" ,  \"another string\" ] , \r\n"
								+ "anObject:{    name: \"a name\" , [{name=\"subname\"}],type:\"type\"})   "//
								+ "{id name appearsIn friends {id name}}}"//
				));

		// Verification
		assertTrue(e.getMessage().contains("The list of parameters for the field 'createHuman' is not finished"),
				"check of this error message: " + e.getMessage());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testBuild_Full_createHuman_KOInput2()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation

		// Go, go, go
		GraphQLRequestPreparationException e = assertThrows(GraphQLRequestPreparationException.class,
				() -> new GraphQLRequest(//
						"mutation {createHuman (human:  {name: \"a name\", friends: [], appearsIn: [JEDI,NEWHOPE], type: \"a type\"})"
								+ "@testDirective(value:&value, anotherValue:?anotherValue, anArray  : [  \"a string\" ,  \"another string\"  , \r\n"
								+ "anObject:{    name: \"a name\" , [{name=\"subname\"}],type:\"type\"})   "//
								+ "{id name appearsIn friends {id name}}}"//
				));

		// Verification
		assertTrue(e.getMessage().contains("Found the end of the GraphQL request before the end of the list"),
				"check of this error message: " + e.getMessage());
	}

}
