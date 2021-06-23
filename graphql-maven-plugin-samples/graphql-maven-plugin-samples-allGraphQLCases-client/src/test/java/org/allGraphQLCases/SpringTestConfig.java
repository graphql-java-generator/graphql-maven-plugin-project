package org.allGraphQLCases;

import org.allGraphQLCases.client.util.MyQueryTypeExecutor;
import org.allGraphQLCases.demo.Main;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import com.graphql_java_generator.client.GraphQLConfiguration;
import com.graphql_java_generator.client.graphqlrepository.EnableGraphQLRepositories;

@Configuration
@PropertySource("classpath:/application.properties")
@Import(Main.class)
@ComponentScan(basePackageClasses = { GraphQLConfiguration.class, MyQueryTypeExecutor.class })
@EnableGraphQLRepositories({ "org.allGraphQLCases.demo.impl" })
public class SpringTestConfig {

}
