<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**

- [Project Overview](#project-overview)
  - [Key Features](#key-features)
- [Building and Running](#building-and-running)
- [Development Conventions](#development-conventions)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Project Overview

This project is a Java-based code generator for GraphQL. It simplifies the development of GraphQL clients and servers by generating boilerplate code from a GraphQL schema. This allows developers to focus on the business logic of their applications.

The project is built with Java and Maven, and it provides both a Maven plugin and a Gradle plugin for integration into the build process. The core logic is written in Java and uses the Spring Framework.

## Key Features

*   **Code Generation:** Generates Java classes (POJOs) from a GraphQL schema.
*   **Client Generation:** Generates client code to execute queries, mutations, and subscriptions against a GraphQL server.
*   **Server Generation:** Generates server-side code to handle GraphQL requests, requiring the developer to implement the data fetching logic.
*   **Maven and Gradle Plugins:** Provides plugins for easy integration with Maven and Gradle builds.
*   **Spring Framework:** Based on the Spring Framework for dependency injection and other features.

# Building and Running

This is a Maven project. To build the project, run the following command from the root directory:

```bash
mvn clean install
```

To run the tests, use the following command:

```bash
mvn test
```

# Development Conventions

*   **Code Style:** The project uses a specific Eclipse code formatter, which can be found in the `graphql-java-generator (eclipse code formatter).xml` file.
*   **Testing:** The project has a comprehensive test suite, including unit tests and integration tests.
*   **Dependencies:** The project uses Maven to manage dependencies. Key dependencies include Spring Framework, GraphQL Java, and various Maven plugins.
*   **Modularity:** The project is divided into several modules, each with a specific purpose. The core logic is in the `graphql-maven-plugin-logic` module, and the Maven plugin is in the `graphql-maven-plugin` module.
