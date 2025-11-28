/**
 * 
 */
package org.allGraphQLCases.demo.impl;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.allGraphQLCases.client.CEP_Episode_CES;
import org.allGraphQLCases.client.CEP_extends_CES;
import org.allGraphQLCases.client.CINP_AllFieldCasesInput_CINS;
import org.allGraphQLCases.client.CINP_CharacterInput_CINS;
import org.allGraphQLCases.client.CINP_FieldParameterInput_CINS;
import org.allGraphQLCases.client.CINP_HumanInput_CINS;
import org.allGraphQLCases.client.CIP_Character_CIS;
import org.allGraphQLCases.client.CTP_AllFieldCases_CTS;
import org.allGraphQLCases.client.CTP_Human_CTS;
import org.allGraphQLCases.client.CTP_break_CTS;
import org.allGraphQLCases.client.util.MyQueryTypeReactiveExecutorAllGraphQLCases;
import org.allGraphQLCases.demo.PartialQueries;

import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.graphqlrepository.BindParameter;
import com.graphql_java_generator.client.graphqlrepository.GraphQLReactiveRepository;
import com.graphql_java_generator.client.graphqlrepository.GraphQLRepository;
import com.graphql_java_generator.client.graphqlrepository.PartialRequest;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

import reactor.core.publisher.Mono;

/**
 * This interface demonstrate the use of GraphqlRepository: it just redefines the method of the {@link PartialQueries}
 * interface. These methods:
 * <UL>
 * <LI>Are marked with the {@link Override} annotation, to make sure it comes from the super interface (useless in
 * normal use case, as the {@link GraphQLRepository} interface would not inherit from another interface)</LI>
 * <LI>Are marked with the {@link PartialRequest} annotation, to define the associated GraphQL request</LI>
 * </UL>
 * Please note that the class is itself marked with the {@link GraphQLRepository} annotation.<BR/>
 * And <B>that's it</B>: no implementation class is needed. Everything is done at runtime by the plugin runtime code.
 * 
 * @author etienne-sf
 */
@GraphQLReactiveRepository(queryExecutor = MyQueryTypeReactiveExecutorAllGraphQLCases.class)
public interface PartialRequestGraphQLReactiveRepository {

	////////////////////////////////////////////////////////////////////////////
	// First part: partialQueries (based on the Star Wars use case)
	@PartialRequest(request = "{appearsIn name}")
	Mono<Optional<List<CIP_Character_CIS>>> withoutParameters() throws GraphQLRequestExecutionException;

	@PartialRequest(request = "{id name appearsIn friends {id name}}")
	Mono<Optional<CIP_Character_CIS>> withOneOptionalParam(CINP_CharacterInput_CINS character)
			throws GraphQLRequestExecutionException;

	@PartialRequest(request = "{id name appearsIn friends {id name}}")
	Mono<Optional<CIP_Character_CIS>> withOneMandatoryParam(CINP_CharacterInput_CINS character)
			throws GraphQLRequestExecutionException;

	@PartialRequest(request = "{id name appearsIn friends {id name}}")
	Mono<Optional<CIP_Character_CIS>> withEnum(CEP_Episode_CES episode) throws GraphQLRequestExecutionException;

	@PartialRequest(request = "{id name appearsIn friends {id name}}")
	Mono<Optional<List<CIP_Character_CIS>>> withList(String name, List<CINP_CharacterInput_CINS> friends)
			throws GraphQLRequestExecutionException;

	@PartialRequest(request = "{id name appearsIn friends {id name}}")
	Mono<Optional<CIP_Character_CIS>> error(String errorLabel) throws GraphQLRequestExecutionException;

	////////////////////////////////////////////////////////////////////////////
	// Second part: partialQueries (based on the allGraphQLCases use case)

	@PartialRequest(request = "{ ... on WithID { id } name date dateTime dates " //
			+ " forname(uppercase: ?uppercase, textToAppendToTheForname: ?textToAppendToTheForname) "
			+ " age nbComments " + " comments booleans aliases planets friends {id}" //
			+ " oneWithIdSubType {id name} "//
			+ " listWithIdSubTypes(nbItems: ?nbItemsWithId, date: ?date, dates: &dates, uppercaseName: ?uppercaseNameList, textToAppendToTheName: ?textToAppendToTheNameWithId) {name id}"
			+ " oneWithoutIdSubType(input: ?input) {name}"//
			+ " listWithoutIdSubTypes(nbItems: ?nbItemsWithoutId, input: ?inputList, textToAppendToTheName: ?textToAppendToTheNameWithoutId) {name}" //
			+ "}")
	public Mono<Optional<CTP_AllFieldCases_CTS>> allFieldCases(CINP_AllFieldCasesInput_CINS allFieldCasesInput,
			@BindParameter(name = "uppercase") Boolean uppercase,
			@BindParameter(name = "textToAppendToTheForname") String textToAppendToTheForname,
			@BindParameter(name = "nbItemsWithId") long nbItemsWithId, //
			@BindParameter(name = "date") Date date, // ,
			@BindParameter(name = "dateTime") OffsetDateTime dateTime, //
			@BindParameter(name = "dates") List<Date> dates,
			@BindParameter(name = "uppercaseNameList") Boolean uppercaseNameList,
			@BindParameter(name = "textToAppendToTheNameWithId") String textToAppendToTheNameWithId,
			@BindParameter(name = "input") CINP_FieldParameterInput_CINS input,
			@BindParameter(name = "nbItemsWithoutId") int nbItemsWithoutId,
			@BindParameter(name = "inputList") CINP_FieldParameterInput_CINS inputList,
			@BindParameter(name = "textToAppendToTheNameWithoutId") String textToAppendToTheNameWithoutId)
			throws GraphQLRequestExecutionException;

	////////////////////////////////////////////////////////////////////////////
	// Third part: check of GraphQL types that are java keywords

	@PartialRequest(request = "{case(test: &test, if: ?if)}")
	public Mono<Optional<CTP_break_CTS>> aBreak(@BindParameter(name = "test") CEP_extends_CES test,
			@BindParameter(name = "if") String _if) throws GraphQLRequestExecutionException;

	////////////////////////////////////////////////////////////////////////////
	// Fourth part: a mutation

	@PartialRequest(request = "{id name}", requestType = RequestType.mutation)
	Mono<Optional<CTP_Human_CTS>> createHuman(CINP_HumanInput_CINS human) throws GraphQLRequestExecutionException;

}
