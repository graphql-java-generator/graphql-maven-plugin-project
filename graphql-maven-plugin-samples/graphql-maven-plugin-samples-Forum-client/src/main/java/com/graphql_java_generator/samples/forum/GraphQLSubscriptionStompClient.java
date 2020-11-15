/**
 * 
 */
package com.graphql_java_generator.samples.forum;

import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql_java_generator.GraphqlUtils;

/**
 * Thanks to Baeldung
 * 
 * @see https://www.baeldung.com/websockets-api-java-spring-client
 * @author etienne-sf
 */
public class GraphQLSubscriptionStompClient<R, T> implements StompSessionHandler {

	/** Logger for this class */
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/** A singleton of the main runtime utility classes */
	GraphqlUtils graphqlUtils = GraphqlUtils.graphqlUtils;

	/** The request to send to the GraphQL server, to initiate the subscription */
	final String request;

	/** The name of the subscription, to provide some context in log and in exception messages */
	final String subscriptionName;

	/** The callback that will receive the events sent by the web socket */
	final SubscriptionCallback<T> subscriptionCallback;

	/** The jackson instance that will handle deserialization of the incoming messages */
	ObjectMapper objectMapper = new ObjectMapper();

	/** The java generated from the GraphQL subscription type, as defined in the GraphQL schema */
	final Class<R> subscriptionType;
	/**
	 * The class that maps to the messages that will be sent back from the GraphQL server, once the subscription is
	 * subscribed. It's actually what's returned by this subscription, as defined in the GraphQL schema. In other words,
	 * it's the java type that matches the field in the subscription GraphQL type, for this subscribed subscription.
	 */
	final Class<T> messageType;

	/** The session, that will receive upon connection of the web socket. */
	WebSocketSession session = null;

	public GraphQLSubscriptionStompClient(String request, String subscriptionName,
			SubscriptionCallback<T> subscriptionCallback, Class<R> subscriptionType, Class<T> messsageType) {
		this.request = request;
		this.subscriptionName = subscriptionName;
		this.subscriptionCallback = subscriptionCallback;
		this.subscriptionType = subscriptionType;
		this.messageType = messsageType;
	}

	@Override
	public Type getPayloadType(StompHeaders headers) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handleFrame(StompHeaders headers, Object payload) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
			Throwable exception) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleTransportError(StompSession session, Throwable exception) {
		// TODO Auto-generated method stub

	}

}
