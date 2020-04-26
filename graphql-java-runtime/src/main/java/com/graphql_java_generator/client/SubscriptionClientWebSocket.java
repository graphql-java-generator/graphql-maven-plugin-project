package com.graphql_java_generator.client;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphql_java_generator.client.request.AbstractGraphQLRequest;

@WebSocket
public class SubscriptionClientWebSocket<T> {

	/** Logger for this class */
	private static Logger logger = LoggerFactory.getLogger(SubscriptionClientWebSocket.class);

	/** The GraphQL full request that will be sent to the GraphQL server, through this Web Socket */
	final String graphQLRequest;

	/** The callback that will manage the onMessage, onClose and onError events */
	final SubscriptionCallback<T> subscriptionCallback;

	/**
	 * 
	 * @param graphQLRequest
	 *            The subscription GraphQL full request, to be sent to the server once the Web Socket is connected.
	 * @param subscriptionCallback
	 *            The callback, provided by the application when it executes the subscription.
	 * @see AbstractGraphQLRequest#AbstractGraphQLRequest(String, com.graphql_java_generator.annotation.RequestType,
	 *      String, com.graphql_java_generator.client.request.InputParameter...)
	 */
	SubscriptionClientWebSocket(String graphQLRequest, SubscriptionCallback<T> subscriptionCallback) {
		this.graphQLRequest = graphQLRequest;
		this.subscriptionCallback = subscriptionCallback;
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		System.out.printf("Got connect: %s%n", session);

		String request = "{\"query\":\"" //
				+ "subscription StockCodeSubscription {"//
				+ "    stockQuotes {" //
				+ "       dateTime" //
				+ "       stockCode" //
				+ "       stockPrice" //
				+ "       stockPriceChange" //
				+ "     }"//
				+ "}\",\"variables\":null,\"operationName\":null}";
		try {
			/*
			 * Future<Void> fut; fut =
			 */
			session.getRemote().sendStringByFuture(request);
			// fut.get(2, TimeUnit.SECONDS); // wait for send to complete.
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@OnWebSocketMessage
	public void onMessage(String msg) {
		System.out.printf("Got msg: %s%n", msg);
	}

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		logger.debug("Connection closed (status {}, reason {}) for request {}", statusCode, reason, graphQLRequest);

	}

	@OnWebSocketError
	public void onError(Throwable cause) {
		System.out.print("WebSocket Error: ");
		cause.printStackTrace(System.out);
	}
}
