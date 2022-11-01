package org.allGraphQLCases.server.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.allGraphQLCases.server.STP_TBar1_STS;
import org.allGraphQLCases.server.util.DataFetchersDelegateTBar1;
import org.dataloader.BatchLoaderEnvironment;
import org.springframework.stereotype.Component;

@Component
public class DataFetchersDelegateTBar1Impl implements DataFetchersDelegateTBar1 {

	@Override
	public List<STP_TBar1_STS> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		List<STP_TBar1_STS> ret = new ArrayList<>(keys.size());

		for (UUID key : keys) {
			ret.add(STP_TBar1_STS.builder().withId(key).build());
		}

		return ret;
	}

}
