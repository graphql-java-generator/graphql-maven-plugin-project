package org.allGraphQLCases.server.impl;

import org.allGraphQLCases.server.STP_Bar140_STS;
import org.allGraphQLCases.server.STP_Foo140_STS;
import org.allGraphQLCases.server.DataFetchersDelegateFoo140;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateFoo140Impl implements DataFetchersDelegateFoo140 {

	@Override
	public STP_Bar140_STS bar(DataFetchingEnvironment dataFetchingEnvironment, STP_Foo140_STS origin) {
		return STP_Bar140_STS.builder().withName("Bar140's name for a Foo140").build();
	}

}
