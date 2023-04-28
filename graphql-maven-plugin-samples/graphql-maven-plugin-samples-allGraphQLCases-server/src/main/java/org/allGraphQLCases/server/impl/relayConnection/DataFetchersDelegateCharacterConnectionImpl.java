package org.allGraphQLCases.server.impl.relayConnection;

import java.util.List;

import org.allGraphQLCases.server.SIP_CharacterConnection_SIS;
import org.allGraphQLCases.server.SIP_CharacterEdge_SIS;
import org.allGraphQLCases.server.STP_PageInfo_STS;
import org.allGraphQLCases.server.DataFetchersDelegateCharacterConnection;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

@Component
public class DataFetchersDelegateCharacterConnectionImpl implements DataFetchersDelegateCharacterConnection {

	@Override
	public List<SIP_CharacterEdge_SIS> edges(DataFetchingEnvironment dataFetchingEnvironment, SIP_CharacterConnection_SIS origin) {
		return origin.getEdges();
	}

	@Override
	public STP_PageInfo_STS pageInfo(DataFetchingEnvironment dataFetchingEnvironment, SIP_CharacterConnection_SIS origin) {
		return origin.getPageInfo();
	}

}
