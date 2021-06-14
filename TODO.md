Here are the next tasks listed, as a TODO list:


## TODO list for the current version
* Check the exec and execWithBindValues of the subscription type
* Remove RequestParametersFormat, that seems useless
* Implement a Spring BeanDefinitionRegistryPostProcessor to load the GraphQL repositories
* Add three annotations, to mark the query, the mutation and the subscription type
* Mark the skipGenerationIfSchemaHasNotChanged parameter as deprecated (no more used)
* Allow the definition of all GraphQL requests in a file, so that the plugin creates the interface and implementation class that allows to use these GraphQL requests
    * The interface and implementation class would be generated at compile time
    * If the file has been updated since the class has been created, the generated interface and implementation class should be updated when the application starts (or by the IDE)
    * This file would:
        * Contain the target class name
        * Contain a list of :
            * Name of the requests (must be a valid java name)
            * The kind of request (query, mutation, subscription)
            * The request, which can be partial and request, thanks to the tag name: partialRequest or fullRequest
    * This generated class would be:
        * Can be used both as a Spring Bean (@Component) and as a standard java class, thanks to a unique @Autowired constructor that has all necessary input parameters.
        * This constructor would prepare all the requests.
            * It's argument is one, two or three of query executor, mutation executor and subscription executor (depending on the different kinds of requests that have been provided)
        * A method is created for each request.
        * The parameters for this method depends on whether:
            * it's a partial request (known list of requests parameters, then Map<String,Object> or Object[])
            * It's a full request (no specific parameter, only Map<String,Object> or Object[])
    * Do the same with annotations, like Spring repositories:
        * Create an interface
        * Mark it with a @GraphQLRepository annotation
        * Create methods
        * Mark them with one of these annotations:
            * @PartialRequest(requestType=Query|Mutation|Subscription, requestName="RequestName", requestParametersFormat=MAP|(default)OBJECT_ARRAY|NONE)
                * The parameters must map the parameters as defined for this request
                * This method must return the response type that maps the requestType#requestName in the GraphQL schema
            * @FullRequest(requestType=Query|Mutation|Subscription, requestParametersFormat=MAP|(default)OBJECT_ARRAY|NONE)
                * This method must return the response type that maps the requestType
            * These annotations allow to define method parameters that map specific bind parameters in the request:
                * The @BindParameter(value="bindParameterName") can be added to the method parameters.
    * To be defined
        * Should the code for the GraphQL Repositories be generated at compile time or at runtime? 
        * In which package?  If runtime, in the same as the source interface. If compile time, in the same as util classes?
* Document in the Client FAQ how to retrieve the extensions response's values
* Use JWT in the OAuth use case, to speed up the tests
* Pass the test on the allGraphQLCases to being executed in parallel
* Add a test in the graphql-maven-plugin-samples-allGraphQLCases-pojo project
* Change the server Subscription implementation from Reactive Subject to Spring reactive
* Add an option to add the Serializable interface for generated POJOs
* [Done in the plugin, waiting for the graphql PR acceptance] Publish a PR to have a per request cache, in graphql-java-spring 
    * Done. Waiting for the PR to be accepted (and then a new release)
* [Almost done, there is still a glinch in server mode] Document how-to avoid the code generation (or find a better idea)
    * https://github.com/graphql-java-generator/graphql-maven-plugin-project/issues/69
    * When addRelayConnection is true, the _generated_schema.graphqls_ should be generated in the generated_resources instead of the classes folder
* @RelayConnection error when applied on a list: https://github.com/graphql-java-generator/graphql-maven-plugin-project/issues/62
    * Solved
* Remove the SubscriptionClientWebSocket class
* Check subscription against the Hasura setup (answer to issue 54) : https://hasura.io/
* Remove the useless interface  com.graphql_java_generator.client.response.RootResponse (from the runtime)
* Replace all the thrown RuntimeException by meaningfull exceptions
* Allow to control the list of schema files, and their order (necessary to properly manage the extend keyword)
* Add a description of the GraphQL mojo
* Waiting for [issue 2055](https://github.com/graphql-java/graphql-java/issues/2055) to be solved. Some test cases can then be run again (see the allGraphQLCases.graphqls file)
* Do a sample project based on the github GraphQL schema
* Allow to change the GraphQL server path (for query/mutation and for subscription)
* Change the JsonResponseWrapper as an interface, implemented by the XxxRootResponse classes
* Allow aliases for query/mutation fields
* [server side] Check graphql-java correction for issue 1844 (Directive values of type 'EnumValue' are not supported yet), waiting for graphql-java v16 release
* enum values may currently not be a java keyword (seems to be a graphql-java limitation). issue to raise in the graphql-java project
* Check compatibility with the schema.public.graphqls (available in the plugin logic test resources folder)
* Document generateJPAAnnotation 
* Stop generating SubscriptionTypeResponse and SubscriptionTypeRootResponse ?

## TODO List for 2.0 version:
* Rename the QueryExecutor (and its implementations) into RequestExecutor
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

