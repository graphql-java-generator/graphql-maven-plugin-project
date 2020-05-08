package com.graphql_java_generator.mavenplugin.samples.simple.client.graphql;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.generated.graphql.QueryType;
import com.graphql_java_generator.samples.simple.client.Main;
import com.graphql_java_generator.samples.simple.client.graphql.PartialDirectRequests;

/**
 * As it is suffixed by "IT", this is an integration test. Thus, it allows us to start the GraphQL StatWars server, see
 * the pom.xml file for details.
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
class PartialDirectRequestsIT extends AbstractIT {

	@BeforeEach
	void setUp() throws Exception {
		Main main = new Main();

		// As we don't have a proper certificate (it's a self-signed one), we must avoid SSL checks.
		//
		// DO NOT DO THIS IN PRODUCTION
		//
		SSLContext sslContext = main.getNoCheckSslContext();
		HostnameVerifier hostNameVerifier = main.getHostnameVerifier();

		// For some tests, we need to execute additional queries
		queryType = new QueryType(Main.graphqlEndpoint, sslContext, hostNameVerifier);

		// Creation of the instance, against which we'll execute the JUnit tests
		queries = new PartialDirectRequests(Main.graphqlEndpoint, sslContext, hostNameVerifier);

	}

}
