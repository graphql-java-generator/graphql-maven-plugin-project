package org.allGraphQLCases.minimal.spring_app;

/**
 * This package contains a demo for the use of the plugin as a Spring Boot app, which is the recommended way to go.<BR/>
 * It contains:
 * <UL>
 * <LI><B>GraphQLRequests</B>: the {@link com.graphql_java_generator.annotation.GraphQLRepository} that contains the
 * GraphQL requests (query, mutation, subscription) used by this application. It's "just" an interface that defines the
 * expected methods. Behind the scene, the plugin takes care of wiring these method, and executes the relevant
 * code.</LI>
 * <LI><B>MinimalSpringApp</B>: the application itself, that calls the
 * {@link org.allGraphQLCases.minimal.spring_app.GraphQLRequests} methods</LI>
 * <LI><B>application.properties</B>: the application properties contain, at least, the GraphQL endpoint URL</LI>
 * </UL>
 */