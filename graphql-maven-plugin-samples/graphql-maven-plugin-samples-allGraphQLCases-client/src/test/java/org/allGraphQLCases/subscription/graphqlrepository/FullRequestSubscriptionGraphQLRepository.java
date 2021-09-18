/**
 * 
 */
package org.allGraphQLCases.subscription.graphqlrepository;

import org.allGraphQLCases.client.util.MyQueryTypeExecutorAllGraphQLCases;

import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.SubscriptionCallback;
import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.client.graphqlrepository.FullRequest;
import com.graphql_java_generator.client.graphqlrepository.GraphQLRepository;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

/**
 * This interface demonstrate the use of GraphqlRepository: it implements a unique Full Requests that is a subscription.
 * 
 * @author etienne-sf
 */
@GraphQLRepository(queryExecutor = MyQueryTypeExecutorAllGraphQLCases.class)
public interface FullRequestSubscriptionGraphQLRepository {

	@FullRequest(request = "subscription {subscribeToAList {}}", requestType = RequestType.subscription)
	SubscriptionClient subscribeToAList(SubscriptionCallback<?> callback) throws GraphQLRequestExecutionException;

}
