package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;

import org.allGraphQLCases.server.SIP_Node_SIS;
import org.allGraphQLCases.server.DataFetchersDelegateNode;
import org.dataloader.BatchLoaderEnvironment;
import org.springframework.stereotype.Component;

@Component
public class DataFetchersDelegateNodeImpl implements DataFetchersDelegateNode {

	@Override
	public List<SIP_Node_SIS> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		throw new RuntimeException("Not implemented yet");
	}

}
