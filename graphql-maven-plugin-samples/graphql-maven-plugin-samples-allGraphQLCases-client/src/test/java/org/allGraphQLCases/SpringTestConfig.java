package org.allGraphQLCases;

import java.net.URI;
import java.util.Collections;

import org.allGraphQLCases.client.util.MyQueryTypeExecutorAllGraphQLCases;
import org.allGraphQLCases.client2.MyQueryTypeExecutorAllGraphQLCases2;
import org.allGraphQLCases.demo.impl.PartialDirectQueries;
import org.forum.client.QueryExecutorForum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.graphql.client.ClientGraphQlRequest;
import org.springframework.graphql.client.ClientGraphQlResponse;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.graphql.client.GraphQlClientInterceptor;
import org.springframework.graphql.client.WebSocketGraphQlClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;

import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.client.OAuthTokenExtractor;
import com.graphql_java_generator.client.SpringContextBean;
import com.graphql_java_generator.client.graphqlrepository.EnableGraphQLRepositories;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Configuration
@SpringBootApplication
@ComponentScan(basePackageClasses = { GraphqlClientUtils.class, MyQueryTypeExecutorAllGraphQLCases.class,
		MyQueryTypeExecutorAllGraphQLCases2.class, QueryExecutorForum.class, PartialDirectQueries.class }, //
		excludeFilters = { //
				@Filter(type = FilterType.REGEX, pattern = "/SpringConfig"),
				@Filter(type = FilterType.REGEX, pattern = "/Main") })
@PropertySource("classpath:/application.properties")
@EnableGraphQLRepositories({ "org.allGraphQLCases.demo.impl", "org.allGraphQLCases.graphqlrepositories",
		"org.allGraphQLCases.subscription.graphqlrepository", "org.allGraphQLCases.two_graphql_servers" })
public class SpringTestConfig {

	private static Logger logger = LoggerFactory.getLogger(SpringTestConfig.class);
	private static Logger loggerBeanPostProcessor = LoggerFactory.getLogger("BeanPostProcessor");

	@Autowired
	ApplicationContext applicationContext;

	/** MyInterceptor allows to check the Subscription data that will be sent back to the client */
	static class MyInterceptor implements GraphQlClientInterceptor {
		@SuppressWarnings("hiding")
		private static Logger logger = LoggerFactory.getLogger(MyInterceptor.class);
		final private String beanSuffix;

		public MyInterceptor(String beanSuffix) {
			this.beanSuffix = beanSuffix;
		}

		@Override
		public Flux<ClientGraphQlResponse> interceptSubscription(ClientGraphQlRequest request,
				SubscriptionChain chain) {
			return chain.next(request).doOnNext(response -> this.interceptionSubscriptionResponse(response));
		}

		/** Interception of each message received on subscription */
		public void interceptionSubscriptionResponse(ClientGraphQlResponse response) {
			if (response.isValid()) {
				logger.debug("[subscription interception] Received a valid response for '{}': {}", beanSuffix,
						response.getData());
			} else {
				logger.debug("[subscription interception] Received a non valid response for '{}': {}", beanSuffix,
						response.getErrors());
			}
		}
	}

	/**
	 * Insures that the {@link SpringContextBean} bean stores the right Spring {@link ApplicationContext} in its static
	 * applicationContext field.<br/>
	 * These JUnit tests may build more than one Spring {@link ApplicationContext}. The plugin's runtime uses the
	 * {@link SpringContextBean} that started at initialization, and stores the Spring {@link ApplicationContext} in its
	 * static applicationContext field.<br/>
	 * The issue in these JUnit tests that create several application contexts is that is creates a mess, as this static
	 * field can contain only one Spring {@link ApplicationContext}: the last one to be created.
	 */
	@Bean
	BeanPostProcessor SpringContextSetterBeanPostProcessor() {
		return new BeanPostProcessor() {
			@Override
			public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
				String classname = bean.getClass().getName();
				if (classname.startsWith("org.allGraphQLCases") || classname.endsWith("IT")) {
					SpringContextBean.setApplicationContext(applicationContext);
				}
				return bean;
			}
		};
	}

	@Bean
	BeanPostProcessor logBeanPostProcessor() {
		return new BeanPostProcessor() {
			@Override
			public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
				if (loggerBeanPostProcessor.isDebugEnabled()) {
					String classname = bean.getClass().getName();
					if (classname.startsWith("org.allGraphQLCases")
							|| classname.startsWith("com.graphql_java_generator")
							|| GraphQlClient.class.isAssignableFrom(bean.getClass())) {
						loggerBeanPostProcessor.debug("Before postProcess init of {} (@{}) - {}", beanName, bean,
								classname);
					}
				}
				return bean;
			}
		};
	}

	@Bean
	@Primary
	ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases(
			ReactiveClientRegistrationRepository clientRegistrations) {
		InMemoryReactiveOAuth2AuthorizedClientService clientService = new InMemoryReactiveOAuth2AuthorizedClientService(
				clientRegistrations);
		AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
				clientRegistrations, clientService);
		ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
				authorizedClientManager);
		oauth.setDefaultClientRegistrationId("provider_test"); // Defines our custom OAuth2 provider
		return oauth;
	}

	@Bean
	@Primary
	ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunctionForum(
			ReactiveClientRegistrationRepository clientRegistrations) {
		InMemoryReactiveOAuth2AuthorizedClientService clientService = new InMemoryReactiveOAuth2AuthorizedClientService(
				clientRegistrations);
		AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
				clientRegistrations, clientService);
		ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
				authorizedClientManager);
		oauth.setDefaultClientRegistrationId("provider_test"); // Defines our custom OAuth2 provider
		return oauth;
	}

	@Bean
	@Primary
	public WebClient webClientAllGraphQLCases(String graphqlEndpointAllGraphQLCases, //
			CodecCustomizer defaultCodecCustomizer, //
			@Autowired(required = false) @Qualifier("httpClientAllGraphQLCases") HttpClient httpClientAllGraphQLCases,
			@Autowired(required = false) @Qualifier("serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases") ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases) {

		// This raises the volume of the in-memory buffer to manager large server responses
		ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
				.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024 * 10)).build();

		return WebClient.builder()//
				.baseUrl(graphqlEndpointAllGraphQLCases)//
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultUriVariables(Collections.singletonMap("url", graphqlEndpointAllGraphQLCases))
				.filter(serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases)//
				.exchangeStrategies(exchangeStrategies)//
				.build();
	}

	@Bean
	@Primary
	public WebClient webClientForum(String graphqlEndpointForum, //
			CodecCustomizer defaultCodecCustomizer, //
			@Autowired(required = false) @Qualifier("httpClientForum") HttpClient httpClientForum,
			@Autowired(required = false) @Qualifier("serverOAuth2AuthorizedClientExchangeFilterFunctionForum") ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunctionForum) {

		// This raises the volume of the in-memory buffer to manager large server responses
		ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
				.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024 * 10)).build();

		return WebClient.builder()//
				.baseUrl(graphqlEndpointForum)//
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultUriVariables(Collections.singletonMap("url", graphqlEndpointForum))
				.filter(serverOAuth2AuthorizedClientExchangeFilterFunctionForum)//
				.exchangeStrategies(exchangeStrategies)//
				.build();
	}

	/**
	 * This overrides the default one provided by the plugin. It adds the OAuth token generation, thanks to the
	 * 
	 * @param graphqlEndpointAllGraphQLCases
	 *            The endpoint that is protected by OAuth
	 * @param serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases
	 *            the {@link ExchangeFilterFunction} that adds the OAuth capability to the http {@link WebClient} for
	 *            this endpoint. It used by the {@link OAuthTokenExtractor} to retrieve a OAuth bearer token, that will
	 *            be used for WebSocket connections.
	 * @return
	 */
	@Bean
	@Qualifier("AllGraphQLCases")
	@Primary
	GraphQlClient webSocketGraphQlClientAllGraphQLCases(String graphqlEndpointAllGraphQLCases,
			@Qualifier("serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases") ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases) {

		logger.debug("Creating SpringTestConfig webSocketGraphQlClientAllGraphQLCases");
		loggerBeanPostProcessor.debug("Creating SpringTestConfig webSocketGraphQlClientAllGraphQLCases");

		// Creation of an OAuthTokenExtractor based on this OAuth ExchangeFilterFunction
		OAuthTokenExtractor oAuthTokenExtractor = new OAuthTokenExtractor(
				serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases);

		// The OAuth token must be checked at each execution. The spring WebSocketClient doesn't provide any way to
		// update the headers just before the request execution. So we override the WebSocketClient to add this
		// capability:
		ReactorNettyWebSocketClient client = new ReactorNettyWebSocketClient() {
			@Override
			public Mono<Void> execute(URI url, HttpHeaders requestHeaders, WebSocketHandler handler) {
				// Let's retrieve the valid OAuth token
				String authorizationHeaderValue = oAuthTokenExtractor.getAuthorizationHeaderValue();

				// Then we apply it to the given headers
				if (requestHeaders == null) {
					requestHeaders = new HttpHeaders();
				} else {
					requestHeaders.remove(OAuthTokenExtractor.AUTHORIZATION_HEADER_NAME);
				}
				logger.trace("Adding the bearer token to the Subscription websocket request");
				requestHeaders.add(OAuthTokenExtractor.AUTHORIZATION_HEADER_NAME, authorizationHeaderValue);

				// Then, let's execute the Web Socket request
				return super.execute(url, requestHeaders, handler);
			}
		};

		return WebSocketGraphQlClient.builder(graphqlEndpointAllGraphQLCases, client)
				.interceptor(new MyInterceptor("AllGraphQLCases")).build();
	}

	/**
	 * This overrides the default one provided by the plugin. It adds the OAuth token generation, thanks to the
	 * 
	 * @param graphqlEndpointForum
	 *            The endpoint that is protected by OAuth
	 * @param serverOAuth2AuthorizedClientExchangeFilterFunctionForum
	 *            the {@link ExchangeFilterFunction} that adds the OAuth capability to the http {@link WebClient} for
	 *            this endpoint. It used by the {@link OAuthTokenExtractor} to retrieve a OAuth bearer token, that will
	 *            be used for WebSocket connections.
	 * @return
	 */
	@Bean
	@Qualifier("Forum")
	@Primary
	GraphQlClient webSocketGraphQlClientForum(String graphqlEndpointForum,
			@Qualifier("serverOAuth2AuthorizedClientExchangeFilterFunctionForum") ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunctionForum) {

		logger.debug("Creating SpringTestConfig webSocketGraphQlClientAllGraphQLCases");
		loggerBeanPostProcessor.debug("Creating SpringTestConfig webSocketGraphQlClientAllGraphQLCases");

		// Creation of an OAuthTokenExtractor based on this OAuth ExchangeFilterFunction
		OAuthTokenExtractor oAuthTokenExtractor = new OAuthTokenExtractor(
				serverOAuth2AuthorizedClientExchangeFilterFunctionForum);

		// The OAuth token must be checked at each execution. The spring WebSocketClient doesn't provide any way to
		// update the headers just before the request execution. So we override the WebSocketClient to add this
		// capability:
		ReactorNettyWebSocketClient client = new ReactorNettyWebSocketClient() {
			@Override
			public Mono<Void> execute(URI url, HttpHeaders requestHeaders, WebSocketHandler handler) {
				// Let's retrieve the valid OAuth token
				String authorizationHeaderValue = oAuthTokenExtractor.getAuthorizationHeaderValue();

				// Then we apply it to the given headers
				if (requestHeaders == null) {
					requestHeaders = new HttpHeaders();
				} else {
					requestHeaders.remove(OAuthTokenExtractor.AUTHORIZATION_HEADER_NAME);
				}
				logger.trace("Adding the bearer token to the Subscription websocket request");
				requestHeaders.add(OAuthTokenExtractor.AUTHORIZATION_HEADER_NAME, authorizationHeaderValue);

				// Then, let's execute the Web Socket request
				return super.execute(url, requestHeaders, handler);
			}
		};

		return WebSocketGraphQlClient.builder(graphqlEndpointForum, client).interceptor(new MyInterceptor("Forum"))
				.build();
	}
}
