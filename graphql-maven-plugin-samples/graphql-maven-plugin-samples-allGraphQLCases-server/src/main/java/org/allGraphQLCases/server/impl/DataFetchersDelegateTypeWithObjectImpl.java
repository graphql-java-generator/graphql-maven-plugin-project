package org.allGraphQLCases.server.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.allGraphQLCases.server.DataFetchersDelegateTypeWithObject;
import org.allGraphQLCases.server.SEP_Episode_SES;
import org.allGraphQLCases.server.STP_TypeWithObject_STS;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

/** Custom field data fetchers are available since release 2.5 */
@Component
public class DataFetchersDelegateTypeWithObjectImpl implements DataFetchersDelegateTypeWithObject {

	@Override
	public List<Object> objects(DataFetchingEnvironment dataFetchingEnvironment, STP_TypeWithObject_STS origin) {
		List<Object> ret = origin.getObjects().stream().collect(Collectors.toList());
		return ret;
	}

	@Override
	public String withArguments(DataFetchingEnvironment dataFetchingEnvironment, STP_TypeWithObject_STS origin,
			String test, Date date, Long _long, Boolean _boolean, SEP_Episode_SES _enum, Object object,
			List<Object> objects) {
		return origin.getWithArguments() //
				+ " (test=" + test//
				+ ", date=" + date//
				+ ", long=" + _long//
				+ ", boolean=" + _boolean//
				+ ", enum=" + _enum//
				+ ", object=" + object//
				+ ", objects=" + objects //
				+ ")";

	}

}
