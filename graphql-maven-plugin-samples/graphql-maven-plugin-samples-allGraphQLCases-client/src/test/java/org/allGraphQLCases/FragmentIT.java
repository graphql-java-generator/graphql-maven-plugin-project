package org.allGraphQLCases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.allGraphQLCases.client.CEP_Episode_CES;
import org.allGraphQLCases.client.CINP_CharacterInput_CINS;
import org.allGraphQLCases.client.CIP_Character_CIS;
import org.allGraphQLCases.client.CTP_Droid_CTS;
import org.allGraphQLCases.client.CTP_Human_CTS;
import org.allGraphQLCases.client.GraphQLRequestAllGraphQLCases;
import org.allGraphQLCases.client.MyQueryTypeExecutorAllGraphQLCases;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

// Adding "webEnvironment = SpringBootTest.WebEnvironment.NONE" avoid this error:
// "No qualifying bean of type 'ReactiveClientRegistrationRepository' available"
// More details here: https://stackoverflow.com/questions/62558552/error-when-using-enablewebfluxsecurity-in-springboot
@SpringBootTest(classes = SpringTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Execution(ExecutionMode.CONCURRENT)
class FragmentIT {

	@Autowired
	MyQueryTypeExecutorAllGraphQLCases myQuery;

	CINP_CharacterInput_CINS input;
	Map<String, Object> params = new HashMap<>();

	@BeforeEach
	void setup() {
		// A useful init for some tests
		input = new CINP_CharacterInput_CINS();
		input.setName("a new name");
		List<CEP_Episode_CES> episodes = new ArrayList<>();
		episodes.add(CEP_Episode_CES.JEDI);
		episodes.add(CEP_Episode_CES.EMPIRE);
		episodes.add(CEP_Episode_CES.NEWHOPE);
		input.setAppearsIn(episodes);
		input.setType("Droid");

		params.put("input", input);
		params.put("value", "the mutation value");
		params.put("anotherValue", "the other mutation value");
		params.put("uppercaseFalse", false);
		params.put("uppercaseTrue", true);
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_ThreeGlobalFragments() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		GraphQLRequestAllGraphQLCases GraphQLRequestAllGraphQLCases = myQuery.getGraphQLRequest(""//
				+ "query{withoutParameters{appearsIn ...fragment1}} " //
				+ "fragment fragment1 on Character {id appearsIn friends{id ...fragment3 ...fragment2 }}"//
				+ "fragment fragment2 on Character {id name(uppercase: &uppercaseTrue)}"//
				+ "fragment fragment3 on Character {appearsIn}"//
		);

		// Go, go, go
		List<CIP_Character_CIS> withoutParameters = GraphQLRequestAllGraphQLCases.execQuery(params)
				.getWithoutParameters();

		// Verification
		assertNotNull(withoutParameters);
		assertTrue(withoutParameters.size() > 0);
		assertTrue(
				withoutParameters.get(0) instanceof CTP_Droid_CTS || withoutParameters.get(0) instanceof CTP_Human_CTS);
		assertNotNull(withoutParameters.get(0).get__typename());
		assertNotNull(withoutParameters.get(0).getAppearsIn());
		assertNotNull(withoutParameters.get(0).getFriends());
		assertNull(withoutParameters.get(0).getName(), "name has not been requested");

		CIP_Character_CIS firstFriend = withoutParameters.get(0).getFriends().get(0);
		assertNotNull(firstFriend.getId());
		assertNotNull(firstFriend.getName());
		assertNotNull(firstFriend.getAppearsIn());
		assertNull(firstFriend.getFriends(), "friends has not been requested");
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_InlineAndGlobalFragments() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		GraphQLRequestAllGraphQLCases GraphQLRequestAllGraphQLCases = myQuery.getGraphQLRequest(""//
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
		List<CIP_Character_CIS> withoutParameters = GraphQLRequestAllGraphQLCases.execQuery(params)
				.getWithoutParameters();

		// Verification
		assertNotNull(withoutParameters);
		assertTrue(withoutParameters.size() > 0);
		assertTrue(withoutParameters.get(0) instanceof CTP_Droid_CTS, "Hope the order of the result won't change");
		assertEquals("Droid", withoutParameters.get(0).get__typename());
		assertNotNull(withoutParameters.get(0).getAppearsIn());
		assertNotNull(withoutParameters.get(0).getId());
		assertNotNull(withoutParameters.get(0).getFriends());
		assertNotNull(withoutParameters.get(0).getName(), "name is requested in inline fragment");
		assertNotNull(((CTP_Droid_CTS) withoutParameters.get(0)).getPrimaryFunction(),
				"primaryFunction is requested for droids");

		CIP_Character_CIS firstFriend = withoutParameters.get(0).getFriends().get(0);
		assertNotNull(firstFriend.getId());
		assertNotNull(firstFriend.getName(), "Requested for Droids");
		assertNull(firstFriend.getAppearsIn());
		assertNull(firstFriend.getFriends(), "friends of friends has not been requested");
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_InlineAndGlobalFragments_withOneOptionalParam_Droid()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		GraphQLRequestAllGraphQLCases GraphQLRequestAllGraphQLCases = myQuery.getGraphQLRequest(""//
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
		CIP_Character_CIS withOneOptionalParam = GraphQLRequestAllGraphQLCases.execQuery(params)
				.getWithOneOptionalParam();

		// Verification
		assertNotNull(withOneOptionalParam);
		assertTrue(withOneOptionalParam instanceof CTP_Droid_CTS, "we've ask for thas in input's type");
		assertEquals("Droid", withOneOptionalParam.get__typename());
		assertTrue(withOneOptionalParam instanceof CTP_Droid_CTS);
		assertNotNull(withOneOptionalParam.getAppearsIn());
		assertNotNull(withOneOptionalParam.getId());
		assertNotNull(withOneOptionalParam.getFriends(), "friends have been requested onyl for droids");
		assertNotNull(withOneOptionalParam.getName(), "name has been requested in inline fragment");
		assertNotNull(((CTP_Droid_CTS) withOneOptionalParam).getPrimaryFunction(),
				"primaryFunction is requested for droids");
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_InlineAndGlobalFragments_withOneOptionalParam_Human()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		GraphQLRequestAllGraphQLCases GraphQLRequestAllGraphQLCases = myQuery.getGraphQLRequest(""//
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
		CIP_Character_CIS withOneOptionalParam = GraphQLRequestAllGraphQLCases.execQuery(params)
				.getWithOneOptionalParam();

		// Verification
		assertNotNull(withOneOptionalParam);
		assertTrue(withOneOptionalParam instanceof CTP_Human_CTS, "we've ask for thas in input's type");
		assertEquals("Human", withOneOptionalParam.get__typename());
		assertTrue(withOneOptionalParam instanceof CTP_Human_CTS);
		assertNotNull(withOneOptionalParam.getAppearsIn());
		assertNotNull(withOneOptionalParam.getId());
		assertNull(withOneOptionalParam.getFriends(), "friends have been requested onyl for droids");
		assertNotNull(withOneOptionalParam.getName(), "name has been requested in inline fragment for human");
		assertNotNull(((CTP_Human_CTS) withOneOptionalParam).getHomePlanet(), "homePlanet is requested for humans");
	}
}
