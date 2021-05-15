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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import org.allGraphQLCases.client.AllFieldCases;
import org.allGraphQLCases.client.AllFieldCasesInput;
import org.allGraphQLCases.client.AllFieldCasesWithoutIdSubtype;
import org.allGraphQLCases.client.Character;
import org.allGraphQLCases.client.Episode;
import org.allGraphQLCases.client.FieldParameterInput;
import org.allGraphQLCases.client.MyQueryType;
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
	AnotherMutationTypeExecutor mutationType;

	ApplicationContext ctx;

	@BeforeEach
	void setup() {
		ctx = new AnnotationConfigApplicationContext(SpringTestConfig.class);

		// For some tests, we need to execute additional partialQueries
		queryType = ctx.getBean(MyQueryTypeExecutor.class);
		assertNotNull(queryType);
		mutationType = ctx.getBean(AnotherMutationTypeExecutor.class);
		assertNotNull(mutationType);
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
		List<List<Double>> matrixVerif = (List<List<Double>>) allFieldCases.getAliasValue("matrix2");
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
		List<AllFieldCasesWithoutIdSubtype> alias65 = (List<AllFieldCasesWithoutIdSubtype>) ret
				.getAliasValue("alias65");
		assertEquals(inputs.size(), alias65.size());
		assertEquals(alias65.get(0).getName().toUpperCase(), alias65.get(0).getName(),
				"The first name should be in uppercase");
		assertNotEquals(alias65.get(1).getName().toUpperCase(), alias65.get(1).getName(),
				"The second name should NOT be in uppercase");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_FullQuery() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		GraphQLRequest multipleQueriesRequest = queryType.getGraphQLRequest("{"//
				+ " directiveOnQuery (uppercase: false) @testDirective(value:&value, anotherValue:?anotherValue)"//
				+ " withOneOptionalParam {aliasId:id id aliasName:name name aliasAppearsIn:appearsIn appearsIn aliasFriends:friends {id name} friends {aliasId:id id aliasName:name name aliasFriends:friends {id name} friends {aliasId:id id aliasName:name name}}}"//
				+ " queryAlias:withOneOptionalParam  {aliasId:id id aliasName:name name aliasAppearsIn2:appearsIn appearsIn aliasFriends:friends {id name} friends {aliasId:id id aliasName:name name aliasFriends:friends {id name} friends {aliasId2:id id aliasName2:name name}}}"//
				+ " withoutParameters {appearsIn @skip(if: &skipAppearsIn) name @skip(if: &skipName) }"//
				+ "}");

		// Go, go, go
		MyQueryType resp = multipleQueriesRequest.execQuery( //
				"value", "An expected returned string", //
				"skipAppearsIn", true, //
				"skipName", false);

		// Verification
		assertNotNull(resp.getWithOneOptionalParam());
		assertNotNull(resp.getAliasValue("queryAlias"));
		assertNotNull(resp.getWithoutParameters());
		//
		// withOneOptionalParam
		Character withOneOptionalParam = resp.getWithOneOptionalParam();
		//
		assertNotNull(withOneOptionalParam.getAliasValue("aliasId"));
		assertTrue(withOneOptionalParam.getAliasValue("aliasId") instanceof String);
		//
		assertNotNull(withOneOptionalParam.getAliasValue("aliasName"));
		assertTrue(withOneOptionalParam.getAliasValue("aliasName") instanceof String);
		//
		assertNotNull(withOneOptionalParam.getName());
		//
		assertNotNull(withOneOptionalParam.getAliasValue("aliasAppearsIn"));
		assertNull(withOneOptionalParam.getAliasValue("aliasAppearsIn2"));
		assertTrue(withOneOptionalParam.getAliasValue("aliasAppearsIn") instanceof List);
		assertTrue(((List<?>) withOneOptionalParam.getAliasValue("aliasAppearsIn")).size() > 0);
		assertTrue(((List<?>) withOneOptionalParam.getAliasValue("aliasAppearsIn")).get(0) instanceof Episode);
		//
		assertNotNull(withOneOptionalParam.getAppearsIn());
		//
		assertNotNull(withOneOptionalParam.getAliasValue("aliasFriends"));
		assertTrue(withOneOptionalParam.getAliasValue("aliasFriends") instanceof List);
		assertTrue(((List<?>) withOneOptionalParam.getAliasValue("aliasFriends")).size() > 0);
		assertTrue(((List<?>) withOneOptionalParam.getAliasValue("aliasFriends")).get(0) instanceof Character);
		Character ch = (Character) ((List<?>) withOneOptionalParam.getAliasValue("aliasFriends")).get(0);
		assertNotNull(ch.getId());
		assertNotNull(ch.getName());
		//
		assertNotNull(withOneOptionalParam.getFriends());
		assertTrue(withOneOptionalParam.getFriends().size() > 0);
		Character charLevel1 = withOneOptionalParam.getFriends().get(0);
		assertNotNull(charLevel1.getAliasValue("aliasId"));
		assertNull(charLevel1.getAliasValue("aliasId2"));
		assertNotNull(charLevel1.getId());
		assertEquals(charLevel1.getAliasValue("aliasId"), charLevel1.getId());
		assertNotNull(charLevel1.getAliasValue("aliasName"));
		assertNotNull(charLevel1.getName());
		assertEquals(charLevel1.getAliasValue("aliasName"), charLevel1.getName());
		assertNotNull(charLevel1.getAliasValue("aliasFriends"));
		assertNotNull(charLevel1.getFriends());
		assertTrue(charLevel1.getFriends().size() > 0);
		Character charLevel2 = charLevel1.getFriends().get(0);
		assertNotNull(charLevel2.getAliasValue("aliasId"));
		assertNull(charLevel2.getAliasValue("aliasId2"));
		assertNotNull(charLevel2.getId());
		assertEquals(charLevel2.getAliasValue("aliasId"), charLevel2.getId());
		assertNotNull(charLevel2.getAliasValue("aliasName"));
		assertNull(charLevel2.getAliasValue("aliasName2"));
		assertNotNull(charLevel2.getName());
		assertEquals(charLevel2.getAliasValue("aliasName"), charLevel2.getName());
		assertNull(charLevel2.getAliasValue("aliasFriends"));
		//
		// Let's check the content of queryAlias (everything is the same out of the deepest level of friends)
		Character queryAlias = (Character) resp.getAliasValue("queryAlias");
		//
		assertNotNull(queryAlias.getAliasValue("aliasId"));
		assertTrue(queryAlias.getAliasValue("aliasId") instanceof String);
		//
		assertNotNull(queryAlias.getAliasValue("aliasName"));
		assertTrue(queryAlias.getAliasValue("aliasName") instanceof String);
		//
		assertNotNull(queryAlias.getName());
		//
		assertNull(queryAlias.getAliasValue("aliasAppearsIn"));
		assertNotNull(queryAlias.getAliasValue("aliasAppearsIn2"));
		assertTrue(queryAlias.getAliasValue("aliasAppearsIn2") instanceof List);
		assertTrue(((List<?>) queryAlias.getAliasValue("aliasAppearsIn2")).size() > 0);
		assertTrue(((List<?>) queryAlias.getAliasValue("aliasAppearsIn2")).get(0) instanceof Episode);
		//
		assertNotNull(queryAlias.getAppearsIn());
		//
		assertNotNull(queryAlias.getAliasValue("aliasFriends"));
		assertTrue(queryAlias.getAliasValue("aliasFriends") instanceof List);
		assertTrue(((List<?>) queryAlias.getAliasValue("aliasFriends")).size() > 0);
		assertTrue(((List<?>) queryAlias.getAliasValue("aliasFriends")).get(0) instanceof Character);
		ch = (Character) ((List<?>) queryAlias.getAliasValue("aliasFriends")).get(0);
		assertNotNull(ch.getId());
		assertNotNull(ch.getName());
		//
		assertNotNull(queryAlias.getFriends());
		assertTrue(queryAlias.getFriends().size() > 0);
		//
		charLevel1 = queryAlias.getFriends().get(0);
		assertNotNull(charLevel1.getAliasValue("aliasId"));
		assertNull(charLevel1.getAliasValue("aliasId2"));
		assertNotNull(charLevel1.getId());
		assertEquals(charLevel1.getAliasValue("aliasId"), charLevel1.getId());
		assertNotNull(charLevel1.getAliasValue("aliasName"));
		assertNotNull(charLevel1.getName());
		assertEquals(charLevel1.getAliasValue("aliasName"), charLevel1.getName());
		assertNotNull(charLevel1.getAliasValue("aliasFriends"));
		assertNotNull(charLevel1.getFriends());
		assertTrue(charLevel1.getFriends().size() > 0);
		//
		charLevel2 = charLevel1.getFriends().get(0);
		assertNull(charLevel2.getAliasValue("aliasId"));
		assertNotNull(charLevel2.getAliasValue("aliasId2"));
		assertNotNull(charLevel2.getId());
		assertEquals(charLevel2.getAliasValue("aliasId2"), charLevel2.getId());
		assertNull(charLevel2.getAliasValue("aliasName"));
		assertNotNull(charLevel2.getAliasValue("aliasName2"));
		assertNotNull(charLevel2.getName());
		assertEquals(charLevel2.getAliasValue("aliasName2"), charLevel2.getName());
		assertNull(charLevel2.getAliasValue("aliasFriends"));
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_createHuman() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		Date date1 = new Calendar.Builder().setDate(2021, 5 - 1, 15).build().getTime();
		Date date2 = new Calendar.Builder().setDate(2021, 5 - 1, 16).build().getTime();
		AllFieldCasesInput inputType = AllFieldCasesInput.builder().withId(UUID.randomUUID().toString())
				.withName("the name").withAge((long) 666).withDate(date1).withDates(Arrays.asList(date1, date2))
				.build();
		GraphQLRequest createHuman = mutationType.getGraphQLRequest("mutation {"//
				+ " mutationAlias : createAllFieldCases(input:$inputType) {aliasId:id id aliasName:name name aliasAge:age age "
				+ "aliasDate:date date aliasDates:dates dates aliasNbComments:nbComments nbComments aliasComments:comments comments "
				+ "aliasBooleans:booleans booleans aliasMatrix:matrix matrix "//
				+ "}}");

		// // Go, go, go
		// AnotherMutationType resp = createHuman.execMutation("inputType", inputType);
		//
		// // Verification
		// assertNotNull(resp.getAliasValue("mutationAlias"));
		// assertNotNull(resp.getAliasValue("mutationAlias") instanceof AllFieldCasesInput);
		//
		// fail("not yet implemented");
	}

}