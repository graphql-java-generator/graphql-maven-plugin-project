package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.allGraphQLCases.server.IBar1;
import org.allGraphQLCases.server.IFoo1;
import org.allGraphQLCases.server.util.DataFetchersDelegateIFoo1;
import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateIFoo1Impl implements DataFetchersDelegateIFoo1 {

	@Override
	public CompletableFuture<IBar1> bar(DataFetchingEnvironment dataFetchingEnvironment,
			DataLoader<UUID, IBar1> dataLoader, IFoo1 origin) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBar1 bar(DataFetchingEnvironment dataFetchingEnvironment, IFoo1 origin) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IFoo1> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		// TODO Auto-generated method stub
		return null;
	}

}
