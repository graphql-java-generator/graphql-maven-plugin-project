/**
 * 
 */
package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

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

	@Resource
	DataGenerator generator;

	@Override
	public List<String> comments(DataFetchingEnvironment dataFetchingEnvironment, allFieldCases source) {
		return generator.generateInstanceList(String.class, 10);
	}

	@Override
	public List<Boolean> booleans(DataFetchingEnvironment dataFetchingEnvironment, allFieldCases source) {
		return generator.generateInstanceList(Boolean.class, 10);
	}

	@Override
	public List<String> aliases(DataFetchingEnvironment dataFetchingEnvironment, allFieldCases source) {
		return generator.generateInstanceList(String.class, 10);
	}

	@Override
	public List<String> planets(DataFetchingEnvironment dataFetchingEnvironment, allFieldCases source) {
		return generator.generateInstanceList(String.class, 10);
	}

	@Override
	public List<Human> friends(DataFetchingEnvironment dataFetchingEnvironment, allFieldCases source) {
		return generator.generateInstanceList(Human.class, 10);
	}

	@Override
	public List<allFieldCases> batchLoader(List<UUID> keys) {
		return generator.generateInstanceList(allFieldCases.class, keys.size());
	}

}
