package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;

import org.allGraphQLCases.server.SIP_IBar2_SIS;
import org.allGraphQLCases.server.util.DataFetchersDelegateIBar2;
import org.dataloader.BatchLoaderEnvironment;
import org.springframework.stereotype.Component;

@Component
public class DataFetchersDelegateIBar2Impl implements DataFetchersDelegateIBar2 {

	@Override
	public List<SIP_IBar2_SIS> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		return null;
	}

}
