/**
 * 
 */
package org.allGraphQLCases.server.impl;

import javax.annotation.Resource;

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
		return generator.generateInstance(Human.class, 3);
	}

}
