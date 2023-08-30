package org.allGraphQLCases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.allGraphQLCases.client.AnotherMutationTypeReactiveExecutorAllGraphQLCases;
import org.allGraphQLCases.client.CINP_FieldParameterInput_CINS;
import org.allGraphQLCases.client.CTP_AllFieldCases_CTS;
import org.allGraphQLCases.client.CTP_MyQueryType_CTS;
import org.allGraphQLCases.client.GraphQLReactiveRequestAllGraphQLCases;
import org.allGraphQLCases.client.MyQueryTypeReactiveExecutorAllGraphQLCases;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.client.GraphQlClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

//Adding "webEnvironment = SpringBootTest.WebEnvironment.NONE" avoid this error:
//"No qualifying bean of type 'ReactiveClientRegistrationRepository' available"
//More details here: https://stackoverflow.com/questions/62558552/error-when-using-enablewebfluxsecurity-in-springboot
@SpringBootTest(classes = SpringTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Execution(ExecutionMode.CONCURRENT)
class ReactiveQueriesIT {

	@Autowired
	MyQueryTypeReactiveExecutorAllGraphQLCases reactiveQueryExecutor;

	@Autowired
	AnotherMutationTypeReactiveExecutorAllGraphQLCases reactiveMutationExecutor;

	@Resource(name = "httpGraphQlClientAllGraphQLCases")
	GraphQlClient httpGraphQlClient;

	// Prepared full queries
	GraphQLReactiveRequestAllGraphQLCases reactiveMutationWithDirectiveRequest;
	GraphQLReactiveRequestAllGraphQLCases reactiveMutationWithoutDirectiveRequest;
	GraphQLReactiveRequestAllGraphQLCases reactiveWithDirectiveTwoParametersRequest;
	GraphQLReactiveRequestAllGraphQLCases reactiveMultipleQueriesRequest;

	public static class ExtensionValue {
		public String name;
		public String forname;
	}

	@BeforeEach
	void setup() throws GraphQLRequestPreparationException {
		String req;
		// The response preparation should be somewhere in the application initialization code.
		req = "mutation{createHuman (human: &humanInput) @testDirective(value:&value, anotherValue:?anotherValue)   "//
				+ "{id name appearsIn friends {id name}}}";//
		this.reactiveMutationWithDirectiveRequest = this.reactiveMutationExecutor.getGraphQLRequest(req);

		req = "mutation{createHuman (human: &humanInput) {id name appearsIn friends {id name}}}";
		this.reactiveMutationWithoutDirectiveRequest = this.reactiveMutationExecutor.getGraphQLRequest(req);

		req = "query{directiveOnQuery (uppercase: false) @testDirective(value:&value, anotherValue:?anotherValue)}";
		this.reactiveWithDirectiveTwoParametersRequest = this.reactiveMutationExecutor.getGraphQLRequest(req);

		req = "{"//
				+ " directiveOnQuery (uppercase: false) @testDirective(value:&value, anotherValue:?anotherValue)"//
				+ " withOneOptionalParam {id name appearsIn friends {id name}}"//
				+ " withoutParameters {appearsIn @skip(if: &skipAppearsIn) name @skip(if: &skipName) }"//
				+ "}";
		this.reactiveMultipleQueriesRequest = this.reactiveQueryExecutor.getGraphQLRequest(req);
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_fullQuery_noDirective_extensionsResponseField()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, JsonProcessingException {
		String request = "{directiveOnQuery}";

		// Direct queries should be used only for very simple cases
		CTP_MyQueryType_CTS resp = this.reactiveQueryExecutor.exec(request).block();

		// Verifications
		assertNotNull(resp);
		List<String> ret = resp.getDirectiveOnQuery();
		assertNotNull(ret);
		assertEquals(0, ret.size());
		//
		// The extensions field contains a CTP_Human_CTS instance, for the key "aValueToTestTheExtensionsField".
		// Check the org.allGraphQLCases.server.extensions.CustomBeans (creation of the customGraphQL Spring bean)
		assertNotNull(resp.getExtensions());
		assertNotNull(resp.getExtensionsAsMap());
		assertNotNull(resp.getExtensionsAsMap().get("aValueToTestTheExtensionsField"));
		ExtensionValue value = resp.getExtensionsField("aValueToTestTheExtensionsField", ExtensionValue.class);
		assertEquals("The name", value.name);
		assertEquals("The forname", value.forname);
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_fullQuery_withDirectiveOneParameter()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		String request = "{directiveOnQuery  (uppercase: true) @testDirective(value:&value)}";

		// Go, go, go

		// Direct queries should be used only for very simple cases, but you can do what you want... :)
		CTP_MyQueryType_CTS resp = this.reactiveQueryExecutor//
				.exec(request, "value", "the value", "skip", Boolean.FALSE) //$NON-NLS-3$
				.block();

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
	void test_preparedQuery_withDirectiveTwoParameters()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// Go, go, go
		CTP_MyQueryType_CTS resp = this.reactiveWithDirectiveTwoParametersRequest.execQuery( //
				"value", "the value", "anotherValue", "the other value", "skip", Boolean.TRUE).block();
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
	void test_preparedQuery_GraphQLVariable_directQuery()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		List<List<Double>> matrix = Arrays.asList(//
				null, //
				Arrays.asList(), //
				Arrays.asList(1.0), //
				Arrays.asList(4.0, 5.0, 6.0)//
		);

		// Go, go, go
		CTP_MyQueryType_CTS resp = this.reactiveQueryExecutor
				.exec("query queryWithAMatrix($matrixParam: [[Float]]!) {withListOfList(matrix:$matrixParam){matrix}}", //
						"matrixParam", matrix)
				.block();

		// Verifications
		List<List<Double>> ret = resp.getWithListOfList().getMatrix();
		assertNotNull(ret);
		assertEquals(4, ret.size());
		int i = 0;
		//
		assertNull(ret.get(i++));
		//
		List<Double> item = ret.get(i++);
		assertNotNull(item);
		assertEquals(0, item.size());
		//
		item = ret.get(i++);
		assertNotNull(item);
		assertEquals(1, item.size());
		assertEquals(1, item.get(0));
		//
		item = ret.get(i++);
		assertNotNull(item);
		assertEquals(3, item.size());
		assertEquals(4, item.get(0));
		assertEquals(5, item.get(1));
		assertEquals(6, item.get(2));
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_fullQuery_GraphQLVariable_directQuery_map()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		List<List<Double>> matrix = Arrays.asList(//
				null, //
				Arrays.asList(), //
				Arrays.asList(1.0), //
				Arrays.asList(4.0, 5.0, 6.0)//
		);
		Map<String, Object> map = new HashMap<>();
		map.put("matrixParam", matrix);

		// Go, go, go
		CTP_MyQueryType_CTS resp = this.reactiveQueryExecutor.execWithBindValues(
				"query queryWithAMatrix($matrixParam: [[Float]]!) {withListOfList(matrix:$matrixParam){matrix}}", map)
				.block();

		// Verifications
		List<List<Double>> ret = resp.getWithListOfList().getMatrix();
		assertNotNull(ret);
		assertEquals(4, ret.size());
		int i = 0;
		//
		assertNull(ret.get(i++));
		//
		List<Double> item = ret.get(i++);
		assertNotNull(item);
		assertEquals(0, item.size());
		//
		item = ret.get(i++);
		assertNotNull(item);
		assertEquals(1, item.size());
		assertEquals(1, item.get(0));
		//
		item = ret.get(i++);
		assertNotNull(item);
		assertEquals(3, item.size());
		assertEquals(4, item.get(0));
		assertEquals(5, item.get(1));
		assertEquals(6, item.get(2));
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_fullQuery_withDirectiveTwoParameters()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		Map<String, Object> params = new HashMap<>();
		params.put("uppercase", true);
		params.put("anotherValue", "another value with an antislash: \\");
		params.put("Value", "a first \"value\"");

		// Preparation
		GraphQLReactiveRequestAllGraphQLCases reactiveDirectiveOnQuery = this.reactiveMutationExecutor
				.getGraphQLRequest("query namedQuery($uppercase :\n" //
						+ "Boolean, \n\r"//
						+ " $Value :   String ! , $anotherValue:String) {directiveOnQuery (uppercase: $uppercase) @testDirective(value:$Value, anotherValue:$anotherValue)}");

		// Go, go, go
		CTP_MyQueryType_CTS resp = reactiveDirectiveOnQuery.execQuery(params).block();

		// Verifications
		assertNotNull(resp);
		List<String> ret = resp.getDirectiveOnQuery();
		assertNotNull(ret);
		assertEquals(2, ret.size());
		//
		assertEquals("A FIRST \"VALUE\"", ret.get(0));
		assertEquals("ANOTHER VALUE WITH AN ANTISLASH: \\", ret.get(1));
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_preparedPartialQuery_Issue65_ListID_array()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		List<CINP_FieldParameterInput_CINS> inputs = new ArrayList<>();
		inputs.add(CINP_FieldParameterInput_CINS.builder().withUppercase(true).build());
		inputs.add(CINP_FieldParameterInput_CINS.builder().withUppercase(false).build());
		//
		GraphQLReactiveRequestAllGraphQLCases reactivePartialQuery = this.reactiveQueryExecutor
				.getAllFieldCasesGraphQLRequest("{issue65(inputs: &inputs)}");

		// Go, go, go
		CTP_AllFieldCases_CTS ret = this.reactiveQueryExecutor
				.allFieldCases(reactivePartialQuery, null, "inputs", inputs).block().get();

		// Verification
		assertEquals(inputs.size(), ret.getIssue65().size());
		assertEquals(ret.getIssue65().get(0).getName().toUpperCase(), ret.getIssue65().get(0).getName(),
				"The first name should be in uppercase");
		assertNotEquals(ret.getIssue65().get(1).getName().toUpperCase(), ret.getIssue65().get(1).getName(),
				"The second name should NOT be in uppercase");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_preparedPartialQuery_Issue65_ListID_map()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		List<CINP_FieldParameterInput_CINS> inputs = new ArrayList<>();
		inputs.add(CINP_FieldParameterInput_CINS.builder().withUppercase(true).build());
		inputs.add(CINP_FieldParameterInput_CINS.builder().withUppercase(false).build());
		//
		Map<String, Object> map = new HashMap<>();
		map.put("inputs", inputs);
		//
		GraphQLReactiveRequestAllGraphQLCases reactivePartialQuery = this.reactiveQueryExecutor
				.getAllFieldCasesGraphQLRequest("{issue65(inputs: &inputs)}");

		// Go, go, go
		CTP_AllFieldCases_CTS ret = this.reactiveQueryExecutor
				.allFieldCasesWithBindValues(reactivePartialQuery, null, map).block().get();

		// Verification
		assertEquals(inputs.size(), ret.getIssue65().size());
		assertEquals(ret.getIssue65().get(0).getName().toUpperCase(), ret.getIssue65().get(0).getName(),
				"The first name should be in uppercase");
		assertNotEquals(ret.getIssue65().get(1).getName().toUpperCase(), ret.getIssue65().get(1).getName(),
				"The second name should NOT be in uppercase");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_directPartialQuery_Issue65_ListID_array()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		List<CINP_FieldParameterInput_CINS> inputs = new ArrayList<>();
		inputs.add(CINP_FieldParameterInput_CINS.builder().withUppercase(true).build());
		inputs.add(CINP_FieldParameterInput_CINS.builder().withUppercase(false).build());
		//

		// Go, go, go
		CTP_AllFieldCases_CTS ret = this.reactiveQueryExecutor
				.allFieldCases("{issue65(inputs: &inputs)}", null, "inputs", inputs).block().get();

		// Verification
		assertEquals(inputs.size(), ret.getIssue65().size());
		assertEquals(ret.getIssue65().get(0).getName().toUpperCase(), ret.getIssue65().get(0).getName(),
				"The first name should be in uppercase");
		assertNotEquals(ret.getIssue65().get(1).getName().toUpperCase(), ret.getIssue65().get(1).getName(),
				"The second name should NOT be in uppercase");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_directPartialQuery_Issue65_ListID_map()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		List<CINP_FieldParameterInput_CINS> inputs = new ArrayList<>();
		inputs.add(CINP_FieldParameterInput_CINS.builder().withUppercase(true).build());
		inputs.add(CINP_FieldParameterInput_CINS.builder().withUppercase(false).build());
		//
		Map<String, Object> map = new HashMap<>();
		map.put("inputs", inputs);

		// Go, go, go
		CTP_AllFieldCases_CTS ret = this.reactiveQueryExecutor
				.allFieldCasesWithBindValues("{issue65(inputs: &inputs)}", null, map).block().get();

		// Verification
		assertEquals(inputs.size(), ret.getIssue65().size());
		assertEquals(ret.getIssue65().get(0).getName().toUpperCase(), ret.getIssue65().get(0).getName(),
				"The first name should be in uppercase");
		assertNotEquals(ret.getIssue65().get(1).getName().toUpperCase(), ret.getIssue65().get(1).getName(),
				"The second name should NOT be in uppercase");
	}

}
