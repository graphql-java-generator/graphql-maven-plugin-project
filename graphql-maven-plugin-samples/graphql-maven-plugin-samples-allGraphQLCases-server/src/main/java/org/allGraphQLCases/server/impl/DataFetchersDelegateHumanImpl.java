package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.allGraphQLCases.server.Character;
import org.allGraphQLCases.server.DataFetchersDelegateHuman;
import org.allGraphQLCases.server.Episode;
import org.allGraphQLCases.server.Human;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author EtienneSF
 *
 */
@Component
public class DataFetchersDelegateHumanImpl implements DataFetchersDelegateHuman {

	@Override
	public CompletableFuture<Character> bestFriend(DataFetchingEnvironment dataFetchingEnvironment,
			DataLoader<UUID, Character> dataLoader, Human source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Character> friends(DataFetchingEnvironment dataFetchingEnvironment, Human source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> comments(DataFetchingEnvironment dataFetchingEnvironment, Human source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Episode> appearsIn(DataFetchingEnvironment dataFetchingEnvironment, Human source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Human> batchLoader(List<UUID> keys) {
		// TODO Auto-generated method stub
		return null;
	}

}
