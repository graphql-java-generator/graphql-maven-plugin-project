Here are the next tasks listed, as a TODO list:


## TODO list for the current version
* [WIP] Adding the relay connection capabilities (almost done).
* Set separateUtilityClasses to true in samples-allGraphQLCases-client and samples-allGraphQLCases-server
* Check the #0038 issue: how to update a OAuth token with a javax.ws.rs.client.Client 
* Switch to apache 2.0 licence
* Optimize the build time of the plugin logic module 
* Replace all the thrown RuntimeException by meaningfull exceptions
* Rename the merge goal into mergeSchema goal (already named like that in gradle)
* Make easier the way to define the target folder for unit test generated files
* Move the packagePropery out of the common configuration
* Make the task/goal names more homogeneous (graphql for maven versus graphqlGenerateCode for Gradle, merge is too generic?)
* Allow to control the list of schema files, and their order (necessary to properly manage the extend keyword)
* Add a description of the GraphQL mojo
* Waiting for [issue 2055](https://github.com/graphql-java/graphql-java/issues/2055) to be solved. Some test cases can then be run again (see the allGraphQLCases.graphqls file)
* Add the Tutorial for Subscription, on the Client side
* Looks like the code generation doesn't work with a schema without a query (see the schema in issue 49)
* Update the README with the XxxxExecutor classes, when the next version is released
* Analyze and response to issue #39 (@connection for relay)
* Add the _connection_ stuff, as [specified here](https://relay.dev/graphql/connections.htmh 
* Add Tutorial to describe the best way to use the plugin
* Do a sample project based on the github GraphQL schema
* Specifying an unknown template in the pom should raise an error
* Allow to change the GraphQL server path (for query/mutation and for subscription)
* Change the JsonResponseWrapper as an interface, implemented by the XxxRootResponse classes
* Allow aliases for query/mutation fields
* Interface that implements Interface (waiting for graphql-java v15)
* [server side] Check graphql-java correction for issue 1844 (Directive values of type 'EnumValue' are not supported yet)
* enum values may currently not be a java keyword (seems to be a graphql-java limitation). issue to raise in the graphql-java project
* Check compatibility with the schema.public.graphqls (available in the plugin logic test resources folder)
* Check InputObjectType with fields that are lists
* Document generateJPAAnnotation 
* Comments coming from the graphQL schema should be reported in the generated code, especially the POJOs and the queries, mutations and subscriptions
* Stop generating SubscriptionTypeResponse and SubscriptionTypeRootResponse ?

## TODO List for 2.0 version:
* Remove the query/mutation/subscription Response type (currently deprecated)
* copyRuntimeSources: false should be the default value (change to be done in the tutorial and the client-dependency)
* separateUtilityClasses: true should be the default value
* Add a generateDeprecatedRequestResponse plugin parameter. Default value to true (no more XxxxResponse would be generated). With a value of true, the XxxxResponse would still be generated for compatibility with old code.



Investigate DTO for database mapping:
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