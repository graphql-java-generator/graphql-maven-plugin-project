/**
 * 
 */
package org.allGraphQLCases.server.impl;

import org.allGraphQLCases.server.DataFetchersDelegateTheSubscriptionType;
import org.allGraphQLCases.server.Episode;
import org.allGraphQLCases.server.Human;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author etienne-sf
 *
 */
@Component
public class DataFetchersDelegateTheSubscriptionTypeImpl implements DataFetchersDelegateTheSubscriptionType {

	@Override
	public Human subscribeNewHumanForEpisode(DataFetchingEnvironment dataFetchingEnvironment, Episode episode) {
		// TODO Auto-generated method stub
		return null;
	}

}
