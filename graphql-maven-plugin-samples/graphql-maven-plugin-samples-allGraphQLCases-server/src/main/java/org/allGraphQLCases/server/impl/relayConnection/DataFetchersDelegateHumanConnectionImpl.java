package org.allGraphQLCases.server.impl.relayConnection;

import java.util.List;

import org.allGraphQLCases.server.HumanConnection;
import org.allGraphQLCases.server.HumanEdge;
import org.allGraphQLCases.server.PageInfo;
import org.allGraphQLCases.server.util.DataFetchersDelegateHumanConnection;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateHumanConnectionImpl implements DataFetchersDelegateHumanConnection {

	@Override
	public List<HumanEdge> edges(DataFetchingEnvironment dataFetchingEnvironment, HumanConnection origin) {
		return origin.getEdges();
	}

	@Override
	public PageInfo pageInfo(DataFetchingEnvironment dataFetchingEnvironment, HumanConnection origin) {
		return origin.getPageInfo();
	}

}
