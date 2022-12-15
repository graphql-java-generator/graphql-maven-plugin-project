Here are the next tasks listed, as a TODO list:


## TODO list for the current version
* Test requests with string that contains special characters (\n, \r, \t, "...)
* #164: Descriptions from the schema should be included in the generated code if they exist
    * In the generated classes the comments could be included to describe the different fields correctly instead of using "Parameter for the mapsAcqr field of query_root, as defined in the GraphQL schema" description string.
    * All comments are loaded in Description field and no longer in comment (graphql.language.Comment) fields when using java schema parser (graphql/graphql-js#927 and graphql/graphql-spec#420).
    * Update of all call to setComments in DocumentParser to load Descriptions instead of comments. Allow descriptions to be acessible in template files. Add descriptions to InputParameters if exist to be acessible to templaters.
* Document in the Client FAQ how to retrieve the extensions response's values
* Tutorial: add the documentation about the application.yml file
    * Especially: `spring.main.web-application-type = none`
* Execute FieldTest.test_Issue1114_checkGenerateCode() (in plugin-locic, com.graphql_java_generator.plugin.language)
* Remove Deprecated getStringContentXxx methods in InputParameter
* Subscription: the client remain active after a `Connection refused` (even if the main thread stops)
* When using Web Sockets for the graphql-transport-ws protocol, the Web Socket can be tested with the Ping/Pong messages
* When using Web Sockets, it should be closed when the last subscription is unsubscribed. Issues :
    * Be sure that no subscription is starting at the same time (probability is low, but...)
    * update the RequestExecutionSpringReactiveImpl.webSocketHandler ?  Or mark the webSocketHandler as completed ?
    * A way could be that the webSocketHandler marks itself as completed (in a synchronized method), and closes the session, so that RequestExecutionSpringReactiveImpl knows that it needs to open a new one.
* add a _HowTo compile page_ on the wiki (to build the plugin project, Java 9 or latter is needed, even if the built plugin is compatible with Java 8)
* Check if spring-boot-starter-security is really needed. It should be added when a project needs OAuth2.
* @EnableGraphQLRepositories: replace the string (that contains the package name) by a class (so that when changing a package name, the code is still valid)
* Check comment of the executor method. The line below is wrong:
    * the request contains the full string that <B><U>follows</U></B> the query/mutation/subscription keyword.<BR/>
* Use JWT in the OAuth use case, to speed up the tests
* Add an option to add the Serializable interface for generated POJOs
* [Done in the plugin, waiting for the graphql PR acceptance] Publish a PR to have a per request cache, in graphql-java-spring 
    * Done. Waiting for the PR to be accepted (and then a new release)
    * In the meantime: the graphql-java-spring is forked within the graphql-maven-plugin project
* Remove the SubscriptionClientWebSocket class
* Remove the useless interface  com.graphql_java_generator.client.response.RootResponse (from the runtime)
* Allow to control the list of schema files, and their order (necessary to properly manage the extend keyword)
* Add a description of the GraphQL mojo
* Waiting for [issue 2055](https://github.com/graphql-java/graphql-java/issues/2055) to be solved. Some test cases can then be run again (see the allGraphQLCases.graphqls file)
* Change the JsonResponseWrapper as an interface, implemented by the XxxRootResponse classes
* [server side] Check graphql-java correction for issue 1844 (Directive values of type 'EnumValue' are not supported yet), waiting for graphql-java v16 release
* enum values may currently not be a java keyword (seems to be a graphql-java limitation). issue to raise in the graphql-java project
* Check compatibility with the schema.public.graphqls (available in the plugin logic test resources folder)
* Document generateJPAAnnotation 

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

