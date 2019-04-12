# GraphQL Maven Plugin : StarWars Server example

This project shows the use of the plugin to support the Star Wars graphql schema.

## General principles

The graphql-maven-plugin generates the maximum of boilerplate code, to avoid you to do it. This works very well with entities and scalar types.

But when it comes to association, it's another story. There are a lot of ways to build and query a database schema.

The Star Wars schema illustrates this:
* The _hero_ query returns the Character types. As Character is an interface, each Character can be either a Human or a Droid. There are two main ways to do that in a database. You can have:
    * One table _CHARACTER_ which contains the two entities (Human and Droid). This simplifies the id management (typically, it can be an id sequence from the database, shared by Human and Droid). And it helps a lot, when managing the friends association: it's just a many to many association between the "character" table and itself.
    * One table for Human and one table for Droid. It's probably not the best choice here, as 80% of the field are commons. But it's not always the case. And you'll probably have to use this pattern in GraphQL complex cases. This pattern is probably the best choice, when a GraphQL object type implements two or more interfaces (with the first pattern, you'll have to duplicate or synchronize data of an entity between two tables).
* The _friends_ association of Character (so: of both Human and Droid) is a many to many association. So you need an association table to manage it. Let's take a look at these two patterns described here above:
    * With one _CHARACTER_ table for all concrete classes which implements this interface, you only need one association table. JPA would generate a _CHARACTER_FRIENDS_ table.
    * With two tables, a _HUMAN_ and a _DROID_ tables, you have two ways to manage this association. To manage this association by standard association table, you need for tables for two entities implementing the _Character_ interface (droid to droid, human to human, droid to human and human to droid). With 4 entities implementing it, you need 16 association tables... So you'll need to find a solution of you own. For instance a _CHARACTER_FRIENDS_ table, which contains these four columns: character_id, character_type (here: human or droid), friend_id, friend_type (human or droid). And of course, any query against this table would be a very specific one.
* The _appearsIn_ association is again a way to show that there are a lot of ways to build a database.
    * In a proper built database, you'll have an _EPISODE_ table. And _appearsIn_ would be a many to many association between the _CHARACTER_ table, or the _HUMAN_ and _DROID_ tables.
    * In a database driven by Java code, you may end with no _EPISODE_ tables. The Java code would take care of the valid values of the enumeration.

All these elements make impossible for the graphql-maven-plugin to generate a proper code that you would just use directly.

## What the graphql-maven-plugin does

It generates:
* the _root_ package:
     * The GraphQLServer class. It contains the _main_ method. This _main_ method is the entry point of the Spring Boot application.
    * The GraphQLProvider class contains the initialization of the GraphQL stuff, based on the graphql-java and the graphql-java-spring-boot-starter-webmvc projects.
    * The GraphQLDataFetchers class contains all the data fetchers for the queries and the associations.   
* The _graphql_ package contains all the GraphQL types based on the GraphQL schema
* The _jpa_ package contains all the JPA repositories and other stuff for this use case


## What the graphql-maven-plugin can't do

It can