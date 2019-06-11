# GraphQL Java Generator Runtime


This project contains the necessary runtime for both the client (the whole stuff) and the server (the annotations).

It's __not used as a dependency__.


Instead, the source from this module is added to the generated code. This allow the generated code to depend on __no dependency from graphql-java_generator__. That is: at any moment, you can decide to take and hack the generated code on your side, and get rid of graphql-java_generator.