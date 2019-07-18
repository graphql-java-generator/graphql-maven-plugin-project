/**
 * 
 */
package com.graphql_java_generator.client.response;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author EtienneSF
 */
public class JsonResponseWrapper {

	/**
	 * This contains the data coming from the GraphQL part. The issue here is that it contains data that depends on the
	 * GraphQL schema. So we just collect the parsed Json, which will be mapped later to the relevant Java POJO
	 */
	public JsonNode data;

	@JsonDeserialize(contentAs = Error.class)
	public List<Error> errors;

}
