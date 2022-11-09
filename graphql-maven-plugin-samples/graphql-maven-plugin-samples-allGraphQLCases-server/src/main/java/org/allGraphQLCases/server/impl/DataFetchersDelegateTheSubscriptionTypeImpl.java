/**
 * 
 */
package org.allGraphQLCases.server.impl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.allGraphQLCases.server.*;
import org.allGraphQLCases.server.SINP_AllFieldCasesWithoutIdSubtypeInput_SINS;
import org.allGraphQLCases.server.SINP_SubscriptionTestParam_SINS;
import org.allGraphQLCases.server.util.DataFetchersDelegateTheSubscriptionType;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

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

	static Mapper mapper = DozerBeanMapperBuilder.buildDefault();

	@Override
	public Publisher<STP_Human_STS> subscribeNewHumanForEpisode(DataFetchingEnvironment dataFetchingEnvironment,
			SEP_Episode_SES SEP_Episode_SES) {
		// The Flux class, from Spring reactive, implements the Publisher interface.
		// Let's return one STP_Human_STS, every 0.1 second
		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.1 second
				.map((l) -> {
					STP_Human_STS h = dataGenerator.generateInstance(STP_Human_STS.class);
					if (!h.getAppearsIn().contains(SEP_Episode_SES)) {
						h.getAppearsIn().add(SEP_Episode_SES);
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

	@Override
	public Publisher<String> subscriptionTest(DataFetchingEnvironment dataFetchingEnvironment,
			SINP_SubscriptionTestParam_SINS param) {
		if (param.getErrorOnSubscription()) {
			// The client asked that an exception is thrown now
			throw new RuntimeException("Oups, the subscriber asked for an error during the subscription");
		} else if (param.getErrorOnNext()) {
			return Flux//
					.interval(Duration.ofMillis(100))// A message every 0.1 second
					.map((l) -> {
						boolean b = true;
						if (b)
							throw new RuntimeException("Oups, the subscriber asked for an error for each next message");
						// The line below will never get executed. But doing this prevents a compilation error !
						return "won't go there";
					});
		} else if (param.getCompleteAfterFirstNotification()) {
			return Flux.just("The subscriber asked for a complete after the first notification");
		} else if (param.getCloseWebSocketBeforeFirstNotification()) {
			return Flux//
					.interval(Duration.ofMillis(100))// A message every 0.1 second
					.map((l) -> {
						boolean b = true;
						if (b)
							throw new RuntimeException(
									"Oups, the subscriber asked that the web socket get disconnected before the first notification");
						// The line below will never get executed. But doing this prevents a compilation error !
						return "won't go there";
					});
		} else {
			// The client didn't ask for any specific error. Let's return a valid flux, that will sent 10 string each
			// second
			return Flux//
					.interval(Duration.ofMillis(100))// A message every 0.1 second
					.map((l) -> Long.toString(l));
		}
	}

	@Override
	public Publisher<STP_AllFieldCases_STS> allGraphQLCasesInput(DataFetchingEnvironment dataFetchingEnvironment,
			SINP_AllFieldCasesInput_SINS input) {
		STP_AllFieldCases_STS ret = mapper.map(input, STP_AllFieldCases_STS.class);

		List<STP_AllFieldCasesWithoutIdSubtype_STS> list = new ArrayList<>(input.getWithoutIdSubtype().size());
		for (SINP_AllFieldCasesWithoutIdSubtypeInput_SINS item : input.getWithoutIdSubtype()) {
			list.add(mapper.map(item, STP_AllFieldCasesWithoutIdSubtype_STS.class));
		}
		ret.setListWithoutIdSubTypes(list);

		return Flux.just(ret);
	}

	@Override
	public Publisher<STP_AllFieldCases_STS> allGraphQLCasesParam(DataFetchingEnvironment dataFetchingEnvironment, String id,
			String name, Long age, Integer integer, Date date, List<Date> dates, List<List<Double>> matrix,
			SINP_AllFieldCasesWithoutIdSubtypeInput_SINS onewithoutIdSubtype,
			List<SINP_AllFieldCasesWithoutIdSubtypeInput_SINS> listwithoutIdSubtype) {
		STP_AllFieldCases_STS ret = new STP_AllFieldCases_STS();
		ret.setId(UUID.fromString(id));
		ret.setName(name);
		ret.setAge(age);
		ret.setDate(date);
		ret.setDates(dates);
		ret.setMatrix(matrix);
		ret.setAliases(Arrays.asList("an alias"));
		ret.setPlanets(Arrays.asList("planet 1", "planet 2"));

		ret.setOneWithoutIdSubType(mapper.map(onewithoutIdSubtype, STP_AllFieldCasesWithoutIdSubtype_STS.class));

		List<STP_AllFieldCasesWithoutIdSubtype_STS> list = new ArrayList<>(listwithoutIdSubtype.size());
		for (SINP_AllFieldCasesWithoutIdSubtypeInput_SINS item : listwithoutIdSubtype) {
			list.add(mapper.map(item, STP_AllFieldCasesWithoutIdSubtype_STS.class));
		}
		ret.setListWithoutIdSubTypes(list);

		return Flux.just(ret);
	}

	@Override
	public Publisher<Optional<String>> subscriptionWithNullResponse(DataFetchingEnvironment dataFetchingEnvironment) {
		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.1 second
				.map((l) -> Optional.ofNullable(null));
	}

	/**
	 * Returns a Flux, that will produce a list of two random dates, every 0.1s
	 */
	@Override
	public Publisher<Optional<List<Date>>> subscribeToAListOfScalars(DataFetchingEnvironment dataFetchingEnvironment) {
		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.1 second
				.map((l) -> Optional.ofNullable(dataGenerator.generateInstanceList(Date.class, 2)));
	}

	@Override
	public Publisher<Optional<String>> _if(DataFetchingEnvironment dataFetchingEnvironment) {
		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.1 second
				.map((l) -> Optional.ofNullable("a value for _if"));
	}

	@Override
	public Publisher<Optional<String>> _implements(DataFetchingEnvironment dataFetchingEnvironment) {
		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.1 second
				.map((l) -> Optional.ofNullable("a value for _implements"));
	}

	@Override
	public Publisher<Optional<SEP_EnumWithReservedJavaKeywordAsValues_SES>> enumWithReservedJavaKeywordAsValues(
			DataFetchingEnvironment dataFetchingEnvironment) {
		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.1 second
				.map((l) -> Optional.ofNullable(SEP_EnumWithReservedJavaKeywordAsValues_SES._instanceof));
	}

	@Override
	public Publisher<Optional<List<SEP_EnumWithReservedJavaKeywordAsValues_SES>>> listOfEnumWithReservedJavaKeywordAsValues(
			DataFetchingEnvironment dataFetchingEnvironment) {
		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.1 second
				.map((l) -> Optional.ofNullable(Arrays.asList(SEP_EnumWithReservedJavaKeywordAsValues_SES._int,
						SEP_EnumWithReservedJavaKeywordAsValues_SES._interface, SEP_EnumWithReservedJavaKeywordAsValues_SES._long)));
	}

}
