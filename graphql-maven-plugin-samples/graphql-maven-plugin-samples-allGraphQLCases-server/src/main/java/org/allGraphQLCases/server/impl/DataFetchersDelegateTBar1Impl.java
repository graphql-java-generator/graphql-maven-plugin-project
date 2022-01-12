package org.allGraphQLCases.server.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.allGraphQLCases.server.TBar1;
import org.allGraphQLCases.server.util.DataFetchersDelegateTBar1;
import org.dataloader.BatchLoaderEnvironment;
import org.springframework.stereotype.Component;

@Component
public class DataFetchersDelegateTBar1Impl implements DataFetchersDelegateTBar1 {

	@Override
	public List<TBar1> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		List<TBar1> ret = new ArrayList<>(keys.size());

		for (UUID key : keys) {
			ret.add(TBar1.builder().withId(key).build());
		}

		return ret;
	}

}
