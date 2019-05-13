package org.graphql.maven.plugin.samples.Forum.client;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class GraphqlClient implements CommandLineRunner {

	/** Logger for this class */
	private static Logger logger = LogManager.getLogger();

	@Resource
	GraphQL graphQL;

	public static void main(String[] args) {
		SpringApplication.run(GraphqlClient.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		logger.info("Application Started !!");
		ExecutionInput executionInput = ExecutionInput.newExecutionInput()
				.query("{hero(episode:NEWHOPE) {id appearsIn friends {name}}}").build();
		ExecutionResult executionResult = graphQL.execute(executionInput);
		Object data = executionResult.getData();
		logger.info(data);
		logger.info("Application Finished !!");
	}

}
