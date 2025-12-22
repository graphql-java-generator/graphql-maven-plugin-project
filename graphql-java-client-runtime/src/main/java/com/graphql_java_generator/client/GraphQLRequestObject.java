/**
 * 
 */
package com.graphql_java_generator.client;

import tools.jackson.databind.JsonNode;

/**
 * This interface marks a class as being the implementation for the GraphQL type defined in the GraphQL schema, that is
 * a query, a mutation or a subscription.
 * 
 * @author etienne-sf
 */
public interface GraphQLRequestObject {

	/**
	 * Set the GraphQL response's extensions field. The generated classes for the query, mutation and subscription of a
	 * GraphQL schema implement this interface. This allows to access to the _extensions_ field, when using Full
	 * Queries. See the <A
	 * HREF="https://github.com/graphql-java-generator/graphql-maven-plugin-project/wiki/client_exec_graphql_requests>Client
	 * page about request execution</A> for more information.
	 * 
	 * @param extensions
	 */
	public void setExtensions(JsonNode extensions);

}
