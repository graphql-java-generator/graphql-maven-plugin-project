/**
 * 
 */
package com.graphql_java_generator.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.graphql_java_generator.client.response.Error;
import com.graphql_java_generator.client.response.JsonResponseWrapper;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.util.GraphqlUtils;

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
public class GraphQLReactiveWebSocketHandler implements WebSocketHandler {

	/** Logger for this class */
	private static Logger logger = LoggerFactory.getLogger(GraphQLReactiveWebSocketHandler.class);

	private static final List<String> SUB_PROTOCOL_LIST = Arrays.asList("graphql-transport-ws");

	public enum MessageType {
		CONNECTION_INIT("connection_init"), CONNECTION_ACK("connection_ack"), SUBSCRIBE("subscribe"), NEXT(
				"next"), ERROR("error"), COMPLETE("complete"),
		// graphiql seems to send a START message, instead of a SUBSCRIBE one :(
		// Let's add it ot this list
		START("start");

		private static final Map<String, MessageType> messageTypes = new HashMap<>(6);

		static {
			for (MessageType messageType : MessageType.values()) {
				messageTypes.put(messageType.getType(), messageType);
			}
		}

		private final String type;

		MessageType(String type) {
			this.type = type;
		}

		public String getType() {
			return this.type;
		}

		@Nullable
		public static MessageType resolve(@Nullable String type) {
			return (type != null) ? messageTypes.get(type) : null;
		}
	}

	/**
	 * This class manages a subscription, wheras the {@link GraphQLReactiveWebSocketHandler} class manages the whole web
	 * socket. There may be more than one subscriptions subscribed on one web socket
	 * 
	 * @author etienne-sf
	 * @param <R>
	 * @param <T>
	 */
	class RequestData<R, T> {

		/**
		 * Holder for the Request Data. This class contains the field that identified request that are passed through
		 * the Web Socket, according to the
		 * <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md">graphql-transport-ws protocol</a>
		 * protocol.
		 * 
		 * @param request
		 *            The JSON map that contains the request to execute. According to the
		 *            <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md">graphql-transport-ws
		 *            protocol</a> protocol, it can be a query, a mutation or a subscription.
		 * @param subscriptionName
		 *            The name of the subscription: mandatory if this operation is a subscription. Must be null if it's
		 *            a query or a mutation.
		 * @param subscriptionCallback
		 *            The callback that will be called a message is received for this idOperation on this web socket
		 * @param requestType
		 *            The java class that matches the query type, mutation type or subscription that is defined in the
		 *            GraphQL schema
		 * @param messageType
		 *            For subscription (that is when <I>subscriptionName</I> is not null), it's the type of the
		 *            <i>subscriptionName</i> field of the requestType. When a message is read, a getter on this field
		 *            is executed to retrieve the message itself.<BR/>
		 *            For query and mutation (that is when <I>subscriptionName</I> is null), it must be the same class
		 *            as requestType. The whole received object in the payload will be returned to the caller for this
		 *            operation. This allows to execute a multifield query or mutation.
		 * @param uniqueIdOperation
		 *            The unique id that identifies messages dedicated to this operation, on the web socket
		 * @throws GraphQLRequestExecutionException
		 */
		RequestData(Map<String, Object> request, String subscriptionName, SubscriptionCallback<T> subscriptionCallback,
				Class<R> requestType, Class<T> messageType, int uniqueIdOperation)
				throws GraphQLRequestExecutionException {
			this.request = request;
			this.subscriptionName = subscriptionName;
			this.subscriptionCallback = subscriptionCallback;
			this.subscriptionType = requestType;
			this.messageType = messageType;
			this.uniqueIdOperation = Integer.toString(uniqueIdOperation);

			// If it's a query or a mutation, T and R must be equal
			if (subscriptionName == null && requestType != messageType) {
				throw new GraphQLRequestExecutionException(
						"[Internal error] When executing query or mutation, T and R should be equal. But R (requestType) is "
								+ requestType.getName() + " and T (messsageType) is " + messageType.getName());
			}
		}

		/**
		 * The id for this subscription on this web socket. It identifies a query and its responses in this web socket,
		 * as specified by the
		 * <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md">graphql-transport-ws protocol</a>.
		 */
		final String uniqueIdOperation;

		/** The request to send to the GraphQL server, to initiate the subscription */
		final Map<String, Object> request;

		/** The name of the subscription, to provide some context in log and in exception messages */
		final String subscriptionName;

		/** The callback that will receive the events sent by the web socket */
		final SubscriptionCallback<T> subscriptionCallback;

		/** The java generated from the GraphQL subscription type, as defined in the GraphQL schema */
		final Class<R> subscriptionType;
		/**
		 * The class that maps to the messages that will be sent back from the GraphQL server, once the subscription is
		 * subscribed. It's actually what's returned by this subscription, as defined in the GraphQL schema. In other
		 * words, it's the java type that matches the field in the subscription GraphQL type, for this subscribed
		 * subscription.
		 */
		final Class<T> messageType;

		/**
		 * True if this uniqueIdOperation has been completed. False if it is allowed to receive notification for this
		 * operation.
		 */
		boolean completed = false;

		/**
		 * The callback that will receive the messages from the web socket. It will map these JSON messages to the
		 * relevant java class, and call the application callback with this java objects
		 * 
		 * @param result
		 *            The received message, where all values have been in a map, as specified in the GraphQL
		 *            specification (see 'Serialization Format')
		 */
		public void onNext(Map<String, Object> result) {
			if (completed) {
				logger.trace(
						"Message received for a subscription of id {} from the Web Socket session {}, but the operation {} has already completed (the message was {})",
						uniqueIdOperation, session, uniqueIdOperation, result);
			} else {
				logger.trace("Message received for a subscription of id {}, from the Web Socket: {} (on session {})",
						uniqueIdOperation, result, session);

				// The generated POJOs have annotations that allow proper deserialization by the Jackson mapper,
				// including management of interfaces and unions. Let's reuse that .
				JsonResponseWrapper response = objectMapper.convertValue(result, JsonResponseWrapper.class);

				if (response.errors != null && response.errors.size() > 0) {
					List<Error> errors;
					String msg;

					if (response.errors == null) {
						errors = null;
						msg = "Unknown error received from the GraphQL server for subscription " + uniqueIdOperation;
						logger.error(msg);
					} else {
						errors = response.errors;
						StringBuilder sb = new StringBuilder();
						sb.append("An error has been received from the GraphQL server for subscription ");
						sb.append(uniqueIdOperation);
						msg = sb.toString();

						if (logger.isErrorEnabled()) {
							sb.append(": ");
							for (Error err : errors) {
								if (errors.size() > 0)
									sb.append(" | ");
								sb.append(err.message);
							}
							logger.error(sb.toString());
						}
					}
					subscriptionCallback.onError(new GraphQLRequestExecutionException(msg, errors));
				} else {
					R r = objectMapper.convertValue(response.data, subscriptionType);

					if (subscriptionName != null) {
						// It's a subscription: we need to get the payload content, from the subscription's name
						@SuppressWarnings("unchecked")
						T t = (T) graphqlUtils.invokeGetter(r, subscriptionName);
						subscriptionCallback.onMessage(t);
					} else {
						// It's a query or a mutation: we send the whole payload.
						@SuppressWarnings("unchecked")
						T t = (T) r;
						subscriptionCallback.onMessage(t);
					}
				}
			}
		}

		public void onError(Throwable t) {
			if (completed) {
				logger.trace(
						"Error received from the Web Socket session {}, but the operation {} has already completed",
						session, uniqueIdOperation);
			} else {
				logger.trace("Error received for WebSocketSession {}: {}", session, t.getMessage());
				// Let's forward the information to the application callback
				subscriptionCallback.onError(t);
			}
		}

		public void onClose(int statusCode, String reason) {
			if (completed) {
				logger.trace(
						"'Close' received from the Web Socket session {}, but the operation {} has already completed (status={}, reason={})",
						session, uniqueIdOperation, reason, session);
			} else {

				logger.trace("onClose(code={}, reason={}) received for WebSocketSession {}: {}", statusCode, reason,
						session);
				// Let's forward the information to the application callback
				subscriptionCallback.onClose(statusCode, reason);
			}
		}

		public void onComplete() {
			if (completed) {
				logger.trace(
						"Complete received from the Web Socket session {}, but the operation {} has already completed",
						session, uniqueIdOperation);
			} else {
				logger.trace("onComplete received for id {} on WebSocketSession {}", uniqueIdOperation, session);
				// Let's forward the information to the application callback
				subscriptionCallback.onClose(0, "Complete");
			}
		}

		public void onSubscriptionExecuted() {
			if (completed) {
				logger.trace(
						"Subscribe received from the Web Socket session {}, but the operation {} has already completed",
						session, uniqueIdOperation);
			} else {
				// We've executed the subscription. Let's transmit this good news to the application callback
				subscriptionCallback.onConnect();
			}
		}

		public boolean isCompleted() {
			return this.completed;
		}

		public void setCompleted(boolean completed) {
			this.completed = completed;
		}
	}

	private static class QueryOrMutationCallback<R> implements SubscriptionCallback<R> {
		CountDownLatch latchResponseOrExceptionReceived = new CountDownLatch(1);
		R response = null;
		List<Throwable> exceptions = new ArrayList<>();

		@Override
		public void onConnect() {
			// no action
		}

		@Override
		public void onMessage(R r) {
			response = r;
			latchResponseOrExceptionReceived.countDown();
		}

		@Override
		public void onClose(int statusCode, String reason) {
			if (response == null) {
				exceptions
						.add(new GraphQLRequestExecutionException("Received onClose while expecting a message (status="
								+ statusCode + ", reason=" + reason + ")"));
			}
			latchResponseOrExceptionReceived.countDown();
		}

		@Override
		public void onError(Throwable cause) {
			exceptions.add(cause);
			latchResponseOrExceptionReceived.countDown();
		}
	}

	private static class GraphQlStatus {
		private static final CloseStatus INVALID_MESSAGE_STATUS = new CloseStatus(4400, "Invalid message");
		@SuppressWarnings("unused")
		private static final CloseStatus UNAUTHORIZED_STATUS = new CloseStatus(4401, "Unauthorized");
		@SuppressWarnings("unused")
		private static final CloseStatus INIT_TIMEOUT_STATUS = new CloseStatus(4408,
				"Connection initialisation timeout");
		@SuppressWarnings("unused")
		private static final CloseStatus TOO_MANY_INIT_REQUESTS_STATUS = new CloseStatus(4429,
				"Too many initialisation requests");

		/**
		 * This method close the current web socket session, including all active subscriptions
		 * 
		 * @param handler
		 * @param session
		 * @param status
		 * @param reason
		 */
		static void closeSession(GraphQLReactiveWebSocketHandler handler, WebSocketSession session, CloseStatus status,
				String reason) {
			for (RequestData<?, ?> subData : handler.registeredSubscriptions.values()) {
				subData.onClose(status.getCode(), (reason == null) ? status.getReason() : reason);
			}
			session.close(status);
		}
	}

	static interface SubscriptionRequestEmitter {
		void emit(RequestData<?, ?> subData, WebSocketMessage msg);
	}

	/**
	 * Each operation on this web socket will be identified by a uniqueIdOperation, that identified a query and its
	 * responses in this web socket, as specified by the
	 * <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md">graphql-transport-ws protocol</a>.
	 */
	private int lastUsedUniqueIdOperation = 0;

	/** The jackson instance that will handle deserialization of the incoming messages */
	final GraphQLObjectMapper objectMapper;

	/** A singleton of the main runtime utility classes */
	GraphqlUtils graphqlUtils = GraphqlUtils.graphqlUtils;

	/** The session, that will receive upon connection of the web socket. */
	WebSocketSession session = null;

	/**
	 * The {@link SubscriptionRequestEmitter} is the instance that can write messages into the output flux of the web
	 * socket, toward the server
	 */
	SubscriptionRequestEmitter webSocketEmitter = null;

	/** Used to wait before executing the subscription, until the ConnectionInit has been sent through the Web Socket */
	CountDownLatch webSocketConnectionInitializationLatch = new CountDownLatch(1);

	/**
	 * Contains the error that occurs during the web socket initialization process (when sending the
	 * <I>connection_init</I> message). It can be read as soon as the webSocketConnectionInitializationLatch count is
	 * down to zero (see {@link CountDownLatch#await()} )
	 */
	Throwable initializationError = null;

	/**
	 * Contains the list of subscriptions that have been executed on this Web Socket. This allows to dispatch the
	 * incoming subscription notifications toward the relevant {@link SubscriptionCallback}
	 */
	Map<String, RequestData<?, ?>> registeredSubscriptions = new ConcurrentHashMap<>();

	public GraphQLReactiveWebSocketHandler(GraphQLObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/**
	 * Execution of a subscription, from the given request.
	 * 
	 * @param <R>
	 * @param <T>
	 * @param request
	 * @param subscriptionName
	 * @param subscriptionCallback
	 * @param subscriptionType
	 * @param messsageType
	 * @return The unique Id Operation that is returned by the server for this subscription, as specified by the
	 *         <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md">graphql-transport-ws
	 *         protocol</a>
	 * @throws GraphQLRequestExecutionException
	 */
	public <R, T> String executeSubscription(Map<String, Object> request, String subscriptionName,
			SubscriptionCallback<T> subscriptionCallback, Class<R> subscriptionType, Class<T> messsageType)
			throws GraphQLRequestExecutionException {
		RequestData<R, T> subData;

		// Let's wait until the web socket is properly initialized, as specified by the graphql-transport-ws protocol
		checkInitializationError();

		synchronized (registeredSubscriptions) {
			subData = new RequestData<R, T>(request, subscriptionName, subscriptionCallback, subscriptionType,
					messsageType, ++lastUsedUniqueIdOperation);
			registeredSubscriptions.put(subData.uniqueIdOperation, subData);
		}

		// Let's do the subscription into the websocket, toward the GraphQL server
		logger.trace("Emitting execution of the subscription id={} on the web socket {} (request={})",
				subData.uniqueIdOperation, session, request);

		webSocketEmitter.emit(subData,
				session.textMessage(encode(subData.uniqueIdOperation, MessageType.SUBSCRIBE, subData.request)));

		return subData.uniqueIdOperation;
	}

	/**
	 * This method executes a query or a mutation over this web socket, as described in the
	 * <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md">graphql-transport-ws</a> protocol.
	 * 
	 * @param <R>
	 * @param request
	 *            The json map that contains the request to execute
	 * @param requestType
	 *            The java type that matches the query or mutation type defined in the GraphQL schema.
	 * @return
	 * @throws GraphQLRequestExecutionException
	 */
	public <R extends GraphQLRequestObject> R executeQueryOrMutation(Map<String, Object> request, Class<R> requestType)
			throws GraphQLRequestExecutionException {
		RequestData<R, ?> subData;
		QueryOrMutationCallback<R> callback = new QueryOrMutationCallback<R>();

		// Let's wait until the web socket is properly initialized, as specified by the graphql-transport-ws protocol
		checkInitializationError();

		synchronized (registeredSubscriptions) {
			subData = new RequestData<R, R>(request, null, callback, requestType, requestType,
					++lastUsedUniqueIdOperation);
			registeredSubscriptions.put(subData.uniqueIdOperation, subData);
		}

		// Let's execute the request into the websocket, toward the GraphQL server
		logger.trace("Emitting execution of the subscription id={} on the web socket {} (request={})",
				subData.uniqueIdOperation, session, request);
		webSocketEmitter.emit(subData,
				session.textMessage(encode(subData.uniqueIdOperation, MessageType.SUBSCRIBE, subData.request)));

		// Then we wait until an answer comes
		int nbSecondsTimeOut = 30;
		try {
			callback.latchResponseOrExceptionReceived.await(nbSecondsTimeOut, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new GraphQLRequestExecutionException("Got interrupted while waiting for request response", e);
		}

		if (callback.exceptions.size() > 0) {
			throw new GraphQLRequestExecutionException(
					"An error occurred while processing the request: " + callback.exceptions.get(0).getMessage(),
					callback.exceptions.get(0));
		} else if (callback.response != null) {
			return callback.response;
		} else {
			// Oups, we received no response. It's a time out error
			throw new GraphQLRequestExecutionException("Received no answer after " + nbSecondsTimeOut + " seconds");
		}
	}

	public void unsubscribe(String uniqueIdOperation) throws GraphQLRequestExecutionException {
		logger.trace("Emitting 'complete' message to close the subscription for the uniqueIdOperation={} on socket {}",
				uniqueIdOperation, session);

		// Let's find the subscription that manages this uniqueIdOperation
		RequestData<?, ?> subData = registeredSubscriptions.get(uniqueIdOperation);

		if (subData == null) {
			// Oups! It is unknown
			throw new GraphQLRequestExecutionException("Unknown uniqueIdOperation " + uniqueIdOperation
					+ " for web socket session " + session + " when trying to unsubscribe");
		} else {
			subData.setCompleted(true);
			webSocketEmitter.emit(subData,
					session.textMessage(encode(subData.uniqueIdOperation, MessageType.COMPLETE, null)));
		}

	}

	/**
	 * Returns the error that occurs during the initialization phase, that is while sending the <I>connection_init</I>
	 *
	 * @return The error that occurs, or null if no error occurred while sending the <I>connection_init</I>
	 * @throws GraphQLRequestExecutionException
	 *             When an error occurs before the Web Socket and the subscription are properly initialized.
	 */
	public void checkInitializationError() throws GraphQLRequestExecutionException {
		int nbSecondsTimeOut = 30;
		// The web socket initialization phase must be finished. And we'll wait at most nbSecondsTimeOut for that.
		try {
			webSocketConnectionInitializationLatch.await(nbSecondsTimeOut, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new GraphQLRequestExecutionException(
					"The thread got interrupted while waiting for web socket initialization");
		}

		if (webSocketConnectionInitializationLatch.getCount() > 0) {
			throw new GraphQLRequestExecutionException("The session on Web Socket " + session
					+ " has not been initialized after " + nbSecondsTimeOut + " seconds");
		} else if (initializationError != null) {
			throw new GraphQLRequestExecutionException(
					"Error during Web Socket or Subscription initialization: "
							+ initializationError.getClass().getSimpleName() + "-" + initializationError.getMessage(),
					initializationError);
		}

		// If we arrive here, we're happy. There has been no error during initialization
	}

	/**
	 * This method may be called by the
	 * {@link RequestExecutionSpringReactiveImpl#execute(com.graphql_java_generator.client.request.AbstractGraphQLRequest, Map, SubscriptionCallback, Class, Class)}
	 * method
	 * 
	 * @param initializationError
	 */
	void setInitializationError(Throwable initializationError) {
		this.initializationError = initializationError;
		webSocketConnectionInitializationLatch.countDown();
	}

	@Override
	public Mono<Void> handle(WebSocketSession sessionParam) {
		this.session = sessionParam;
		logger.trace("new web socket session received: {}", session);

		Mono<Void> input = session.receive()//
				.doOnNext(message -> onNext(message))//
				.doOnError(t -> onError(t))//
				.doOnComplete(() -> onComplete())//
				.then();

		@SuppressWarnings("unchecked")
		Mono<Void> output = session//
				.send((Flux<WebSocketMessage>) (Flux<?>) Flux//
						.push(sink -> {
							// The first action must be to send the connection_init message to the server
							sink.next(session.textMessage(encode(null, MessageType.CONNECTION_INIT, null)));
							logger.trace("The 'connection_init' message has been written on the web socket {}",
									session);

							// Then, we attach the publisher that will allow to send the incoming subscriptions into
							// this flux
							webSocketEmitter = new SubscriptionRequestEmitter() {
								@Override
								public synchronized void emit(RequestData<?, ?> subData, WebSocketMessage msg) {
									if (logger.isTraceEnabled())
										logger.trace("Emitting message for uniqueIdOperation {} on web socket {}: {}",
												subData.uniqueIdOperation, session, msg.getPayloadAsText());
									sink.next(msg);
									if (!subData.isCompleted())
										subData.onSubscriptionExecuted();
								}
							};
						})//
						.doOnError(t -> initializationError = t)//
				// .doOnNext((msg) -> {
				// //
				// })//
				// .doAfterTerminate(() -> {
				// })
				);

		logger.trace("End of handle(session {}) method execution", session);
		return Mono.zip(input, output).then();
	}

	/**
	 * The callback that will receive the messages from the web socket. It will map these JSON messages to the relevant
	 * java class, and call the application callback with this java objects. This message can be any valid message,
	 * according to the <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md">graphql-transport-ws
	 * protocol</a>
	 * 
	 * @param message
	 *            The received JSON message
	 */
	@SuppressWarnings("unchecked")
	public void onNext(WebSocketMessage message) {

		JsonNode jsonNode;
		Map<String, Object> map;
		try {
			jsonNode = objectMapper.readTree(message.getPayloadAsText());
			map = objectMapper.treeToValue(jsonNode, HashMap.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Error while reading '" + message.getPayloadAsText() + "' as a Map", e);
		}

		String id = (String) map.get("id");
		RequestData<?, ?> subData = null;
		if (id != null) {
			// Let's find the subscription that manages this uniqueIdOperation
			subData = registeredSubscriptions.get(id);
		}
		String type = (String) map.get("type");
		MessageType messageType = MessageType.resolve(type);
		if (messageType == null) {
			// Invalid message. We close the whole session, as described in the protocol
			GraphQlStatus.closeSession(this, session, GraphQlStatus.INVALID_MESSAGE_STATUS,
					"Invalid message: " + message.getPayloadAsText());
			return;
		}

		switch (messageType) {
		case CONNECTION_ACK:
			logger.trace("Received 'connection_ack' on web socket {}", session);
			// Ok, the connectionInit message has been sent. It's now allowed to send GraphQL
			// subscription on this web socket (see #executeSubscription() in this class)
			webSocketConnectionInitializationLatch.countDown();
			break;
		case NEXT:
			if (logger.isTraceEnabled())
				logger.trace("Received 'next' for id {} on web socket {} (payload={})", id, session,
						message.getPayloadAsText());
			if (id == null) {
				// Invalid message. We close the whole session, as described in the protocol
				GraphQlStatus.closeSession(this, session, GraphQlStatus.INVALID_MESSAGE_STATUS,
						"Invalid message (id is null): " + message.getPayloadAsText());
				return;
			}

			if (subData == null) {
				// Oups! The server sent a message with a uniqueIdOperation that is unknown by the client
				// There is nothing in the protocol for this case...
				// We ignore this message, and mark this unknown uniqueIdOperation as complete so that we receive no
				// other message for this uniqueIdOperation
				logger.warn(
						"[graphql-transport-ws] Unknown uniqueIdOperation {} for web socket session {} (a 'complete' message is sent to the server to that he stops managing this uniqueIdOperation)",
						id, session);
				webSocketEmitter.emit(subData, session.textMessage(encode(id, MessageType.COMPLETE, null)));
			} else if (subData.isCompleted()) {
				logger.warn("Receive a message for a closed uniqueIdOperation ({}) on web socket {}", id, session);
			} else if (map.get("payload") == null) {
				String msg = "payload is mandatory for 'next' messages";
				logger.error(msg);
				subData.onError(new GraphQLRequestExecutionException(msg));
			} else if (!(map.get("payload") instanceof Map)) {
				String msg = "payload should be a Map, but <" + map.get("payload") + "> is not a Map";
				logger.error(msg);
				subData.onError(new GraphQLRequestExecutionException(msg));
			} else {
				subData.onNext((Map<String, Object>) map.get("payload"));
			}
			break;
		case COMPLETE:
			logger.trace("Received 'complete' for id {} on web socket {} (payload={})", id, session, message);
			subData.onComplete();
			break;
		case ERROR:
			logger.warn("Received 'error' for id {} on web socket {} (payload={})", id, session,
					message.getPayloadAsText());
			// The payload is a list of GraphQLErrors
			if (map.get("payload") instanceof Map) {
				// The payload is one error
				String msg = (String) ((Map<?, ?>) map.get("payload")).get("message");
				subData.onError(new GraphQLRequestExecutionException(msg));
			} else {
				// The payload is a list of errors.
				try {
					List<Error> errors = new ArrayList<>();
					for (JsonNode node : jsonNode.get("payload")) {
						errors.add(objectMapper.treeToValue(node, Error.class));
					}
					subData.onError(new GraphQLRequestExecutionException("Error on subscription " + id, errors));
				} catch (JsonProcessingException e) {
					throw new RuntimeException("Error while reading the errors from '" + message.getPayloadAsText(), e);
				}
			}
			break;
		default:
			logger.warn("Received non managed message '{}' for id {} on web socket {} (payload={})", type, id, session,
					message);
			// Oups! This message type exists in MessageType, but is not properly managed here.
			// This is an internal error.
			String msg = "Non managed message type '" + type + "'";
			if (subData != null) {
				subData.onError(new GraphQLRequestExecutionException(msg));
			} else {
				logger.error(msg);
			}
		}
	}

	public void onError(Throwable t) {
		if (t == null) {
			t = new RuntimeException("Unknown exception");
			logger.error("The Web Socket session {} ended with an unknown error", session);
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("The Web Socket session ").append(session).append(" ended with an error (")
					.append(t.getClass().getSimpleName()).append(t.getMessage()).append(")");
			for (StackTraceElement row : t.getStackTrace()) {
				sb.append("\n    ").append(row);
			}
			logger.error(sb.toString());
		}

		// We must free every resources for this websocket
		for (RequestData<?, ?> subData : registeredSubscriptions.values()) {
			subData.onError(t);
		}

		if (session != null) {
			session.close(CloseStatus.SERVER_ERROR);
			// This session should not be reused
			session = null;
		}
	}

	public void onComplete() {
		logger.trace("onComplete received for WebSocketSession {}", session);

		// Let's forward the information to each subscription
		for (RequestData<?, ?> subData : registeredSubscriptions.values()) {
			subData.onComplete();
		}
	}

	@Override
	public List<String> getSubProtocols() {
		return SUB_PROTOCOL_LIST;
	}

	public WebSocketSession getSession() {
		return session;
	}

	/**
	 * Encodes a message, according to the
	 * <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md">graphql-transport-ws protocol</a>
	 */
	String encode(@Nullable String id, MessageType messageType, @Nullable Object payload) {
		Map<String, Object> payloadMap = new HashMap<>(3);
		payloadMap.put("type", messageType.getType());
		if (id != null) {
			payloadMap.put("id", id);
		}
		if (payload != null) {
			payloadMap.put("payload", payload);
		}
		try {
			return objectMapper.writeValueAsString(payloadMap);
		} catch (IOException ex) {
			throw new RuntimeException("Failed to write " + payloadMap + " as JSON", ex);
		}
	}
}
