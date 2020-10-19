package org.allGraphQLCases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.allGraphQLCases.client.Character;
import org.allGraphQLCases.client.CharacterInput;
import org.allGraphQLCases.client.Droid;
import org.allGraphQLCases.client.Episode;
import org.allGraphQLCases.client.Human;
import org.allGraphQLCases.client.util.GraphQLRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.graphql_java_generator.client.GraphQLConfiguration;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

@Execution(ExecutionMode.CONCURRENT)
class FragmentIT {

	CharacterInput input;
	Map<String, Object> params = new HashMap<>();

	@BeforeEach
	void setup() {
		// Default configuration for GraphQLRequest
		GraphQLRequest.setStaticConfiguration(new GraphQLConfiguration(Main.GRAPHQL_ENDPOINT));

		// A useful init for some tests
		input = new CharacterInput();
		input.setName("a new name");
		List<Episode> episodes = new ArrayList<>();
		episodes.add(Episode.JEDI);
		episodes.add(Episode.EMPIRE);
		episodes.add(Episode.NEWHOPE);
		input.setAppearsIn(episodes);
		input.setType("Droid");

		params.put("input", input);
		params.put("value", "the mutation value");
		params.put("anotherValue", "the other mutation value");
		params.put("uppercaseFalse", false);
		params.put("uppercaseTrue", true);
	}

	@Test
	void test_ThreeGlobalFragments() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		GraphQLRequest graphQLRequest = new GraphQLRequest(""//
				+ "query{withoutParameters{appearsIn ...fragment1}} " //
				+ "fragment fragment1 on Character {id appearsIn friends{id ...fragment3 ...fragment2 }}"//
				+ "fragment fragment2 on Character {id name(uppercase: &uppercaseTrue)}"//
				+ "fragment fragment3 on Character {appearsIn}"//
		);

		// Go, go, go
		List<Character> withoutParameters = graphQLRequest.execQuery(params).getWithoutParameters();

		// Verification
		assertNotNull(withoutParameters);
		assertTrue(withoutParameters.size() > 0);
		assertTrue(withoutParameters.get(0) instanceof Droid || withoutParameters.get(0) instanceof Human);
		assertNotNull(withoutParameters.get(0).get__typename());
		assertNotNull(withoutParameters.get(0).getAppearsIn());
		assertNotNull(withoutParameters.get(0).getFriends());
		assertNull(withoutParameters.get(0).getName(), "name has not been requested");

		Character firstFriend = withoutParameters.get(0).getFriends().get(0);
		assertNotNull(firstFriend.getId());
		assertNotNull(firstFriend.getName());
		assertNotNull(firstFriend.getAppearsIn());
		assertNull(firstFriend.getFriends(), "friends has not been requested");
	}

	@Test
	void test_InlineAndGlobalFragments() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		GraphQLRequest graphQLRequest = new GraphQLRequest(""//
				+ "query{" //
				+ "  withoutParameters{"//
				+ "    appearsIn " //
				+ "    ...id " //
				+ "    ... on Character { ...id friends { ...id }} " //
				+ "    ... on Droid {  primaryFunction ... on Character {name(uppercase: ?uppercaseFalse) friends {name}}  } " //
				+ "    ... on Human {  homePlanet ... on Human { ... on Character  { name(uppercase: ?uppercaseFalse)}} } " //
				+ "  } "//
				+ "} " //
				+ "fragment id on Character {id} "//
		);

		// Go, go, go
		List<Character> withoutParameters = graphQLRequest.execQuery(params).getWithoutParameters();

		// Verification
		assertNotNull(withoutParameters);
		assertTrue(withoutParameters.size() > 0);
		assertTrue(withoutParameters.get(0) instanceof Droid, "Hope the order of the result won't change");
		assertEquals("Droid", withoutParameters.get(0).get__typename());
		assertNotNull(withoutParameters.get(0).getAppearsIn());
		assertNotNull(withoutParameters.get(0).getId());
		assertNotNull(withoutParameters.get(0).getFriends());
		assertNotNull(withoutParameters.get(0).getName(), "name is requested in inline fragment");
		assertNotNull(((Droid) withoutParameters.get(0)).getPrimaryFunction(),
				"primaryFunction is requested for droids");

		Character firstFriend = withoutParameters.get(0).getFriends().get(0);
		assertNotNull(firstFriend.getId());
		assertNotNull(firstFriend.getName(), "Requested for Droids");
		assertNull(firstFriend.getAppearsIn());
		assertNull(firstFriend.getFriends(), "friends of friends has not been requested");
	}

	@Test
	void test_InlineAndGlobalFragments_withOneOptionalParam_Droid()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		GraphQLRequest graphQLRequest = new GraphQLRequest(""//
				+ "query{" //
				+ "  withOneOptionalParam(character: &input){"//
				+ "    appearsIn " //
				+ "    ...id " //
				+ "    ... on Character { ...id } " //
				+ "    ... on Droid {  primaryFunction ... on Character {name(uppercase: ?notDefinedBindVariable) friends {name}}  } " //
				+ "    ... on Human {  homePlanet ... on Human { ... on Character  { name(uppercase: ?notDefinedBindVariable)}} } " //
				+ "  } "//
				+ "} " //
				+ "fragment id on Character {id} "//
		);

		// Go, go, go
		Character withOneOptionalParam = graphQLRequest.execQuery(params).getWithOneOptionalParam();

		// Verification
		assertNotNull(withOneOptionalParam);
		assertTrue(withOneOptionalParam instanceof Droid, "we've ask for thas in input's type");
		assertEquals("Droid", withOneOptionalParam.get__typename());
		assertTrue(withOneOptionalParam instanceof Droid);
		assertNotNull(withOneOptionalParam.getAppearsIn());
		assertNotNull(withOneOptionalParam.getId());
		assertNotNull(withOneOptionalParam.getFriends(), "friends have been requested onyl for droids");
		assertNotNull(withOneOptionalParam.getName(), "name has been requested in inline fragment");
		assertNotNull(((Droid) withOneOptionalParam).getPrimaryFunction(), "primaryFunction is requested for droids");
	}

	@Test
	void test_InlineAndGlobalFragments_withOneOptionalParam_Human()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		GraphQLRequest graphQLRequest = new GraphQLRequest(""//
				+ "query{" //
				+ "  withOneOptionalParam(character: &input){"//
				+ "    appearsIn " //
				+ "    ...id " //
				+ "    ... on Character { ...id } " //
				+ "    ... on Droid {  primaryFunction ... on Character {name(uppercase: ?notDefinedBindVariable) friends {name}}  } " //
				+ "    ... on Human {  homePlanet ... on Human { ... on Character  { name(uppercase: ?notDefinedBindVariable)}} } " //
				+ "  } "//
				+ "} " //
				+ "fragment id on Character {id} "//
		);
		input.setType("Human");

		// Go, go, go
		Character withOneOptionalParam = graphQLRequest.execQuery(params).getWithOneOptionalParam();

		// Verification
		assertNotNull(withOneOptionalParam);
		assertTrue(withOneOptionalParam instanceof Human, "we've ask for thas in input's type");
		assertEquals("Human", withOneOptionalParam.get__typename());
		assertTrue(withOneOptionalParam instanceof Human);
		assertNotNull(withOneOptionalParam.getAppearsIn());
		assertNotNull(withOneOptionalParam.getId());
		assertNull(withOneOptionalParam.getFriends(), "friends have been requested onyl for droids");
		assertNotNull(withOneOptionalParam.getName(), "name has been requested in inline fragment for human");
		assertNotNull(((Human) withOneOptionalParam).getHomePlanet(), "homePlanet is requested for humans");
	}
}
