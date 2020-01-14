package org.allGraphQLCases;

import org.allGraphQLCases.graphql.DirectQueries;
import org.allGraphQLCases.graphql.PreparedQueries;
import org.allGraphQLCases.graphql.WithBuilder;

import com.graphql_java_generator.client.response.GraphQLRequestExecutionException;
import com.graphql_java_generator.client.response.GraphQLRequestPreparationException;

/**
 * The main class, which executes the same queries, built by three different methods. See {@link DirectQueries},
 * {@link PreparedQueries}, {@link WithBuilder}
 * 
 * @author EtienneSF
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
		execOne(new DirectQueries(GRAPHQL_ENDPOINT));

		System.out.println("============================================================================");
		System.out.println("======= MOST SECURE WAY: PREPARED QUERIES ==================================");
		System.out.println("============================================================================");
		execOne(new PreparedQueries(GRAPHQL_ENDPOINT));

		System.out.println("============================================================================");
		System.out.println("======= MOST SECURE WAY: PREPARED QUERIES ==================================");
		System.out.println("============================================================================");
		execOne(new WithBuilder(GRAPHQL_ENDPOINT));

		System.out.println("");
		System.out.println("");
		System.out.println("Sample application finished ... enjoy !    :)");
		System.out.println("");
		System.out.println("(please take a look at the other samples, for other use cases)");
	}

	public void execOne(Queries client) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// A random value, to variabilize mutations
		int i = (int) (Math.random() * Integer.MAX_VALUE);

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
