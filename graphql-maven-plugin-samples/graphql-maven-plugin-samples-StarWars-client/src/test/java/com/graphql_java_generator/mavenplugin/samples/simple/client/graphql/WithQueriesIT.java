package com.graphql_java_generator.mavenplugin.samples.simple.client.graphql;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.junit.jupiter.api.BeforeEach;

import com.generated.graphql.QueryType;
import com.graphql_java_generator.samples.simple.client.Main;
import com.graphql_java_generator.samples.simple.client.graphql.WithQueries;

/**
 * As it is suffixed by "IT", this is an integration test. Thus, it allows us to start the GraphQL StatWars server, see
 * the pom.xml file for details.
 * 
 * @author EtienneSF
 */
class WithQueriesIT extends AbstractIT {

	@BeforeEach
	void setUp() throws Exception {
		Main main = new Main();
		SSLContext sslContext = main.getSslContext();
		HostnameVerifier hostNameVerifier = main.getHostnameVerifier();
		// For some tests, we need to execute additional queries
		queryType = new QueryType(Main.graphqlEndpoint, sslContext, hostNameVerifier);

		// Creation of the instance, against which we'll execute the JUnit tests
		queries = new WithQueries(Main.graphqlEndpoint, main.getSslContext(), main.getHostnameVerifier());
	}

}
