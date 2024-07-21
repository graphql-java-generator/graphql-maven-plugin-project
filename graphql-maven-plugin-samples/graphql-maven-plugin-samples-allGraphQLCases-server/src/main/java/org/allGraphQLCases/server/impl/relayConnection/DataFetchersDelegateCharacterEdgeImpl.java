package org.allGraphQLCases.server.impl.relayConnection;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.allGraphQLCases.server.DataFetchersDelegateCharacterEdge;
import org.allGraphQLCases.server.SIP_CharacterEdge_SIS;
import org.allGraphQLCases.server.SIP_Character_SIS;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateCharacterEdgeImpl implements DataFetchersDelegateCharacterEdge {

	@Override
	public CompletableFuture<SIP_Character_SIS> node(DataFetchingEnvironment dataFetchingEnvironment,
			DataLoader<UUID, SIP_Character_SIS> dataLoader, SIP_CharacterEdge_SIS origin) {
		return CompletableFuture.completedFuture(origin.getNode());
	}

}
