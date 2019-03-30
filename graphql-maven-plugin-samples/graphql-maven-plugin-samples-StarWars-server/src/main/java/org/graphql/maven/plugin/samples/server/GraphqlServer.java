package org.graphql.maven.plugin.samples.server;

import javax.annotation.Resource;

import org.graphql.maven.plugin.samples.server.generated.CharacterImpl;
import org.graphql.maven.plugin.samples.server.jpa.DatabaseTools;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

import com.coxautodev.graphql.tools.SchemaParserDictionary;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableConfigurationProperties
public class GraphqlServer {

	@Resource
	DatabaseTools databaseTools;

	public static void main(String[] args) {
		SpringApplication.run(GraphqlServer.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void initDatabase() throws Exception {
		databaseTools.initDatabase();
	}

	@Bean
	public SchemaParserDictionary schemaParserDictionary() {
		return new SchemaParserDictionary().add("Character", CharacterImpl.class);
	}

}
