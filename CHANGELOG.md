# Not releases yet

Client mode:
* The client code has been highly simplified :
    * The Builder support is now limited to the  _withQueryResponseDef(String query)_  method.
    * The support for the  _withField()_, _withSubObject()_ (...) methods has been removed
    * The  ___IntrospectionQuery_  class is no mode generated: the ___schema_  and  ___type_  queries has been added to the GraphQL schema's query, as defined in the GraphQL spec. A default query is added if no query was defined in the GraphQL schema.  
    * The client code should now use the  _GraphQLRequest_  that is now generated along with the GraphQL java classes.
    * Please check the behavior of your full queries: the mutation keyword is now mandatory for mutations, as specified in the GraphQL specification. The query keyword remains optional. 
    * Please the [client mode doc](https://graphql-maven-plugin-project.graphql-java-generator.com/client.html) for more information. 

# 1.5.0

Both modes (client and server):
- GraphQL __Directives__ are now managed
- GraphQL types can implement multiple interfaces
- Upgrade of graphql-java from v13.0 to v14.0

Client mode:
- Directives can be added in the query, on query and fields (fragment is for a next release, coming soon)
- The query/subscription/mutation classes have now a collection of __exec__ methods, which allows to execute several queries/mutations/subscriptions in one server call. This allows to add directive on the queries/mutations/subscriptions. Please the [client mode doc](https://graphql-maven-plugin-project.graphql-java-generator.com/client.html) for more information. 
- Added a queryName/mutationName/subscriptionName that accept bind parameters, for each query/mutation/subscription. Please have a look at the allGraphQLCases client tests, in the _org.allGraphQLCases.FullQueriesDirectIT_ class
- interfaces are properly deserialized, thanks to GraphQL introspection. 
(caution: code impact. Previously, for each interface, the plugin would generated a concrete class that doesn't exist in the GraphQL schema. This is not the case any more, and only GraphQL types are now generated
- The __typename is added to the list of scalar fields, for every request GraphQL nonscalar type. This allow to properly deserialize interfaces and unions.
  


# 1.4.0

Both modes (client and server):
- The plugin is compatible again with java 8
- The provided Date and DateTime scalars are now provided as a static field (instead of the class itself), due to a graphql-java change) 

Client mode:
- Can now invoke GraphQL introspection queries  (it was already the case on server side, thanks to graphql-java)

# 1.3.2

Both modes (client and server):
- Input parameters are now managed for scalar fields (custom or not)
- Removed the dependency to log4j, replaced by slf4j
- the GraphQL schema may now use java keywords (if the GraphQL schema uses identifiers that are java keywords, these identifiers are prefixed by an underscore in the generated code)

Client mode:
- Added a constructor in the generated query/mutation/subscription with a preconfigured Jersey client instance to support customization of the rest request

# 1.3.1

Both modes (client and server):
- The project now compiles up to JDK 13 (the generated code is still compatible with java 8 and higher)
- Unknown GraphQL concept are now ignored (instead of blocking the plugin work by throwing an error)
 


# 1.3


Both modes (client and server):
- Custom Scalars are now properly managed. You can provide your own Custom Scalars, or used the ones defined by graphql-java
- Fixed issue 8: Problem when using Boolean Type with property prefix "is"


# 1.2


Both modes (client and server):
- Corrected a bad dependency version, which prevents the released plugin to work
- Input object types are now accepted
- [CAUTION, code impact] All GraphQL exceptions have been moved into the com.graphql_java_generator.exception package

Client mode:
- Connection to https is made simpler (just declare the https URL)
- Input parameters are properly managed for queries, mutations and regular GraphQL type's field. It's possible to prepare query with Bind variables, like in JPA
- Only for request prepared by the Builder: simplification and change in the way to construct ObjectResponse objects
- Exception GraphQLExecutionException renamed to GraphQLRequestExecutionException

Server mode:
- XxxDataFetchersDelegate interfaces renamed as DataFetchersDelegateXxxx (code is easier to read, as the DataFetchersDelegates are grouped together in the classes list)
- Input parameters are accepted for queries, mutations and object's field.  
- Add of the generateJPAAnnotation plugin parameter (default to false)