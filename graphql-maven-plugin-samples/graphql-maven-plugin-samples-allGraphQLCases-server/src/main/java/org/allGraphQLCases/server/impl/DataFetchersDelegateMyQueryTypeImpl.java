package org.allGraphQLCases.server.impl;

import java.util.List;

import javax.annotation.Resource;

import org.allGraphQLCases.server.Character;
import org.allGraphQLCases.server.CharacterImpl;
import org.allGraphQLCases.server.CharacterInput;
import org.allGraphQLCases.server.DataFetchersDelegateMyQueryType;
import org.allGraphQLCases.server.Episode;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author EtienneSF
 *
 */
@Component
public class DataFetchersDelegateMyQueryTypeImpl implements DataFetchersDelegateMyQueryType {

	@Resource
	DataGenerator generator;

	@Override
	public List<Character> withoutParameters(DataFetchingEnvironment dataFetchingEnvironment) {
		return generator.generateInstanceList(Character.class, 2, 10);
	}

	@Override
	public Character withOneOptionalParam(DataFetchingEnvironment dataFetchingEnvironment, CharacterInput character) {
		return generator.generateInstance(CharacterImpl.class, 2);
	}

	@Override
	public Character withOneMandatoryParam(DataFetchingEnvironment dataFetchingEnvironment, CharacterInput character) {
		return generator.generateInstance(CharacterImpl.class, 2);
	}

	@Override
	public Character withEnum(DataFetchingEnvironment dataFetchingEnvironment, Episode episode) {
		return generator.generateInstance(CharacterImpl.class, 2);
	}

	@Override
	public List<Character> withList(DataFetchingEnvironment dataFetchingEnvironment, String name,
			List<CharacterInput> friends) {
		return generator.generateInstanceList(Character.class, 2, 10);
	}

	@Override
	public String error(DataFetchingEnvironment dataFetchingEnvironment, String errorLabel) {
		// This method is here only to test the error behavior.
		throw new RuntimeException("This is an error: " + errorLabel);
	}

}
