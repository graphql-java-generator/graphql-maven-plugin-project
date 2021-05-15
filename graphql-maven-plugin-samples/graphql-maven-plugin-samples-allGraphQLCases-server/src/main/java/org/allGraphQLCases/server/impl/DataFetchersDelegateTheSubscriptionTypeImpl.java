/**
 * 
 */
package org.allGraphQLCases.server.impl;

import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.allGraphQLCases.server.Episode;
import org.allGraphQLCases.server.Human;
import org.allGraphQLCases.server.util.DataFetchersDelegateTheSubscriptionType;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;
import reactor.core.publisher.Flux;

/**
 * @author etienne-sf
 *
 */
@Component
public class DataFetchersDelegateTheSubscriptionTypeImpl implements DataFetchersDelegateTheSubscriptionType {

	@Autowired
	DataGenerator dataGenerator;

	@Override
	public Publisher<Human> subscribeNewHumanForEpisode(DataFetchingEnvironment dataFetchingEnvironment,
			Episode episode) {
		// The Flux class, from Spring reactive, implements the Publisher interface.
		// Let's return one Human, every 0.1 second
		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.1 second
				.map((l) -> {
					Human h = dataGenerator.generateInstance(Human.class);
					if (!h.getAppearsIn().contains(episode)) {
						h.getAppearsIn().add(episode);
					}
					h.setId(new UUID(0, l));
					return h;
				});
	}

	@Override
	public Publisher<List<Integer>> subscribeToAList(DataFetchingEnvironment dataFetchingEnvironment) {
		// The Flux class, from Spring reactive, implements the Publisher interface.
		// Let's return one list of integer, every 0.1 second
		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.1 second
				.map((l) -> {
					// This message is a list of two integers
					return Arrays.asList(l.intValue(), 2 * l.intValue());
				});
	}

	@Override
	public Publisher<Date> issue53(DataFetchingEnvironment dataFetchingEnvironment, Date date) {
		// The Flux class, from Spring reactive, implements the Publisher interface.
		// Let's returns one item, the date that has been provided as a parameter
		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.1 second
				.map((l) -> {
					// This message is always the date provided as a parameter
					return date;
				});
	}

}
