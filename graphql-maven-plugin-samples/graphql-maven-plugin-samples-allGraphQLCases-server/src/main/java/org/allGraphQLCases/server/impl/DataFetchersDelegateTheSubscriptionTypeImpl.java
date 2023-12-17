/**
 * 
 */
package org.allGraphQLCases.server.impl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.allGraphQLCases.server.DataFetchersDelegateTheSubscriptionType;
import org.allGraphQLCases.server.SEP_EnumWithReservedJavaKeywordAsValues_SES;
import org.allGraphQLCases.server.SEP_Episode_SES;
import org.allGraphQLCases.server.SINP_AllFieldCasesInput_SINS;
import org.allGraphQLCases.server.SINP_AllFieldCasesWithoutIdSubtypeInput_SINS;
import org.allGraphQLCases.server.SINP_InputWithJson_SINS;
import org.allGraphQLCases.server.SINP_InputWithObject_SINS;
import org.allGraphQLCases.server.SINP_SubscriptionTestParam_SINS;
import org.allGraphQLCases.server.STP_AllFieldCasesWithoutIdSubtype_STS;
import org.allGraphQLCases.server.STP_AllFieldCases_STS;
import org.allGraphQLCases.server.STP_Human_STS;
import org.allGraphQLCases.server.STP_TypeWithJson_STS;
import org.allGraphQLCases.server.STP_TypeWithObject_STS;
import org.allGraphQLCases.server.config.GraphQlException;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import graphql.language.OperationDefinition;
import graphql.schema.DataFetchingEnvironment;
import reactor.core.publisher.Flux;

/**
 * @author etienne-sf
 *
 */
@Component
public class DataFetchersDelegateTheSubscriptionTypeImpl implements DataFetchersDelegateTheSubscriptionType {

	static Logger logger = LoggerFactory.getLogger(DataFetchersDelegateTheSubscriptionTypeImpl.class);

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
				logger.debug("The subscription 'subscribeNewHumanForEpisode' is now active"); //$NON-NLS-1$
				DataFetchersDelegateTheSubscriptionTypeImpl.this.subscribedOnSubscribeNewHumanForEpisode = true;
			}
		};

		Runnable onCancel = new Runnable() {
			@Override
			public void run() {
				logger.debug("The subscription 'subscribeNewHumanForEpisode' is now canceled"); //$NON-NLS-1$
				DataFetchersDelegateTheSubscriptionTypeImpl.this.subscribedOnSubscribeNewHumanForEpisode = false;
			}
		};

		Runnable onTerminate = new Runnable() {
			@Override
			public void run() {
				logger.debug("The subscription 'subscribeNewHumanForEpisode' is now terminated"); //$NON-NLS-1$
				DataFetchersDelegateTheSubscriptionTypeImpl.this.subscribedOnSubscribeNewHumanForEpisode = false;
			}
		};

		Consumer<Throwable> onError = new Consumer<Throwable>() {
			@Override
			public void accept(Throwable t) {
				logger.debug("The subscription 'subscribeNewHumanForEpisode' had an error: {}-{}", //$NON-NLS-1$
						t.getClass().getName(), t.getMessage());
				DataFetchersDelegateTheSubscriptionTypeImpl.this.subscribedOnSubscribeNewHumanForEpisode = false;
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
					STP_Human_STS h = this.dataGenerator.generateInstance(STP_Human_STS.class);
					if (!h.getAppearsIn().contains(SEP_Episode_SES)) {
						h.getAppearsIn().add(SEP_Episode_SES);
					}
					h.setId(new UUID(0, l));
					logger.trace("subscribeNewHumanForEpisode [active={}] Sending this human: {}", //$NON-NLS-1$
							this.subscribedOnSubscribeNewHumanForEpisode, h);
					return h;
				});
	}

	@Override
	public Flux<List<Integer>> subscribeToAList(DataFetchingEnvironment dataFetchingEnvironment) {
		logger.debug("Executing subscription subscribeToAList()"); //$NON-NLS-1$
		// The Flux class, from Spring reactive, implements the Publisher interface.
		// Let's return one list of integer, every 0.1 second
		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.2 second
				.map((l) -> {
					// This message is a list of two integers
					return Arrays.asList(l.intValue(), 2 * l.intValue());
				}).doOnEach(lst -> {
					if (logger.isTraceEnabled()) {
						String separator = ""; //$NON-NLS-1$
						StringBuilder sb = new StringBuilder();
						for (int i : lst.get()) {
							sb.append(separator).append(i);
							separator = ","; //$NON-NLS-1$
						}
						logger.trace("Sending this list: [{}]", sb); //$NON-NLS-1$
					}
				});
	}

	@Override
	public Flux<Date> issue53(DataFetchingEnvironment dataFetchingEnvironment, Date date) {
		logger.debug("Executing subscription issue53({})", date); //$NON-NLS-1$
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
		logger.debug("Executing subscription subscriptionTest({})", param); //$NON-NLS-1$

		if (param.getErrorOnSubscription()) {
			// The client asked that an exception is thrown now
			throw new GraphQlException("Oups, the subscriber asked for an error during the subscription"); //$NON-NLS-1$
		} else if (param.getErrorOnNext()) {
			return Flux//
					.interval(Duration.ofMillis(100))// A message every 0.1 second
					.map((l) -> {
						boolean b = true;
						if (b)
							throw new GraphQlException("Oups, the subscriber asked for an error for each next message"); //$NON-NLS-1$
						// The line below will never get executed. But doing this prevents a compilation error !
						return "won't go there"; //$NON-NLS-1$
					});
		} else if (param.getCompleteAfterFirstNotification()) {
			return Flux.just("The subscriber asked for a complete after the first notification"); //$NON-NLS-1$
		} else if (param.getCloseWebSocketBeforeFirstNotification()) {
			return Flux//
					.interval(Duration.ofMillis(100))// A message every 0.1 second
					.map((l) -> {
						boolean b = true;
						if (b)
							throw new GraphQlException(
									"Oups, the subscriber asked that the web socket get disconnected before the first notification"); //$NON-NLS-1$
						// The line below will never get executed. But doing this prevents a compilation error !
						return "won't go there"; //$NON-NLS-1$
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
		logger.debug("Executing subscription allGraphQLCasesInput({})", input); //$NON-NLS-1$

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
		logger.debug("Executing subscription allGraphQLCasesParam(id={}, name={}, age={}...)", id, name, age); //$NON-NLS-1$

		STP_AllFieldCases_STS ret = new STP_AllFieldCases_STS();
		ret.setId(UUID.fromString(id));
		ret.setName(name);
		ret.setAge(age);
		ret.setDate(date);
		ret.setDates(dates);
		ret.setMatrix(matrix);
		ret.setAliases(Arrays.asList("an alias")); //$NON-NLS-1$
		ret.setPlanets(Arrays.asList("planet 1", "planet 2")); //$NON-NLS-1$ //$NON-NLS-2$

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
		logger.debug("Executing subscription subscriptionWithNullResponse()"); //$NON-NLS-1$

		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.2 second
				.map((l) -> {
					logger.trace("Sending a message in 'subscriptionWithNullResponse'"); //$NON-NLS-1$
					return Optional.ofNullable(null);
				});
	}

	/**
	 * Returns a Flux, that will produce a list of two random dates, every 0.1s
	 */
	@Override
	public Flux<Optional<List<Date>>> subscribeToAListOfScalars(DataFetchingEnvironment dataFetchingEnvironment) {
		logger.debug("Executing subscription subscribeToAListOfScalars()"); //$NON-NLS-1$

		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.1 second
				.map((l) -> Optional.ofNullable(this.dataGenerator.generateInstanceList(Date.class, 2)));
	}

	@Override
	public Flux<Optional<String>> _if(DataFetchingEnvironment dataFetchingEnvironment) {
		logger.debug("Executing subscription _if()"); //$NON-NLS-1$

		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.1 second
				.map((l) -> Optional.ofNullable("a value for _if")); //$NON-NLS-1$
	}

	@Override
	public Flux<Optional<String>> _implements(DataFetchingEnvironment dataFetchingEnvironment) {
		logger.debug("Executing subscription _implements()"); //$NON-NLS-1$

		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.1 second
				.map((l) -> Optional.ofNullable("a value for _implements")); //$NON-NLS-1$
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
			DataFetchingEnvironment dataFetchingEnvironment,
			org.allGraphQLCases.server.SEP_EnumWithReservedJavaKeywordAsValues_SES _enum) {
		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.1 second
				.map((l) -> (_enum == null) ? SEP_EnumWithReservedJavaKeywordAsValues_SES._assert : _enum);
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
		logger.debug("Executing subscription enumWithReservedJavaKeywordAsValues()"); //$NON-NLS-1$

		return Flux//
				.interval(Duration.ofMillis(100))// A message every 0.1 second
				.map((l) -> Arrays.asList(SEP_EnumWithReservedJavaKeywordAsValues_SES._boolean, null,
						SEP_EnumWithReservedJavaKeywordAsValues_SES._break));
	}

	@Override
	public Flux<List<SEP_EnumWithReservedJavaKeywordAsValues_SES>> returnMandatoryListOfMandatoryEnums(
			DataFetchingEnvironment dataFetchingEnvironment) {
		logger.debug("Executing subscription listOfEnumWithReservedJavaKeywordAsValues()"); //$NON-NLS-1$

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

	@Override
	public Publisher<Optional<ObjectNode>> json(DataFetchingEnvironment dataFetchingEnvironment,
			com.fasterxml.jackson.databind.node.ObjectNode jsonParam) {
		try {
			ObjectNode json = //
					(jsonParam == null) ? //
							new ObjectMapper().readValue("{\"field1\":\"value1\", \"field2\":\"value2\"}",
									ObjectNode.class)//
							: jsonParam;
			return Flux//
					.interval(Duration.ofMillis(100))// A message every 0.1 second
					.map((l) -> Optional.of(json));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Publisher<Optional<List<ObjectNode>>> jsons(DataFetchingEnvironment dataFetchingEnvironment,
			List<com.fasterxml.jackson.databind.node.ObjectNode> jsonsParam) {
		try {
			Optional<List<ObjectNode>> jsons = (jsonsParam == null) ? //
					Optional.of(Arrays.asList(//
							new ObjectMapper().readValue("{\"field11\":\"value11\", \"field12\":[11,12]}",
									ObjectNode.class),
							new ObjectMapper().readValue("{\"field21\":\"value21\", \"field22\":[21,22]}",
									ObjectNode.class)))//
					: Optional.of(jsonsParam);
			return Flux//
					.interval(Duration.ofMillis(100))// A message every 0.1 second
					.map((l) -> jsons);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Publisher<List<STP_TypeWithJson_STS>> jsonsWithInput(DataFetchingEnvironment dataFetchingEnvironment,
			List<SINP_InputWithJson_SINS> input) {
		logger.debug("Executing subscription jsonsWithInput()");
		List<STP_TypeWithJson_STS> jsons;

		try {
			if (input == null) {
				ObjectNode json = new ObjectMapper()//
						.readValue(//
								"{\"field\":\"jsonsWithInput\",\"field2\":[1,2,3]}", //
								ObjectNode.class);
				STP_TypeWithJson_STS item = STP_TypeWithJson_STS.builder()//
						.withTest("jsonsWithInput")//
						.withDate(Calendar.getInstance().getTime())//
						.withLong(6789L)//
						.withBoolean(true)//
						.withEnum(SEP_Episode_SES.JEDI)//
						.withJson(json)//
						.withJsons(Arrays.asList(json, json))//
						.build();
				jsons = Arrays.asList(item, item);
			} else {
				jsons = input.stream()//
						.map(i -> {

							STP_TypeWithJson_STS item = new STP_TypeWithJson_STS();
							item.setTest(i.getTest());
							item.setDate(i.getDate());
							item.setLong(i.getLong());
							item.setBoolean(i.getBoolean());
							item.setEnum(i.getEnum());
							item.setJson(i.getJson());// this value will be reused by the TypeWithJson controller
							item.setJsons(i.getJsons());// this value will be reused by the TypeWithJson controller

							// Let's build the withArguments value, according to the received parameters for this field
							// Caution: this works properly only in the context of the integration tests
							item.setWithArguments(//
									DataFetchersDelegateMyQueryTypeImpl.buildWithArguments(//
											(OperationDefinition) dataFetchingEnvironment.getDocument().getDefinitions()
													.get(0)));

							return item;
						})//
						.collect(Collectors.toList());
			}

			return Flux//
					.interval(Duration.ofMillis(100))// A message every 0.1 second
					.map((l) -> jsons);

		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Publisher<List<STP_TypeWithObject_STS>> objectsWithInput(DataFetchingEnvironment dataFetchingEnvironment,
			List<SINP_InputWithObject_SINS> input) {
		logger.debug("Executing subscription jsonsWithInput()");
		List<STP_TypeWithObject_STS> jsons;

		try {
			if (input == null) {
				ObjectNode json = new ObjectMapper()//
						.readValue(//
								"{\"field\":\"jsonsWithInput\",\"field2\":[1,2,3]}", //
								ObjectNode.class);
				STP_TypeWithObject_STS item = STP_TypeWithObject_STS.builder()//
						.withTest("jsonsWithInput")//
						.withDate(Calendar.getInstance().getTime())//
						.withLong(6789L)//
						.withBoolean(true)//
						.withEnum(SEP_Episode_SES.JEDI)//
						.withObject(json)//
						.withObjects(Arrays.asList(json, json))//
						.build();
				jsons = Arrays.asList(item, item);
			} else {
				jsons = input.stream()//
						.map(item -> mapper.map(item, STP_TypeWithObject_STS.class))//
						.collect(Collectors.toList());
			}

			return Flux//
					.interval(Duration.ofMillis(100))// A message every 0.1 second
					.map((l) -> jsons);

		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

}
