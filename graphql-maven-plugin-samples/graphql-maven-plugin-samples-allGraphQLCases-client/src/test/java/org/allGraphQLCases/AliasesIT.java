/**
 * 
 */
package org.allGraphQLCases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.allGraphQLCases.client.AllFieldCases;
import org.allGraphQLCases.client.AllFieldCasesWithoutIdSubtype;
import org.allGraphQLCases.client.FieldParameterInput;
import org.allGraphQLCases.client.util.AnotherMutationTypeExecutor;
import org.allGraphQLCases.client.util.GraphQLRequest;
import org.allGraphQLCases.client.util.MyQueryTypeExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
public class AliasesIT {
	MyQueryTypeExecutor queryType;
	AnotherMutationTypeExecutor mutation;

	ApplicationContext ctx;

	@BeforeEach
	void setup() {
		ctx = new AnnotationConfigApplicationContext(SpringTestConfig.class);

		// For some tests, we need to execute additional partialQueries
		queryType = ctx.getBean(MyQueryTypeExecutor.class);
		assertNotNull(queryType);
		mutation = ctx.getBean(AnotherMutationTypeExecutor.class);
		assertNotNull(mutation);
	}

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
		GraphQLRequest graphQLRequest = queryType.getWithListOfListGraphQLRequest("{matrix2 : matrix}");
		//
		List<List<Double>> matrixSrc = new ArrayList<>();
		for (int i = 0; i <= 2; i += 1) {
			List<Double> sublist = new ArrayList<>();
			for (int j = 0; j <= 3; j += 1) {
				sublist.add((double) (i + j));
			}
			matrixSrc.add(sublist);
		} // for
		Predicate<List<List<Double>>> checkMatrix = new Predicate<List<List<Double>>>() {
			@Override
			public boolean test(List<List<Double>> matrixVerif) {
				for (int i = 0; i <= 2; i += 1) {
					List<Double> sublist = matrixVerif.get(i);
					assertEquals(4, sublist.size());
					for (int j = 0; j <= 3; j += 1) {
						assertEquals(i + j, sublist.get(j));
					}
				}
				return true;
			}
		};

		// Go, go, go
		AllFieldCases allFieldCases = queryType.withListOfList(graphQLRequest, matrixSrc);

		// Verification

		assertNotNull(allFieldCases);
		assertNull(allFieldCases.getMatrix(), "No matrix field in the response");
		@SuppressWarnings("unchecked")
		List<List<Double>> matrixVerif = (List<List<Double>>) allFieldCases.getAliasValue("matrix2", Double.class);
		assertNotNull(matrixVerif);
		assertTrue(checkMatrix.test(matrixVerif), "Check of the returned matrix content");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue65_ListID() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		List<FieldParameterInput> inputs = new ArrayList<>();
		inputs.add(FieldParameterInput.builder().withUppercase(true).build());
		inputs.add(FieldParameterInput.builder().withUppercase(false).build());
		//
		GraphQLRequest graphQLRequest = queryType
				.getAllFieldCasesGraphQLRequest("{alias65:issue65(inputs: &inputs) issue65(inputs: &inputs)}");

		// Go, go, go
		AllFieldCases ret = queryType.allFieldCases(graphQLRequest, null, "inputs", inputs);

		// Verification
		List<AllFieldCasesWithoutIdSubtype> issue65 = ret.getIssue65();
		assertEquals(inputs.size(), issue65.size());
		assertEquals(issue65.get(0).getName().toUpperCase(), issue65.get(0).getName(),
				"The first name should be in uppercase");
		assertNotEquals(issue65.get(1).getName().toUpperCase(), issue65.get(1).getName(),
				"The second name should NOT be in uppercase");

		@SuppressWarnings("unchecked")
		List<AllFieldCasesWithoutIdSubtype> alias65 = (List<AllFieldCasesWithoutIdSubtype>) ret.getAliasValue("alias65",
				AllFieldCasesWithoutIdSubtype.class);
		assertEquals(inputs.size(), alias65.size());
		assertEquals(alias65.get(0).getName().toUpperCase(), alias65.get(0).getName(),
				"The first name should be in uppercase");
		assertNotEquals(alias65.get(1).getName().toUpperCase(), alias65.get(1).getName(),
				"The second name should NOT be in uppercase");

	}

}
