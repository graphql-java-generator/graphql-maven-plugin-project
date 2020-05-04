/**
 * 
 */
package org.allGraphQLCases.server.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Resource;

import org.allGraphQLCases.server.AllFieldCases;
import org.allGraphQLCases.server.AllFieldCasesWithIdSubtype;
import org.allGraphQLCases.server.AllFieldCasesWithoutIdSubtype;
import org.allGraphQLCases.server.DataFetchersDelegateAllFieldCases;
import org.allGraphQLCases.server.FieldParameterInput;
import org.allGraphQLCases.server.Human;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author etienne-sf
 *
 */
@Component
public class DataFetchersDelegateAllFieldCasesImpl implements DataFetchersDelegateAllFieldCases {

	@Resource
	DataGenerator generator;

	@Override
	public List<String> comments(DataFetchingEnvironment dataFetchingEnvironment, AllFieldCases source) {
		return generator.generateInstanceList(String.class, 10);
	}

	@Override
	public List<Boolean> booleans(DataFetchingEnvironment dataFetchingEnvironment, AllFieldCases source) {
		return generator.generateInstanceList(Boolean.class, 10);
	}

	@Override
	public List<String> aliases(DataFetchingEnvironment dataFetchingEnvironment, AllFieldCases source) {
		return generator.generateInstanceList(String.class, 10);
	}

	@Override
	public List<String> planets(DataFetchingEnvironment dataFetchingEnvironment, AllFieldCases source) {
		return generator.generateInstanceList(String.class, 10);
	}

	@Override
	public List<Human> friends(DataFetchingEnvironment dataFetchingEnvironment, AllFieldCases source) {
		return generator.generateInstanceList(Human.class, 10);
	}

	@Override
	public List<AllFieldCases> batchLoader(List<UUID> keys) {
		return generator.generateInstanceList(AllFieldCases.class, keys.size());
	}

	@Override
	public CompletableFuture<AllFieldCasesWithIdSubtype> oneWithIdSubType(
			DataFetchingEnvironment dataFetchingEnvironment, DataLoader<UUID, AllFieldCasesWithIdSubtype> dataLoader,
			AllFieldCases source) {
		return dataLoader.load(UUID.randomUUID());
	}

	@Override
	public AllFieldCasesWithIdSubtype oneWithIdSubType(DataFetchingEnvironment dataFetchingEnvironment,
			AllFieldCases origin) {
		return generator.generateInstance(AllFieldCasesWithIdSubtype.class);
	}

	@Override
	public List<AllFieldCasesWithIdSubtype> listWithIdSubTypes(DataFetchingEnvironment dataFetchingEnvironment,
			AllFieldCases source, Long nbItems, Date date, List<Date> dates, Boolean uppercaseName,
			String textToAppendToTheForname) {
		List<AllFieldCasesWithIdSubtype> list = generator.generateInstanceList(AllFieldCasesWithIdSubtype.class, 3);

		for (AllFieldCasesWithIdSubtype item : list) {
			if (uppercaseName != null && uppercaseName) {
				item.setName(item.getName().toUpperCase());
			}
			item.setName(item.getName() + textToAppendToTheForname);
		}

		return list;
	}

	@Override
	public AllFieldCasesWithoutIdSubtype oneWithoutIdSubType(DataFetchingEnvironment dataFetchingEnvironment,
			AllFieldCases source, FieldParameterInput input) {
		AllFieldCasesWithoutIdSubtype ret = generator.generateInstance(AllFieldCasesWithoutIdSubtype.class);

		if (input != null && input.getUppercase() != null && input.getUppercase()) {
			if (ret.getName() != null) {
				ret.setName(ret.getName().toUpperCase());
			}
		}

		return ret;
	}

	@Override
	public List<AllFieldCasesWithoutIdSubtype> listWithoutIdSubTypes(DataFetchingEnvironment dataFetchingEnvironment,
			AllFieldCases source, Long nbItems, FieldParameterInput input, String textToAppendToTheForname) {
		List<AllFieldCasesWithoutIdSubtype> list = generator.generateInstanceList(AllFieldCasesWithoutIdSubtype.class,
				nbItems.intValue());

		for (AllFieldCasesWithoutIdSubtype item : list) {
			if (input != null && input.getUppercase() != null && input.getUppercase()) {
				item.setName(item.getName().toUpperCase());
			}
			item.setName(item.getName() + textToAppendToTheForname);
		}

		return list;
	}

	@Override
	public List<Date> dates(DataFetchingEnvironment dataFetchingEnvironment, AllFieldCases source) {
		return generator.generateInstanceList(Date.class, 5);
	}

}
