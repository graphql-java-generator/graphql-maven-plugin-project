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

		Character character = queryType.hero("{id name friends {id name appearsIn friends{id name}}}", Episode.NEWHOPE);
		System.out.println(character); // Note that character is a POJO

		//

		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("------------------    executeHuman()    ---------------------------------------------");

		Human human = queryType.human("{id name appearsIn homePlanet friends{name}}", "180");
		System.out.println(human); // Note that human is a POJO

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

		CharacterImpl characterImpl = executor.execute(objectResponse, parameters, CharacterImpl.class);
		System.out.println(characterImpl); // Note that characterImpl is a POJO

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

		human = executor.execute(objectResponse, parameters, Human.class);
		System.out.println(human); // Note that human is a POJO
	}

}
