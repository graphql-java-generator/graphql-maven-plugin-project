Here are the next tasks listed, as a TODO list:
* Check compilation with java 25: got a report that it wouldn't work
* Correct the link to the `ignoredSpringMappings` in the wiki (FAQ Server)
* Issue #214 and #215: add a `ignoredSpringMappings` parameter, to prevent the generation of some type Controllers, or field Controllers
* Read the https://maven.apache.org/whatsnewinmaven4.html page, to check compatibility with maven 4
* Empty the server Spring autoconfiguration class: controllers apparently can't be defined through bean declaration there. So this class content is currently useless
* Check that the two generated graphQLClient (httpGraphQLClient and webSocketGraphQLClient) are properly documented in the tutorials
* Check the links in the wiki (eg: https://graphql-maven-plugin-project.graphql-java-generator.com/client.html)
    * And close issue in the GraphQL-Forum-Gradle-Tutorial-client project)
* Add the generateDataFetchersForFields parameter in the tutorials
* Dozer is deprecated. Replace it by [mapstruct](https://github.com/mapstruct/mapstruct) or [modelmapper](https://github.com/modelmapper/modelmapper)
* (for release 4)
    * Remove DataFetcherDelegates on interfaces, as they are not used !   :((
        * Propose a PR to add doc and display warnings in spring-graphql
        * Propose a PR to add doc and display warnings in graphql-java
    * Remove the useless methods of the DataFetchersDelegate: when a DataFetcherDelegate has withDataLoader=true, then two methods are generated for it. The one with the DataLoader (that is used), and the one without the DataLoader (that isn't used)
* plugin doc:
    * Find and correct the dead links
	* Check the generated doc for the `ignoredSpringMappings` plugin parameter
	* Correct the link to the `ignoredSpringMappings` in the wiki (FAQ Server)
* Check the sitemap, to enhance SEO, for instance see [the doc here](https://www.sitew.com/Comment-optimiser-son-referencement/sitemap)
* Align with GraphQL spec 2021 (almost done)
    * Check the impact of the changes in the interface hierarchies
        * All details in [this blog](https://dev.to/mikemarcacci/intermediate-interfaces-generic-utility-types-in-graphql-50e8))
        * todo: add the PageInfo, Node, Edge, Connection interfaces
* Instead of having to parameterize what to do with controllers, the plugin should be able to analyze the existing code by the implementor, find the defined controllers, and just define the others
    * Take a look at [beanshell](https://github.com/beanshell/beanshell)
    * Take a look at this [stackoverflow answer](https://stackoverflow.com/a/36221056/5056068)
    * Test [ezreflections](https://github.com/salimm/ezreflections)
* Issue #220: use immutable classes or records instead of classes
* Test all parameter values for BatchMappingDataFetcherReturnType
* generateBatchMappingDataFetchers:
    * Complete the doc for the plugin parameter
        * Add a link to https://github.com/spring-projects/spring-graphql/issues/232
* Issue with the generateBatchMappingDataFetchers:
    * These data fetcher can not have access to the field arguments (see [issue #232](https://github.com/spring-projects/spring-graphql/issues/232) of spring-graphql)
        * This is ok if this field has no argument
        * What should be done if this field has arguments ?
            * This would be a minority of cases, so do nothing, that is: 
                * Generate a standard data fetcher
                * try to still use a BatchLoader, but with a Context 
* Check the generated doc for the `ignoredSpringMappings` plugin parameter
* Replace DataFetchersDelegateRegistry attributes by proper autowired spring fields
* The arguments for a subobject are available in the `DataFetchingEnvironment`, thanks to the `getArgument(argName)` method. For a scalar field, the idea is to add a getter for the field, with the `DataFetchingEnvironment` as a parameter. This getter would be in the generated POJO. It would be nice to add the developper to configure the content of this getter.
    * See https://www.graphql-java.com/documentation/v20/data-fetching/
* Optimize `getStringContentForGraphqlQuery`: use a `StringBuilder` instead of returning and concatenating strings
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
* @EnableGraphQLRepositories: replace the string (that contains the package name) by a class (so that when changing a package name, the code is still valid)
* Add a description for the GraphQL mojo
* Document generateJPAAnnotation 

