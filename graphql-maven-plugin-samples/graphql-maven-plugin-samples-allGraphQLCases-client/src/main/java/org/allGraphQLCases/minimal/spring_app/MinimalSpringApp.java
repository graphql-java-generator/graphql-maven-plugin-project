/**
 * 
 */
package org.allGraphQLCases.minimal.spring_app;

import java.util.Arrays;
import java.util.List;

import org.allGraphQLCases.client.Character;
import org.allGraphQLCases.client.CharacterInput;
import org.allGraphQLCases.client.Episode;
import org.allGraphQLCases.client.Human;
import org.allGraphQLCases.client.HumanInput;
import org.allGraphQLCases.client.util.MyQueryTypeExecutorAllGraphQLCases;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.UnAuthenticatedServerOAuth2AuthorizedClientRepository;

import com.graphql_java_generator.client.GraphQLConfiguration;
import com.graphql_java_generator.client.graphqlrepository.EnableGraphQLRepositories;

/**
 * @author etienne-sf
 */
@SuppressWarnings("deprecation")
@SpringBootApplication(scanBasePackageClasses = { MinimalSpringApp.class, GraphQLConfiguration.class,
		MyQueryTypeExecutorAllGraphQLCases.class })
@EnableGraphQLRepositories("org.allGraphQLCases.minimal.spring_app")
public class MinimalSpringApp implements CommandLineRunner {

	/** The logger for this class */
	static protected Logger logger = LoggerFactory.getLogger(MinimalSpringApp.class);

	@Autowired
	GraphQLRequests graphQLRequests;
	@Autowired
	String graphqlEndpoint2;

	public static void main(String[] args) {
		SpringApplication.run(MinimalSpringApp.class, args);
	}

	/**
	 * This method is started by Spring, once the Spring context has been loaded. This is run, as this class implements
	 * {@link CommandLineRunner}
	 */
	@Override
	public void run(String... args) throws Exception {
		CharacterInput characterInput = CharacterInput.builder().withName("the name")
				.withAppearsIn(Arrays.asList(Episode.JEDI, Episode.NEWHOPE)).withType("Human").build();
		HumanInput humanInput = HumanInput.builder().withName("the name")
				.withAppearsIn(Arrays.asList(Episode.JEDI, Episode.NEWHOPE)).build();

		logger.info("");
		logger.info("Executing this query: 'graphQLRequests.withoutParameters()' ");
		logger.info(
				"Note: the first GraphQL request execution is longer, as the OAuth token must be acquired, and Reactive code must be started");
		//
		// Below is all you need to execute the GraphQL Request defined in the GraphQL Repository: graphQLRequests
		List<Character> response1 = graphQLRequests.withoutParameters();
		//
		logger.info(response1.toString());

		logger.info("");
		logger.info("Executing this query: 'graphQLRequests.withOneOptionalParam(input)'");
		//
		// Below is all you need to execute the GraphQL Request defined in the GraphQL Repository: graphQLRequests
		Character response2 = graphQLRequests.withOneOptionalParam(characterInput);
		//
		logger.info("The query result is: " + response2.toString());

		logger.info("");
		logger.info("Executing this mutation: 'graphQLRequests.createHuman(input)'");
		//
		// Below is all you need to execute the GraphQL Request defined in the GraphQL Repository: graphQLRequests
		Human human = graphQLRequests.createHuman(humanInput).getCreateHuman();
		//
		logger.info("The mutation result is: " + human.toString());

		logger.info("Normal end of execution");
	}

	/**
	 * This beans is all that is needed to wire OAuth into the application, thanks to Spring Boot and some configuration
	 * lines in the resources/application.properties file
	 */
	@Bean
	ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunction(
			ReactiveClientRegistrationRepository clientRegistrations) {
		ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
				clientRegistrations, new UnAuthenticatedServerOAuth2AuthorizedClientRepository());
		oauth.setDefaultClientRegistrationId("provider_test");
		return oauth;
	}
}
