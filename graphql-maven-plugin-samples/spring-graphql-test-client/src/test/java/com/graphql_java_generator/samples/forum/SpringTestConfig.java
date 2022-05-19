package com.graphql_java_generator.samples.forum;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.client.graphqlrepository.EnableGraphQLRepositories;

@TestConfiguration
@ComponentScan(basePackageClasses = { SpringTestConfig.class, GraphqlClientUtils.class }, excludeFilters = {
		@Filter(type = FilterType.REGEX, pattern = "graphql\\..*") })
@EnableGraphQLRepositories({ "com.graphql_java_generator.samples.forum.client.graphql" })
public class SpringTestConfig {

}
