This files describes the changes that have been brought to the generated code, and their possible impacts on existing project that uses ths graphql-generator plugin

# Done

* Report update on these templates:
    * GraphQLRequest.java
    * client_query_mutation_executor.vm.java (now uses the Spring `graphQlClient` bean)
    * client_query_mutation_subscription_rootResponse.vm.java
    * client_subscription_executor.vm.java (now uses the Spring `graphQlClient` bean)
    * client_spring_configuration.vm.java (is now as spring auto-configuration class)


# TODO

List of things that must or should be done before the first release:
* Rename httpGraphQLClient into queryMutationGraphQLClient, and webSocketGraphQLClient into subscriptionGraphQLClient
* Check the tests in allGraphQlCases client
* In the allGraphQLCases sample: update the non-spring app, to demonstrate how to use the plugin to embed a non-spring app
* Simplify the component scan paths (by using the auto configuration class)
* Move the code that is not user-specific from GraphQLTransportWSIT to the relevant class (the auto-configuration ?)
* Remove ObjectResponse
* Use spring.factories to define the autoconfiguration classes. The merge several spring.factories in one must be checked again
* Document dependencies for OAuth (cf allGraphQlCases-client project)
* Rename GraphQLJavaGeneratorAutoConfiguration to GraphQLJavaGeneratorConfiguration
* Report the @JsonIgnore around line 225 on object_content.bm
* Report changes of the QueryExecutor and MutationExecutor
* Should the `SubscriptionClient` interface and the `SubscriptionClientReactiveImpl` class by kept or removed ?
    * The main parameter would be to be blocking (as now) or non blocking
    * If kept, `SubscriptionClientReactiveImpl` should be renamed to `SubscriptionClientSpringGrapQLImpl`
* Should the XxxRootResponse classes not be generated any more ?   (check generateDeprecatedRequestResponse confif parameter)
    * Then, the `AbstractGraphQLRequest`'s methods `getQueryContext()`, `getMutationContext()` and `getSubscriptionContext()` should also be removed. There may be a big impact on the request generation.
* Create a new exception (that inherits from GraphQLRequestExecutionException), that indicates that the exception occurred on server side
* Check to use AutoConfiguration defined by annotation (not by META-INF file)
* Check alias in GraphQL response:
    * For query or mutation
    * fro subscription
* Check Subscription that returns one of these (to check the `field.getValue()` method):
    * scalars (or custom scalars)
    * list
    * object
* Optimize the `GraphQLObjectMapper.treeToValue(Map, class)` and `GraphQLObjectMapper.treeToValue(List, class)`
* Remove the `GraphQLRepositoryInvocationHandlerFirstTest` class, as soon as this use case (GraphQLRepository in non spring app) is confirmed to be useless.<BR/>
    * Then merge `AbstractGraphQLRepositoryInvocationHandlerTest` and `GraphQLRepositoryInvocationHandlerSecondTest`
* Try to remove the byte-buddy and commons-text dependencies

# To Document

* `WebClient` is now the minimal one. You can override it, in order to manage specific things, like:
    * `CodecCustomizer`
    * `HttpClient`
    * `ServerOAuth2AuthorizedClientExchangeFilterFunction`
    * _(or make it no more minimal, like before?)
For instance:
```java
	// AllGraphQLCases is the value of the springBeanSuffix plugin parameter
	// If you attack only one GraphQL server, this parameter may remain unset (that is: an empty string)
	@Bean
	@Primary
	ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases(
			ReactiveClientRegistrationRepository clientRegistrations) {
		ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
				clientRegistrations, new UnAuthenticatedServerOAuth2AuthorizedClientRepository());
		oauth.setDefaultClientRegistrationId("provider_test");
		return oauth;
	}

	@Bean
	@Primary
	public WebClient webClientAllGraphQLCases(String graphqlEndpointAllGraphQLCases, //
			CodecCustomizer defaultCodecCustomizer, //
			@Autowired(required = false) @Qualifier("httpClientAllGraphQLCases") HttpClient httpClientAllGraphQLCases,
			@Autowired(required = false) @Qualifier("serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases") ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases) {
		return WebClient.builder()//
				.baseUrl(graphqlEndpointAllGraphQLCases)//
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultUriVariables(Collections.singletonMap("url", graphqlEndpointAllGraphQLCases))
				.filter(serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases)//
				.build();
	}
```
* `Executors` can now only be retrieved as Spring bean. The `javax.ws.rs.client.Client` is no more accepted.
* Protocol choice for GraphQL queries and mutations: the new `queryMutationExecutionProtocol` plugin parameter allows to choose between http (default) and webSocket.
* OAuth: it is now supported by the standard Spring Boot parameters.
    * TODO : update the OAuth document (no more need of the `ServerOAuth2AuthorizedClientExchangeFilterFunction`)
* The `GraphQLConfiguration` class has been removed. It could have been used to manage GraphQL configuration for non Spring apps, based on the Jersey http client. The support for these apps has been removed, as the generated code is based on Spring
    * An impact is the use of the `@SpringBootApplication` annotation, on the main app class. The `GraphqlClientUtils` should be used instead, like this:
```java
@SpringBootApplication(scanBasePackageClasses = { SpringMain.class, GraphqlClientUtils.class, QueryExecutor.class })
public class SpringMain implements CommandLineRunner {
	... Do something that uses GraphQL
}
```
* (must check the doc here: it is quite confuse in this point) The `GraphQLRequest` class is used to execute full requests. It is now suffixed by the schema suffix defined in the `springBeanSuffix` defined in the plugin configuration, which is an empty string by default.
* In 1.x releases of the plugin the GraphQL endpoint's path is configured by the `graphql.url` entry in the Spring configuration file (application.yml or application.properties)
    * [for servers only] In the 2.x releases, this configuration is manager by spring. So the relevant configuration entry is `spring.graphql.path` in the Spring configuration file. Its default value is `/graphql`
    * [for client only] As there seems to be no way to define two GraphQL server urls in Spring GraphQL yet, the configuration entry remains `graphql.endpointXXXXXX.url`, where XXXXXX is the suffix defined in your pom.xml or gradle.plugin (it may be undefined, and is then empty)
    * Note: take care that Spring's properties ends with path, whereas the client is an url.


# Query and Mutation

The `GraphQLRequestExecutionException` has been updated. It now contains a `getErrors()` method, that allows to retrieve the list of errors returned by the GraphQL server.

# Subscription

The `SubscriptionClient` now contains only the `unsubscribe()` method. The `getSession()` (that allows to retrieve the `WebSocketSession` has been removed, as all the web socket management is now done by spring-graphql)

The `SubscriptionCallback` interface is unchanged. But its `onError(Throwable)` method is now called with a `GraphQLRequestExecutionException`: its `getErrors()` method allows to retrieve the list of errors returned by the GraphQL server.