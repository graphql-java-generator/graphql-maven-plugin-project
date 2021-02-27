/**
 * 
 */
package com.graphql_java_generator.client;

import java.io.IOException;

import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.util.GraphqlUtils;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.EmitResult;
import reactor.core.publisher.Sinks.One;

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

	One<Void> subscriptionMono = Sinks.one();

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
		this.session = session;

		logger.trace("new web socket session received: {}", session);

		logger.trace("Before sending the subscription request into the web socket");
		Mono<Void> input = //
				session//
						.send(Mono.//
								just(session.textMessage(request))//
								.doAfterTerminate(() -> logger
										.trace("The subscription request has been written on the Websocket")))//
						.thenMany(session.receive())//
						.doOnSubscribe(s -> onSubscribe(s))//
						.doOnNext(message -> onNext(message))//
						.doOnError(t -> onError(t))//
						.doOnComplete(() -> onComplete())//
						.then();
		logger.trace("After sending the subscription request into the web socket");

		logger.trace("End of handle(session) method execution");
		return input;
	}

	public WebSocketSession getSession() {
		return session;
	}

	/**
	 * The callback that will receive the messages from the web socket. It will map these JSON messages to the relevant
	 * java class, and call the application callback with this java objects
	 * 
	 * @param message
	 *            The received JSON message
	 */
	public void onNext(WebSocketMessage message) {
		String msg = message.getPayloadAsText();
		logger.trace("Message received from the Web Socket: {} (on session {})", msg, session);

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

	public void onError(Throwable t) {
		logger.trace("Error received for WebSocketSession {}: {}", session, t.getMessage());
		// Let's forward the information to the application callback
		subscriptionCallback.onError(t);
		// This stops the Flux, so we also raise the error into the Mono that monitor the end of this session.
		subscriptionMono.tryEmitError(t);
	}

	public void onComplete() {
		logger.trace("onComplete received for WebSocketSession {}: {}", session);
		// Let's forward the information to the application callback
		subscriptionCallback.onClose(0, "onComplete");
		// Let's close the Mono that monitor the end of this session.
		EmitResult result = subscriptionMono.tryEmitEmpty();

		try {
			result.orThrow();
		} catch (Exception e) {
			// The result is an Exception. We send it to the application callback
			RuntimeException e2 = new RuntimeException("Error while emitting to the subscription Mono", e);
			subscriptionCallback.onError(e2);
		}
	}

	public void onSubscribe(Subscription s) {
		// We've executed the subscription. Let's transmit this good news to the application callback
		subscriptionCallback.onConnect();
	}
}
