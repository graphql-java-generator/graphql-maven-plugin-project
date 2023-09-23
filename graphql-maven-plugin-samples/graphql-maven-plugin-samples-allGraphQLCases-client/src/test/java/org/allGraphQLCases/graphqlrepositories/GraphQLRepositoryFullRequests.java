/**
 * 
 */
package org.allGraphQLCases.graphqlrepositories;

import org.allGraphQLCases.client.CINP_CharacterInput_CINS;
import org.allGraphQLCases.client.CINP_HumanInput_CINS;
import org.allGraphQLCases.client.CTP_AnotherMutationType_CTS;
import org.allGraphQLCases.client.CTP_MyQueryType_CTS;
import org.allGraphQLCases.client.util.MyQueryTypeExecutorAllGraphQLCases;

import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.graphqlrepository.BindParameter;
import com.graphql_java_generator.client.graphqlrepository.FullRequest;
import com.graphql_java_generator.client.graphqlrepository.GraphQLRepository;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

/**
 * This interface demonstrates the use of {@link GraphQLRepository}.
 * 
 * @author etienne-sf
 */
@GraphQLRepository(queryExecutor = MyQueryTypeExecutorAllGraphQLCases.class)
public interface GraphQLRepositoryFullRequests {

	@FullRequest(request = "{withoutParameters{appearsIn name} withOneOptionalParam(character: &character){id name appearsIn friends {id name}}}")
	public CTP_MyQueryType_CTS fullRequestQuery(//
			@BindParameter(name = "character") CINP_CharacterInput_CINS character//
	) throws GraphQLRequestExecutionException;

	@FullRequest(request = "mutation { createHuman(human:&human) {id name}}", requestType = RequestType.mutation)
	CTP_AnotherMutationType_CTS fullRequestMutation(//
			@BindParameter(name = "human") CINP_HumanInput_CINS human//
	) throws GraphQLRequestExecutionException;

	// Subscription are tested in the org.allGraphQLCases.subscription.graphqlrepository package
}
