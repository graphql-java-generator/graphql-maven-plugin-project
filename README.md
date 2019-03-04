# GraphQL Maven Plugin

This project is a maven plugin, which makes it possible to work with graphQL in a schema first approach.

It's a work in progress. But the generated code is already ready to use.

### What the plugin can't manage (as of its current state)

- Reading of graphqls is only possible within the classpath of the plugin (not in the src/main/resources, for instance)
- Scalars
- Comments are not reported in the generated code

### Note for contributors

This projet is a maven plugin project. 

If you want to compile it, you'll have to add the lombok.jar file in your IDE. Please see the relevant section, in the Install menu of the [https://projectlombok.org/][lombok] home page. This very nice tools generates all java boiler plate code, like setters, getters, constructors from fields...



# License

`graphql-maven-plugin` is licensed under the MIT License. See [LICENSE](LICENSE.md) for details.

[lombok]: https://projectlombok.org/