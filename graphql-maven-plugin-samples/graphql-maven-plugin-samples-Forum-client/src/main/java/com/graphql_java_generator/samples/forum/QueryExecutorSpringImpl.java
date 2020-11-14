/**
 * 
 */
package com.graphql_java_generator.samples.forum;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.QueryExecutor;
import com.graphql_java_generator.client.QueryExecutorImpl;
import com.graphql_java_generator.client.SubscriptionCallback;
import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.client.request.AbstractGraphQLRequest;
import com.graphql_java_generator.client.response.JsonResponseWrapper;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

import reactor.core.publisher.Mono;

/**
 * This is the default implementation for the {@link QueryExecutor} This implementation has been added in version 1.12.
 * 
 * @since 1.12
 * @author etienne-sf
 */
@Component
public class QueryExecutorSpringImpl implements QueryExecutor {

	/** Logger for this class */
	private static Logger logger = LoggerFactory.getLogger(QueryExecutorSpringImpl.class);

	WebClient webClient;

	/**
	 * The {@link ObjectMapper} that will read the json response, and map it to the correct java class, generated from
	 * the GraphQL type defined in the source GraphQL schema
	 */
	ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * This constructor may be called by Spring, once it has build a {@link WebClient} bean, or directly, in non Spring
	 * applications.
	 * 
	 * @param webClient
	 */
	@Autowired
	public QueryExecutorSpringImpl(WebClient webClient) {
		this.webClient = webClient;
	}

	@Override
	public <R> R execute(AbstractGraphQLRequest graphQLRequest, Map<String, Object> parameters,
			Class<R> dataResponseType) throws GraphQLRequestExecutionException {

		if (graphQLRequest.getRequestType().equals(RequestType.subscription))
			throw new GraphQLRequestExecutionException("This method may not be called for subscriptions");

		String jsonRequest = graphQLRequest.buildRequest(parameters);

		try {

			logger.trace(GRAPHQL_MARKER, "Executing GraphQL request: {}", jsonRequest);

			JsonResponseWrapper responseJson = webClient//
					.post()//
					.contentType(MediaType.APPLICATION_JSON)//
					.body(Mono.just(jsonRequest), String.class)//
					.accept(MediaType.APPLICATION_JSON)//
					.retrieve()//
					.bodyToMono(JsonResponseWrapper.class)//
					.block();

			return QueryExecutorImpl.parseDataFromGraphQLServerResponse(objectMapper, responseJson, dataResponseType);

		} catch (IOException e) {
			throw new GraphQLRequestExecutionException(
					"Error when executing query <" + jsonRequest + ">: " + e.getMessage(), e);
		}
	}

	@Override
	public <R, T> SubscriptionClient execute(AbstractGraphQLRequest graphQLRequest, Map<String, Object> parameters,
			SubscriptionCallback<T> subscriptionCallback, String subscriptionName, Class<R> subscriptionType,
			Class<T> messageType) throws GraphQLRequestExecutionException {
		throw new RuntimeException("not yet implemented");
	}

}
