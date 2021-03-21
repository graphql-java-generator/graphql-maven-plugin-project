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
	 * This field is described in the <A HREF= "https://spec.graphql.org/June2018/#sec-Response">GraphQL
	 * specification</A>. It is stored here, without further mapping. It's actually a map, where value can be any
	 * object. So we can't deserialize here. And it would slow down the deserialization process.<BR/>
	 * To get the value from this field, one must execute full queries, and retrieve from the received object. See the
	 * client FAQ about this.
	 */
	public JsonNode extensions;
}
