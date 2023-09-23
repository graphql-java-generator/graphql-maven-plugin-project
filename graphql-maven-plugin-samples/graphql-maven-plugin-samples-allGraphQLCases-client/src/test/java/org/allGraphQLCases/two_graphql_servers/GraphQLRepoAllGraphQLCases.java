/**
 * 
 */
package org.allGraphQLCases.two_graphql_servers;

import org.allGraphQLCases.client.util.MyQueryTypeExecutorAllGraphQLCases;

import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.SubscriptionCallback;
import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.client.graphqlrepository.FullRequest;
import com.graphql_java_generator.client.graphqlrepository.GraphQLRepository;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

/**
 * This is a GraphQL Repository that is based on the forum GraphQL schema, thanks to the {@link QueryTypeExecutor} class
 * provided to the {@link GraphQLRepository} annotation.
 * 
 * @author etienne-sf
 */
@GraphQLRepository(queryExecutor = MyQueryTypeExecutorAllGraphQLCases.class)
public interface GraphQLRepoAllGraphQLCases {

	@FullRequest(request = "subscription {subscribeToAList {}}", requestType = RequestType.subscription)
	SubscriptionClient subscribeToAList(SubscriptionCallback<?> callback) throws GraphQLRequestExecutionException;
}
