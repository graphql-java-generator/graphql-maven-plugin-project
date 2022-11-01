package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;

import org.allGraphQLCases.server.STP_TBar2_STS;
import org.allGraphQLCases.server.util.DataFetchersDelegateTBar2;
import org.dataloader.BatchLoaderEnvironment;
import org.springframework.stereotype.Component;

@Component
public class DataFetchersDelegateTBar2Impl implements DataFetchersDelegateTBar2 {

	@Override
	public List<STP_TBar2_STS> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		return null;
	}

}
