/**
 * 
 */
package org.allGraphQLCases.server.impl;

import java.util.Date;
import java.util.List;

import org.allGraphQLCases.server.DataFetchersDelegateTypeWithJson;
import org.allGraphQLCases.server.SEP_Episode_SES;
import org.allGraphQLCases.server.STP_TypeWithJson_STS;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.ObjectNode;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateTypeWithJsonImpl implements DataFetchersDelegateTypeWithJson {

	@Override
	public List<ObjectNode> jsons(DataFetchingEnvironment dataFetchingEnvironment, STP_TypeWithJson_STS origin) {
		List<ObjectNode> ret = origin.getJsons();
		return ret;
	}

	/** Custom field data fetchers are available since release 2.5 */
	@Override
	public String withArguments(DataFetchingEnvironment dataFetchingEnvironment, STP_TypeWithJson_STS origin,
			String test, Date date, Long _long, Boolean _boolean, SEP_Episode_SES _enum, ObjectNode json,
			List<ObjectNode> jsons) {
		return origin.getWithArguments() //
				+ " (test=" + test//
				+ ", date=" + date//
				+ ", long=" + _long//
				+ ", boolean=" + _boolean//
				+ ", enum=" + _enum//
				+ ", json=" + json//
				+ ", jsons=" + jsons //
				+ ")";
	}

}
