/**
 * 
 */
package com.graphql_java_generator.client;

import java.io.IOException;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.client.response.JsonResponseWrapper;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

/**
 * This class is the query executor : a generic class, reponsible for calling the GraphQL server, for query, mutation
 * and subscription.<BR/>
 * It has one major parameter: the GraphQL endpoint. See the {@link #QueryExecutorImpl(String)} for more information.
 * 
 * @author EtienneSF
 */
public class QueryExecutorImpl implements QueryExecutor {

	static {
		GRAPHQL_QUERY_MARKER.add(GRAPHQL_MARKER);
		GRAPHQL_MUTATION_MARKER.add(GRAPHQL_MARKER);
		GRAPHQL_SUBSCRIPTION_MARKER.add(GRAPHQL_MARKER);
	}

	/** Logger for this class */
	private static Logger logger = LoggerFactory.getLogger(QueryExecutorImpl.class);

	/** The Jersey {@link Client}, used to execute the request toward the GraphQL server */
	Client client;
	/** Jackson {@link ObjectMapper}, used for serialisation / de-serialisation */
	ObjectMapper objectMapper;
	/** The Jersey {@link WebTarget}, used to execute the request toward the GraphQL server */
	WebTarget webTarget;

	/**
	 * This constructor expects the URI of the GraphQL server<BR/>
	 * For example: http://my.server.com/graphql or https://my.server.com/graphql
	 * 
	 * @param graphqlEndpoint
	 *            the http URI for the GraphQL endpoint
	 */
	public QueryExecutorImpl(String graphqlEndpoint) {
		this(graphqlEndpoint, ClientBuilder.newClient(), new ObjectMapper());
	}

	/**
	 * This constructor expects the URI of the GraphQL server. This constructor works only for https servers, not for
	 * http ones.<BR/>
	 * For example: https://my.server.com/graphql<BR/>
	 * It allows to specify the SSLContext and the HostnameVerifier. It is used in the integration test... to remove
	 * most of the control on https protocol, and allow connection to an https with a self-signed certificate.
	 * 
	 * @param graphqlEndpoint
	 *            the https URI for the GraphQL endpoint
	 * @param sslContext
	 * @param hostnameVerifier
	 */
	public QueryExecutorImpl(String graphqlEndpoint, SSLContext sslContext, HostnameVerifier hostnameVerifier) {
		if (graphqlEndpoint.startsWith("http:")) {
			throw new IllegalArgumentException(
					"This GraphQL endpoint is an http one. Please use the relevant Query/Mutation/Subscription constructor (without the SSLContext and HostnameVerifier parameters)");
		}
		client = ClientBuilder.newBuilder().sslContext(sslContext).hostnameVerifier(hostnameVerifier).build();
		objectMapper = new ObjectMapper();
		webTarget = client.target(graphqlEndpoint);
	}

	/**
	 * This constructor expects the URI of the GraphQL server and a configured JAX-RS client that gives the opportunity
	 * to customise the REST request<BR/>
	 * For example: http://my.server.com/graphql
	 *
	 * @param graphqlEndpoint
	 *            the http URI for the GraphQL endpoint
	 * @param client
	 *            {@link Client} javax.ws.rs.client.Client to support customization of the rest request
	 * @param objectMapper
	 *            {@link ObjectMapper} com.fasterxml.jackson.databind.ObjectMapper to support configurable mapping
	 */
	public QueryExecutorImpl(String graphqlEndpoint, Client client, ObjectMapper objectMapper) {
		this.client = client;
		this.objectMapper = objectMapper;
		this.webTarget = client.target(graphqlEndpoint);
	}

	/** {@inheritDoc} */
	@Override
	public <T> T execute(ObjectResponse objectResponse, Map<String, Object> parameters, Class<T> valueType)
			throws GraphQLRequestExecutionException {
		String request = null;
		try {
			// Let's build the GraphQL request, to send to the server
			request = objectResponse.buildRequest(parameters);
			logger.trace(GRAPHQL_MARKER, "Generated GraphQL request: {}", request);

			return doJsonRequestExecution(request, valueType);
		} catch (IOException e) {
			throw new GraphQLRequestExecutionException(
					"Error when executing query <" + request + ">: " + e.getMessage(), e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public <T> T execute(String query, Class<T> valueType) throws GraphQLRequestExecutionException {
		try {
			return doJsonRequestExecution(query, valueType);
		} catch (IOException e) {
			throw new GraphQLRequestExecutionException(e.getMessage(), e);
		}
	}

	/**
	 * Executes the given json request, and returns the server response mapped into the relevant java classes.
	 * 
	 * @param <T>
	 *            The GraphQL type to map the response into
	 * @param jsonRequest
	 *            The json request to send to the server, as is.
	 * @param valueType
	 *            The GraphQL type to map the response into
	 * @return
	 * @throws IOException
	 * @throws GraphQLRequestExecutionException
	 */
	<T> T doJsonRequestExecution(String jsonRequest, Class<T> valueType)
			throws IOException, GraphQLRequestExecutionException {
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		invocationBuilder.header("Accept", MediaType.APPLICATION_JSON);

		JsonResponseWrapper response = invocationBuilder.post(Entity.entity(jsonRequest, MediaType.APPLICATION_JSON),
				JsonResponseWrapper.class);

		if (logger.isTraceEnabled()) {
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

}
