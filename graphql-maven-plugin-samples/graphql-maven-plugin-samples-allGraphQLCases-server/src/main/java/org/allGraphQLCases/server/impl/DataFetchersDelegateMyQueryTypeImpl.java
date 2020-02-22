package org.allGraphQLCases.server.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.allGraphQLCases.server.AllFieldCases;
import org.allGraphQLCases.server.AllFieldCasesInput;
import org.allGraphQLCases.server.Character;
import org.allGraphQLCases.server.CharacterImpl;
import org.allGraphQLCases.server.CharacterInput;
import org.allGraphQLCases.server.DataFetchersDelegateMyQueryType;
import org.allGraphQLCases.server.Episode;
import org.allGraphQLCases.server._break;
import org.allGraphQLCases.server._extends;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.stereotype.Component;

import graphql.language.EnumValue;
import graphql.language.Field;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author EtienneSF
 *
 */
@Component
public class DataFetchersDelegateMyQueryTypeImpl implements DataFetchersDelegateMyQueryType {

	@Resource
	DataGenerator generator;

	Mapper mapper = new DozerBeanMapper();

	@Override
	public List<Character> withoutParameters(DataFetchingEnvironment dataFetchingEnvironment) {
		return generator.generateInstanceList(Character.class, 10);
	}

	@Override
	public Character withOneOptionalParam(DataFetchingEnvironment dataFetchingEnvironment, CharacterInput character) {
		if (character == null) {
			return generator.generateInstance(CharacterImpl.class);
		} else {
			Character c = mapper.map(character, CharacterImpl.class);
			c.setId(UUID.randomUUID());
			return c;
		}
	}

	@Override
	public Character withOneMandatoryParam(DataFetchingEnvironment dataFetchingEnvironment, CharacterInput character) {
		Character c = mapper.map(character, CharacterImpl.class);
		c.setId(UUID.randomUUID());
		return c;
	}

	@Override
	public Character withEnum(DataFetchingEnvironment dataFetchingEnvironment, Episode episode) {
		Character c = generator.generateInstance(CharacterImpl.class);

		// The episode list (appearsIn) will be filled by another call (the graphql manages the joins).
		// To check the given parameter, we put the episode name in the returned character's name
		c.setName(episode.name());

		return c;
	}

	@Override
	public List<Character> withList(DataFetchingEnvironment dataFetchingEnvironment, String name,
			List<CharacterInput> characters) {
		List<Character> list = new ArrayList<Character>(characters.size());
		for (CharacterInput input : characters) {
			Character c = mapper.map(input, CharacterImpl.class);
			c.setId(UUID.randomUUID());
			list.add(c);
		}

		list.get(0).setName(name);

		return list;
	}

	@Override
	public Character error(DataFetchingEnvironment dataFetchingEnvironment, String errorLabel) {
		// This method is here only to test the error behavior.
		throw new RuntimeException("This is an error: " + errorLabel);
	}

	@Override
	public AllFieldCases allFieldCases(DataFetchingEnvironment dataFetchingEnvironment, AllFieldCasesInput input) {
		AllFieldCases ret;
		if (input != null) {
			ret = mapper.map(input, AllFieldCases.class);
		} else {
			ret = generator.generateInstance(AllFieldCases.class);
		}
		return ret;
	}

	@Override
	public _break aBreak(DataFetchingEnvironment dataFetchingEnvironment) {
		_break ret = new _break();

		// Let's retrieve the input parameter test, that contains the expected value to return
		Field aBreak = (Field) dataFetchingEnvironment.getOperationDefinition().getSelectionSet().getSelections()
				.get(0);
		Field aCase = (Field) aBreak.getSelectionSet().getSelections().get(0);
		EnumValue enumValue = (EnumValue) aCase.getArguments().get(0).getValue();
		_extends value = _extends.valueOf(enumValue.getName());

		ret.setCase(value);
		return ret;
	}

}
