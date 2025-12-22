package org.allGraphQLCases.demo;

import java.util.Arrays;
import java.util.List;

import org.allGraphQLCases.client.CEP_Episode_CES;
import org.allGraphQLCases.client.CINP_CharacterInput_CINS;
import org.allGraphQLCases.client.util.MyQueryTypeExecutorAllGraphQLCases;
import org.allGraphQLCases.client2.MyQueryTypeExecutorAllGraphQLCases2;
import org.allGraphQLCases.demo.impl.PartialDirectQueries;
import org.allGraphQLCases.demo.impl.PartialPreparedQueries;
import org.allGraphQLCases.demo.impl.PartialRequestGraphQLRepository;
import org.allGraphQLCases.demo.subscription.ExecSubscription;
import org.forum.client.QueryExecutorForum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.client.graphqlrepository.EnableGraphQLRepositories;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * The main class, which executes the same partialQueries, built by three different methods. See
 * {@link PartialDirectQueries}, {@link PartialPreparedQueries}, {@link PartialWithBuilder}<BR/>
 * 
 * A sample query, to get an OAuth token:
 * 
 * <pre>
curl -u "clientId:secret" -X POST "http://localhost:8181/oauth2/token?grant_type=client_credentials" --noproxy "*" -i
 * </pre>
 * 
 * Then, reuse the previous token in the next query:
 * 
 * <pre>
curl -i -X POST "http://localhost:8180/graphql" --noproxy "*" -H "Authorization: Bearer 8c8e4a5b-d903-4ed6-9738-6f7f364b87ec"
 * </pre>
 * 
 * @author etienne-sf
 * @see https://michalgebauer.github.io/spring-graphql-security/
 */
@SpringBootApplication(scanBasePackageClasses = { Main.class, GraphqlClientUtils.class,
		MyQueryTypeExecutorAllGraphQLCases.class, MyQueryTypeExecutorAllGraphQLCases2.class, QueryExecutorForum.class })
@EnableAutoConfiguration()
@EnableGraphQLRepositories({ "org.allGraphQLCases.demo.impl", "org.allGraphQLCases.subscription.graphqlrepository" })
public class Main implements CommandLineRunner {

	@Autowired
	PartialDirectQueries partialDirectQueries;
	@Autowired
	PartialPreparedQueries partialPreparedQueries;
	@Autowired
	PartialRequestGraphQLRepository partialRequestGraphQLRepository;
	@Autowired
	ExecSubscription execSubscription;

	public static void main(String[] args) {
		try (ConfigurableApplicationContext context = SpringApplication.run(Main.class, args)) {
			// No action
		}
	}

	/**
	 * This method is started by Spring, once the Spring context has been loaded. This is run, as this class implements
	 * {@link CommandLineRunner}
	 */
	@Override
	public void run(String... args) throws Exception {

		// Execution of three different ways of calling the GraphQL server

		System.out.println("============================================================================");
		System.out.println("======= SIMPLEST WAY: GRAPHQL REPOSITOTRY  =================================");
		System.out.println("============================================================================");
		execOne(partialRequestGraphQLRepository);

		System.out.println("============================================================================");
		System.out.println("======= A SIMPLE WAY: DIRECT QUERIES =======================================");
		System.out.println("============================================================================");
		execOne(partialDirectQueries);

		System.out.println("============================================================================");
		System.out.println("======= MOST SECURE WAY: PREPARED QUERIES ==================================");
		System.out.println("============================================================================");
		execOne(partialPreparedQueries);

		// Then a subscription
		System.out.println("============================================================================");
		System.out.println("======= EXECUTING A SUBSCRIPTION ===========================================");
		System.out.println("============================================================================");
		execSubscription.exec();

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

		System.out.println("----------------  withoutParameters  ----------------------------------------------");
		System.out.println(client.withoutParameters());

		System.out.println("---------------- withOneOptionalParam -------------------------------------------");
		CINP_CharacterInput_CINS ci1 = CINP_CharacterInput_CINS.builder().withName("my name")
				.withAppearsIn(Arrays.asList(CEP_Episode_CES.JEDI, CEP_Episode_CES.NEWHOPE)).withType("Droid").build();
		System.out.println(client.withOneOptionalParam(ci1));

		System.out.println("---------------- withOneMandatoryParam ------------------------------------------");
		CINP_CharacterInput_CINS ci2 = CINP_CharacterInput_CINS.builder().withName("my other name")
				.withAppearsIn(Arrays.asList()).withType("Human").build();
		System.out.println(client.withOneMandatoryParam(ci2));

		System.out.println("---------------- withEnum -------------------------------------------------------");
		System.out.println(client.withEnum(CEP_Episode_CES.NEWHOPE));

		System.out.println("---------------- withList -------------------------------------------------------");
		List<CINP_CharacterInput_CINS> chars = Arrays.asList(ci1, ci2);
		System.out.println(client.withList("The name", chars));
	}

}
