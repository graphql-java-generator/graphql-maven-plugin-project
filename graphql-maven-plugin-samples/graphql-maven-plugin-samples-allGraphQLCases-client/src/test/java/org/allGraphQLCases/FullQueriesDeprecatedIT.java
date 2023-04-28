package org.allGraphQLCases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.allGraphQLCases.client.CTP_AnotherMutationType_CTS;
import org.allGraphQLCases.client.CIP_Character_CIS;
import org.allGraphQLCases.client.CEP_Episode_CES;
import org.allGraphQLCases.client.CTP_Human_CTS;
import org.allGraphQLCases.client.CINP_HumanInput_CINS;
import org.allGraphQLCases.client.CTP_MyQueryType_CTS;
import org.allGraphQLCases.client.AnotherMutationTypeExecutorAllGraphQLCases;
import org.allGraphQLCases.client.MyQueryTypeExecutorAllGraphQLCases;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

//Adding "webEnvironment = SpringBootTest.WebEnvironment.NONE" avoid this error:
//"No qualifying bean of type 'ReactiveClientRegistrationRepository' available"
//More details here: https://stackoverflow.com/questions/62558552/error-when-using-enablewebfluxsecurity-in-springboot
@SpringBootTest(classes = SpringTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Execution(ExecutionMode.CONCURRENT)
class FullQueriesDeprecatedIT {

	@Autowired
	ApplicationContext ctx;

	@Autowired
	MyQueryTypeExecutorAllGraphQLCases myQuery;
	@Autowired
	AnotherMutationTypeExecutorAllGraphQLCases mutationType;

	ObjectResponse mutationWithDirectiveResponse;
	ObjectResponse mutationWithoutDirectiveResponse;
	ObjectResponse withDirectiveTwoParametersResponse;
	ObjectResponse multipleQueriesResponse;

	@BeforeEach
	void setup() throws GraphQLRequestPreparationException {

		// The response preparation should be somewhere in the application initialization code.
		mutationWithDirectiveResponse = mutationType.getResponseBuilder().withQueryResponseDef(//
				"mutation{createHuman (human: &humanInput) @testDirective(value:&value, anotherValue:?anotherValue)   "//
						+ "{id name appearsIn friends {id name}}}"//
		).build();

		mutationWithoutDirectiveResponse = mutationType.getResponseBuilder().withQueryResponseDef(//
				"mutation{createHuman (human: &humanInput) {id name appearsIn friends {id name}}}"//
		).build();

		withDirectiveTwoParametersResponse = myQuery.getResponseBuilder().withQueryResponseDef(
				"query{directiveOnQuery (uppercase: false) @testDirective(value:&value, anotherValue:?anotherValue)}")
				.build();

		multipleQueriesResponse = myQuery.getResponseBuilder().withQueryResponseDef("{"//
				+ " directiveOnQuery (uppercase: false) @testDirective(value:&value, anotherValue:?anotherValue)"//
				+ " withOneOptionalParam {id name appearsIn friends {id name}}"//
				+ " withoutParameters {appearsIn @skip(if: &skipAppearsIn) name @skip(if: &skipName) }"//
				+ "}").build();

	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void noDirective() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// Go, go, go
		CTP_MyQueryType_CTS resp = myQuery.exec("{directiveOnQuery}"); // Direct queries should be used only for very
																// simple cases

		// Verifications
		assertNotNull(resp);
		List<String> ret = resp.getDirectiveOnQuery();
		assertNotNull(ret);
		assertEquals(0, ret.size());
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void withDirectiveOneParameter() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// Go, go, go
		CTP_MyQueryType_CTS resp = myQuery.exec("{directiveOnQuery  (uppercase: true) @testDirective(value:&value)}", //
				"value", "the value", "skip", Boolean.FALSE);

		// Verifications
		assertNotNull(resp);
		List<String> ret = resp.getDirectiveOnQuery();
		assertNotNull(ret);
		assertEquals(1, ret.size());
		//
		assertEquals("THE VALUE", ret.get(0));
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void withDirectiveTwoParameters() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// Go, go, go
		CTP_MyQueryType_CTS resp = myQuery.exec(withDirectiveTwoParametersResponse, //
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

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void mutation() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		CINP_HumanInput_CINS input = new CINP_HumanInput_CINS();
		input.setName("a new name");
		List<CEP_Episode_CES> episodes = new ArrayList<>();
		episodes.add(CEP_Episode_CES.JEDI);
		episodes.add(CEP_Episode_CES.EMPIRE);
		episodes.add(CEP_Episode_CES.NEWHOPE);
		input.setAppearsIn(episodes);

		////////////////////////////////////////////////////////////////////////////////////////////////
		// WITHOUT DIRECTIVE

		// Go, go, go
		CTP_AnotherMutationType_CTS resp = mutationType.exec(mutationWithoutDirectiveResponse, "humanInput", input);

		// Verifications
		assertNotNull(resp);
		CTP_Human_CTS ret = resp.getCreateHuman();
		assertNotNull(ret);
		assertEquals("a new name", ret.getName());

		////////////////////////////////////////////////////////////////////////////////////////////////
		// WITH DIRECTIVE

		// Go, go, go
		resp = mutationType.exec(mutationWithDirectiveResponse, //
				"humanInput", input, //
				"value", "the mutation value", //
				"anotherValue", "the other mutation value");

		// Verifications
		assertNotNull(resp);
		ret = resp.getCreateHuman();
		assertNotNull(ret);
		assertEquals("the other mutation value", ret.getName());
	}

	/**
	 * Test of this multiple query request :
	 * 
	 * <PRE>
	 * {
	 * directiveOnQuery (uppercase: false) @testDirective(value:&value, anotherValue:?anotherValue)
	 * withOneOptionalParam {id name appearsIn friends {id name}}
	 * withoutParameters {appearsIn @skip(if: &skipAppearsIn) name @skip(if: &skipName) }
	 * }
	 * </PRE>
	 */
	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void multipleQueriesResponse() throws GraphQLRequestExecutionException {
		/*
		 * { directiveOnQuery (uppercase: false) @testDirective(value:&value, anotherValue:?anotherValue)
		 * 
		 * withOneOptionalParam {id name appearsIn friends {id name}}
		 * 
		 * withoutParameters {appearsIn @skip(if: &skipAppearsIn) name @skip(if: &skipName) }}
		 */

		////////////////////////////////////////////////////////////////////////////////////////////
		// Let's skip appearsIn but not name

		// Go, go, go
		CTP_MyQueryType_CTS resp = myQuery.exec(multipleQueriesResponse, //
				"value", "An expected returned string", //
				"skipAppearsIn", true, //
				"skipName", false);

		// Verification
		assertNotNull(resp);
		//
		List<String> directiveOnQuery = resp.getDirectiveOnQuery();
		assertEquals(1, directiveOnQuery.size());
		assertEquals("An expected returned string", directiveOnQuery.get(0));
		//
		CIP_Character_CIS withOneOptionalParam = resp.getWithOneOptionalParam();
		assertNotNull(withOneOptionalParam.getFriends());
		//
		List<CIP_Character_CIS> withoutParameters = resp.getWithoutParameters();
		assertNotNull(withoutParameters);
		assertTrue(withoutParameters.size() > 0);
		assertNull(withoutParameters.get(0).getAppearsIn());
		assertNotNull(withoutParameters.get(0).getName());

		////////////////////////////////////////////////////////////////////////////////////////////
		// Let's skip appearsIn but not name

		// Go, go, go
		resp = myQuery.exec(multipleQueriesResponse, //
				"value", "An expected returned string", //
				"skipAppearsIn", false, //
				"skipName", true);

		// Verification
		withoutParameters = resp.getWithoutParameters();
		assertNotNull(withoutParameters);
		assertTrue(withoutParameters.size() > 0);
		assertNotNull(withoutParameters.get(0).getAppearsIn());
		assertNull(withoutParameters.get(0).getName());
	}
}
