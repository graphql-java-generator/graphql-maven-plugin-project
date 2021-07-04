package org.allGraphQLCases.two_graphql_servers;

import org.allGraphQLCases.client.util.MyQueryTypeExecutor;
import org.allGraphQLCases.demo.Main;
import org.forum.client.util.QueryTypeExecutor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.graphql_java_generator.client.GraphQLConfiguration;

@Configuration
@SpringBootApplication(scanBasePackageClasses = { Main.class, GraphQLConfiguration.class, MyQueryTypeExecutor.class })
@PropertySource("classpath:/application.properties")
@ComponentScan(basePackageClasses = { GraphQLConfiguration.class, MyQueryTypeExecutor.class, QueryTypeExecutor.class })
// @EnableGraphQLRepositories({ "org.allGraphQLCases.two_graphql_servers" })
public class TwoGraphQLServersSpringConfig {
}
