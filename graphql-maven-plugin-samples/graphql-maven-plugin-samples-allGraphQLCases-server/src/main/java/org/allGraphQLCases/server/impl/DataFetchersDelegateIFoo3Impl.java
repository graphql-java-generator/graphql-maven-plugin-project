package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.allGraphQLCases.server.DataFetchersDelegateIFoo3;
import org.allGraphQLCases.server.SIP_IBar12_SIS;
import org.allGraphQLCases.server.SIP_IFoo3_SIS;
import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateIFoo3Impl implements DataFetchersDelegateIFoo3 {

	@Override
	public CompletableFuture<SIP_IBar12_SIS> bar(DataFetchingEnvironment dataFetchingEnvironment,
			DataLoader<UUID, SIP_IBar12_SIS> dataLoader, SIP_IFoo3_SIS origin) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SIP_IFoo3_SIS> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		// TODO Auto-generated method stub
		return null;
	}

}
