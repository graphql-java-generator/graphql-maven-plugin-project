package org.allGraphQLCases.server.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.allGraphQLCases.server.IBar1;
import org.allGraphQLCases.server.TBar1;
import org.allGraphQLCases.server.util.DataFetchersDelegateIBar1;
import org.dataloader.BatchLoaderEnvironment;
import org.springframework.stereotype.Component;

@Component
public class DataFetchersDelegateIBar1Impl implements DataFetchersDelegateIBar1 {

	@Override
	public List<IBar1> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		List<IBar1> ret = new ArrayList<>(keys.size());

		for (UUID key : keys) {
			ret.add(TBar1.builder().withId(key).build());
		}

		return ret;
	}

}
