/**
 * 
 */
package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

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

	@Resource
	DataGenerator generator;

	@Override
	public List<Character> friends(DataFetchingEnvironment dataFetchingEnvironment, Droid source) {
		return generator.generateInstanceList(Character.class, 3, 30);
	}

	@Override
	public List<Episode> appearsIn(DataFetchingEnvironment dataFetchingEnvironment, Droid source) {
		return generator.generateInstanceList(Episode.class, 3, 30);
	}

	@Override
	public List<Droid> batchLoader(List<UUID> keys) {
		return generator.generateInstanceList(Droid.class, 3, keys.size());
	}

}
