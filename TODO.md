Here are the next tasks listed, as a TODO list:

## TODO list for the 3.x branch
* Remove the useless methods of the DataFetchersDelegate: when a DataFetcherDelegate has withDataLoader=true, then two methods are generated for it. The one with the DataLoader (that is used), and the one without the DataLoader (that isn't used)

## TODO list for the 2.x branch
* Issue #220: use immutable classes or records instead of classes
* Issue #218: Default target folder based on <directory>target</directory>
* Rename the generated `DataFetchersDelegateRegistry` class (as a `Registry` type in the GraphQL schema would generate a name collision)
* Test all parameter values for BatchMappingDataFetcherReturnType
* generateBatchMappingDataFetchers:
    * Check that the returned sub-objects are link to their 
    * Complete the doc for the plugin parameter
        * Add a link to https://github.com/spring-projects/spring-graphql/issues/232
    * Check all four possible return types: BatchMappingDataFetcherReturnType
    * Complete AllGraphQLCasesServer_util_batchMapping_Test
    * 
* Issue with the generateBatchMappingDataFetchers:
    * These data fetcher can not have access to the field arguments (see [issue #232](https://github.com/spring-projects/spring-graphql/issues/232) of spring-graphql)
        * This is ok if this field has no argument
        * What should be done if this field has arguments ?
            * This would be a minority of cases, so do nothing, that is: 
                * Generate a standard data fetcher
                * try to still use a BatchLoader, but with a Context 
                
* Check the generated doc for the `ignoredSpringMappings` plugin parameter
* Instead of having to parameterize what to do with controllers, the plugin should be able to analyse the existing code by the implementor, find the defined controllers, and just define the others
* Correct the link to the `ignoredSpringMappings` in the wiki (FAQ Server)
* In the migration guide and the wiki (FAQ server) : explains that the overriding bean must have a different name (and may not extend the generated controller)
* Correction the client execution error when executing these lines in `OverriddenControllerIT.checkThatTheCharacterControllerIsOverridden()`
```
		// String req = "{name(uppercase:true) @testDirective(value:\"checkThatTheCharacterControllerIsOverridden\")}";
		String req = "{name @testDirective(value:\"checkThatTheCharacterControllerIsOverridden\")}";
		List<CIP_Character_CIS> name = this.queryExecutor.withoutParameters(req);
```
* plugin doc:
    * Add the since parameter
    * Just have a summary in the starting table (not the full doc)
    * Find and correct the dead links
* Empty the server Spring autoconfiguration class: controllers apparently can't be defined through bean declaration there. So this class content is currently useless
* Do additional tests on the generated code, in the plugin-logic module
* Issue #214 and #215: add a `ignoredSpringMappings` parameter, to prevent the generation of some type Controllers, or field Controllers
* Update the samples to the 2.7 release
    * Update the dependencies in the README (or better : remove them)
* Check that the two generated graphQLClient (httpGraphQLClient and webSocketGraphQLClient) are properly documented in the tutorials
* Indicates in the Gradle tutorials that there are two versions of the plugin
* Check the links in the wiki (eg: https://graphql-maven-plugin-project.graphql-java-generator.com/client.html)
    * And close issue in the GraphQL-Forum-Gradle-Tutorial-client project)
* Ajouter le paramètre generateDataFetchersForFields dans les tutoriels
* Dozer is deprecated. Replace it by [mapstruct](https://github.com/mapstruct/mapstruct) or [modelmapper](https://github.com/modelmapper/modelmapper)
* Replace DataFetchersDelegateRegistry attributes by proper autowired spring fields
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
