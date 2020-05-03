package com.graphql_java_generator.samples.forum.server;

import static java.util.Collections.singletonList;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.graphql_java_generator.samples.forum.server.temp.util.JsonKit;
import com.graphql_java_generator.samples.forum.server.temp.util.QueryParameters;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.execution.instrumentation.ChainedInstrumentation;
import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.tracing.TracingInstrumentation;

public class WebSocketHandler extends TextWebSocketHandler {

	private static final Logger log = LoggerFactory.getLogger(WebSocketHandler.class);

	GraphQLProvider graphQLProvider;
	private final AtomicReference<Subscription> subscriptionRef;

	public WebSocketHandler(GraphQLProvider graphQLProvider) {
		this.graphQLProvider = graphQLProvider;
		subscriptionRef = new AtomicReference<>();
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		log.info("Websocket connection established");
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		log.info("Closing subscription ");
		Subscription subscription = subscriptionRef.get();
		if (subscription != null) {
			subscription.cancel();
		}
	}

	@Override
	protected void handleTextMessage(WebSocketSession webSocketSession, TextMessage message) throws Exception {
		String graphqlQuery = message.getPayload();
		log.info("Websocket said {}", graphqlQuery);

		QueryParameters parameters = QueryParameters.from(graphqlQuery);

		ExecutionInput executionInput = ExecutionInput.newExecutionInput().query(parameters.getQuery())
				.variables(parameters.getVariables()).operationName(parameters.getOperationName()).build();

		Instrumentation instrumentation = new ChainedInstrumentation(singletonList(new TracingInstrumentation()));

		//
		// In order to have subscriptions in graphql-java you MUST use the
		// SubscriptionExecutionStrategy strategy.
		//
		GraphQL graphQL = GraphQL.newGraphQL(graphQLProvider.getGraphQLSchema()).instrumentation(instrumentation)
				.build();

		ExecutionResult executionResult = graphQL.execute(executionInput);

		Publisher<ExecutionResult> stockPriceStream = executionResult.getData();

		stockPriceStream.subscribe(new Subscriber<ExecutionResult>() {

			@Override
			public void onSubscribe(Subscription s) {
				log.debug("Executing subscription");
				subscriptionRef.set(s);
				request(1);
			}

			@Override
			public void onNext(ExecutionResult er) {
				log.debug("Sending new post");
				try {
					Object stockPriceUpdate = er.getData();
					String json = JsonKit.toJsonString(stockPriceUpdate);
					webSocketSession.sendMessage(new TextMessage(json));
				} catch (IOException e) {
					e.printStackTrace();
				}
				request(1);
			}

			@Override
			public void onError(Throwable t) {
				log.error("Subscription threw an exception", t);
				try {
					webSocketSession.close();
				} catch (IOException e) {
					log.error("Unable to close websocket session", e);
				}
			}

			@Override
			public void onComplete() {
				log.info("Subscription complete");
				try {
					webSocketSession.close();
				} catch (IOException e) {
					log.error("Unable to close websocket session", e);
				}
			}
		});
	}

	private void request(int n) {
		Subscription subscription = subscriptionRef.get();
		if (subscription != null) {
			subscription.request(n);
		}
	}

}
