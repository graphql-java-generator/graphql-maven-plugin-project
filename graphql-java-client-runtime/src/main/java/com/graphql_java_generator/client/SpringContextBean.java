/**
 * 
 */
package com.graphql_java_generator.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.request.AbstractGraphQLRequest;

/**
 * This class
 * 
 * @author etienne-sf
 */
@Component
public class SpringContextBean {

	private static Logger logger = LoggerFactory.getLogger(SpringContextBean.class);

	/** A logger used to debug Spring Beans configuration and loading at runtime */
	private static Logger loggerBeanPostProcessor = LoggerFactory.getLogger("BeanPostProcessor");

	private static ApplicationContext applicationContext;

	/**
	 * Builds the Bean with the Spring Application Context, which is stored into a static attribute of the class. This
	 * allows to provide a the Spring IoC container to non-spring classes, like {@link AbstractGraphQLRequest}.
	 * 
	 * @return
	 */
	@Autowired
	public SpringContextBean(ApplicationContext applicationContext) {
		loggerBeanPostProcessor.debug("Setting applicationContext to {}", applicationContext);
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
	 * classes.<br/>
	 * Spring uses the Autowired constructor. This method is used by unit tests.
	 * 
	 * @return
	 */
	public static void setApplicationContext(ApplicationContext applicationContext) {
		loggerBeanPostProcessor.debug("Setting applicationContext to {}", applicationContext);
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
	 * @param requestType
	 *            The type of request is necessary, to retrieve the good {@link GraphQlClient}
	 * @return
	 */
	public static GraphQlClient getGraphQlClient(String graphQLSchema, RequestType requestType) {
		String beanName = ((requestType == RequestType.subscription) ? "webSocket" : "http")//
				+ "GraphQlClient" //
				+ ((graphQLSchema == null) ? "" : graphQLSchema);
		GraphQlClient bean = applicationContext.getBean(beanName, GraphQlClient.class);

		logger.debug("Retrieving the '{}' bean (@{})", beanName, bean);
		loggerBeanPostProcessor.debug("Retrieving the '{}' bean (@{}) - {}", beanName, bean, applicationContext);
		return bean;
	}
}
