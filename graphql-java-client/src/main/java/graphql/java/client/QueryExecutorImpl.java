/**
 * 
 */
package graphql.java.client;

import java.util.List;

import graphql.java.client.request.InputParameter;
import graphql.java.client.request.ResponseDefinition;

/**
 * This class is the query executor : a generic class, reponsible for calling
 * 
 * @author EtienneSF
 */
public class QueryExecutorImpl implements QueryExecutor {

	/**
	 * Execution of the given query
	 * 
	 * @param parameters
	 * @param query
	 * @param response
	 */
	@Override
	public void execute(String queryName, List<InputParameter> parameters, ResponseDefinition responseDef) {

		// String request = buildRequest(queryName, parameters, responseDef);

		// Voir :
		// https://www.mkyong.com/webservices/jax-rs/restfull-java-client-with-java-net-url/
		// https://www.mkyong.com/webservices/jax-rs/restful-java-client-with-jersey-client/
		// https://www.baeldung.com/jersey-jax-rs-client
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
