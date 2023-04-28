/**
 * 
 */
package org.allGraphQLCases.minimal.spring_app;

import java.util.Arrays;
import java.util.List;

import org.allGraphQLCases.client.CEP_Episode_CES;
import org.allGraphQLCases.client.CINP_CharacterInput_CINS;
import org.allGraphQLCases.client.CINP_HumanInput_CINS;
import org.allGraphQLCases.client.CIP_Character_CIS;
import org.allGraphQLCases.client.CTP_Human_CTS;
import org.allGraphQLCases.client.MyQueryTypeExecutorAllGraphQLCases;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.client.graphqlrepository.EnableGraphQLRepositories;
import com.graphql_java_generator.util.GraphqlUtils;

/**
 * @author etienne-sf
 */
@SpringBootApplication(scanBasePackageClasses = { MinimalSpringApp.class, GraphqlClientUtils.class,
		MyQueryTypeExecutorAllGraphQLCases.class })
@EnableGraphQLRepositories({ "org.allGraphQLCases.minimal.spring_app" })
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
		CINP_CharacterInput_CINS characterInput = CINP_CharacterInput_CINS.builder().withName("the name")
				.withAppearsIn(Arrays.asList(CEP_Episode_CES.JEDI, CEP_Episode_CES.NEWHOPE)).withType("Human").build();
		CINP_HumanInput_CINS humanInput = CINP_HumanInput_CINS.builder().withName("the name")
				.withAppearsIn(Arrays.asList(CEP_Episode_CES.JEDI, CEP_Episode_CES.NEWHOPE)).build();

		logger.info("");
		logger.info("Executing this query: 'graphQLRequests.withoutParameters()' ");
		logger.info(
				"Note: the first GraphQL request execution is longer, as the OAuth token must be acquired, and Reactive code must be started");
		//
		// Below is all you need to execute the GraphQL Request defined in the GraphQL Repository: graphQLRequests
		List<CIP_Character_CIS> response1 = graphQLRequests.withoutParameters();
		//
		logger.info(response1.toString());

		logger.info("");
		logger.info("Executing this query: 'graphQLRequests.withOneOptionalParam(input)'");
		//
		// Below is all you need to execute the GraphQL Request defined in the GraphQL Repository: graphQLRequests
		CIP_Character_CIS response2 = graphQLRequests.withOneOptionalParam(characterInput);
		//
		logger.info("The query result is: " + response2.toString());

		logger.info("");
		logger.info("Executing this mutation: 'graphQLRequests.createHuman(input)'");
		//
		// Below is all you need to execute the GraphQL Request defined in the GraphQL Repository: graphQLRequests
		CTP_Human_CTS human = graphQLRequests.createHuman(humanInput).getCreateHuman();
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
