package org.allGraphQLCases.server.impl;

import org.allGraphQLCases.server.Bar140;
import org.allGraphQLCases.server.IBar140;
import org.allGraphQLCases.server.IFoo140;
import org.allGraphQLCases.server.util.DataFetchersDelegateIFoo140;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateIFoo140Impl implements DataFetchersDelegateIFoo140 {

	@Override
	public IBar140 bar(DataFetchingEnvironment dataFetchingEnvironment, IFoo140 origin) {
		return Bar140.builder().withName("Bar140's name for an IBar140").build();
	}

}
