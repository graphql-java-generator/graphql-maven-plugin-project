This files describes the changes that have been brought to the generated code, and their possible impacts on existing project that uses ths graphql-generator plugin

# Done

* Report update on these templates:
    * GraphQLRequest.java
    * client_query_mutation_executor.vm.java
    * client_query_mutation_subscription_rootResponse.vm.java
    * client_subscription_executor.vm.java
    * GraphQLJavaGeneratorAutoConfiguration


# TODO

List of things that must or should be done before the first release:
* `SpringConfiguration` must be renamed to a name with no possible collision, like `GraphQLJavaGeneratorAutoConfiguration`
* Remove the generation of `SpringConfiguration` (and the META-INF file)
* Report changes of the QueryExecutor and MutationExecutor
* Should the `SubscriptionClient` interface and the `SubscriptionClientReactiveImpl` class by kept or removed ?
    * The main parameter would be to be blocking (as now) or non blocking
    * If kept, `SubscriptionClientReactiveImpl` should be renamed to `SubscriptionClientSpringGrapQLImpl`
* Should the XxxRootResponse classes not be generated any more.
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

# To Document

* `WebClient` is now the minimal one. It must be overriden, to manage:
    * `CodecCustomizer`
    * `HttpClient`
    * `ServerOAuth2AuthorizedClientExchangeFilterFunction`
    * _(or make it no more minimal, like before?)


# Query and Mutation

The `GraphQLRequestExecutionException` has been updated. It now contains a `getErrors()` method, that allows to retrieve the list of errors returned by the GraphQL server.

# Subscription

The `SubscriptionClient` now contains only the `unsubscribe()` method. The `getSession()` (that allows to retrieve the `WebSocketSession` has been removed, as all the web socket management is now done by spring-graphql)

The `SubscriptionCallback` interface is unchanged. But its `onError(Throwable)` method is now called with a `GraphQLRequestExecutionException`: its `getErrors()` method allows to retrieve the list of errors returned by the GraphQL server.