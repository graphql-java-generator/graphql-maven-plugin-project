/**
 * 
 */
package com.spring_graphql_test_client;

import org.forum.generated.util.QueryExecutor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.graphql_java_generator.client.GraphQLConfiguration;
import com.graphql_java_generator.client.graphqlrepository.EnableGraphQLRepositories;

/**
 * This simple class is the minimal code to start a Spring application
 * 
 * @author etienne-sf
 *
 */
@SpringBootApplication(scanBasePackageClasses = { Main.class, GraphQLConfiguration.class, QueryExecutor.class })
@EnableGraphQLRepositories({ "com.spring_graphql_test_client" })
public class Main {

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

}
