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
import java.util.function.Consumer;

import org.allGraphQLCases.server.DataFetchersDelegateTheSubscriptionType;
import org.allGraphQLCases.server.SEP_EnumWithReservedJavaKeywordAsValues_SES;
import org.allGraphQLCases.server.SEP_Episode_SES;
import org.allGraphQLCases.server.SINP_AllFieldCasesInput_SINS;
import org.allGraphQLCases.server.SINP_AllFieldCasesWithoutIdSubtypeInput_SINS;
import org.allGraphQLCases.server.SINP_SubscriptionTestParam_SINS;
import org.allGraphQLCases.server.STP_AllFieldCasesWithoutIdSubtype_STS;
import org.allGraphQLCases.server.STP_AllFieldCases_STS;
import org.allGraphQLCases.server.STP_Human_STS;
import org.allGraphQLCases.server.config.GraphQlException;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static Logger logger = LoggerFactory.getLogger(DataFetchersDelegateTheSubscriptionTypeImpl.class);

	@Autowired
	DataGenerator dataGenerator;

	static Mapper mapper = DozerBeanMapperBuilder.buildDefault();

	boolean subscribedOnSubscribeNewHumanForEpisode = false;

	@Override
	public Flux<STP_Human_STS> subscribeNewHumanForEpisode(DataFetchingEnvironment dataFetchingEnvironment,
			SEP_Episode_SES SEP_Episode_SES) {

		Consumer<? super Subscription> onSubscribe = new Consumer<Subscription>() {
			@Override
			public void accept(Subscription t) {
				logger.debug("The subscription 'subscribeNewHumanForEpisode' is now active");
				subscribedOnSubscribeNewHumanForEpisode = true;
			}
		};

		Runnable onCancel = new Runnable() {
			@Override
			public void run() {
				logger.debug("The subscription 'subscribeNewHumanForEpisode' is now canceled");
				subscribedOnSubscribeNewHumanForEpisode = false;
			}
		};

		Runnable onTerminate = new Runnable() {
			@Override
			public void run() {
				logger.debug("The subscription 'subscribeNewHumanForEpisode' is now terminated");
				subscribedOnSubscribeNewHumanForEpisode = false;
			}
		};

		Consumer<Throwable> onError = new Consumer<Throwable>() {
			@Override
			public void accept(Throwable t) {
				logger.debug("The subscription 'subscribeNewHumanForEpisode' had an error: {}-{}",
						t.getClass().getName(), t.getMessage());
				subscribedOnSubscribeNewHumanForEpisode = false;
			}
		};

		// The Flux class, from Spring reactive, implements the Publisher interface.
		// Let's return one STP_Human_STS, every 0.1 second
		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.1 second
				.doOnSubscribe(onSubscribe)//
				.doOnCancel(onCancel)//
				.doOnTerminate(onTerminate)//
				.doOnError(Throwable.class, onError)//
				.map((l) -> {
					STP_Human_STS h = dataGenerator.generateInstance(STP_Human_STS.class);
					if (!h.getAppearsIn().contains(SEP_Episode_SES)) {
						h.getAppearsIn().add(SEP_Episode_SES);
					}
					h.setId(new UUID(0, l));
					logger.trace("subscribeNewHumanForEpisode [active={}] Sending this human: {}",
							subscribedOnSubscribeNewHumanForEpisode, h);
					return h;
				});
	}

	@Override
	public Flux<List<Integer>> subscribeToAList(DataFetchingEnvironment dataFetchingEnvironment) {
		logger.debug("Executing subscription subscribeToAList()");
		// The Flux class, from Spring reactive, implements the Publisher interface.
		// Let's return one list of integer, every 0.1 second
		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.2 second
				.map((l) -> {
					// This message is a list of two integers
					return Arrays.asList(l.intValue(), 2 * l.intValue());
				}).doOnEach(lst -> {
					if (logger.isTraceEnabled()) {
						String separator = "";
						StringBuilder sb = new StringBuilder();
						for (int i : lst.get()) {
							sb.append(separator).append(i);
							separator = ",";
						}
						logger.trace("Sending this list: [{}]", sb);
					}
				});
	}

	@Override
	public Flux<Date> issue53(DataFetchingEnvironment dataFetchingEnvironment, Date date) {
		logger.debug("Executing subscription issue53({})", date);
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
	public Flux<String> subscriptionTest(DataFetchingEnvironment dataFetchingEnvironment,
			SINP_SubscriptionTestParam_SINS param) {
		logger.debug("Executing subscription subscriptionTest({})", param);

		if (param.getErrorOnSubscription()) {
			// The client asked that an exception is thrown now
			throw new GraphQlException("Oups, the subscriber asked for an error during the subscription");
		} else if (param.getErrorOnNext()) {
			return Flux//
					.interval(Duration.ofMillis(100))// A message every 0.1 second
					.map((l) -> {
						boolean b = true;
						if (b)
							throw new GraphQlException("Oups, the subscriber asked for an error for each next message");
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
							throw new GraphQlException(
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
	public Flux<STP_AllFieldCases_STS> allGraphQLCasesInput(DataFetchingEnvironment dataFetchingEnvironment,
			SINP_AllFieldCasesInput_SINS input) {
		logger.debug("Executing subscription allGraphQLCasesInput({})", input);

		STP_AllFieldCases_STS ret = mapper.map(input, STP_AllFieldCases_STS.class);

		List<STP_AllFieldCasesWithoutIdSubtype_STS> list = new ArrayList<>(input.getWithoutIdSubtype().size());
		for (SINP_AllFieldCasesWithoutIdSubtypeInput_SINS item : input.getWithoutIdSubtype()) {
			list.add(mapper.map(item, STP_AllFieldCasesWithoutIdSubtype_STS.class));
		}
		ret.setListWithoutIdSubTypes(list);

		return Flux.just(ret);
	}

	@Override
	public Flux<STP_AllFieldCases_STS> allGraphQLCasesParam(DataFetchingEnvironment dataFetchingEnvironment, String id,
			String name, Long age, Integer integer, Date date, List<Date> dates, List<List<Double>> matrix,
			SINP_AllFieldCasesWithoutIdSubtypeInput_SINS onewithoutIdSubtype,
			List<SINP_AllFieldCasesWithoutIdSubtypeInput_SINS> listwithoutIdSubtype) {
		logger.debug("Executing subscription allGraphQLCasesParam(id={}, name={}, age={}...)", id, name, age);

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
	public Flux<Optional<String>> subscriptionWithNullResponse(DataFetchingEnvironment dataFetchingEnvironment) {
		logger.debug("Executing subscription subscriptionWithNullResponse()");

		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.2 second
				.map((l) -> {
					logger.trace("Sending a message in 'subscriptionWithNullResponse'");
					return Optional.ofNullable(null);
				});
	}

	/**
	 * Returns a Flux, that will produce a list of two random dates, every 0.1s
	 */
	@Override
	public Flux<Optional<List<Date>>> subscribeToAListOfScalars(DataFetchingEnvironment dataFetchingEnvironment) {
		logger.debug("Executing subscription subscribeToAListOfScalars()");

		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.1 second
				.map((l) -> Optional.ofNullable(dataGenerator.generateInstanceList(Date.class, 2)));
	}

	@Override
	public Flux<Optional<String>> _if(DataFetchingEnvironment dataFetchingEnvironment) {
		logger.debug("Executing subscription _if()");

		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.1 second
				.map((l) -> Optional.ofNullable("a value for _if"));
	}

	@Override
	public Flux<Optional<String>> _implements(DataFetchingEnvironment dataFetchingEnvironment) {
		logger.debug("Executing subscription _implements()");

		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.1 second
				.map((l) -> Optional.ofNullable("a value for _implements"));
	}

	@Override
	public Flux<Optional<SEP_EnumWithReservedJavaKeywordAsValues_SES>> enumWithReservedJavaKeywordAsValues(
			DataFetchingEnvironment dataFetchingEnvironment) {
		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.1 second
				.map((l) -> {
					if (l % 2 == 0)
						return Optional.of(SEP_EnumWithReservedJavaKeywordAsValues_SES._instanceof);
					else
						return Optional.empty();
				});
	}

	@Override
	public Flux<Optional<List<SEP_EnumWithReservedJavaKeywordAsValues_SES>>> listOfEnumWithReservedJavaKeywordAsValues(
			DataFetchingEnvironment dataFetchingEnvironment) {
		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.1 second
				.map((l) -> {
					if (l % 2 == 0)
						return Optional.of(//
								Arrays.asList(SEP_EnumWithReservedJavaKeywordAsValues_SES._int,
										SEP_EnumWithReservedJavaKeywordAsValues_SES._interface,
										SEP_EnumWithReservedJavaKeywordAsValues_SES._long, //
										null));
					else
						return Optional.empty();
				});
	}

	@Override
	public Flux<Optional<SEP_EnumWithReservedJavaKeywordAsValues_SES>> returnEnum(
			DataFetchingEnvironment dataFetchingEnvironment) {
		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.1 second
				.map((l) -> Optional.ofNullable(null));
	}

	@Override
	public Flux<SEP_EnumWithReservedJavaKeywordAsValues_SES> returnMandatoryEnum(
			DataFetchingEnvironment dataFetchingEnvironment) {
		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.1 second
				.map((l) -> SEP_EnumWithReservedJavaKeywordAsValues_SES._assert);
	}

	@Override
	public Flux<Optional<List<SEP_EnumWithReservedJavaKeywordAsValues_SES>>> returnListOfEnums(
			DataFetchingEnvironment dataFetchingEnvironment) {
		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.1 second
				.map((l) -> Optional.ofNullable(null));
	}

	@Override
	public Flux<Optional<List<SEP_EnumWithReservedJavaKeywordAsValues_SES>>> returnListOfMandatoryEnums(
			DataFetchingEnvironment dataFetchingEnvironment) {
		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.1 second
				.map((l) -> Optional.ofNullable(null));
	}

	@Override
	public Flux<Optional<List<List<SEP_EnumWithReservedJavaKeywordAsValues_SES>>>> returnListOfListOfEnums(
			DataFetchingEnvironment dataFetchingEnvironment) {
		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.1 second
				.map((l) -> Optional.of(//
						Arrays.asList(//
								Arrays.asList(SEP_EnumWithReservedJavaKeywordAsValues_SES._boolean, null,
										SEP_EnumWithReservedJavaKeywordAsValues_SES._break), //
								null, //
								Arrays.asList(SEP_EnumWithReservedJavaKeywordAsValues_SES._default, null,
										SEP_EnumWithReservedJavaKeywordAsValues_SES._implements))));
	}

	@Override
	public Flux<List<SEP_EnumWithReservedJavaKeywordAsValues_SES>> returnMandatoryListOfEnums(
			DataFetchingEnvironment dataFetchingEnvironment) {
		logger.debug("Executing subscription enumWithReservedJavaKeywordAsValues()");

		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.1 second
				.map((l) -> Arrays.asList(SEP_EnumWithReservedJavaKeywordAsValues_SES._boolean, null,
						SEP_EnumWithReservedJavaKeywordAsValues_SES._break));
	}

	@Override
	public Flux<List<SEP_EnumWithReservedJavaKeywordAsValues_SES>> returnMandatoryListOfMandatoryEnums(
			DataFetchingEnvironment dataFetchingEnvironment) {
		logger.debug("Executing subscription listOfEnumWithReservedJavaKeywordAsValues()");

		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.1 second
				.map((l) -> Arrays.asList(//
						SEP_EnumWithReservedJavaKeywordAsValues_SES._byte,
						SEP_EnumWithReservedJavaKeywordAsValues_SES._case));
	}

	@Override
	public Publisher<Optional<String>> _null(DataFetchingEnvironment dataFetchingEnvironment) {
		// This method is not implemented (not used in internal tests)
		return null;
	}

}
