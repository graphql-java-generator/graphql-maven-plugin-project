/**
 * 
 */
package graphql.java.client;

import java.io.IOException;
import java.util.List;

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
import graphql.java.client.request.ObjectResponseDef;
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
		webTarget = client.target("http://localhost:8180").path("graphql");
	}

	/** {@inheritDoc} */
	@Override
	public <T> T execute(ObjectResponseDef objectResponseDef, List<InputParameter> parameters, Class<T> valueType)
			throws IOException, GraphQLExecutionException {
		logger.warn(GRAPHQL_MARKER, "[TODO] Check and minimize the jersey dependencies");

		// Let's build the GraphQL request, to send to the server
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		invocationBuilder.header("Accept", MediaType.APPLICATION_JSON);
		String request = buildRequest(objectResponseDef, parameters);
		logger.trace(GRAPHQL_MARKER, "Generated GraphQL request: {}", request);

		// String rawResponse = invocationBuilder.post(Entity.entity(request, MediaType.APPLICATION_JSON),
		// String.class);
		// logger.trace(GRAPHQL_MARKER, "Received response: {}", rawResponse);

		JsonResponseWrapper response = invocationBuilder.post(Entity.entity(request, MediaType.APPLICATION_JSON),
				JsonResponseWrapper.class);

		if (response.errors == null || response.errors.size() == 0) {
			// No errors. Let's parse the data
			ObjectMapper mapper = new ObjectMapper();
			JsonNode json = response.data.get(objectResponseDef.getFieldName());
			if (json != null) {
				return mapper.treeToValue(json, valueType);
			}
			throw new GraphQLResponseParseException("Could not retrieve the '" + objectResponseDef.getFieldName() + "' node");

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
	 * @param objectResponseDef
	 *            Defines what response is expected from the server. The {@link ObjectResponseDef#getFieldAlias()} method
	 *            returns the field of the query, that is: the query name.
	 * @param parameters
	 * @return The GraphQL request, ready to be sent to the GraphQl server.
	 */
	String buildRequest(ObjectResponseDef objectResponseDef, List<InputParameter> parameters) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(objectResponseDef.getFieldName());
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
		objectResponseDef.appendResponseQuery(sb);
		sb.append("}");

		return "{\"query\":\"" + sb.toString() + "\",\"variables\":null,\"operationName\":null}";
	}

	/**
	 * Parse the GraphQL server response, and map it to the objects, generated from the GraphQL schema.
	 * 
	 * @param <T>
	 * 
	 * @param rawResponse
	 * @param objectResponseDef
	 * @return
	 * @throws GraphQLResponseParseException
	 * @throws IOException
	 */
	<T> T parseResponse(String rawResponse, ObjectResponseDef objectResponseDef, Class<T> valueType)
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
