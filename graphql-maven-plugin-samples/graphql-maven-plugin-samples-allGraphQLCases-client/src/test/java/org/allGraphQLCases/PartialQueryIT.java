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
import java.util.Optional;
import java.util.UUID;

import org.allGraphQLCases.client.CEP_EnumWithReservedJavaKeywordAsValues_CES;
import org.allGraphQLCases.client.CINP_AllFieldCasesInput_CINS;
import org.allGraphQLCases.client.CINP_FieldParameterInput_CINS;
import org.allGraphQLCases.client.CTP_AllFieldCasesWithIdSubtype_CTS;
import org.allGraphQLCases.client.CTP_AllFieldCases_CTS;
import org.allGraphQLCases.client.CTP_ReservedJavaKeywordAllFieldCases_CTS;
import org.allGraphQLCases.client.util.AnotherMutationTypeExecutorAllGraphQLCases;
import org.allGraphQLCases.client.util.GraphQLRequestAllGraphQLCases;
import org.allGraphQLCases.client.util.MyQueryTypeExecutorAllGraphQLCases;
import org.allGraphQLCases.client.util.MyQueryTypeReactiveExecutorAllGraphQLCases;
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
	MyQueryTypeExecutorAllGraphQLCases queryExecutor;
	@Autowired
	MyQueryTypeReactiveExecutorAllGraphQLCases reactiveQueryType;

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
		GraphQLRequestAllGraphQLCases GraphQLRequestAllGraphQLCases = this.queryExecutor
				.getWithListOfListGraphQLRequest("{matrix}");
		//
		List<List<Double>> matrixSrc = new ArrayList<>();
		for (int i = 0; i <= 2; i += 1) {
			List<Double> sublist = new ArrayList<>();
			for (int j = 0; j <= 3; j += 1) {
				sublist.add((double) (i + j));
			}
			matrixSrc.add(sublist);
		} // for

		///////////////////////////////////////////////////////////////////
		// NON REACTIVE
		///////////////////////////////////////////////////////////////////

		// Go, go, go
		CTP_AllFieldCases_CTS allFieldCases = this.queryExecutor.withListOfList(GraphQLRequestAllGraphQLCases,
				matrixSrc);

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

		///////////////////////////////////////////////////////////////////
		// REACTIVE
		///////////////////////////////////////////////////////////////////

		// Go, go, go
		Optional<CTP_AllFieldCases_CTS> allFieldCasesReactive = this.reactiveQueryType
				.withListOfList(GraphQLRequestAllGraphQLCases, matrixSrc).block();

		// Verification
		assertTrue(allFieldCasesReactive.isPresent());
		matrixVerif = allFieldCasesReactive.get().getMatrix();
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
		GraphQLRequestAllGraphQLCases GraphQLRequestAllGraphQLCases = this.mutation.getDeleteSnacksGraphQLRequest("");

		// Go, go, go
		Boolean ret = this.mutation.deleteSnacks(GraphQLRequestAllGraphQLCases, ids);

		// Verification
		assertTrue(ret);
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue65_ListID() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		List<CINP_FieldParameterInput_CINS> inputs = new ArrayList<>();
		inputs.add(CINP_FieldParameterInput_CINS.builder().withUppercase(true).build());
		inputs.add(CINP_FieldParameterInput_CINS.builder().withUppercase(false).build());
		//
		GraphQLRequestAllGraphQLCases GraphQLRequestAllGraphQLCases = this.queryExecutor
				.getAllFieldCasesGraphQLRequest("{issue65(inputs: &inputs)}");

		// Go, go, go
		CTP_AllFieldCases_CTS ret = this.queryExecutor.allFieldCases(GraphQLRequestAllGraphQLCases, null, "inputs",
				inputs);

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
		GraphQLRequestAllGraphQLCases GraphQLRequestAllGraphQLCases = this.queryExecutor.getIssue53GraphQLRequest("");

		// Go, go, go
		Date ret = this.queryExecutor.issue53(GraphQLRequestAllGraphQLCases, date);

		// Verification
		assertEquals(date, ret);
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue82_IntParameter() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		GraphQLRequestAllGraphQLCases GraphQLRequestAllGraphQLCases = this.queryExecutor
				.getWithOneMandatoryParamDefaultValueGraphQLRequest("");

		// Go, go, go
		Integer ret = this.queryExecutor.withOneMandatoryParamDefaultValue(GraphQLRequestAllGraphQLCases, 2);

		// Verification
		assertEquals(2, ret);
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue139_PR177_FieldAsReservedKeyword()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		CINP_AllFieldCasesInput_CINS input = CINP_AllFieldCasesInput_CINS.builder()//
				.withId(UUID.randomUUID().toString())//
				.withName("name")//
				.withBreak("A string to check the return")//
				.withAge(3L)//
				.withDates(new ArrayList<>())//
				.withAliases(new ArrayList<>())//
				.withPlanets(new ArrayList<>())//
				.withMatrix(new ArrayList<>())//
				.build();

		// Go, go, go
		CTP_AllFieldCases_CTS response = this.queryExecutor.allFieldCases("{break(if:&if)}", input, "if", "if's value");

		// Verification
		assertEquals("A string to check the return (if=if's value)", response.getBreak());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue139_PR177_QueryAsReservedKeyword()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Go, go, go
		String response = this.queryExecutor._implements("", "A string to check the return");

		// Verification
		assertEquals("A string to check the return", response);
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue139_EnumValueIf() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Go, go, go
		CEP_EnumWithReservedJavaKeywordAsValues_CES response = this.queryExecutor
				.enumWithReservedJavaKeywordAsValues("");

		// Verification
		assertEquals(CEP_EnumWithReservedJavaKeywordAsValues_CES._if, response);
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue139_EnumValueListOfJavaReservedKeywords_withNullParams()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Go, go, go
		List<CEP_EnumWithReservedJavaKeywordAsValues_CES> response = this.queryExecutor
				.listOfEnumWithReservedJavaKeywordAsValues("", null, null);
		// the two parameters are null. Their default values are:
		// param1: CEP_EnumWithReservedJavaKeywordAsValues_CES=abstract,
		// param2: [CEP_EnumWithReservedJavaKeywordAsValues_CES]=[assert,boolean]
		// Let's check that.

		// Verification
		assertNotNull(response);
		assertEquals(3, response.size());
		assertEquals(CEP_EnumWithReservedJavaKeywordAsValues_CES._abstract, response.get(0));
		assertEquals(CEP_EnumWithReservedJavaKeywordAsValues_CES._assert, response.get(1));
		assertEquals(CEP_EnumWithReservedJavaKeywordAsValues_CES._boolean, response.get(2));
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue139_EnumValueListOfJavaReservedKeywords()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Go, go, go
		List<CEP_EnumWithReservedJavaKeywordAsValues_CES> response = this.queryExecutor
				.listOfEnumWithReservedJavaKeywordAsValues("", //
						/* param1 */CEP_EnumWithReservedJavaKeywordAsValues_CES._return, //
						/* param2 */ Arrays.asList(CEP_EnumWithReservedJavaKeywordAsValues_CES._byte,
								CEP_EnumWithReservedJavaKeywordAsValues_CES._const));

		// Verification
		assertNotNull(response);
		assertEquals(3, response.size());
		assertEquals(CEP_EnumWithReservedJavaKeywordAsValues_CES._return, response.get(0));
		assertEquals(CEP_EnumWithReservedJavaKeywordAsValues_CES._byte, response.get(1));
		assertEquals(CEP_EnumWithReservedJavaKeywordAsValues_CES._const, response.get(2));
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue166_FieldAsJavaReservedKeywords()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Go, go, go
		CTP_ReservedJavaKeywordAllFieldCases_CTS response = this.queryExecutor.reservedJavaKeywordAllFieldCases(""//
				+ "{" //
				+ "  if "// the if type is an enum (Unit)
				+ "  nonJavaKeywordField {id} " // The nonJavaKeywordField field is an interface (WithID)
				+ "  implements {id} " // The implements field is an interface (WithID)
				+ "  import "// The import field is a scalar (String)
				+ "  instanceof "// The instanceof field is custom scalar (Date)
				+ "  int {id name} "// The int field is an object type (Human)
				+ "  interface {"// The interface field is an union (AnyCharacter). Queries on union must specify what
									// field should be returned, depending on the returned type.
				+ "      ... on Human {id name}" //
				+ "      ... on Droid {id name}" //
				+ "  }" //
				+ "}");

		// Verification
		assertNotNull(response);
		assertNotNull(response.get__typename());
		assertNotNull(response.getIf());
		assertNotNull(response.getNonJavaKeywordField());
		assertNotNull(response.getImplements());
		assertNotNull(response.getImport());
		assertNotNull(response.getInstanceof());
		assertNotNull(response.getInt());
		assertNotNull(response.getInterface());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_oneWithoutFieldParameter() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		CTP_AllFieldCasesWithIdSubtype_CTS allFieldCasesWithIdSubtype = this.queryExecutor
				.allFieldCases("{oneWithoutFieldParameter}", null).getOneWithoutFieldParameter();

		assertNotNull(allFieldCasesWithIdSubtype);
	}

}
