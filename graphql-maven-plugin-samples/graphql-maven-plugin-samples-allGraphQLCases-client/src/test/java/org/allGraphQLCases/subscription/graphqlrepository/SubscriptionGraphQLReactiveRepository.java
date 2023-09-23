/**
 * 
 */
package org.allGraphQLCases.subscription.graphqlrepository;

import java.util.Optional;

import org.allGraphQLCases.client.CEP_EnumWithReservedJavaKeywordAsValues_CES;
import org.allGraphQLCases.client.CTP_TheSubscriptionType_CTS;
import org.allGraphQLCases.client.util.MyQueryTypeReactiveExecutorAllGraphQLCases;

import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.graphqlrepository.FullRequest;
import com.graphql_java_generator.client.graphqlrepository.GraphQLReactiveRepository;
import com.graphql_java_generator.client.graphqlrepository.PartialRequest;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

import reactor.core.publisher.Flux;

/**
 * This interface demonstrates the use of {@link GraphQLReactiveRepository}: it implements a unique Full Requests that
 * is a subscription.
 * 
 * @author etienne-sf
 */
@GraphQLReactiveRepository(queryExecutor = MyQueryTypeReactiveExecutorAllGraphQLCases.class)
public interface SubscriptionGraphQLReactiveRepository {

	@FullRequest(request = "subscription {subscribeToAList {}}", requestType = RequestType.subscription)
	public Flux<CTP_TheSubscriptionType_CTS> subscribeToAList() throws GraphQLRequestExecutionException;

	@PartialRequest(request = "", requestType = RequestType.subscription)
	public Flux<Optional<CEP_EnumWithReservedJavaKeywordAsValues_CES>> returnEnum()
			throws GraphQLRequestExecutionException;

	@PartialRequest(request = "", requestType = RequestType.subscription)
	public Flux<Optional<CEP_EnumWithReservedJavaKeywordAsValues_CES>> returnMandatoryEnum(
			CEP_EnumWithReservedJavaKeywordAsValues_CES _enum) throws GraphQLRequestExecutionException;

}
