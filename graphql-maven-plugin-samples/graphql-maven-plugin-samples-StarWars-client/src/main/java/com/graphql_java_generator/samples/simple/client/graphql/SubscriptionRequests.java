package com.graphql_java_generator.samples.simple.client.graphql;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.generated.graphql.GraphQLRequest;
import com.generated.graphql.MutationTypeExecutor;
import com.generated.graphql.SubscriptionTypeExecutor;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

@Component
public class SubscriptionRequests {

	@Autowired
	MutationTypeExecutor mutationTypeExecutor;
	@Autowired
	SubscriptionTypeExecutor subscriptionTypeExecutor;

	public void execSubscription()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException, IOException {
		// Preparation
		GraphQLRequest subscriptionRequest = subscriptionTypeExecutor
				.getNewCharacterGraphQLRequest("{id name __typename}");
		GraphQLRequest createHumanRequest = mutationTypeExecutor.getCreateHumanGraphQLRequest("{id name __typename}");
		NewCharacterSubscriptionCallback newCharacterSubscriptionCallback = new NewCharacterSubscriptionCallback();
		String humaneName = "Anakin";
		String homePlanet = "Tatooine";

		// Go, go, go
		System.out.println("Submitting the 'subscribeToNewPostWithBindValues' GraphQL subscription");
		subscriptionTypeExecutor.newCharacter(subscriptionRequest, newCharacterSubscriptionCallback);

		// Let's check that everything is ready
		if (!newCharacterSubscriptionCallback.connected) {
			throw new RuntimeException("The subscription should be active");
		}

		System.out.println("Creating a Human (for which we expect a notification) from this name: '" + humaneName
				+ "' and this homePlanet: '" + homePlanet + "'");
		mutationTypeExecutor.createHuman(createHumanRequest, humaneName, homePlanet);

		//////////////////////////////////////////////////////////////////////////////////////
		// Let's check that we've received the expected notification, for this post creation

		// Let's wait 10 seconds max for the post creation notification (from the subscription). We'll get interrupted
		// before, as soon as we receive the notification
		// (see the callback implementation in the PostSubscriptionCallback class)
		com.generated.graphql.Character notifiedPost = null;
		// Let's wait 10s max, until the connection is active
		final int TIMEOUT = 10;
		for (int i = 0; i < TIMEOUT * 1000 / 10; i += 1) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
			if ((notifiedPost = newCharacterSubscriptionCallback.lastReceivedMessage) != null) {
				// Ok, we're connected. We're done
				break;
			}
		}
		if (notifiedPost == null) {
			throw new RuntimeException("The notification for the post creation was not received");
		}

		// We need to free the server resources, at the end
		newCharacterSubscriptionCallback.close();
	}

}
