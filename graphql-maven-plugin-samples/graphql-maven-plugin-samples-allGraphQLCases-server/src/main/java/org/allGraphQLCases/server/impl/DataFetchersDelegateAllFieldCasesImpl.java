/**
 * 
 */
package org.allGraphQLCases.server.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Resource;

import org.allGraphQLCases.server.STP_AllFieldCases_STS;
import org.allGraphQLCases.server.SINP_AllFieldCasesInput_SINS;
import org.allGraphQLCases.server.STP_AllFieldCasesWithIdSubtype_STS;
import org.allGraphQLCases.server.STP_AllFieldCasesWithoutIdSubtype_STS;
import org.allGraphQLCases.server.SINP_FieldParameterInput_SINS;
import org.allGraphQLCases.server.STP_AllFieldCases_STS;
import org.allGraphQLCases.server.STP_Human_STS;
import org.allGraphQLCases.server.impl.DataFetchersDelegateAllFieldCasesWithIdSubtypeImpl.KeyContext;
import org.allGraphQLCases.server.util.DataFetchersDelegateAllFieldCases;
import org.dataloader.BatchLoaderEnvironment;
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
	public List<String> comments(DataFetchingEnvironment dataFetchingEnvironment, STP_AllFieldCases_STS source) {
		// If this attribute was set, let's keep its value
		if (source.getComments() != null)
			// The field has already been filled (probably from incoming argument). We'll keep it
			return source.getComments();
		else
			return generator.generateInstanceList(String.class, 10);
	}

	@Override
	public List<Boolean> booleans(DataFetchingEnvironment dataFetchingEnvironment, STP_AllFieldCases_STS source) {
		// If this attribute was set, let's keep its value
		if (source.getBooleans() != null)
			// The field has already been filled (probably from incoming argument). We'll keep it
			return source.getBooleans();
		else
			return generator.generateInstanceList(Boolean.class, 10);
	}

	@Override
	public List<String> aliases(DataFetchingEnvironment dataFetchingEnvironment, STP_AllFieldCases_STS source) {
		return generator.generateInstanceList(String.class, 10);
	}

	@Override
	public List<String> planets(DataFetchingEnvironment dataFetchingEnvironment, STP_AllFieldCases_STS source) {
		return generator.generateInstanceList(String.class, 10);
	}

	@Override
	public List<STP_Human_STS> friends(DataFetchingEnvironment dataFetchingEnvironment, STP_AllFieldCases_STS source) {
		return generator.generateInstanceList(STP_Human_STS.class, 10);
	}

	@Override
	public List<List<Double>> matrix(DataFetchingEnvironment dataFetchingEnvironment, STP_AllFieldCases_STS origin) {
		// When the request is "withListOfList", the matrix field is field from the input parameter.
		// So, if this field is non null, we let its value. Otherwise, we provide one.
		if (origin.getMatrix() != null) {
			// The field has already been filled (probably from incoming argument). We'll keep it
			return origin.getMatrix();
		} else {
			List<List<Double>> list = new ArrayList<>();
			for (int i = 0; i < 2; i += 1) {
				List<Double> sublist = new ArrayList<>();
				sublist.add(DataGenerator.RANDOM.nextDouble());
				sublist.add(DataGenerator.RANDOM.nextDouble());
				sublist.add(DataGenerator.RANDOM.nextDouble());
				list.add(sublist);
			}
			return list;
		}
	}

	@Override
	public List<STP_AllFieldCases_STS> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		return generator.generateInstanceList(STP_AllFieldCases_STS.class, keys.size());
	}

	@Override
	public CompletableFuture<STP_AllFieldCasesWithIdSubtype_STS> oneWithIdSubType(
			DataFetchingEnvironment dataFetchingEnvironment, DataLoader<UUID, STP_AllFieldCasesWithIdSubtype_STS> dataLoader,
			STP_AllFieldCases_STS source, Boolean uppercase) {
		KeyContext kc = new KeyContext();
		kc.uppercase = uppercase;
		return dataLoader.load(UUID.randomUUID(), kc);
	}

	@Override
	public STP_AllFieldCasesWithIdSubtype_STS oneWithIdSubType(DataFetchingEnvironment dataFetchingEnvironment,
			STP_AllFieldCases_STS origin, Boolean uppercase) {
		return generator.generateInstance(STP_AllFieldCasesWithIdSubtype_STS.class);
	}

	@Override
	public CompletableFuture<List<STP_AllFieldCasesWithIdSubtype_STS>> listWithIdSubTypes(
			DataFetchingEnvironment dataFetchingEnvironment, DataLoader<UUID, STP_AllFieldCasesWithIdSubtype_STS> dataLoader,
			STP_AllFieldCases_STS origin, Long nbItems, Date date, List<Date> dates, Boolean uppercaseName,
			String textToAppendToTheForname) {

		List<UUID> uuids = generator.generateInstanceList(UUID.class, nbItems.intValue());

		// We store the parameter that'll allow the datafetcher to return a STP_AllFieldCasesWithIdSubtype_STS that respects
		// what the GraphQL request expects
		List<Object> keyContexts = new ArrayList<>();
		KeyContext kc = new KeyContext();
		kc.uppercase = uppercaseName;
		kc.textToAppendToTheForname = textToAppendToTheForname;
		for (int i = 0; i < uuids.size(); i += 1) {
			keyContexts.add(kc);
		}

		return dataLoader.loadMany(uuids, keyContexts);
	}

	@Override
	public List<STP_AllFieldCasesWithIdSubtype_STS> listWithIdSubTypes(DataFetchingEnvironment dataFetchingEnvironment,
			STP_AllFieldCases_STS source, Long nbItems, Date date, List<Date> dates, Boolean uppercaseName,
			String textToAppendToTheForname) {
		List<STP_AllFieldCasesWithIdSubtype_STS> list = generator.generateInstanceList(STP_AllFieldCasesWithIdSubtype_STS.class, 3);

		for (STP_AllFieldCasesWithIdSubtype_STS item : list) {
			if (uppercaseName != null && uppercaseName) {
				item.setName(item.getName().toUpperCase());
			}
			item.setName(item.getName() + textToAppendToTheForname);
		}

		return list;
	}

	@Override
	public STP_AllFieldCasesWithoutIdSubtype_STS oneWithoutIdSubType(DataFetchingEnvironment dataFetchingEnvironment,
			STP_AllFieldCases_STS source, SINP_FieldParameterInput_SINS input) {
		if (source.getOneWithoutIdSubType() != null) {
			// The field has already been filled (probably from incoming argument). We'll keep it
			return source.getOneWithoutIdSubType();
		} else {
			STP_AllFieldCasesWithoutIdSubtype_STS ret = generator.generateInstance(STP_AllFieldCasesWithoutIdSubtype_STS.class);

			if (input != null && input.getUppercase() != null && input.getUppercase()) {
				if (ret.getName() != null) {
					ret.setName(ret.getName().toUpperCase());
				}
			}

			return ret;

		}
	}

	@Override
	public List<STP_AllFieldCasesWithoutIdSubtype_STS> listWithoutIdSubTypes(DataFetchingEnvironment dataFetchingEnvironment,
			STP_AllFieldCases_STS source, Long nbItems, SINP_FieldParameterInput_SINS input, String textToAppendToTheForname) {
		if (source.getListWithoutIdSubTypes() != null) {
			// The field has already been filled (probably from incoming argument). We'll keep it
			return source.getListWithoutIdSubTypes();
		} else {
			List<STP_AllFieldCasesWithoutIdSubtype_STS> list = generator
					.generateInstanceList(STP_AllFieldCasesWithoutIdSubtype_STS.class, nbItems.intValue());

			for (STP_AllFieldCasesWithoutIdSubtype_STS item : list) {
				if (input != null && input.getUppercase() != null && input.getUppercase()) {
					item.setName(item.getName().toUpperCase());
				}
				item.setName(item.getName() + textToAppendToTheForname);
			}

			return list;
		}
	}

	@Override
	public List<Date> dates(DataFetchingEnvironment dataFetchingEnvironment, STP_AllFieldCases_STS source) {
		// If this attribute was set, let's keep its value
		if (source.getDates() != null)
			return source.getDates();
		else
			return generator.generateInstanceList(Date.class, 5);
	}

	@Override
	public List<STP_AllFieldCasesWithoutIdSubtype_STS> issue65(DataFetchingEnvironment dataFetchingEnvironment,
			STP_AllFieldCases_STS origin, List<SINP_FieldParameterInput_SINS> inputs) {

		List<STP_AllFieldCasesWithoutIdSubtype_STS> ret = generator.generateInstanceList(STP_AllFieldCasesWithoutIdSubtype_STS.class,
				inputs.size());

		// Let's put in uppercase the name, for items in the return list that match the inputs that have uppercase set
		// to true
		for (int i = 0; i < inputs.size(); i += 1) {
			STP_AllFieldCasesWithoutIdSubtype_STS item = ret.get(i);
			if (inputs.get(i).getUppercase()) {
				item.setName(item.getName().toUpperCase());
			}
		}
		return ret;
	}

	@Override
	public CompletableFuture<STP_AllFieldCases_STS> issue66(DataFetchingEnvironment dataFetchingEnvironment,
			DataLoader<UUID, STP_AllFieldCases_STS> dataLoader, STP_AllFieldCases_STS origin, List<SINP_AllFieldCasesInput_SINS> input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public STP_AllFieldCases_STS issue66(DataFetchingEnvironment dataFetchingEnvironment, STP_AllFieldCases_STS origin,
			List<SINP_AllFieldCasesInput_SINS> input) {
		// TODO Auto-generated method stub
		return null;
	}

}
