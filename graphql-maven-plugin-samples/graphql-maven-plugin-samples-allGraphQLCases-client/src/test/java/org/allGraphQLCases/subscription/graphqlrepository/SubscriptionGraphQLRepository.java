/**
 * 
 */
package org.allGraphQLCases.subscription.graphqlrepository;

import org.allGraphQLCases.client.CEP_EnumWithReservedJavaKeywordAsValues_CES;
import org.allGraphQLCases.client.MyQueryTypeExecutorAllGraphQLCases;

import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.SubscriptionCallback;
import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.client.graphqlrepository.FullRequest;
import com.graphql_java_generator.client.graphqlrepository.GraphQLRepository;
import com.graphql_java_generator.client.graphqlrepository.PartialRequest;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

/**
 * This interface demonstrates the use of GraphqlRepository: it implements subscriptions, with both Partial and Full
 * Requests.
 * 
 * @author etienne-sf
 */
@GraphQLRepository(queryExecutor = MyQueryTypeExecutorAllGraphQLCases.class)
public interface SubscriptionGraphQLRepository {

	@FullRequest(request = "subscription {subscribeToAList {}}", requestType = RequestType.subscription)
	public SubscriptionClient subscribeToAList(SubscriptionCallback<?> callback)
			throws GraphQLRequestExecutionException;

	@PartialRequest(request = "", requestType = RequestType.subscription)
	public SubscriptionClient returnEnum(SubscriptionCallback<?> callback) throws GraphQLRequestExecutionException;

	@PartialRequest(request = "", requestType = RequestType.subscription)
	public SubscriptionClient returnMandatoryEnum(SubscriptionCallback<?> callback,
			CEP_EnumWithReservedJavaKeywordAsValues_CES _enum) throws GraphQLRequestExecutionException;
}
