/**
 * 
 */
package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.allGraphQLCases.server.Character;
import org.allGraphQLCases.server.Episode;
import org.allGraphQLCases.server.util.DataFetchersDelegateCharacter;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author etienne-sf
 *
 */
@Component
public class DataFetchersDelegateCharacterImpl implements DataFetchersDelegateCharacter {

	@Resource
	DataGenerator generator;

	@Override
	public List<Character> friends(DataFetchingEnvironment dataFetchingEnvironment, Character source) {
		return generator.generateInstanceList(Character.class, 4);
	}

	@Override
	public List<Episode> appearsIn(DataFetchingEnvironment dataFetchingEnvironment, Character source) {
		return generator.generateInstanceList(Episode.class, 2);
	}

	@Override
	public List<Character> batchLoader(List<UUID> keys) {
		return generator.generateInstanceList(Character.class, keys.size());
	}

}
