Here are the next tasks listed, as a TODO list:


## TODO list for the current version
* Check usage of scalar extension
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
* Remove the query/mutation/subscription Response type (currently deprecated)
* copyRuntimeSources: false should be the default value (change to be done in the tutorial and the client-dependency)
* separateUtilityClasses: true should be the default value
* Add a generateDeprecatedRequestResponse plugin parameter. Default value to true (no more XxxxResponse would be generated). With a value of true, the XxxxResponse would still be generated for compatibility with old code.



Investigate DTO for database mapping (done in the Gradle and Maven tutorials):
https://stackoverflow.com/questions/60456804/how-to-use-graphql-with-jpa-if-schema-is-different-to-database-structure
https://stackoverflow.com/questions/58801227/graphql-tools-map-entity-type-to-graphql-type/58809449#58809449


Tutorials:
- https://www.howtographql.com/
- dev zone

