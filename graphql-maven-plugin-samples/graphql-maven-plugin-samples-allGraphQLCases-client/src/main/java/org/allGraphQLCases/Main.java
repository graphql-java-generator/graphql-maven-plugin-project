package org.allGraphQLCases;

import org.allGraphQLCases.impl.PartialDirectQueries;
import org.allGraphQLCases.impl.PartialPreparedQueries;
import org.allGraphQLCases.subscription.ExecSubscription;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * The main class, which executes the same partialQueries, built by three different methods. See
 * {@link PartialDirectQueries}, {@link PartialPreparedQueries}, {@link PartialWithBuilder}
 * 
 * @author etienne-sf
 */
public class Main {

	public static final String GRAPHQL_ENDPOINT = "http://localhost:8180/graphql";

	public static void main(String[] args) throws Exception {
		new Main().execAll();
	}

	void execAll() throws Exception {

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
		System.out.println("(please take a look at the other samples, for other use cases)");
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
