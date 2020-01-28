Here are the next tasks listed, as a TODO list:
* Custom Scalars : almost done
* Finish the samples in the client doc page
* Document generateJPAAnnotation 
* Manage directives (https://www.apollographql.com/docs/graphql-tools/schema-directives/)
* Link with the maven generated plugin documentation
* Check query method returning a scalar (forum schema is ready for that)
* Check scalar field of regular Type, having input parameters (forum schema is ready for that)
* Parsing the Query request for Query Preparation should directly call the Builder (instead of creating the QueryField array, then calling the Builder)
* Subscriptions. Currently, GraphQL Java Generator manages queries and mutations.
* Add a gradle plugin (work in progress)
* Manage properties which name are java keyword, like: public, private, class... Currently, it would generate a compilation error.
* Comments should be reported in the generated code, especially the POJOs and the queries, mutations and subscriptions
* Fragment in graphql queries
* The plugin currently manages only one GraphQL schema file. It would be nice to allow several graphqls files, with a pattern like /*.graphqls
