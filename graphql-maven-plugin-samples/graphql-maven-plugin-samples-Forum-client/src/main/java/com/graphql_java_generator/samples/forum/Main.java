package com.graphql_java_generator.samples.forum;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.graphql_java_generator.client.GraphQLConfiguration;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.Queries;
import com.graphql_java_generator.samples.forum.client.graphql.PartialDirectRequests;
import com.graphql_java_generator.samples.forum.client.graphql.PartialPreparedRequests;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.GraphQLRequest;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Member;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.MutationTypeExecutor;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Post;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.PostInput;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.QueryTypeExecutor;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.SubscriptionTypeExecutor;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.TopicPostInput;

/**
 * A Spring Boot client app. Very easy to use and to configure
 * 
 * @author etienne-sf
 */
@SpringBootApplication(scanBasePackageClasses = { Main.class, GraphQLConfiguration.class, QueryTypeExecutor.class })
public class Main implements CommandLineRunner {
	public static String GRAPHQL_ENDPOINT_URL = "http://localhost:8180/graphql";

	@Autowired
	PartialDirectRequests partialDirectRequests;
	@Autowired
	PartialPreparedRequests partialPreparedRequests;
	@Autowired
	MutationTypeExecutor mutationTypeExecutor;
	@Autowired
	SubscriptionTypeExecutor subscriptionTypeExecutor;

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

	/**
	 * This method is started by Spring, once the Spring context has been loaded. This is run, as this class implements
	 * {@link CommandLineRunner}
	 */
	@Override
	public void run(String... args) throws Exception {
		// System.out.println("");
		// System.out.println("============================================================================");
		// System.out.println("======= SIMPLEST WAY: DIRECT QUERIES =======================================");
		// System.out.println("============================================================================");
		// exec(partialDirectRequests, null);
		//
		// System.out.println("");
		// System.out.println("============================================================================");
		// System.out.println("======= MOST SECURE WAY: PREPARED QUERIES ==================================");
		// System.out.println("============================================================================");
		// exec(partialPreparedRequests, null);

		System.out.println("");
		System.out.println("============================================================================");
		System.out.println("======= LET'S EXECUTE A SUBSCRIPTION      ==================================");
		System.out.println("============================================================================");
		execSubscription();

		System.out.println("");
		System.out.println("");
		System.out.println("Sample application finished ... enjoy !    :)");
		System.out.println("");
		System.out.println("(please take a look at the other samples, for other use cases)");
	}

	void exec(Queries client, String name) throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		try {

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  boardsSimple  --------------------------------------------");
			System.out.println(client.boardsSimple());

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  topicAuthorPostAuthor  -----------------------------------");
			Calendar cal = Calendar.getInstance();
			cal.set(2018, 12, 20);
			System.out.println(client.topicAuthorPostAuthor("Board name 2", cal.getTime()));

			System.out.println("----------------------------------------------------------------------------");
			System.out.println("----------------  createBoard  ---------------------------------------------");
			// We need a unique name. Let's use a random name for that, if none was provided.
			name = (name != null) ? name : "Name " + Float.floatToIntBits((float) Math.random() * Integer.MAX_VALUE);
			System.out.println(client.createBoard(name, true));

		} catch (javax.ws.rs.ProcessingException e) {
			System.out.println("");
			System.out.println("ERROR");
			System.out.println("");
			System.out.println(
					"Please start the server from the project graphql-maven-plugin-samples-StarWars-server, before executing the client part");
		}
	}

	private void execSubscription()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, IOException {
		// Preparation
		GraphQLRequest subscriptionRequest = subscriptionTypeExecutor
				.getSubscribeToNewPostGraphQLRequest("{id date author publiclyAvailable title content}");
		GraphQLRequest createPostRequest = mutationTypeExecutor
				.getCreatePostGraphQLRequest("{id date author{id} title content publiclyAvailable}");

		PostSubscriptionCallback<Post> postSubscriptionCallback = new PostSubscriptionCallback<>(
				Thread.currentThread());
		Member author = new Member();
		author.setId("12");
		PostInput postInput = new PostInput();
		postInput.setTopicId("22");
		postInput.setInput(getTopicPostInput(author, "Some other content",
				new GregorianCalendar(2020, 11 - 1, 21).getTime(), false, "The good title for a post"));

		// Go, go, go
		System.out.println("Submitting the 'subscribeToNewPostWithBindValues' GraphQL subscription");
		subscriptionTypeExecutor.subscribeToNewPost(subscriptionRequest, postSubscriptionCallback, "Board name 1");

		// Let's check that everything is ready
		if (!postSubscriptionCallback.connected) {
			throw new RuntimeException("The subscription should be active");
		}

		System.out.println(
				"Creating a post (for which we expect a notification) from this postInput: " + postInput.toString());
		mutationTypeExecutor.createPost(createPostRequest, postInput);

		//////////////////////////////////////////////////////////////////////////////////////
		// Let's check that we've received the expected notification, for this post creation

		// Let's wait 10 seconds max for the post creation notification (from the subscription). We'll get interrupted
		// before, as soon as we receive the notification
		// (see the callback implementation in the PostSubscriptionCallback class)
		Post notifiedPost = null;
		// Let's wait 10s max, until the connection is active
		final int TIMEOUT = 10;
		for (int i = 0; i < TIMEOUT * 1000 / 10; i += 1) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
			if ((notifiedPost = postSubscriptionCallback.lastReceivedMessage) != null) {
				// Ok, we're connected. We're done
				break;
			}
		}
		if (notifiedPost == null) {
			throw new RuntimeException("The notification for the post creation was not received");
		}

		// We need to free the server resources, at the end
		postSubscriptionCallback.close();
	}

	private TopicPostInput getTopicPostInput(Member author, String content, Date date, boolean publiclyAvailable,
			String title) {
		TopicPostInput input = new TopicPostInput();
		input.setAuthorId(author.getId());
		input.setContent(content);
		input.setDate(date);
		input.setPubliclyAvailable(publiclyAvailable);
		input.setTitle(title);

		return input;
	}

	@Bean
	String graphqlEndpoint() {
		return GRAPHQL_ENDPOINT_URL;
	}

	@Bean
	WebClient webClient(String graphqlEndpoint) {
		return WebClient.builder()//
				.baseUrl(graphqlEndpoint)//
				// .defaultCookie("cookieKey", "cookieValue")//
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultUriVariables(Collections.singletonMap("url", graphqlEndpoint))//
				.build();
	}

}
