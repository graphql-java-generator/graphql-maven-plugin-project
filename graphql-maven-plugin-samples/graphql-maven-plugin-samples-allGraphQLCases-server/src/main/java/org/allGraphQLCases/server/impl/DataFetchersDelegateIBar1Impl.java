package org.allGraphQLCases.server.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.allGraphQLCases.server.SIP_IBar1_SIS;
import org.allGraphQLCases.server.STP_TBar1_STS;
import org.allGraphQLCases.server.util.DataFetchersDelegateIBar1;
import org.dataloader.BatchLoaderEnvironment;
import org.springframework.stereotype.Component;

@Component
public class DataFetchersDelegateIBar1Impl implements DataFetchersDelegateIBar1 {

	@Override
	public List<SIP_IBar1_SIS> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		List<SIP_IBar1_SIS> ret = new ArrayList<>(keys.size());

		for (UUID key : keys) {
			ret.add(STP_TBar1_STS.builder().withId(key).build());
		}

		return ret;
	}

}
