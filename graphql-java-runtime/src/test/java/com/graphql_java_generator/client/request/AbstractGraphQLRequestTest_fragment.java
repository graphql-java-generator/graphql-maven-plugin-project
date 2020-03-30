package com.graphql_java_generator.client.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.graphql_java_generator.client.domain.allGraphQLCases.AnotherMutationType;
import com.graphql_java_generator.client.domain.allGraphQLCases.Episode;
import com.graphql_java_generator.client.domain.allGraphQLCases.GraphQLRequest;
import com.graphql_java_generator.client.domain.allGraphQLCases.HumanInput;
import com.graphql_java_generator.client.domain.allGraphQLCases.MyQueryType;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

class AbstractGraphQLRequestTest_fragment {

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
	void testBuild_ThreeFragments() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Go, go, go
		MyQueryType queryType = new MyQueryType("http://localhost");
		AbstractGraphQLRequest graphQLRequest = queryType.getGraphQLRequest(//
				""//
						+ "query{withoutParameters{appearsIn ...fragment1}} " //
						//
						+ "fragment fragment1 on Character {"//
						+ "   id appearsIn friends{id ...fragment3 ...fragment2 }"//
						+ "}"//
						//
						+ "fragment fragment2 on Character {"//
						+ "   id name "//
						+ "}"// //
						+ "fragment fragment3 on Character { appearsIn }"//
		);

		// Verification
		assertEquals(1, graphQLRequest.query.fields.size());
		QueryField withoutParameters = graphQLRequest.query.fields.get(0);
		assertEquals("withoutParameters", withoutParameters.name);
		assertEquals(2, withoutParameters.fields.size(), " (with the added __typename field)");
		//
		assertEquals("appearsIn", withoutParameters.fields.get(0).name);
		assertEquals("__typename", withoutParameters.fields.get(1).name);
		assertEquals("Episode", withoutParameters.fields.get(0).clazz.getSimpleName());
		assertEquals(0, withoutParameters.fields.get(0).inputParameters.size());
		// Nb fragments
		assertEquals(1, withoutParameters.fragments.size());
		assertEquals("fragment1", withoutParameters.fragments.get(0));

		assertEquals(3, graphQLRequest.fragments.size());
		//
		// Fragment 1
		//
		QueryField content1 = graphQLRequest.fragments.get(0).content;
		assertEquals("Character", content1.clazz.getSimpleName());
		assertEquals(4, content1.fields.size(), "with __typename");
		int i = 0;
		assertEquals("id", content1.fields.get(i++).getName());
		assertEquals("appearsIn", content1.fields.get(i++).name);
		assertEquals("friends", content1.fields.get(i++).name);
		assertEquals("__typename", content1.fields.get(i++).name);
		//
		QueryField friends = content1.fields.get(2);
		assertEquals("friends", friends.name);
		assertEquals(2, friends.fields.size(), "with __typename");
		i = 0;
		assertEquals("id", friends.fields.get(i++).name);
		assertEquals("__typename", friends.fields.get(i++).name);
		assertEquals(2, friends.fragments.size());
		assertEquals("fragment3", friends.fragments.get(0));
		assertEquals("fragment2", friends.fragments.get(1));
		//
		// Fragment 2
		//
		// + "fragment fragment2 on Character {"//
		// + " id name "//
		// + "}"// //
		//
		QueryField content2 = graphQLRequest.fragments.get(1).content;
		assertEquals("Character", content2.clazz.getSimpleName());
		assertEquals(3, content2.fields.size(), "with __typename");
		i = 0;
		assertEquals("id", content2.fields.get(i++).name);
		assertEquals("name", content2.fields.get(i++).name);
		assertEquals("__typename", content2.fields.get(i++).name);
		//
		// Fragment 3
		//
		// fragment fragment3 on Character { appearsIn }
		//
		QueryField content3 = graphQLRequest.fragments.get(2).content;
		assertEquals("Character", content3.clazz.getSimpleName());
		assertEquals(2, content3.fields.size(), "with __typename");
		i = 0;
		assertEquals("appearsIn", content3.fields.get(i++).name);
		assertEquals("__typename", content3.fields.get(i++).name);

		assertEquals("{\"query\":\""//
				+ "fragment fragment1 on Character{id appearsIn friends{id __typename ...fragment3 ...fragment2} __typename}"
				+ "fragment fragment2 on Character{id name __typename}"//
				+ "fragment fragment3 on Character{appearsIn __typename}" //
				+ "query{withoutParameters{appearsIn __typename ...fragment1}}"//
				+ "\",\"variables\":null,\"operationName\":null}", //
				graphQLRequest.buildRequest(params));
	}

	@Test
	void testBuild_Partial_createHuman() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		fail("A refaire");
		// Preparation
		AnotherMutationType mutationType = new AnotherMutationType("http://localhost/graphql");
		params = new HashMap<>();
		params.put("anotherMutationTypeCreateHumanHuman", input);
		params.put("value", "the mutation value");
		params.put("anotherValue", "the other mutation value");

		// Go, go, go
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
	void testBuild_Full_createHuman_withBuilder()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		fail("A refaire");
		// Preparation
		AnotherMutationType mutationType = new AnotherMutationType("http://localhost/graphql");

		// Go, go, go
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
	void testBuild_Full_createHuman() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		fail("A refaire");
		// Preparation
		AnotherMutationType mutationType = new AnotherMutationType("http://localhost/graphql");

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

}
