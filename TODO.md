Here are the next tasks listed, as a TODO list:

## TODO list for the 2.x branch
* The arguments for a subobject are available in the `DataFetchingEnvironment`, thanks to the `getArgument(argName)` method. For a scalar field, the idea is to add a getter for the field, with the `DataFetchingEnvironment` as a parameter. This getter would be in the generated POJO. It would be nice to add the developper to configure the content of this getter.
    * See https://www.graphql-java.com/documentation/v20/data-fetching/
* Optimize `getStringContentForGraphqlQuery`: use a `StringBuilder` instead of returning and concatenating strings
* Use [[_TOC_]] for the wiki pages (and the README)
* Refresh the GitHub and Shopify samples
* Review the plugin documentation (goal and parameters)
* Issue Gradle-project #15: redundant cast to Long 
* Issue #113: accept a schema.json as an input for code generation (instead of graphqls files)
* Issue #125: object_content.vm.java is hardcoded in templated
* Idea #183: replace hard coded fields by maps. This would save memory for objects with lots of field (4000 fields in the identified use case)
* [Gradle] issue #14 : build is not compatible with the `--configuration-cache` gradle parameter (experimental feature)
* `DirectiveRegistryInitializer`:
    * initialize it only once (not at each creation of a GraphQLRequest)
* Tutorial: add the documentation about the application.yml file
    * Especially: `spring.main.web-application-type = none`
* Execute FieldTest.test_Issue1114_checkGenerateCode() (in plugin-logic, com.graphql_java_generator.plugin.language)
* add a _HowTo compile page_ on the wiki (to build the plugin project, Java 9 or latter is needed, even if the built plugin is compatible with Java 8)
* @EnableGraphQLRepositories: replace the string (that contains the package name) by a class (so that when changing a package name, the code is still valid)
* Add a description for the GraphQL mojo
* Document generateJPAAnnotation 


## TODO list for the 1.x branch
