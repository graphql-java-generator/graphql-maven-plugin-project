Here are the next tasks listed, as a TODO list:
* Generate the WebSocketConfig and WebSocketHandler classes
* Document how to change the GraphQL server path (for query/mutation and for subscription)
* Document the subscription
* Check subscription that returns list
* Check subscription returning sub objects ({id date author publiclyAvailable title content})
* Test subscription with Custom Scalars, input types...
* Specifying an unknown template in the pom should raise an error
* Change the JsonResponseWrapper as an interface, implemented by the XxxRootResponse classes
* Allow aliases for query/mutation fields
* Interface that implements Interface (waiting for graphql-java v15)
* [server side] Check graphql-java correction for issue 1844 (Directive values of type 'EnumValue' are not supported yet)
* enum values may currently not be a java keyword (seems to be a graphql-java limitation). issue to raise in the graphql-java project
* Check compatibility with the schema.public.graphqls (available in the plugin logic test resources folder)
* Check InputObjectType with fields that are lists
* Document generateJPAAnnotation 
* Subscriptions. Currently, GraphQL Java Generator manages queries and mutations.
* Comments coming from the graphQL schema should be reported in the generated code, especially the POJOs and the queries, mutations and subscriptions

TODO List for 2.0 version:
* Remove the query/mutation/subscription Response type (currently deprecated)
* separateUtilityClasses: true should be the default value




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