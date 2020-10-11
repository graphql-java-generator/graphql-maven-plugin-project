/** Generated by the default template from graphql-java-generator */
package com.graphql_java_generator.server.domain.allGraphQLCases;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Resource;

import org.dataloader.DataLoader;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.GraphqlUtils;

import graphql.schema.DataFetcher;

/**
 * @author generated by graphql-java-generator
 * @see <a href=
 *      "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@Component
public class GraphQLDataFetchers {

	/** The logger for this instance */
	protected Logger logger = LoggerFactory.getLogger(GraphQLDataFetchers.class);

	@Resource
	DataFetchersDelegateMyQueryType dataFetchersDelegateMyQueryType;

	@Resource
	DataFetchersDelegateAnotherMutationType dataFetchersDelegateAnotherMutationType;

	@Resource
	DataFetchersDelegateTheSubscriptionType dataFetchersDelegateTheSubscriptionType;

	@Resource
	DataFetchersDelegateAllFieldCases dataFetchersDelegateAllFieldCases;

	@Resource
	DataFetchersDelegateAllFieldCasesInterfaceType dataFetchersDelegateAllFieldCasesInterfaceType;

	@Resource
	DataFetchersDelegateHuman dataFetchersDelegateHuman;

	@Resource
	DataFetchersDelegateDroid dataFetchersDelegateDroid;

	@Resource
	DataFetchersDelegateCommented dataFetchersDelegateCommented;

	@Resource
	DataFetchersDelegateAllFieldCasesInterface dataFetchersDelegateAllFieldCasesInterface;

	@Resource
	DataFetchersDelegateCharacter dataFetchersDelegateCharacter;

	@Resource
	DataFetchersDelegateAllFieldCasesWithIdSubtype dataFetchersDelegateAllFieldCasesWithIdSubtype;

	@Resource
	DataFetchersDelegateWithID dataFetchersDelegateWithID;

	@Resource
	GraphqlUtils graphqlUtils;

	////////////////////////////////////////////////////////////////////////////////////////////////
	// Data fetchers for DataFetchersDelegateMyQueryType
	////////////////////////////////////////////////////////////////////////////////////////////////

	public DataFetcher<List<Character>> dataFetchersDelegateMyQueryTypeWithoutParameters() {
		return dataFetchingEnvironment -> {

			List<Character> ret = dataFetchersDelegateMyQueryType.withoutParameters(dataFetchingEnvironment);
			logger.debug("withoutParameters: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	public DataFetcher<Character> dataFetchersDelegateMyQueryTypeWithOneOptionalParam() {
		return dataFetchingEnvironment -> {
			CharacterInput character = (CharacterInput) graphqlUtils.getInputObject(
					dataFetchingEnvironment.getArgument("character"), "CharacterInput", CharacterInput.class);

			Character ret = null;
			try {
				ret = dataFetchersDelegateMyQueryType.withOneOptionalParam(dataFetchingEnvironment, character);
			} catch (NoSuchElementException e) {
				// There was no items in the Optional
			}

			if (ret != null)
				logger.debug("withOneOptionalParam: 1 result found");
			else
				logger.debug("withOneOptionalParam: no result found");

			return ret;
		};
	}

	public DataFetcher<Character> dataFetchersDelegateMyQueryTypeWithOneMandatoryParam() {
		return dataFetchingEnvironment -> {
			CharacterInput character = (CharacterInput) graphqlUtils.getInputObject(
					dataFetchingEnvironment.getArgument("character"), "CharacterInput", CharacterInput.class);

			Character ret = null;
			try {
				ret = dataFetchersDelegateMyQueryType.withOneMandatoryParam(dataFetchingEnvironment, character);
			} catch (NoSuchElementException e) {
				// There was no items in the Optional
			}

			if (ret != null)
				logger.debug("withOneMandatoryParam: 1 result found");
			else
				logger.debug("withOneMandatoryParam: no result found");

			return ret;
		};
	}

	public DataFetcher<Character> dataFetchersDelegateMyQueryTypeWithOneMandatoryParamDefaultValue() {
		return dataFetchingEnvironment -> {
			Integer nbResultat = dataFetchingEnvironment.getArgument("nbResultat");

			Character ret = null;
			try {
				ret = dataFetchersDelegateMyQueryType.withOneMandatoryParamDefaultValue(dataFetchingEnvironment,
						nbResultat);
			} catch (NoSuchElementException e) {
				// There was no items in the Optional
			}

			if (ret != null)
				logger.debug("withOneMandatoryParamDefaultValue: 1 result found");
			else
				logger.debug("withOneMandatoryParamDefaultValue: no result found");

			return ret;
		};
	}

	public DataFetcher<Droid> dataFetchersDelegateMyQueryTypeWithTwoMandatoryParamDefaultVal() {
		return dataFetchingEnvironment -> {
			DroidInput theHero = (DroidInput) graphqlUtils
					.getInputObject(dataFetchingEnvironment.getArgument("theHero"), "DroidInput", DroidInput.class);
			Integer num = dataFetchingEnvironment.getArgument("num");

			Droid ret = null;
			try {
				ret = dataFetchersDelegateMyQueryType.withTwoMandatoryParamDefaultVal(dataFetchingEnvironment, theHero,
						num);
			} catch (NoSuchElementException e) {
				// There was no items in the Optional
			}

			if (ret != null)
				logger.debug("withTwoMandatoryParamDefaultVal: 1 result found");
			else
				logger.debug("withTwoMandatoryParamDefaultVal: no result found");

			return ret;
		};
	}

	public DataFetcher<Character> dataFetchersDelegateMyQueryTypeWithEnum() {
		return dataFetchingEnvironment -> {
			Episode episode = Episode.valueOf(dataFetchingEnvironment.getArgument("episode"));

			Character ret = null;
			try {
				ret = dataFetchersDelegateMyQueryType.withEnum(dataFetchingEnvironment, episode);
			} catch (NoSuchElementException e) {
				// There was no items in the Optional
			}

			if (ret != null)
				logger.debug("withEnum: 1 result found");
			else
				logger.debug("withEnum: no result found");

			return ret;
		};
	}

	public DataFetcher<List<Character>> dataFetchersDelegateMyQueryTypeWithList() {
		return dataFetchingEnvironment -> {
			String firstName = dataFetchingEnvironment.getArgument("firstName");
			@SuppressWarnings("unchecked")
			List<CharacterInput> characters = (List<CharacterInput>) graphqlUtils.getInputObject(
					dataFetchingEnvironment.getArgument("characters"), "CharacterInput", CharacterInput.class);

			List<Character> ret = dataFetchersDelegateMyQueryType.withList(dataFetchingEnvironment, firstName,
					characters);
			logger.debug("withList: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	public DataFetcher<AllFieldCases> dataFetchersDelegateMyQueryTypeAllFieldCases() {
		return dataFetchingEnvironment -> {
			AllFieldCasesInput input = (AllFieldCasesInput) graphqlUtils.getInputObject(
					dataFetchingEnvironment.getArgument("input"), "AllFieldCasesInput", AllFieldCasesInput.class);

			AllFieldCases ret = null;
			try {
				ret = dataFetchersDelegateMyQueryType.allFieldCases(dataFetchingEnvironment, input);
			} catch (NoSuchElementException e) {
				// There was no items in the Optional
			}

			if (ret != null)
				logger.debug("allFieldCases: 1 result found");
			else
				logger.debug("allFieldCases: no result found");

			return ret;
		};
	}

	public DataFetcher<List<AnyCharacter>> dataFetchersDelegateMyQueryTypeUnionTest() {
		return dataFetchingEnvironment -> {
			HumanInput human1 = (HumanInput) graphqlUtils.getInputObject(dataFetchingEnvironment.getArgument("human1"),
					"HumanInput", HumanInput.class);
			HumanInput human2 = (HumanInput) graphqlUtils.getInputObject(dataFetchingEnvironment.getArgument("human1"),
					"HumanInput", HumanInput.class);
			DroidInput droid1 = (DroidInput) graphqlUtils.getInputObject(dataFetchingEnvironment.getArgument("droid1"),
					"DroidInput", DroidInput.class);
			DroidInput droid2 = (DroidInput) graphqlUtils.getInputObject(dataFetchingEnvironment.getArgument("droid2"),
					"DroidInput", DroidInput.class);

			List<AnyCharacter> ret = dataFetchersDelegateMyQueryType.unionTest(dataFetchingEnvironment, human1, human2,
					droid1, droid2);
			logger.debug("unionTest: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	public DataFetcher<Character> dataFetchersDelegateMyQueryTypeError() {
		return dataFetchingEnvironment -> {
			String errorLabel = dataFetchingEnvironment.getArgument("errorLabel");

			Character ret = null;
			try {
				ret = dataFetchersDelegateMyQueryType.error(dataFetchingEnvironment, errorLabel);
			} catch (NoSuchElementException e) {
				// There was no items in the Optional
			}

			if (ret != null)
				logger.debug("error: 1 result found");
			else
				logger.debug("error: no result found");

			return ret;
		};
	}

	public DataFetcher<_break> dataFetchersDelegateMyQueryTypeABreak() {
		return dataFetchingEnvironment -> {

			_break ret = null;
			try {
				ret = dataFetchersDelegateMyQueryType.aBreak(dataFetchingEnvironment);
			} catch (NoSuchElementException e) {
				// There was no items in the Optional
			}

			if (ret != null)
				logger.debug("aBreak: 1 result found");
			else
				logger.debug("aBreak: no result found");

			return ret;
		};
	}

	public DataFetcher<List<String>> dataFetchersDelegateMyQueryTypeDirectiveOnQuery() {
		return dataFetchingEnvironment -> {
			Boolean uppercase = dataFetchingEnvironment.getArgument("uppercase");

			List<String> ret = dataFetchersDelegateMyQueryType.directiveOnQuery(dataFetchingEnvironment, uppercase);
			logger.debug("directiveOnQuery: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	public DataFetcher<Character> dataFetchersDelegateMyQueryTypeDirectiveOnField() {
		return dataFetchingEnvironment -> {

			Character ret = null;
			try {
				ret = dataFetchersDelegateMyQueryType.directiveOnField(dataFetchingEnvironment);
			} catch (NoSuchElementException e) {
				// There was no items in the Optional
			}

			if (ret != null)
				logger.debug("directiveOnField: 1 result found");
			else
				logger.debug("directiveOnField: no result found");

			return ret;
		};
	}

	public DataFetcher<MyQueryType> dataFetchersDelegateMyQueryTypeRelay() {
		return dataFetchingEnvironment -> {

			MyQueryType ret = null;
			try {
				ret = dataFetchersDelegateMyQueryType.relay(dataFetchingEnvironment);
			} catch (NoSuchElementException e) {
				// There was no items in the Optional
			}

			if (ret != null)
				logger.debug("relay: 1 result found");
			else
				logger.debug("relay: no result found");

			return ret;
		};
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	// Data fetchers for DataFetchersDelegateAnotherMutationType
	////////////////////////////////////////////////////////////////////////////////////////////////

	public DataFetcher<Human> dataFetchersDelegateAnotherMutationTypeCreateHuman() {
		return dataFetchingEnvironment -> {
			HumanInput human = (HumanInput) graphqlUtils.getInputObject(dataFetchingEnvironment.getArgument("human"),
					"HumanInput", HumanInput.class);

			Human ret = null;
			try {
				ret = dataFetchersDelegateAnotherMutationType.createHuman(dataFetchingEnvironment, human);
			} catch (NoSuchElementException e) {
				// There was no items in the Optional
			}

			if (ret != null)
				logger.debug("createHuman: 1 result found");
			else
				logger.debug("createHuman: no result found");

			return ret;
		};
	}

	public DataFetcher<AllFieldCases> dataFetchersDelegateAnotherMutationTypeCreateAllFieldCases() {
		return dataFetchingEnvironment -> {
			AllFieldCasesInput input = (AllFieldCasesInput) graphqlUtils.getInputObject(
					dataFetchingEnvironment.getArgument("input"), "AllFieldCasesInput", AllFieldCasesInput.class);

			AllFieldCases ret = null;
			try {
				ret = dataFetchersDelegateAnotherMutationType.createAllFieldCases(dataFetchingEnvironment, input);
			} catch (NoSuchElementException e) {
				// There was no items in the Optional
			}

			if (ret != null)
				logger.debug("createAllFieldCases: 1 result found");
			else
				logger.debug("createAllFieldCases: no result found");

			return ret;
		};
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	// Data fetchers for DataFetchersDelegateTheSubscriptionType
	////////////////////////////////////////////////////////////////////////////////////////////////

	public DataFetcher<Publisher<Human>> dataFetchersDelegateTheSubscriptionTypeSubscribeNewHumanForEpisode() {
		return dataFetchingEnvironment -> {
			Episode episode = Episode.valueOf(dataFetchingEnvironment.getArgument("episode"));

			Publisher<Human> ret = null;
			try {
				ret = dataFetchersDelegateTheSubscriptionType.subscribeNewHumanForEpisode(dataFetchingEnvironment,
						episode);
			} catch (NoSuchElementException e) {
				// There was no items in the Optional
			}

			if (ret != null)
				logger.debug("subscribeNewHumanForEpisode: 1 result found");
			else
				logger.debug("subscribeNewHumanForEpisode: no result found");

			return ret;
		};
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	// Data fetchers for DataFetchersDelegateAllFieldCases
	////////////////////////////////////////////////////////////////////////////////////////////////

	public DataFetcher<List<Date>> dataFetchersDelegateAllFieldCasesDates() {
		return dataFetchingEnvironment -> {
			AllFieldCases source = dataFetchingEnvironment.getSource();

			List<Date> ret = dataFetchersDelegateAllFieldCases.dates(dataFetchingEnvironment, source);
			logger.debug("dates: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	public DataFetcher<List<String>> dataFetchersDelegateAllFieldCasesComments() {
		return dataFetchingEnvironment -> {
			AllFieldCases source = dataFetchingEnvironment.getSource();

			List<String> ret = dataFetchersDelegateAllFieldCases.comments(dataFetchingEnvironment, source);
			logger.debug("comments: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	public DataFetcher<List<Boolean>> dataFetchersDelegateAllFieldCasesBooleans() {
		return dataFetchingEnvironment -> {
			AllFieldCases source = dataFetchingEnvironment.getSource();

			List<Boolean> ret = dataFetchersDelegateAllFieldCases.booleans(dataFetchingEnvironment, source);
			logger.debug("booleans: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	public DataFetcher<List<String>> dataFetchersDelegateAllFieldCasesAliases() {
		return dataFetchingEnvironment -> {
			AllFieldCases source = dataFetchingEnvironment.getSource();

			List<String> ret = dataFetchersDelegateAllFieldCases.aliases(dataFetchingEnvironment, source);
			logger.debug("aliases: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	public DataFetcher<List<String>> dataFetchersDelegateAllFieldCasesPlanets() {
		return dataFetchingEnvironment -> {
			AllFieldCases source = dataFetchingEnvironment.getSource();

			List<String> ret = dataFetchersDelegateAllFieldCases.planets(dataFetchingEnvironment, source);
			logger.debug("planets: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	public DataFetcher<List<Human>> dataFetchersDelegateAllFieldCasesFriends() {
		return dataFetchingEnvironment -> {
			AllFieldCases source = dataFetchingEnvironment.getSource();

			List<Human> ret = dataFetchersDelegateAllFieldCases.friends(dataFetchingEnvironment, source);
			logger.debug("friends: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	public DataFetcher<AllFieldCasesWithIdSubtype> dataFetchersDelegateAllFieldCasesOneWithIdSubType() {
		return dataFetchingEnvironment -> {
			AllFieldCases source = dataFetchingEnvironment.getSource();

			AllFieldCasesWithIdSubtype ret = null;
			try {
				ret = dataFetchersDelegateAllFieldCases.oneWithIdSubType(dataFetchingEnvironment, source);
			} catch (NoSuchElementException e) {
				// There was no items in the Optional
			}

			if (ret != null)
				logger.debug("oneWithIdSubType: 1 result found");
			else
				logger.debug("oneWithIdSubType: no result found");

			return ret;
		};
	}

	public DataFetcher<CompletableFuture<AllFieldCasesWithIdSubtype>> dataFetchersDelegateAllFieldCasesOneWithIdSubTypeWithDataLoader() {
		return dataFetchingEnvironment -> {
			AllFieldCases source = dataFetchingEnvironment.getSource();

			DataLoader<UUID, AllFieldCasesWithIdSubtype> dataLoader = dataFetchingEnvironment
					.getDataLoader("AllFieldCasesWithIdSubtype");

			// This dataLoader may be null. Let's hande that:
			if (dataLoader != null)
				return dataFetchersDelegateAllFieldCases.oneWithIdSubType(dataFetchingEnvironment, dataLoader, source);
			else
				return CompletableFuture.supplyAsync(
						() -> dataFetchersDelegateAllFieldCases.oneWithIdSubType(dataFetchingEnvironment, source));
		};
	}

	public DataFetcher<List<AllFieldCasesWithIdSubtype>> dataFetchersDelegateAllFieldCasesListWithIdSubTypes() {
		return dataFetchingEnvironment -> {
			Long nbItems = dataFetchingEnvironment.getArgument("nbItems");
			Date date = dataFetchingEnvironment.getArgument("date");
			List<Date> dates = dataFetchingEnvironment.getArgument("dates");
			Boolean uppercaseName = dataFetchingEnvironment.getArgument("uppercaseName");
			String textToAppendToTheForname = dataFetchingEnvironment.getArgument("textToAppendToTheForname");
			AllFieldCases source = dataFetchingEnvironment.getSource();

			List<AllFieldCasesWithIdSubtype> ret = dataFetchersDelegateAllFieldCases.listWithIdSubTypes(
					dataFetchingEnvironment, source, nbItems, date, dates, uppercaseName, textToAppendToTheForname);
			logger.debug("listWithIdSubTypes: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	public DataFetcher<AllFieldCasesWithoutIdSubtype> dataFetchersDelegateAllFieldCasesOneWithoutIdSubType() {
		return dataFetchingEnvironment -> {
			FieldParameterInput input = (FieldParameterInput) graphqlUtils.getInputObject(
					dataFetchingEnvironment.getArgument("input"), "FieldParameterInput", FieldParameterInput.class);
			AllFieldCases source = dataFetchingEnvironment.getSource();

			AllFieldCasesWithoutIdSubtype ret = null;
			try {
				ret = dataFetchersDelegateAllFieldCases.oneWithoutIdSubType(dataFetchingEnvironment, source, input);
			} catch (NoSuchElementException e) {
				// There was no items in the Optional
			}

			if (ret != null)
				logger.debug("oneWithoutIdSubType: 1 result found");
			else
				logger.debug("oneWithoutIdSubType: no result found");

			return ret;
		};
	}

	public DataFetcher<List<AllFieldCasesWithoutIdSubtype>> dataFetchersDelegateAllFieldCasesListWithoutIdSubTypes() {
		return dataFetchingEnvironment -> {
			Long nbItems = dataFetchingEnvironment.getArgument("nbItems");
			FieldParameterInput input = (FieldParameterInput) graphqlUtils.getInputObject(
					dataFetchingEnvironment.getArgument("input"), "FieldParameterInput", FieldParameterInput.class);
			String textToAppendToTheForname = dataFetchingEnvironment.getArgument("textToAppendToTheForname");
			AllFieldCases source = dataFetchingEnvironment.getSource();

			List<AllFieldCasesWithoutIdSubtype> ret = dataFetchersDelegateAllFieldCases
					.listWithoutIdSubTypes(dataFetchingEnvironment, source, nbItems, input, textToAppendToTheForname);
			logger.debug("listWithoutIdSubTypes: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	// Data fetchers for DataFetchersDelegateAllFieldCasesInterfaceType
	////////////////////////////////////////////////////////////////////////////////////////////////

	public DataFetcher<List<String>> dataFetchersDelegateAllFieldCasesInterfaceTypeComments() {
		return dataFetchingEnvironment -> {
			AllFieldCasesInterfaceType source = dataFetchingEnvironment.getSource();

			List<String> ret = dataFetchersDelegateAllFieldCasesInterfaceType.comments(dataFetchingEnvironment, source);
			logger.debug("comments: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	public DataFetcher<List<Boolean>> dataFetchersDelegateAllFieldCasesInterfaceTypeBooleans() {
		return dataFetchingEnvironment -> {
			AllFieldCasesInterfaceType source = dataFetchingEnvironment.getSource();

			List<Boolean> ret = dataFetchersDelegateAllFieldCasesInterfaceType.booleans(dataFetchingEnvironment,
					source);
			logger.debug("booleans: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	public DataFetcher<List<String>> dataFetchersDelegateAllFieldCasesInterfaceTypeAliases() {
		return dataFetchingEnvironment -> {
			AllFieldCasesInterfaceType source = dataFetchingEnvironment.getSource();

			List<String> ret = dataFetchersDelegateAllFieldCasesInterfaceType.aliases(dataFetchingEnvironment, source);
			logger.debug("aliases: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	public DataFetcher<List<String>> dataFetchersDelegateAllFieldCasesInterfaceTypePlanets() {
		return dataFetchingEnvironment -> {
			AllFieldCasesInterfaceType source = dataFetchingEnvironment.getSource();

			List<String> ret = dataFetchersDelegateAllFieldCasesInterfaceType.planets(dataFetchingEnvironment, source);
			logger.debug("planets: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	public DataFetcher<List<Human>> dataFetchersDelegateAllFieldCasesInterfaceTypeFriends() {
		return dataFetchingEnvironment -> {
			AllFieldCasesInterfaceType source = dataFetchingEnvironment.getSource();

			List<Human> ret = dataFetchersDelegateAllFieldCasesInterfaceType.friends(dataFetchingEnvironment, source);
			logger.debug("friends: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	public DataFetcher<AllFieldCasesWithIdSubtype> dataFetchersDelegateAllFieldCasesInterfaceTypeOneWithIdSubType() {
		return dataFetchingEnvironment -> {
			AllFieldCasesInterfaceType source = dataFetchingEnvironment.getSource();

			AllFieldCasesWithIdSubtype ret = null;
			try {
				ret = dataFetchersDelegateAllFieldCasesInterfaceType.oneWithIdSubType(dataFetchingEnvironment, source);
			} catch (NoSuchElementException e) {
				// There was no items in the Optional
			}

			if (ret != null)
				logger.debug("oneWithIdSubType: 1 result found");
			else
				logger.debug("oneWithIdSubType: no result found");

			return ret;
		};
	}

	public DataFetcher<CompletableFuture<AllFieldCasesWithIdSubtype>> dataFetchersDelegateAllFieldCasesInterfaceTypeOneWithIdSubTypeWithDataLoader() {
		return dataFetchingEnvironment -> {
			AllFieldCasesInterfaceType source = dataFetchingEnvironment.getSource();

			DataLoader<UUID, AllFieldCasesWithIdSubtype> dataLoader = dataFetchingEnvironment
					.getDataLoader("AllFieldCasesWithIdSubtype");

			// This dataLoader may be null. Let's hande that:
			if (dataLoader != null)
				return dataFetchersDelegateAllFieldCasesInterfaceType.oneWithIdSubType(dataFetchingEnvironment,
						dataLoader, source);
			else
				return CompletableFuture.supplyAsync(() -> dataFetchersDelegateAllFieldCasesInterfaceType
						.oneWithIdSubType(dataFetchingEnvironment, source));
		};
	}

	public DataFetcher<List<AllFieldCasesWithIdSubtype>> dataFetchersDelegateAllFieldCasesInterfaceTypeListWithIdSubTypes() {
		return dataFetchingEnvironment -> {
			Integer nbItems = dataFetchingEnvironment.getArgument("nbItems");
			Boolean uppercaseName = dataFetchingEnvironment.getArgument("uppercaseName");
			String textToAppendToTheForname = dataFetchingEnvironment.getArgument("textToAppendToTheForname");
			AllFieldCasesInterfaceType source = dataFetchingEnvironment.getSource();

			List<AllFieldCasesWithIdSubtype> ret = dataFetchersDelegateAllFieldCasesInterfaceType.listWithIdSubTypes(
					dataFetchingEnvironment, source, nbItems, uppercaseName, textToAppendToTheForname);
			logger.debug("listWithIdSubTypes: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	public DataFetcher<AllFieldCasesWithoutIdSubtype> dataFetchersDelegateAllFieldCasesInterfaceTypeOneWithoutIdSubType() {
		return dataFetchingEnvironment -> {
			FieldParameterInput input = (FieldParameterInput) graphqlUtils.getInputObject(
					dataFetchingEnvironment.getArgument("input"), "FieldParameterInput", FieldParameterInput.class);
			AllFieldCasesInterfaceType source = dataFetchingEnvironment.getSource();

			AllFieldCasesWithoutIdSubtype ret = null;
			try {
				ret = dataFetchersDelegateAllFieldCasesInterfaceType.oneWithoutIdSubType(dataFetchingEnvironment,
						source, input);
			} catch (NoSuchElementException e) {
				// There was no items in the Optional
			}

			if (ret != null)
				logger.debug("oneWithoutIdSubType: 1 result found");
			else
				logger.debug("oneWithoutIdSubType: no result found");

			return ret;
		};
	}

	public DataFetcher<List<AllFieldCasesWithoutIdSubtype>> dataFetchersDelegateAllFieldCasesInterfaceTypeListWithoutIdSubTypes() {
		return dataFetchingEnvironment -> {
			Integer nbItems = dataFetchingEnvironment.getArgument("nbItems");
			FieldParameterInput input = (FieldParameterInput) graphqlUtils.getInputObject(
					dataFetchingEnvironment.getArgument("input"), "FieldParameterInput", FieldParameterInput.class);
			String textToAppendToTheForname = dataFetchingEnvironment.getArgument("textToAppendToTheForname");
			AllFieldCasesInterfaceType source = dataFetchingEnvironment.getSource();

			List<AllFieldCasesWithoutIdSubtype> ret = dataFetchersDelegateAllFieldCasesInterfaceType
					.listWithoutIdSubTypes(dataFetchingEnvironment, source, nbItems, input, textToAppendToTheForname);
			logger.debug("listWithoutIdSubTypes: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	// Data fetchers for DataFetchersDelegateHuman
	////////////////////////////////////////////////////////////////////////////////////////////////

	public DataFetcher<Character> dataFetchersDelegateHumanBestFriend() {
		return dataFetchingEnvironment -> {
			Human source = dataFetchingEnvironment.getSource();

			Character ret = null;
			try {
				ret = dataFetchersDelegateHuman.bestFriend(dataFetchingEnvironment, source);
			} catch (NoSuchElementException e) {
				// There was no items in the Optional
			}

			if (ret != null)
				logger.debug("bestFriend: 1 result found");
			else
				logger.debug("bestFriend: no result found");

			return ret;
		};
	}

	public DataFetcher<CompletableFuture<Character>> dataFetchersDelegateHumanBestFriendWithDataLoader() {
		return dataFetchingEnvironment -> {
			Human source = dataFetchingEnvironment.getSource();

			DataLoader<UUID, Character> dataLoader = dataFetchingEnvironment.getDataLoader("Character");

			// This dataLoader may be null. Let's hande that:
			if (dataLoader != null)
				return dataFetchersDelegateHuman.bestFriend(dataFetchingEnvironment, dataLoader, source);
			else
				return CompletableFuture
						.supplyAsync(() -> dataFetchersDelegateHuman.bestFriend(dataFetchingEnvironment, source));
		};
	}

	public DataFetcher<List<Character>> dataFetchersDelegateHumanFriends() {
		return dataFetchingEnvironment -> {
			Human source = dataFetchingEnvironment.getSource();

			List<Character> ret = dataFetchersDelegateHuman.friends(dataFetchingEnvironment, source);
			logger.debug("friends: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	public DataFetcher<List<String>> dataFetchersDelegateHumanComments() {
		return dataFetchingEnvironment -> {
			Human source = dataFetchingEnvironment.getSource();

			List<String> ret = dataFetchersDelegateHuman.comments(dataFetchingEnvironment, source);
			logger.debug("comments: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	public DataFetcher<List<Episode>> dataFetchersDelegateHumanAppearsIn() {
		return dataFetchingEnvironment -> {
			Human source = dataFetchingEnvironment.getSource();

			List<Episode> ret = dataFetchersDelegateHuman.appearsIn(dataFetchingEnvironment, source);
			logger.debug("appearsIn: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	// Data fetchers for DataFetchersDelegateDroid
	////////////////////////////////////////////////////////////////////////////////////////////////

	public DataFetcher<List<Character>> dataFetchersDelegateDroidFriends() {
		return dataFetchingEnvironment -> {
			Droid source = dataFetchingEnvironment.getSource();

			List<Character> ret = dataFetchersDelegateDroid.friends(dataFetchingEnvironment, source);
			logger.debug("friends: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	public DataFetcher<List<Episode>> dataFetchersDelegateDroidAppearsIn() {
		return dataFetchingEnvironment -> {
			Droid source = dataFetchingEnvironment.getSource();

			List<Episode> ret = dataFetchersDelegateDroid.appearsIn(dataFetchingEnvironment, source);
			logger.debug("appearsIn: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	// Data fetchers for DataFetchersDelegateCommented
	////////////////////////////////////////////////////////////////////////////////////////////////

	public DataFetcher<List<String>> dataFetchersDelegateCommentedComments() {
		return dataFetchingEnvironment -> {
			Commented source = dataFetchingEnvironment.getSource();

			List<String> ret = dataFetchersDelegateCommented.comments(dataFetchingEnvironment, source);
			logger.debug("comments: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	// Data fetchers for DataFetchersDelegateAllFieldCasesInterface
	////////////////////////////////////////////////////////////////////////////////////////////////

	public DataFetcher<List<String>> dataFetchersDelegateAllFieldCasesInterfaceComments() {
		return dataFetchingEnvironment -> {
			AllFieldCasesInterface source = dataFetchingEnvironment.getSource();

			List<String> ret = dataFetchersDelegateAllFieldCasesInterface.comments(dataFetchingEnvironment, source);
			logger.debug("comments: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	public DataFetcher<List<Boolean>> dataFetchersDelegateAllFieldCasesInterfaceBooleans() {
		return dataFetchingEnvironment -> {
			AllFieldCasesInterface source = dataFetchingEnvironment.getSource();

			List<Boolean> ret = dataFetchersDelegateAllFieldCasesInterface.booleans(dataFetchingEnvironment, source);
			logger.debug("booleans: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	public DataFetcher<List<String>> dataFetchersDelegateAllFieldCasesInterfaceAliases() {
		return dataFetchingEnvironment -> {
			AllFieldCasesInterface source = dataFetchingEnvironment.getSource();

			List<String> ret = dataFetchersDelegateAllFieldCasesInterface.aliases(dataFetchingEnvironment, source);
			logger.debug("aliases: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	public DataFetcher<List<String>> dataFetchersDelegateAllFieldCasesInterfacePlanets() {
		return dataFetchingEnvironment -> {
			AllFieldCasesInterface source = dataFetchingEnvironment.getSource();

			List<String> ret = dataFetchersDelegateAllFieldCasesInterface.planets(dataFetchingEnvironment, source);
			logger.debug("planets: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	public DataFetcher<List<Human>> dataFetchersDelegateAllFieldCasesInterfaceFriends() {
		return dataFetchingEnvironment -> {
			AllFieldCasesInterface source = dataFetchingEnvironment.getSource();

			List<Human> ret = dataFetchersDelegateAllFieldCasesInterface.friends(dataFetchingEnvironment, source);
			logger.debug("friends: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	public DataFetcher<AllFieldCasesWithIdSubtype> dataFetchersDelegateAllFieldCasesInterfaceOneWithIdSubType() {
		return dataFetchingEnvironment -> {
			AllFieldCasesInterface source = dataFetchingEnvironment.getSource();

			AllFieldCasesWithIdSubtype ret = null;
			try {
				ret = dataFetchersDelegateAllFieldCasesInterface.oneWithIdSubType(dataFetchingEnvironment, source);
			} catch (NoSuchElementException e) {
				// There was no items in the Optional
			}

			if (ret != null)
				logger.debug("oneWithIdSubType: 1 result found");
			else
				logger.debug("oneWithIdSubType: no result found");

			return ret;
		};
	}

	public DataFetcher<CompletableFuture<AllFieldCasesWithIdSubtype>> dataFetchersDelegateAllFieldCasesInterfaceOneWithIdSubTypeWithDataLoader() {
		return dataFetchingEnvironment -> {
			AllFieldCasesInterface source = dataFetchingEnvironment.getSource();

			DataLoader<UUID, AllFieldCasesWithIdSubtype> dataLoader = dataFetchingEnvironment
					.getDataLoader("AllFieldCasesWithIdSubtype");

			// This dataLoader may be null. Let's hande that:
			if (dataLoader != null)
				return dataFetchersDelegateAllFieldCasesInterface.oneWithIdSubType(dataFetchingEnvironment, dataLoader,
						source);
			else
				return CompletableFuture.supplyAsync(() -> dataFetchersDelegateAllFieldCasesInterface
						.oneWithIdSubType(dataFetchingEnvironment, source));
		};
	}

	public DataFetcher<List<AllFieldCasesWithIdSubtype>> dataFetchersDelegateAllFieldCasesInterfaceListWithIdSubTypes() {
		return dataFetchingEnvironment -> {
			Integer nbItems = dataFetchingEnvironment.getArgument("nbItems");
			Boolean uppercaseName = dataFetchingEnvironment.getArgument("uppercaseName");
			String textToAppendToTheForname = dataFetchingEnvironment.getArgument("textToAppendToTheForname");
			AllFieldCasesInterface source = dataFetchingEnvironment.getSource();

			List<AllFieldCasesWithIdSubtype> ret = dataFetchersDelegateAllFieldCasesInterface.listWithIdSubTypes(
					dataFetchingEnvironment, source, nbItems, uppercaseName, textToAppendToTheForname);
			logger.debug("listWithIdSubTypes: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	public DataFetcher<AllFieldCasesWithoutIdSubtype> dataFetchersDelegateAllFieldCasesInterfaceOneWithoutIdSubType() {
		return dataFetchingEnvironment -> {
			FieldParameterInput input = (FieldParameterInput) graphqlUtils.getInputObject(
					dataFetchingEnvironment.getArgument("input"), "FieldParameterInput", FieldParameterInput.class);
			AllFieldCasesInterface source = dataFetchingEnvironment.getSource();

			AllFieldCasesWithoutIdSubtype ret = null;
			try {
				ret = dataFetchersDelegateAllFieldCasesInterface.oneWithoutIdSubType(dataFetchingEnvironment, source,
						input);
			} catch (NoSuchElementException e) {
				// There was no items in the Optional
			}

			if (ret != null)
				logger.debug("oneWithoutIdSubType: 1 result found");
			else
				logger.debug("oneWithoutIdSubType: no result found");

			return ret;
		};
	}

	public DataFetcher<List<AllFieldCasesWithoutIdSubtype>> dataFetchersDelegateAllFieldCasesInterfaceListWithoutIdSubTypes() {
		return dataFetchingEnvironment -> {
			Integer nbItems = dataFetchingEnvironment.getArgument("nbItems");
			FieldParameterInput input = (FieldParameterInput) graphqlUtils.getInputObject(
					dataFetchingEnvironment.getArgument("input"), "FieldParameterInput", FieldParameterInput.class);
			String textToAppendToTheForname = dataFetchingEnvironment.getArgument("textToAppendToTheForname");
			AllFieldCasesInterface source = dataFetchingEnvironment.getSource();

			List<AllFieldCasesWithoutIdSubtype> ret = dataFetchersDelegateAllFieldCasesInterface
					.listWithoutIdSubTypes(dataFetchingEnvironment, source, nbItems, input, textToAppendToTheForname);
			logger.debug("listWithoutIdSubTypes: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	// Data fetchers for DataFetchersDelegateCharacter
	////////////////////////////////////////////////////////////////////////////////////////////////

	public DataFetcher<List<Character>> dataFetchersDelegateCharacterFriends() {
		return dataFetchingEnvironment -> {
			Character source = dataFetchingEnvironment.getSource();

			List<Character> ret = dataFetchersDelegateCharacter.friends(dataFetchingEnvironment, source);
			logger.debug("friends: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	public DataFetcher<List<Episode>> dataFetchersDelegateCharacterAppearsIn() {
		return dataFetchingEnvironment -> {
			Character source = dataFetchingEnvironment.getSource();

			List<Episode> ret = dataFetchersDelegateCharacter.appearsIn(dataFetchingEnvironment, source);
			logger.debug("appearsIn: {} found rows", (ret == null) ? 0 : ret.size());

			return ret;
		};
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	// Data fetchers for DataFetchersDelegateAllFieldCasesWithIdSubtype
	////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////
	// Data fetchers for DataFetchersDelegateWithID
	////////////////////////////////////////////////////////////////////////////////////////////////
}