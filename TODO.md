Here are the next tasks listed, as a TODO list:


## TODO list for the current version
* Manage [CVE-2021-44228](https://nvd.nist.gov/vuln/detail/CVE-2021-44228) : update to log4j >= 2.16.0 (last version as of 16 déc 2021)
* Issue [#114](https://github.com/graphql-java-generator/graphql-maven-plugin-project/issues/114)
* Remove getVariablesAsStringOld() (in Payload)
* In allGraphQLCasesClient: in this sample, the list of custom scalars defined for the Forum schema should not contain `Long` and `NonNegativeInt`. In this case, their definition is erased at runtime in the `CustomScalarRegistryImpl.customScalarRegistry`.
    * Workaround : define in the pom or gradle.build file, the full list of all scalars in the `<configuration>` for every GraphQL schema.
* Check wiki, to properly indicate that the `allGraphQLCases.graphqls` is located in the allGraphQLCases client project
* Remove Deprecated getStrincContentXxx in InputParameter
* Issue #105: it's no more possible to override the ExchangeFilterFunction
* Solve issue #103 (define the Velocity log file, toward the target folder)
* Subscription: the client remain active after a `Connection refused` (even if the main thread stops)
* Simplify the $generateJacksonStuff expression (directly use $configuration.generateJacksonAnnotations)
* Check of the [issue 9 on Gradle project](https://github.com/graphql-java-generator/graphql-gradle-plugin-project/issues/9): comments marked with `"` or `###` are ignored (but they also don't respect the GraphQL spec)
* When using Web Sockets for the graphql-transport-ws protocol, the Web Socket can be tested with the Ping/Pong messages
* When using Web Sockets, it should be closed when the last subscription is unsubscribed. Issues :
    * Be sure that no subscription is starting at the same time (probability is low, but...)
    * update the RequestExecutionSpringReactiveImpl.webSocketHandler ?  Or mark the webSocketHandler as completed ?
    * A way could be that the webSocketHandler marks itself as completed (in a synchronized method), and closes the session, so that RequestExecutionSpringReactiveImpl knows that it needs to open a new one.
* add a _HowTo compile page_ on the wiki (to build the plugin project, Java 9 or latter is needed, even if the built plugin is compatible with Java 8)
* The default name is Xxx, not XxxxType (Query versus QueryType)
* Remove the dependency to GSON (in the server runtime)
* The 'graphql-java-runtime.properties' exists two times, once for each schema (in generatedResources). So there is an issue when copying files
    * Sol1: generate in build/resources/main
    * Sol2: Give a try to Gradle 8.0
- But then, a `clean` may not re-generate the resource?
- What impact on maven
- A `clean` in eclipse removes the file
==> So it must be in  generates/resources 
* Test the default values from the extensions in the gradle task
* Check if spring-boot-starter-security is really needed. It should be added when a project needs OAuth2.
* @EnableGraphQLRepositories: replace the string (that contains the package name) by a class (so that when changing a package name, the code is still valid)
* Check comment of the executor method. The line below is wrong:
    * the request contains the full string that <B><U>follows</U></B> the query/mutation/subscription keyword.<BR/>
* Mark the skipGenerationIfSchemaHasNotChanged parameter as deprecated (no more used)
* Document in the Client FAQ how to retrieve the extensions response's values
* Use JWT in the OAuth use case, to speed up the tests
* Pass the test on the allGraphQLCases to being executed in parallel
* Add a test in the graphql-maven-plugin-samples-allGraphQLCases-pojo project
* Change the server Subscription implementation from Reactive Subject to Spring reactive
* Add an option to add the Serializable interface for generated POJOs
* [Done in the plugin, waiting for the graphql PR acceptance] Publish a PR to have a per request cache, in graphql-java-spring 
    * Done. Waiting for the PR to be accepted (and then a new release)
* [Almost done, there is still a glinch in server mode] Document how-to avoid the code generation (or find a better idea)
    * https://github.com/graphql-java-generator/graphql-maven-plugin-project/issues/69
    * When addRelayConnection is true, the _generated_schema.graphqls_ should be generated in the generated_resources instead of the classes folder
* @RelayConnection error when applied on a list: https://github.com/graphql-java-generator/graphql-maven-plugin-project/issues/62
    * Solved
* Remove the SubscriptionClientWebSocket class
* Check subscription against the Hasura setup (answer to issue 54) : https://hasura.io/
* Remove the useless interface  com.graphql_java_generator.client.response.RootResponse (from the runtime)
* Replace all the thrown RuntimeException by meaningfull exceptions
* Allow to control the list of schema files, and their order (necessary to properly manage the extend keyword)
* Add a description of the GraphQL mojo
* Waiting for [issue 2055](https://github.com/graphql-java/graphql-java/issues/2055) to be solved. Some test cases can then be run again (see the allGraphQLCases.graphqls file)
* Do a sample project based on the github GraphQL schema
* Allow to change the GraphQL server path (for query/mutation and for subscription)
* Change the JsonResponseWrapper as an interface, implemented by the XxxRootResponse classes
* Allow aliases for query/mutation fields
* [server side] Check graphql-java correction for issue 1844 (Directive values of type 'EnumValue' are not supported yet), waiting for graphql-java v16 release
* enum values may currently not be a java keyword (seems to be a graphql-java limitation). issue to raise in the graphql-java project
* Check compatibility with the schema.public.graphqls (available in the plugin logic test resources folder)
* Document generateJPAAnnotation 
* Stop generating SubscriptionTypeResponse and SubscriptionTypeRootResponse ?

## TODO List for 2.0 version:
* Rename the QueryExecutor (and its implementations) into RequestExecutor
* Remove the query/mutation/subscription Response type (currently deprecated)
* copyRuntimeSources: false should be the default value (change to be done in the tutorial and the client-dependency)
* separateUtilityClasses: true should be the default value
* Add a generateDeprecatedRequestResponse plugin parameter. Default value to true (no more XxxxResponse would be generated). With a value of true, the XxxxResponse would still be generated for compatibility with old code.
* Remove the `graphql-java-runtime`, and put the runtime in either the `graphql-java-server-dependencies` or `graphql-java-client-dependencies`


Investigate DTO for database mapping (done in the Gradle and Maven tutorials):
https://stackoverflow.com/questions/60456804/how-to-use-graphql-with-jpa-if-schema-is-different-to-database-structure
https://stackoverflow.com/questions/58801227/graphql-tools-map-entity-type-to-graphql-type/58809449#58809449


Tutorials:
- https://www.howtographql.com/
- dev zone

