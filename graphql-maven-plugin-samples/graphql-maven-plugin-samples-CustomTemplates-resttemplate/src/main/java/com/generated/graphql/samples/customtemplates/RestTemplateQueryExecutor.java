package com.generated.graphql.samples.customtemplates;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql_java_generator.client.GraphQLRequestObject;
import com.graphql_java_generator.client.RequestExecution;
import com.graphql_java_generator.client.SubscriptionCallback;
import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.client.request.AbstractGraphQLRequest;
import com.graphql_java_generator.client.response.JsonResponseWrapper;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

/**
 * {@link RequestExecution} implementation using Spring {@link RestTemplate} as the http client Property grapql.endpoint
 * in required in application configuraion
 * 
 * @author ggomez
 *
 */
@Component
@Qualifier("RestTemplateQueryExecutor")
@Primary
public class RestTemplateQueryExecutor implements RequestExecution {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(RestTemplateQueryExecutor.class);

	@Value("${graphql.endpoint}")
	protected String graphqlEndpoint;

	@Autowired
	protected RestTemplate restTemplate;

	protected ObjectMapper objectMapper;

	public RestTemplateQueryExecutor() {
		this.objectMapper = new ObjectMapper();
	}

	// @Override
	// public <T> T execute(ObjectResponse objectResponse, Map<String, Object> parameters, Class<T> valueType)
	// throws GraphQLRequestExecutionException {
	// // TODO Auto-generated method stub
	// return null;
	// }

	@Override
	public <T extends GraphQLRequestObject> T execute(AbstractGraphQLRequest graphQLRequest,
			Map<String, Object> parameters, Class<T> valueType) throws GraphQLRequestExecutionException {
		String request = null;
		try {
			// Let's build the GraphQL request, to send to the server
			request = graphQLRequest.buildRequestAsString(parameters);
			logger.trace(GRAPHQL_MARKER, "Generated GraphQL request: {}", request);

			return doJsonRequestExecution(request, valueType);
		} catch (IOException e) {
			throw new GraphQLRequestExecutionException(
					"Error when executing query <" + request + ">: " + e.getMessage(), e);
		}

	}

	protected <T> T doJsonRequestExecution(String jsonRequest, Class<T> valueType)
			throws IOException, GraphQLRequestExecutionException {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(jsonRequest, headers);

		JsonResponseWrapper response = this.restTemplate
				.postForEntity(graphqlEndpoint, entity, JsonResponseWrapper.class).getBody();

		if (logger.isInfoEnabled()) {
			logger.trace("Parsed response data: {}", objectMapper.writeValueAsString(response.data));
			logger.trace("Parsed response errors: {}", objectMapper.writeValueAsString(response.errors));
		}

		if (response.errors == null || response.errors.size() == 0) {
			// No errors. Let's parse the data
			return objectMapper.treeToValue(response.data, valueType);
		} else {
			int nbErrors = 0;
			String agregatedMessage = null;
			for (com.graphql_java_generator.client.response.Error error : response.errors) {
				String msg = error.toString();
				nbErrors += 1;
				logger.error(GRAPHQL_MARKER, msg);
				if (agregatedMessage == null) {
					agregatedMessage = msg;
				} else {
					agregatedMessage += ", ";
					agregatedMessage += msg;
				}
			}
			if (nbErrors == 0) {
				throw new GraphQLRequestExecutionException("An unknown error occured");
			} else {
				throw new GraphQLRequestExecutionException(nbErrors + " errors occured: " + agregatedMessage);
			}
		}
	}

	@Override
	public <R, T> SubscriptionClient execute(AbstractGraphQLRequest graphQLRequest, Map<String, Object> parameters,
			SubscriptionCallback<T> subscriptionCallback, Class<R> subscriptionType, Class<T> messageType)
			throws GraphQLRequestExecutionException {
		// No subscription in this sample, so we don't really care of this implementation.
		return null;
	}

}
