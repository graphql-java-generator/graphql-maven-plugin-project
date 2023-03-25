/**
 * 
 */
package com.graphql_java_generator.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.request.AbstractGraphQLRequest;
import com.graphql_java_generator.client.response.Error;
import com.graphql_java_generator.client.response.JsonResponseWrapper;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.util.GraphqlUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;

/**
 * This class implements the Web Socket, as needed by the Spring Web Socket implementation. It applies the
 * <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md">graphql-transport-ws</a>) protocol for all
 * requests (queries, mutations and subscriptions). That is: all requests are executed within the same web socket.
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

	private final static int NB_SECONDS_TIME_OUT_FOR_INITIALIZATION = 10;
	private final static int NB_SECONDS_TIME_OUT_FOR_REQUESTS = 30;
	private final static ObjectMapper defaultObjectMapper = new ObjectMapper();

	/**
	 * Each operation on this web socket will be identified by a uniqueIdOperation, that identified a query and its
	 * responses in this web socket, as specified by the
	 * <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md">graphql-transport-ws protocol</a>.
	 */
	private static long nextUniqueIdOperation = 1;

	/**
	 * A <I>graphqlEndpoint</I> Spring bean, of type String, must be provided, with the URL of the GraphQL endpoint, for
	 * instance <I>https://my.serveur.com/graphql</I>
	 */
	String graphqlEndpoint;

	/**
	 * If the subscription is on a different endpoint than the main GraphQL endpoint, thant you can define a
	 * <I>graphqlSubscriptionEndpoint</I> Spring bean, of type String, with this specific URL, for instance
	 * <I>https://my.serveur.com/graphql/subscription</I>. <BR/>
	 * For instance, Java servers suffer from a limitation which prevent to server both GET/POST HTTP verbs and
	 * WebSockets on the same URL. This limitation is now under control, for instance in the server version of this
	 * plugin.<BR/>
	 * If no bean <I>graphqlSubscriptionEndpoint</I> Spring bean is defined, then the <I>graphqlEndpoint</I> URL is also
	 * used for subscriptions (which is the standard case).
	 */
	@Deprecated
	String graphqlSubscriptionEndpoint;

	/**
	 * The Spring reactive {@link WebSocketClient} web socket client, that will execute HTTP requests to build the web
	 * sockets, for GraphQL subscriptions.<BR/>
	 * This is mandatory if the application latter calls subscription. It may be null otherwise.
	 */
	WebSocketClient webSocketClient;

	/**
	 * The optional {@link ServerOAuth2AuthorizedClientExchangeFilterFunction} that manages the OAuth Authorization
	 * header, on client side
	 */
	final ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunction;

	/**
	 * The optional {@link OAuthTokenExtractor} that extracts the OAuth Authorization header, once the
	 * {@link ServerOAuth2AuthorizedClientExchangeFilterFunction} has gotten it
	 */
	final OAuthTokenExtractor oAuthTokenExtractor;

	/**
	 * This map contains all the web socket session handlers, that manage the communication with the server(s) through
	 * the private final Map<String, String> sessionHandlers = new ConcurrentHashMap<>();
	 */
	private final Map<String, WebSocketSessionHandler> sessionHandlers = new ConcurrentHashMap<>();

	/**
	 * Used to identify when the web socket is initialized, and read to execute the first subscription. The count is
	 * down to 0 as soon as one of this conditions occurs:<br/>
	 * - The handle method is called, and the {@link WebSocketSessionHandler} instance has been created<br/>
	 * - An error occurs during the web socket connection process. In this case, {@link #webSocketConnectionError}
	 * contains the error that occurred
	 */
	CountDownLatch webSocketConnectionLatch = null;

	/** The exception that occurs during the last web socket connection attempt, if any */
	Throwable webSocketConnectionError = null;

	public GraphQLReactiveWebSocketHandler(//
			String graphqlEndpoint, //
			String graphqlSubscriptionEndpoint, //
			WebSocketClient webSocketClient, //
			ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunction,
			OAuthTokenExtractor oAuthTokenExtractor) {
		this.graphqlEndpoint = graphqlEndpoint;
		this.graphqlSubscriptionEndpoint = graphqlSubscriptionEndpoint;
		this.webSocketClient = webSocketClient;
		this.serverOAuth2AuthorizedClientExchangeFilterFunction = serverOAuth2AuthorizedClientExchangeFilterFunction;
		this.oAuthTokenExtractor = oAuthTokenExtractor;

		// Let's start a new web socket connection asap
		new Thread(() -> {
			try {
				this.getActiveWebSocketSession();
			} catch (GraphQLRequestExecutionException e) {
				// If an exception occurs here, it will be stored in the webSocketConnectionError attribute
				e.printStackTrace();
			}
		}).start();
	}

	/**
	 * Managing of a new Web Socket session. This method is the only method of this class. All the work is delegated.
	 */
	@Override
	public Mono<Void> handle(WebSocketSession session) {
		logger.trace("new web socket session received: {}", session.getId());
		if (sessionHandlers.get(session.getId()) != null) {
			throw new RuntimeException(
					"[Internal Error] Trying to handle an already known Web Socket session: " + session.getId());
		}
		// Ok, we're good. Let's create the session handler, that will manage this session.
		WebSocketSessionHandler sessionHandler = new WebSocketSessionHandler(session);
		sessionHandlers.put(session.getId(), sessionHandler);
		Mono<Void> ret = sessionHandler.handleWebSocketSession();

		// The web socket connection is done. Let's indicate it:
		webSocketConnectionLatch.countDown();

		return ret;
	}

	/**
	 * Execution of a subscription, from the given request and parameters, onto this Web Socket.
	 * 
	 * @param <R>
	 * @param <T>
	 * @param request
	 * @param parameters
	 *            The parameter's map, that contain the parameters name and value
	 * @param subscriptionCallback
	 * @param subscriptionType
	 * @param messsageType
	 * @return The unique Id Operation that is returned by the server for this subscription, as specified by the
	 *         <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md">graphql-transport-ws
	 *         protocol</a>
	 * @throws GraphQLRequestExecutionException
	 */
	public <R, T> SubscriptionClient executeSubscription(AbstractGraphQLRequest request, Map<String, Object> parameters,
			SubscriptionCallback<T> subscriptionCallback, Class<R> subscriptionType, Class<T> messsageType)
			throws GraphQLRequestExecutionException {
		return getActiveWebSocketSession().executeSubscription(request, parameters, subscriptionCallback,
				subscriptionType, messsageType);
	}

	/**
	 * Execution of a query or a mutation, from the given request and parameters, onto this Web Socket.
	 * 
	 * @param <R>
	 * @param graphQLRequest
	 * @param parameters
	 * @param dataResponseType
	 * @return
	 * @throws GraphQLRequestExecutionException
	 */
	public <R extends GraphQLRequestObject> R executeQueryOrMutation(AbstractGraphQLRequest graphQLRequest,
			Map<String, Object> parameters, Class<R> dataResponseType) throws GraphQLRequestExecutionException {
		return getActiveWebSocketSession().executeQueryOrMutation(graphQLRequest, parameters, dataResponseType);
	}

	/**
	 * Retrieve a Web Socket Session that is valid, and create one if no Web Socket Session is ready for query
	 * execution. <br/>
	 * If the last Web Socket session created had an error, it removed this session from the managed session list and
	 * throw this exception. This exception will be thrown only once, and the caller can retry to execute its query.
	 * 
	 * @return
	 * @throws GraphQLRequestExecutionException
	 */
	private synchronized WebSocketSessionHandler getActiveWebSocketSession() throws GraphQLRequestExecutionException {

		WebSocketSessionHandler activeSession = findActiveWebSocketSession();
		if (activeSession != null) {
			// Ok, we've found a session that is ready.
			return activeSession;
		}

		// No session is ready for GraphQL query execution. Let's create one
		logger.debug("getActiveWebSocketSession(): Starting a new connection on {}", getWebSocketURI());
		// Let's try a new Web Socket connection
		webSocketConnectionLatch = new CountDownLatch(1);
		webSocketConnectionError = null;

		// Is there an OAuth authentication to handle?
		HttpHeaders headers = new HttpHeaders();
		if (serverOAuth2AuthorizedClientExchangeFilterFunction != null && oAuthTokenExtractor != null) {
			String authorizationHeaderValue = oAuthTokenExtractor.getAuthorizationHeaderValue();
			logger.debug("Got this OAuth token (authorization header value): {}", authorizationHeaderValue);
			headers.add(OAuthTokenExtractor.AUTHORIZATION_HEADER_NAME, authorizationHeaderValue);
		} else {
			logger.debug(
					"No serverOAuth2AuthorizedClientExchangeFilterFunction or no oAuthTokenExtractor where provided. No OAuth token is provided.");
		}

		if (logger.isTraceEnabled()) {
			// Let's log the sent headers
			StringBuilder sb = new StringBuilder();
			sb.append("The Subscription GET request will be sent with these headers:\n");
			if (headers.entrySet().size() == 0) {
				sb.append("    ").append("<No headers!>");
			} else {
				for (Entry<String, List<String>> header : headers.entrySet()) {
					sb.append("    ").append(header.getKey());
					boolean first = false;
					for (String value : header.getValue()) {
						if (!first)
							sb.append(",");
						sb.append(value);
						if (!first)
							sb.append("\n");
						first = false;
					}
				}
			}
			logger.trace(sb.toString());
		}

		// Let's execute the connection
		webSocketClient//
				.execute(getWebSocketURI(), headers, this)//
				.subscribeOn(Schedulers.parallel())//
				.doOnError((t) -> {
					logger.error("Receive an error during web socket connection: {}", t.getMessage());
					webSocketConnectionError = t;
					webSocketConnectionLatch.countDown();
				})
				// The execution must occur in another thread, while we wait in this one that the connection is
				// ready.
				.subscribe();

		////////////////////////////
		// We must block here until either:
		// - The handle method has been called, which means the web socket connection is done (but the session
		// initialization is not finished)
		// - An error has been thrown during the connection process (see here above, the doOnError treatment)
		try {
			if (!webSocketConnectionLatch.await(NB_SECONDS_TIME_OUT_FOR_INITIALIZATION, TimeUnit.SECONDS)) {
				if (webSocketConnectionError == null) {
					// Let' store the fact that we couldn't initialize this web socket
					webSocketConnectionError = new GraphQLRequestExecutionException("The web socket connection to "
							+ (graphqlSubscriptionEndpoint == null ? graphqlEndpoint : graphqlSubscriptionEndpoint)
							+ " has not been initialized after " + NB_SECONDS_TIME_OUT_FOR_INITIALIZATION + " seconds");
				}
			}
		} catch (InterruptedException e) {
			throw new GraphQLRequestExecutionException(
					"The thread got interrupted while waiting for web socket connection to "
							+ (graphqlSubscriptionEndpoint == null ? graphqlEndpoint : graphqlSubscriptionEndpoint));
		}

		if (webSocketConnectionError != null)
			if (webSocketConnectionError instanceof GraphQLRequestExecutionException)
				throw (GraphQLRequestExecutionException) webSocketConnectionError;
			else
				throw new GraphQLRequestExecutionException("Error during Web Socket connection to "
						+ (graphqlSubscriptionEndpoint == null ? graphqlEndpoint : graphqlSubscriptionEndpoint) + ": "
						+ webSocketConnectionError.getClass().getSimpleName() + "-"
						+ webSocketConnectionError.getMessage(), webSocketConnectionError);

		// If the web socket connection is ok, a new active session must have been registered. The below method will
		// wait until the graphQL handshake is finished on the newly connected web socket session
		activeSession = findActiveWebSocketSession();

		if (activeSession != null)
			return activeSession;
		else
			throw new GraphQLRequestExecutionException("Unable to create a Web Socket Session to "
					+ (graphqlSubscriptionEndpoint == null ? graphqlEndpoint : graphqlSubscriptionEndpoint));
	}

	/**
	 * Returns the first Web Socket session that is ready for GraphQL execution. Or null if there is no such
	 * session.<br/>
	 * This method searches for an active web soket session. If none is found, it tries to create a new web socket
	 * session, and to return it.
	 * 
	 * @return a Web Socket session if one was active, or if it succeed to create a new one.<br/>
	 *         Null if there was non active web session, and it coudn't create a new one.
	 * 
	 */
	private WebSocketSessionHandler findActiveWebSocketSession() throws GraphQLRequestExecutionException {
		// We'll try each available session handlers. If one is invalid, we remove it.
		while (sessionHandlers.values().size() > 0) {
			WebSocketSessionHandler sessionHandler = sessionHandlers.values().iterator().next();

			try {
				logger.trace("Waiting for readiness of the web socket session {}", sessionHandler.getSession().getId());
				sessionHandler.checkWebSessionReadiness();
			} catch (GraphQLRequestExecutionException e) {
				// This session is in error. We remove it from the list, and then throw the initialization error to the
				// caller
				logger.trace("The web socket session {} is not valid ({})", sessionHandler.getSession().getId(),
						e.getMessage());
				sessionHandlers.remove(sessionHandler.getSession().getId());
			}

			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// If we arrive here, this session is valid, and can execute queries. Let's return it.
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			return sessionHandler;
		}

		// No active session found
		return null;
	}

	@Override
	public List<String> getSubProtocols() {
		return SUB_PROTOCOL_LIST;
	}

	/**
	 * Retrieves the URI for the Web Socket, based on the GraphQL endpoint that has been given to the Constructor
	 * 
	 * @return
	 * @throws GraphQLRequestExecutionException
	 */
	public URI getWebSocketURI() throws GraphQLRequestExecutionException {
		String endpoint = (graphqlSubscriptionEndpoint != null) ? graphqlSubscriptionEndpoint : graphqlEndpoint;
		if (endpoint.startsWith("http:") || endpoint.startsWith("https:")) {
			// We'll use the ws or the wss protocol. Let's just replace http by ws for that
			try {
				return new URI("ws" + endpoint.substring(4));
			} catch (URISyntaxException e) {
				throw new GraphQLRequestExecutionException(
						"Error when trying to determine the Web Socket endpoint for GraphQL endpoint " + endpoint, e);
			}
		}
		throw new GraphQLRequestExecutionException(
				"non managed protocol for endpoint " + endpoint + ". This method manages only http and https");
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * This class manages one Web Socket session and its status. The {@link GraphQLReactiveWebSocketHandler} class
	 * registers each {@link WebSocketSessionHandler} instances, can can search for a valid session (and possibly create
	 * one) when executing a GraphQL query.
	 */
	static class WebSocketSessionHandler {
		Logger logger = LoggerFactory.getLogger(WebSocketSessionHandler.class);

		final WebSocketSession session;

		/**
		 * The {@link SubscriptionRequestEmitter} is the instance that can write messages into the output flux of the
		 * web socket, toward the server
		 */
		SubscriptionRequestEmitter sessionEmitter = null;

		/** A singleton of the main runtime utility classes */
		GraphqlUtils graphqlUtils = GraphqlUtils.graphqlUtils;
		/**
		 * Contains the list of subscriptions that have been executed on this Web Socket. This allows to dispatch the
		 * incoming subscription notifications toward the relevant {@link SubscriptionCallback}
		 */
		final Map<String, RequestData<?, ?>> registeredGraphQLQueries = new ConcurrentHashMap<>();

		/**
		 * Used to identify when the session is initialized and ready for GraphQL executions. The count is down to 0 as
		 * soon as one of this conditions occurs:<br/>
		 * - The initial handshake is done, and GraphQL queries can be executed on this session<br/>
		 * - An error occurs during this handshake. In this case, {@link #initializationError} contains the error that
		 * occurred
		 */
		CountDownLatch sessionInitializationLatch = new CountDownLatch(1);

		/**
		 * Contains the error that occurs during the web socket initialization process (when sending the
		 * <I>connection_init</I> message). It can be read as soon as the webSocketConnectionInitializationLatch count
		 * is down to zero (see {@link CountDownLatch#await()} )
		 */
		Throwable lastSessionError = null;

		WebSocketSessionHandler(WebSocketSession session) {
			this.session = session;
		}

		/**
		 * Execution of a subscription, from the given request.
		 * 
		 * @param <R>
		 * @param <T>
		 * @param request
		 * @param parameters
		 *            The parameter's map, that contain the parameters name and value
		 * @param subscriptionCallback
		 * @param subscriptionType
		 * @param messsageType
		 * @return The unique Id Operation that is returned by the server for this subscription, as specified by the
		 *         <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md">graphql-transport-ws
		 *         protocol</a>
		 * @throws GraphQLRequestExecutionException
		 */
		public <R, T> SubscriptionClient executeSubscription(AbstractGraphQLRequest request,
				Map<String, Object> parameters, SubscriptionCallback<T> subscriptionCallback, Class<R> subscriptionType,
				Class<T> messsageType) throws GraphQLRequestExecutionException {
			RequestData<R, T> requestData;

			if (!request.getRequestType().equals(RequestType.subscription)) {
				throw new IllegalArgumentException(
						"The request must be either a subscription, but is " + request.getRequestType());
			}

			// Let's wait until the web socket is properly initialized, as specified by the graphql-transport-ws
			// protocol
			checkWebSessionReadiness();

			requestData = new RequestData<R, T>(session, request, parameters, subscriptionCallback, subscriptionType,
					messsageType);
			registeredGraphQLQueries.put(requestData.uniqueIdOperation, requestData);

			// Let's do the subscription into the websocket, toward the GraphQL server
			logger.trace("Emitting execution of the subscription id={} on the web socket {} (request={})",
					requestData.uniqueIdOperation, session.getId(), request.getGraphQLRequest());

			sessionEmitter.emit(requestData,
					session.textMessage(encode(requestData.uniqueIdOperation, MessageType.SUBSCRIBE, requestData)));
			subscriptionCallback.onConnect();

			return new SubscriptionClientReactiveImpl(requestData.uniqueIdOperation, this);
		}

		/**
		 * This method executes a query or a mutation over this web socket, as described in the
		 * <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md">graphql-transport-ws</a> protocol.
		 * 
		 * @param <R>
		 * @param request
		 *            The request to execute
		 * @param parameters
		 *            The parameter's map, that contain the parameters name and value
		 * @param requestType
		 *            The java type that matches the query or mutation type defined in the GraphQL schema.
		 * @return
		 * @throws GraphQLRequestExecutionException
		 */
		public <R extends GraphQLRequestObject> R executeQueryOrMutation(AbstractGraphQLRequest request,
				Map<String, Object> parameters, Class<R> requestType) throws GraphQLRequestExecutionException {
			RequestData<R, ?> requestData;
			QueryOrMutationCallback<R> callback = new QueryOrMutationCallback<R>();

			if (request.getRequestType().equals(RequestType.subscription)) {
				throw new IllegalArgumentException(
						"The request must be either a query or a mutation, but is " + request.getRequestType());
			}

			// Let's wait until the web socket session is properly initialized, as specified by the graphql-transport-ws
			// protocol
			checkWebSessionReadiness();

			requestData = new RequestData<R, R>(session, request, parameters, callback, requestType, requestType);
			registeredGraphQLQueries.put(requestData.uniqueIdOperation, requestData);

			// Let's execute the request into the websocket, toward the GraphQL server
			logger.trace("Emitting execution of the subscription id={} on the web socket {} (request={})",
					requestData.uniqueIdOperation, session.getId(), request);
			sessionEmitter.emit(requestData,
					session.textMessage(encode(requestData.uniqueIdOperation, MessageType.SUBSCRIBE, requestData)));

			// Then we wait until an answer comes
			try {
				callback.latchResponseOrExceptionReceived.await(NB_SECONDS_TIME_OUT_FOR_REQUESTS, TimeUnit.SECONDS);
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
				throw new GraphQLRequestExecutionException(
						"Received no answer after " + NB_SECONDS_TIME_OUT_FOR_REQUESTS + " seconds");
			}
		}

		/**
		 * GraphQL unsubscribe to the given operation, as defined in the
		 * <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md">graphql-transport-ws</a> protocol.
		 * 
		 * @param uniqueIdOperation
		 * @throws GraphQLRequestExecutionException
		 */
		public void unsubscribe(String uniqueIdOperation) throws GraphQLRequestExecutionException {
			logger.trace(
					"Emitting 'complete' message to close the subscription for the uniqueIdOperation={} on socket {}",
					uniqueIdOperation, session.getId());

			// Let's find the subscription that manages this uniqueIdOperation
			RequestData<?, ?> requestData = registeredGraphQLQueries.get(uniqueIdOperation);

			if (requestData == null) {
				// Oups! It is unknown
				throw new GraphQLRequestExecutionException("Unknown uniqueIdOperation " + uniqueIdOperation
						+ " for web socket session " + session + " when trying to unsubscribe");
			} else {
				requestData.setCompleted(true);
				sessionEmitter.emit(requestData,
						session.textMessage(encode(requestData.uniqueIdOperation, MessageType.COMPLETE, null)));
			}

		}

		public WebSocketSession getSession() {
			return session;
		}

		/**
		 * Manages one web socket session. This session may be used to server several queries, mutations and
		 * subscriptions.
		 * 
		 * @return
		 */
		Mono<Void> handleWebSocketSession() {
			Mono<Void> input = session.receive()//
					.subscribeOn(Schedulers.parallel())// Message will be treated in parallel threads. Necessary in high
														// rate condition
					.doOnNext(message -> onNext(message))//
					.doOnError(t -> onError(t))//
					.doOnComplete(() -> onComplete())//
					.doFinally((sig) -> onfinally(sig))//
					.then();

			@SuppressWarnings("unchecked")
			Mono<Void> output = session//
					.send((Flux<WebSocketMessage>) (Flux<?>) Flux//
							.create(sink -> {
								// The first action must be to send the connection_init message to the server
								// In return, the GraphQL server will return a ConnectionAck. Once the ConnectionAck is
								// received, the Web Socket session is ready to emit GraphQL queries (see the
								// ConnectionAck management in the onNext() method)
								sink.next(session.textMessage(encode(null, MessageType.CONNECTION_INIT, null)));
								logger.trace("The 'connection_init' message has been written on the web socket {}",
										session.getId());

								// Then, we attach the publisher that will allow to send the incoming subscriptions into
								// this flux
								sessionEmitter = new SubscriptionRequestEmitter() {
									@Override
									public void emit(RequestData<?, ?> requestData, WebSocketMessage msg) {
										if (logger.isTraceEnabled())
											logger.trace(
													"Emitting message for uniqueIdOperation {} on web socket {}: {}",
													requestData.uniqueIdOperation, session.getId(),
													msg.getPayloadAsText());
										sink.next(msg);
									}
								};
							})//
							.doOnError(t -> {
								lastSessionError = t;
								logger.error("Error received on the emitting flux toward the server for session {}: {}",
										session.getId(), t.getMessage());
							})//
					);

			logger.trace("End of handle(session {}) method execution", session.getId());
			return Mono.zip(input, output).then();
		}

		/**
		 * Checks that this Web Session is ready to execute GraphQL queries, that is: the session has no error and is
		 * not closed. If an error occured during the initialization phase, then this error is thrown.
		 *
		 * @throws GraphQLRequestExecutionException
		 *             The last error that occurred, or a generic {@link GraphQLRequestExecutionException} exception if
		 *             the session is closed
		 */
		public void checkWebSessionReadiness() throws GraphQLRequestExecutionException {
			// The web socket initialization phase must be finished. And we'll wait at most nbSecondsTimeOut for that.
			try {
				if (!sessionInitializationLatch.await(NB_SECONDS_TIME_OUT_FOR_INITIALIZATION, TimeUnit.SECONDS)) {
					lastSessionError = new GraphQLRequestExecutionException(
							"The session on Web Socket " + session.getId() + " has not been initialized after "
									+ NB_SECONDS_TIME_OUT_FOR_INITIALIZATION + " seconds");
				}
				if (lastSessionError != null) {
					throw new GraphQLRequestExecutionException(
							"Error during Web Socket or Subscription initialization: "
									+ lastSessionError.getClass().getSimpleName() + "-" + lastSessionError.getMessage(),
							lastSessionError);
				}
			} catch (InterruptedException e) {
				throw new GraphQLRequestExecutionException(
						"The thread got interrupted while waiting for web socket initialization");
			}

			// If we arrive here, we're happy. There has been no error. But the session may be closed
			if (!session.isOpen()) {
				throw new GraphQLRequestExecutionException("The Web Socket session " + session.getId() + " is closed");
			}
		}

		/**
		 * This method close the current web socket session, including all active subscriptions
		 * 
		 * @param handler
		 * @param session
		 * @param status
		 * @param reason
		 */
		void closeSession(CloseStatus status, String reason) {
			sessionInitializationLatch.countDown();
			closeAllRequests(status, reason);
			session.close(status);
		}

		/**
		 * The callback that will receive the messages from the web socket. It will map these JSON messages to the
		 * relevant java class, and call the application callback with this java objects. This message can be any valid
		 * message, according to the
		 * <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md">graphql-transport-ws protocol</a>
		 * 
		 * @param message
		 *            The received JSON message
		 */
		@SuppressWarnings("unchecked")
		private void onNext(WebSocketMessage message) {
			if (logger.isTraceEnabled()) {
				logger.trace("The web socket {} received this message: {}", session.getId(),
						message.getPayloadAsText());
			}

			JsonNode jsonNode;
			Map<String, Object> map;
			try {
				jsonNode = defaultObjectMapper.readTree(message.getPayloadAsText());
				map = defaultObjectMapper.treeToValue(jsonNode, HashMap.class);
			} catch (JsonProcessingException e) {
				throw new RuntimeException("Error while reading '" + message.getPayloadAsText() + "' as a Map", e);
			}

			String id = (String) map.get("id");
			RequestData<?, ?> requestData = null;
			if (id != null) {
				// Let's find the subscription that manages this uniqueIdOperation
				requestData = registeredGraphQLQueries.get(id);
			}
			String type = (String) map.get("type");
			MessageType messageType = MessageType.resolve(type);
			if (messageType == null) {
				// Invalid message. We close the whole session, as described in the protocol
				closeSession(GraphQlStatus.INVALID_MESSAGE_STATUS, "Invalid message: " + message.getPayloadAsText());
				return;
			}

			switch (messageType) {
			case CONNECTION_ACK:
				// Ok, the connectionInit message has been sent. It's now allowed to send GraphQL
				// subscription on this web socket (see #executeSubscription() in this class)
				sessionInitializationLatch.countDown();
				logger.trace("Received 'connection_ack' on web socket {}", session.getId());
				break;
			case NEXT:
				if (logger.isTraceEnabled())
					logger.trace("Received 'next' for id {} on web socket {} (payload={})", id, session.getId(),
							message.getPayloadAsText());
				if (id == null) {
					// Invalid message. We close the whole session, as described in the protocol
					closeSession(GraphQlStatus.INVALID_MESSAGE_STATUS,
							"Invalid message (id is null): " + message.getPayloadAsText());
					return;
				}

				if (requestData == null) {
					// Oups! The server sent a message with a uniqueIdOperation that is unknown by the client
					// There is nothing in the protocol for this case...
					// We ignore this message, and mark this unknown uniqueIdOperation as complete so that we receive no
					// other message for this uniqueIdOperation
					logger.warn(
							"[graphql-transport-ws] Unknown uniqueIdOperation {} for web socket session {} (a 'complete' message is sent to the server to that he stops managing this uniqueIdOperation)",
							id, session.getId());
					sessionEmitter.emit(requestData, session.textMessage(encode(id, MessageType.COMPLETE, null)));
				} else if (requestData.isCompleted()) {
					logger.warn("Receive a message for a closed uniqueIdOperation ({}) on web socket {}", id,
							session.getId());
				} else if (map.get("payload") == null) {
					String msg = "payload is mandatory for 'next' messages";
					logger.error(msg);
					requestData.onError(new GraphQLRequestExecutionException(msg));
				} else if (!(map.get("payload") instanceof Map)) {
					String msg = "payload should be a Map, but <" + map.get("payload") + "> is not a Map";
					logger.error(msg);
					requestData.onError(new GraphQLRequestExecutionException(msg));
				} else {
					requestData.onNext((Map<String, Object>) map.get("payload"));
				}
				break;
			case COMPLETE:
				logger.trace("Received 'complete' for id {} on web socket {} (payload={})", id, session.getId(),
						message);
				requestData.onComplete();
				break;
			case ERROR:
				logger.warn("Received 'error' for id {} on web socket {} (payload={})", id, session.getId(),
						message.getPayloadAsText());
				// The payload is a list of GraphQLErrors
				if (map.get("payload") instanceof Map) {
					// The payload is one error
					String msg = (String) ((Map<?, ?>) map.get("payload")).get("message");
					requestData.onError(new GraphQLRequestExecutionException(msg));
				} else {
					// The payload is a list of errors.
					try {
						List<Error> errors = new ArrayList<>();
						for (JsonNode node : jsonNode.get("payload")) {
							errors.add(defaultObjectMapper.treeToValue(node, Error.class));
						}
						requestData
								.onError(new GraphQLRequestExecutionException("Error on subscription " + id, errors));
					} catch (JsonProcessingException e) {
						throw new RuntimeException("Error while reading the errors from '" + message.getPayloadAsText(),
								e);
					}
				}
				break;
			default:
				logger.warn("Received non managed message '{}' for id {} on web socket {} (payload={})", type, id,
						session.getId(), message);
				// Oups! This message type exists in MessageType, but is not properly managed here.
				// This is an internal error.
				String msg = "Non managed message type '" + type + "'";
				if (requestData != null) {
					requestData.onError(new GraphQLRequestExecutionException(msg));
				} else {
					logger.error(msg);
				}
			}
		}

		private void onError(Throwable t) {
			logger.error("Received this error for session {}", session.getId());
			// This session is in error state. Let's mark it.
			lastSessionError = t;

			// If this error occurred during the initialization process, we must free the relevant latch
			sessionInitializationLatch.countDown();

			if (t == null) {
				t = new RuntimeException("Unknown exception");
				logger.error("The Web Socket session {} ended with an unknown error", session.getId());
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append("The Web Socket session ").append(session.getId()).append(" ended with an error (")
						.append(t.getClass().getSimpleName()).append(t.getMessage()).append(")");
				for (StackTraceElement row : t.getStackTrace()) {
					sb.append("\n    ").append(row);
				}
				logger.error(sb.toString());
			}

			// We must free every resources for this websocket
			for (RequestData<?, ?> requestData : registeredGraphQLQueries.values()) {
				requestData.onError(t);
			}

			closeSession(CloseStatus.SERVER_ERROR, "");
		}

		/**
		 * This method is called when the whole Flux complete, which should never happen
		 */
		private void onComplete() {
			logger.trace("onComplete received for WebSocketSession {}", session.getId());

			// Let's forward the information to each subscription
			for (RequestData<?, ?> requestData : registeredGraphQLQueries.values()) {
				requestData.onComplete();
			}
		}

		/**
		 * Reaction when the web socket session finishes
		 * 
		 * @param sig
		 */
		private void onfinally(SignalType sig) {
			sessionInitializationLatch.countDown();
			logger.trace("onfinally received for WebSocketSession {} with signal {}", session.getId(), sig);
			closeAllRequests(CloseStatus.SERVER_ERROR, "Signal received: " + sig.toString());
			session.close();
		}

		/**
		 * @param status
		 * @param reason
		 */
		private void closeAllRequests(CloseStatus status, String reason) {
			logger.debug("Closing session {} for status {} and reason {}", session.getId(), status, reason);
			for (RequestData<?, ?> requestData : registeredGraphQLQueries.values()) {
				requestData.onClose(status.getCode(), (reason == null) ? status.getReason() : reason);
			}
		}

		/**
		 * Encodes a message, according to the
		 * <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md">graphql-transport-ws protocol</a>
		 */
		String encode(@Nullable String id, MessageType messageType, @Nullable RequestData<?, ?> requestData) {
			Map<String, Object> payloadMap = new HashMap<>(3);
			payloadMap.put("type", messageType.getType());
			if (id != null) {
				payloadMap.put("id", id);
			}
			if (requestData != null) {
				payloadMap.put("payload", requestData.getRequestMap());
			}
			try {
				return defaultObjectMapper.writeValueAsString(payloadMap);
			} catch (IOException ex) {
				throw new RuntimeException("Failed to write " + payloadMap + " as JSON", ex);
			}
		}
	}// WebSocketSessionHandler

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * This class manages a subscription, wheras the {@link GraphQLReactiveWebSocketHandler} class manages the whole web
	 * socket. There may be more than one subscriptions subscribed on one web socket
	 * 
	 * @author etienne-sf
	 * @param <R>
	 * @param <T>
	 */
	static class RequestData<R, T> {

		/**
		 * Holder for the Request Data. This class contains the field that identified request that are passed through
		 * the Web Socket, according to the
		 * <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md">graphql-transport-ws protocol</a>
		 * protocol.
		 * 
		 * @param session
		 *            The session on which the messages for this subscription are managed
		 * @param objectMapper
		 *            The mapper configured for this subscription
		 * @param request
		 *            The GraphQL request to execute. According to the
		 *            <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md">graphql-transport-ws
		 *            protocol</a> protocol, it can be a query, a mutation or a subscription.
		 * @param parameters
		 *            The parameter's map, that contain the parameters name and value
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
		RequestData(WebSocketSession session, AbstractGraphQLRequest request, Map<String, Object> parameters,
				SubscriptionCallback<T> subscriptionCallback, Class<R> requestType, Class<T> messageType)
				throws GraphQLRequestExecutionException {
			this.session = session;
			this.request = request;
			this.parameters = parameters;
			this.subscriptionCallback = subscriptionCallback;
			this.subscriptionType = requestType;
			this.messageType = messageType;
			this.uniqueIdOperation = getNextIdOperation();

			// If it's a query or a mutation, T and R must be equal
			if (!request.getRequestType().equals(RequestType.subscription) && requestType != messageType) {
				throw new GraphQLRequestExecutionException(
						"[Internal error] When executing query or mutation, T and R should be equal. But R (requestType) is "
								+ requestType.getName() + " and T (messsageType) is " + messageType.getName());
			}
		}

		/** The session on which the messages for this subscription are managed */
		final WebSocketSession session;

		/**
		 * The id for this subscription on this web socket. It identifies a query and its responses in this web socket,
		 * as specified by the
		 * <a href="https://github.com/enisdenjo/graphql-ws/blob/master/PROTOCOL.md">graphql-transport-ws protocol</a>.
		 */
		final String uniqueIdOperation;

		/** The request to send to the GraphQL server, to initiate the subscription */
		final AbstractGraphQLRequest request;

		/** The parameter's map, that contain the parameters name and value */
		final Map<String, Object> parameters;

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

		/** Returns the payload to use in the requests toward the GraphQL server */
		Map<String, Object> getRequestMap() {
			try {
				return request.buildRequestAsMap(parameters);
			} catch (GraphQLRequestExecutionException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}

		private synchronized String getNextIdOperation() {
			return Long.toString(nextUniqueIdOperation++);
		}

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
						uniqueIdOperation, session.getId(), uniqueIdOperation, result);
			} else {
				logger.trace("Message received for a subscription of id {}, from the Web Socket: {} (on session {})",
						uniqueIdOperation, result, session.getId());

				// The generated POJOs have annotations that allow proper deserialization by the Jackson mapper,
				// including management of interfaces and unions. Let's reuse that .
				JsonResponseWrapper response = request.getGraphQLObjectMapper().convertValue(result,
						JsonResponseWrapper.class);

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
					R r = request.getGraphQLObjectMapper().convertValue(response.data, subscriptionType);

					if (request.getRequestType().equals(RequestType.subscription)) {
						// It's a subscription: we need to get the payload content, from the subscription's name
						@SuppressWarnings("unchecked")
						T t = (T) GraphqlUtils.graphqlUtils.invokeGetter(r,
								request.getSubscription().getFields().get(0).getName());
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
						session.getId(), uniqueIdOperation);
			} else {
				logger.trace("Error received for WebSocketSession {}: {}", session.getId(), t.getMessage());
				// Let's forward the information to the application callback
				subscriptionCallback.onError(t);
			}
		}

		public void onClose(int statusCode, String reason) {
			if (completed) {
				logger.trace(
						"'Close' received from the Web Socket session {}, but the operation {} has already completed (status={}, reason={})",
						session.getId(), uniqueIdOperation, reason, session.getId());
			} else {

				logger.trace("onClose(code={}, reason={}) received for WebSocketSession {}: {}", statusCode, reason,
						session.getId());
				// Let's forward the information to the application callback
				subscriptionCallback.onClose(statusCode, reason);
			}
		}

		public void onComplete() {
			if (completed) {
				logger.trace(
						"Complete received from the Web Socket session {}, but the operation {} has already completed",
						session.getId(), uniqueIdOperation);
			} else {
				logger.trace("onComplete received for id {} on WebSocketSession {}", uniqueIdOperation,
						session.getId());
				// Let's forward the information to the application callback
				subscriptionCallback.onClose(0, "Complete");
			}
		}

		public boolean isCompleted() {
			return this.completed;
		}

		public void setCompleted(boolean completed) {
			this.completed = completed;
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	static interface SubscriptionRequestEmitter {
		void emit(RequestData<?, ?> requestData, WebSocketMessage msg);
	}
}
