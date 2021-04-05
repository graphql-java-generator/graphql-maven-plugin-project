package org.allGraphQLCases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.allGraphQLCases.client.AnotherMutationType;
import org.allGraphQLCases.client.CharacterInput;
import org.allGraphQLCases.client.Episode;
import org.allGraphQLCases.client.Human;
import org.allGraphQLCases.client.HumanInput;
import org.allGraphQLCases.client.MyQueryType;
import org.allGraphQLCases.client.util.AnotherMutationTypeExecutor;
import org.allGraphQLCases.client.util.GraphQLRequest;
import org.allGraphQLCases.client.util.MyQueryTypeExecutor;
import org.allGraphQLCases.client.util.TheSubscriptionTypeExecutor;
import org.allGraphQLCases.subscription.SubscriptionCallbackToADate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

@Execution(ExecutionMode.CONCURRENT)
class GraphQLVariablesIT {

	ApplicationContext ctx;

	MyQueryTypeExecutor myQuery;
	AnotherMutationTypeExecutor mutationType;

	public static class ExtensionValue {
		public String name;
		public String forname;
	}

	@BeforeEach
	void setup() throws GraphQLRequestPreparationException {
		ctx = new AnnotationConfigApplicationContext(SpringTestConfig.class);
		myQuery = ctx.getBean(MyQueryTypeExecutor.class);
		assertNotNull(myQuery);
		mutationType = ctx.getBean(AnotherMutationTypeExecutor.class);
		assertNotNull(mutationType);
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_GraphQLVariable_directQuery()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		List<List<Double>> matrix = Arrays.asList(//
				null, //
				Arrays.asList(), //
				Arrays.asList(1.0), //
				Arrays.asList(4.0, 5.0, 6.0)//
		);

		// Go, go, go
		MyQueryType resp = myQuery.exec(
				"query queryWithAMatrix($matrixParam: [[Float]]!) {withListOfList(matrix:$matrixParam){matrix}}", //
				"matrixParam", matrix);

		// Verifications
		List<List<Double>> ret = resp.getWithListOfList().getMatrix();
		assertNotNull(ret);
		assertEquals(4, ret.size());
		int i = 0;
		//
		assertNull(ret.get(i++));
		//
		List<Double> item = ret.get(i++);
		assertNotNull(item);
		assertEquals(0, item.size());
		//
		item = ret.get(i++);
		assertNotNull(item);
		assertEquals(1, item.size());
		assertEquals(1, item.get(0));
		//
		item = ret.get(i++);
		assertNotNull(item);
		assertEquals(3, item.size());
		assertEquals(4, item.get(0));
		assertEquals(5, item.get(1));
		assertEquals(6, item.get(2));
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void withDirectiveTwoParameters() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		GraphQLRequest directiveOnQuery = mutationType
				.getGraphQLRequest("query namedQuery($uppercase :\n" //
						+ "Boolean, \n\r"//
						+ " $Value :   String ! , $anotherValue:String) {directiveOnQuery (uppercase: $uppercase) @testDirective(value:$Value, anotherValue:$anotherValue)}");
		Map<String, Object> params = new HashMap<>();
		params.put("uppercase", true);
		params.put("anotherValue", "another value");
		params.put("Value", "a first value");

		// Go, go, go
		MyQueryType resp = directiveOnQuery.execQuery(params);

		// Verifications
		assertNotNull(resp);
		List<String> ret = resp.getDirectiveOnQuery();
		assertNotNull(ret);
		assertEquals(2, ret.size());
		//
		assertEquals("A FIRST VALUE", ret.get(0));
		assertEquals("ANOTHER VALUE", ret.get(1));
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void mutation() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		GraphQLRequest mutationWithDirectiveRequest = mutationType.getGraphQLRequest("" + //
				"mutation creation($name  : String!, $friends : [ CharacterInput\r\n] , $appearsIn: [Episode]!,$uppercaseName:Boolean)\r\n"//
				+ "{createHuman (human: {name:$name, friends:$friends, appearsIn:$appearsIn}) "//
				+ "{id name(uppercase: $uppercaseName) appearsIn friends {id friends appearsIn name}}}"//
		);

		CharacterInput friendsParam = CharacterInput.builder().withName("a friend's name")
				.withAppearsIn(Arrays.asList(Episode.NEWHOPE)).withType("Human").build();

		// Go, go, go
		AnotherMutationType resp = mutationWithDirectiveRequest.execMutation(//
				"name", "a new name", //
				"appearsIn", Arrays.asList(Episode.JEDI, Episode.EMPIRE, Episode.NEWHOPE), //
				"uppercaseName", true, //
				"friends", friendsParam);

		// Verifications
		assertNotNull(resp);
		Human human = resp.getCreateHuman();
		assertNotNull(human);
		assertEquals("A NEW NAME", human.getName());
		assertEquals(3, human.getAppearsIn().size());
		assertEquals(Episode.JEDI, human.getAppearsIn().get(0));
		assertEquals(Episode.EMPIRE, human.getAppearsIn().get(1));
		assertEquals(Episode.NEWHOPE, human.getAppearsIn().get(2));
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_mixBindParameterGraphQLVariable()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		GraphQLRequest mutation = mutationType.getGraphQLRequest("" + //
				"mutation creation($uppercaseName:Boolean)\r\n"//
				+ "{createHuman (human: &human) "//
				+ "{id name(uppercase: $uppercaseName) appearsIn friends {id friends appearsIn name}}}"//
		);

		CharacterInput friendParam = CharacterInput.builder().withName("a friend's name")
				.withAppearsIn(Arrays.asList(Episode.NEWHOPE)).withType("Human").build();
		//
		HumanInput input = new HumanInput();
		input.setName("a new name");
		input.setAppearsIn(Arrays.asList(Episode.JEDI, Episode.EMPIRE, Episode.NEWHOPE));
		input.setFriends(Arrays.asList(friendParam));

		// Go, go, go
		AnotherMutationType resp = mutation.execMutation(//
				"human", input, //
				"uppercaseName", true);

		// Verifications
		assertNotNull(resp);
		Human human = resp.getCreateHuman();
		assertNotNull(human);
		assertEquals("A NEW NAME", human.getName());
	}

	public static class SubscribeToADate implements Runnable {
		final TheSubscriptionTypeExecutor subscriptionExecutor;
		final SubscriptionCallbackToADate callback;
		final String clientName;

		SubscribeToADate(TheSubscriptionTypeExecutor executor, String clientName) {
			this.subscriptionExecutor = executor;
			this.clientName = clientName;
			this.callback = new SubscriptionCallbackToADate(clientName);
		}

		@Override
		public void run() {
			try {
				// GraphQLRequest mutation = subscriptionExecutor.getGraphQLRequest(
				// "subscription sub($aCustomScalarParam: Date!) {issue53($aCustomScalarParam}{}}");
				SubscriptionClient sub = subscriptionExecutor.issue53("", callback, null);
				fail("No subscription with full requests");

				try {
					Thread.sleep(500); // Wait 0.5 second, so that other thread is ready
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}

				// Let's wait a max of 1 second, until we receive some notifications
				try {
					for (int i = 1; i < 10; i += 1) {
						if (callback.lastReceivedMessage != null)
							break;
						Thread.sleep(100); // Wait 0.1 second
					} // for
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}

				// Let's disconnect from the subscription
				sub.unsubscribe();
			} catch (GraphQLRequestExecutionException |

					GraphQLRequestPreparationException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	public void test_GraphQLVariables_subscribeToAList()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, InterruptedException {
		// Preparation
		TheSubscriptionTypeExecutor subscriptionExecutor = ctx.getBean(TheSubscriptionTypeExecutor.class);

		// To test the issue 72, we create two clients for the subscription, and check that each of them properly
		// receives the notifications
		SubscribeToADate client1 = new SubscribeToADate(subscriptionExecutor, "client1");
		SubscribeToADate client2 = new SubscribeToADate(subscriptionExecutor, "client2");

		Thread thread1 = new Thread(client1);
		Thread thread2 = new Thread(client2);

		thread1.start();
		thread2.start();

		// Let's wait for the end of our two subscription client threads
		thread1.join();
		thread2.join();

		assertNotNull(client1.callback.lastReceivedMessage, "The client 1 should have received a message");
		assertNotNull(client2.callback.lastReceivedMessage, "The client 2 should have received a message");
	}
}
