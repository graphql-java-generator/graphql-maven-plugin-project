/**
 * 
 */
package org.allGraphQLCases.graphqlrepository.non_spring;

import java.util.List;

import org.allGraphQLCases.client.CIP_Character_CIS;
import org.allGraphQLCases.client.CINP_CharacterInput_CINS;
import org.allGraphQLCases.client.CINP_HumanInput_CINS;

import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.graphqlrepository.BindParameter;
import com.graphql_java_generator.client.graphqlrepository.FullRequest;
import com.graphql_java_generator.client.graphqlrepository.GraphQLRepository;
import com.graphql_java_generator.client.graphqlrepository.PartialRequest;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import org.allGraphQLCases.client.CTP_AnotherMutationType_CTS;

/**
 * This is a demo of the use of a {@link GraphQLRepository}. It contains the definition of GraphQL requests. Doing this
 * hides all the wiring to prepare and execute the GraphQL requests (query/mutation/subscription).
 * 
 * @author etienne-sf
 */
@GraphQLRepository // Useless for non-Spring app. But you can consider using it for 'documentation'
public interface GraphQLRequests {

	@PartialRequest(request = "{appearsIn name }")
	public List<CIP_Character_CIS> withoutParameters() throws GraphQLRequestExecutionException;

	@PartialRequest(request = "{id appearsIn name}")
	public CIP_Character_CIS withOneOptionalParam(CINP_CharacterInput_CINS character) throws GraphQLRequestExecutionException;

	@FullRequest(request = "mutation {createHuman (human: &input) {id name} }", requestType = RequestType.mutation)
	public CTP_AnotherMutationType_CTS createHuman(@BindParameter(name = "input") CINP_HumanInput_CINS input)
			throws GraphQLRequestExecutionException;
}
