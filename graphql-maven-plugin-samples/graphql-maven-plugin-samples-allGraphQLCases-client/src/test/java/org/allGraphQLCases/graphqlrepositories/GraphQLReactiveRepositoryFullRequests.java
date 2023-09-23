/**
 * 
 */
package org.allGraphQLCases.graphqlrepositories;

import org.allGraphQLCases.client.CINP_CharacterInput_CINS;
import org.allGraphQLCases.client.CINP_HumanInput_CINS;
import org.allGraphQLCases.client.CTP_AnotherMutationType_CTS;
import org.allGraphQLCases.client.CTP_MyQueryType_CTS;
import org.allGraphQLCases.client.util.MyQueryTypeReactiveExecutorAllGraphQLCases;

import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.graphqlrepository.BindParameter;
import com.graphql_java_generator.client.graphqlrepository.FullRequest;
import com.graphql_java_generator.client.graphqlrepository.GraphQLReactiveRepository;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

import reactor.core.publisher.Mono;

/**
 * This interface demonstrate the use of {@link GraphQLReactiveRepository}: it implements a unique Full Requests that is
 * a subscription.
 * 
 * @author etienne-sf
 */
@GraphQLReactiveRepository(queryExecutor = MyQueryTypeReactiveExecutorAllGraphQLCases.class)
public interface GraphQLReactiveRepositoryFullRequests {

	@FullRequest(request = "{withoutParameters{appearsIn name} withOneOptionalParam(character: &character){id name appearsIn friends {id name}}}")
	Mono<CTP_MyQueryType_CTS> fullRequestQuery(//
			@BindParameter(name = "character") CINP_CharacterInput_CINS character//
	) throws GraphQLRequestExecutionException;

	@FullRequest(request = "mutation { createHuman(human:&human) {id name}}", requestType = RequestType.mutation)
	Mono<CTP_AnotherMutationType_CTS> fullRequestMutation(//
			@BindParameter(name = "human") CINP_HumanInput_CINS human//
	) throws GraphQLRequestExecutionException;

	// Subscription are tested in the org.allGraphQLCases.subscription.graphqlrepository package
}
