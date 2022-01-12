package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.allGraphQLCases.server.TBar1;
import org.allGraphQLCases.server.TFoo1;
import org.allGraphQLCases.server.util.DataFetchersDelegateTFoo1;
import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateTFoo1Impl implements DataFetchersDelegateTFoo1 {

	@Override
	public CompletableFuture<TBar1> bar(DataFetchingEnvironment dataFetchingEnvironment,
			DataLoader<UUID, TBar1> dataLoader, TFoo1 origin) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TBar1 bar(DataFetchingEnvironment dataFetchingEnvironment, TFoo1 origin) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TFoo1> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		// TODO Auto-generated method stub
		return null;
	}

}
