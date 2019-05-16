package graphql.java.client;

import java.io.IOException;
import java.util.List;

import graphql.java.client.domain.forum.Board;
import graphql.java.client.domain.forum.Member;
import graphql.java.client.domain.forum.Post;
import graphql.java.client.domain.forum.QueryType;
import graphql.java.client.domain.forum.Topic;
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
public class ManualTest_Forum {

	static QueryExecutor executor = new QueryExecutorImpl();
	static QueryType queryType = new QueryType();

	public static void main(String[] args)
			throws GraphQLExecutionException, IOException, GraphQLRequestPreparationException {

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////// Short way: your write the GraphQL yourself
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////

		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("------------------    boards()    ---------------------------------------------------");

		// Execution of the query. We get the result back in a POJO
		List<Board> boards = queryType.boards(
				"{id name publiclyAvailable topics{id date author{id name email type} nbPosts posts{date author{name email type}}}}");

		System.out.println(boards);

		//

		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("------------------    topics()    ---------------------------------------------------");

		// Execution of the query. We get the result back in a POJO
		List<Topic> topics = queryType.topics(
				"{id date author{name email type alias}publiclyAvailable nbPosts title content posts{id date author{alias email} publiclyAvailable title content}}",
				"Board name 2");

		System.out.println(topics);

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////// More verbose: you use our Builder.
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////

		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("------------------    boards()    (with builder)   -----------------------------");

		// ObjectResponse
		ObjectResponse objectResponse = queryType.getBoardsResponseBuilder().withField("id").withField("name")
				.withField("publiclyAvailable")//
				.withSubObject("topics", ObjectResponse.newSubObjectBuilder(Topic.class).withField("date")
						.withSubObject("author", ObjectResponse.newSubObjectBuilder(Member.class).withField("name")
								.withField("type").build())
						.withField("publiclyAvailable").withField("nbPosts").build())
				.build();

		// Execution of the query. We get the result back in a POJO
		boards = queryType.boards(objectResponse);

		System.out.println(boards);

		//

		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("------------------    topics()   (with builder)  ------------------------------");

		// ObjectResponse
		objectResponse = queryType.getTopicsResponseBuilder()//
				.withField("id").withField("date").withField("publiclyAvailable")//
				.withSubObject("author",
						ObjectResponse.newSubObjectBuilder(Member.class).withField("name").withField("type")
								.withField("email").build())
				.withSubObject("posts",
						ObjectResponse.newSubObjectBuilder(Post.class).withField("date").withField("title")
								.withField("content")
								.withSubObject("author", ObjectResponse.newSubObjectBuilder(Member.class)
										.withField("name").withField("type").withField("email").build())
								.build())
				.build();

		// Execution of the query. We get the result back in a POJO
		topics = queryType.topics(objectResponse, "Board name 2");

		System.out.println(topics);

		System.out.println("");
		System.out.println("");
		System.out.println("Sample application finished ... enjoy !    :)");
		System.out.println("");
		System.out.println("(please take a look at the other samples, for other use cases)");
	}

}
