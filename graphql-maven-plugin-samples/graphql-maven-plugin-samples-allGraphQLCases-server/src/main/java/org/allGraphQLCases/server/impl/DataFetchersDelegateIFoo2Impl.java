package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.allGraphQLCases.server.IBar2;
import org.allGraphQLCases.server.IFoo2;
import org.allGraphQLCases.server.util.DataFetchersDelegateIFoo2;
import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateIFoo2Impl implements DataFetchersDelegateIFoo2 {

	@Override
	public CompletableFuture<IBar2> bar(DataFetchingEnvironment dataFetchingEnvironment,
			DataLoader<UUID, IBar2> dataLoader, IFoo2 origin) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBar2 bar(DataFetchingEnvironment dataFetchingEnvironment, IFoo2 origin) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IFoo2> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		// TODO Auto-generated method stub
		return null;
	}

}
