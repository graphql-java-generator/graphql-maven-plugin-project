package com.graphql_java_generator.samples.forum.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	private final GraphQLProvider graphQLProvider;

	@Autowired
	public WebSocketConfig(GraphQLProvider graphQLProvider) {
		this.graphQLProvider = graphQLProvider;
	}

	@Bean
	public ServletServerContainerFactoryBean createWebSocketContainer() {
		ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
		// container.setMaxTextMessageBufferSize(8192);
		// container.setMaxBinaryMessageBufferSize(8192);
		return container;
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(new WebSocketHandler(graphQLProvider), "/graphql/subscription").setAllowedOrigins("*");
	}

}
