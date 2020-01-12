/**
 * 
 */
package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;

import org.allGraphQLCases.server.Character;
import org.allGraphQLCases.server.DataFetchersDelegateCharacter;
import org.allGraphQLCases.server.Episode;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author EtienneSF
 *
 */
@Component
public class DataFetchersDelegateCharacterImpl implements DataFetchersDelegateCharacter {

	@Override
	public List<Character> friends(DataFetchingEnvironment dataFetchingEnvironment, Character source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Episode> appearsIn(DataFetchingEnvironment dataFetchingEnvironment, Character source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Character> batchLoader(List<UUID> keys) {
		// TODO Auto-generated method stub
		return null;
	}

}
