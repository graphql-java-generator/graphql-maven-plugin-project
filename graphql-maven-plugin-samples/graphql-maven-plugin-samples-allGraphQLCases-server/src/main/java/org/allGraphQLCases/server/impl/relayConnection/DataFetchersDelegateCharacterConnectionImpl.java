package org.allGraphQLCases.server.impl.relayConnection;

import java.util.List;

import org.allGraphQLCases.server.CharacterConnection;
import org.allGraphQLCases.server.CharacterEdge;
import org.allGraphQLCases.server.PageInfo;
import org.allGraphQLCases.server.util.DataFetchersDelegateCharacterConnection;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateCharacterConnectionImpl implements DataFetchersDelegateCharacterConnection {

	@Override
	public List<CharacterEdge> edges(DataFetchingEnvironment dataFetchingEnvironment, CharacterConnection origin) {
		return origin.getEdges();
	}

	@Override
	public PageInfo pageInfo(DataFetchingEnvironment dataFetchingEnvironment, CharacterConnection origin) {
		return origin.getPageInfo();
	}

}
