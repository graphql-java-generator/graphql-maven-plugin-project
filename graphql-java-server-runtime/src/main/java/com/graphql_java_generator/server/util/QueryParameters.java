package com.graphql_java_generator.server.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

/**
 * Graphql clients can send GET or POST HTTP requests. The spec does not make an explicit distinction. So you may need
 * to handle both. The following was tested using a graphiql client tool found here :
 * https://github.com/skevy/graphiql-app
 *
 * You should consider bundling graphiql in your application
 *
 * https://github.com/graphql/graphiql
 *
 * This outlines more information on how to handle parameters over http
 *
 * http://graphql.org/learn/serving-over-http/
 */
public class QueryParameters {

	private static JsonMapper objectMapper = JsonMapper.builder().build();

	private String query;
	private String operationName;
	private Map<String, Object> variables = Collections.emptyMap();

	public String getQuery() {
		return query;
	}

	public String getOperationName() {
		return operationName;
	}

	public Map<String, Object> getVariables() {
		return variables;
	}

	@SuppressWarnings("unchecked")
	public static QueryParameters from(String queryMessage) {
		QueryParameters parameters = new QueryParameters();
		Map<String, Object> json;
		try {
			json = objectMapper.readValue(queryMessage, Map.class);
		} catch (JacksonException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		parameters.query = (String) json.get("query");
		parameters.operationName = (String) json.get("operationName");
		parameters.variables = getVariables((Map<?, ?>) json.get("variables"));
		return parameters;
	}

	private static Map<String, Object> getVariables(Map<?, ?> variables) {
		Map<String, Object> vars = new HashMap<>();
		variables.forEach((k, v) -> vars.put(String.valueOf(k), v));
		return vars;
	}

}
