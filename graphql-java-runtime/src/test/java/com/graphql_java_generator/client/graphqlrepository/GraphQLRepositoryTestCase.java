package com.graphql_java_generator.client.graphqlrepository;

import java.util.Date;
import java.util.List;

import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.SubscriptionCallback;
import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.domain.client.allGraphQLCases.AllFieldCases;
import com.graphql_java_generator.domain.client.allGraphQLCases.AllFieldCasesInput;
import com.graphql_java_generator.domain.client.allGraphQLCases.AnotherMutationType;
import com.graphql_java_generator.domain.client.allGraphQLCases.Character;
import com.graphql_java_generator.domain.client.allGraphQLCases.CharacterInput;
import com.graphql_java_generator.domain.client.allGraphQLCases.Episode;
import com.graphql_java_generator.domain.client.allGraphQLCases.FieldParameterInput;
import com.graphql_java_generator.domain.client.allGraphQLCases.Human;
import com.graphql_java_generator.domain.client.allGraphQLCases.HumanInput;
import com.graphql_java_generator.domain.client.allGraphQLCases.MyQueryType;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

/**
 * This interface contains the test cases for the {@link GraphQLRepositoryInvocationHandlerTest} test class
 * 
 * @author etienne-sf
 */
@GraphQLRepository
public interface GraphQLRepositoryTestCase {

	/** no requestName: the method name is the field name of the query type in the GraphQL schema */
	@PartialRequest(request = "{appearsIn name}")
	public Character withOneOptionalParam(CharacterInput character) throws GraphQLRequestExecutionException;

	/** with requestName: the method name is free */
	@PartialRequest(requestName = "withOneOptionalParam", request = "{appearsIn name id}")
	public Character thisIsNotARequestName1(CharacterInput character) throws GraphQLRequestExecutionException;

	/** with requestName: the method name is free */
	@PartialRequest(requestName = "withOneOptionalParam", request = "{id name appearsIn}")
	public Character thisIsNotARequestName2(CharacterInput character) throws GraphQLRequestExecutionException;

	/** with requestName (the method name is free) and bind parameter values */
	@PartialRequest(requestName = "allFieldCases", request = "{listWithoutIdSubTypes(nbItems: &nbItemsParam, input:?fieldParameterInput)}")
	public AllFieldCases thisIsNotARequestName3(AllFieldCasesInput input,
			@BindParameter(name = "nbItemsParam") long nbItems, //
			@BindParameter(name = "fieldParameterInput") FieldParameterInput fieldParameterInput)
			throws GraphQLRequestExecutionException;

	/** A query with an Integer parameter */
	@PartialRequest(requestName = "withOneMandatoryParamDefaultValue", request = "")
	Integer withIntParamAndReturnType(int intParam) throws GraphQLRequestExecutionException;

	/**
	 * A query with a float parameter (GraphQL Float actually maps to Java Double, but Java Float and float should be
	 * accepted here)
	 */
	@PartialRequest(requestName = "issue82Float", request = "")
	Double withDoubleParamAndReturnType(double f) throws GraphQLRequestExecutionException;

	/** A query with a boolean parameter */
	@PartialRequest(requestName = "directiveOnQuery", request = "")
	List<String> withBooleanParam(boolean bool) throws GraphQLRequestExecutionException;

	/** A query with a {@link List} parameter */
	@PartialRequest(requestName = "withListOfList", request = "")
	AllFieldCases withListParam(List<List<Float>> matrix) throws GraphQLRequestExecutionException;

	/** Mutation: without requestName */
	@PartialRequest(request = "{id}", requestType = RequestType.mutation)
	public Human createHuman(HumanInput character) throws GraphQLRequestExecutionException;

	/** Mutation: with requestName */
	@PartialRequest(requestName = "createHuman", request = "{id name}", requestType = RequestType.mutation)
	public Human thisIsAMutation(HumanInput character) throws GraphQLRequestExecutionException;

	/** Subscription */
	@PartialRequest(request = "{ id   appearsIn }", requestType = RequestType.subscription)
	public SubscriptionClient subscribeNewHumanForEpisode(SubscriptionCallback<Human> subscriptionCallback,
			Episode episode) throws GraphQLRequestExecutionException;

	/** Full request: query */
	@FullRequest(request = "{directiveOnQuery  (uppercase: ?uppercase) @testDirective(value:&valueParam)}")
	public MyQueryType fullRequest1(@BindParameter(name = "uppercase") boolean uppercase,
			@BindParameter(name = "valueParam") String value) throws GraphQLRequestExecutionException;

	/** Full request: query, with queryType */
	@FullRequest(request = "{directiveOnQuery  (uppercase: ?uppercase) @testDirective(value:&valueParam)}", requestType = RequestType.query)
	public MyQueryType fullRequest2(@BindParameter(name = "valueParam") String value,
			@BindParameter(name = "uppercase") boolean uppercase) throws GraphQLRequestExecutionException;

	/** Full request: mutation */
	@FullRequest(request = "mutation($input: HumanInput!) {createHuman(human: $input) {id name }}", requestType = RequestType.mutation)
	public AnotherMutationType fullRequestMutation(@BindParameter(name = "input") HumanInput humanInput)
			throws GraphQLRequestExecutionException;

	/** Full request: subscription */
	@FullRequest(request = "subscription {issue53(date: &date) {}}", requestType = RequestType.subscription)
	public SubscriptionClient fullSubscription(SubscriptionCallback<Date> callback,
			@BindParameter(name = "date") Date date) throws GraphQLRequestExecutionException;

}
