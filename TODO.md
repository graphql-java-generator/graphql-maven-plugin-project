Here are the next tasks listed, as a TODO list:
* Test with null value (like in the shopify schema)
* Test with ArrayValue (like in the github schema)
* Test with ObjectValue (like in the github schema). Caution, the ObjectValue throws no error, but the value is replaced by a null
* [server side] Check graphql-java correction for issue 1844 (Directive values of type 'EnumValue' are not supported yet)
* enum values may currently not be a java keyword (seems to be a graphql-java limitation). issue to raise in the graphql-java project
* Check compatibility with the schema.public.graphqls (available in the plugin logic test resources folder)
* Check compatibility with the shopify (available in the plugin logic test resources folder)
* Check InputObjectType with fields that are lists
* Document generateJPAAnnotation 
* Check query method returning a scalar (forum schema is ready for that)
* Subscriptions. Currently, GraphQL Java Generator manages queries and mutations.
* Add a gradle plugin (work in progress)
* Comments should be reported in the generated code, especially the POJOs and the queries, mutations and subscriptions
* The plugin currently manages only one GraphQL schema file. It would be nice to allow several graphqls files, with a pattern like /*.graphqls   (it should already by the case, but must be tested to confirm)