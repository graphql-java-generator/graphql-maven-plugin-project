
<!--ts-->
   * [What is it?](#what-is-it)
   * [Three main versions: 1.x, 2.x and 3.x](#three-main-versions-1x-2x-and-3x)
   * [Availability: Maven and Gradle](#availability-maven-and-gradle)
       * [Maven Plugin](#maven-plugin)
       * [Gradle Plugin](#gradle-plugin)
   * [The plugin goals/tasks](#the-plugin-goalstasks)
   * [The Documentation](#the-documentation)
   * [Compatibility with GraphQL](#compatibility-with-graphql)
   * [Change log](#change-log)
   * [Note for contributors](#note-for-contributors)
   * [License](#license)
<!--te-->


# What is it?

The GraphQL Java Generator makes it easy to work in Java with graphQL in a schema first approach.

This project is a code generator, that allows to quickly develop __GraphQL clients__ and __GraphQL servers__ in java, based on a GraphQL schema.

That is: graphql-java-generator generates the boilerplate code, and lets you concentrate on what's specific to your use case.

* In __client mode__ : graphql-java-generator generates an executor class for each query, mutation type and/or subscription type. These classes contain the methods to call the queries, mutations and subscriptions. That is: __to call the GraphQL server, you just call the relevant method__. In client mode, the plugin generates:
    * The __POJOs__ from the GraphQL schema. That is: one class, interface or enum for each item in the provided GraphQL schema file(s)
    * The __utility classes__ that allows you to execute queries, mutations and subscriptions, and to retrieve their result (including the GraphQL response's _extensions field)
    * Support blocking and (since 2.3) reactive (Mono, Flux) queries
    * The support for the full GraphQL specification (relay cursors, subscription, custom scalars, fragment, directive, GraphQL variables, GraphQL alias...).
    * The capability to use __bind parameters__ within your queries, mutations and subscriptions, in an easier way than the GraphQL variables
    * It is based on Spring framework, and since 2.0 on __spring-graphql__. It still can be used with non-spring apps, as explained in the project's wiki.
    * When used in a spring boot app, each Spring component can be overridden. This allows fine tuning, like connecting to __OAuth__ server, changing the _WebClient_, and much more
    * Since 1.17, it is possible to execute GraphQL request by just creating a Java interface, no code at all: __GraphQL Repositories__ work almost like Spring Data Repositories. More information [in the wiki](https://github.com/graphql-java-generator/graphql-maven-plugin-project/wiki/client_graphql_repository)
* In __server mode__ : graphql-java-generator generates an almost ready to start GraphQL server. The developer has only to develop the access to the data. That is :
    * The generated code can be packaged either in a __jar__ (starting as a Java application) or a __war__ (starting in a Java container like tomcat or jetty).
    * graphql-java-generator generates the __main method__ (in a jar project) or the __main servlet__ class (in a war project),
    * It generates the __POJOs__ for the provided GraphQL schema file(s). 
    * It supports the full GraphQL specification (relay cursors, query/mutation/subscription, custom scalars, fragment, directive, aliases...)
    * The generated code is a __Spring boot app (or servlet)__. You can __override every default component__, to personalize your server: GraphQL components (add instrumentation, type wiring, field wiring...), HTTP stuff (Spring Security, OAuth, OpenID Connect...) and much more. See the [server FAQ](../../wiki/server_faq) for more information on this.
    * Various options allows to personalize the generated code (standard JPA annotations, Java type for ID fields, custom scalars, specific annotations...). See the [plugin parameters page](https://graphql-maven-plugin-project.graphql-java-generator.com/graphql-maven-plugin/plugin-info.html) for all the goal/tasks and their plugin parameters
    * The developer just has to __implement each DataFetchersDelegate__, based on the provided interfaces, to provide the access to the data

Other points that are worth to point out:
* The project is __extensively tested__:
    * Through unit tests (for the runtime),
    * Through unit and integration tests (for the plugin logic)
    * Through full integration tests: three samples contain both the client and the server parts. Integration tests are run on client side, against "its" server side. More than 250 integration tests are run on client side against the server part.
* A big effort is done to __avoid any impact on your code, when the plugin evolves__. 
* The `generateGraphQLSchema` maven/gradle goal/task allows to __merge several schemas in one__, adding (for instance) relay capability in the generated schema

# Three main versions: 1.x, 2.x, 3.x and 4.x

The 1.x versions:
* Allows non-spring application either by using the `javax.ws.rs.client.Client` client, which is deprecated and has been removed in Spring Boot 3
* Is based directly on [graphql-java](https://www.graphql-java.com/), and a clone of [graphql-java-spring](https://github.com/graphql-java/graphql-java-spring).
* Is not compatible with Spring Boot 3 and Spring Framework 5

The 2.x versions:
* Is based on [spring-graphql](https://spring.io/projects/spring-graphql)
* Is compatible with Spring Boot 3 and Spring Framework 6
    * Another version of the Gradle plugin had been created to achieve this (see below)
* (since 2.3) Allows also reactive queries (that returns reactive Mono or Flux)
* Please check the [Client migration from 1.x to 2.x](../../wiki/client_migrate_1-x_to_2-x) or [Server migration from 1.x to 2.x](../../wiki/server_migrate_1-x_to_2-x) if you're using the 1.x version.

The 3.x versions:
* Is compatible with java 17 and upper (tested ok with java 25)
* Is compatible with JPMS (Java Modules)
* Doesn't maintain the Gradle graphql-gradle-plugin plugin. If your using gradle, you'll have to switch to graphql-gradle-plugin3 (just add a '3' to the plugin name if you was not already using it).
* The project won't build in java 25, but the published plugin is compatible with java 25.
* Is based on spring boot 3 and spring framework 5

The 4.x versions:
* Is compatible with java 17 and upper (tested ok with java 25)
* Is compatible with JPMS (Java Modules)
* Doesn't maintain the Gradle graphql-gradle-plugin plugin. If your using gradle, you'll have to switch to graphql-gradle-plugin3 (just add a '3' to the plugin name if you was not already using it).
* Is based on spring boot 4 and spring framework 7 (see the [CHANGELOG](CHANGELOG.md) for the impacts)

# Availability: Maven and Gradle

The generator is currently available both as a Maven plugin and as a Gradle plugins.

The plugin documentation is available [on this page](https://graphql-maven-plugin-project.graphql-java-generator.com/graphql-maven-plugin/plugin-info.html). It lists all the plugin goals and their parameters. It is valid for both Maven and Gradle.

## Maven plugin

The __Maven plugin__ is available in the project ([graphql-maven-plugin-project](https://github.com/graphql-java-generator/graphql-maven-plugin-project)).

The last published version can be found in the [Maven Central Repository](https://mvnrepository.com/artifact/com.graphql-java-generator/graphql-maven-plugin)


## Gradle Plugin

Two __Gradle plugins__ are available from the project [graphql-gradle-plugin-project](https://github.com/graphql-java-generator/graphql-gradle-plugin-project).

They offer the exact same functionalities:
* __[graphql-gradle-plugin](https://plugins.gradle.org/plugin/com.graphql-java-generator.graphql-gradle-plugin)__ is compiled in java 8, against Spring Boot 2 and Spring Framework 5
    * This version of the plugin exists for the 1.x and 2.x releases
    * As the graphql-gradle-plugin is built for Sprint Boot 2, it will not maintained starting from version 3.0
* __[graphql-gradle-plugin3](https://plugins.gradle.org/plugin/com.graphql-java-generator.graphql-gradle-plugin3)__ is compiled in java 17, against Spring Boot 3 and Spring Framework 6


# The plugin goals/tasks

All maven goals and gradle tasks are described on [this page](https://graphql-maven-plugin-project.graphql-java-generator.com/graphql-maven-plugin/plugin-info.html)

This plugin contains these goals (Maven) / tasks (Gradle):
* __`generateClientCode`__ : this goal generates the client code from the Graphql schema file(s)
    * You'll find all the details [on this page](https://graphql-maven-plugin-project.graphql-java-generator.com/graphql-maven-plugin/generateClientCode-mojo.html)
* __`generateServerCode`__ : this goal generates the server code from the Graphql schema file(s)
    * You'll find all the details [on this page](https://graphql-maven-plugin-project.graphql-java-generator.com/graphql-maven-plugin/generateServerCode-mojo.html)
* __`generatePojo`__ : this goal generates only the java objects that match the provided GraphQL schema. It allows to work in Java with graphQL, in a schema first approach.
    * You'll find all the details [on this page](https://graphql-maven-plugin-project.graphql-java-generator.com/graphql-maven-plugin/generatePojo-mojo.html)
* (deprecated) __`graphql`__ was the previous main goal. It can generate both the client and the server code, thanks to its _mode_ parameter. 
    * You'll find all the details [on this page](https://graphql-maven-plugin-project.graphql-java-generator.com/graphql-maven-plugin/graphql-mojo.html) 
* __`generateGraphQLSchema`__ allows to generate a GraphQL schema file, based on the source GraphQL schemas. It can be used to merge several GraphQL schema files into one file, or to reformat the schema files.
    * You'll find all the details [on this page](https://graphql-maven-plugin-project.graphql-java-generator.com/graphql-maven-plugin/generateGraphQLSchema-mojo.html) 


# The Documentation

The full documentation is available on the [github wiki](../../wiki). 

You can also:
* Take a look at the tutorials :
    * For Maven
        *[GraphQL-Forum-Maven-Tutorial-client](https://github.com/graphql-java-generator/GraphQL-Forum-Maven-Tutorial-client)
        * [GraphQL-Forum-Maven-Tutorial-server](https://github.com/graphql-java-generator/GraphQL-Forum-Maven-Tutorial-server)
    * For Gradle
        * [GraphQL-Forum-Gradle-Tutorial-client](https://github.com/graphql-java-generator/GraphQL-Forum-Gradle-Tutorial-client)
        * [GraphQL-Forum-Gradle-Tutorial-server](https://github.com/graphql-java-generator/GraphQL-Forum-Gradle-Tutorial-server)
* Study the samples available in the project
* Directly go to the client or server documentation on the [github wiki](../../wiki)


# Compatibility with GraphQL

This plugin respects all the GraphQL specifications:
- queries, mutations and subscriptions
- introspection
- custom scalars
- input types
- interfaces and unions (that are both implemented in Java interfaces into the generated code)
- directives
- fragments (global and inline)
- input parameters (for fields and directives)
- Use of Bind Parameters to map Java variables with input parameters
- easy execution of just a query/mutation/subscription (one field of the query, mutation or subscription type) as a standard method call
- execution of a full GraphQL request, which allows to execute several queries or several mutations at once
- Management of the GraphQL response's _extensions_ field
- Comments and description coming from the GraphQL schema are reported in the generated code


# Change log

The change log is available on the [CHANGELOG](CHANGELOG.md) page


# Note for contributors

## Branches

Since 01 may 2023, the default branch is the `master_2.x` branch. It contains all the changes done for the 2.0 version.
This branch

The historical branch has been renamed from `master` to `master_1.x` branch. This branch is used only for the 1.x maintenance, that is: only major and security issues. 

## Project organization

All the plugin logic is stored in the [graphql-maven-plugin-project](https://github.com/graphql-java-generator/graphql-maven-plugin-project) project.

The [Maven plugin](https://github.com/graphql-java-generator/graphql-maven-plugin-project) and the [Gradle plugin](https://github.com/graphql-java-generator/graphql-gradle-plugin-project) are just wrapper for the plugin logic, available in the __graphql-maven-plugin-logic__ module of the maven project. 

If you want to compile the maven project, you'll have to add the lombok.jar file in your IDE. Please see the relevant section, in the Install menu of the [https://projectlombok.org/](https://projectlombok.org/) home page. This very nice tools generates all java boiler plate code, like setters, getters, constructors from fields...

If you use eclipse, please use the __code formatter__ given with the project (file _graphql-java-generator (eclipse code formatter).xml_ at the root of the project). This allows to have the sample code formatting: the code is then homogeneous, and the comparison between versions is simpler. To do this, go to the eclipse preferences, select Java/Code Style/Formatter, and import this file. Then, in the Java/Editor/Save Actions, check the "Perform the selected action on save", "Format source code", "Format all lines", "Organize imports" and "Additional actions" which its default content


# License

`graphql-java-generator` is licensed under the MIT License. See [LICENSE](LICENSE.md) for details.
