/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.graphql_java_generator.plugin.PluginConfiguration;

/**
 * @author EtienneSF
 */
@Configuration
@Import({ JacksonAutoConfiguration.class })
@ComponentScan(basePackages = { "com.graphql_java_generator" })
public class SpringConfiguration {

	/**
	 * This static field is a trick to let the Spring ApplicationContext access to this instance. If you find any better
	 * solution, let us know !
	 */
	static GraphqlMavenPlugin mojo = null;

	@Bean
	PluginConfiguration pluginConfiguration() {
		return new PluginConfigurationImpl(mojo);
	}
}
