/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

import com.graphql_java_generator.plugin.conf.GenerateClientCodeConfiguration;

/**
 * @author etienne-sf
 */
@Configuration
@Import({ JacksonAutoConfiguration.class })
@ComponentScan(basePackages = { "com.graphql_java_generator" }, excludeFilters = {
		@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateGraphQLSchema.*"),
		@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateServerCode.*"),
		@Filter(type = FilterType.REGEX, pattern = ".*\\.GraphQL.*") })
public class GenerateClientCodeSpringConfiguration {

	/**
	 * This static field is a trick to let the Spring ApplicationContext access to this instance. If you find any better
	 * solution, let us know !
	 */
	static GenerateClientCodeMojo mojo = null;

	@Bean
	GenerateClientCodeConfiguration pluginConfiguration() {
		return new GenerateClientCodeConfigurationImpl(mojo);
	}

}
