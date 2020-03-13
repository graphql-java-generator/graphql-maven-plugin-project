package org.allGraphQLCases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.allGraphQLCases.client.AnotherMutationType;
import org.allGraphQLCases.client.AnotherMutationTypeResponse;
import org.allGraphQLCases.client.Episode;
import org.allGraphQLCases.client.Human;
import org.allGraphQLCases.client.HumanInput;
import org.allGraphQLCases.client.MyQueryType;
import org.allGraphQLCases.client.MyQueryTypeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

class FullQueriesDirectIT {

	MyQueryType queryType;
	AnotherMutationType mutationType;

	ObjectResponse responseMutationWithDirective;
	ObjectResponse responseMutationWithoutDirective;
	ObjectResponse responseWithDirectiveTwoParameters;

	@BeforeEach
	void setup() throws GraphQLRequestPreparationException {
		queryType = new MyQueryType(Main.GRAPHQL_ENDPOINT);
		mutationType = new AnotherMutationType(Main.GRAPHQL_ENDPOINT);

		// The response preparation should be somewhere in the application initialization code.
		responseMutationWithDirective = mutationType.getResponseBuilder().withQueryResponseDef(//
				"{createHuman (human: &humanInput) @testDirective(value:&value, anotherValue:?anotherValue)   "//
						+ "{id name appearsIn friends {id name}}}"//
		).build();
		responseMutationWithoutDirective = mutationType.getResponseBuilder().withQueryResponseDef(//
				"{createHuman (human: &humanInput) {id name appearsIn friends {id name}}}"//
		).build();
		responseWithDirectiveTwoParameters = queryType.getResponseBuilder().withQueryResponseDef(
				"{directiveOnQuery (uppercase: false) @testDirective(value:&value, anotherValue:?anotherValue)}")
				.build();

	}

	@Test
	void noDirective() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// Go, go, go
		MyQueryTypeResponse resp = queryType.exec("{directiveOnQuery}"); // Direct queries should be used only for very
																			// simple cases

		// Verifications
		assertNotNull(resp);
		List<String> ret = resp.getDirectiveOnQuery();
		assertNotNull(ret);
		assertEquals(0, ret.size());
	}

	@Test
	void withDirectiveOneParameter() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// Go, go, go
		MyQueryTypeResponse resp = queryType.exec("{directiveOnQuery  (uppercase: true) @testDirective(value:&value)}", //
				"value", "the value", "skip", Boolean.FALSE);

		// Verifications
		assertNotNull(resp);
		List<String> ret = resp.getDirectiveOnQuery();
		assertNotNull(ret);
		assertEquals(1, ret.size());
		//
		assertEquals("THE VALUE", ret.get(0));
	}

	@Test
	void withDirectiveTwoParameters() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// Go, go, go
		MyQueryTypeResponse resp = queryType.exec(responseWithDirectiveTwoParameters, //
				"value", "the value", "anotherValue", "the other value", "skip", Boolean.TRUE);

		// Verifications
		assertNotNull(resp);
		List<String> ret = resp.getDirectiveOnQuery();
		assertNotNull(ret);
		assertEquals(2, ret.size());
		//
		assertEquals("the value", ret.get(0));
		assertEquals("the other value", ret.get(1));
	}

	@Test
	void mutation() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		HumanInput input = new HumanInput();
		input.setName("a new name");
		List<Episode> episodes = new ArrayList<>();
		episodes.add(Episode.JEDI);
		episodes.add(Episode.EMPIRE);
		episodes.add(Episode.NEWHOPE);
		input.setAppearsIn(episodes);

		////////////////////////////////////////////////////////////////////////////////////////////////
		// WITHOUT DIRECTIVE

		// Go, go, go
		AnotherMutationTypeResponse resp = mutationType.exec(responseMutationWithoutDirective, "humanInput", input);

		// Verifications
		assertNotNull(resp);
		Human ret = resp.getCreateHuman();
		assertNotNull(ret);
		assertEquals("a new name", ret.getName());

		////////////////////////////////////////////////////////////////////////////////////////////////
		// WITH DIRECTIVE

		// Go, go, go
		resp = mutationType.exec(responseMutationWithDirective, //
				"humanInput", input, //
				"value", "the mutation value", //
				"anotherValue", "the other mutation value");

		// Verifications
		assertNotNull(resp);
		ret = resp.getCreateHuman();
		assertNotNull(ret);
		assertEquals("the other mutation value", ret.getName());
	}

}
