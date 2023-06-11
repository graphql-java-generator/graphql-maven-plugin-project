Here are the next tasks listed, as a TODO list:

## TODO list for the 2.x branch
* Check for GraphQL specification change (to properly manage them)
* Review the plugin documentation (goal and parameters)
* Issue Gradle-project #15: redundant cast to Long 


## TODO list for the 1.x branch
* Add an option to add the Serializable interface for generated POJOs
* Idea #183: replace hard coded fields by maps. This would save memory for objects with lots of field (4000 fields in the identified use case)
* Add or correct the URL in the README and in the wiki's home.
* [Gradle] issue #14 : build is not compatible with the `--configuration-cache` gradle parameter (experimental feature)
* `DirectiveRegistryInitializer`:
    * separate it from each schema (e.g.: allGraphQlCases client)
    * initialize it only once (not at each creation of a GraphQLRequest)
* Tutorial: add the documentation about the application.yml file
    * Especially: `spring.main.web-application-type = none`
* Execute FieldTest.test_Issue1114_checkGenerateCode() (in plugin-locic, com.graphql_java_generator.plugin.language)
* add a _HowTo compile page_ on the wiki (to build the plugin project, Java 9 or latter is needed, even if the built plugin is compatible with Java 8)
* @EnableGraphQLRepositories: replace the string (that contains the package name) by a class (so that when changing a package name, the code is still valid)
* Remove the SubscriptionClientWebSocket class
* Add a description of the GraphQL mojo
* [server side] Check graphql-java correction for issue 1844 (Directive values of type 'EnumValue' are not supported yet), waiting for graphql-java v16 release
* enum values may currently not be a java keyword (seems to be a graphql-java limitation). issue to raise in the graphql-java project
* Document generateJPAAnnotation 

