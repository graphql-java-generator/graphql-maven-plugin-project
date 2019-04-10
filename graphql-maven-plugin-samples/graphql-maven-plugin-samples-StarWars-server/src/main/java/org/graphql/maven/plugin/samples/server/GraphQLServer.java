package org.graphql.maven.plugin.samples.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableConfigurationProperties
public class GraphQLServer {

	public static void main(String[] args) {
		SpringApplication.run(GraphQLServer.class, args);
	}

}
