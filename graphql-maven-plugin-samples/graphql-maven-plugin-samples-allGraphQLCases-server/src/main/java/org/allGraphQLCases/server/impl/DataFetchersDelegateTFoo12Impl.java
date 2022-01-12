package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.allGraphQLCases.server.TBar12;
import org.allGraphQLCases.server.TFoo12;
import org.allGraphQLCases.server.util.DataFetchersDelegateTFoo12;
import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateTFoo12Impl implements DataFetchersDelegateTFoo12 {

	@Override
	public CompletableFuture<TBar12> bar(DataFetchingEnvironment dataFetchingEnvironment,
			DataLoader<UUID, TBar12> dataLoader, TFoo12 origin) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TBar12 bar(DataFetchingEnvironment dataFetchingEnvironment, TFoo12 origin) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TFoo12> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		// TODO Auto-generated method stub
		return null;
	}

}
