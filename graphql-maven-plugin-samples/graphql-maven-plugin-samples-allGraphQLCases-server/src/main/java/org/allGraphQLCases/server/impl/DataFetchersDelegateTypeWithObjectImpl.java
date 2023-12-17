package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.allGraphQLCases.server.DataFetchersDelegateTypeWithObject;
import org.allGraphQLCases.server.STP_TypeWithObject_STS;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateTypeWithObjectImpl implements DataFetchersDelegateTypeWithObject {

	@Override
	public List<Object> objects(DataFetchingEnvironment dataFetchingEnvironment, STP_TypeWithObject_STS origin) {
		List<Object> ret = origin.getObjects().stream().collect(Collectors.toList());
		return ret;
	}

}
