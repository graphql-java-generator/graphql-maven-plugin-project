package org.allGraphQLCases.server.impl;

import java.util.List;

import org.allGraphQLCases.server.STP_TFoo1_STS;
import org.allGraphQLCases.server.STP_TList_STS;
import org.allGraphQLCases.server.util.DataFetchersDelegateTList;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateTListImpl implements DataFetchersDelegateTList {

	@Override
	public List<STP_TFoo1_STS> list(DataFetchingEnvironment dataFetchingEnvironment, STP_TList_STS origin) {
		// TODO Auto-generated method stub
		return null;
	}

}
