package com.graphql_java_generator.samples.simple.client;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.graphql_java_generator.client.response.GraphQLExecutionException;
import com.graphql_java_generator.client.response.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.simple.client.graphql.DirectQueries;
import com.graphql_java_generator.samples.simple.client.graphql.WithBuilder;
import com.graphql_java_generator.samples.simple.client.graphql.WithQueries;

/**
 * The main class, which executes the same queries, built by three different
 * methods. See {@link DirectQueries}, {@link WithQueries}, {@link WithBuilder}
 * 
 * @author EtienneSF
 */
public class Main {

	public static String graphqlEndpoint = "https://localhost:8443/starwars/graphql";

	public static void main(String[] args) throws Exception {
		new Main().execAll();
	}

	void execAll() throws Exception {

		// Execution of three way to user the GraphQL client, to call the GraphQL server

		System.out.println("============================================================================");
		System.out.println("======= SIMPLEST WAY: DIRECT QUERIES =======================================");
		System.out.println("============================================================================");
		execOne(new DirectQueries(graphqlEndpoint, getSslContext(), new NoOpHostnameVerifier()));

		System.out.println("============================================================================");
		System.out.println("======= MOST SECURE WAY: PREPARED QUERIES ==================================");
		System.out.println("============================================================================");
		execOne(new WithQueries(graphqlEndpoint, getSslContext(), new NoOpHostnameVerifier()));

		System.out.println("============================================================================");
		System.out.println("======= MOST SECURE WAY: PREPARED QUERIES ==================================");
		System.out.println("============================================================================");
		execOne(new WithBuilder(graphqlEndpoint, getSslContext(), new NoOpHostnameVerifier()));

		System.out.println("");
		System.out.println("");
		System.out.println("Sample application finished ... enjoy !    :)");
		System.out.println("");
		System.out.println("(please take a look at the other samples, for other use cases)");
	}

	public void execOne(Queries client) throws GraphQLExecutionException, GraphQLRequestPreparationException {
		// A random value, to variabilize mutations
		int i = (int) (Math.random() * Integer.MAX_VALUE);

		try {

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  heroSimple  ----------------------------------------------");
			System.out.println(client.heroPartial());

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  heroFriendsFriendsFriends  -------------------------------");
			System.out.println(client.heroFriendsFriendsFriends());

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  humanSimple  ----------------------------------------------");
			System.out.println(client.humanPartial());

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  humanFriendsFriendsFriends  ------------------------------");
			System.out.println(client.humanFriendsFriendsFriends());

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  droidSimple  ----------------------------------------------");
			System.out.println(client.droidPartial());

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  droidFriendsFriendsFriends  ------------------------------");
			System.out.println(client.droidPartial());

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

	public SSLContext getSslContext() throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext sslContext = SSLContext.getInstance("TLSv1");

		KeyManager[] keyManagers = null;
		TrustManager[] trustManager = { new NoOpTrustManager() };
		SecureRandom secureRandom = new SecureRandom();

		sslContext.init(keyManagers, trustManager, secureRandom);

		return sslContext;
	}

	public HostnameVerifier getHostnameVerifier() {
		return new NoOpHostnameVerifier();
	}

	public class NoOpTrustManager implements X509TrustManager {
		@Override
		public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}
	}

	public class NoOpHostnameVerifier implements HostnameVerifier {
		@Override
		public boolean verify(String s, SSLSession sslSession) {
			return true;
		}
	}
}
