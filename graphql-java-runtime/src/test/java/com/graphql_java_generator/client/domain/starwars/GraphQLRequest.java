/**
 * 
 */
package com.graphql_java_generator.client.domain.starwars;

import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.client.request.QueryField;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * @author etienne-sf
 *
 */
public class GraphQLRequest extends ObjectResponse {

	public GraphQLRequest(String graphQLRequest) throws GraphQLRequestPreparationException {
		super(graphQLRequest);
	}

	@Override
	public QueryField getQueryContext() throws GraphQLRequestPreparationException {
		return new QueryField(QueryTypeRootResponse.class, QueryTypeResponse.class, "query");
	}

	@Override
	public QueryField getMutationContext() throws GraphQLRequestPreparationException {
		return new QueryField(MutationTypeRootResponse.class, MutationTypeResponse.class, "mutation");
	}

	@Override
	public QueryField getSubscriptionContext() throws GraphQLRequestPreparationException {
		throw new GraphQLRequestPreparationException("Subscriptions are not managed yet");
	}

}
