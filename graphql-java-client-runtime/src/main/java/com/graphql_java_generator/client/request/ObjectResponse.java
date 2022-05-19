/**
 * 
 */
package com.graphql_java_generator.client.request;

import org.springframework.graphql.client.GraphQlClient;

import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * This useless class is here only for compatibility with existing code, developped before the 1.6 release. It's
 * actually just a wrapper for the {@link AbstractGraphQLRequest}. It allows this kind of code to work as before:
 * 
 * <PRE>
 * ObjectResponse withHeroResponse = queryType.getHeroBuilder()
 *         .withQueryResponseDef("{id name appearsIn friends {id name}}").build();
 * 
 * [...]
 * 
 * Character c = queryType.hero(withHeroResponse, character);
 * </PRE>
 * 
 * @author etienne-sf
 *
 */
abstract public class ObjectResponse extends AbstractGraphQLRequest {

	/** {@inheritDoc} */
	public ObjectResponse(GraphQlClient graphQlClient, String schema, String graphQLRequest)
			throws GraphQLRequestPreparationException {
		super(graphQlClient, schema, graphQLRequest);
	}

	/** {@inheritDoc} */
	public ObjectResponse(GraphQlClient graphQlClient, String schema, String graphQLRequest, RequestType requestType,
			String queryName, InputParameter... inputParams) throws GraphQLRequestPreparationException {
		super(graphQlClient, schema, graphQLRequest, requestType, queryName, inputParams);
	}

}
