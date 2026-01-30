Here are the next tasks listed, as a TODO list:
* Check documentation (wiki+README) that the schema can be given in json (tested in the forum client module)
* Add checks with queries that return interfaces (already ok?) and unions (also already ok?)
* Check error when using a query the needs a DataLoader, but the DataFetchersDelegate has not been defined (must be an interface)
* Add a check that all needed DataFetchersDelegate for interfaces have been defined. That is :
    * 
* The forum samples should run with java 25 (or update the tutorial to java 25)
* (for release 4, improve the batch mapper and data loader generation, WIP in the master_4x_change_batching_generation)
    * Improve DataFetcherDelegates for interfaces: check their real use by the server
    * Improve the way BatchMapper are generated
    * Remove the useless methods of the DataFetchersDelegate: when a DataFetcherDelegate has withDataLoader=true, then two methods are generated for it. The one with the DataLoader (that is used), and the one without the DataLoader (that isn't used)
    * Add a test to log useless methods in the implementation of dataFetchersDelegate
* Upgrade to maven 4
* Check compatibility with gradle 10
* (to check) The gradle plugin may not compile without having first build locally the maven plugin (due to the custom-resttemplate dependency added in 3.1)
* Check compilation with java 25: got a report that it wouldn't work
* Manage mockito warning with jdk 25 :
```
Mockito is currently self-attaching to enable the inline-mock-maker. This will no longer work in future releases of the JDK. Please add Mockito as an agent to your build as described in Mockito's documentation: https://javadoc.io/doc/org.mockito/mockito-core/latest/org.mockito/org/mockito/Mockito.html#0.3
WARNING: A Java agent has been loaded dynamically (C:\Users\egauthier\.m2\repository\net\bytebuddy\byte-buddy-agent\1.17.8\byte-buddy-agent-1.17.8.jar)
WARNING: If a serviceability tool is in use, please run with -XX:+EnableDynamicAgentLoading to hide this warning
WARNING: If a serviceability tool is not in use, please run with -Djdk.instrument.traceUsage for more information
WARNING: Dynamic loading of agents will be disallowed by default in a future release
```
* Investigate the compilation warning in GenerateCodeGenerator: there seems to be an issue with Gradle when loading a template from an external jar
    * In the catch, 'template' is not initialized, and 'theTemplate' is initialized, but not used
* Correct the link to the `ignoredSpringMappings` in the wiki (FAQ Server)
* Issue #214 and #215: add a `ignoredSpringMappings` parameter, to prevent the generation of some type Controllers, or field Controllers
* Read the https://maven.apache.org/whatsnewinmaven4.html page, to check compatibility with maven 4
* Empty the server Spring autoconfiguration class: controllers apparently can't be defined through bean declaration there. So this class content is currently useless
* Check that the two generated graphQLClient (httpGraphQLClient and webSocketGraphQLClient) are properly documented in the tutorials
* Check the links in the wiki (eg: https://graphql-maven-plugin-project.graphql-java-generator.com/client.html)
    * And close issue in the GraphQL-Forum-Gradle-Tutorial-client project)
* Add the generateDataFetchersForFields parameter in the tutorials
* Dozer is deprecated. Replace it by [mapstruct](https://github.com/mapstruct/mapstruct) or [modelmapper](https://github.com/modelmapper/modelmapper)
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
* The arguments for a subobject are available in the `DataFetchingEnvironment`, thanks to the `getArgument(argName)` method. For a scalar field, the idea is to add a getter for the field, with the `DataFetchingEnvironment` as a parameter. This getter would be in the generated POJO. It would be nice to add the developper to configure the content of this getter.
    * See https://www.graphql-java.com/documentation/v20/data-fetching/
* Issue #113: accept a schema.json as an input for code generation (instead of graphqls files)
* Idea #183: replace hard coded fields by maps. This would save memory for objects with lots of field (4000 fields in the identified use case)
* [Gradle] issue #14 : build is not compatible with the `--configuration-cache` gradle parameter (experimental feature)
* Tutorial: add the documentation about the application.yml file
    * Especially: `spring.main.web-application-type = none`
* Execute FieldTest.test_Issue1114_checkGenerateCode() (in plugin-logic, com.graphql_java_generator.plugin.language)
* @EnableGraphQLRepositories: replace the string (that contains the package name) by a class (so that when changing a package name, the code is still valid)
* Add a description for the GraphQL mojo
* Document generateJPAAnnotation 

