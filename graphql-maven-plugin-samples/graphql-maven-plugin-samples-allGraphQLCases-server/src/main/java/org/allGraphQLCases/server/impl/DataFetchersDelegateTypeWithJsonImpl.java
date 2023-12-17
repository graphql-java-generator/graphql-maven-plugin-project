/**
 * 
 */
package org.allGraphQLCases.server.impl;

import java.util.List;

import org.allGraphQLCases.server.DataFetchersDelegateTypeWithJson;
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

}
