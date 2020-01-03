Here are the next tasks listed, as a TODO list:
* Have a non fixed number of arguments in the query/mutation/subscription generated client method, so that it is not necessary for the caller to generate the map. These argument would be: "paramName1", "paramValue1", "paramName2", "paramValue2"...  (then simplify the Forum client sample)
* Finish the samples in the client doc page
* Check query method returning a scalar (forum schema is ready for that)
* Check scalar field of regular Type, having input parameters (forum schema is ready for that)
* Manage default values for input parameters
* Parsing the Query request for Query Preparation should directly call the Builder (instead of creating the QueryField array, then calling the Builder)
* Subscriptions. Currently, GraphQL Java Generator manages queries and mutations.
* Add a gradle plugin (work in progress)
* Manage properties which name are java keyword, like: public, private, class... Currently, it would generate a compilation error.
* Comments should be reported in the generated code, especially the POJOs and the queries, mutations and subscriptions
* Allow definition of specific Scalars (for instance Date, DateTime, Time)
* Fragment in graphql queries
* The plugin currently manages only one GraphQL schema file. It would be nice to allow several graphqls files, with a pattern like /*.graphqls
* [minor] The InputParameters for the query/mutation/subscription are all optional. If a mandatory parameter is missing at execution time, the request is sent to the server, which returns an error. It's fine. An enhancement is that GraphQL mandatory parameters would be marked as a mandatory InputParameter