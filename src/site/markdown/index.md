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

## How to use it

### Samples

You'll find these samples in the project. For all of these samples, there are two projects: the client and the server.

* Basic
    * The simplest samples. Start from them, when you want to start a new project
* StarWars
    * The server is packaged as a war
    * The GraphQL server exposes https
    * The data model is directly compatible with the generated code  
*  Forum
    * The server is packaged as a Spring Boot application
    * The GraphQL server exposes http
    * The server uses the schema personalization, to overcome the default code generation

Note: The client project for the StarWars and Forum samples contains integration tests. They are part of the global build. As such, the client projects contains integration tests that allow to check the graphql-maven-plugin for both the client and the server for these two projects. 

### Client mode

When in _client_ mode, you can query the server with just one line of code.

For instance :

```Java
Human human = queryType.human("{id name appearsIn homePlanet friends{name}}", "180");
```

In this mode, the plugin generates:

* One java class for the Query object
* One java class for the Mutation object (if any)
* One POJO for each standard object of the GraphQL object

The generated code is stand-alone. That is: your project, when it runs, it doesn't depend on any dependency from graphql-java-generator.


You'll find more information on the [client](client.html) page.

### Server mode

When in server mode, the plugin generates:

* When in a jar maven project, the main class to start a Spring Boot application
* When in a war maven project, the servlet configuration to be embedded in a war package. It can then be deployed in any standard application server
* The declaration to [graphql-java](https://www.graphql-java.com/), which is the only GraphQL dependency. It's actually mandatory for any GraphQL implementation in Java
* The DataFetcherDelegate interface declarations for all the [Data Fetchers](https://www.graphql-java.com/documentation/master/data-fetching/), which is the graphql-java word for GraphQL resolvers.
* The wiring of these Data Fetchers with the graphql-java
* The POJOs to manipulate the GraphQL objects defined in the GraphQL schema. 
    * These POJOs are annotated with JPA annotations. This allows to link them to almost any database
    * You can customize these annotations, with the Schema Personalization file (see below for details)
    * (in a near future) It will be possible to define your own code template, to generate exactly the code you want 
 
In a near future, graphql-java-generator will also provide an implementation for the dataloader, which will improve performances, see [https://github.com/graphql-java/java-dataloader](https://github.com/graphql-java/java-dataloader).

Once all this is generated, you'll have to implement the DataFetcherDelegate interfaces. The DataFetcherDelegate implementation is the only work that remains on your side. They are the link between the GraphQL schema and your data storage. A DataFetcherDelegate implementation looks like this:

```Java
@Component
public class TopicDataFetchersDelegateImpl implements TopicDataFetchersDelegate {

	@Resource
	MemberRepository memberRepository;
	@Resource
	PostRepository postRepository;
	@Resource
	GraphQLUtil graphQLUtil;

	@Override
	public Member author(DataFetchingEnvironment dataFetchingEnvironment, Topic source) {
		return memberRepository.findById(source.getAuthorId()).get();
	}

	@Override
	public List<Post> posts(DataFetchingEnvironment dataFetchingEnvironment, Topic source, String since) {
		if (since == null)
			return graphQLUtil.iterableToList(postRepository.findByTopicId(source.getId()));
		else
			return graphQLUtil.iterableToList(postRepository.findByTopicIdAndSince(source.getId(), since));
	}
}
```

You'll find all the info on the [server](server.html) page.

# Main evolutions for the near future

You'll find below the main changes, that are foreseen in the near future
- Manage Subscriptions. Currently, GraphQL Java Generator manages queries and mutations.
- Add a gradle plugin (work in progress)
- Manage properties which name are java keyword, like: public, private, class... Currently, it would generate a compilation error.
- Manage field parameters. Currently, GraphQL Java Generator accepts parameters out of the query level (that is on object fields), only with Direct Queries (which is nice enough to begin)
- Comments should be reported in the generated code, especially the POJOs and the queries, mutations and subscriptions
- Define specific Scalars (for instance Date, DateTime, Time)
- Fragment in graphql queries
- The plugin currently manages only one schema. It would be nice to allow several graphqls files, with a pattern like /*.graphqls


# Note for contributors

This project is a maven plugin project. 

If you want to compile it, you'll have to add the lombok.jar file in your IDE. Please see the relevant section, in the Install menu of the [https://projectlombok.org/](https://projectlombok.org/) home page. This very nice tools generates all java boiler plate code, like setters, getters, constructors from fields...

If you use eclipse, please use the __code formatter__ given with the project (file _graphql-java-generator (eclipse code formatter).xml_ at the root of the project). This allows to have the sample code formatting: the code is then homogeneous, and the comparison between versions is simpler. To do this, go to the eclipse preferences, select Java/Code Style/Formatter, and import this file. Then, in the Java/Editor/Save Actions, check the "Perform the selected action on save", "Format source code", "Format all lines", "Organize imports" and "Additional actions" which its default content


# License

`graphql-java-generator` is licensed under the MIT License. See [LICENSE](LICENSE.md) for details.
