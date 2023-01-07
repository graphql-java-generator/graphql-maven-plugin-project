package com.graphql_java_generator.samples.forum.test;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;

import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.client.graphqlrepository.EnableGraphQLRepositories;
import com.graphql_java_generator.samples.forum.client.DirectQueriesWithFieldInputParameters;
import com.graphql_java_generator.samples.forum.client.graphql.PartialPreparedRequests;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Query;

@TestConfiguration
@SpringBootApplication
@ComponentScan(basePackageClasses = { GraphqlClientUtils.class, Query.class, PartialPreparedRequests.class,
		DirectQueriesWithFieldInputParameters.class })
@EnableGraphQLRepositories({ "com.graphql_java_generator.samples.forum.client.graphql" })
public class SpringTestConfig {

}
