package com.graphql_java_generator.client;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql_java_generator.spring.client.GraphQLAutoConfiguration;

/**
 * This class contains a GraphQL configuration for the GraphQL client. The main element of this configuration contains
 * 
 * @author etienne-sf
 */
@Component
public class GraphQLConfiguration {

	/**
	 * The {@link QueryExecutor} is responsible for the execution of the GraphQLRequest, and for parsing the server
	 * response.<BR/>
	 * When the application is executed as a Spring app, then this field is field by the IoC Spring container.<BR/>
	 * Otherwise, the default constructor should not be used. The other constructor will then build the relevant
	 * instance of {@link QueryExecutor}.
	 */
	@Autowired
	QueryExecutor executor;

	/** The default constructor, that is used by Spring. */
	@Autowired
	public GraphQLConfiguration() {
		// No action. All configuration is done through Spring injection
	}

	/**
	 * This constructor expects the URI of the GraphQL server. This constructor works only for http servers, not for
	 * https ones.<BR/>
	 * For example: http://my.server.com/graphql
	 * 
	 * @param graphqlEndpoint
	 *            the http URI for the GraphQL endpoint
	 */
	public GraphQLConfiguration(String graphqlEndpoint) {
		// Let's "manually" reuse the default configuration, as defined in the Spring Auto Configuration bean
		GraphQLAutoConfiguration conf = new GraphQLAutoConfiguration();
		this.executor = new QueryExecutorSpringReactiveImpl(graphqlEndpoint, null,
				conf.webClient(graphqlEndpoint, null, null), conf.webSocketClient(null), null, null,
				new GraphQLObjectMapper());
	}

	/**
	 * This method is deprecated since version v1.12. It is based on the Jersey {@link Client}, but this client has a
	 * hard to use the OAuth implementation. The default implementation of this implementation is now based on
	 * Spring<BR/>
	 * This constructor expects the URI of the GraphQL server. This constructor works only for https servers, not for
	 * http ones.<BR/>
	 * For example: https://my.server.com/graphql<BR/>
	 * <BR/>
	 * {@link SSLContext} and {@link HostnameVerifier} are regular Java stuff. You'll find lots of documentation on the
	 * web. The StarWars sample is based on the <A HREF=
	 * "http://www.thinkcode.se/blog/2019/01/27/a-jersey-client-supporting-https">http://www.thinkcode.se/blog/2019/01/27/a-jersey-client-supporting-https</A>
	 * blog. But this sample implements a noHostVerification, which of course, is the simplest but the safest way to go.
	 * 
	 * @param graphqlEndpoint
	 *            the https URI for the GraphQL endpoint
	 * @param sslContext
	 * @param hostnameVerifier
	 */
	@Deprecated
	public GraphQLConfiguration(String graphqlEndpoint, SSLContext sslContext, HostnameVerifier hostnameVerifier) {
		this.executor = new QueryExecutorImpl(graphqlEndpoint, sslContext, hostnameVerifier);
	}

	/**
	 * This method is deprecated since version v1.12. It is based on the Jersey {@link Client}, but this client has a
	 * hard to use the OAuth implementation. The default implementation of this implementation is now based on
	 * Spring<BR/>
	 * This constructor expects the URI of the GraphQL server and a configured JAX-RS client that gives the opportunity
	 * to customize the REST request<BR/>
	 * For example: http://my.server.com/graphql
	 *
	 * @param graphqlEndpoint
	 *            the http URI for the GraphQL endpoint
	 * @param client
	 *            {@link Client} javax.ws.rs.client.Client to support customization of the rest request
	 * @param objectMapper
	 *            {@link ObjectMapper} com.fasterxml.jackson.databind.ObjectMapper to support configurable mapping
	 */
	@Deprecated
	public GraphQLConfiguration(String graphqlEndpoint, Client client, GraphQLObjectMapper objectMapper) {
		this.executor = new QueryExecutorImpl(graphqlEndpoint, client, objectMapper);
	}

	/** Retrieves the {@link QueryExecutor} for this GraphQL configuration */
	public QueryExecutor getQueryExecutor() {
		return executor;
	}

}
