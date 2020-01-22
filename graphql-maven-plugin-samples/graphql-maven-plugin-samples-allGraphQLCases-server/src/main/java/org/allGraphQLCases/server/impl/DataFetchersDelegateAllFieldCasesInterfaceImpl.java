package org.allGraphQLCases.server.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.allGraphQLCases.server.AllFieldCasesInterface;
import org.allGraphQLCases.server.AllFieldCasesInterfaceType;
import org.allGraphQLCases.server.AllFieldCasesWithIdSubtype;
import org.allGraphQLCases.server.AllFieldCasesWithoutIdSubtype;
import org.allGraphQLCases.server.DataFetchersDelegateAllFieldCasesInterface;
import org.allGraphQLCases.server.Episode;
import org.allGraphQLCases.server.FieldParameterInput;
import org.allGraphQLCases.server.Human;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateAllFieldCasesInterfaceImpl implements DataFetchersDelegateAllFieldCasesInterface {

	@Override
	public List<String> comments(DataFetchingEnvironment dataFetchingEnvironment, AllFieldCasesInterface source) {
		List<String> ret = new ArrayList<>();
		ret.add("Comment1");
		ret.add("Comment2");
		return ret;
	}

	@Override
	public List<Boolean> booleans(DataFetchingEnvironment dataFetchingEnvironment, AllFieldCasesInterface source) {
		List<Boolean> ret = new ArrayList<>();
		ret.add(true);
		ret.add(false);
		return null;
	}

	@Override
	public List<String> aliases(DataFetchingEnvironment dataFetchingEnvironment, AllFieldCasesInterface source) {
		List<String> ret = new ArrayList<>();
		ret.add("Alias 1");
		ret.add("Alias 2");
		return ret;
	}

	@Override
	public List<String> planets(DataFetchingEnvironment dataFetchingEnvironment, AllFieldCasesInterface source) {
		List<String> ret = new ArrayList<>();
		ret.add("Planet 1");
		ret.add("Planet 2");
		return ret;
	}

	@Override
	public List<Human> friends(DataFetchingEnvironment dataFetchingEnvironment, AllFieldCasesInterface source) {
		Human human = new Human();
		human.setId(UUID.randomUUID());
		human.setName("a name");
		human.setAppearsIn(new ArrayList<Episode>());

		List<Human> ret = new ArrayList<>();
		ret.add(human);

		return ret;
	}

	@Override
	public CompletableFuture<AllFieldCasesWithIdSubtype> oneWithIdSubType(
			DataFetchingEnvironment dataFetchingEnvironment, DataLoader<UUID, AllFieldCasesWithIdSubtype> dataLoader,
			AllFieldCasesInterface source) {
		return dataLoader.load(UUID.randomUUID());
	}

	@Override
	public List<AllFieldCasesWithIdSubtype> listWithIdSubTypes(DataFetchingEnvironment dataFetchingEnvironment,
			AllFieldCasesInterface source, Integer nbItems, Boolean uppercaseName, String textToAppendToTheForname) {
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
			AllFieldCasesInterface source, FieldParameterInput input) {
		AllFieldCasesWithoutIdSubtype type = new AllFieldCasesWithoutIdSubtype();
		type.setName("A name");

		if (input != null && input.getUppercase() != null && input.getUppercase()) {
			type.setName(type.getName().toUpperCase());
		}

		return type;
	}

	@Override
	public List<AllFieldCasesWithoutIdSubtype> listWithoutIdSubTypes(DataFetchingEnvironment dataFetchingEnvironment,
			AllFieldCasesInterface source, Integer nbItems, FieldParameterInput input,
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
	public List<AllFieldCasesInterface> batchLoader(List<UUID> keys) {
		List<AllFieldCasesInterface> ret = new ArrayList<>();

		for (UUID key : keys) {
			AllFieldCasesInterfaceType item = new AllFieldCasesInterfaceType();
			item.setId(key);
			item.setName("Name for " + key.toString());
			item.setAge((int) (Math.random() * Integer.MAX_VALUE));
			item.setAliases(new ArrayList<String>());
			item.setPlanets(new ArrayList<String>());
		}

		return ret;
	}

}
