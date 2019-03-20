/**
 * 
 */
package graphql.java.client;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
import graphql.java.client.request.ResponseDefinition;
import graphql.java.client.response.GraphQLExecutionException;
import graphql.java.client.response.GraphQLResponseParseException;
import graphql.java.client.response.JsonResponseWrapper;

/**
 * This class is the query executor : a generic class, reponsible for calling
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

	public QueryExecutorImpl() {
		client = ClientBuilder.newClient();
		webTarget = client.target("http://localhost:8080").path("graphql");
	}

	/** {@inheritDoc} */
	@Override
	public <T> T execute(String queryName, List<InputParameter> parameters, ResponseDefinition responseDef,
			Class<T> valueType) throws IOException, GraphQLExecutionException {
		logger.warn(GRAPHQL_MARKER, "[TODO] Check and minimize the jersey dependencies");

		// Let's build the GraphQL request, to send to the server
		String request = buildRequest(queryName, parameters, responseDef);
		logger.trace(GRAPHQL_MARKER, "Generated GraphQL request: {}", request);

		request = "{\"query\":\"" + request + "\",\"variables\":null,\"operationName\":null}";
		logger.trace(GRAPHQL_MARKER, "Sending JSON request to GraphQL server: {}", request);

		Invocation.Builder invocationBuilder = webTarget.request(MediaType.TEXT_PLAIN_TYPE);
		invocationBuilder.header("Accept", MediaType.APPLICATION_JSON);
		String rawResponse = invocationBuilder.post(Entity.entity(request, MediaType.TEXT_PLAIN_TYPE), String.class);
		logger.trace(GRAPHQL_MARKER, "Received response: {}", rawResponse);
		// return parseResponse(rawResponse, queryName, responseDef, valueType);

		JsonResponseWrapper response = invocationBuilder.post(Entity.entity(request, MediaType.TEXT_PLAIN_TYPE),
				JsonResponseWrapper.class);

		if (response.errors == null || response.errors.size() == 0) {
			// No errors. Let's parse the data
			ObjectMapper mapper = new ObjectMapper();
			JsonNode hero = response.data.get("hero");
			JsonNode human = response.data.get("human");
			if (hero != null)
				return mapper.treeToValue(hero, valueType);
			if (human != null)
				return mapper.treeToValue(human, valueType);
			throw new GraphQLResponseParseException("Could not retrieve the 'hero' nor 'human' node");

		} else {
			for (graphql.java.client.response.Error error : response.errors) {
				error.logError(logger, GRAPHQL_MARKER);
			}
			String msg;
			if (response.errors.size() == 1)
				msg = "An error occurred: " + response.errors.get(0).message;
			else
				msg = response.errors.size() + " errors occurred: "
						+ response.errors.stream().map(e -> e.message).collect(Collectors.joining(" / "));

			throw new GraphQLExecutionException(msg);
		}

	}

	/**
	 * Builds a single GraphQL request from the parameter given.
	 * 
	 * @param queryName
	 * @param parameters
	 * @param responseDef
	 * @return The GraphQL request, ready to be sent to the GraphQl server.
	 */
	String buildRequest(String queryName, List<InputParameter> parameters, ResponseDefinition responseDef) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(queryName);
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
		sb.append(" ");
		responseDef.appendResponseQuery(sb);
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Parse the GraphQL server response, and map it to the objects, generated from the GraphQL schema.
	 * 
	 * @param <T>
	 * 
	 * @param rawResponse
	 * @param queryName
	 * @param responseDef
	 * @return
	 * @throws GraphQLResponseParseException
	 * @throws IOException
	 */
	<T> T parseResponse(String rawResponse, String queryName, ResponseDefinition responseDef, Class<T> valueType)
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
