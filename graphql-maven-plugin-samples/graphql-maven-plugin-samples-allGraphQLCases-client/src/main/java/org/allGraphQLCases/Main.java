package org.allGraphQLCases;

import org.allGraphQLCases.client.util.MyQueryTypeExecutor;
import org.allGraphQLCases.impl.PartialDirectQueries;
import org.allGraphQLCases.impl.PartialPreparedQueries;
import org.allGraphQLCases.subscription.ExecSubscription;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.graphql_java_generator.client.GraphQLConfiguration;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * The main class, which executes the same partialQueries, built by three different methods. See
 * {@link PartialDirectQueries}, {@link PartialPreparedQueries}, {@link PartialWithBuilder}
 * 
 * @author etienne-sf
 */
@SpringBootApplication(scanBasePackageClasses = { Main.class, GraphQLConfiguration.class, MyQueryTypeExecutor.class })
public class Main implements CommandLineRunner {

	public static final String GRAPHQL_ENDPOINT = "http://localhost:8180/graphql";

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

	/**
	 * This method is started by Spring, once the Spring context has been loaded. This is run, as this class implements
	 * {@link CommandLineRunner}
	 */
	@Override
	public void run(String... args) throws Exception {

		// Execution of three way to user the GraphQL client, to call the GraphQL server

		System.out.println("============================================================================");
		System.out.println("======= SIMPLEST WAY: DIRECT QUERIES =======================================");
		System.out.println("============================================================================");
		execOne(new PartialDirectQueries(GRAPHQL_ENDPOINT));

		System.out.println("============================================================================");
		System.out.println("======= MOST SECURE WAY: PREPARED QUERIES ==================================");
		System.out.println("============================================================================");
		execOne(new PartialPreparedQueries(GRAPHQL_ENDPOINT));

		System.out.println("============================================================================");
		System.out.println("======= EXECUTING A SUBSCRIPTION ===========================================");
		System.out.println("============================================================================");
		new ExecSubscription().exec();

		System.out.println("");
		System.out.println("");
		System.out.println("Sample application finished ... enjoy !    :)");
		System.out.println("");
		System.out.println("Please take a look at the other samples, for other use cases");
		System.out.println(
				"You'll find more information on the plugin's web site: https://graphql-maven-plugin-project.graphql-java-generator.com/");
	}

	public void execOne(PartialQueries client)
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		try {

			System.out.println("----------------  withoutParameters  ----------------------------------------------");
			System.out.println(client.withoutParameters());

			// System.out.println("---------------- withOneOptionalParam -------------------------------------------");
			// System.out.println(client.withOneOptionalParam(Episode.NEWHOPE));
			//
			// System.out.println("---------------- withOneMandatoryParam ------------------------------------------");
			// System.out.println(client.withOneMandatoryParam(Episode.NEWHOPE));
			//
			// System.out.println("---------------- withOneMandatoryParamDefaultValue ------------------------------");
			// System.out.println(client.withOneMandatoryParamDefaultValue(Episode.NEWHOPE));
			//
			// System.out.println("---------------- withTwoMandatoryParamDefaultVal --------------------------------");
			// System.out.println(client.withTwoMandatoryParamDefaultVal(Episode.NEWHOPE));
			//
			// System.out.println("---------------- withEnum -------------------------------------------------------");
			// System.out.println(client.withEnum(Episode.NEWHOPE));
			//
			// System.out.println("---------------- withList -------------------------------------------------------");
			// System.out.println(client.withList(Episode.NEWHOPE));

		} catch (javax.ws.rs.ProcessingException e) {
			throw new RuntimeException(
					"Please start the server from the project graphql-maven-plugin-samples-StarWars-server, before executing the client part",
					e);
		}
	}
}
