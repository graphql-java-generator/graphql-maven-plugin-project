package org.allGraphQLCases.server.impl;

import org.allGraphQLCases.server.DataFetchersDelegatebreak;
import org.allGraphQLCases.server.SEP_extends_SES;
import org.allGraphQLCases.server.STP_break_STS;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

/** Custom field data fetchers are available since release 2.5 */
@Component
public class DataFetchersDelegatebreakImpl implements DataFetchersDelegatebreak {

	/** Custom field data fetchers are available since release 2.5 */
	@Override
	public SEP_extends_SES _case(DataFetchingEnvironment dataFetchingEnvironment, STP_break_STS origin,
			SEP_extends_SES test, String _if) {
		// A not-so-useful method
		return origin.getCase();
	}

}
