package org.allGraphQLCases.server.config;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomBeans {

	@Bean
	public GraphQlSourceBuilderCustomizer sourceBuilderCustomizer() {
		return (builder) -> builder.instrumentation(Arrays.asList(new MyInstrumentation()));
	}

}
