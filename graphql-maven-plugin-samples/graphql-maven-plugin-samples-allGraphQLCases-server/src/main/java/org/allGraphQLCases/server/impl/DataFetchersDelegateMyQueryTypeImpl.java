package org.allGraphQLCases.server.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.allGraphQLCases.server.Character;
import org.allGraphQLCases.server.CharacterImpl;
import org.allGraphQLCases.server.CharacterInput;
import org.allGraphQLCases.server.DataFetchersDelegateMyQueryType;
import org.allGraphQLCases.server.Episode;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
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

}
