/**
 * 
 */
package org.forum.server.specific_code;

import org.forum.server.graphql.GraphQLWiring;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import graphql.schema.idl.TypeRuntimeWiring;

/**
 * @author etienne-sf
 */
@Component
@Primary
public class CustomGraphQLWiring extends GraphQLWiring {

	@Override
	protected TypeRuntimeWiring.Builder addWiringForSubscription(TypeRuntimeWiring.Builder typeWiring) {
		typeWiring.dataFetcher("subscribeToNewPost",
				graphQLDataFetchers.dataFetchersDelegateSubscriptionSubscribeToNewPost());
		return typeWiring;
	}

}
