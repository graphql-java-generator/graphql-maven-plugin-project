/**
 * 
 */
package org.allGraphQLCases.server.impl;

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

	@Override
	public Human createHuman(DataFetchingEnvironment dataFetchingEnvironment, HumanInput human) {
		// TODO Auto-generated method stub
		return null;
	}

}
