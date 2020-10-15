package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;

import org.allGraphQLCases.server.DataFetchersDelegateNode;
import org.allGraphQLCases.server.Node;
import org.springframework.stereotype.Component;

@Component
public class DataFetchersDelegateNodeImpl implements DataFetchersDelegateNode {

	@Override
	public List<Node> batchLoader(List<UUID> keys) {
		throw new RuntimeException("Not implemented yet");
	}

}
