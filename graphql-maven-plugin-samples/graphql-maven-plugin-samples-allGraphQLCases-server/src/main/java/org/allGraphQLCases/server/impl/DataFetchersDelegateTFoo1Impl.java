package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.allGraphQLCases.server.STP_TBar1_STS;
import org.allGraphQLCases.server.STP_TFoo1_STS;
import org.allGraphQLCases.server.DataFetchersDelegateTFoo1;
import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateTFoo1Impl implements DataFetchersDelegateTFoo1 {

	@Override
	public CompletableFuture<STP_TBar1_STS> bar(DataFetchingEnvironment dataFetchingEnvironment,
			DataLoader<UUID, STP_TBar1_STS> dataLoader, STP_TFoo1_STS origin) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public STP_TBar1_STS bar(DataFetchingEnvironment dataFetchingEnvironment, STP_TFoo1_STS origin) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<STP_TFoo1_STS> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		// TODO Auto-generated method stub
		return null;
	}

}
