package org.allGraphQLCases.server.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.allGraphQLCases.server.AllFieldCasesWithIdSubtype;
import org.allGraphQLCases.server.util.DataFetchersDelegateAllFieldCasesWithIdSubtype;
import org.springframework.stereotype.Component;

@Component
public class DataFetchersDelegateAllFieldCasesWithIdSubtypeImpl
		implements DataFetchersDelegateAllFieldCasesWithIdSubtype {

	@Resource
	DataGenerator generator;

	@Override
	public List<AllFieldCasesWithIdSubtype> batchLoader(List<UUID> keys) {
		List<AllFieldCasesWithIdSubtype> list = new ArrayList<>(keys.size());

		for (UUID id : keys) {
			AllFieldCasesWithIdSubtype item = generator.generateInstance(AllFieldCasesWithIdSubtype.class);
			item.setId(id);
			list.add(item);
		}

		return list;
	}

}
