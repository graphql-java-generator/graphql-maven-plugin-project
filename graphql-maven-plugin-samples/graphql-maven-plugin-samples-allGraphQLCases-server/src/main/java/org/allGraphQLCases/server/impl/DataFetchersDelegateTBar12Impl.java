package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;

import org.allGraphQLCases.server.TBar12;
import org.allGraphQLCases.server.util.DataFetchersDelegateTBar12;
import org.dataloader.BatchLoaderEnvironment;
import org.springframework.stereotype.Component;

@Component
public class DataFetchersDelegateTBar12Impl implements DataFetchersDelegateTBar12 {

	@Override
	public List<TBar12> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		return null;
	}

}
