package org.allGraphQLCases.server.impl;

import org.allGraphQLCases.server.STP_Bar140_STS;
import org.allGraphQLCases.server.SIP_IBar140_SIS;
import org.allGraphQLCases.server.SIP_IFoo140_SIS;
import org.allGraphQLCases.server.DataFetchersDelegateIFoo140;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateIFoo140Impl implements DataFetchersDelegateIFoo140 {

	@Override
	public SIP_IBar140_SIS bar(DataFetchingEnvironment dataFetchingEnvironment, SIP_IFoo140_SIS origin) {
		return STP_Bar140_STS.builder().withName("STP_Bar140_STS's name for an SIP_IBar140_SIS").build();
	}

}
