Here are the next tasks listed, as a TODO list:
* Allow aliases for query/mutation fields
* [server side] Check graphql-java correction for issue 1844 (Directive values of type 'EnumValue' are not supported yet)
* enum values may currently not be a java keyword (seems to be a graphql-java limitation). issue to raise in the graphql-java project
* Check compatibility with the schema.public.graphqls (available in the plugin logic test resources folder)
* Check InputObjectType with fields that are lists
* Document generateJPAAnnotation 
* Subscriptions. Currently, GraphQL Java Generator manages queries and mutations.
* Comments coming from the graphQL schema should be reported in the generated code, especially the POJOs and the queries, mutations and subscriptions


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