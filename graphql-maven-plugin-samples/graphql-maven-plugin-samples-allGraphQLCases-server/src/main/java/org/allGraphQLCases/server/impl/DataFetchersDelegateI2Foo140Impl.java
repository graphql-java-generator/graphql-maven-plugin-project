package org.allGraphQLCases.server.impl;

import org.allGraphQLCases.server.Bar140;
import org.allGraphQLCases.server.I2Bar140;
import org.allGraphQLCases.server.I2Foo140;
import org.allGraphQLCases.server.util.DataFetchersDelegateI2Foo140;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateI2Foo140Impl implements DataFetchersDelegateI2Foo140 {

	@Override
	public I2Bar140 bar(DataFetchingEnvironment dataFetchingEnvironment, I2Foo140 origin) {
		return Bar140.builder().withName("Bar140's name for an I2Bar140").build();
	}

}
