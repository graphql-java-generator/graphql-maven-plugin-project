package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;

import org.allGraphQLCases.server.STP_TBar12_STS;
import org.allGraphQLCases.server.DataFetchersDelegateTBar12;
import org.dataloader.BatchLoaderEnvironment;
import org.springframework.stereotype.Component;

@Component
public class DataFetchersDelegateTBar12Impl implements DataFetchersDelegateTBar12 {

	@Override
	public List<STP_TBar12_STS> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		return null;
	}

}
