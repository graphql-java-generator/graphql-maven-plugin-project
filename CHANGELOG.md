

1.x


Any mode, client or server:
- Corrected a bad dependency version, which prevents the released plugin to work
- Input object types are now accepted


Client mode:
- Connection to https is made simpler (just declare the https URL)
- Input parameters are properly managed for queries, mutations and regular GraphQL type's field. It's possible to prepare query with Bind variables, like in JPA
- Only for request prepared by the Builder: simplification and change in the way to construct ObjectResponse objects
- Exception GraphQLExecutionException renamed to GraphQLRequestExecutionException

Server mode:
- XxxDataFetchersDelegate interfaces renamed as DataFetchersDelegateXxxx (code is easier to read, as the DataFetchersDelegates are grouped together in the classes list)
- Input parameters are accepted for queries, mutations and object's field.  