/**
 * 
 */
package com.graphql_java_generator.samples.forum;

import java.io.IOException;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * This class implements the Web Socket, as needed by the Spring Web Socket implementation.
 *
 * @param <R>
 *            The class that is generated from the subscription definition in the GraphQL schema. It contains one
 *            attribute, for each available subscription. In the incoming messages, the data JSON field of the GraphQL
 *            server response is of this type. So it is needed to decode the received JSON.
 * @param <T>
 *            The type that must be returned by the query or mutation: it's the class that maps to the GraphQL type
 *            returned by this subscription. In other words, it's the java type that matches the field in the
 *            subscription GraphQL type, for this subscribed subscription.
 * @author etienne-sf
 */
public class GraphQLReactiveWebSocketHandler<R, T> implements WebSocketHandler {

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

	public GraphQLReactiveWebSocketHandler(String request, String subscriptionName,
			SubscriptionCallback<T> subscriptionCallback, Class<R> subscriptionType, Class<T> messsageType) {
		this.request = request;
		this.subscriptionName = subscriptionName;
		this.subscriptionCallback = subscriptionCallback;
		this.subscriptionType = subscriptionType;
		this.messageType = messsageType;
	}

	@Override
	public Mono<Void> handle(WebSocketSession session) {
		logger.debug("Web Socket connected (session {}) for request {}", session, request);

		Flux<WebSocketMessage> input = session.receive().log("message received")
				.doOnNext(message -> handleMessage(message));

		// The onSuccess consumer will be called, once the subscription is written into the web socket
		Consumer<? super Void> onSuccess = new Consumer<Void>() {
			@Override
			public void accept(Void t) {
				// We've executed the subscription. Let's transmit this good news to the application callback
				subscriptionCallback.onConnect(session);
			}
		};
		// Let actually execute the subscription
		logger.trace("Before sending the subscription request into the web socket");
		session.send(Flux.just(request).map(session::textMessage)).doOnSuccess(onSuccess).subscribe();
		logger.trace("After sending the subscription request into the web socket");

		// Setting the session indicates that the connection is done. So we do it last.
		this.session = session;

		logger.trace("End of handle(session) method execution");

		return input.then();
	}

	/**
	 * The callback that will receive the messages from the web socket. It will map these JSON messages to the relevant
	 * java class, and call the application callback with this java cobjects
	 * 
	 * @param message
	 *            The received JSON message
	 */
	public void handleMessage(WebSocketMessage message) {
		logger.debug("Message received from the Web Socket: {}", message);

		String msg = message.getPayloadAsText();

		try {

			R r = objectMapper.readValue(msg, subscriptionType);
			@SuppressWarnings("unchecked")
			T t = (T) graphqlUtils.invokeGetter(r, subscriptionName);
			subscriptionCallback.onMessage(t);

		} catch (IOException e) {
			String errorMsg = "An error (" + e.getMessage()
					+ ") occured while parsing a server message for subscription '" + subscriptionName + "'";
			if (logger.isTraceEnabled()) {
				errorMsg = errorMsg + ". The received message is <" + msg + ">";
			}
			logger.error(errorMsg);
			// Let's tell the application that an error occurs while reading a message
			subscriptionCallback.onError(new GraphQLRequestExecutionException(errorMsg, e));
		}
	}
}
