/**
 * 
 */
package com.graphql_java_generator.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.ExchangeFunctions;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * This class is used to retrieve the OAuth token, when in client mode, for subscription. It is used by the
 * {@link SubscriptionClientReactiveImpl} class, when executing a subscription. The reason is that the current websocket
 * implementation doesn't use Spring Security OAuth filters. So we have to manually implement them (for subscription
 * only).<BR/>
 * This implementation is based on philsttr idea, as explained
 * <a href="https://github.com/spring-projects/spring-security/issues/6711">on this page</a>. Here is what philsttr
 * proposed:<BR/>
 * 
 * <UL>
 * <LI>Create an ExchangeFunction that has two filters that execute in the following order:</LI>
 * <UL>
 * <LI>ServerOAuth2AuthorizedClientExchangeFilterFunction</LI>
 * <LI>a custom ExchangeFilterFunction that:</LI>
 * <UL>
 * <LI>if the request is a bogus request (from step 2) capture the request's Authorization header and returns a
 * ClientResponse with an Authorization header (without invoking the downstream ExchangeFunction)</LI>
 * <LI>else invoke the downstream ExchangeFunction (to handle requests created by the
 * ServerOAuth2AuthorizedClientExchangeFilterFunction, such as a request to refresh the token)</LI>
 * </UL>
 * </UL>
 * <LI>Send a bogus request through the ExchangeFunction created in step 1</LI>
 * <UL>
 * <LI>grab the Authorization header from the ClientResponse</LI>
 * </UL>
 * </UL>
 * 
 * Using this stream, I can reuse ExchangeFilterFunctions provided by spring security to generically obtain the
 * Authorization header value for use in places other than a WebClient.
 * 
 * @author philsttr
 * @author etienne-sf
 * @see https://github.com/spring-projects/spring-security/issues/6711
 */
public class OAuthTokenExtractor {

	/** The name of the HTTP header that contains the OAuth token */
	public final static String AUTHORIZATION_HEADER_NAME = "Authorization";

	/**
	 * A dummy request that is filtered. When this request is executed, it is filtered, and a
	 * {@link GetOAuthTokenClientResponse} is generated
	 */
	private final static String DUMMY_REQUEST = "http://127.0.0.1:80/this_is_a_bad_request_that_will_not_be_really_executed";

	/**
	 * The {@link ServerOAuth2AuthorizedClientExchangeFilterFunction} is responsible for getting OAuth token from the
	 * OAuth authorization server. It is optional, and may be provided by the App's spring config. If it is not
	 * provided, then there is no OAuth authentication on client side. If provided, then the client uses it to provide
	 * the OAuth2 authorization token, when accessing the GraphQL resource server, for queries/mutations/subscriptions.
	 */
	final ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunction;

	/** The filter which retrieves the Authorization header value */
	OAuthTokenFilter oAuthTokenFilter;

	/** The exchange function that simulates a WebClient, and allows to retrieve the OAuth token */
	ExchangeFunction getOAuthTokenExchangeFunction;

	/** The dummy request that will be executed, and that will cause the token retrieval or refresh */
	ClientRequest dummyHttpRequest;

	/**
	 * 
	 * @param serverOAuth2AuthorizedClientExchangeFilterFunction
	 *            The {@link ServerOAuth2AuthorizedClientExchangeFilterFunction} is responsible for getting OAuth token
	 *            from the OAuth authorization server. It is optional, and may be provided by the App's spring config.
	 *            If it is not provided, then there is no OAuth authentication on client side. If provided, then the
	 *            client uses it to provide the OAuth2 authorization token, when accessing the GraphQL resource server,
	 *            for queries/mutations/subscriptions.
	 */
	public OAuthTokenExtractor(
			ServerOAuth2AuthorizedClientExchangeFilterFunction serverOAuth2AuthorizedClientExchangeFilterFunction) {
		if (serverOAuth2AuthorizedClientExchangeFilterFunction == null) {
			throw new NullPointerException(
					"[internal error] serverOAuth2AuthorizedClientExchangeFilterFunction may not be null");
		}

		this.serverOAuth2AuthorizedClientExchangeFilterFunction = serverOAuth2AuthorizedClientExchangeFilterFunction;
		oAuthTokenFilter = new OAuthTokenFilter();

		// The filter will be applied in their reverse order.
		getOAuthTokenExchangeFunction = ExchangeFunctions.create(new ReactorClientHttpConnector())
				.filter(oAuthTokenFilter).filter(serverOAuth2AuthorizedClientExchangeFilterFunction);

		try {
			dummyHttpRequest = ClientRequest.create(HttpMethod.GET, new URI(DUMMY_REQUEST)).build();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

	}

	/**
	 * Returns the Authorization header value, as it has been returned by the
	 * {@link ServerOAuth2AuthorizedClientExchangeFilterFunction}, from the OAuth authorization server. The method is
	 * synchronized to avoid that the client, when using multi-threading, try to retrieve two token simultaneously.
	 * 
	 * @return
	 */
	public synchronized String getAuthorizationHeaderValue() {
		return getOAuthTokenExchangeFunction.exchange(dummyHttpRequest)
				.map(response -> response.bodyToMono(String.class).block()).block();
	}

	/**
	 * This class filters the dummy request, and respond by the {@link GetOAuthTokenClientResponse} when the
	 * {@link #DUMMY_REQUEST} is executed
	 */
	public static class OAuthTokenFilter implements ExchangeFilterFunction {
		@Override
		public @NonNull Mono<ClientResponse> filter(@NonNull ClientRequest request, @NonNull ExchangeFunction next) {
			String url = request.url().toString();
			if (!url.equals(DUMMY_REQUEST)) {
				// Standard case. We relay the request to the next ExchangeFilter
				return next.exchange(request);
			} else {
				// The caller is just wanting to get the OAuth token. Let's return it
				List<String> headers = request.headers().get(AUTHORIZATION_HEADER_NAME);
				String authorizationHeaderValue = null;
				if (headers == null || headers.size() == 0) {
					// throw new RuntimeException(
					System.out.println("WARNING:" + //
							"No " + AUTHORIZATION_HEADER_NAME + " header found, when exactly 1 is expected");
				} else if (headers.size() > 1) {
					// throw new RuntimeException(
					System.out.println("WARNING:" + //
							"Found " + headers.size() + " " + AUTHORIZATION_HEADER_NAME
							+ " headers, when exactly 1 is expected");
				} else {
					authorizationHeaderValue = headers.get(0);
				}

				return Mono.just(new GetOAuthTokenClientResponse(authorizationHeaderValue));
			}
		}
	}

	/**
	 * An implementation of a spring {@link ClientResponse}, that will contain the faked response, which body is the
	 * value for the Authorization header (that contains the bearer token)
	 * 
	 * @author etienne-sf
	 */
	public static class GetOAuthTokenClientResponse implements ClientResponse {

		private final String authorizationHeader;

		public GetOAuthTokenClientResponse(String authorizationHeader) {
			this.authorizationHeader = authorizationHeader;
		}

		@Override
		public @NonNull HttpStatus statusCode() {
			return HttpStatus.OK;
		}

		@Override
		public @NonNull Headers headers() {
			return new Headers() {
				@Override
				public @NonNull List<String> header(@NonNull String headerName) {
					List<String> ret = new ArrayList<>();
					if (AUTHORIZATION_HEADER_NAME.equals(headerName)) {
						ret.add(authorizationHeader);
					}
					return ret;
				}

				@Override
				public @NonNull Optional<MediaType> contentType() {
					return Optional.of((MediaType) null);
				}

				@Override
				public @NonNull OptionalLong contentLength() {
					return OptionalLong.of(0);
				}

				@Override
				public @NonNull HttpHeaders asHttpHeaders() {
					return new HttpHeaders();
				}
			};

		}

		@Override
		@SuppressWarnings("null")
		public @NonNull MultiValueMap<String, ResponseCookie> cookies() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		@SuppressWarnings("null")
		public @NonNull ExchangeStrategies strategies() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		@SuppressWarnings("null")
		public @NonNull <T> T body(@NonNull BodyExtractor<T, ? super ClientHttpResponse> extractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public @NonNull <T> Mono<T> bodyToMono(@NonNull Class<? extends T> elementClass) {
			if (elementClass == String.class) {
				return Mono.just((T) ((authorizationHeader == null) ? "" : authorizationHeader));
			} else {
				throw new IllegalArgumentException(
						"Only String class is allowed, for " + this.getClass().getSimpleName() + "'s body, but a "
								+ elementClass.getName() + " was requested");
			}
		}

		@Override
		public @NonNull <T> Mono<T> bodyToMono(@NonNull ParameterizedTypeReference<T> elementTypeRef) {
			throw new RuntimeException("Not Implemented");
		}

		@Override
		public @NonNull <T> Flux<T> bodyToFlux(@NonNull Class<? extends T> elementClass) {
			throw new RuntimeException("Not Implemented");
		}

		@Override
		public @NonNull <T> Flux<T> bodyToFlux(@NonNull ParameterizedTypeReference<T> elementTypeRef) {
			throw new RuntimeException("Not Implemented");
		}

		@Override
		public @NonNull Mono<Void> releaseBody() {
			throw new RuntimeException("Not Implemented");
		}

		@Override
		public @NonNull <T> Mono<ResponseEntity<T>> toEntity(@NonNull Class<T> bodyClass) {
			throw new RuntimeException("Not Implemented");
		}

		@Override
		public @NonNull <T> Mono<ResponseEntity<T>> toEntity(@NonNull ParameterizedTypeReference<T> bodyTypeReference) {
			throw new RuntimeException("Not Implemented");
		}

		@Override
		public @NonNull <T> Mono<ResponseEntity<List<T>>> toEntityList(@NonNull Class<T> elementClass) {
			throw new RuntimeException("Not Implemented");
		}

		@Override
		public @NonNull <T> Mono<ResponseEntity<List<T>>> toEntityList(
				@NonNull ParameterizedTypeReference<T> elementTypeRef) {
			throw new RuntimeException("Not Implemented");
		}

		@Override
		public @NonNull Mono<ResponseEntity<Void>> toBodilessEntity() {
			throw new RuntimeException("Not Implemented");
		}

		@Override
		public @NonNull Mono<WebClientResponseException> createException() {
			throw new RuntimeException("Not Implemented");
		}

		@Override
		public @NonNull String logPrefix() {
			return this.getClass().getSimpleName();
		}

		@Override
		@SuppressWarnings("null")
		public @NonNull <T> Mono<T> createError() {
			return null;
		}

		@Override
		@SuppressWarnings("null")
		public @NonNull HttpRequest request() {
			return null;
		}
	}
}
