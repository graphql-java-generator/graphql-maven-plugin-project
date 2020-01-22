package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.allGraphQLCases.server.AllFieldCasesInterface;
import org.allGraphQLCases.server.AllFieldCasesWithIdSubtype;
import org.allGraphQLCases.server.AllFieldCasesWithoutIdSubtype;
import org.allGraphQLCases.server.DataFetchersDelegateAllFieldCasesInterface;
import org.allGraphQLCases.server.FieldParameterInput;
import org.allGraphQLCases.server.Human;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateAllFieldCasesInterfaceImpl implements DataFetchersDelegateAllFieldCasesInterface {

	@Override
	public List<String> comments(DataFetchingEnvironment dataFetchingEnvironment, AllFieldCasesInterface source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Boolean> booleans(DataFetchingEnvironment dataFetchingEnvironment, AllFieldCasesInterface source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> aliases(DataFetchingEnvironment dataFetchingEnvironment, AllFieldCasesInterface source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> planets(DataFetchingEnvironment dataFetchingEnvironment, AllFieldCasesInterface source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Human> friends(DataFetchingEnvironment dataFetchingEnvironment, AllFieldCasesInterface source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<AllFieldCasesWithIdSubtype> oneWithIdSubType(
			DataFetchingEnvironment dataFetchingEnvironment, DataLoader<UUID, AllFieldCasesWithIdSubtype> dataLoader,
			AllFieldCasesInterface source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AllFieldCasesWithIdSubtype> listWithIdSubTypes(DataFetchingEnvironment dataFetchingEnvironment,
			AllFieldCasesInterface source, Integer nbItems, Boolean uppercaseName, String textToAppendToTheForname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AllFieldCasesWithoutIdSubtype oneWithoutIdSubType(DataFetchingEnvironment dataFetchingEnvironment,
			AllFieldCasesInterface source, FieldParameterInput input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AllFieldCasesWithoutIdSubtype> listWithoutIdSubTypes(DataFetchingEnvironment dataFetchingEnvironment,
			AllFieldCasesInterface source, Integer nbItems, FieldParameterInput input,
			String textToAppendToTheForname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AllFieldCasesInterface> batchLoader(List<UUID> keys) {
		// TODO Auto-generated method stub
		return null;
	}

}
