Here are the next tasks listed, as a TODO list:
* Finish the "## GraphQL query (and mutation) calls" section
* Client mode: simplify the SSL conf, and update the client doc (removing the complex stuff about SSL)
* Client mode: Separate documentation for PreparedQueries and WithBuilder queries
    * And simplify the doc in the README and Index (to make them identical) 
* Subscriptions. Currently, GraphQL Java Generator manages queries and mutations.
* Add a gradle plugin (work in progress)
* Manage properties which name are java keyword, like: public, private, class... Currently, it would generate a compilation error.
* Manage field parameters. Currently, GraphQL Java Generator accepts parameters out of the query level (that is on object fields), only with Direct Queries (which is nice enough to begin)
* Comments should be reported in the generated code, especially the POJOs and the queries, mutations and subscriptions
* Allow definition of specific Scalars (for instance Date, DateTime, Time)
* Fragment in graphql queries
* The plugin currently manages only one GraphQL schema file. It would be nice to allow several graphqls files, with a pattern like /*.graphqls
