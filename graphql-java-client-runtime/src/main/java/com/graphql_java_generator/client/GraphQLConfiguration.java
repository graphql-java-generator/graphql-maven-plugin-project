package com.graphql_java_generator.client;

import java.util.Collections;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;

import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import reactor.netty.http.client.HttpClient;

/**
 * This class contains a GraphQL configuration for the GraphQL client. The main element of this configuration contains
 * 
 * @author etienne-sf
 */
public class GraphQLConfiguration {

	/**
	 * The {@link RequestExecution} is responsible for the execution of the GraphQLRequest, and for parsing the server
	 * response.<BR/>
	 * When the application is executed as a Spring app, then this field is field by the IoC Spring container.<BR/>
	 * Otherwise, the default constructor should not be used. The other constructor will then build the relevant
	 * instance of {@link RequestExecution}.
	 */
	final RequestExecution requestExecutor;

	/** The default constructor, that is used by Spring. */
	public GraphQLConfiguration(RequestExecution requestExecutor) {
		this.requestExecutor = requestExecutor;
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
		this.requestExecutor = new RequestExecutionSpringReactiveImpl(graphqlEndpoint, null,
				getWebClient(graphqlEndpoint, null, null, (ExchangeFilterFunction[]) null), getWebSocketClient(null),
				null, null);
	}

	/**
	 * Builds a Spring reactive {@link WebClient}, from the specified parameters.<BR/>
	 * Note: this utility can be used if you need to create your own {@link WebClient}, for instance to add your own
	 * filters to the {@link WebClient}
	 * 
	 * @param graphqlEndpoint
	 * @param httpClient
	 * @param filters
	 *            Optional list of additional filters that will be added to the returned {@link WebClient}
	 * @return
	 */
	public static WebClient getWebClient(String graphqlEndpoint, HttpClient httpClient,
			ExchangeFilterFunction... filters) {
		return getWebClient(graphqlEndpoint, null, httpClient, filters);
	}

	/**
	 * Builds a Spring reactive {@link WebClient}, from the specified parameters.<BR/>
	 * Note: this utility can be used if you need to create your own {@link WebClient}, for instance to add your own
	 * filters to the {@link WebClient}
	 * 
	 * @param graphqlEndpoint
	 * @param codecCustomizer
	 *            The Spring {@link CodecCustomizer}. Typically, the generated Spring autoconfiguration uses the
	 *            defaultCodecCustomizer that loads the spring.codec.xxx properties from the Spring configuration file.
	 * @param httpClient
	 * @param filters
	 *            Optional list of additional filters that will be added to the returned {@link WebClient}
	 * @return
	 */
	public static WebClient getWebClient(String graphqlEndpoint, CodecCustomizer codecCustomizer, HttpClient httpClient,
			ExchangeFilterFunction... filters) {
		Builder webClientBuilder = WebClient.builder()//
				.baseUrl(graphqlEndpoint)//
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultUriVariables(Collections.singletonMap("url", graphqlEndpoint));

		if (codecCustomizer != null) {
			webClientBuilder.codecs(configurer -> codecCustomizer.customize(configurer));
		}

		if (httpClient != null) {
			webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient));
		}
		if (filters != null) {
			for (ExchangeFilterFunction filter : filters) {
				if (filter != null) {
					webClientBuilder.filter(filter);
				}
			}
		}

		return webClientBuilder.build();
	}

	/**
	 * Creates the Spring reactive {@link WebSocketClient} that will be used for subscriptions.
	 * 
	 * @param httpClient
	 * @return
	 */
	public static WebSocketClient getWebSocketClient(HttpClient httpClient) {
		if (httpClient == null) {
			return new ReactorNettyWebSocketClient(HttpClient.create());
		} else {
			return new ReactorNettyWebSocketClient(httpClient);
		}
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
		this.requestExecutor = new RequestExecutionImpl(graphqlEndpoint, sslContext, hostnameVerifier);
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
	 */
	@Deprecated
	public GraphQLConfiguration(String graphqlEndpoint, Client client) {
		this.requestExecutor = new RequestExecutionImpl(graphqlEndpoint, client);
	}

	/** Retrieves the {@link RequestExecution} for this GraphQL configuration */
	public RequestExecution getQueryExecutor() {
		return requestExecutor;
	}

}
