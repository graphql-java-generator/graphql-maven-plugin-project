package org.graphql.maven.plugin.samples.server;

import javax.annotation.Resource;

import org.graphql.maven.plugin.samples.server.jpa.DatabaseTools;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableConfigurationProperties
public class GraphQLServer {

	@Resource
	DatabaseTools databaseTools;

	public static void main(String[] args) {
		SpringApplication.run(GraphQLServer.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void initDatabase() throws Exception {
		databaseTools.initDatabase();
	}

}
