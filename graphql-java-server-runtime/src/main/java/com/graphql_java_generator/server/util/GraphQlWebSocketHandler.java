/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Some reminder, for internal tests
 * 
<PRE>
subscription subs {subscribeToNewPost(boardName:"Board name 1") {id title}}

query board {boards{id  name topics{id title posts(since: "2019-01-01") {id date author{id name} publiclyAvailable title content}}}}

mutation createPost {createPost(post: {topicId:"1", input: {title:"a title" ,content:"a content", date:"2021-10-09", authorId:"11"}}) { id title}}


</PRE>
 */
package com.graphql_java_generator.server.util;

import static java.util.Collections.singletonList;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.SubProtocolCapable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.ErrorType;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphqlErrorBuilder;
import graphql.execution.instrumentation.ChainedInstrumentation;
import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.tracing.TracingInstrumentation;
import graphql.schema.GraphQLSchema;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * WebSocketHandler for GraphQL based on
 * <a href= "https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md">GraphQL Over WebSocket Protocol</a> and
 * for use on a Servlet container with {@code spring-websocket}.
 *
 * @author Rossen Stoyanchev
 * @since 1.0.0
 */
public class GraphQlWebSocketHandler extends TextWebSocketHandler implements SubProtocolCapable {

	private static final Logger log = LoggerFactory.getLogger(GraphQlWebSocketHandler.class);

	private static final List<String> SUB_PROTOCOL_LIST = Arrays.asList("graphql-transport-ws",
			"subscriptions-transport-ws");

	private final Duration initTimeoutDuration = Duration.ofMillis((long) 30 * 1000); // 30s;

	/** This {@link Map} contains all active web sockets */
	private final Map<String, SessionState> sessionInfoMap = new ConcurrentHashMap<>();

	/**
	 * The Jackson {@link ObjectMapper} that will decode the incoming request, and encode the output
	 */
	ObjectMapper objectMapper;

	/** The {@link GraphQL} that will manage actually the request */
	GraphQL graphQL;

	/**
	 * Create a new instance.
	 * 
	 */
	public GraphQlWebSocketHandler(GraphQLSchema graphQLSchema) {
		objectMapper = new ObjectMapper();

		// In order to have subscriptions in graphql-java, we MUST use the
		// SubscriptionExecutionStrategy strategy.
		Instrumentation instrumentation = new ChainedInstrumentation(singletonList(new TracingInstrumentation()));
		graphQL = GraphQL.newGraphQL(graphQLSchema).instrumentation(instrumentation).build();
	}

	@Override
	public List<String> getSubProtocols() {
		return SUB_PROTOCOL_LIST;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		if (log.isTraceEnabled()) {
			log.trace("Executing 'afterConnectionEstablished' for session " + session.getId()
					+ ", with acceptedProtocol=" + session.getAcceptedProtocol());
		}
		if ("subscriptions-transport-ws".equalsIgnoreCase(session.getAcceptedProtocol())) {
			if (log.isTraceEnabled()) {
				log.trace("apollographql/subscriptions-transport-ws is not supported, nor maintained. "
						+ "Please, use https://github.com/enisdenjo/graphql-ws.");
			}
			GraphQlStatus.closeSession(session, GraphQlStatus.INVALID_MESSAGE_STATUS);
			return;
		}

		SessionState sessionState = new SessionState(session.getId());
		this.sessionInfoMap.put(session.getId(), sessionState);
		if (log.isTraceEnabled()) {
			log.trace("The session " + session.getId() + " has been registered");
		}

		Mono.delay(this.initTimeoutDuration).then(Mono.fromRunnable(() -> {
			if (sessionState.isConnectionInitNotProcessed()) {
				log.trace("Timeout ({}s) while waiting for the connection initialization",
						this.initTimeoutDuration.getSeconds());
				GraphQlStatus.closeSession(session, GraphQlStatus.INIT_TIMEOUT_STATUS);
			}
		})).subscribe();

	}

	@Override
	@SuppressWarnings("unchecked")
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		Map<String, Object> map = objectMapper.readValue(message.getPayload(), HashMap.class);
		String id = (String) map.get("id");
		String type = (String) map.get("type");
		MessageType messageType = MessageType.resolve(type);
		if (messageType == null) {
			GraphQlStatus.closeSession(session, GraphQlStatus.INVALID_MESSAGE_STATUS);
			return;
		}
		SessionState sessionState = getSessionInfo(session);
		switch (messageType) {
		case CONNECTION_INIT:
			log.trace("Received 'connection_init' for web socket {}", session);
			if (sessionState.setConnectionInitProcessed()) {
				GraphQlStatus.closeSession(session, GraphQlStatus.TOO_MANY_INIT_REQUESTS_STATUS);
				return;
			}
			TextMessage outputMessage = encode(null, MessageType.CONNECTION_ACK, null);
			synchronized (session) {
				session.sendMessage(outputMessage);
			}
			return;
		case START: // This message seems to be sent by graphiql instead of SUBSCRIBE :(
		case SUBSCRIBE:
			log.trace("Received 'subscribe' for operation id {} on web socket {} ({})", id, session, map);
			Map<String, Object> request = getPayload(map);
			if (sessionState.isConnectionInitNotProcessed()) {
				GraphQlStatus.closeSession(session, GraphQlStatus.UNAUTHORIZED_STATUS);
				return;
			}
			if (id == null) {
				GraphQlStatus.closeSession(session, GraphQlStatus.INVALID_MESSAGE_STATUS);
				return;
			}
			URI uri = session.getUri();
			Assert.notNull(uri, "Expected handshake url");
			HttpHeaders headers = session.getHandshakeHeaders();
			manageSubscribeMessage(uri, headers, request, id, session);
			return;
		case COMPLETE:
			manageCompleteMessage(session, id, sessionState);
			return;
		default:
			GraphQlStatus.closeSession(session, GraphQlStatus.INVALID_MESSAGE_STATUS);
		}

	}

	/**
	 * Actual Management of the Subscription, in a synchronized method
	 * 
	 * @param uri
	 *            The called URI
	 * @param headers
	 *            The HTTP headers
	 * @param payload
	 *            The payload map, that may contain these entries (according toe the <I>Subscribe</I> message of the
	 *            <I>graphql-transport-ws</I> protocol: operationName, query, variables and extensions. query is the
	 *            only mandatory value.
	 * 
	 * @param id
	 * @param session
	 * @throws IOException
	 */
	private synchronized void manageSubscribeMessage(URI uri, HttpHeaders headers, Map<String, Object> payload,
			String id, WebSocketSession session) throws IOException {
		String query = payload.get("query").toString();
		Object operationName = payload.get("operationName");
		@SuppressWarnings("unchecked")
		Map<String, Object> variables = (Map<String, Object>) payload.get("variables");
		@SuppressWarnings("unchecked")
		Map<String, Object> extensions = (Map<String, Object>) payload.get("extensions");

		ExecutionInput executionInput = ExecutionInput.newExecutionInput()//
				.query(query)//
				.variables((variables == null) ? new HashMap<>() : variables)//
				.operationName((operationName == null) ? null : operationName.toString())//
				.extensions((extensions == null) ? new HashMap<>() : extensions)//
				.build();

		ExecutionResult executionResult = graphQL.execute(executionInput);

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Case 1: the execution results in an error
		if (executionResult.getErrors() != null && executionResult.getErrors().size() > 0) {
			// If the subscription failed, we must return an error.
			try {
				Object errors = executionResult.toSpecification().get("errors");
				log.trace("Sending 'error' message for operation {}: {}", id, errors);
				synchronized (session) {
					session.sendMessage(encode(id, MessageType.ERROR, errors));
				}
			} catch (IOException e) {
				log.error("Could not send error message for subscription {} due to {}: {}", id,
						e.getClass().getSimpleName(), e.getMessage());
			}
		} else if (executionResult.getData() instanceof Publisher) {
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Case 2: the execution results in an reactive Publisher (the request is a Subscription).
			// Let's subscribe to it
			Publisher<ExecutionResult> publisher = executionResult.getData();

			publisher.subscribe(new Subscriber<ExecutionResult>() {

				private String uniqueOperationId = id;
				private Subscription subscription;

				@Override
				public synchronized void onSubscribe(Subscription s) {
					this.subscription = s;

					log.trace(
							"Executing onSubscribe for subscription of id {} in Web Socket Session {} (the reactive flux subscription is {})",
							id, session.getId(), s);

					Subscription prev = getSessionInfo(session).getSubscriptions().putIfAbsent(id, subscription);
					if (prev != null) {
						throw new SubscriptionExistsException();
					}

					subscription.request(1);
				}

				@Override
				public synchronized void onNext(ExecutionResult er) {
					try {
						TextMessage msg = encode(uniqueOperationId, MessageType.NEXT, er.toSpecification());
						log.trace("Sending new notification for subscription {}, on Web Socket Session {}: {}",
								uniqueOperationId, session.getId(), msg.getPayload());

						synchronized (session) {
							session.sendMessage(msg);
						}

						subscription.request(1);
					} catch (IOException e) {
						onError(e);
					}
				}

				@Override
				public synchronized void onError(Throwable t) {
					log.error("Received onError for Subscription id={}, on web socket {} (the error is {}: {}", id,
							session.getId(), t.getClass().getSimpleName(), t.getMessage());

					if (t instanceof SubscriptionExistsException) {
						CloseStatus status = new CloseStatus(4409, "Subscriber for " + id + " already exists");
						GraphQlStatus.closeSession(session, status);
					} else {
						ErrorType errorType = ErrorType.DataFetchingException;
						String message = t.getMessage();
						Map<String, Object> errorMap = GraphqlErrorBuilder.newError().errorType(errorType)
								.message(message).build().toSpecification();
						List<Map<String, Object>> errors = Arrays.asList(errorMap);

						try {
							session.sendMessage(encode(uniqueOperationId, MessageType.ERROR, errors));
						} catch (IOException e) {
							log.error("Could not send error message for subscription {} due to {}: {}", id,
									e.getClass().getSimpleName(), e.getMessage());
						}
					}
				}

				@Override
				public synchronized void onComplete() {
					log.debug("Received onComplete for Subscription id={} on web socket {}", id, session.getId());
					try {
						session.sendMessage(encode(uniqueOperationId, MessageType.COMPLETE, null));

						// Let's close this subscription
						Subscription sub = getSessionInfo(session).getSubscriptions().get(id);
						if (sub != null) {
							log.trace("Removing reactive flux subscription is {}, after onComplete", sub);
							getSessionInfo(session).getSubscriptions().remove(id);
							sub.cancel();
						}
					} catch (IOException e) {
						log.error("Unable to close websocket session", e);
					}
				}
			});
		} else if (executionResult.getData() instanceof Map) {
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Case 3: the execution result is a Map (the request is a query or a mutation)
			// Let's return its result with a unique "Next" message
			TextMessage msg = encode(id, MessageType.NEXT, executionResult.toSpecification());
			log.trace("Sending response for query or mutation {}, on Web Socket Session {}: {}", id, session.getId(),
					msg.getPayload());

			synchronized (session) {
				session.sendMessage(msg);
			}
		} else {
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Case 4: the execution result is of an unexpected data type
			// Let's return its result in an error message
			TextMessage msg = encode(id, MessageType.ERROR, executionResult.toSpecification());
			log.trace("Sending error for query or mutation {}, on Web Socket Session {}: {}", id, session.getId(),
					msg.getPayload());

			synchronized (session) {
				session.sendMessage(msg);
			}
		}
	}

	/**
	 * Manage of the Complete message, in a synchronized method
	 * 
	 * @param session
	 * @param id
	 * @param sessionState
	 */
	private synchronized void manageCompleteMessage(WebSocketSession session, String id, SessionState sessionState) {
		log.trace("Received 'complete' for operation id {} on web socket {}", id, session);
		if (id != null) {
			Subscription subscription = sessionState.getSubscriptions().remove(id);
			if (subscription != null) {
				log.trace(
						"Cancelling subscription for operation id {} on web socket {} (the reactive flux subscription is {})",
						id, session, subscription);
				subscription.cancel();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getPayload(Map<String, Object> message) {
		Object payload = message.get("payload");
		Assert.notNull(payload, "No payload in message: " + message);
		Assert.isTrue(payload instanceof Map,
				"The payload should be a Map, but is a " + payload.getClass().getName() + ", in message: " + message);

		return (Map<String, Object>) payload;
	}

	private SessionState getSessionInfo(WebSocketSession session) {
		SessionState info = this.sessionInfoMap.get(session.getId());
		Assert.notNull(info, "No SessionInfo for " + session);
		return info;
	}

	private <T> TextMessage encode(@Nullable String id, MessageType messageType, @Nullable Object payload) {
		Map<String, Object> payloadMap = new HashMap<>(3);
		payloadMap.put("type", messageType.getType());
		if (id != null) {
			payloadMap.put("id", id);
		}
		if (payload != null) {
			payloadMap.put("payload", payload);
		}
		try {
			// HttpOutputMessageAdapter outputMessage = new HttpOutputMessageAdapter();
			// ((HttpMessageConverter<T>) this.converter).write((T) payloadMap, null,
			// outputMessage);
			// return new TextMessage(outputMessage.toByteArray());
			return new TextMessage(objectMapper.writeValueAsString(payloadMap));
		} catch (IOException ex) {
			throw new IllegalStateException("Failed to write " + payloadMap + " as JSON", ex);
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {
		if (log.isTraceEnabled()) {
			log.trace("Executing 'handleTransportError' for session " + session.getId() + " of exception: "
					+ exception.getClass().getSimpleName() + ": " + exception.getMessage());
		}
		SessionState info = this.sessionInfoMap.remove(session.getId());
		if (info != null) {
			info.dispose();
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
		if (log.isTraceEnabled()) {
			log.trace("Executing 'afterConnectionClosed' for session " + session.getId() + ", with closeStatus="
					+ closeStatus);
		}
		SessionState info = this.sessionInfoMap.remove(session.getId());
		if (info != null) {
			info.dispose();
		}
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}

	private enum MessageType {

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

	private static class GraphQlStatus {

		private static final CloseStatus INVALID_MESSAGE_STATUS = new CloseStatus(4400, "Invalid message");

		private static final CloseStatus UNAUTHORIZED_STATUS = new CloseStatus(4401, "Unauthorized");

		private static final CloseStatus INIT_TIMEOUT_STATUS = new CloseStatus(4408,
				"Connection initialisation timeout");

		private static final CloseStatus TOO_MANY_INIT_REQUESTS_STATUS = new CloseStatus(4429,
				"Too many initialisation requests");

		static void closeSession(WebSocketSession session, CloseStatus status) {
			try {
				session.close(status);
			} catch (IOException ex) {
				if (log.isDebugEnabled()) {
					log.debug("Error while closing session with status: " + status, ex);
				}
			}
		}

	}

	private static class SessionState {

		private boolean connectionInitProcessed;
		private final Map<String, Subscription> subscriptions = new ConcurrentHashMap<>();
		private final Scheduler scheduler;

		SessionState(String sessionId) {
			this.scheduler = Schedulers.newSingle("GraphQL-WsSession-" + sessionId);
		}

		boolean isConnectionInitNotProcessed() {
			return !this.connectionInitProcessed;
		}

		synchronized boolean setConnectionInitProcessed() {
			boolean previousValue = this.connectionInitProcessed;
			this.connectionInitProcessed = true;
			return previousValue;
		}

		Map<String, Subscription> getSubscriptions() {
			return this.subscriptions;
		}

		void dispose() {
			for (Map.Entry<String, Subscription> entry : this.subscriptions.entrySet()) {
				try {
					entry.getValue().cancel();
				} catch (Throwable ex) {
					// Ignore and keep on
				}
			}
			this.subscriptions.clear();
			this.scheduler.dispose();
		}
	}

	@SuppressWarnings("serial")
	private static class SubscriptionExistsException extends RuntimeException {

	}
}
