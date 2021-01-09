package org.allGraphQLCases;

import org.allGraphQLCases.client.util.MyQueryTypeExecutor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import com.graphql_java_generator.client.GraphQLConfiguration;

@Configuration
@PropertySource("classpath:/application.properties")
@Import(Main.class)
@ComponentScan(basePackageClasses = { GraphQLConfiguration.class, MyQueryTypeExecutor.class })
public class SpringTestConfig {

}
