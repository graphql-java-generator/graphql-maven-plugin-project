package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.allGraphQLCases.server.SIP_IBar1_SIS;
import org.allGraphQLCases.server.SIP_IFoo1_SIS;
import org.allGraphQLCases.server.util.DataFetchersDelegateIFoo1;
import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateIFoo1Impl implements DataFetchersDelegateIFoo1 {

	@Override
	public CompletableFuture<SIP_IBar1_SIS> bar(DataFetchingEnvironment dataFetchingEnvironment,
			DataLoader<UUID, SIP_IBar1_SIS> dataLoader, SIP_IFoo1_SIS origin) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SIP_IBar1_SIS bar(DataFetchingEnvironment dataFetchingEnvironment, SIP_IFoo1_SIS origin) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SIP_IFoo1_SIS> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		// TODO Auto-generated method stub
		return null;
	}

}
