package org.allGraphQLCases.server.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Resource;

import org.allGraphQLCases.server.AllFieldCasesInterfaceType;
import org.allGraphQLCases.server.AllFieldCasesWithIdSubtype;
import org.allGraphQLCases.server.AllFieldCasesWithoutIdSubtype;
import org.allGraphQLCases.server.Episode;
import org.allGraphQLCases.server.FieldParameterInput;
import org.allGraphQLCases.server.Human;
import org.allGraphQLCases.server.HumanConnection;
import org.allGraphQLCases.server.HumanEdge;
import org.allGraphQLCases.server.PageInfo;
import org.allGraphQLCases.server.util.DataFetchersDelegateAllFieldCasesInterfaceType;
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
	public List<String> comments(DataFetchingEnvironment dataFetchingEnvironment, AllFieldCasesInterfaceType source) {
		List<String> ret = new ArrayList<>();
		ret.add("Comment1");
		ret.add("Comment2");
		return ret;
	}

	@Override
	public List<Boolean> booleans(DataFetchingEnvironment dataFetchingEnvironment, AllFieldCasesInterfaceType source) {
		List<Boolean> ret = new ArrayList<>();
		ret.add(true);
		ret.add(false);
		return null;
	}

	@Override
	public List<String> aliases(DataFetchingEnvironment dataFetchingEnvironment, AllFieldCasesInterfaceType source) {
		List<String> ret = new ArrayList<>();
		ret.add("Alias 1");
		ret.add("Alias 2");
		return ret;
	}

	@Override
	public List<String> planets(DataFetchingEnvironment dataFetchingEnvironment, AllFieldCasesInterfaceType source) {
		List<String> ret = new ArrayList<>();
		ret.add("Planet 1");
		ret.add("Planet 2");
		return ret;
	}

	@Override
	public HumanConnection friends(DataFetchingEnvironment dataFetchingEnvironment, AllFieldCasesInterfaceType source) {
		Human human = Human.builder().withId(UUID.randomUUID()).withName("a name")
				.withAppearsIn(new ArrayList<Episode>()).build();
		//
		HumanEdge edge = HumanEdge.builder().withNode(human).withCursor(BAD_CURSOR).build();
		//
		List<HumanEdge> edges = new ArrayList<>();
		edges.add(edge);

		PageInfo pageInfo = PageInfo.builder().withEndCursor(BAD_CURSOR).withHasNextPage(false)
				.withHasPreviousPage(false).withStartCursor(BAD_CURSOR).build();

		return HumanConnection.builder().withEdges(edges).withPageInfo(pageInfo).build();
	}

	@Override
	public CompletableFuture<AllFieldCasesWithIdSubtype> oneWithIdSubType(
			DataFetchingEnvironment dataFetchingEnvironment, DataLoader<UUID, AllFieldCasesWithIdSubtype> dataLoader,
			AllFieldCasesInterfaceType source) {
		return dataLoader.load(UUID.randomUUID());
	}

	@Override
	public AllFieldCasesWithIdSubtype oneWithIdSubType(DataFetchingEnvironment dataFetchingEnvironment,
			AllFieldCasesInterfaceType origin) {
		return generator.generateInstance(AllFieldCasesWithIdSubtype.class);
	}

	@Override
	public List<AllFieldCasesWithIdSubtype> listWithIdSubTypes(DataFetchingEnvironment dataFetchingEnvironment,
			AllFieldCasesInterfaceType source, Integer nbItems, Boolean uppercaseName,
			String textToAppendToTheForname) {
		AllFieldCasesWithIdSubtype type = new AllFieldCasesWithIdSubtype();
		type.setId(UUID.randomUUID());
		type.setName("A name");

		if (textToAppendToTheForname != null)
			type.setName(type.getName() + textToAppendToTheForname);

		if (uppercaseName != null && uppercaseName) {
			type.setName(type.getName().toUpperCase());
		}

		List<AllFieldCasesWithIdSubtype> ret = new ArrayList<>();
		for (int i = 0; i < nbItems; i += 1) {
			ret.add(type);
		}

		return ret;
	}

	@Override
	public AllFieldCasesWithoutIdSubtype oneWithoutIdSubType(DataFetchingEnvironment dataFetchingEnvironment,
			AllFieldCasesInterfaceType source, FieldParameterInput input) {
		AllFieldCasesWithoutIdSubtype type = new AllFieldCasesWithoutIdSubtype();
		type.setName("A name");

		if (input != null && input.getUppercase() != null && input.getUppercase()) {
			type.setName(type.getName().toUpperCase());
		}

		return type;
	}

	@Override
	public List<AllFieldCasesWithoutIdSubtype> listWithoutIdSubTypes(DataFetchingEnvironment dataFetchingEnvironment,
			AllFieldCasesInterfaceType source, Integer nbItems, FieldParameterInput input,
			String textToAppendToTheForname) {
		AllFieldCasesWithoutIdSubtype type = new AllFieldCasesWithoutIdSubtype();
		type.setName("A name");

		if (textToAppendToTheForname != null)
			type.setName(type.getName() + textToAppendToTheForname);

		if (input != null && input.getUppercase() != null && input.getUppercase()) {
			type.setName(type.getName().toUpperCase());
		}

		List<AllFieldCasesWithoutIdSubtype> ret = new ArrayList<>();
		for (int i = 0; i < nbItems; i += 1) {
			ret.add(type);
		}

		return ret;
	}

	@Override
	public List<AllFieldCasesInterfaceType> batchLoader(List<UUID> keys) {
		List<AllFieldCasesInterfaceType> ret = new ArrayList<>();

		for (UUID key : keys) {
			AllFieldCasesInterfaceType item = new AllFieldCasesInterfaceType();
			item.setId(key);
			item.setName("Name for " + key.toString());
			item.setAge((long) (Math.random() * Long.MAX_VALUE));
			item.setAliases(new ArrayList<String>());
			item.setPlanets(new ArrayList<String>());
		}

		return ret;
	}

}
