/**
 * 
 */
package graphql.java.client;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import graphql.java.client.request.InputParameter;
import graphql.java.client.request.ResponseDefinition;

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

	/**
	 * Execution of the given query
	 * 
	 * @param parameters
	 * @param query
	 * @param response
	 */
	@Override
	public void execute(String queryName, List<InputParameter> parameters, ResponseDefinition responseDef) {
		logger.warn(GRAPHQL_MARKER, "[TODO] Check and minimize the jersey dependencies");

		// Let's build the GraphQL request, to send to the server
		String request = buildRequest(queryName, parameters, responseDef);
		logger.trace(GRAPHQL_MARKER, "Generated GraphQL request: {}", request);

		request = "{\"query\":\"" + request + "\",\"variables\":null,\"operationName\":null}";
		logger.trace(GRAPHQL_MARKER, "Sending JSON request to GraphQL server: {}", request);

		Invocation.Builder invocationBuilder = webTarget.request(MediaType.TEXT_PLAIN_TYPE);
		invocationBuilder.header("Accept", MediaType.APPLICATION_JSON);
		// Response postResponse = invocationBuilder.post(Entity.entity(request, MediaType.TEXT_PLAIN_TYPE));
		String response = invocationBuilder.post(Entity.entity(request, MediaType.TEXT_PLAIN_TYPE), String.class);
		// System.out.println(postResponse.toString());
		System.out.println(response);
	}

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

}
