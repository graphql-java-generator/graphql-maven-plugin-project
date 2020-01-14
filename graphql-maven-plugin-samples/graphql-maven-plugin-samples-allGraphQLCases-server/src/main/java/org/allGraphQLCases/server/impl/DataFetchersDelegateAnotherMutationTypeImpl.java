/**
 * 
 */
package org.allGraphQLCases.server.impl;

import javax.annotation.Resource;

import org.allGraphQLCases.server.AllFieldCases;
import org.allGraphQLCases.server.AllFieldCasesInput;
import org.allGraphQLCases.server.DataFetchersDelegateAnotherMutationType;
import org.allGraphQLCases.server.Human;
import org.allGraphQLCases.server.HumanInput;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author EtienneSF
 *
 */
@Component
public class DataFetchersDelegateAnotherMutationTypeImpl implements DataFetchersDelegateAnotherMutationType {

	@Resource
	DataGenerator generator;

	@Override
	public Human createHuman(DataFetchingEnvironment dataFetchingEnvironment, HumanInput human) {
		return generator.generateInstance(Human.class);
	}

	@Override
	public AllFieldCases createAllFieldCases(DataFetchingEnvironment dataFetchingEnvironment,
			AllFieldCasesInput input) {
		// TODO Auto-generated method stub
		return null;
	}

}
