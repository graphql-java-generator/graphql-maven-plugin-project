package graphql.java.client;

import java.io.IOException;

import graphql.java.client.domain.starwars.Character;
import graphql.java.client.domain.starwars.Episode;
import graphql.java.client.domain.starwars.Human;
import graphql.java.client.domain.starwars.QueryType;
import graphql.java.client.request.ObjectResponse;
import graphql.java.client.response.GraphQLExecutionException;
import graphql.java.client.response.GraphQLRequestPreparationException;

/**
 * Manual test for query execution. Not a JUnit test. The automation for this test is done in the
 * graphql-maven-plugin-samples-StarWars-server module. This class is done for manual testing of the client, before
 * checking all around with the maven build of all modules.
 * 
 * @author EtienneSF
 */
public class ManualTest_StarWars {

	static String graphqlEndpoint = "http://localhost:8180/graphql";
	static QueryExecutor executor = new QueryExecutorImpl(graphqlEndpoint);
	static QueryType queryType = new QueryType(graphqlEndpoint);

	public static void main(String[] args)
			throws GraphQLExecutionException, IOException, GraphQLRequestPreparationException {

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////// Short way: your write the GraphQL yourself
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////

		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("------------------    executeHero()    ----------------------------------------------");

		// Execution of the query. We get the result back in a POJO
		Character character = queryType.hero("{id name friends {id name appearsIn friends{id name}}}", Episode.NEWHOPE);

		System.out.println(character);

		//

		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("------------------    executeHuman()    ---------------------------------------------");

		// Execution of the query. We get the result back in a POJO
		Human human = queryType.human("{id name appearsIn homePlanet friends{name}}",
				"00000000-0000-0000-0000-000000000180");

		System.out.println(human);

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////// More verbose: you use our Builder.
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////

		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("------------------    executeHero()    (with builder)   -----------------------------");

		// ObjectResponse
		ObjectResponse objectResponse = queryType.getHeroResponseBuilder().withField("id").withField("name")
				.withField("appearsIn")
				.withSubObject("friends", ObjectResponse.newSubObjectBuilder(Character.class).withField("name").build())
				.build();

		// Execution of the query. We get the result back in a POJO
		Character hero = queryType.hero(objectResponse, Episode.NEWHOPE);

		System.out.println(hero);

		//

		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("------------------    executeHuman()   (with builder)  ------------------------------");

		// ObjectResponse
		objectResponse = queryType.getHumanResponseBuilder()//
				.withField("id").withField("name").withField("appearsIn")//
				.withSubObject("friends", ObjectResponse.newSubObjectBuilder(Character.class).withField("name").build())
				.build();

		// Execution of the query. We get the result back in a POJO
		human = queryType.human(objectResponse, "00000000-0000-0000-0000-000000000180");

		System.out.println(human);

		System.out.println("");
		System.out.println("");
		System.out.println("Sample application finished ... enjoy !    :)");
		System.out.println("");
		System.out.println("(please take a look at the other samples, for other use cases)");
	}

}
