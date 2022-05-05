/**
 * 
 */
package graphql.spring.web.servlet.components;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import graphql.ExecutionResult;
import graphql.spring.web.servlet.ExecutionResultHandler;
import graphql.spring.web.servlet.GraphQLInvocation;
import graphql.spring.web.servlet.GraphQLInvocationData;
import graphql.spring.web.servlet.JsonSerializer;

/**
 * @author etienne-sf
 */
@RestController
public class CustomGraphQLController {

	private static final Logger log = LoggerFactory.getLogger(CustomGraphQLController.class);

	static String APPLICATION_GRAPHQL_VALUE = "application/graphql";
	static MediaType APPLICATION_GRAPHQL = MediaType.parseMediaType(APPLICATION_GRAPHQL_VALUE);

	@Autowired
	GraphQLInvocation graphQLInvocation;

	@Autowired
	ExecutionResultHandler executionResultHandler;

	@Autowired
	JsonSerializer jsonSerializer;

	// TODO remove the 2 in the URL (which is here only for tests)
	@RequestMapping(value = "${graphql.url:graphql3}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public Object graphqlPOST(@RequestHeader(value = HttpHeaders.CONTENT_TYPE, required = false) String contentType,
			@RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "operationName", required = false) String operationName,
			@RequestParam(value = "variables", required = false) String variablesJson,
			@RequestBody(required = false) String body, WebRequest webRequest) throws IOException {

		MediaType mediaType = null;
		if (!StringUtils.isEmpty(contentType)) {
			try {
				mediaType = MediaType.parseMediaType(contentType);
			} catch (InvalidMediaTypeException ignore) {
			}
		}

		if (body == null) {
			body = "";
		}

		// https://graphql.org/learn/serving-over-http/#post-request
		//
		// A standard GraphQL POST request should use the application/json content type,
		// and include a JSON-encoded body of the following form:
		//
		// {
		// "query": "...",
		// "operationName": "...",
		// "variables": { "myVariable": "someValue", ... }
		// }

		if (MediaType.APPLICATION_JSON.equalsTypeAndSubtype(mediaType)) {
			GraphQLRequestBody request = jsonSerializer.deserialize(body, GraphQLRequestBody.class);
			if (request.getQuery() == null) {
				request.setQuery("");
			}
			return executeRequest(request.getQuery(), request.getOperationName(), request.getVariables(), webRequest);
		}

		// In addition to the above, we recommend supporting two additional cases:
		//
		// * If the "query" query string parameter is present (as in the GET example above),
		// it should be parsed and handled in the same way as the HTTP GET case.

		if (query != null) {
			return executeRequest(query, operationName, convertVariablesJson(variablesJson), webRequest);
		}

		// * If the "application/graphql" Content-Type header is present,
		// treat the HTTP POST body contents as the GraphQL query string.

		if (APPLICATION_GRAPHQL.equalsTypeAndSubtype(mediaType)) {
			log.debug("Managing query, from a POST http: {}", query);
			return executeRequest(body, null, null, webRequest);
		}

		throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Could not process GraphQL request");
	}

	@RequestMapping(value = "${graphql.url:graphql3}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, headers = {
			"Connection!=upgrade", "Connection!=Upgrade" })
	public Object graphqlGET(@RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "operationName", required = false) String operationName,
			@RequestParam(value = "variables", required = false) String variablesJson, WebRequest webRequest) {

		// https://graphql.org/learn/serving-over-http/#get-request
		//
		// When receiving an HTTP GET request, the GraphQL query should be specified in the "query" query string.
		// For example, if we wanted to execute the following GraphQL query:
		//
		// {
		// me {
		// name
		// }
		// }
		//
		// This request could be sent via an HTTP GET like so:
		//
		// http://myapi/graphql?query={me{name}}
		//
		// Query variables can be sent as a JSON-encoded string in an additional query parameter called "variables".
		// If the query contains several named operations,
		// an "operationName" query parameter can be used to control which one should be executed.

		log.debug("Managing query, from a GET http: {} (header: Connection={}))", query,
				webRequest.getHeader("Connection"));
		return executeRequest(query, operationName, convertVariablesJson(variablesJson), webRequest);
	}

	/**
	 * This dummy method just returns an empty String. It allows a proper mapping of the incoming request, when the
	 * client tries to open a websocket.
	 * 
	 * @return
	 */
	// @RequestMapping(value = "${graphql.url:graphql2}", method = RequestMethod.GET, produces =
	// MediaType.APPLICATION_JSON_VALUE)
	// public String dummyGET() {
	// return "";
	// }

	private Map<String, Object> convertVariablesJson(String jsonMap) {
		if (jsonMap == null) {
			return Collections.emptyMap();
		}
		return jsonSerializer.deserialize(jsonMap, Map.class);
	}

	private Object executeRequest(String query, String operationName, Map<String, Object> variables,
			WebRequest webRequest) {
		GraphQLInvocationData invocationData = new GraphQLInvocationData(query, operationName, variables);
		CompletableFuture<ExecutionResult> executionResult = graphQLInvocation.invoke(invocationData, webRequest);
		return executionResultHandler.handleExecutionResult(executionResult);
	}

}
