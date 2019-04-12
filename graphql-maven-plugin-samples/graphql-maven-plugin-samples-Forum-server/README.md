# GraphQL Maven Plugin : Forum Server example

This project shows the use of the plugin to support a forum. Its aim is to demonstrate that on a "standard" schema (no interface, no union), the graphql-maven-plugin generates the whole project.

## The data model

The data model for this sample is a standard data model, based on a forum use case. It's based on the simple machine (https://wiki.simplemachines.org) board system, for the object hierarchy.
 
It contains these objects:

* Member: a member is a user who registered to the forum. Depending on the forum configuration, and his type, he may create new Boards, new Topics or post new Post. 
* MemberType: enumeration. Each member is of one type. It can be one of: ADMIN, MODERATOR, STANDARD.
* Board: each forum contains one or more boards, where topics can be created.
* Topic: each new subject posted in a board. Each Board contains zero or more Topics. It typically leads to a discussion thread.
* Post: each response to a topic, if any. That is: the threads are flat. From a topic, you can read the list of post attached to it, as a list.

Of course simple machine is a little bit more complex... 

But you never know. Perhaps this sample will be better than the original !   ;-)

## The GraphQL schema

TODO: put the URL

The schema is a basic one, with no interface or union. It contains one enumeration: MemberType. 

  
