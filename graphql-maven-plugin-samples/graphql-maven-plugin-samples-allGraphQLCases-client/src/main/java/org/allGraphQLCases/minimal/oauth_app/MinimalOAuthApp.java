/**
 * 
 */
package org.allGraphQLCases.minimal.oauth_app;

import java.util.List;

import org.allGraphQLCases.client.CIP_Character_CIS;
import org.allGraphQLCases.client.MyQueryTypeExecutorAllGraphQLCases;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.graphql_java_generator.client.GraphqlClientUtils;

/**
 * This class describes how to create a minimal app, that can access to an OAuth2 protected GraphQL server
 * 
 * @author etienne-sf
 */
@SpringBootApplication(scanBasePackageClasses = { MinimalOAuthApp.class, GraphqlClientUtils.class,
		MyQueryTypeExecutorAllGraphQLCases.class })
public class MinimalOAuthApp implements CommandLineRunner {

	/** The logger for this class */
	static protected Logger logger = LoggerFactory.getLogger(MinimalOAuthApp.class);

	@Autowired
	MyQueryTypeExecutorAllGraphQLCases queryType;

	public static void main(String[] args) {
		SpringApplication.run(MinimalOAuthApp.class, args);
	}

	/**
	 * This method is started by Spring, once the Spring context has been loaded. This is run, as this class implements
	 * {@link CommandLineRunner}
	 */
	@Override
	public void run(String... args) throws Exception {
		List<CIP_Character_CIS> response;
		String query = "{appearsIn name }";

		logger.info("Executing this query: '" + query
				+ "' (the first GraphQL request execution is longer, as the Reactive code must be started)");
		response = queryType.withoutParameters(query);
		logger.info(response.toString());

		logger.info("Re-executing this query: '" + query + "'");
		response = queryType.withoutParameters(query);
		logger.info(response.toString());

		logger.info("Normal end of execution");
	}

}
