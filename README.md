# GraphQL Maven Plugin

This project is a maven plugin, which makes it possible to work with graphQL in a schema first approach.

It's a work in progress. But the generated code is already ready to use.

### What the plugin can't manage (as of its current state)

- Fragment in graphql queries
- Scalars
- queries (mutations...) that return data not defined as an object (e.g.: two objects). This needs to be wrapped in some kind of container.
- Comments are not reported in the generated code
- Date, DateTime, Time

### Dependencies needed for the generated code

The generated code depends on:
- log4j2


A typical POM for a project which would use this maven plugin is :

<?xml version="1.0"?>
<project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd"
	xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<modelVersion>4.0.0</modelVersion>
	<artifactId>myArtifact</artifactId>
	<build>
		<plugins>
			<plugin>
				<groupId>com.graphql-java</groupId>
				<artifactId>graphql-maven-plugin</artifactId>
				<version>...</version>
				<executions>
					<execution>
						<goals>
							<goal>graphql</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>
	<dependencies>
		<!-- log4j2 is needed for logging -->
		<dependency>
			<groupId>org.apache.logging.log4j</groupId>
			<artifactId>log4j-api</artifactId>
			<version>2.11.2</version>
		</dependency>
		<dependency>
			<groupId>org.apache.logging.log4j</groupId>
			<artifactId>log4j-core</artifactId>
			<version>2.11.2</version>
		</dependency>
	</dependencies>
</project>


### Note for contributors

This projet is a maven plugin project. 

If you want to compile it, you'll have to add the lombok.jar file in your IDE. Please see the relevant section, in the Install menu of the [https://projectlombok.org/][lombok] home page. This very nice tools generates all java boiler plate code, like setters, getters, constructors from fields...



# License

`graphql-maven-plugin` is licensed under the MIT License. See [LICENSE](LICENSE.md) for details.

[lombok]: https://projectlombok.org/