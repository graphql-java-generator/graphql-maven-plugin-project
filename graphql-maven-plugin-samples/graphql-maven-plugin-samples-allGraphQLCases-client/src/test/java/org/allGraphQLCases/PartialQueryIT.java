/**
 * 
 */
package org.allGraphQLCases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.allGraphQLCases.client.AllFieldCases;
import org.allGraphQLCases.client.EnumWithReservedJavaKeywordAsValues;
import org.allGraphQLCases.client.FieldParameterInput;
import org.allGraphQLCases.client.util.AnotherMutationTypeExecutorAllGraphQLCases;
import org.allGraphQLCases.client.util.GraphQLRequest;
import org.allGraphQLCases.client.util.MyQueryTypeExecutorAllGraphQLCases;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * @author etienne-sf
 */
// Adding "webEnvironment = SpringBootTest.WebEnvironment.NONE" avoid this error:
// "No qualifying bean of type 'ReactiveClientRegistrationRepository' available"
// More details here: https://stackoverflow.com/questions/62558552/error-when-using-enablewebfluxsecurity-in-springboot
@SpringBootTest(classes = SpringTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Execution(ExecutionMode.CONCURRENT)
public class PartialQueryIT {

	@Autowired
	MyQueryTypeExecutorAllGraphQLCases queryType;
	@Autowired
	AnotherMutationTypeExecutorAllGraphQLCases mutation;

	/**
	 * Test of list that contain list, when sending request and receiving response
	 * 
	 * @throws GraphQLRequestPreparationException
	 * @throws GraphQLRequestExecutionException
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_ListOfList() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		GraphQLRequest graphQLRequest = queryType.getWithListOfListGraphQLRequest("{matrix}");
		//
		List<List<Double>> matrixSrc = new ArrayList<>();
		for (int i = 0; i <= 2; i += 1) {
			List<Double> sublist = new ArrayList<>();
			for (int j = 0; j <= 3; j += 1) {
				sublist.add((double) (i + j));
			}
			matrixSrc.add(sublist);
		} // for

		// Go, go, go
		AllFieldCases allFieldCases = queryType.withListOfList(graphQLRequest, matrixSrc);

		// Verification
		assertNotNull(allFieldCases);
		List<List<Double>> matrixVerif = allFieldCases.getMatrix();
		assertNotNull(matrixVerif);
		assertEquals(3, matrixVerif.size());
		for (int i = 0; i <= 2; i += 1) {
			List<Double> sublist = matrixVerif.get(i);
			assertEquals(4, sublist.size());
			for (int j = 0; j <= 3; j += 1) {
				assertEquals(i + j, sublist.get(j));
			}
		} // for
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue51_ListID() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		List<String> ids = new ArrayList<>();
		ids.add("11111111-1111-1111-1111-111111111111");
		ids.add("22222222-2222-2222-2222-222222222222");
		ids.add("33333333-3333-3333-3333-333333333333");
		//
		GraphQLRequest graphQLRequest = mutation.getDeleteSnacksGraphQLRequest("");

		// Go, go, go
		Boolean ret = mutation.deleteSnacks(graphQLRequest, ids);

		// Verification
		assertTrue(ret);
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue65_ListID() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		List<FieldParameterInput> inputs = new ArrayList<>();
		inputs.add(FieldParameterInput.builder().withUppercase(true).build());
		inputs.add(FieldParameterInput.builder().withUppercase(false).build());
		//
		GraphQLRequest graphQLRequest = queryType.getAllFieldCasesGraphQLRequest("{issue65(inputs: &inputs)}");

		// Go, go, go
		AllFieldCases ret = queryType.allFieldCases(graphQLRequest, null, "inputs", inputs);

		// Verification
		assertEquals(inputs.size(), ret.getIssue65().size());
		assertEquals(ret.getIssue65().get(0).getName().toUpperCase(), ret.getIssue65().get(0).getName(),
				"The first name should be in uppercase");
		assertNotEquals(ret.getIssue65().get(1).getName().toUpperCase(), ret.getIssue65().get(1).getName(),
				"The second name should NOT be in uppercase");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue53_DateQueryParameter() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2018, 02, 01);// Month is 0-based, so this date is 2018, January the first
		Date date = cal.getTime();
		//
		GraphQLRequest graphQLRequest = queryType.getIssue53GraphQLRequest("");

		// Go, go, go
		Date ret = queryType.issue53(graphQLRequest, date);

		// Verification
		assertEquals(date, ret);
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue82_IntParameter() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		GraphQLRequest graphQLRequest = queryType.getWithOneMandatoryParamDefaultValueGraphQLRequest("");

		// Go, go, go
		Integer ret = queryType.withOneMandatoryParamDefaultValue(graphQLRequest, 2);

		// Verification
		assertEquals(2, ret);
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue139_EnumValueIf() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Go, go, go
		EnumWithReservedJavaKeywordAsValues response = queryType.enumWithReservedJavaKeywordAsValues("");

		// Verification
		assertEquals(EnumWithReservedJavaKeywordAsValues._if, response);
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue139_EnumValueListOfJavaReservedKeywords_withNullParams()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Go, go, go
		List<EnumWithReservedJavaKeywordAsValues> response = queryType.listOfEnumWithReservedJavaKeywordAsValues("",
				null, null);
		// the two parameters are null. Their default values are:
		// param1: EnumWithReservedJavaKeywordAsValues=abstract,
		// param2: [EnumWithReservedJavaKeywordAsValues]=[assert,boolean]
		// Let's check that.

		// Verification
		assertNotNull(response);
		assertEquals(3, response.size());
		assertEquals(EnumWithReservedJavaKeywordAsValues._abstract, response.get(0));
		assertEquals(EnumWithReservedJavaKeywordAsValues._assert, response.get(1));
		assertEquals(EnumWithReservedJavaKeywordAsValues._boolean, response.get(2));
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue139_EnumValueListOfJavaReservedKeywords()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Go, go, go
		List<EnumWithReservedJavaKeywordAsValues> response = queryType.listOfEnumWithReservedJavaKeywordAsValues("", //
				/* param1 */EnumWithReservedJavaKeywordAsValues._return, //
				/* param2 */ Arrays.asList(EnumWithReservedJavaKeywordAsValues._byte,
						EnumWithReservedJavaKeywordAsValues._const));

		// Verification
		assertNotNull(response);
		assertEquals(3, response.size());
		assertEquals(EnumWithReservedJavaKeywordAsValues._return, response.get(0));
		assertEquals(EnumWithReservedJavaKeywordAsValues._byte, response.get(1));
		assertEquals(EnumWithReservedJavaKeywordAsValues._const, response.get(2));
	}

}
