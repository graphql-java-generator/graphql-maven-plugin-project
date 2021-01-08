Here are the next tasks listed, as a TODO list:


## TODO list for the current version
* Check <copyGraphQLJavaSources>false</copyGraphQLJavaSources>  (it seems that the code is still copied)
* Make subscription also work with OAuth protected servers (integration test is in graphql-maven-plugin-samples-allGraphQLCases-client)
* Remove all log4j dependencies from the client and server pom dependencies (issue #52)
   Remove exclusions in poms, like spring-boot-starter-logging
* Check this issue: when executing the full Forum client test case for the first time after starting the server, the subscription test won't work. It works everytime if executed alone, or with the full test case starting from the second execution.
* Remove the SubscriptionClientWebSocket class
* Check subscription against the Hasura setup (answer to issue 54) : https://hasura.io/
* Check the client dependencies (from all client samples)
* Finish updating the client_spring page
* Add unit test in runtime, to test the Reactive stuff
* Check the WebClient with a default TLS configuration on a well known server
* Update the README with the XxxxExecutor classes
* Make the Maven plugin generate its code in the graphql-maven-plugin folder (like the gradle plugin)
==> update the four tutorials
==> Check the documented default value
* [WIP] Adding the relay connection capabilities (almost done, unit tests are Ok, remaining task: integration tests).
* Check the #0038 issue: how to update a OAuth token with a javax.ws.rs.client.Client 
	OAuth:"https://www.baeldung.com/jersey-sse-client-request-headers";//
	"https://www.baeldung.com/spring-webclient-oauth2#springsecurity-internals"//
	"https://manhtai.github.io/posts/spring-webclient-oauth2-client-credentials/"
* Switch to apache 2.0 licence
* Comments coming from the graphQL schema should be reported in the generated code, especially the POJOs and the queries, mutations and subscriptions
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



Hello,

  You can try one of the com.graphql-java-generator maven or gradle plugins.
They generates the POJOs and utility classes from the GraphQL schema, to let you execute GraphQL request from your java code:

https://github.com/graphql-java-generator/graphql-maven-plugin-project

https://github.com/graphql-java-generator/graphql-gradle-plugin-project

FYI, these plugins also have a server mode, to help developing GraphQL servers, in Java.

Etienne  