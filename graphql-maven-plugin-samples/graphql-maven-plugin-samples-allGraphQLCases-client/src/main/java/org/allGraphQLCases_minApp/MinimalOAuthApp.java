/**
 * 
 */
package org.allGraphQLCases_minApp;

import org.allGraphQLCases.client.util.MyQueryTypeExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.UnAuthenticatedServerOAuth2AuthorizedClientRepository;

import com.graphql_java_generator.client.GraphQLConfiguration;

/**
 * This class describes how to create a minimal app, that can access to an OAuth2 protected GraphQL server
 * 
 * @author etienne-sf
 */
@SpringBootApplication(scanBasePackageClasses = { MinimalOAuthApp.class, GraphQLConfiguration.class,
		MyQueryTypeExecutor.class })
public class MinimalOAuthApp implements CommandLineRunner {

	@Autowired
	MyQueryTypeExecutor queryType;

	public static void main(String[] args) {
		SpringApplication.run(MinimalOAuthApp.class, args);
	}

	/**
	 * This method is started by Spring, once the Spring context has been loaded. This is run, as this class implements
	 * {@link CommandLineRunner}
	 */
	@Override
	public void run(String... args) throws Exception {
		String query = "{appearsIn name }";
		System.out.println("Executing this query: '" + query + "'");
		System.out.println(queryType.withoutParameters(query));
	}

	@Bean
	ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunction(
			ReactiveClientRegistrationRepository clientRegistrations) {
		ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
				clientRegistrations, new UnAuthenticatedServerOAuth2AuthorizedClientRepository());
		oauth.setDefaultClientRegistrationId("provider_test");
		return oauth;
	}
}
