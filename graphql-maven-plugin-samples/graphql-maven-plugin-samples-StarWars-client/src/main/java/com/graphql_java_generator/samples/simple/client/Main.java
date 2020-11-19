package com.graphql_java_generator.samples.simple.client;

import java.util.Collections;

import javax.net.ssl.SSLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;

import com.generated.graphql.Episode;
import com.generated.graphql.QueryTypeExecutor;
import com.graphql_java_generator.client.GraphQLConfiguration;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.simple.client.graphql.PartialDirectRequests;
import com.graphql_java_generator.samples.simple.client.graphql.PartialPreparedRequests;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.netty.http.client.HttpClient;

/**
 * The main class, which executes the same queries, built by three different methods. See {@link PartialDirectRequests},
 * {@link PartialPreparedRequests}, {@link WithBuilder}
 * 
 * @author etienne-sf
 */
@SpringBootApplication(scanBasePackageClasses = { Main.class, GraphQLConfiguration.class, QueryTypeExecutor.class })
public class Main implements CommandLineRunner {

	@Value("${graphql.endpoint.url}")
	private String graphqlEndpoint;
	@Value("${graphql.endpoint.subscriptionUrl}")
	private String graphqlSubscriptionEndpoint;

	@Autowired
	PartialDirectRequests partialDirectRequests;
	@Autowired
	PartialPreparedRequests partialPreparedRequests;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Main.class, args);
	}

	/**
	 * This method is started by Spring, once the Spring context has been loaded. This is run, as this class implements
	 * {@link CommandLineRunner}
	 */
	@Override
	public void run(String... args) throws Exception {

		// Execution of two ways that use the GraphQL client, to call the GraphQL server

		System.out.println("============================================================================");
		System.out.println("======= SIMPLEST WAY: DIRECT QUERIES =======================================");
		System.out.println("============================================================================");
		execOne(partialDirectRequests);

		System.out.println("============================================================================");
		System.out.println("======= MOST SECURE WAY: PREPARED QUERIES ==================================");
		System.out.println("============================================================================");
		execOne(partialPreparedRequests);

		System.out.println("");
		System.out.println("");
		System.out.println("Sample application finished ... enjoy !    :)");
		System.out.println("");
		System.out.println("Please take a look at the other samples, for other use cases");
		System.out.println(
				"You'll find more information on the plugin's web site: https://graphql-maven-plugin-project.graphql-java-generator.com/");
	}

	public void execOne(Queries client) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// A random value, to variabilize mutations
		int i = (int) (Math.random() * Integer.MAX_VALUE);

		try {

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  heroSimple  ----------------------------------------------");
			System.out.println(client.heroPartial(Episode.NEWHOPE));

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  heroFriendsFriendsFriends  -------------------------------");
			System.out.println(client.heroFriendsFriendsFriends(Episode.NEWHOPE));

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  humanSimple  ----------------------------------------------");
			System.out.println(client.humanPartial("00000000-0000-0000-0000-000000000045"));

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  humanFriendsFriendsFriends  ------------------------------");
			System.out.println(client.humanFriendsFriendsFriends("00000000-0000-0000-0000-000000000180"));

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  droidSimple  ----------------------------------------------");
			System.out.println(client.droidPartial("00000000-0000-0000-0000-000000000003"));

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  droidFriendsFriendsFriends  ------------------------------");
			System.out.println(client.droidPartial("00000000-0000-0000-0000-000000000003"));

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  droidDoesNotExist  ---------------------------------------");
			System.out.println(client.droidDoesNotExist());

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  createHuman  ---------------------------------------------");
			System.out.println(client.createHuman("A new Human (" + i++ + ")", "A random planet"));

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  addFriend  -----------------------------------------------");
			System.out.println(client.droidDoesNotExist());

		} catch (javax.ws.rs.ProcessingException e) {
			throw new RuntimeException(
					"Please start the server from the project graphql-maven-plugin-samples-StarWars-server, before executing the client part",
					e);
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////// Below is the configuration, based on Spring beans
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Bean
	String graphqlEndpoint() {
		return graphqlEndpoint;
	}

	@Bean
	String graphqlSubscriptionEndpoint() {
		return graphqlSubscriptionEndpoint;
	}

	@Bean
	SslContext insecureSslContext() throws SSLException {
		return SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
	}

	@Bean
	WebClient webClient(String graphqlEndpoint, SslContext nettySslContext) {
		Builder ret = WebClient.builder()//
				.baseUrl(graphqlEndpoint)//
				// .defaultCookie("cookieKey", "cookieValue")//
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultUriVariables(Collections.singletonMap("url", graphqlEndpoint));

		if (nettySslContext != null) {
			HttpClient httpConnector = HttpClient.create().secure(t -> t.sslContext(nettySslContext));
			ret.clientConnector(new ReactorClientHttpConnector(httpConnector));
		}
		return ret.build();
	}

	/**
	 * Returns a Dummy SSLContext, that won't check the server certificate. With a real check, an exception is thrown,
	 * as it is self signed.<BR/>
	 * DON'T USE IT IN PRODUCTION.
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	// @Bean
	// public SSLContext sslContext() throws NoSuchAlgorithmException, KeyManagementException {
	// SSLContext sslContext = SSLContext.getInstance("TLSv1");
	//
	// // Very, very bad. Don't do that in production !
	// KeyManager[] keyManagers = null;
	// TrustManager[] trustManager = { new X509TrustManager() {
	// @Override
	// public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
	// }
	//
	// @Override
	// public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
	// }
	//
	// @Override
	// public X509Certificate[] getAcceptedIssuers() {
	// return new X509Certificate[0];
	// }
	// } };
	// SecureRandom secureRandom = new SecureRandom();
	//
	// sslContext.init(keyManagers, trustManager, secureRandom);
	//
	// return sslContext;
	// }
	//
	// /**
	// * Creates a netty {@link SslContext} from a JDK {@link SSLContext}. It is called only if no netty
	// * {@link SslContext} has already been defined.
	// *
	// * @return a default netty {@link SslContext}, if no JDK {@link SSLContext} Spring bean exists. And a netty
	// * {@link SslContext} based on the existing JDK {@link SSLContext} Spring bean otherwise.
	// * @throws SSLException
	// */
	// @ConditionalOnMissingBean
	// @Bean
	// SslContext nettySslContext(SSLContext sslContext) throws SSLException {
	// if (sslContext == null) {
	// return SslContextBuilder.forClient().build();
	// } else {
	// // return new JdkSslContext(sslContext, true, null, null, null, ClientAuth.NONE, null, true);
	// return new JdkSslContext(sslContext, true, ClientAuth.NONE);
	// }
	// }
}
