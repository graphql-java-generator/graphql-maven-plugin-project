/**
 * 
 */
package org.allGraphQLCases.minimal.spring_app;

import java.util.Arrays;
import java.util.List;

import org.allGraphQLCases.client.Character;
import org.allGraphQLCases.client.CharacterInput;
import org.allGraphQLCases.client.Episode;
import org.allGraphQLCases.client.Human;
import org.allGraphQLCases.client.HumanInput;
import org.allGraphQLCases.client.util.MyQueryTypeExecutorAllGraphQLCases;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.util.GraphqlUtils;

/**
 * @author etienne-sf
 */
@SpringBootApplication(scanBasePackageClasses = { MinimalSpringApp.class, MyQueryTypeExecutorAllGraphQLCases.class })
public class MinimalSpringApp implements CommandLineRunner {

	/** The logger for this class */
	static protected Logger logger = LoggerFactory.getLogger(MinimalSpringApp.class);

	@Autowired
	GraphQLRequests graphQLRequests;

	public static void main(String[] args) {
		SpringApplication.run(MinimalSpringApp.class, args);
	}

	/**
	 * This method is started by Spring, once the Spring context has been loaded. This is run, as this class implements
	 * {@link CommandLineRunner}
	 */
	@Override
	public void run(String... args) throws Exception {
		CharacterInput characterInput = CharacterInput.builder().withName("the name")
				.withAppearsIn(Arrays.asList(Episode.JEDI, Episode.NEWHOPE)).withType("Human").build();
		HumanInput humanInput = HumanInput.builder().withName("the name")
				.withAppearsIn(Arrays.asList(Episode.JEDI, Episode.NEWHOPE)).build();

		logger.info("");
		logger.info("Executing this query: 'graphQLRequests.withoutParameters()' ");
		logger.info(
				"Note: the first GraphQL request execution is longer, as the OAuth token must be acquired, and Reactive code must be started");
		//
		// Below is all you need to execute the GraphQL Request defined in the GraphQL Repository: graphQLRequests
		List<Character> response1 = graphQLRequests.withoutParameters();
		//
		logger.info(response1.toString());

		logger.info("");
		logger.info("Executing this query: 'graphQLRequests.withOneOptionalParam(input)'");
		//
		// Below is all you need to execute the GraphQL Request defined in the GraphQL Repository: graphQLRequests
		Character response2 = graphQLRequests.withOneOptionalParam(characterInput);
		//
		logger.info("The query result is: " + response2.toString());

		logger.info("");
		logger.info("Executing this mutation: 'graphQLRequests.createHuman(input)'");
		//
		// Below is all you need to execute the GraphQL Request defined in the GraphQL Repository: graphQLRequests
		Human human = graphQLRequests.createHuman(humanInput).getCreateHuman();
		//
		logger.info("The mutation result is: " + human.toString());

		logger.info("Normal end of execution");
	}

	@Bean
	@ConditionalOnMissingBean(name = "graphqlUtils")
	GraphqlUtils graphqlUtils() {
		return GraphqlUtils.graphqlUtils;
	}

	@Bean
	@ConditionalOnMissingBean(name = "graphqlClientUtils")
	GraphqlClientUtils graphqlClientUtils() {
		return GraphqlClientUtils.graphqlClientUtils;
	}
}
