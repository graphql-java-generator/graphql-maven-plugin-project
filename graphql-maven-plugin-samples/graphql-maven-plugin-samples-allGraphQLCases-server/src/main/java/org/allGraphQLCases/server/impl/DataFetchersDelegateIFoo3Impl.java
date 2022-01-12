package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.allGraphQLCases.server.IBar12;
import org.allGraphQLCases.server.IFoo3;
import org.allGraphQLCases.server.util.DataFetchersDelegateIFoo3;
import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateIFoo3Impl implements DataFetchersDelegateIFoo3 {

	@Override
	public CompletableFuture<IBar12> bar(DataFetchingEnvironment dataFetchingEnvironment,
			DataLoader<UUID, IBar12> dataLoader, IFoo3 origin) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBar12 bar(DataFetchingEnvironment dataFetchingEnvironment, IFoo3 origin) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IFoo3> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		// TODO Auto-generated method stub
		return null;
	}

}
