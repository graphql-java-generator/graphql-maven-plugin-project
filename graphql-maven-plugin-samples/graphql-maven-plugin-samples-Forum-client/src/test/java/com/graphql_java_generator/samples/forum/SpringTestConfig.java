package com.graphql_java_generator.samples.forum;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

import com.graphql_java_generator.client.GraphQLConfiguration;

@TestConfiguration
@ComponentScan(basePackageClasses = { SpringTestConfig.class, GraphQLConfiguration.class }, excludeFilters = {
		@Filter(type = FilterType.REGEX, pattern = "graphql\\..*") })
public class SpringTestConfig {

}
