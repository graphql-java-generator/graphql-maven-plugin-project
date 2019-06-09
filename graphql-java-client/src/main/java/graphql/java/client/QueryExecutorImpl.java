/**
 * 
 */
package graphql.java.client;

import java.io.IOException;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.java.client.request.InputParameter;
import graphql.java.client.request.ObjectResponse;
import graphql.java.client.response.GraphQLExecutionException;
import graphql.java.client.response.GraphQLResponseParseException;
import graphql.java.client.response.JsonResponseWrapper;

/**
 * This class is the query executor : a generic class, reponsible for calling the GraphQL server, for query, mutation
 * and subscription.<BR/>
 * It has one major parameter: the GraphQL endpoint. See the {@link #QueryExecutorImpl(String)} for more information.
 * 
 * @author EtienneSF
 */
public class QueryExecutorImpl implements QueryExecutor {

	/** Logger for this class */
	private static Logger logger = LogManager.getLogger();

	/** The Jersey {@link Client}, used to execute the request toward the GraphQL server */
	Client client;
	/** The Jersey {@link WebTarget}, used to execute the request toward the GraphQL server */
	WebTarget webTarget;

	/**
	 * This constructor expects the URI of the GraphQL server. This constructor works only for http servers, not for
	 * https ones.<BR/>
	 * For example: http://my.server.com/graphql
	 * 
	 * @param graphqlEndpoint
	 *            the http URI for the GraphQL endpoint
	 */
	public QueryExecutorImpl(String graphqlEndpoint) {
		if (graphqlEndpoint.startsWith("https:")) {
			throw new IllegalArgumentException(
					"This GraphQL endpoint is an https one. Please provide the SSLContext and HostnameVerifier items, by using the relevant Query/Mutation/Subscription constructor");
		}
		client = ClientBuilder.newClient();
		webTarget = client.target(graphqlEndpoint);
	}

	/**
	 * This constructor expects the URI of the GraphQL server. This constructor works only for http servers, not for
	 * https ones.<BR/>
	 * For example: https://my.server.com/graphql
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
		webTarget = client.target(graphqlEndpoint);
	}

	/** {@inheritDoc} */
	@Override
	public <T> T execute(String requestType, ObjectResponse objectResponse, List<InputParameter> parameters,
			Class<T> valueType) throws GraphQLExecutionException {
		String request = null;
		try {
			// Let's build the GraphQL request, to send to the server
			request = buildRequest(requestType, objectResponse, parameters);
			logger.trace(GRAPHQL_MARKER, "Generated GraphQL request: {}", request);

			return doJsonRequestExecution(request, valueType);
		} catch (IOException e) {
			throw new GraphQLExecutionException("Error when executing query <" + request + ">: " + e.getMessage(), e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public <T> T execute(String query, Class<T> valueType) throws IOException, GraphQLExecutionException {
		return doJsonRequestExecution(query, valueType);
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
	 * @throws GraphQLExecutionException
	 */
	<T> T doJsonRequestExecution(String jsonRequest, Class<T> valueType) throws IOException, GraphQLExecutionException {
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		invocationBuilder.header("Accept", MediaType.APPLICATION_JSON);

		JsonResponseWrapper response = invocationBuilder.post(Entity.entity(jsonRequest, MediaType.APPLICATION_JSON),
				JsonResponseWrapper.class);

		if (logger.isTraceEnabled()) {
			ObjectMapper objectMapper = new ObjectMapper();
			logger.trace("Parsed response data: {}", objectMapper.writeValueAsString(response.data));
			logger.trace("Parsed response errors: {}", objectMapper.writeValueAsString(response.errors));
		}

		if (response.errors == null || response.errors.size() == 0) {
			// No errors. Let's parse the data
			ObjectMapper mapper = new ObjectMapper();
			return mapper.treeToValue(response.data, valueType);
		} else {
			int nbErrors = 0;
			String agregatedMessage = null;
			for (graphql.java.client.response.Error error : response.errors) {
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
				throw new GraphQLExecutionException("An unknown error occured");
			} else {
				throw new GraphQLExecutionException(nbErrors + " errors occured: " + agregatedMessage);
			}
		}
	}

	/**
	 * Builds a single GraphQL request from the parameter given.
	 * 
	 * @param requestType
	 *            One of "query", "mutation" or "subscription"
	 * @param objectResponse
	 *            Defines what response is expected from the server. The {@link ObjectResponse#getFieldAlias()} method
	 *            returns the field of the query, that is: the query name.
	 * @param parameters
	 * @return The GraphQL request, ready to be sent to the GraphQl server.
	 */
	String buildRequest(String requestType, ObjectResponse objectResponse, List<InputParameter> parameters) {
		if (!requestType.equals("query") && !requestType.equals("mutation") && !requestType.equals("subscription")) {
			throw new IllegalArgumentException(
					"requestType must be one of \"query\", \"mutation\" or \"subscription\", but is \"" + requestType
							+ "\"");
		}

		StringBuilder sb = new StringBuilder();
		sb.append(requestType).append(" ");
		sb.append("{");
		sb.append(objectResponse.getFieldName());
		if (parameters != null && parameters.size() > 0) {
			sb.append("(");
			boolean writeComma = false;
			for (InputParameter param : parameters) {
				if (writeComma)
					sb.append(", ");
				writeComma = true;
				sb.append(param.getName()).append(": ").append(param.getValueForGraphqlQuery());
			} // for
			sb.append(")");
		}
		objectResponse.appendResponseQuery(sb);
		sb.append("}");

		return "{\"query\":\"" + sb.toString() + "\",\"variables\":null,\"operationName\":null}";
	}

	/**
	 * Parse the GraphQL server response, and map it to the objects, generated from the GraphQL schema.
	 * 
	 * @param <T>
	 * 
	 * @param rawResponse
	 * @param objectResponse
	 * @return
	 * @throws GraphQLResponseParseException
	 * @throws IOException
	 */
	<T> T parseResponse(String rawResponse, ObjectResponse objectResponse, Class<T> valueType)
			throws GraphQLResponseParseException, IOException {

		// Let's read this response with Jackson
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(rawResponse);

		// The main node should be unique, named data, and be a container
		if (node.size() != 1)
			throw new GraphQLResponseParseException(
					"The response should contain one root element, but it contains " + node.size() + " elements");

		JsonNode data = node.get("data");
		if (data == null)
			throw new GraphQLResponseParseException("Could not retrieve the 'data' node");

		JsonNode hero = data.get("hero");
		if (hero == null)
			throw new GraphQLResponseParseException("Could not retrieve the 'hero' node");

		return mapper.treeToValue(hero, valueType);
	}

}
