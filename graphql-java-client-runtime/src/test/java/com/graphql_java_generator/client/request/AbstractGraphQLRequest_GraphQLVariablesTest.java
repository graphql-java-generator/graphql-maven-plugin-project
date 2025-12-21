package com.graphql_java_generator.client.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.graphql_java_generator.client.SpringContextBean;
import com.graphql_java_generator.domain.client.allGraphQLCases.MyQueryTypeExecutorAllGraphQLCases;
import com.graphql_java_generator.domain.client.forum.GraphQLRequest;
import com.graphql_java_generator.domain.client.forum.MemberType;
import com.graphql_java_generator.domain.client.forum.PostInput;
import com.graphql_java_generator.domain.client.forum.Query;
import com.graphql_java_generator.domain.client.forum.RegistriesInitializer;
import com.graphql_java_generator.domain.client.forum.TopicPostInput;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

@Execution(ExecutionMode.CONCURRENT)
class AbstractGraphQLRequest_GraphQLVariablesTest {

	Query queryType;
	Map<String, Object> params;

	@SuppressWarnings("unchecked")
	@BeforeEach
	void setup() throws GraphQLRequestPreparationException {
		queryType = new Query();

		params = new HashMap<>();
		params.put("memberName", "a member Name");
		params.put("sinceParam", new GregorianCalendar(1900, 10 - 1, 24).getTime());

		ApplicationContext applicationContext = mock(ApplicationContext.class);
		when(applicationContext.getBean(anyString(), any(Class.class))).thenReturn(null);
		SpringContextBean.setApplicationContext(applicationContext);
	}

	@Test
	void testBuild_withGraphQLVariables()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, JsonProcessingException {
		// Go, go, go
		// This query is not a GraphQL valid request, as the $post and $anIntParam are not used. But it's enough for
		// this unit test
		AbstractGraphQLRequest graphQLRequest = new GraphQLRequest(null,
				"mutation crPst  ($post: PostInput!, $anIntParam: Int){createPost(post: $post){id date author{id}}}");
		RegistriesInitializer.initializeAllRegistries();
		TopicPostInput topicPostInput = TopicPostInput.builder().withAuthorId("12")
				.withDate(new GregorianCalendar(2021, 3 - 1, 13).getTime()).withPubliclyAvailable(true)
				.withTitle("a \"title\"").withContent("some content with an antislash: \\").build();
		PostInput inputPost = PostInput.builder().withTopicId("22").withInput(topicPostInput).build();
		//
		Map<String, Object> params = new HashMap<>();
		params.put("post", inputPost);
		params.put("anIntParam", 666);// This

		// Verification
		assertEquals(0, graphQLRequest.aliasFields.size());
		AbstractGraphQLRequest_allGraphQLCasesTest.checkPayload(graphQLRequest.getPayload(params), ""//
				+ "mutation crPst($post:PostInput!,$anIntParam:Int){createPost(post:$post){id date author{id __typename} __typename}}",
				params, //
				null);
	}

	@Test
	void testBuild_withNameAndGraphQLVariables()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, JsonProcessingException {
		// Go, go, go
		// This query is not a GraphQL valid request, as the $post and $anIntParam are not used. But it's enough for
		// this unit test
		AbstractGraphQLRequest graphQLRequest = new GraphQLRequest(null,
				"query titi($post: PostInput!, $anIntParam: Int, $aCustomScalar : [ [   Date ! ]] !, $anEnum: MemberType, $aDate: Date!) {boards{topics{id}}}");
		RegistriesInitializer.initializeAllRegistries();
		TopicPostInput topicPostInput = TopicPostInput.builder().withAuthorId("12")
				.withDate(new GregorianCalendar(2021, 3 - 1, 13).getTime()).withPubliclyAvailable(true)
				.withTitle("a title").withContent("some content").build();
		PostInput inputPost = PostInput.builder().withTopicId("22").withInput(topicPostInput).build();
		//
		List<List<Date>> dates = Arrays.asList(
				Arrays.asList(new GregorianCalendar(2021, 4 - 1, 1).getTime(),
						new GregorianCalendar(2021, 4 - 1, 2).getTime()),
				Arrays.asList(new GregorianCalendar(2021, 4 - 1, 3).getTime(),
						new GregorianCalendar(2021, 4 - 1, 4).getTime()));
		//
		Date aDate = new GregorianCalendar(2021, 10 - 1, 11).getTime();
		//
		Map<String, Object> params = new HashMap<>();
		params.put("post", inputPost);
		params.put("anIntParam", 666);
		params.put("aCustomScalar", dates);
		params.put("anEnum", MemberType.ADMIN);
		params.put("aDate", aDate);

		// Verification
		assertEquals(0, graphQLRequest.aliasFields.size());
		AbstractGraphQLRequest_allGraphQLCasesTest.checkPayload(graphQLRequest.getPayload(params), ""//
				+ "query titi($post:PostInput!,$anIntParam:Int,$aCustomScalar:[[Date!]]!,$anEnum:MemberType,$aDate:Date!){boards{topics{id __typename} __typename}}",
				params, //
				null);
	}

	@Test
	void testBuild_withNonProvidedGraphQLVariables()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, JsonProcessingException {
		// Go, go, go
		// This query is not a GraphQL valid request, as the $post and $anIntParam are not used. But it's enough for
		// this unit test
		AbstractGraphQLRequest graphQLRequest = new GraphQLRequest(null,
				"query titi($param:String) {boards{topics{id}}}");

		// Verification (with a provided variable)
		Map<String, Object> map = new HashMap<>();
		map.put("param", "A value");
		AbstractGraphQLRequest_allGraphQLCasesTest.checkPayload(graphQLRequest.getPayload(map), ""//
				+ "query titi($param:String){boards{topics{id __typename} __typename}}", map, //
				null);

		// Verification (without any variables, that is: no map provided)
		AbstractGraphQLRequest_allGraphQLCasesTest.checkPayload(graphQLRequest.getPayload(null), ""//
				+ "query titi($param:String){boards{topics{id __typename} __typename}}", null, //
				null);

		// Verification (with a non provided variable, that is: a empty map)
		AbstractGraphQLRequest_allGraphQLCasesTest.checkPayload(graphQLRequest.getPayload(new HashMap<>()), ""//
				+ "query titi($param:String){boards{topics{id __typename} __typename}}", null, //
				null);

	}

	@Test
	void testBuild_withDefaultValues()
			throws GraphQLRequestPreparationException, JsonProcessingException, GraphQLRequestExecutionException {
		// Creating a MyQueryTypeExecutorMySchema is mandatory to initialize the GraphQLTypeMappingRegistry
		new MyQueryTypeExecutorAllGraphQLCases();

		// Go, go, go
		// This query is not a GraphQL valid request, as the $post and $anIntParam are not used.
		// But it's enough for this unit test
		AbstractGraphQLRequest graphQLRequest = new com.graphql_java_generator.domain.client.allGraphQLCases.GraphQLRequest(
				null, """
						query myQueryName(
							$nbItemsParam: Long! = 666,
							$dateParam: MyCustomScalarForADate = "2025-11-17",
							$datesParam: [MyCustomScalarForADate]! = ["2025-11-18"],
							$uppercaseNameParam: Boolean = true,
							$textToAppendToTheNameParam: String = "default value"
							)
						{
							allFieldCases {
								listWithIdSubTypes(
									nbItems: $nbItemsParam,
									date: $dateParam,
									dates: $datesParam,
									uppercaseName: $uppercaseNameParam,
									textToAppendToTheName: $textToAppendToTheNameParam)
								{
									name
									date
									dates
								}
							}
						}
						""");

		//
		List<List<Date>> dates = Arrays.asList(
				Arrays.asList(new GregorianCalendar(2025, 11 - 1, 1).getTime(),
						new GregorianCalendar(2025, 11 - 1, 2).getTime()),
				Arrays.asList(new GregorianCalendar(2025, 11 - 1, 3).getTime(),
						new GregorianCalendar(2025, 11 - 1, 4).getTime()));
		//
		Date aDate = new GregorianCalendar(2025, 12 - 1, 11).getTime();
		//
		Map<String, Object> params = new HashMap<>();
		params.put("nbItemsParam", (long) 2);
		params.put("dateParam", aDate);
		params.put("datesParam", dates);
		params.put("uppercaseNameParam", false);
		params.put("textToAppendToTheNameParam", "some text");

		// Verification
		assertEquals(0, graphQLRequest.aliasFields.size());
		AbstractGraphQLRequest_allGraphQLCasesTest.checkPayload(graphQLRequest.getPayload(params), "" + //
				"query myQueryName($nbItemsParam:Long!=666,$dateParam:MyCustomScalarForADate=\"2025-11-17\",$datesParam:[MyCustomScalarForADate]!=[\"2025-11-18\"],$uppercaseNameParam:Boolean=true,$textToAppendToTheNameParam:String=\"default value\")"
				+ "{allFieldCases{listWithIdSubTypes(nbItems:$nbItemsParam,date:$dateParam,dates:$datesParam,uppercaseName:$uppercaseNameParam,textToAppendToTheName:$textToAppendToTheNameParam)"
				+ "{name date dates __typename} __typename" + //
				"}}", //
				params, //
				null);
	}
}
