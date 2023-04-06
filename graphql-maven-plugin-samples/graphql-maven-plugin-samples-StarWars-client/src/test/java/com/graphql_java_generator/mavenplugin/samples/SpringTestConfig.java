package com.graphql_java_generator.mavenplugin.samples;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collections;

import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.graphql.client.WebSocketGraphQlClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import com.generated.graphql.QueryTypeExecutor;
import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.samples.simple.client.Main;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import reactor.netty.http.client.HttpClient;

@TestConfiguration
@ComponentScan(basePackageClasses = { Main.class, SpringTestConfig.class, GraphqlClientUtils.class,
		QueryTypeExecutor.class }, excludeFilters = { @Filter(type = FilterType.REGEX, pattern = "graphql\\..*") })
public class SpringTestConfig {

	@Value("${trust.store}")
	private Resource trustStoreResource;

	@Value("${trust.store.password}")
	private String trustStorePassword;

	/** Logger for this class */
	Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * This sample uses in https, but with a self-signed certificate. So we use the keystore that contains the
	 * certificate.
	 * 
	 * @return
	 * @throws KeyStoreException
	 * @throws IOException
	 * @throws CertificateException
	 * @throws NoSuchAlgorithmException
	 * @throws UnrecoverableKeyException
	 */
	@Bean
	HttpClient sslHttpClient() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException,
			UnrecoverableKeyException {
		KeyStore trustStore = KeyStore.getInstance("JKS");
		try (InputStream inputStream = trustStoreResource.getInputStream()) {
			trustStore.load(inputStream, trustStorePassword.toCharArray());
		}
		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
		trustManagerFactory.init(trustStore);

		SslContext sslContext = SslContextBuilder.forClient().trustManager(trustManagerFactory).build();

		return HttpClient.create().secure(t -> t.sslContext(sslContext));
	}

	@Bean
	@Primary
	public WebClient webClient(String graphqlEndpoint, HttpClient sslHttpClient) {
		return WebClient.builder()//
				.clientConnector(new ReactorClientHttpConnector(sslHttpClient))//
				.baseUrl(graphqlEndpoint)//
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultUriVariables(Collections.singletonMap("url", graphqlEndpoint)).build();
	}

	@Bean
	@Primary
	GraphQlClient webSocketGraphQlClient(String graphqlEndpoint, HttpClient sslHttpClient) {
		WebSocketClient client = new ReactorNettyWebSocketClient(sslHttpClient);
		return WebSocketGraphQlClient.builder(graphqlEndpoint, client).build();
	}
}
