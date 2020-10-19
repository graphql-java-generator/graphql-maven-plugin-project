package org.allGraphQLCases.server.impl.relayConnection;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.allGraphQLCases.server.Character;
import org.allGraphQLCases.server.CharacterEdge;
import org.allGraphQLCases.server.util.DataFetchersDelegateCharacterEdge;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateCharacterEdgeImpl implements DataFetchersDelegateCharacterEdge {

	@Override
	public Character node(DataFetchingEnvironment dataFetchingEnvironment, CharacterEdge origin) {
		return origin.getNode();
	}

	@Override
	public CompletableFuture<Character> node(DataFetchingEnvironment dataFetchingEnvironment,
			DataLoader<UUID, Character> dataLoader, CharacterEdge origin) {
		return CompletableFuture.completedFuture(origin.getNode());
	}

}
