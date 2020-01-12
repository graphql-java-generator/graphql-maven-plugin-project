/**
 * 
 */
package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;

import org.allGraphQLCases.server.Character;
import org.allGraphQLCases.server.DataFetchersDelegateDroid;
import org.allGraphQLCases.server.Droid;
import org.allGraphQLCases.server.Episode;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author EtienneSF
 *
 */
@Component
public class DataFetchersDelegateDroidImpl implements DataFetchersDelegateDroid {

	@Override
	public List<Character> friends(DataFetchingEnvironment dataFetchingEnvironment, Droid source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Episode> appearsIn(DataFetchingEnvironment dataFetchingEnvironment, Droid source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Droid> batchLoader(List<UUID> keys) {
		// TODO Auto-generated method stub
		return null;
	}

}
