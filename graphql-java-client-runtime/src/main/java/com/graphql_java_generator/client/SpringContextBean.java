/**
 * 
 */
package com.graphql_java_generator.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Component;

/**
 * This class
 * 
 * @author etienne-sf
 */
@Component
public class SpringContextBean {

	private static ApplicationContext applicationContext;

	@Autowired
	public SpringContextBean(ApplicationContext applicationContext) {
		SpringContextBean.applicationContext = applicationContext;
	}

	/**
	 * Retrieves the Spring Application Context. This method allows to connect a non-spring class to the Spring IoC
	 * container.
	 * 
	 * @return
	 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * Sets the Spring Application Context. This method allows to provide a the Spring IoC container to non-spring
	 * classes
	 * 
	 * @return
	 */
	public static void setApplicationContext(ApplicationContext applicationContext) {
		SpringContextBean.applicationContext = applicationContext;
	}

	/**
	 * Retrieves the {@link GraphQlClient} Spring bean, associated with the given GraphQL suffix. The GraphQL suffix is
	 * defined in the plugin configuration. By default, it is an empty string. It becomes mandatory when the client
	 * connects to two GraphQl servers or more: it allows to select the {@link GraphQlClient} created for the relevant
	 * server.
	 * 
	 * @param graphQLSchema
	 *            The GraphQL schema suffix, as defined in the plugin configuration
	 * @return
	 */
	public static GraphQlClient getGraphQlClient(String graphQLSchema) {
		return applicationContext.getBean(//
				"graphQlClient" + ((graphQLSchema == null) ? "" : graphQLSchema), //
				GraphQlClient.class);
	}
}
