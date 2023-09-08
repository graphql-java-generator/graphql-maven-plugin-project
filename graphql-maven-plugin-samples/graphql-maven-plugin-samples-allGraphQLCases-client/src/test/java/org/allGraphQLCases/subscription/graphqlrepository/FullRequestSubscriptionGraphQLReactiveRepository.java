/**
 * 
 */
package org.allGraphQLCases.subscription.graphqlrepository;

import org.allGraphQLCases.client.CTP_TheSubscriptionType_CTS;
import org.allGraphQLCases.client.MyQueryTypeReactiveExecutorAllGraphQLCases;

import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.graphqlrepository.FullRequest;
import com.graphql_java_generator.client.graphqlrepository.GraphQLReactiveRepository;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

import reactor.core.publisher.Flux;

/**
 * This interface demonstrate the use of GraphqlRepository: it implements a unique Full Requests that is a subscription.
 * 
 * @author etienne-sf
 */
@GraphQLReactiveRepository(queryExecutor = MyQueryTypeReactiveExecutorAllGraphQLCases.class)
public interface FullRequestSubscriptionGraphQLReactiveRepository {

	@FullRequest(request = "subscription {subscribeToAList {}}", requestType = RequestType.subscription)
	public Flux<CTP_TheSubscriptionType_CTS> subscribeToAList() throws GraphQLRequestExecutionException;

}
