# GraphQL Maven Plugin (client mode)

When configuring the graphql-maven-plugin in client mode, it will generate all the necessary code to make it easy to call a GraphQL server. The code is generated from the given GraphQL schema. 

Basically, it will generate:

* One java class for the Query object
* One java class for the Mutation objet (if any)
* One POJO for each standard object of the GraphQL object

All the necessary runtime is added into the generated code. That is: your project, when it runs, doesn't depend on any dependency from graphql-java-generator.

This is why we call it an accelerator: you can generate the code once, and get rid of graphql-java-generator if you wish.

BTW: we think its better to continue using it, but you're free to leave, and keep the generated code.  
:)

