package org.allGraphQLCases.server.impl;

import org.allGraphQLCases.server.Bar140;
import org.allGraphQLCases.server.Foo140;
import org.allGraphQLCases.server.util.DataFetchersDelegateFoo140;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateFoo140Impl implements DataFetchersDelegateFoo140 {

	@Override
	public Bar140 bar(DataFetchingEnvironment dataFetchingEnvironment, Foo140 origin) {
		return Bar140.builder().withName("Bar140's name for a Foo140").build();
	}

}
