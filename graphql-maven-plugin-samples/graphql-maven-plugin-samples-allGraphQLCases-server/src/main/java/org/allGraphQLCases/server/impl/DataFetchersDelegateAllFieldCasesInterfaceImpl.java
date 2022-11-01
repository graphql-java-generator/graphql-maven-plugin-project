package org.allGraphQLCases.server.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Resource;

import org.allGraphQLCases.server.SIP_AllFieldCasesInterface_SIS;
import org.allGraphQLCases.server.STP_AllFieldCasesInterfaceType_STS;
import org.allGraphQLCases.server.STP_AllFieldCasesWithIdSubtype_STS;
import org.allGraphQLCases.server.STP_AllFieldCasesWithoutIdSubtype_STS;
import org.allGraphQLCases.server.SEP_Episode_SES;
import org.allGraphQLCases.server.SINP_FieldParameterInput_SINS;
import org.allGraphQLCases.server.STP_Human_STS;
import org.allGraphQLCases.server.STP_HumanConnection_STS;
import org.allGraphQLCases.server.STP_HumanEdge_STS;
import org.allGraphQLCases.server.STP_PageInfo_STS;
import org.allGraphQLCases.server.util.DataFetchersDelegateAllFieldCasesInterface;
import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateAllFieldCasesInterfaceImpl implements DataFetchersDelegateAllFieldCasesInterface {

	final String BAD_CURSOR = "TODO : implement a sample cursor capability. It's specific to each implementation";

	@Resource
	DataGenerator generator;

	@Override
	public List<String> comments(DataFetchingEnvironment dataFetchingEnvironment, SIP_AllFieldCasesInterface_SIS source) {
		List<String> ret = new ArrayList<>();
		ret.add("Comment1");
		ret.add("Comment2");
		return ret;
	}

	@Override
	public List<Boolean> booleans(DataFetchingEnvironment dataFetchingEnvironment, SIP_AllFieldCasesInterface_SIS source) {
		List<Boolean> ret = new ArrayList<>();
		ret.add(true);
		ret.add(false);
		return null;
	}

	@Override
	public List<String> aliases(DataFetchingEnvironment dataFetchingEnvironment, SIP_AllFieldCasesInterface_SIS source) {
		List<String> ret = new ArrayList<>();
		ret.add("Alias 1");
		ret.add("Alias 2");
		return ret;
	}

	@Override
	public List<String> planets(DataFetchingEnvironment dataFetchingEnvironment, SIP_AllFieldCasesInterface_SIS source) {
		List<String> ret = new ArrayList<>();
		ret.add("Planet 1");
		ret.add("Planet 2");
		return ret;
	}

	@Override
	public STP_HumanConnection_STS friends(DataFetchingEnvironment dataFetchingEnvironment, SIP_AllFieldCasesInterface_SIS source) {
		STP_Human_STS human = STP_Human_STS.builder().withId(UUID.randomUUID()).withName("a name")
				.withAppearsIn(new ArrayList<SEP_Episode_SES>()).build();
		//
		STP_HumanEdge_STS edge = STP_HumanEdge_STS.builder().withNode(human).withCursor(BAD_CURSOR).build();
		//
		List<STP_HumanEdge_STS> edges = new ArrayList<>();
		edges.add(edge);

		STP_PageInfo_STS pageInfo = STP_PageInfo_STS.builder().withEndCursor(BAD_CURSOR).withHasNextPage(false)
				.withHasPreviousPage(false).withStartCursor(BAD_CURSOR).build();

		return STP_HumanConnection_STS.builder().withEdges(edges).withPageInfo(pageInfo).build();
	}

	@Override
	public CompletableFuture<STP_AllFieldCasesWithIdSubtype_STS> oneWithIdSubType(
			DataFetchingEnvironment dataFetchingEnvironment, DataLoader<UUID, STP_AllFieldCasesWithIdSubtype_STS> dataLoader,
			SIP_AllFieldCasesInterface_SIS source) {
		return dataLoader.load(UUID.randomUUID());
	}

	@Override
	public STP_AllFieldCasesWithIdSubtype_STS oneWithIdSubType(DataFetchingEnvironment dataFetchingEnvironment,
			SIP_AllFieldCasesInterface_SIS origin) {
		return generator.generateInstance(STP_AllFieldCasesWithIdSubtype_STS.class);
	}

	@Override
	public List<STP_AllFieldCasesWithIdSubtype_STS> listWithIdSubTypes(DataFetchingEnvironment dataFetchingEnvironment,
			SIP_AllFieldCasesInterface_SIS source, Integer nbItems, Boolean uppercaseName, String textToAppendToTheForname) {
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
			SIP_AllFieldCasesInterface_SIS source, SINP_FieldParameterInput_SINS input) {
		STP_AllFieldCasesWithoutIdSubtype_STS type = new STP_AllFieldCasesWithoutIdSubtype_STS();
		type.setName("A name");

		if (input != null && input.getUppercase() != null && input.getUppercase()) {
			type.setName(type.getName().toUpperCase());
		}

		return type;
	}

	@Override
	public List<STP_AllFieldCasesWithoutIdSubtype_STS> listWithoutIdSubTypes(DataFetchingEnvironment dataFetchingEnvironment,
			SIP_AllFieldCasesInterface_SIS source, Integer nbItems, SINP_FieldParameterInput_SINS input,
			String textToAppendToTheForname) {
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
	public List<org.allGraphQLCases.server.SIP_AllFieldCasesInterface_SIS> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		List<SIP_AllFieldCasesInterface_SIS> ret = new ArrayList<>();

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

}
