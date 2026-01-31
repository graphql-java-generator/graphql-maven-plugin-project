
<!--ts-->
<!--te-->

    

# 4.x versions

## Main changes in the 4.x releases (including breaking changes)

* Upgrade to Spring Boot 4 and Spring Framework 7. This implies:
    * Upgrade from Jackson 2 to Jackson 3. 
        * If you're using the JSON scalar, you may have to change the javaType parameter from `om.fasterxml.jackson.databind.node.ObjectNode` to `tools.jackson.databind.node.ObjectNode`
    * In server mode, when using an alternate path to expose the GraphQL server, the property in the spring `application.properties` file changed from `spring.graphql.path` to `spring.graphql.http.path`
* Needs at least java 17 (tested with Java 25)


## 4.0

All modes (client and server):
* Upgrade to Spring Boot 4 and Spring Framework 7. This implies:
    * Upgrade from Jackson 2 to Jackson 3. 
        * If you're using the JSON scalar, you may have to change the javaType parameter from `om.fasterxml.jackson.databind.node.ObjectNode` to `tools.jackson.databind.node.ObjectNode`
    * In server mode, when using an alternate path to expose the GraphQL server, the property in the spring `application.properties` file changed from `spring.graphql.path` to `spring.graphql.http.path`

Gradle plugin:
* Correction of issue 26: Gradle deprecation in GeneratePojoExtension.isGenerateJacksonAnnotations_Raw


Client mode:
* When connected to two different GraphQL servers, there could be conflicts between directive defined in this two schemas
* Removal of two templates (CUSTOM_SCALAR_REGISTRY_INITIALIZER and DIRECTIVE_REGISTRY_INITIALIZER), replaced by one: REGISTRIES_INITIALIZER. This allows a better isolation of some plugin's internal technical code.


# 3.x versions

## Breaking changes in the 3.x releases

* The default value for `generateDataFetcherForEveryFieldsWithArguments` be changed to true in version 3.0.1. This implies to implement more Data Fetchers
* The com.graphql-java-generator.graphql-gradle-plugin is no more maintained
    * You must switch to the com.graphql-java-generator.graphql-gradle-plugin3 plugin. That is: add '3' to the plugin's name


## 3.1

All modes (client and server):
* [minor] Better formatting of the generated code
* [minor] All strings in the generated code are marked with //$NON-NLS-1$ style comments, to prevent compilation warnings in some environments
* [Templates] Issue #125: Creation of the OBJECT_CONTENT template, that allows to override the content of an object (fields and methods)

Client mode:
* PR #237: Better generation for parameters (thanks to klafbang)
    * Setting a bind variable to null would erroneously remove the relevant argument in the query
    * Default value in GraphQL request parameters was not managed
* Correction of a NullPointerException when a query returns an interface containing fields with parameters
* Issue #238: if a webClient overrides the default one, it may use its own property (the application property _graphql.endpoint.url_ is then no more mandatory). This allows such application to use any property of their own to define the GraphQL url

## 3.0.1

Gradle plugin:
* The plugin would need java 21, and not 17 as indicated

server mode:
* The default value for `generateDataFetcherForEveryFieldsWithArguments` is now true, as announced for the  3.x versions


## 3.0


All modes (client and server) :
* The plugin now needs at least Java 17
* Upgrade of all dependencies: spring boot 3.5.4, graphql-java 24.0, ...
* Usage of JPMS (Java Platform Module System): the `graphql-java-client-runtime` and `graphql-java-server-runtime` are now java modules
    * Caution: due to java naming rules, in the module names, the 'minus' have been changed into underscores. The module names are:
        * com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_client_runtime
        * com.graphql_java_generator.graphql_maven_plugin_project.graphql_java_server_runtime
Gradle:
* Upgrade of gradle wrapper to 9.0.0
* The com.graphql-java-generator.graphql-gradle-plugin is no more maintained
    * You must switch to the com.graphql-java-generator.graphql-gradle-plugin3 plugin. That is: add '3' to the plugin's name
    
    

# 2.x versions

## Main enhancement in the 2.x releases

* The generated code uses spring-graphql
* Since 2.5, the `generateDataFetcherForEveryFieldsWithArguments` plugin param. This parameter allows to generate data fetchers for every field that has input argument, and add them in the generated POJOs.
    * The default is false, to backward compatibility.

## Breaking changes in 2.x versions

* 2.5
     * [Gradle] All task properties in `build.gradle` files, that contain file path must now be relative to the local project root. This is due to the compatibility with the configuration cache, which changed the path resolution methods.
* 2.8
    * [server mode] When the `generateDataLoaderForLists` plugin parameter is set to true, the plugin no more generates two methods per field (for field that return lists)
        * Two methods in `DataFetchersDelegateXxx`: one with the `DataLoader` parameter, that is used by the controller, and one without the `DataLoader` parameter, that is not used. The useless method is no more generated. This may result in compilation error, if the `@Override` method was added: you would then have to remove the implementation for this useless method.


## Not released yet

All modes (client and server):
* Added full support for the GraphQL 2021 specification:
    * The plugin now properly manages repeatable directives
    * Support added for the standard `@specifiedBy` annotation
    * interface hierarchy was already implemented (an interface may implement another one)
    * Added tests, to be sure that directives are in the correct order

Internal:
* Java version for the forum samples changed to 17 
* Various enhancements on the web site

## 2.9


All modes (client and server):
* Issue #218: Default target folder was based on "target" (on both the Maven and Gradle plugins)
* Issue #228: The plugin would throw an error, if an object contains more than one field of ID type

Pojo in server mode
* Issue #227: the generated code would not compile with the `generatePojo` goal/task in server mode when `generateJacksonAnnotations` is set to `true`

Custom templates:
* Breaking change due to the #227 issue correction, the custom templates `client_query_mutation_type.vm.java`, `client_subscription_type.vm.java`, `interface_type.vm.java` and `object_type.vm.java` changed. The import for `com.fasterxml.jackson.annotation.JsonProperty` and `com.fasterxml.jackson.databind.annotation.JsonDeserialize` has been added
* Each template states its name in a comment in the first line:
```java
/** Generated by the '${templateName}' default template from graphql-java-generator */
```
    * You can use the `templateName` place holder to change the header to something like 'Generated by the ${templateName} custom template'


## 2.8

Server mode:
* The new `generateBatchMappingDataFetchers` plugin parameters is in __beta version__ in this release. It allows to generate data fetchers with the <code>@BatchMapping</code> annotation (instead of the `@SchemaMapping` one). This allows to manage the N+1 select problem: so this allows much better performances, by highly diminishing the number of executed requests
    * The new `batchMappingDataFetcherReturnType` allows to control the return type of these data fetchers
    * Please note that the behaviour for this parameter may change a little for GraphQL schema that use field parameters. 
    * For GraphQL schemas that don't use field parameters, its behaviour can be considered as stable
* Issues #214 and #215: the new `ignoredSpringMappings` plugin parameter allows to ignore a list of type and field mappings (or all mappings, when this parameter is set to the star character, "*").
    * An ignored type mapping prevent the generation of its `DataFetcherDelegate`, and its entity Spring Controller. The [Spring Controller](https://docs.spring.io/spring-graphql/reference/controllers.html) must be 'manually' implemented.
    * An ignored field mapping prevent the generation of the method for this field in the `DataFetcherDelegate`, and its entity Spring Controller. A [Spring Controller](https://docs.spring.io/spring-graphql/reference/controllers.html) must be 'manually' implemented for this field.
* Issue #217: compilation error when a field first letter is in uppercase.

Internal API:
* The `DataFetcher.completableFuture` has been renamed to `DataFetcher.withDataLoader`. This impacts these templates: `object_content.vm.java`, `server_EntityController.vm.java` and `server_GraphQLDataFetchersDelegate.vm.java`

## 2.7

Client mode:
* Issue #213: The GraphQL custom scalars are wired according to spring-graphql needs. This fixes bean error at startup, in some specific cases

All modes (client and server):
* Issue #213: The GraphQL custom scalars which names in the provided schema is different that the GraphQL type name in the provided GraphQL scalar implementation would throw an error at execution.

Internal:
* The `server_GraphQLWiring.vm.java` custom template has been renamed to `GraphQLWiring.vm.java`, as this template is now used for both the client and the server mode.


## 2.6

Gradle:
* issue #21: dependencies for spring framework6 are enforced for graphql-gradle-plugin3 

All modes (client and server):
* Issue #113: base the code generation on a json schema (from an introspection query), instead of regular graphqls GraphQL schema files. This is done with the help of this new plugin parameter: `jsonGraphqlSchemaFilename`
* Issue #208: the plugin is now marked as thread safe (no more warning when using maven parallel builds)


## 2.5

Gradle:
* Upgrade of gradle wrapper to 8.6
* Issue #14: The plugin is now compatible with Gradle configuration cache
    * __Possible breaking change:__ The path given to the task properties must now all be relatives to the root of their project (without a leading slash), eg : "build/generated/mytarget" (not "/build/generated/mytarget" or "$builDir/generated/mytarget"))
    * (Pojo goals) No more need to add the generated source folder to the sourceSets.main.java.srcDirs. It is automatically added to the java source folders.

All modes:
* Remove a bad java import, which could cause compilation error when just generating POJO

Server mode:
* Add of the `generateDataFetcherForEveryFieldsWithArguments` plugin param. This parameter allows to generate data fetchers for every field that has input argument, and add them in the generated POJOs. This allows a better compatibility with spring-graphql, and an easy access to the field's parameters.
* Issue #209: error with subscription that returns enum, when the implementation returns a Publisher that is not a Flux


## 2.4

Dependencies upgrade:
* Spring boot to 2.7.18 and 3.2.1
* Upgrade of plexus-utils to the 3.0.24 (to get rid of CVE vulnerabilities)

Client and server modes:
* Issues #205 and #207: The JSON and Object custom scalars are now properly managed

Client mode:
* When executing full requests, the `query` keyword was mandatory, whereas it is optional in the GraphQL specs.


## 2.3.2

Client and server modes:
* Correction of issue #202: the generated code would not compile, if a GraphQL interface or type has a `class` attribute (due to the final `getClass` method). The generated method is `get_Class()`



## 2.3.1

Client mode:
* Issue 199: the generated code would not compile if the GraphQL schema is too big
* Issue #195: the _data_ part is now parsed even when there are errors. If an error occurs during the request execution (for instance the GraphQL server returns errors in its response), then the plugin tries to parse the _data _ part of the response. The parsed _data_ is attached to the thrown `GraphQLRequestExecutionException` or `GraphQLRequestExecutionUncheckedException`, along with the full response. They can be retrieved with the `getData()` and `getResponse()` methods.
* Issue #200:
    * A missing break would prevent Custom Scalars of type Boolean to be deserialized (could not read the server response)
    * Alias on fields that are a Custom scalars could not be deserialized when using the deprecated ResponseType


## 2.3

Client and server modes:
* Correction of issues #184 and #198: error with custom scalars, when the custom scalar's class is not in the plugin's classpath
* The generated code generates much less warnings

Client mode:
* Add of the reactive executors and GraphQL repositories:
    * Queries and mutations return Mono
    * Subscriptions return Flux

Gradle plugin:
* Correction of the issues 13 and 18: The plugin is running all tasks instead of running only the configured tasks


## 2.2

Gradle plugin:
* Creation of a graphql-gradle-plugin3 plugin, compiled against Spring Boot 3 and Spring Framework 6
    * graphql-gradle-plugin is still available, if you' re using Spring Boot 2 and Spring Framework 5

Both mode:
* The schema personalization capability is now open to both the server and client modes. It allows to:
    * Add or modify fields
    * Add interface and annotation to classes (GraphQL types, input types, interfaces, unions and enums) or fields.


## 2.1

Both mode:
* Compile dependency upgraded to Spring Boot 2.7.12 (and 3.1.0)

Server mode:
* Issue #190: The Spring Entity Controllers generated by the plugin can now be overridden

Client mode:
* Issue #189: using OAuth2 in spring 6 (with copyRuntimeSource=true only) would through this error: "missing createError() method in OAuthTokenExtractor.GetOAuthTokenClientResponse"


## 2.0

Change of some plugin parameters value (please read either [[Client migration from 1.x to 2.x|client_migrate_1-x_to_2-x]] or [[Server migration from 1.x to 2.x|server_migrate_1-x_to_2-x]] for more information) changed of default value:
* copyRuntimeSources: false _(both client and server mode)_
* generateBatchLoaderEnvironment: true _(server only)_
* generateDeprecatedRequestResponse: false _(client only)_
* skipGenerationIfSchemaHasNotChanged: true _(both client and server mode)_
It was initially planned to force their value to the new default valye. But this would have too much impact on the existing code. Changing the default value allows 'old' users to minimize the impact when switching to the 2.0 version, while new user will use cleaner code.


Server mode:
* Issue #190: The spring entity controllers can now be overridden

## 2.0RC1

Release Candidate version for the 2.x versions.

Main changes:
* Based on [spring-graphql](https://spring.io/projects/spring-graphql)
* Upgrade of dependencies, based on [spring-boot 2.7.10](https://docs.spring.io/spring-boot/docs/2.7.10/reference/html/)
* Needs JDK 17 to be build, but the generated artifact is compatible with Java 8
* Compatibility with Spring Boot 3.
    * For a sample of this, you can check the [graphql-maven-plugin-samples-Forum-client](https://github.com/graphql-java-generator/graphql-maven-plugin-project/tree/master_2.x/graphql-maven-plugin-samples/graphql-maven-plugin-samples-Forum-client) and the [graphql-maven-plugin-samples-Forum-server](https://github.com/graphql-java-generator/graphql-maven-plugin-project/tree/master_2.x/graphql-maven-plugin-samples/graphql-maven-plugin-samples-Forum-server) samples that are part of the build.
* __gradle plugin__: The id changed from `com.graphql_java_generator.graphql-gradle-plugin` to `com.graphql-java-generator.graphql-gradle-plugin`

You can check these pages for more information on how to migrate from 1.x versions:
* [[Client migration from 1.x to 2.x|client_migrate_1-x_to_2-x]]
* [[Server migration from 1.x to 2.x|server_migrate_1-x_to_2-x]]

Know issues:
* All builds for servers should be executed with a clean (_mvn clean install_ or _gradlew clean build_), otherwise the GraphQL schema available at runtime becomes invalid. The server won't start.
* For Spring 3, in client mode, copyRuntimeSources should be manually to false, to avoid compilation errors


# 1.x versions

## 1.18.12

Client and server modes:
* Correction of issues #184 and #198: error with custom scalars, when the custom scalar's class is not in the plugin's classpath

Client mode:
* Issue 199: the generated code would not compile if the GraphQL schema is too big

## 1.18.11

Both modes:
* Issue #179: Problem with primitive types as java type for custom scalars
* The build is now Ok with java 17
* Much better reliability of Subscriptions


## 1.18.10

Dependency upgrade: 
* Upgrade from graphql-java 19.2 to 20.0
* Upgrade from graphql-java-extended-scalars 19.0 to 20.0

Both modes:
* PR #171: Add descriptions for input parameters if they exist
* Field that are java reserved keywords of either GraphQL types or GraphQL input types would cause error during request execution
    * Subject started thanks to the PR #177 (Modifying the getGetter method to accept reserved keywords)


Client mode:
* Issue #173: introspection query from graphql-java 19.2 would not work (the plugin was using an old introspection schema)
* Issue #174: request execution error with Custom scalar that are arrays
* Issue #175: adding the `@JsonProperty("xxx")` annotation on getter of the generated POJO would solve some issues when generating an openAPI based on the generated file, with field having case issues
* Issue #176: the `GraphQLRequestExecutionException` class has now a `getErrors()` method, that allows to retrieve the list of `GraphQLError` returned by the server, including the extension field.


## 1.18.9

Dependency upgrade: 
* Upgrade from Spring Boot 2.7.4 to 2.7.6


Both modes:
* Issue #166: Corrected an issue that prevents to request data when GraphQL field's name are java reserved keywords
* Issue #164: the descriptions from the GraphQL schema is now included in the java comments for objects, fields, union...
* GraphQLDirectives are now written as Java annotation in the generate code
    * Note: this doesn't work yet for schema, scalar (that may receive a Directive when extended) and custom scalars

Server mode:
* Issue #162: Declaring a GraphQL directive applicable to VARIABLE_DEFINITION would prevent the GraphQL server to start



## 1.18.8

Dependency upgrade: 
* Upgrade from Spring Boot 2.4.4 to 2.7.4
* Upgrade from Spring Framework 5.3.5 to 5.3.23
* Upgrade from graphql-java 18.3 to 19.2
* Upgrade from graphql-java-extended-scalars 18.0 to 19.0
* Upgrade from lombok 1.18.12 to 1.18.24 (to solve compatibility issues with JDK >= 15)

All modes:
* (Thanks to agesenm-ELS) Prefix and Suffix management for POJO generated from the GraphQL schema.
    * Please [check these new plugin parameters](https://graphql-maven-plugin-project.graphql-java-generator.com/graphql-maven-plugin/plugin-info.html): typePrefix, typeSuffix, unionPrefix, unionSuffix, enumPrefix, enumSuffix, interfacePrefix, interfaceSuffix, inputPrefix, inputSuffix


## 1.18.7

Dependency upgrade :
* Upgrade from graphql-java 17.3 to graphql-java 18.0
* Upgrade from graphql-java-extended-scalars 17.0 to 18.0

All modes:
* Issue #136: most usages of the `extend` GraphQL keyword where not managed
* Prevent endless compilation from an IDE (eclipse...) when a type is removed from the GraphQL schema
* Issue [Gradle n°11](https://github.com/graphql-java-generator/graphql-gradle-plugin-project/issues/11): maxTokens is not set by default to Integer.MAX_VALUE (=2147483647). This prevent errors on big GraphQL schemas
* Issue #139: Compilation Failures, when keywords used in enum values, or query/execution/subscription fields



## 1.18.6

All modes:
* Removed various compilation warnings in the generated code

Client mode:
* Issue #132 (regression in 1.18.5). The code would not compile when generateDeprecatedRequestResponse=false and separateUtilityClasses=true.

Gradle plugin:
* As soon as a task defined in the _build.gradle_ , it is added as a dependency for the _compileJava_ and _processResources_ tasks
    * No more need to write _compileJava.dependsOn(generateServerCode)_ or _processResources.dependsOn(generateServerCode)_
* Removed a warning (about a missing `javax.annotation.meta.When` annotation), when building from the Gradle plugin
* No more Gradle 8 compatibility warning


## 1.18.5

Both modes (client and server):
* Issue #130: A GraphQL type of name `Field` or `Builder` would prevent the generated code to compile

Client mode:
* Removes some useless dependencies (that could lead to a wrong error message, about a missing graphQL bean)
* commons-io upgraded from 2.8.0 to 2.11.0
* commons-text upgraded from 1.8 to 1.9

Server mode:
* Issue #131: Spring configuration properties like `spring.codec.max-in-memory-size` would be ignored.


## 1.18.4

Both modes (client and server):
* (Issue #128) partial rewrite of the generation code, to avoid conflicts between the POJO's java types, and other class names (like Client, Date...)


Server mode:
* Added a `generateDataLoaderForLists` plugin parameter. The default value is false, which let the generated code unchanged. When set to true, data loader methods is also generated for lists (in DataFetcherDelegates)
* Added the `generateDataLoaderForLists` directive: it can be added to a specific GraphQL field, which type is a list of GraphQL objects, which have an id. The generated code will then make use of a Data Loader. Please read the details are on the [Wiki server page](https://github.com/graphql-java-generator/graphql-maven-plugin-project/wiki/server).
* The `DataFetcherDelegate` interfaces has now two methods for batch loading (instead of once before). The provided default implementation prevent side effect on the existing `DataFetcherDelegate` implementations. These methods are `batchLoader` and `unorderedReturnBatchLoader`. All the details are on the [Wiki server page](https://github.com/graphql-java-generator/graphql-maven-plugin-project/wiki/server).
* Upgrade of java-dataloader from version 2.2.3 to version 3.1.2


## 1.18.3

All modes:
* Solved an issue: an Exception would be thrown when using the POJO setters with a null value.


## 1.18.2

Gradle plugin:
* Solved a build issue, that would generate an error with some old configurations of the plugin

Server more:
* Issue #122, and PR #123: NullPointerException would occurs when a notification returns a null value. This may occurs when a Subscription response is a non mandatory object or scalar. For this kind of subscription only, the DataFetcher must now return a `Publisher<Optional<JavaType>>` instead of a `Publisher<JavaType>`
    * There is no impact on DataFetchers for Queries or Mutations
    * There is no impact on DataFetchers for Subscriptions which return type is mandatory (like `String!`)


## 1.18.1

All modes (client and server):
* Dependencies has been restructured to properly separate client and server runtimes:
    * The __`graphql-java-runtime` module no longer exists__ . If you're using, you must change to one of `graphql-java-client-runtime` or `graphql-java-server-runtime`.
    * When `copyRuntimeSources` is false, the used dependencies should be either `graphql-java-client-runtime` or `graphql-java-server-runtime`
    * When `copyRuntimeSources` is true, the used dependencies should be either `graphql-java-client-dependencies` or `graphql-java-server-dependencies`
    * 	This solves issues #109 and #56
* graphql-java upgraded from 16.2 to 17.3
	* You can check the changes on the [graphql-java release page](https://github.com/graphql-java/graphql-java/releases)
    * The main changes are:
        * __Non standard scalars__ have moved from the `graphql.Scalars` class (that is included in the graphql-java module) to the `graphql-java-extended-scalars`. So, if you're using non standard scalars like `Byte`, `Short` and `Long` (...), you'll probably have to change your configuration in your pom.xml or gradle.build file, from `graphql.Scalars.xxx` to `graphql.scalars.ExtendedScalars.xxx`
        * The graphQL schema is, by default, limited to 15000 tokens. This version adds the new plugin parameter __`maxTokens`__, that allows to override this limit.
* Issue #114: allows overriding field's type when implementing interface


Server mode:
* More robust multi-threading management for subscriptions


Upgrade of dependencies versions (to remove security issues):
* h2 is used only for sample. Upgraded from 1.4.200 to 2.1.210
* velocity is the template engine. Upgraded from 1.7 to 2.3
    * A (positive) side effect, is that the velocity logging is managed through slf4j, like all the plugin's code logging. So this solves the issue #103, caused by velocity.log beeing created in the Intellij `bin` folder.
    
    
    


## 1.18

All modes (client and server):
* __Subscriptions__ are now managed with full respect of the [graphql-transport-ws](https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md) WebSocket sub-protocol, as defined in the [graphql-ws](https://github.com/enisdenjo/graphql-ws) reference library.
    * Caution: if you are using Subscriptions, you must upgrade both the client(s) and the server, as the subprotocol changes in this version.
* PR #101: Removes unused imports in some generated classes
* __generatePojo__ : corrected compilation errors (before this release, the graphql-java-runtime, graphql-java-client-dependencies or graphql-java-server-dependencies) was necessary to compile the generated code.

Client mode:
* The Spring Configuration is an auto-configuration. This was not the case in 1.17.x releases, which would prevent the Spring `@Primary` annotation to work, causing a regression compared to previous version. Thanks to that, it's now easy (again) to override Spring beans.
* Subscription: better exception management (especially if a connection error occurs)
* Simplified and secured the code that generates the request
* You can override the `RequestExecution` to use query, mutation and subscription according to the [graphql-transport-ws](https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md). Please find a sample in the `GraphQLTransportWSSpringConfiguration` spring configuration class of [this test](https://github.com/graphql-java-generator/graphql-maven-plugin-project/blob/master/graphql-maven-plugin-samples/graphql-maven-plugin-samples-allGraphQLCases-client/src/test/java/org/allGraphQLCases/GraphQLTransportWSIT.java)


## 1.17.3

Client mode:
* Corrected issue #98: Spring Bean name collision with the QueryExecutor (which is an internal Bean from the plugin). This is a regression of the previous 1.17x versions.

Internal:
* The `QueryExecutor` interface (and its implementations) has been renamed into `RequestExecution`, to avoid name collision with the QueryExecutor Spring Bean, coming from the GraphQL schema.


## 1.17.2 

Client mode:
* Correction of issues #95 and #96: the `springBeanSuffix` is now properly applied to all spring beans. Now it is also applied to the query, mutation and subscription executors, as well as all other Spring beans.


## 1.17

All modes (client and server):
* The maven plugin is now better integrated in the IDE. No more need of the maven-helper-plugin, to add the folder for the generated sources and generated resources.

Client mode:
* The generated code can now attack __two (or more) GraphQL servers__.
    * More information on [this page](https://github.com/graphql-java-generator/graphql-maven-plugin-project/wiki/client_more_than_one_graphql_servers).
* Added the ability to create GraphQL Repositories: like Spring Data Repositories, GraphQL Repositories allow to declare GraphQL requests (query, mutation, subscription) by __just declaring an interface__. The runtime code create dynamic proxies at runtime, that will execute these GraphQL requests.
    * In other words: GraphQL Repositories is a powerful tool that allows to execute GraphQL request, without writing code for that: just declare these requests in the GraphQL Repository interface
    * More information on [this page](https://github.com/graphql-java-generator/graphql-maven-plugin-project/wiki/client_graphql_repository).


## 1.16

Both modes:
* The plugin now properly manages GraphQL scalar extensions
* Added a control, that the version of the runtime matches the version of the plugin. Doing such a control can prevent some weird errors.
* Added a check, that the provided custom templates have valid names and match to existing files (or resources in the classpath)

Client mode:
* Issue #82: Using Hard coded Int parameters would raise an exception (for instance in this request: `"{aRequest(intParam: 3) {...}}"`)


## 1.15

Client mode:
* The queries, mutations and subscriptions now __accept GraphQL aliases__, like specified in [GraphQL spec](http://spec.graphql.org/June2018/#sec-Field-Alias)
    * Once the request is executed, The alias value can be retrieved with the `Object getAliasValue(String: aliasname)` method that has been added to every generated objects and interfaces. This method returns the alias value, parsed into the relevant Java Object.
* The default `QueryExecutor` provided by the plugin is now a Spring bean. If you want to override it, you should not mark your own `QueryExecutor` with the `@Primary` annotation, to ignore the default one.


## 1.14.2

New goal/task added:
* generatePojo: This goal allows to only generate the Java classes and interfaces that match the provided GraphQL schema.

Server mode:
* Workaround to build a project with Gradle 7 without the gradle wrapper (this commit prevents a strange error during the build)

Internal:
* The plugin now uses slf4j as the logging frontend framework, as do Gradle and Maven (since maven 3.1). This means that, when using Maven, the minimum release version is the 3.1 version.

## 1.14.1

Both mode:
* Upgrade of _com.google.guava_, to version 30.1.1-jre, to remove a vulnerability
* Upgrade of Spring boot from 2.4.0 to 2.4.4
* Upgrade of Spring framework from 5.3.0 to 5.3.5
* Upgrade of Spring security from 5.4.1 to 5.4.5
* Upgrade of graphql-java-extended-scalars version from 1.0.1 to 16.0.1
* Upgrade of commons-io from 2.6 to 2.8.0
* Upgrade of dozer-core from 6.5.0 to 6.5.2
* Upgrade of h2 from 1.4.199 to 1.4.200

Client mode:
* Issue #65: When using requests with the parameters in the request (no GraphQL Variables and no Bind Parameter), the request is properly encoded when these parameters are or contain strings
* Dependency order changed in the graphql-java-client-dependencies module, to make sure the right spring's dependencies are used (this could prevent a Spring app to start)
* Removed the use of the `reactor.core.publisher.Sinks` class, to restore compatibility for those who uses an older version of Spring Boot

## 1.14

Both mode:
* Upgrade of _com.google.guava_, to remove a vulnerability

Client mode:
* Request with GraphQL variable are now accepted. Of course, this works only with full requests. You can find more information on GraphQL Variables in the [GraphQL spec](http://spec.graphql.org/June2018/index.html#sec-Language.Variables). 
* Subscription can now be executed as full request. They were previously limited to partial requests.

Custom templates:
* The following templates have been updated : client_DirectiveRegistryInitializer.vm.java, client_GraphQLRequest.vm.java, client_jackson_deserializers.vm.java, client_query_mutation_executor.vm.java, client_subscription_executor.vm.java, client_subscription_type.vm.java, server_GraphQLDataFetchers.vm.java
* These updates are due to:
    * The `GraphQLInputParameters` has been updated, to now embed all the GraphQL information (prerequisite for GraphQL variable)
    * The list level is now better managed (not just a boolean, but the real depth when there are lists of lists)
    * The input parameters now typed with an enum, not just a boolean for mandatory/optional.
* The _client_query_target_type.vm.java_ was not used and has been removed.


## 1.13

Both mode:
* Custom Templates can now be defined as a resource in the current project (no need to embed them in a jar prior to use them). See the [CustomTemplates-client pom file](https://github.com/graphql-java-generator/graphql-maven-plugin-project/blob/master/graphql-maven-plugin-samples/graphql-maven-plugin-samples-CustomTemplates-client/pom.xml) as a sample for that.

Client mode:
* The _extensions_ field on the root of the GraphQL server response can be retrieved by using Full Queries. More information on the [Client page about request execution](https://graphql-maven-plugin-project.graphql-java-generator.com/exec_graphql_requests.html)
* Issue #55 : the _extensions_ field of the GraphQL errors is now properly managed. It's possible to receive any GraphQL valid response for this field. And the `Error` class has now the proper getters to manage it (including deserialization of the _extensions_ map values in any Java classes)
* Issue #65: It's now possible to provide a full query that contains all GraphQL parameters (without runtime parameters). More info in the [Client FAQ](https://graphql-maven-plugin-project.graphql-java-generator.com/client_faq.html)

Server mode:
* The Query/Mutation/Subscription are now available on the same URL (/graphql by default). This is the standard GraphQL behavior, but it was tricky to build, due to a Java limitation.


## 1.12.5

Client mode:
* Issue #53: custom scalars would not be properly managed when they are a parameter of a query, mutation or subscription.

## 1.12.4

Server mode:
* It's now possible to override the type wiring, thanks to the new `GraphQLWiring` class.
* It's now possible to override the DataFetcher definitions, by overriding `GraphQLDataFetchers`. This allows, for instance, to change the DataLoader behavior.
* [Internal] The `GraphQLProvider` class has been removed. The Spring beans it created are now created by the `GraphQLServerMain` class. The type wiring has been moved in the new `GraphQLWiring` class. This allows an easier overriding of the generated type wiring.


## 1.12.3


Both mode:
* Corrected a multithreading issue with the provided custom scalars GraphQLScalarTypeDate and GraphQLScalarTypeDateTime

Server mode:
* Issue #72: The subscription notifications would not be properly sent when more than one client subscribed to a subscription.

Client mode:
* The GraphQL server response could not be deserialized, when it contains the (non standard) _extensions_ entry


## 1.12.2

Both modes (client and server):
* Added the _skipGenerationIfSchemaHasNotChanged_ parameter. It's in beta version. It prevents code and resource generation, of the schema file(s) are older than these generated sources or resources. It's default value is false in 1.x releases.
* When the _addRelayConnections_ parameter is true, the XxxConnection types of the fields marked with @RelayConnection are now non mandatory.
* The comments read in the provided schema are reported into the generated code and the generated GraphQL schemas.

Server mode:
* The graphql-java version has been upgraded to 16.2 (the latest version at this time)
* The generated code would not allow the specific implementation to override the GraphQLInvocation Spring Bean



## 1.12.1

Both modes (client and server):
* Correction for the _addRelayConnections_: the _Node_ interface was not properly copied to all implementing subtypes and subinterfaces

Server mode:
* The server won't start when the _graphql-java-runtime_ dependency is in the classpath (that is when the _copyRuntimeSources_ plugin parameter is set to false)
* When a DataFetcher has a BatchLoader, two datafetchers would be wired, instead of just one. This is internal to the generated code, and has no impact on the "user's" code.
* The cache of the DataLoader is now managed for per request.
* When the _addRelayConnections_ plugin parameter is set to true, the _generateServerCode_ task/goal (and _graphql_ task/goal when in server mode) copies the generated schema file in the _/classes_ folder, so that the graphql-java engine has a proper access to it, at runtime.


## 1.12

Both modes (client and server):
* Added support for OAuth 2
* Removed all dependencies to log4j
* [internal] The GraphqlUtils class has been moved into the com.graphql_java_generator.util package


Client mode:
* The client can now be a Spring Boot app (and that's now the recommended way to build a GraphQL app). see the [plugin web site](https://graphql-maven-plugin-project.graphql-java-generator.com/client_spring.html) for more info on this
* The subscription management has been updated.
==> Spring reactive WebSocketClient
==> The _SubscriptionClient_ interface has a new method: _WebSocketSession getSession()_, which allows to retrieve the Spring reactive _WebSocketSession_. 

Server mode:
* Corrected a regression in 1.11.2, due to _generateBatchLoaderEnvironment_ plugin parameter (see issue #64)

## 1.11.2

server mode:
* The generated code would not compile for fields with parameters (when the field's type is an entity with an id)
* Add of the _generateBatchLoaderEnvironment_ parameter. When in server mode, it allows the batch loader to retrieve the context, for instance the field parameters associated to this id.


## 1.11.1

Both modes (client and server):
* Upgrade of spring boot from 2.3.3 to 2.4.0
* Issue 54: The generated code would not compile for subscriptions that return a list

Gradle plugin:
* The plugin is now compatible with a JDK/JRE 8 (it previously needed java 13)

## 1.11

Both modes (client and server):
* Changes in goal(maven)/task(gradle) names, to make them clear and homogeneous between the gradle and the maven plugin:
    * The _graphql_ maven goal (and _graphqlGenerateCode_ gradle task) are deprecated, but they will be maintained in future 2.x versions
    * The goal(maven)/task(gradle) are now:
        * __generateClientCode__ : generates the Java code on client side, to access a GraphQL server, based on its GraphQL schema. It is the same as the deprecated _graphql_ maven goal (or _graphqlGenerateCode_ gradle task), with the _mode_ parameter removed (it is internally forced to _client_).
        * __generateServerCode__ : generates the Java code on server side, to access a GraphQL server, based on its GraphQL schema. It is the same as the deprecated _graphql_ maven goal (or _graphqlGenerateCode_ gradle task), with the _mode_ parameter removed (it is forced to _server_).
        * __generateGraphQLSchema__ : new, see below
        * __graphql__ (maven) / ___graphqlGenerateCode__ (gradle) : deprecated and maintained. It's the same as the new _generateClientCode_ and _generateServerCode_ goals/tasks, with the _mode_ plugin parameter that allows to choose between the client or the server mode.  
* New _generateGraphQLSchema_ goal/task that allows to generate the GraphQL schema file. It's interesting when:
    * There are several GraphQL schema files in input (for instance with the extends GraphQL capability)
    * The _addRelayConnections_ is used, that adds the _Node_ interface, and the _Edge_ and _Connection_ types to the schema.


Client Mode (generateClientCode):
* Corrected issue 50: could not work with nested arrays (for instance [[Float]])
* Corrected issue 51: error with argument being a list of ID ([ID]) 



## 1.10

Both modes (client and server):
* Upgrade of graphql-java from v14.0 to v15.0
* The main improvement for this is: the plugin now accepts interfaces that implement one or more interfaces
* Attributes of input types that are enum are now properly managed

Server mode:
* The generated code would not compile if a GraphQL interface is defined, but not used, in the given GraphQL schema


## 1.9

Both modes (client and server):
* The GraphQL schema can now be split into separate files, including one file containing GraphQL extend keyword on the other file's objects
* Add of the _merge_ goal/task: it generates a GraphQL schema file, based on the source GraphQL schemas. It can be used to merge several GraphQL schema files into one file, or to reformat the schema files.

Client mode:
* Fixes #46: Strings not properly escaped for JSON

## 1.8.1

Both modes (client and server):
* The generated code was not compatible with Java 8 (only with Java 9 and above)

## 1.8

Both modes (client and server):
* Corrected issue #43: GraphQL Float was mapped to Float, instead of Double

Client mode:
* a XxxxExecutor class is now generated for each query, mutation and subscription types, for better separation of __GraphQL objects and utility classes__ . They contains the methods to prepare and execute the queries/mutations/subscriptions that were in the query/mutation/subscription classes. These the query/mutation/subscription classes are still generated, but their use to prepare and execute the queries/mutations/subscriptions is now deprecated.
    * In other word: existing code CAN remain as is. It'll continue to work.
    * New code SHOULD use the XxxExecutor classes.
* Add of the generateDeprecatedRequestResponse. Default value is true, which makes it transparent for existing code. If set to false, the XxxResponse classes are not generated (where Xxx is the GraphQL name for the query, mutation and subscription objects), nor the Xxxx classes in the util subpackage (only if separateUtilityClasses is true). If generated, these classes are marked as Deprecated, and should not be used any more.
* Solved issue #44: no more need to add an _extension_ bloc in the _build_ pom's bloc, to make the plugin work. 
* Named Fragments would not work if seperateUtilityClass was false

Server mode:
* Added the _scanBasePackages_ plugin parameter. It allows to create our Spring Beans, including the DataFetchersDelegateXxx implementation in another place than a subpackage of the package define in the _packageName_ plugin parameter
* Corrected a dependency issue (for server implemented from the code generated by the plugin)

## 1.7.0

Both modes (client and server):
* The plugin now manages __subscription__
* For custom templates: the QUERY_MUTATION_SUBSCRIPTION template has been split into two templates, QUERY_MUTATION and SUBSCRIPTION

Server mode:
* In some cases, the DataFetcherDelegate now have another DataFetcher that must be implemented
* Some server templates rename, to respect java standards. An underscore has been added to these templates name: BATCH_LOADER_DELEGATE, BATCH_LOADER_DELEGATE_IMPL, DATA_FETCHER and DATA_FETCHER_DELEGATE

## 1.6.1

Both modes (client and server):
* Default value for input parameters (fields and directives) that are null, array or an object are now properly managed.
* Plugin parameter _copyGraphQLJavaSources_ renamed to _copyRuntimeSources_
* New Plugin parameter _separateUtilityClasses_: it allows to separate the generated utility classes (including GraphQLRequest, query/mutation/subscription) in a subfolder. This avoids collision in the generated code, between utility classes and the classes directly generated from the GraphQL schema.

Client mode:
* Thanks to the _separateUtilityClasses_ plugin parameter, the plugin can generate the code for the shopify and github GraphQL schema (needs some additional tests: tester need here...)
   

## 1.6.0

Both modes (client and server):
* Added a _templates_ plugin parameter, that allows to override the default templates. This allows the developper to control exactly what code is generated by the developer.
* Added a _copyGraphQLJavaSources_ that allows to control whether the runtime code is embedded in the generated (previous behavior, set as the default behavior), or not. ==> _copyGraphQLJavaSources_ renamed to _copyRuntimeSources_ in 1.7.0 version
* Removed the unused java annotation GraphQLCustomScalar
* Solved issue #35: argument for directive could not be CustomScalar, Boolean, Int, Float or enum (enum are still not valid on the server side of this plugin, due to a graphql-java 14.0 limitation, see https://github.com/graphql-java/graphql-java/issues/1844 for more details)

Client mode:
* Added GraphQL __fragment__ capability. Query/mutation/subscription can now contain Fragment(s) and inline Fragment(s), including directives on fragments.
* Added support for GraphQL __Union__
* The client code has been highly simplified :
    * The Builder support is now limited to the  _withQueryResponseDef(String query)_  method.
    * The support for the  _withField()_, _withSubObject()_ (...) methods has been removed
    * The  ___IntrospectionQuery_  class is no more generated: the ___schema_  and  ___type_  queries has been added to the GraphQL schema's query, as defined in the GraphQL spec. A default query is added if no query was defined in the GraphQL schema.  
    * The client code should now use the  _GraphQLRequest_  that is now generated along with the GraphQL java classes.
    * A _GraphQLConfiguration_ class has been added. The generated query/mutation/subscription classes create an instance of this class, so there is no impact on existing code. But this class must be used to configure the _GraphQLRequest_.
    * Please check the behavior of your full queries: the mutation keyword is now mandatory for mutations, as specified in the GraphQL specification. The query keyword remains optional. 
    * Please the [client mode doc](https://graphql-maven-plugin-project.graphql-java-generator.com/client.html) for more information.

## 1.5.0

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
  


## 1.4.0

Both modes (client and server):
- The plugin is compatible again with java 8
- The provided Date and DateTime scalars are now provided as a static field (instead of the class itself), due to a graphql-java change) 

Client mode:
- Can now invoke GraphQL introspection queries  (it was already the case on server side, thanks to graphql-java)

## 1.3.2

Both modes (client and server):
- Input parameters are now managed for scalar fields (custom or not)
- Removed the dependency to log4j, replaced by slf4j
- the GraphQL schema may now use java keywords (if the GraphQL schema uses identifiers that are java keywords, these identifiers are prefixed by an underscore in the generated code)

Client mode:
- Added a constructor in the generated query/mutation/subscription with a preconfigured Jersey client instance to support customization of the rest request

## 1.3.1

Both modes (client and server):
- The project now compiles up to JDK 13 (the generated code is still compatible with java 8 and higher)
- Unknown GraphQL concept are now ignored (instead of blocking the plugin work by throwing an error)
 


## 1.3


Both modes (client and server):
- Custom Scalars are now properly managed. You can provide your own Custom Scalars, or used the ones defined by graphql-java
- Fixed issue 8: Problem when using Boolean Type with property prefix "is"


## 1.2


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