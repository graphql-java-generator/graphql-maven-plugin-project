package org.allGraphQLCases.oauth;

import org.allGraphQLCases.client.util.MyQueryTypeExecutorAllGraphQLCases;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.client.SpringContextBean;

/**
 * A Spring configuration without OAuth, to check that the OAuth authentication is active on server side
 * 
 * @author etienne-sf
 */
@Configuration
@EnableAutoConfiguration
@PropertySource("classpath:/application.properties")
// No OAuth configuration from the Main class @Import(Main.class)
@ComponentScan(basePackageClasses = { GraphqlClientUtils.class, MyQueryTypeExecutorAllGraphQLCases.class })
public class SpringTestConfigWithoutOAuth {

	@Value("${another.parameter.for.the.graphql.endpointAllGraphQLCases.url}")
	String anotherParameterForTheGraphqlEndpointAllGraphQLCasesUrl;

	@Autowired
	ApplicationContext applicationContext;

	/**
	 * As this project declares the url of the GraphQL endpoint with another property name, we need to redefine the
	 * graphqlEndpointAllGraphQLCases bean
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean(name = "graphqlEndpointAllGraphQLCases")
	String graphqlEndpointAllGraphQLCases() {
		return anotherParameterForTheGraphqlEndpointAllGraphQLCasesUrl;
	}

	/**
	 * Insures that the {@link SpringContextBean} bean stores the right Spring {@link ApplicationContext} in its static
	 * applicationContext field.<br/>
	 * These JUnit tests may build more than one Spring {@link ApplicationContext}. The plugin's runtime uses the
	 * {@link SpringContextBean} that started at initialization, and stores the Spring {@link ApplicationContext} in its
	 * static applicationContext field.<br/>
	 * The issue in these JUnit tests that create several application contexts is that is creates a mess, as this static
	 * field can contain only one Spring {@link ApplicationContext}: the last one to be created.
	 */
	@Bean
	BeanPostProcessor SpringContextSetterBeanPostProcessor() {
		return new BeanPostProcessor() {
			@Override
			public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
				String classname = bean.getClass().getName();
				if (classname.startsWith("org.allGraphQLCases") || classname.endsWith("IT")) {
					SpringContextBean.setApplicationContext(applicationContext);
				}
				return bean;
			}
		};
	}
}
