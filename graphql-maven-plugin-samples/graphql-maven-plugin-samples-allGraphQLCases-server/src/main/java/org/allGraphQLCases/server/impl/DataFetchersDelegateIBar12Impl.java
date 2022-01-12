package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;

import org.allGraphQLCases.server.IBar12;
import org.allGraphQLCases.server.util.DataFetchersDelegateIBar12;
import org.dataloader.BatchLoaderEnvironment;
import org.springframework.stereotype.Component;

@Component
public class DataFetchersDelegateIBar12Impl implements DataFetchersDelegateIBar12 {

	@Override
	public List<IBar12> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		return null;
	}

}
