Here are the next tasks listed, as a TODO list:
* Parsing the Query request for Query Preparation should directly call the Builder (instead of creating the QueryField array, then calling the Builder)
* Subscriptions. Currently, GraphQL Java Generator manages queries and mutations.
* Add a gradle plugin (work in progress)
* Manage properties which name are java keyword, like: public, private, class... Currently, it would generate a compilation error.
* Comments should be reported in the generated code, especially the POJOs and the queries, mutations and subscriptions
* Allow definition of specific Scalars (for instance Date, DateTime, Time)
* Fragment in graphql queries
* The plugin currently manages only one GraphQL schema file. It would be nice to allow several graphqls files, with a pattern like /*.graphqls
