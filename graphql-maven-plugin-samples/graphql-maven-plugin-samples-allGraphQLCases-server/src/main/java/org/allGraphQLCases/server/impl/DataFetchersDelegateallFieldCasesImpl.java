/**
 * 
 */
package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;

import org.allGraphQLCases.server.DataFetchersDelegateallFieldCases;
import org.allGraphQLCases.server.Human;
import org.allGraphQLCases.server.allFieldCases;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author EtienneSF
 *
 */
@Component
public class DataFetchersDelegateallFieldCasesImpl implements DataFetchersDelegateallFieldCases {

	@Override
	public List<String> comments(DataFetchingEnvironment dataFetchingEnvironment, allFieldCases source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Boolean> booleans(DataFetchingEnvironment dataFetchingEnvironment, allFieldCases source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> aliases(DataFetchingEnvironment dataFetchingEnvironment, allFieldCases source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> planets(DataFetchingEnvironment dataFetchingEnvironment, allFieldCases source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Human> friends(DataFetchingEnvironment dataFetchingEnvironment, allFieldCases source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<allFieldCases> batchLoader(List<UUID> keys) {
		// TODO Auto-generated method stub
		return null;
	}

}
