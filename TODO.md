Here are the next tasks listed, as a TODO list:

## TODO list for the 2.x branch
* #184 et #198: issues with custom scalars which type is no in the plugin-logic dependencies
    * Report correction in 1.x branch
* #199: "code too large" compilation error
* Gradle issue #18: all Gradle tasks are run, even if unnecessary
* #195: give access to the received data, even if there are errors
* Check for GraphQL specification changes (to properly manage them)
* Review the plugin documentation (goal and parameters)
* Issue Gradle-project #15: redundant cast to Long 


## TODO list for the 1.x branch
* #184 et #198: issues with custom scalars which type is not in the plugin dependencies
* #199: "code too large" compilation error
* Add an option to add the Serializable interface for generated POJOs
    * ==> Done. Answer to the related issue to do, when released
* Idea #183: replace hard coded fields by maps. This would save memory for objects with lots of field (4000 fields in the identified use case)
* [Gradle] issue #14 : build is not compatible with the `--configuration-cache` gradle parameter (experimental feature)
* `DirectiveRegistryInitializer`:
    * separate it from each schema (e.g.: allGraphQlCases client)
    * initialize it only once (not at each creation of a GraphQLRequest)
* Tutorial: add the documentation about the application.yml file
    * Especially: `spring.main.web-application-type = none`
* Execute FieldTest.test_Issue1114_checkGenerateCode() (in plugin-logic, com.graphql_java_generator.plugin.language)
* add a _HowTo compile page_ on the wiki (to build the plugin project, Java 9 or latter is needed, even if the built plugin is compatible with Java 8)
* @EnableGraphQLRepositories: replace the string (that contains the package name) by a class (so that when changing a package name, the code is still valid)
* Add a description for the GraphQL mojo
* enum values may currently not be a java keyword (seems to be a graphql-java limitation). issue to raise in the graphql-java project
* Document generateJPAAnnotation 

