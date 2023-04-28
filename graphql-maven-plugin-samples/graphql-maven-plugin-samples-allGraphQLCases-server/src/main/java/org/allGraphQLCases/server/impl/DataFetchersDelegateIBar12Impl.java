package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;

import org.allGraphQLCases.server.SIP_IBar12_SIS;
import org.allGraphQLCases.server.DataFetchersDelegateIBar12;
import org.dataloader.BatchLoaderEnvironment;
import org.springframework.stereotype.Component;

@Component
public class DataFetchersDelegateIBar12Impl implements DataFetchersDelegateIBar12 {

	@Override
	public List<SIP_IBar12_SIS> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		return null;
	}

}
