### Schema Personalization (for server mode)

## Presentation

The GraphQL maven plugin generates java code, based on the given GraphQL schema(s). This gives you a defaut behavior that is suitable for standard cases. 
So, if you're starting a new GraphQL server project, it can happen that the default behavior is enough for you.

But the generated code can not handle all the combinations that exist in all your projects:
* Retrieve data from APIs instead of a JPA database
* Complementary data that needs to be stored or retrieved from the database
* Various ways to manage relations between entities
* Various ways to handle the GraphQL interfaces or unions
* ...

So the GraphQL maven plugin allows you to personalize the way the code is generated, to suit your needs.

This personalization allows you to:
* Add fields to existing entities
* Add or replace annotation for fields defined in the GraphQL schema
* Add or replace annotation for entities defined in the GraphQL schema


You can also create new Entities, by your own. Just put them in sub-package of the package which contains the GraphQLServer class, and they will be scanned at runtime.


## General structure of the file



## Add fields to existing entities

This can be very useful for various cases:
* To properly manage the relations between objects. JPA allows all the standard ways. But if you let JPA manage the associations, you'll have performance issues. It's better to
have a specific code, using the GraphQL data loader. More details here: [https://github.com/graphql-java/java-dataloader](https://github.com/graphql-java/java-dataloader) 
* When complementary data is needed to compute the fields that are used in the GraphQl schema. For instance, when a field distance has an input parameters, you may need
other data to manage all the possibilities.
* ...

  

## Add annotation to fields defined in the GraphQL schema

## Replace annotation for fields defined in the GraphQL schema

## Add annotation to entities defined in the GraphQL schema

## Replace annotation for entities defined in the GraphQL schema
