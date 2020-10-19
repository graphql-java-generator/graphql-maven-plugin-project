/**
 * 
 */
package org.allGraphQLCases.server.impl;

import org.allGraphQLCases.server.Episode;
import org.allGraphQLCases.server.Human;
import org.allGraphQLCases.server.util.DataFetchersDelegateTheSubscriptionType;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author etienne-sf
 *
 */
@Component
public class DataFetchersDelegateTheSubscriptionTypeImpl implements DataFetchersDelegateTheSubscriptionType {

	@Override
	public Publisher<Human> subscribeNewHumanForEpisode(DataFetchingEnvironment dataFetchingEnvironment,
			Episode episode) {
		// TODO Auto-generated method stub
		return null;
	}

}
