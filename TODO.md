Here are the next tasks listed, as a TODO list:
* Allow aliases for query/mutation fields
* [server side] Check graphql-java correction for issue 1844 (Directive values of type 'EnumValue' are not supported yet)
* enum values may currently not be a java keyword (seems to be a graphql-java limitation). issue to raise in the graphql-java project
* Check compatibility with the schema.public.graphqls (available in the plugin logic test resources folder)
* Check InputObjectType with fields that are lists
* Document generateJPAAnnotation 
* Subscriptions. Currently, GraphQL Java Generator manages queries and mutations.
* Comments coming from the graphQL schema should be reported in the generated code, especially the POJOs and the queries, mutations and subscriptions
