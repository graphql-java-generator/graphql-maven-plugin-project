package com.graphql_java_generator.client.graphqlrepository;

import java.util.Map;

import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.SubscriptionCallback;
import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.client.domain.allGraphQLCases.Character;
import com.graphql_java_generator.client.domain.allGraphQLCases.CharacterInput;
import com.graphql_java_generator.client.domain.allGraphQLCases.Episode;
import com.graphql_java_generator.client.domain.allGraphQLCases.Human;
import com.graphql_java_generator.client.domain.allGraphQLCases.HumanInput;
import com.graphql_java_generator.client.domain.allGraphQLCases.MyQueryType;
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
	public Character withOneOptionalParam(CharacterInput character, Object... o)
			throws GraphQLRequestExecutionException;

	/** with requestName: the method name is free */
	@PartialRequest(requestName = "withOneOptionalParam", request = "{appearsIn name id}")
	public Character thisIsNotARequestName1(CharacterInput character, Object... o)
			throws GraphQLRequestExecutionException;

	/** no requestName: the method name is the field name of the query type in the GraphQL schema */
	@PartialRequest(requestName = "withOneOptionalParam", request = "{id name appearsIn}")
	public Character thisIsNotARequestName2(CharacterInput character) throws GraphQLRequestExecutionException;

	/** with requestName: the method name is free */
	@PartialRequest(requestName = "withOneOptionalParam", request = "{appearsIn id name}", requestType = RequestType.query)
	public Character thisIsNotARequestName3(CharacterInput character, Map<String, Object> map)
			throws GraphQLRequestExecutionException;

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
	@FullRequest(request = "{directiveOnQuery  (uppercase: true) @testDirective(value:&value)}")
	public MyQueryType fullQuery1(Object... paramsAndValues) throws GraphQLRequestExecutionException;

	/** Full request, with requestType: query */
	@FullRequest(request = "{directiveOnQuery (uppercase: true) @testDirective(value:&value)}", requestType = RequestType.query)
	public MyQueryType fullQuery2(String value, Object... paramsAndValues) throws GraphQLRequestExecutionException;

	/** Full request, with requestType: query */
	@FullRequest(request = "{directiveOnQuery (uppercase: true) @testDirective(value:&value)}", requestType = RequestType.query)
	public MyQueryType fullQuery3(String value) throws GraphQLRequestExecutionException;
}
