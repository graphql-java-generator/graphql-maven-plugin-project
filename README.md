
<!--ts-->
<!--te-->



# GraphQL Java Generator

## What is it?

The GraphQL Java Generator makes it easy to work in Java with graphQL in a schema first approach.

This project is a code generator, that allows to quickly develop __GraphQL clients__ and __GraphQL servers__ in java, based on a GraphQL schema.

That is: graphql-java-generator generates the boilerplate code, and lets you concentrate on what's specific to your use case. Then, the running code __doesn't depend on any dependencies from graphql-java-generator__. So you can get rid of graphql-java-generator at any time: just put the generated code in your SCM, and that's it.

* In __client mode__ : graphql-java-generator generates a class for each query, mutation type and/or subscription type. These classes contain the methods to call the queries, mutations and subscriptions. That is: __to call the GraphQL server, you just call one of this method__. In client mode, the plugin generates:
    * The __POJOs__ from the GraphQL schema. That is: one class, interface or enum for each item in the provided GraphQL schema file(s)
    * The __utility classes__ that allows you to execute queries, mutations and subscriptions, and to retrieve their result (including the GraphQL response's _extensions field)
    * The support for the full GraphQL specification (relay cursors, subscription, custom scalars, fragment, directive, GraphQL variables, GraphQL alias...).
    * The capability to use __bind parameters__ within your queries, mutations and subscriptions, in an easier way than the GraphQL variables
    * It can be used within a __spring boot app__, or in non-spring apps. When using as a spring boot app, each Spring component can be overridden. This allows fine tuning, like connecting to __OAuth__ server, changing the _HttpClient_, and much more
* In __server mode__ : graphql-java-generator generates an almost ready to start GraphQL server. The developer has only to develop request to the data. That is :
    * The generated code can be packaged either in a __jar__ (starting as a Java application) or a __war__ (starting in a Java container like tomcat or jetty).
    * graphql-java-generator generates the __main method__ (in a jar project) or the __main servlet__ class (in a war project), and all the Spring wiring, based on [graphql-java-spring](https://github.com/graphql-java/graphql-java-spring), itself being build on top of [graphql-java](https://www.graphql-java.com/).
    * It generates the __POJOs__ for the provided GraphQL schema file(s). 
    * It supports the full GraphQL specification (relay cursors, query/mutation/subscription, custom scalars, fragment, directive, aliases...)
    * The generated code is a __Spring boot app (or servlet)__. You can __override every default component__, to personalize your server: GraphQL components (add instrumentation, type wiring, field wiring...), HTTP stuff (Spring Security, OAuth, OpenID Connect...) and much more. See the [[server FAQ|server_faq]] for more information on this.
    * Various options allows to personalize the generated code (standard JPA annotations, Java type for ID fields, custom scalars, specific annotations...). See the [plugin parameters page](https://graphql-maven-plugin-project.graphql-java-generator.com/graphql-maven-plugin/plugin-info.html) for all the goal/tasks and their plugin parameters
    * The developer just has to __implement each DataFetchersDelegate__, based on the provided interfaces, and __the GraphQL server is ready to go!__

Other points that are worth to point out:
* The project is __extensively tested__:
    * Through unit tests (for the runtime),
    * Through unit and integration tests (for the plugin logic)
    * Through full integration tests: three samples contain both the client and the server part, and integration tests are run on client side, against "its" server side. Around 150 integration tests are run on client side against the server part.
* A big effort is done to __avoid any impact on your code, when the plugin evolves__. 
* A maven/gradle goal/task allows to __merge several schemas in one__, adding (for instance) relay capability in the generated schema

__The interesting part is that graphql-java-generator is an accelerator: you don't depend on any library from graphql-java-generator__. So, it just helps you to build application based on [graphql-java](https://www.graphql-java.com).
At any time, you can take the generated code as a full sample for your graphql-java usage. You can then update the generated code, where it's not compliant for you. The other way is to use the personalization capability, and stay with the generated code as a basis.


## The plugin goals/tasks

All maven goals and gradle tasks are described on [this page](https://graphql-maven-plugin-project.graphql-java-generator.com/graphql-maven-plugin/plugin-info.html)

This plugin contains these goals (Maven) / tasks (Gradle):
* __`generateClientCode`__ : this goal generates the client code from the Graphql schema file(s)
    * You'll find all the details [on this page](https://graphql-maven-plugin-project.graphql-java-generator.com/graphql-maven-plugin/generateClientCode-mojo.html)
* __`generateServerCode`__ : this goal generates the server code from the Graphql schema file(s)
    * You'll find all the details [on this page](https://graphql-maven-plugin-project.graphql-java-generator.com/graphql-maven-plugin/generateServerCode-mojo.html)
* __`generatePojo`__ : this goal generates only the java objects that match the provided GraphQL schema. It allows to work
 * in Java with graphQL, in a schema first approach.
    * You'll find all the details [on this page](https://graphql-maven-plugin-project.graphql-java-generator.com/graphql-maven-plugin/generatePojo-mojo.html)
* __`graphql`__ was the previous main goal. It's now __deprecated__. It can generate both the client and the server code, thanks to its <mode> parameter. 
    * You'll find all the details [on this page](https://graphql-maven-plugin-project.graphql-java-generator.com/graphql-maven-plugin/graphql-mojo.html) 
* __`merge`__ allows to generate a GraphQL schema file, based on the source GraphQL schemas. It can be used to merge several GraphQL schema files into one file, or to reformat the schema files.
    * You'll find all the details [on this page](https://graphql-maven-plugin-project.graphql-java-generator.com/graphql-maven-plugin/merge-mojo.html) 

## Availability: Maven and Gradle

The generator is currently available both as a Maven plugin and as a Gradle plugin:

* The __Maven plugin__ is available in the project ([graphql-maven-plugin-project](https://github.com/graphql-java-generator/graphql-maven-plugin-project)) 

* A __Gradle plugin__ is available in the project [graphql-gradle-plugin-project](https://github.com/graphql-java-generator/graphql-gradle-plugin-project). It offers exactly the same functionalities.

## Documentation

### This README

This README gives you a quick overview of what this plugin offers

### The full project documentation

The [Full documentation](https://graphql-maven-plugin-project.graphql-java-generator.com/) is [available here](https://graphql-maven-plugin-project.graphql-java-generator.com/) , for both the Gradle and the Maven plugin.

For the client mode, you'll find there a description on how to:

* Start with a [Spring app](https://graphql-maven-plugin-project.graphql-java-generator.com/client_spring.html)

* Start with a [non Spring app](https://graphql-maven-plugin-project.graphql-java-generator.com/client_non_spring.html)

* [Execute GraphQL requests](https://graphql-maven-plugin-project.graphql-java-generator.com/exec_graphql_requests.html) with the help of this plugin

* [Customize the templates](https://graphql-maven-plugin-project.graphql-java-generator.com/customtemplates.html) to change the generated code, according to your needs.

* [Access to an OAuth2](https://graphql-maven-plugin-project.graphql-java-generator.com/client_oauth2.html) GraphQL server


For the server mode, you'll find explanations on how to:

* [Build a GraphQL server](https://graphql-maven-plugin-project.graphql-java-generator.com/server.html) with the help of this plugin

* [Implement subscriptions](https://graphql-maven-plugin-project.graphql-java-generator.com/server_subscription.html)

* [Personalize the generated code](https://graphql-maven-plugin-project.graphql-java-generator.com/schema_personalization.html)

* [Customize the templates](https://graphql-maven-plugin-project.graphql-java-generator.com/customtemplates.html) to change the generated code, according to your needs.

* Implement [an OAuth2 GraphQL server](https://graphql-maven-plugin-project.graphql-java-generator.com/server_oauth2.html)

### The tutorials

Two tutorials are available for the Maven plugin:
* The Client tutorial describes how to use this plugin, when building a GraphQL client. It's in the [GraphQL-Forum-Maven-Tutorial-client project](https://github.com/graphql-java-generator/GraphQL-Forum-Maven-Tutorial-client).
* The Server tutorial describes how to build a GraphQL server in the [GraphQL-Forum-Maven-Tutorial-server project](https://github.com/graphql-java-generator/GraphQL-Forum-Maven-Tutorial-server).

The same two tutorials exist for the Gradle plugin:
* The Client tutorial describes how to use this plugin, when building a GraphQL client. It's in the [GraphQL-Forum-Gradle-Tutorial-client git repository](https://github.com/graphql-java-generator/GraphQL-Forum-Gradle-Tutorial-client).
* The Server tutorial describes how to build a GraphQL server in the [GraphQL-Forum-Gradle-Tutorial-server](https://github.com/graphql-java-generator/GraphQL-Forum-Gradle-Tutorial-server).

## How to use it?

### Full project documentation

You'll find below a quick presentation of the plugin.

For all the available information, please go to the [project website](https://graphql-maven-plugin-project.graphql-java-generator.com/README.html)


### Plugin documentation

The plugin documentation, generated by maven, is available [on this page](https://graphql-maven-plugin-project.graphql-java-generator.com/graphql-maven-plugin/graphql-mojo.html)

### Samples

You'll find the following samples in the project. For all of these samples, there are two projects: the client and the server.

* allGraphQLCases
    * This project is a compilation of GraphQL capabilities ... that are managed by this plugin. Its main objective is for integration testing.
    * The server is packaged as a Spring Boot application
    * The GraphQL server exposes http
*  Forum
    * The server is packaged as a Spring Boot application
    * The GraphQL server exposes http
    * The server uses the schema personalization, to override the default code generation
    * The GraphQL model maps to the database model
    * The Forum client project shows implementation of the same queries in the XxxQueries classes.
    * This sample contains a subscription, on both the client and the server side 
* StarWars (only in the [Maven plugin](https://github.com/graphql-java-generator/graphql-maven-plugin-project) project)
    * The server is packaged as a war
    * The GraphQL server exposes https
    * The GraphQL interfaces in this model (character, and friends relation) makes it difficult to map to native RDBMS data model. This project uses JPA native queries to overcome this.
    * The StarWars client project shows implementation of the same queries in the XxxQueries classes.
    * This sample contains a subscription, on both the client and the server side 
 * CustomTemplates (only in the [Maven plugin](https://github.com/graphql-java-generator/graphql-maven-plugin-project) project)
    * An example related on how to customize code templates
		* graphql-maven-plugin-samples-CustomTemplates-resttemplate project offers a customize template for Query/Mutation/Subscriptino client class and offer a Spring-base RestTemplate implementation for QueryExecutor template
		* graphql-maven-plugin-samples-CustomTemplates-resttemplate proyect defines a client project that generates code with customized template defined in previous project

Note: The client projects for these samples contain integration tests. They are part of the global build. These integration tests check the graphql-maven-plugin behaviour for both the client and the server for these samples.

### Client mode

When in _client_ mode, you can query the server with just one line of code.

For instance :

```Java
String id = [an id];

Human human = queryType.human("{id name appearsIn homePlanet friends{name}}", id);
```

You can use input types:

```Java
HumanInput input = new HumanInput();
... [some initialization of input content]

Human human = mutationType.createHuman("{id name appearsIn friends {id name}}", input);
```


In this mode, the plugin generates:

* One java class for the Query object
* One java class for the Mutation object (if any)
* One POJO for each standard object of the GraphQL object
* All the necessary runtime is actually attached as source code into your project: the generated code is stand-alone. So, your project, when it runs, doesn't depend on any external dependency from graphql-java-generator.

You'll find more information on the [client](https://graphql-maven-plugin-project.graphql-java-generator.com/client.html) page.

### Server mode

When in server mode, the plugin generates:

* The main class:
    * When in a jar maven project, the main class to start a Spring Boot application
    * When in a war maven project, the servlet configuration to be embedded in a war package. It can then be deployed in any standard application server
* The declaration of all the [graphql-java](https://www.graphql-java.com/) stuff
* The DataFetchersDelegate interface declarations for all the [Data Fetchers](https://www.graphql-java.com/documentation/master/data-fetching/), which is the graphql-java word for GraphQL resolvers.
    * The DataFetchersDelegate groups the Data Fetchers into one class per GraphQL object (including the Query and the Mutation)
    * It contains the proper declarations to use the [DataLoader](https://github.com/graphql-java/java-dataloader) out of the box
* The POJOs to manipulate the GraphQL objects defined in the GraphQL schema.
    * These POJOs are annotated with JPA annotations. This allows you to link them to almost any database
    * You can customize these annotations, with the Schema Personalization file (see below for details)
* All the necessary runtime is actually attached as source code into your project: the generated code is stand-alone. So, your project, when it runs, doesn't depend on any external dependency from graphql-java-generator.

Once all this is generated, your only work is to implement the DataFetchersDelegate interfaces. They are the link between the GraphQL schema and your data storage. As such, they are specific to your use case. A DataFetchersDelegate implementation looks like this:

```Java
package com.graphql_java_generator.samples.forum.server.specific_code;

[imports]

@Component
public class DataFetchersDelegateTopicImpl implements DataFetchersDelegateTopic {

	@Resource
	MemberRepository memberRepository;
	@Resource
	PostRepository postRepository;
	@Resource
	TopicRepository topicRepository;

	@Resource
	GraphQLUtil graphQLUtil;

	@Override
	public CompletableFuture<Member> author(DataFetchingEnvironment dataFetchingEnvironment,
			DataLoader<UUID, Member> dataLoader, Topic source) {
		return dataLoader.load(source.getAuthorId());
	}

	@Override
	public List<Post> posts(DataFetchingEnvironment dataFetchingEnvironment, Topic source, String since) {
		if (since == null)
			return graphQLUtil.iterableToList(postRepository.findByTopicId(source.getId()));
		else
			return graphQLUtil.iterableToList(postRepository.findByTopicIdAndSince(source.getId(), since));
	}

	@Override
	public List<Topic> batchLoader(List<UUID> keys) {
		return topicRepository.findByIds(keys);
	}
}
```

You'll find all the info on the [server](https://graphql-maven-plugin-project.graphql-java-generator.com/server.html) page.



### Custom code templates

Customizing the templates allows you to modify the generated code, in the way you want. 

So, if for any reason you may need to customize the generated code, you can replace the default templates by your own, by using the plugin parameter **templates**: the **templates** plugin parameter is a map where the key is the ID of the template to customize and the value is a classpath entry to the resources containing the customized template.

Customize templates shall be provided in a dependency configured in the plugin.

Both client and server templates can be customized. 	

All the documentation, and the list of available templates is available in the [Customizing code templates](https://graphql-maven-plugin-project.graphql-java-generator.com/customtemplates.html) page.


# Compatibility with GraphQL

This plugin respects quite all the GraphQL specification:
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
- Comments coming from the GraphQL schema are reported in the generated code


# Change log

The Change Log is available [here](CHANGELOG.md)


# Note for contributors

All the plugin logic is stored in the [graphql-maven-plugin-project](https://github.com/graphql-java-generator/graphql-maven-plugin-project) project.

The [Maven plugin](https://github.com/graphql-java-generator/graphql-maven-plugin-project) and the [Gradle plugin](https://github.com/graphql-java-generator/graphql-gradle-plugin-project) are just wrapper for the plugin logic, available in the __graphql-maven-plugin-logic__ module of the maven project. 

If you want to compile the maven project, you'll have to add the lombok.jar file in your IDE. Please see the relevant section, in the Install menu of the [https://projectlombok.org/](https://projectlombok.org/) home page. This very nice tools generates all java boiler plate code, like setters, getters, constructors from fields...

If you use eclipse, please use the __code formatter__ given with the project (file _graphql-java-generator (eclipse code formatter).xml_ at the root of the project). This allows to have the sample code formatting: the code is then homogeneous, and the comparison between versions is simpler. To do this, go to the eclipse preferences, select Java/Code Style/Formatter, and import this file. Then, in the Java/Editor/Save Actions, check the "Perform the selected action on save", "Format source code", "Format all lines", "Organize imports" and "Additional actions" which its default content


# License

`graphql-java-generator` is licensed under the MIT License. See [LICENSE](LICENSE.md) for details.
