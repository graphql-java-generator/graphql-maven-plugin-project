/**
 * 
 */
package com.graphql_java_generator.client.response;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author etienne-sf
 */
public class JsonResponseWrapper {

	/**
	 * This contains the data coming from the GraphQL part. The issue here is that it contains data that depends on the
	 * GraphQL schema. So we just collect the parsed Json, which will be mapped later to the relevant Java POJO
	 */
	public JsonNode data;

	/** This optional field contains the errors, when one or more errors occurred */
	@JsonDeserialize(contentAs = Error.class)
	public List<Error> errors;

	/**
	 * This field is absent from the GraphQL specification. But Shopify returns it. So this field is here, only to allow
	 * the JSON response parsing. Its content is not parsed.
	 */
	public JsonNode extensions;
}
