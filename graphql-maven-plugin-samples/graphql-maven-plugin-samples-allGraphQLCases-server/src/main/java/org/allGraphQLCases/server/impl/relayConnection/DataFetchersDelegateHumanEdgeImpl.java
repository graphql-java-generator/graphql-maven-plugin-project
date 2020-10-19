package org.allGraphQLCases.server.impl.relayConnection;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.allGraphQLCases.server.Human;
import org.allGraphQLCases.server.HumanEdge;
import org.allGraphQLCases.server.util.DataFetchersDelegateHumanEdge;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateHumanEdgeImpl implements DataFetchersDelegateHumanEdge {

	@Override
	public Human node(DataFetchingEnvironment dataFetchingEnvironment, HumanEdge origin) {
		return origin.getNode();
	}

	@Override
	public CompletableFuture<Human> node(DataFetchingEnvironment dataFetchingEnvironment,
			DataLoader<UUID, Human> dataLoader, HumanEdge origin) {
		// This works with this simple sample. For real case, it would be better to use the CompletableFuture, to
		// execute the request asynchronously
		return CompletableFuture.completedFuture(origin.getNode());
	}

}
