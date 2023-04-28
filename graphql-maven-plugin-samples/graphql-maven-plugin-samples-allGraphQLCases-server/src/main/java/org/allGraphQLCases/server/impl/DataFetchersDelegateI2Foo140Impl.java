package org.allGraphQLCases.server.impl;

import org.allGraphQLCases.server.STP_Bar140_STS;
import org.allGraphQLCases.server.SIP_I2Bar140_SIS;
import org.allGraphQLCases.server.SIP_I2Foo140_SIS;
import org.allGraphQLCases.server.DataFetchersDelegateI2Foo140;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateI2Foo140Impl implements DataFetchersDelegateI2Foo140 {

	@Override
	public SIP_I2Bar140_SIS bar(DataFetchingEnvironment dataFetchingEnvironment, SIP_I2Foo140_SIS origin) {
		return STP_Bar140_STS.builder().withName("STP_Bar140_STS's name for an SIP_I2Bar140_SIS").build();
	}

}
