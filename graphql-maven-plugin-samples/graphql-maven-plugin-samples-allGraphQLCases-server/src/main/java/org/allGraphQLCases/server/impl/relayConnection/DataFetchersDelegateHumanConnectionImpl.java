package org.allGraphQLCases.server.impl.relayConnection;

import java.util.List;

import org.allGraphQLCases.server.STP_HumanConnection_STS;
import org.allGraphQLCases.server.STP_HumanEdge_STS;
import org.allGraphQLCases.server.STP_PageInfo_STS;
import org.allGraphQLCases.server.util.DataFetchersDelegateHumanConnection;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateHumanConnectionImpl implements DataFetchersDelegateHumanConnection {

	@Override
	public List<STP_HumanEdge_STS> edges(DataFetchingEnvironment dataFetchingEnvironment, STP_HumanConnection_STS origin) {
		return origin.getEdges();
	}

	@Override
	public STP_PageInfo_STS pageInfo(DataFetchingEnvironment dataFetchingEnvironment, STP_HumanConnection_STS origin) {
		return origin.getPageInfo();
	}

}
