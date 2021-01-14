package org.allGraphQLCases.server.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.allGraphQLCases.server.AllFieldCasesWithIdSubtype;
import org.allGraphQLCases.server.util.DataFetchersDelegateAllFieldCasesWithIdSubtype;
import org.dataloader.BatchLoaderEnvironment;
import org.springframework.stereotype.Component;

@Component
public class DataFetchersDelegateAllFieldCasesWithIdSubtypeImpl
		implements DataFetchersDelegateAllFieldCasesWithIdSubtype {

	@Resource
	DataGenerator generator;

	@Override
	public List<AllFieldCasesWithIdSubtype> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		List<AllFieldCasesWithIdSubtype> list = new ArrayList<>(keys.size());

		for (UUID id : keys) {
			AllFieldCasesWithIdSubtype item = generator.generateInstance(AllFieldCasesWithIdSubtype.class);
			item.setId(id);

			// Let's manage the uppercase parameter, that was associated with this key
			Boolean uppercase = (Boolean) environment.getKeyContexts().get(id);
			if (uppercase != null && uppercase) {
				item.setName(item.getName().toUpperCase());
			}

			list.add(item);
		}

		return list;
	}

}
