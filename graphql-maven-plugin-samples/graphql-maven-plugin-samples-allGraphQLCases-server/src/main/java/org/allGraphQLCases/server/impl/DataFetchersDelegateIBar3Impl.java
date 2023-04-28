package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;

import org.allGraphQLCases.server.SIP_IBar3_SIS;
import org.allGraphQLCases.server.DataFetchersDelegateIBar3;
import org.dataloader.BatchLoaderEnvironment;
import org.springframework.stereotype.Component;

@Component
public class DataFetchersDelegateIBar3Impl implements DataFetchersDelegateIBar3 {

	@Override
	public List<SIP_IBar3_SIS> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		return null;
	}

}
