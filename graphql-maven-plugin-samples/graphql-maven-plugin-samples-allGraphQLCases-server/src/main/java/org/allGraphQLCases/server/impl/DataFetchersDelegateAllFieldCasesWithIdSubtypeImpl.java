package org.allGraphQLCases.server.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.allGraphQLCases.server.DataFetchersDelegateAllFieldCasesWithIdSubtype;
import org.allGraphQLCases.server.STP_AllFieldCasesWithIdSubtype_STS;
import org.dataloader.BatchLoaderEnvironment;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;
import jakarta.annotation.Resource;

@Component
public class DataFetchersDelegateAllFieldCasesWithIdSubtypeImpl
		implements DataFetchersDelegateAllFieldCasesWithIdSubtype {

	@Resource
	DataGenerator generator;

	/**
	 * This class contains the context that allows to precise what should be done on the returned
	 * STP_AllFieldCasesWithIdSubtype_STS instance
	 */
	public static class KeyContext {
		public Boolean uppercase;
		public String textToAppendToTheName;
		public Date date;
		public List<Date> dates;
	}

	@Override
	public List<STP_AllFieldCasesWithIdSubtype_STS> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		List<STP_AllFieldCasesWithIdSubtype_STS> list = new ArrayList<>(keys.size());

		for (UUID id : keys) {
			STP_AllFieldCasesWithIdSubtype_STS item = generator
					.generateInstance(STP_AllFieldCasesWithIdSubtype_STS.class);
			item.setId(id);

			Object context = environment.getKeyContexts().get(id);
			if (context != null) {

				if (!(context instanceof KeyContext)) {
					throw new RuntimeException(
							"The DataFetchersDelegateAllFieldCasesWithIdSubtypeImpl's batchLoader received a bad context: it's an instance of "
									+ context.getClass().getName() + ", but the expected class is "
									+ KeyContext.class.getName());
				}

				KeyContext keyContext = (KeyContext) context;

				// Let's manage the KeyContext parameter, that was associated with this key
				if (keyContext.uppercase != null && keyContext.uppercase) {
					item.setName(item.getName().toUpperCase());
				}
				if (keyContext.textToAppendToTheName != null) {
					item.setName(item.getName() + keyContext.textToAppendToTheName);
				}
				if (keyContext.date != null) {
					item.setDate(keyContext.date);
				}
				if (keyContext.dates != null) {
					item.setDates(keyContext.dates);
				}
			}

			list.add(item);
		}

		return list;
	}

	@Override
	public Object dates(DataFetchingEnvironment dataFetchingEnvironment, STP_AllFieldCasesWithIdSubtype_STS origin) {
		return origin.getDates();
	}

}
