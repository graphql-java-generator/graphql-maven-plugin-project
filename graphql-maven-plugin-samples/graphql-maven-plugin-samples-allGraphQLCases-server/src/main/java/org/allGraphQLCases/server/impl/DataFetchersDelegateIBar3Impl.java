package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;

import org.allGraphQLCases.server.IBar3;
import org.allGraphQLCases.server.util.DataFetchersDelegateIBar3;
import org.dataloader.BatchLoaderEnvironment;
import org.springframework.stereotype.Component;

@Component
public class DataFetchersDelegateIBar3Impl implements DataFetchersDelegateIBar3 {

	@Override
	public List<IBar3> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		return null;
	}

}
