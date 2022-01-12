package org.allGraphQLCases.server.impl;

import java.util.List;

import org.allGraphQLCases.server.IFoo1;
import org.allGraphQLCases.server.IList;
import org.allGraphQLCases.server.util.DataFetchersDelegateIList;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateIListImpl implements DataFetchersDelegateIList {

	@Override
	public List<IFoo1> list(DataFetchingEnvironment dataFetchingEnvironment, IList origin) {
		// TODO Auto-generated method stub
		return null;
	}

}
