package org.allGraphQLCases.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.allGraphQLCases.client.CINP_CharacterInput_CINS;
import org.allGraphQLCases.client.CEP_Episode_CES;
import org.allGraphQLCases.client.util.MyQueryTypeExecutorAllGraphQLCases;
import org.allGraphQLCases.client2.util.MyQueryTypeExecutorAllGraphQLCases2;
import org.allGraphQLCases.demo.impl.PartialDirectQueries;
import org.allGraphQLCases.demo.impl.PartialPreparedQueries;
import org.allGraphQLCases.demo.impl.PartialRequestGraphQLRepository;
import org.allGraphQLCases.demo.subscription.ExecSubscription;
import org.forum.client.util.QueryExecutorForum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.UnAuthenticatedServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import com.graphql_java_generator.client.GraphQLConfiguration;
import com.graphql_java_generator.client.graphqlrepository.EnableGraphQLRepositories;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * The main class, which executes the same partialQueries, built by three different methods. See
 * {@link PartialDirectQueries}, {@link PartialPreparedQueries}, {@link PartialWithBuilder}<BR/>
 * 
 * A sample query, to get an OAuth token:
 * 
 * <pre>
curl -u "clientId:secret" -X POST "http://localhost:8181/oauth/token?grant_type=client_credentials" --noproxy "*" -i
 * </pre>
 * 
 * Then, reuse the previous token in the next query:
 * 
 * <pre>
curl -i -X POST "http://localhost:8180/graphql" --noproxy "*" -H "Authorization: Bearer 8c8e4a5b-d903-4ed6-9738-6f7f364b87ec"
 * </pre>
 * 
 * And, to check the token:
 * 
 * <pre>
curl -i -X GET "http://localhost:8181/profile/me" --noproxy "*" -H "Authorization: Bearer 8c8e4a5b-d903-4ed6-9738-6f7f364b87ec"
 * </pre>
 * 
 * @author etienne-sf
 * @see https://michalgebauer.github.io/spring-graphql-security/
 */
@SuppressWarnings("deprecation")
@SpringBootApplication(scanBasePackageClasses = { Main.class, GraphQLConfiguration.class,
		MyQueryTypeExecutorAllGraphQLCases.class, MyQueryTypeExecutorAllGraphQLCases2.class, QueryExecutorForum.class })
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@EnableGraphQLRepositories({ "org.allGraphQLCases.demo.impl", "org.allGraphQLCases.subscription.graphqlrepository" })
public class Main implements CommandLineRunner {

	@Autowired
	PartialDirectQueries partialDirectQueries;
	@Autowired
	PartialPreparedQueries partialPreparedQueries;
	@Autowired
	PartialRequestGraphQLRepository partialRequestGraphQLRepository;
	@Autowired
	ExecSubscription execSubscription;

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

	/**
	 * This method is started by Spring, once the Spring context has been loaded. This is run, as this class implements
	 * {@link CommandLineRunner}
	 */
	@Override
	public void run(String... args) throws Exception {

		// Execution of three different ways of calling the GraphQL server

		System.out.println("============================================================================");
		System.out.println("======= SIMPLEST WAY: GRAPHQL REPOSITOTRY  =================================");
		System.out.println("============================================================================");
		execOne(partialRequestGraphQLRepository);

		System.out.println("============================================================================");
		System.out.println("======= A SIMPLE WAY: DIRECT QUERIES =======================================");
		System.out.println("============================================================================");
		execOne(partialDirectQueries);

		System.out.println("============================================================================");
		System.out.println("======= MOST SECURE WAY: PREPARED QUERIES ==================================");
		System.out.println("============================================================================");
		execOne(partialPreparedQueries);

		// Then a subscription
		System.out.println("============================================================================");
		System.out.println("======= EXECUTING A SUBSCRIPTION ===========================================");
		System.out.println("============================================================================");
		execSubscription.exec();

		System.out.println("");
		System.out.println("");
		System.out.println("Sample application finished ... enjoy !    :)");
		System.out.println("");
		System.out.println("Please take a look at the other samples, for other use cases");
		System.out.println(
				"You'll find more information on the plugin's web site: https://graphql-maven-plugin-project.graphql-java-generator.com/");
	}

	public void execOne(PartialQueries client)
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		try {

			System.out.println("----------------  withoutParameters  ----------------------------------------------");
			System.out.println(client.withoutParameters());

			System.out.println("---------------- withOneOptionalParam -------------------------------------------");
			CINP_CharacterInput_CINS ci1 = CINP_CharacterInput_CINS.builder().withName("my name")
					.withAppearsIn(Arrays.asList(CEP_Episode_CES.JEDI, CEP_Episode_CES.NEWHOPE)).withType("Droid").build();
			System.out.println(client.withOneOptionalParam(ci1));

			System.out.println("---------------- withOneMandatoryParam ------------------------------------------");
			CINP_CharacterInput_CINS ci2 = CINP_CharacterInput_CINS.builder().withName("my other name").withAppearsIn(Arrays.asList())
					.withType("Human").build();
			System.out.println(client.withOneMandatoryParam(ci2));

			System.out.println("---------------- withEnum -------------------------------------------------------");
			System.out.println(client.withEnum(CEP_Episode_CES.NEWHOPE));

			System.out.println("---------------- withList -------------------------------------------------------");
			List<CINP_CharacterInput_CINS> chars = Arrays.asList(ci1, ci2);
			System.out.println(client.withList("The name", chars));

		} catch (javax.ws.rs.ProcessingException e) {
			throw new RuntimeException(
					"Please start the server from the project graphql-maven-plugin-samples-StarWars-server, before executing the client part",
					e);
		}
	}

	@Bean
	public ReactiveClientRegistrationRepository reactiveClientRegistrationRepository(
			OAuth2ClientProperties oAuth2ClientProperties) {
		List<ClientRegistration> clientRegistrations = new ArrayList<>();

		// because autoconfigure does not work for an unknown reason, here the ClientRegistrations are manually
		// configured based on the application.yml
		oAuth2ClientProperties.getRegistration().forEach((k, v) -> {
			String tokenUri = oAuth2ClientProperties.getProvider().get(k).getTokenUri();
			ClientRegistration clientRegistration = ClientRegistration.withRegistrationId(k).tokenUri(tokenUri)
					.clientId(v.getClientId()).clientSecret(v.getClientSecret())
					.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS).build();
			clientRegistrations.add(clientRegistration);
		});

		return new InMemoryReactiveClientRegistrationRepository(clientRegistrations);
	}

	@Bean
	ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases(
			ReactiveClientRegistrationRepository clientRegistrations) {
		ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
				clientRegistrations, new UnAuthenticatedServerOAuth2AuthorizedClientRepository());
		oauth.setDefaultClientRegistrationId("provider_test");
		return oauth;
	}

	// @Bean
	ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunctionAllGraphQLCases() {

		ClientRegistration reg = ClientRegistration.withRegistrationId("provider_test")
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS).build();
		ReactiveClientRegistrationRepository clientRegistrations = new InMemoryReactiveClientRegistrationRepository(
				reg);

		ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
				clientRegistrations, new UnAuthenticatedServerOAuth2AuthorizedClientRepository());
		oauth.setDefaultClientRegistrationId("provider_test");
		return oauth;
	}

	@Bean
	ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunctionForum(
			ReactiveClientRegistrationRepository clientRegistrations) {
		return null;
	}
}
