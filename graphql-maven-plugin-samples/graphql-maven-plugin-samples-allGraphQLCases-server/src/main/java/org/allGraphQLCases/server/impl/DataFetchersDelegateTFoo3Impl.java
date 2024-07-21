package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.allGraphQLCases.server.DataFetchersDelegateTFoo3;
import org.allGraphQLCases.server.STP_TBar12_STS;
import org.allGraphQLCases.server.STP_TFoo3_STS;
import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateTFoo3Impl implements DataFetchersDelegateTFoo3 {

	@Override
	public CompletableFuture<STP_TBar12_STS> bar(DataFetchingEnvironment dataFetchingEnvironment,
			DataLoader<UUID, STP_TBar12_STS> dataLoader, STP_TFoo3_STS origin) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<STP_TFoo3_STS> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		// TODO Auto-generated method stub
		return null;
	}

}
