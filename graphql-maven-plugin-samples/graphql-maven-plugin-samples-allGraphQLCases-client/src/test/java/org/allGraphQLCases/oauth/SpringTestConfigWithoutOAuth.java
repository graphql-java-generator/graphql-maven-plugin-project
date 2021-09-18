package org.allGraphQLCases.oauth;

import org.allGraphQLCases.client.util.MyQueryTypeExecutorAllGraphQLCases;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.graphql_java_generator.client.GraphQLConfiguration;

/**
 * A Spring configuration without OAuth, to check that the OAuth authentication is active on server side
 * 
 * @author etienne-sf
 */
@Configuration
@EnableAutoConfiguration
@PropertySource("classpath:/application.properties")
// No OAuth configuration from the Main class @Import(Main.class)
@ComponentScan(basePackageClasses = { GraphQLConfiguration.class, MyQueryTypeExecutorAllGraphQLCases.class })
public class SpringTestConfigWithoutOAuth {

}
