
# Questions about the server configuration


## HowTo to change the server port and path

The server is a Spring Boot app or servlet. It is configured through one of the `application.properties` or the `application.yml` files. It should be available in the root of the classpath, so you should provide is in the project's _src/main/resources_ folder.

The below sample is based on the _allGraphQLCases-server_, available as a sample in the Maven and Gradle projects:

```yml
# Changing the port for the GraphQL server
server:
  port: 8180


# Changing the server path
graphql:
  url: /my/updated/graphql/path
```