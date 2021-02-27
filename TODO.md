Here are the next tasks listed, as a TODO list:


## TODO list for the current version
* Issue when two client subscribed to the same subscription
   https://github.com/graphql-java-generator/graphql-maven-plugin-project/issues/72
* [Waiting for an answer] Publish a PR to have a per request cache, in graphql-java-spring 
    * Done. Waiting for the PR to be accepted (and then a new release)
* Document how-to avoid the code generation (or find a better idea)
    * https://github.com/graphql-java-generator/graphql-maven-plugin-project/issues/69
* Add a control at runtime, that the runtime is the good version, as the plugin that generated the code.
* @RelayConnection error when applied on a list: https://github.com/graphql-java-generator/graphql-maven-plugin-project/issues/62
* When addRelayConnection is true, the _generated_schema.graphqls_ should be generated in the generated_resources instead of the classes folder
* Finish the job for the _generatePOJO_ goal/task.
    * The _GeneratePOJOConfiguration_ interface has been created, with some default value. But how to prevent these parameters to be displayed in the doc. _GeneratePOJOConfiguration_ should probably be the super interface for the _GenerateCodeCommonConfiguration_ (and not the reverse as currently).
    * Also check the _GenerateCodeDocumentParser.initScalarTypes_ method 
* Removed unused integration test in the plugin 
    * Done. But still to be checked: Remove exclusions in poms, like spring-boot-starter-logging
* Issue 65: add some more info when someone a request like one of those:
    * "{id name subObject(types: [TYPE1, TYPE2])}"   (the list should be an input parameter)
    * "{id name subObject(type: {some json})}"       (the json should be an input parameter)
* Remove the SubscriptionClientWebSocket class
* Check subscription against the Hasura setup (answer to issue 54) : https://hasura.io/
* Check the client dependencies (from all client samples)
* Add unit test in runtime, to confirm the use of the BatchLoader
* Check the WebClient with a default TLS configuration on a well known server
* Update the README with the XxxxExecutor classes
* Remove the useless interface  com.graphql_java_generator.client.response.RootResponse (from the runtime)
* Remove the WithDataLoader at the end of the DataFetcher names (no impact on the user's code)
* Make the Maven plugin generate its code in the graphql-maven-plugin folder (like the gradle plugin)
    * update the four tutorials
    * Check the documented default value
* Better document the BatchLoader (including the generateBatchLoaderEnvironment parameter)
* [WIP] Adding the relay connection capabilities (almost done, unit tests are Ok, remaining task: integration tests).
* Check the #0038 issue: how to update a OAuth token with a javax.ws.rs.client.Client 
	OAuth:"https://www.baeldung.com/jersey-sse-client-request-headers";//
	"https://www.baeldung.com/spring-webclient-oauth2#springsecurity-internals"//
	"https://manhtai.github.io/posts/spring-webclient-oauth2-client-credentials/"
* Switch to apache 2.0 licence
* Replace all the thrown RuntimeException by meaningfull exceptions
* Allow to control the list of schema files, and their order (necessary to properly manage the extend keyword)
* Add a description of the GraphQL mojo
* Waiting for [issue 2055](https://github.com/graphql-java/graphql-java/issues/2055) to be solved. Some test cases can then be run again (see the allGraphQLCases.graphqls file)
* Add the Tutorial for Subscription, on the Client side
* Analyze and response to issue #39 (@connection for relay)
* Do a sample project based on the github GraphQL schema
* Specifying an unknown template in the pom should raise an error
* Allow to change the GraphQL server path (for query/mutation and for subscription)
* Change the JsonResponseWrapper as an interface, implemented by the XxxRootResponse classes
* Allow aliases for query/mutation fields
* [server side] Check graphql-java correction for issue 1844 (Directive values of type 'EnumValue' are not supported yet), waiting for graphql-java v16 release
* enum values may currently not be a java keyword (seems to be a graphql-java limitation). issue to raise in the graphql-java project
* Check compatibility with the schema.public.graphqls (available in the plugin logic test resources folder)
* Document generateJPAAnnotation 
* Stop generating SubscriptionTypeResponse and SubscriptionTypeRootResponse ?
* Have a look to https://github.com/kobylynskyi/graphql-java-codegen

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

