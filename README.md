# GraphQL Java Generator

The GraphQL Java Generator makes it easy to work in Java with graphQL in a schema first approach.

This project is an accelerator to develop __GraphQL clients__ and __GraphQL servers__ in java.

That is: graphql-java-generator generates the boilerplate code, and lets you concentrate on what's specific to your use case. Then, the running code __doesn't depend on any dependencies from graphql-java-generator__. So you can get rid of graphql-java-generator at any time: just put the generated code in your SCM, and that's it.

* In __client mode__ : graphql-java-generator generates a class for each query and mutation type (subscriptions are not managed yet). These classes contain the methods to call the queries and mutations. That is, to call the GraphQL server, you just call one of this method.
    * graphql-java-generator also generates the POJOs from the GraphQL schema. The __GraphQL response is stored in these POJOs__, for easy and standard use in Java.
* In __server mode__ : graphql-java-generator generates the whole heart of the GraphQL server. The developer has only to develop request to the data. That is :
    * graphql-java-generator generates the main method (in a jar project) or the main servler (in a war project), and all the Spring wiring, based on [graphql-java-spring](https://github.com/graphql-java/graphql-java-spring), itself being build on top of [graphql-java](https://www.graphql-java.com/).
    * It generates the POJOs with the standard JPA annotations, to make it easy to link with a database. But of course, you can also implement your GraphQL server based on REST resources, or any other kind of data storage.  
    * graphql-java-generator also generates interfaces, named DataFetchersDelegate. It expects a Spring Bean to be defined.
    * The developer just has to implement each DataFetchersDelegate, and the GraphQL server is ready to go!

Please, take a look at the projects that are within the graphql-maven-plugin-samples: they show various ways to implement a GraphQL server, based on the graphql-java library.

__The interesting part is that graphql-java-generator is just an accelerator: you don't depend on any library from graphql-java-generator__. So, it just helps you to build application based on [graphql-java](https://www.graphql-java.com) .
If the generated code doesn't fully suit your needs, you can take what's generated as a full sample for graphql-java usage, based on your use case. You can then update the generated code, where it's not compliant for you. And that's it. The only thing, there, is that we would like to know what was not correct for your use case, so that we can embed it into next versions. Or perhaps, if it's just a matter of documentation, to better explain how to use it...

The generator is currently available as a maven plugin. A Gradle plugin will come soon.


## Aim of this project

The aim of this project is to:

* Hide all the GraphQL technical stuff and boilerplate code
* Let the developer concentrate on his/her specific use case
* Make it __very easy__ to create a GaphQL client, based on the generated POJOs. The calls to the GraphQL server are hidden. The client code just call a generated Java method, with Java parameters.
* Make it __easy__ to create a GraphQL server. The plugin generated the server boilerplate code and the POJOs. But it's still up to the developer to map the GraphQL schema to the database schema. See the provided samples for different ways to do this. The generated POJOs integrate the JPA schema, making the database access easy, thanks to the Spring Data Repositories.
* Let the generated code work as a standalone code. That is: your project, when it runs, doesn't depend on any dependency from graphql-java-generator.

## How to use it?

### Full projet documentation

You'll find below a quick presentation of the plugin.

For all the available information, please go to the [project website](https://graphql-maven-plugin-project.graphql-java-generator.com/)

### Samples

You'll find the following samples in the project. For all of these samples, there are two projects: the client and the server.

* Basic
    * The simplest sample: "Hello World" ! :) Start from them, when you want to start a new project
    * It also contains an "error" query, which always returns an error. This allows you to check how to manage errors returned when executing a GraphQL query.
    * The server is packaged as a Spring Boot application
    * The GraphQL server exposes http
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
* StarWars
    * The server is packaged as a war
    * The GraphQL server exposes https
    * The GraphQL interfaces in this model (character, and friends relation) makes it difficult to map to native RDBMS data model. This project uses JPA native queries to overcome this.
    * The StarWars client project shows implementation of the same queries in the XxxQueries classes.
 * CustomTemplates
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

Or, with bind parameters:

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

# Plugin GraphQL compatibility

The plugin currently manages this part of GraphQL specifications:
- Object Type
- Schema, Queries and Mutation types
- Custom Scalars
- Input Parameters
- Interfaces
- Directives
- Alias on field name (not on query or mutation names)
- GraphQL names that are java keyword (out of enum items)



### Custom code templates

If for any reason you may need to customize the template to modify the generated code this can be donde using the parameter **tempaltes**

Here there's an exmaple of plugin configuration to use customized templates

```
<project ...>
...

<build>
<plugins>
...
	<plugin>
		<groupId>com.graphql-java-generator</groupId>
		<artifactId>graphql-maven-plugin</artifactId>
		<version>${lastReleasedVersion}</version>

		<executions>
			<execution>
				<goals>
					<goal>graphql</goal>
				</goals>
			</execution>
		</executions>
		<configuration>
			<mode>client</mode>
			<templates>
				<QUERY_MUTATION_SUBSCRIPTION>classpath/entry/to/cutomtemplate.java.vm</QUERY_MUTATION_SUBSCRIPTION>
			</templates>
		</configuration>
		<dependencies>

			<!-- Dependency containing your templates-->
			<dependency>
				<groupId>...</groupId>
				<artifactId>...</artifactId>
				<version>...</version>
			</dependency>

		</dependencies>
	</plugin>
</plugins>
</build>
...
</project>
```
**templates** param is a map where the key is the ID of the template to customize
and the value is a classpath entry to the resources containing the customized tempalte

Customize templates shall be provided in a depdency configured in the plugin

Both client and server templates can be customized. 	

The avialable template IDs that can be configured for customization are:

| ID | Scope | Default template |
| --- | --- |
| OBJECT | COMMON | [templates/object_type.vm.java](http://github.com/graphql-java-generator/graphql-maven-plugin-project/tree/master/graphql-maven-plugin-logic/src/main/resources/templates/object_type.vm.java) |
| INTERFACE | COMMON | [templates/interface_type.vm.java](http://github.com/graphql-java-generator/graphql-maven-plugin-project/tree/master/graphql-maven-plugin-logic/src/main/resources/templates/interface_type.vm.java) |
| ENUM | COMMON | [templates/enum_type.vm.java](http://github.com/graphql-java-generator/graphql-maven-plugin-project/tree/master/graphql-maven-plugin-logic/src/main/resources/templates/enum_type.vm.java) |
| UNION | COMMON | [templates/union_type.vm.java](http://github.com/graphql-java-generator/graphql-maven-plugin-project/tree/master/graphql-maven-plugin-logic/src/main/resources/templates/union_type.vm.java) |
| CUSTOM_SCALAR_REGISTRY_INITIALIZER | CLIENT | [templates/client_CustomScalarRegistryInitializer.vm.java](http://github.com/graphql-java-generator/graphql-maven-plugin-project/tree/master/graphql-maven-plugin-logic/src/main/resources/templates/client_CustomScalarRegistryInitializer.vm.java) |
| QUERY_MUTATION_SUBSCRIPTION | CLIENT | [templates/client_query_mutation_subscription_type.vm.java](http://github.com/graphql-java-generator/graphql-maven-plugin-project/tree/master/graphql-maven-plugin-logic/src/main/resources/templates/client_query_mutation_subscription_type.vm.java) |
| QUERY_TARGET_TYPE | CLIENT | [templates/client_query_target_type.vm.java](http://github.com/graphql-java-generator/graphql-maven-plugin-project/tree/master/graphql-maven-plugin-logic/src/main/resources/templates/client_query_target_type.vm.java) |
| JACKSON_DESERIALIZER | CLIENT | [templates/client_jackson_deserialize.vm.java](http://github.com/graphql-java-generator/graphql-maven-plugin-project/tree/master/graphql-maven-plugin-logic/src/main/resources/templates/client_jackson_deserialize.vm.java) |
| BATCHLOADERDELEGATE | SERVER | [templates/server_BatchLoaderDelegate.vm.java](http://github.com/graphql-java-generator/graphql-maven-plugin-project/tree/master/graphql-maven-plugin-logic/src/main/resources/templates/server_BatchLoaderDelegate.vm.java) |
| BATCHLOADERDELEGATEIMPL | SERVER | [templates/server_BatchLoaderDelegateImpl.vm.java](http://github.com/graphql-java-generator/graphql-maven-plugin-project/tree/master/graphql-maven-plugin-logic/src/main/resources/templates/server_BatchLoaderDelegateImpl.vm.java) |
| DATAFETCHER | SERVER | [templates/server_GraphQLDataFetchers.vm.java](http://github.com/graphql-java-generator/graphql-maven-plugin-project/tree/master/graphql-maven-plugin-logic/src/main/resources/templates/server_GraphQLDataFetchers.vm.java) |
| DATAFETCHERDELEGATE | SERVER | [templates/server_GraphQLDataFetchersDelegate.vm.java](http://github.com/graphql-java-generator/graphql-maven-plugin-project/tree/master/graphql-maven-plugin-logic/src/main/resources/templates/server_GraphQLDataFetchersDelegate.vm.java) |
| GRAPHQLUTIL | SERVER | [templates/server_GraphQLUtil.vm.java](http://github.com/graphql-java-generator/graphql-maven-plugin-project/tree/master/graphql-maven-plugin-logic/src/main/resources/templates/server_GraphQLUtil.vm.java) |
| PROVIDER | SERVER | [templates/server_GraphQLProvider.vm.java](http://github.com/graphql-java-generator/graphql-maven-plugin-project/tree/master/graphql-maven-plugin-logic/src/main/resources/templates/server_GraphQLProvider.vm.java) |
| SERVER | SERVER | [templates/server_GraphQLServerMain.vm.java](http://github.com/graphql-java-generator/graphql-maven-plugin-project/tree/master/graphql-maven-plugin-logic/src/main/resources/templates/server_GraphQLServerMain.vm.java) |


# Main evolutions for the near future

You'll find below the main changes, that are planned in the near future:
- Union
- Fragment in graphql queries
- Subscriptions. Currently, GraphQL Java Generator manages queries and mutations.
- Add a gradle plugin (work in progress)
- Manage properties which name are java keyword, like: public, private, class... Currently, it would generate a compilation error.
- Comments should be reported in the generated code, especially the POJOs and the queries, mutations and subscriptions


# Change log

The Change Log is available [here](CHANGELOG.md)


# Note for contributors

This project is a maven plugin project. 

If you want to compile it, you'll have to add the lombok.jar file in your IDE. Please see the relevant section, in the Install menu of the [https://projectlombok.org/](https://projectlombok.org/) home page. This very nice tools generates all java boiler plate code, like setters, getters, constructors from fields...

If you use eclipse, please use the __code formatter__ given with the project (file _graphql-java-generator (eclipse code formatter).xml_ at the root of the project). This allows to have the sample code formatting: the code is then homogeneous, and the comparison between versions is simpler. To do this, go to the eclipse preferences, select Java/Code Style/Formatter, and import this file. Then, in the Java/Editor/Save Actions, check the "Perform the selected action on save", "Format source code", "Format all lines", "Organize imports" and "Additional actions" which its default content



# Full projet documentation

For all the available information, please go to the [project website](https://graphql-maven-plugin-project.graphql-java-generator.com/)

# License

`graphql-java-generator` is licensed under the MIT License. See [LICENSE](LICENSE.md) for details.
