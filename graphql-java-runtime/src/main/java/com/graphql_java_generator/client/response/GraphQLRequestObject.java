/**
 * 
 */
package com.graphql_java_generator.client.response;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This interface marks a class as being the implementation for the GraphQL type that is a query, a mutation or a
 * subscription.
 * 
 * @author etienne-sf
 */
public interface GraphQLRequestObject {

	/**
	 * Set the GraphQL response's extensions field. The generated classes for the query, mutation and subscription of a
	 * GraphQL schema implement this interface. This allows to access to the _extensions_ field, when using Full
	 * Queries. See the Client FAQ for more information.
	 * 
	 * @param extensions
	 */
	public void setExtensions(JsonNode extensions);

}
