package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;

import org.allGraphQLCases.server.TBar2;
import org.allGraphQLCases.server.util.DataFetchersDelegateTBar2;
import org.dataloader.BatchLoaderEnvironment;
import org.springframework.stereotype.Component;

@Component
public class DataFetchersDelegateTBar2Impl implements DataFetchersDelegateTBar2 {

	@Override
	public List<TBar2> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		return null;
	}

}
