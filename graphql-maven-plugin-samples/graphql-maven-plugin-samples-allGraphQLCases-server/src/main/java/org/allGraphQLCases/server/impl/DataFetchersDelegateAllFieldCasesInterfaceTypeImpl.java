package org.allGraphQLCases.server.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Resource;

import org.allGraphQLCases.server.DataFetchersDelegateAllFieldCasesInterfaceType;
import org.allGraphQLCases.server.SEP_Episode_SES;
import org.allGraphQLCases.server.SINP_FieldParameterInput_SINS;
import org.allGraphQLCases.server.STP_AllFieldCasesInterfaceType_STS;
import org.allGraphQLCases.server.STP_AllFieldCasesWithIdSubtype_STS;
import org.allGraphQLCases.server.STP_AllFieldCasesWithoutIdSubtype_STS;
import org.allGraphQLCases.server.STP_HumanConnection_STS;
import org.allGraphQLCases.server.STP_HumanEdge_STS;
import org.allGraphQLCases.server.STP_Human_STS;
import org.allGraphQLCases.server.STP_PageInfo_STS;
import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateAllFieldCasesInterfaceTypeImpl
		implements DataFetchersDelegateAllFieldCasesInterfaceType {

	final String BAD_CURSOR = "TODO : implement a sample cursor capability. It's specific to each implementation";

	@Resource
	DataGenerator generator;

	@Override
	public List<String> comments(DataFetchingEnvironment dataFetchingEnvironment,
			STP_AllFieldCasesInterfaceType_STS source) {
		List<String> ret = new ArrayList<>();
		ret.add("Comment1");
		ret.add("Comment2");
		return ret;
	}

	@Override
	public List<Boolean> booleans(DataFetchingEnvironment dataFetchingEnvironment,
			STP_AllFieldCasesInterfaceType_STS source) {
		List<Boolean> ret = new ArrayList<>();
		ret.add(true);
		ret.add(false);
		return null;
	}

	@Override
	public List<String> aliases(DataFetchingEnvironment dataFetchingEnvironment,
			STP_AllFieldCasesInterfaceType_STS source) {
		List<String> ret = new ArrayList<>();
		ret.add("Alias 1");
		ret.add("Alias 2");
		return ret;
	}

	@Override
	public List<String> planets(DataFetchingEnvironment dataFetchingEnvironment,
			STP_AllFieldCasesInterfaceType_STS source) {
		List<String> ret = new ArrayList<>();
		ret.add("Planet 1");
		ret.add("Planet 2");
		return ret;
	}

	@Override
	public STP_HumanConnection_STS friends(DataFetchingEnvironment dataFetchingEnvironment,
			STP_AllFieldCasesInterfaceType_STS source) {
		STP_Human_STS human = STP_Human_STS.builder().withId(UUID.randomUUID()).withName("a name")
				.withAppearsIn(new ArrayList<SEP_Episode_SES>()).build();
		//
		STP_HumanEdge_STS edge = STP_HumanEdge_STS.builder().withNode(human).withCursor(this.BAD_CURSOR).build();
		//
		List<STP_HumanEdge_STS> edges = new ArrayList<>();
		edges.add(edge);

		STP_PageInfo_STS pageInfo = STP_PageInfo_STS.builder().withEndCursor(this.BAD_CURSOR).withHasNextPage(false)
				.withHasPreviousPage(false).withStartCursor(this.BAD_CURSOR).build();

		return STP_HumanConnection_STS.builder().withEdges(edges).withPageInfo(pageInfo).build();
	}

	@Override
	public CompletableFuture<STP_AllFieldCasesWithIdSubtype_STS> oneWithIdSubType(
			DataFetchingEnvironment dataFetchingEnvironment,
			DataLoader<UUID, STP_AllFieldCasesWithIdSubtype_STS> dataLoader,
			STP_AllFieldCasesInterfaceType_STS source) {
		return dataLoader.load(UUID.randomUUID());
	}

	@Override
	public List<STP_AllFieldCasesWithIdSubtype_STS> listWithIdSubTypes(DataFetchingEnvironment dataFetchingEnvironment,
			STP_AllFieldCasesInterfaceType_STS source, Integer nbItems, Boolean uppercaseName,
			String textToAppendToTheForname) {
		STP_AllFieldCasesWithIdSubtype_STS type = new STP_AllFieldCasesWithIdSubtype_STS();
		type.setId(UUID.randomUUID());
		type.setName("A name");

		if (textToAppendToTheForname != null)
			type.setName(type.getName() + textToAppendToTheForname);

		if (uppercaseName != null && uppercaseName) {
			type.setName(type.getName().toUpperCase());
		}

		List<STP_AllFieldCasesWithIdSubtype_STS> ret = new ArrayList<>();
		for (int i = 0; i < nbItems; i += 1) {
			ret.add(type);
		}

		return ret;
	}

	@Override
	public STP_AllFieldCasesWithoutIdSubtype_STS oneWithoutIdSubType(DataFetchingEnvironment dataFetchingEnvironment,
			STP_AllFieldCasesInterfaceType_STS source, SINP_FieldParameterInput_SINS input) {
		STP_AllFieldCasesWithoutIdSubtype_STS type = new STP_AllFieldCasesWithoutIdSubtype_STS();
		type.setName("A name");

		if (input != null && input.getUppercase() != null && input.getUppercase()) {
			type.setName(type.getName().toUpperCase());
		}

		return type;
	}

	@Override
	public List<STP_AllFieldCasesWithoutIdSubtype_STS> listWithoutIdSubTypes(
			DataFetchingEnvironment dataFetchingEnvironment, STP_AllFieldCasesInterfaceType_STS source, Integer nbItems,
			SINP_FieldParameterInput_SINS input, String textToAppendToTheForname) {
		STP_AllFieldCasesWithoutIdSubtype_STS type = new STP_AllFieldCasesWithoutIdSubtype_STS();
		type.setName("A name");

		if (textToAppendToTheForname != null)
			type.setName(type.getName() + textToAppendToTheForname);

		if (input != null && input.getUppercase() != null && input.getUppercase()) {
			type.setName(type.getName().toUpperCase());
		}

		List<STP_AllFieldCasesWithoutIdSubtype_STS> ret = new ArrayList<>();
		for (int i = 0; i < nbItems; i += 1) {
			ret.add(type);
		}

		return ret;
	}

	@Override
	public List<STP_AllFieldCasesInterfaceType_STS> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		List<STP_AllFieldCasesInterfaceType_STS> ret = new ArrayList<>();

		for (UUID key : keys) {
			STP_AllFieldCasesInterfaceType_STS item = new STP_AllFieldCasesInterfaceType_STS();
			item.setId(key);
			item.setName("Name for " + key.toString());
			item.setAge((long) (Math.random() * Long.MAX_VALUE));
			item.setAliases(new ArrayList<String>());
			item.setPlanets(new ArrayList<String>());
		}

		return ret;
	}

	/** Custom field data fetchers are available since release 2.5 */
	@Override
	public String forname(DataFetchingEnvironment dataFetchingEnvironment, STP_AllFieldCasesInterfaceType_STS origin,
			Boolean uppercase, String textToAppendToTheForname) {
		return ((uppercase != null && origin.getForname() != null && uppercase) ? origin.getForname().toUpperCase()
				: origin.getForname())//
				+ ((textToAppendToTheForname == null) ? "" : textToAppendToTheForname);
	}

}
