/**
 * 
 */
package org.allGraphQLCases.demo.impl;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

import org.allGraphQLCases.client.AllFieldCases;
import org.allGraphQLCases.client.AllFieldCasesInput;
import org.allGraphQLCases.client.Character;
import org.allGraphQLCases.client.CharacterInput;
import org.allGraphQLCases.client.Episode;
import org.allGraphQLCases.client.FieldParameterInput;
import org.allGraphQLCases.client.Human;
import org.allGraphQLCases.client.HumanInput;
import org.allGraphQLCases.client._break;
import org.allGraphQLCases.client._extends;
import org.allGraphQLCases.client.util.MyQueryTypeExecutorAllGraphQLCases;
import org.allGraphQLCases.demo.PartialQueries;

import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.graphqlrepository.BindParameter;
import com.graphql_java_generator.client.graphqlrepository.GraphQLRepository;
import com.graphql_java_generator.client.graphqlrepository.PartialRequest;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

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
@GraphQLRepository(queryExecutor = MyQueryTypeExecutorAllGraphQLCases.class)
public interface PartialRequestGraphQLRepository extends PartialQueries {

	////////////////////////////////////////////////////////////////////////////
	// First part: partialQueries (based on the Star Wars use case)
	@Override
	@PartialRequest(request = "{appearsIn name}")
	List<Character> withoutParameters() throws GraphQLRequestExecutionException;

	@Override
	@PartialRequest(request = "{id name appearsIn friends {id name}}")
	Character withOneOptionalParam(CharacterInput character) throws GraphQLRequestExecutionException;

	@Override
	@PartialRequest(request = "{id name appearsIn friends {id name}}")
	Character withOneMandatoryParam(CharacterInput character) throws GraphQLRequestExecutionException;

	@Override
	@PartialRequest(request = "{id name appearsIn friends {id name}}")
	Character withEnum(Episode episode) throws GraphQLRequestExecutionException;

	@Override
	@PartialRequest(request = "{id name appearsIn friends {id name}}")
	List<Character> withList(String name, List<CharacterInput> friends) throws GraphQLRequestExecutionException;

	@Override
	@PartialRequest(request = "{id name appearsIn friends {id name}}")
	Character error(String errorLabel) throws GraphQLRequestExecutionException;

	////////////////////////////////////////////////////////////////////////////
	// Second part: partialQueries (based on the allGraphQLCases use case)

	@Override
	@PartialRequest(request = "{ ... on WithID { id } name date dateTime dates " //
			+ " forname(uppercase: ?uppercase, textToAppendToTheForname: ?textToAppendToTheForname) "
			+ " age nbComments " + " comments booleans aliases planets friends {id}" //
			+ " oneWithIdSubType {id name} "//
			+ " listWithIdSubTypes(nbItems: ?nbItemsWithId, date: ?date, dates: &dates, uppercaseName: ?uppercaseNameList, textToAppendToTheForname: ?textToAppendToTheFornameWithId) {name id}"
			+ " oneWithoutIdSubType(input: ?input) {name}"//
			+ " listWithoutIdSubTypes(nbItems: ?nbItemsWithoutId, input: ?inputList, textToAppendToTheForname: ?textToAppendToTheFornameWithoutId) {name}" //
			+ "}")
	public AllFieldCases allFieldCases(AllFieldCasesInput allFieldCasesInput,
			@BindParameter(name = "uppercase") Boolean uppercase,
			@BindParameter(name = "textToAppendToTheForname") String textToAppendToTheForname,
			@BindParameter(name = "nbItemsWithId") long nbItemsWithId, //
			@BindParameter(name = "date") Date date, // ,
			@BindParameter(name = "dateTime") OffsetDateTime dateTime, //
			@BindParameter(name = "dates") List<Date> dates,
			@BindParameter(name = "uppercaseNameList") Boolean uppercaseNameList,
			@BindParameter(name = "textToAppendToTheFornameWithId") String textToAppendToTheFornameWithId,
			@BindParameter(name = "input") FieldParameterInput input,
			@BindParameter(name = "nbItemsWithoutId") int nbItemsWithoutId,
			@BindParameter(name = "inputList") FieldParameterInput inputList,
			@BindParameter(name = "textToAppendToTheFornameWithoutId") String textToAppendToTheFornameWithoutId)
			throws GraphQLRequestExecutionException;

	////////////////////////////////////////////////////////////////////////////
	// Third part: check of GraphQL types that are java keywords

	@Override
	@PartialRequest(request = "{case(test: &test, if: ?if)}")
	public _break aBreak(@BindParameter(name = "test") _extends test, @BindParameter(name = "if") String _if)
			throws GraphQLRequestExecutionException;

	////////////////////////////////////////////////////////////////////////////
	// Fourth part: a mutation

	@Override
	@PartialRequest(request = "{id name}", requestType = RequestType.mutation)
	Human createHuman(HumanInput human) throws GraphQLRequestExecutionException;

}
