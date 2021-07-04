package com.graphql_java_generator.client;

import java.io.IOException;
import java.util.List;

import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.domain.client.forum.Board;
import com.graphql_java_generator.domain.client.forum.MutationType;
import com.graphql_java_generator.domain.client.forum.QueryType;
import com.graphql_java_generator.domain.client.forum.Topic;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * Manual test for query execution. Not a JUnit test. This allows to execute checks<BR/>
 * The automation for this test is done in the graphql-maven-plugin-samples-Forum-server module. This class is done for
 * manual testing of the client, before checking all around with the whole maven build of all modules.
 * 
 * @author etienne-sf
 */
public class ManualTest_Forum {

	static String graphqlEndpoint = "http://localhost:8180/graphql";
	static QueryExecutor executor = new QueryExecutorImpl(graphqlEndpoint);
	static QueryType queryType = new QueryType(graphqlEndpoint);
	static MutationType mutationType = new MutationType(graphqlEndpoint);

	public static void main(String[] args)
			throws GraphQLRequestExecutionException, IOException, GraphQLRequestPreparationException {
		ObjectResponse resp;
		List<Board> boards;
		List<Topic> topics;

		System.out.println("/////////////////////////////////////////////////////////////////////////////////////");
		System.out.println("////////////////// Short way: you write the GraphQL yourself");
		System.out.println("/////////////////////////////////////////////////////////////////////////////////////");

		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("------------------ boards() ---------------------------------------------------");

		// Execution of the query. We get the result back in a POJO
		boards = queryType.boards(
				"{id name publiclyAvailable topics(since: \"2018-12-20\") {id date author{id name email type} nbPosts posts{date author{name email type}}}}");

		System.out.println(boards);

		//

		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("------------------ topics() ---------------------------------------------------");

		// Execution of the query. We get the result back in a POJO
		topics = queryType.topics(
				"{id date author{name email type alias}publiclyAvailable nbPosts title content posts{id date author{alias email} publiclyAvailable title content}}",
				"Board name 2");

		System.out.println(topics);

		System.out.println("/////////////////////////////////////////////////////////////////////////////////////");
		System.out.println("//////////////// recommanded way: you prepare the requets in a GraphQL way");
		System.out.println("/////////////////////////////////////////////////////////////////////////////////////");

		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("------------------ boards() ---------------------------------------------------");

		// Preparation query (this should be executed somewhere in the initialization stuff, like a Spring Bean)
		resp = queryType.getBoardsResponseBuilder().withQueryResponseDef(
				"{id name publiclyAvailable topics{id date author{id name email type} nbPosts posts{date author{name email type}}}}")
				.build();
		// Execution of the query. We get the result back in a POJO
		boards = queryType.boards(resp);

		System.out.println(boards);

		//

		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("------------------ topics() ---------------------------------------------------");

		// Preparation query (this should be executed somewhere in the initialization stuff, like a Spring Bean)
		resp = queryType.getTopicsResponseBuilder().withQueryResponseDef(
				"{id date author{name email type alias}publiclyAvailable nbPosts title content posts{id date author{alias email} publiclyAvailable title content}}")
				.build();
		// Execution of the query. We get the result back in a POJO
		topics = queryType.topics(resp, "Board name 2");

		System.out.println(topics);

		System.out.println("/////////////////////////////////////////////////////////////////////////////////////");
		System.out.println("////////////////// let's call a mutation");
		System.out.println("/////////////////////////////////////////////////////////////////////////////////////");

		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("------------------    createBoard()    ----------------------------------------------");

		// Preparation query (this should be executed somewhere in the initialization stuff, like a Spring Bean)
		resp = mutationType.getCreateBoardResponseBuilder().build(); // Let's load the whole object
		// Execution of the query. We get the result back in a POJO

		Board board = mutationType.createBoard(resp, "a new Board", true);

		System.out.println(board);

		System.out.println("");
		System.out.println("");
		System.out.println("Sample application finished ... enjoy !    :)");
		System.out.println("");
		System.out.println("(please take a look at the other samples, for other use cases)");
	}

}
