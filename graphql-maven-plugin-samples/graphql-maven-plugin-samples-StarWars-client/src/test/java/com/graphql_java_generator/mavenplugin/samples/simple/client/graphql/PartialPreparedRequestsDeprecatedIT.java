package com.graphql_java_generator.mavenplugin.samples.simple.client.graphql;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.generated.graphql.QueryType;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.simple.client.graphql.PartialPreparedRequestsDeprecated;

/**
 * As it is suffixed by "IT", this is an integration test. Thus, it allows us to start the GraphQL StatWars server, see
 * the pom.xml file for details.
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
class PartialPreparedRequestsDeprecatedIT extends AbstractIT {

	public PartialPreparedRequestsDeprecatedIT()
			throws KeyManagementException, NoSuchAlgorithmException, GraphQLRequestPreparationException {
		SSLContext sslContext = PartialPreparedRequestsDeprecated.getNoCheckSslContext();
		HostnameVerifier hostNameVerifier = PartialPreparedRequestsDeprecated.getHostnameVerifier();

		// This integration test is about the deprecated QueryType (new code should use XxxxTypeExecutor classes)
		queryType = new QueryType(PartialPreparedRequestsDeprecated.GRAPHQL_ENDPOINT, sslContext, hostNameVerifier);

		// Creation of the instance, against which we'll execute the JUnit tests
		queries = new PartialPreparedRequestsDeprecated();
		((PartialPreparedRequestsDeprecated) queries).init();
	}

}
