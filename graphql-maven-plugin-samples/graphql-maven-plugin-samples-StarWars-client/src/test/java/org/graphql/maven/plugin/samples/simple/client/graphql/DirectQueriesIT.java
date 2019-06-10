package org.graphql.maven.plugin.samples.simple.client.graphql;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.graphql.maven.plugin.samples.simple.client.Main;
import org.junit.jupiter.api.BeforeEach;

import com.generated.graphql.QueryType;

/**
 * As it is suffixed by "IT", this is an integration test. Thus, it allows us to
 * start the GraphQL StatWars server, see the pom.xml file for details.
 * 
 * @author EtienneSF
 */
class DirectQueriesIT extends AbstractIT {

	@BeforeEach
	void setUp() throws Exception {
		Main main = new Main();
		SSLContext sslContext = main.getSslContext();
		HostnameVerifier hostNameVerifier = main.getHostnameVerifier();
		// For some tests, we need to execute additional queries
		queryType = new QueryType(Main.graphqlEndpoint, sslContext, hostNameVerifier);

		// Creation of the instance, against which we'll execute the JUnit tests
		queries = new DirectQueries(Main.graphqlEndpoint, sslContext, hostNameVerifier);

	}

}
