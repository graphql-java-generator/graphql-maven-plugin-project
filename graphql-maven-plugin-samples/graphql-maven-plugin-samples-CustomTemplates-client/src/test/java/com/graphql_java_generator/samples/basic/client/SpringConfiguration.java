package com.graphql_java_generator.samples.basic.client;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.samples.customtemplates.client.graphql.forum.client.QueryExecutor;

@Configuration
@SpringBootApplication
@ComponentScan(basePackageClasses = { GraphqlClientUtils.class, QueryExecutor.class })
public class SpringConfiguration {

	@Bean
	@Primary
	ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunction(
			ReactiveClientRegistrationRepository clientRegistrations) {
		InMemoryReactiveOAuth2AuthorizedClientService clientService = new InMemoryReactiveOAuth2AuthorizedClientService(
				clientRegistrations);
		AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
				clientRegistrations, clientService);
		ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
				authorizedClientManager);
		oauth.setDefaultClientRegistrationId("provider_test"); // Defines our custom OAuth2 provider
		return oauth;
	}

	@Bean
	@Primary
	public WebClient webClient(//
			ReactiveClientRegistrationRepository clientRegistrations, //
			String graphqlEndpoint, //
			CodecCustomizer defaultCodecCustomizer, //
			@Autowired ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunction) {

		return WebClient.builder()//
				.baseUrl(graphqlEndpoint)//
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultUriVariables(Collections.singletonMap("url", graphqlEndpoint))//
				.filter(serverOAuth2AuthorizedClientExchangeFilterFunction)//
				.build();
	}

}
