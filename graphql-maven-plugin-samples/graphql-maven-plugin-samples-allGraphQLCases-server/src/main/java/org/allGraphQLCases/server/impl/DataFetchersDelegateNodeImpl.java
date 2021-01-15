package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;

import org.allGraphQLCases.server.Node;
import org.allGraphQLCases.server.util.DataFetchersDelegateNode;
import org.dataloader.BatchLoaderEnvironment;
import org.springframework.stereotype.Component;

@Component
public class DataFetchersDelegateNodeImpl implements DataFetchersDelegateNode {

	@Override
	public List<Node> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		throw new RuntimeException("Not implemented yet");
	}

}
