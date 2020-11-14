package com.graphql_java_generator.samples.forum;

import java.util.Calendar;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.graphql_java_generator.client.GraphQLConfiguration;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.Queries;
import com.graphql_java_generator.samples.forum.client.graphql.PartialDirectRequests;
import com.graphql_java_generator.samples.forum.client.graphql.PartialPreparedRequests;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.QueryTypeExecutor;

/**
 * A Spring Boot client app. Very easy to use and to configure
 * 
 * @author etienne-sf
 */
@SpringBootApplication(scanBasePackageClasses = { Main.class, GraphQLConfiguration.class, QueryTypeExecutor.class })
public class Main implements CommandLineRunner {
	public static String GRAPHQL_ENDPOINT_URL = "http://localhost:8180/graphql";

	@Autowired
	PartialDirectRequests partialDirectRequests;
	@Autowired
	PartialPreparedRequests partialPreparedRequests;

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

	/**
	 * This method is started by Spring, once the Spring context has been loaded. This is run, as this class implements
	 * {@link CommandLineRunner}
	 */
	@Override
	public void run(String... args) throws Exception {
		System.out.println("");
		System.out.println("============================================================================");
		System.out.println("======= SIMPLEST WAY: DIRECT QUERIES =======================================");
		System.out.println("============================================================================");
		exec(partialDirectRequests, null);

		System.out.println("");
		System.out.println("============================================================================");
		System.out.println("======= MOST SECURE WAY: PREPARED QUERIES ==================================");
		System.out.println("============================================================================");
		exec(partialPreparedRequests, null);

		System.out.println("");
		System.out.println("");
		System.out.println("Sample application finished ... enjoy !    :)");
		System.out.println("");
		System.out.println("(please take a look at the other samples, for other use cases)");
	}

	void exec(Queries client, String name) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		try {

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  boardsSimple  --------------------------------------------");
			System.out.println(client.boardsSimple());

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  topicAuthorPostAuthor  -----------------------------------");
			Calendar cal = Calendar.getInstance();
			cal.set(2018, 12, 20);
			System.out.println(client.topicAuthorPostAuthor("Board name 2", cal.getTime()));

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  createBoard  ---------------------------------------------");
			// We need a unique name. Let's use a random name for that, if none was provided.
			name = (name != null) ? name : "Name " + Float.floatToIntBits((float) Math.random() * Integer.MAX_VALUE);
			System.out.println(client.createBoard(name, true));

		} catch (javax.ws.rs.ProcessingException e) {
			System.out.println("");
			System.out.println("ERROR");
			System.out.println("");
			System.out.println(
					"Please start the server from the project graphql-maven-plugin-samples-StarWars-server, before executing the client part");
		}
	}

	@Bean
	String graphqlEndpoint() {
		return GRAPHQL_ENDPOINT_URL;
	}

	@Bean
	WebClient webClient(String graphqlEndpoint) {
		return WebClient.builder()//
				.baseUrl(graphqlEndpoint)//
				// .defaultCookie("cookieKey", "cookieValue")//
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultUriVariables(Collections.singletonMap("url", graphqlEndpoint))//
				.build();
	}

}
