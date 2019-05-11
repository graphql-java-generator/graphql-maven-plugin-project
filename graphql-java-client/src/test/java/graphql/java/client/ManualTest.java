package graphql.java.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import graphql.java.client.domain.Character;
import graphql.java.client.domain.CharacterImpl;
import graphql.java.client.domain.Episode;
import graphql.java.client.domain.Human;
import graphql.java.client.domain.QueryType;
import graphql.java.client.request.InputParameter;
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
public class ManualTest {

	static QueryExecutor executor = new QueryExecutorImpl();
	static QueryType queryType = new QueryType();

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
		Human human = queryType.human("{id name appearsIn homePlanet friends{name}}", "180");

		System.out.println(human);

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////// More verbose: you use our Builder.
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////

		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("------------------    executeHero()    (with builder)   -----------------------------");

		// InputParameters
		List<InputParameter> parameters = new ArrayList<>();
		parameters.add(new InputParameter("episode", Episode.NEWHOPE));

		// ObjectResponse
		ObjectResponse objectResponse = queryType.getHeroResponseBuilder().withField("id").withField("name")
				.withField("appearsIn")
				.withSubObject("friends", ObjectResponse.newSubObjectBuilder(Character.class).withField("name").build())
				.build();

		// Execution of the query. We get the result back in a POJO
		CharacterImpl characterImpl = executor.execute(objectResponse, parameters, CharacterImpl.class);

		System.out.println(characterImpl);

		//

		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("------------------    executeHuman()   (with builder)  ------------------------------");

		// InputParameters
		parameters = new ArrayList<>();
		parameters.add(new InputParameter("id", "180"));

		// ObjectResponse
		objectResponse = queryType.getHumanResponseBuilder()//
				.withField("id").withField("name").withField("appearsIn")//
				.withSubObject("friends", ObjectResponse.newSubObjectBuilder(Character.class).withField("name").build())
				.build();

		// Execution of the query. We get the result back in a POJO
		human = executor.execute(objectResponse, parameters, Human.class);

		System.out.println(human);
	}

}
