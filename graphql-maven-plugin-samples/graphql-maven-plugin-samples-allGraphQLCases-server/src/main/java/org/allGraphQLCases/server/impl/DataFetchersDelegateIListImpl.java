package org.allGraphQLCases.server.impl;

import java.util.List;

import org.allGraphQLCases.server.SIP_IFoo1_SIS;
import org.allGraphQLCases.server.SIP_IList_SIS;
import org.allGraphQLCases.server.DataFetchersDelegateIList;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateIListImpl implements DataFetchersDelegateIList {

	@Override
	public List<SIP_IFoo1_SIS> list(DataFetchingEnvironment dataFetchingEnvironment, SIP_IList_SIS origin) {
		// TODO Auto-generated method stub
		return null;
	}

}
