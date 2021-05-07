package com.graphql_java_generator.client;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql_java_generator.client.request.AbstractGraphQLRequest;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.util.GraphqlUtils;

/**
 * This class implements the Web Socket, as needed by the jetty's Web Socket implementation.
 *
 * @param <R>
 *            The class that is generated from the subscription definition in the GraphQL schema. It contains one
 *            attribute, for each available subscription. The data tag of the GraphQL server response will be mapped
 *            into an instance of this class.
 * @param <T>
 *            The type that must be returned by the query or mutation: it's the class that maps to the GraphQL type
 *            returned by this subscription.
 * 
 * @author etienne-sf
 */
@WebSocket
public class SubscriptionClientWebSocket<R, T> {

	/** Logger for this class */
	private static Logger logger = LoggerFactory.getLogger(SubscriptionClientWebSocket.class);

	/** A singleton of the main runtime utility classes */
	GraphqlUtils graphqlUtils = GraphqlUtils.graphqlUtils;

	/** The GraphQL full request that will be sent to the GraphQL server, through this Web Socket */
	final String request;

	/** The name of the subscription, to provide some context in log and in exception messages */
	final String subscriptionName;

	/** The callback that will manage the onMessage, onClose and onError events */
	final SubscriptionCallback<T> subscriptionCallback;

	/** The jackson instance that will handle deserialization of the incoming messages */
	ObjectMapper objectMapper;

	/** The class that maps to the GraphQL subscription type, as defined in the GraphQL schema */
	Class<R> subscriptionType;
	/**
	 * The class that maps to the messages that will be sent back from the GraphQL server, once the subscription is
	 * subscribed. It's actually what's returned by this subscription, as defined in the GraphQL schema.
	 */
	Class<T> messageType;

	/**
	 * @param request
	 *            The subscription GraphQL full request, to be sent to the server once the Web Socket is connected.
	 * @param subscriptionCallback
	 *            The callback, provided by the application when it executes the subscription.
	 * @param subscriptionType
	 *            The R class: the class that is generated from the subscription definition in the GraphQL schema. It
	 *            contains one attribute, for each available subscription. The data tag of the GraphQL server response
	 *            will be mapped into an instance of this class.
	 * @param messageType
	 *            The T class: the type that must be returned by the query or mutation: it's the class that maps to the
	 *            GraphQL type returned by this subscription.
	 * @param objectMapper
	 *            the Jackson {@link ObjectMapper} to use
	 * @see AbstractGraphQLRequest#AbstractGraphQLRequest(String, com.graphql_java_generator.annotation.RequestType,
	 *      String, com.graphql_java_generator.client.request.InputParameter...)
	 */
	SubscriptionClientWebSocket(String request, String subscriptionName, SubscriptionCallback<T> subscriptionCallback,
			Class<R> subscriptionType, Class<T> messsageType, ObjectMapper objectMapper) {
		this.request = request;
		this.subscriptionName = subscriptionName;
		this.subscriptionCallback = subscriptionCallback;
		this.objectMapper = objectMapper;
		this.subscriptionType = subscriptionType;
		this.messageType = messsageType;
	}

	/**
	 * As soon as the Web Socket is connected, this method executes the subscription GraphQL request
	 * 
	 * @param session
	 */
	@OnWebSocketConnect
	public void onConnect(Session session) {
		logger.debug("Web Socket connected (session {}) for request {}", session, request);
		try {
			session.getRemote().sendStringByFuture(request);
		} catch (Throwable t) {
			subscriptionCallback.onError(new GraphQLRequestPreparationException(
					"Error while submitting the subscription request<" + request + ">", t));
		}
	}

	/**
	 * Each message is received as text, that is a standard GraphQL response. This method maps this GraphQL response to
	 * the POJOs that have been generated from the GraphQL Schema, and notify the application through the
	 * {@link SubscriptionCallback} that has been provided, when the subscription is executed.
	 * 
	 * @param msg
	 */
	@OnWebSocketMessage
	public void onMessage(String msg) {

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

	/**
	 * Notify the application, when the Web Socket is closed. This notification is done through the
	 * {@link SubscriptionCallback} that has been provided, when the subscription is executed.
	 * 
	 * @param msg
	 */
	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		logger.debug("Connection closed (status {}, reason {}) for request {}", statusCode, reason, request);
		subscriptionCallback.onClose(statusCode, reason);
	}

	/**
	 * Notify the application, when an error occurs. This notification is done through the {@link SubscriptionCallback}
	 * that has been provided, when the subscription is executed.
	 * 
	 * @param msg
	 */
	@OnWebSocketError
	public void onError(Throwable cause) {
		logger.error("WebSocket Error: {}", cause.getMessage());
		subscriptionCallback.onError(cause);
	}
}
