package org.allGraphQLCases.server.impl.relayConnection;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.allGraphQLCases.server.DataFetchersDelegateHumanEdge;
import org.allGraphQLCases.server.STP_HumanEdge_STS;
import org.allGraphQLCases.server.STP_Human_STS;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateHumanEdgeImpl implements DataFetchersDelegateHumanEdge {

	@Override
	public CompletableFuture<STP_Human_STS> node(DataFetchingEnvironment dataFetchingEnvironment,
			DataLoader<UUID, STP_Human_STS> dataLoader, STP_HumanEdge_STS origin) {
		// This works with this simple sample. For real case, it would be better to use the CompletableFuture, to
		// execute the request asynchronously
		return CompletableFuture.completedFuture(origin.getNode());
	}

}
