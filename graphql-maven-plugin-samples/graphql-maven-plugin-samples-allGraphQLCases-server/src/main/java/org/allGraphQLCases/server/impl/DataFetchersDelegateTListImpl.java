package org.allGraphQLCases.server.impl;

import java.util.List;

import org.allGraphQLCases.server.TFoo1;
import org.allGraphQLCases.server.TList;
import org.allGraphQLCases.server.util.DataFetchersDelegateTList;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateTListImpl implements DataFetchersDelegateTList {

	@Override
	public List<TFoo1> list(DataFetchingEnvironment dataFetchingEnvironment, TList origin) {
		// TODO Auto-generated method stub
		return null;
	}

}
