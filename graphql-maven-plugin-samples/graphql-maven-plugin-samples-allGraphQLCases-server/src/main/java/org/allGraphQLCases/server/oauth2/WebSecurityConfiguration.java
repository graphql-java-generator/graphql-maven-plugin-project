package org.allGraphQLCases.server.oauth2;

/**
 * 
 * @author etienne-sf
 * @see https://docs.spring.io/spring-security/site/docs/5.4.2/reference/html5/#servlet-authorization-filtersecurityinterceptor
 */
// @EnableWebFluxSecurity
public class WebSecurityConfiguration {

	// @Value("${spring.security.oauth2.resource.introspection-uri}")
	// String introspectionUri;
	//
	// @Value("${spring.security.oauth2.client.client-id}")
	// String clientId;
	//
	// @Value("${spring.security.oauth2.client.client-secret}")
	// String clientSecret;
	//
	// @Bean
	// SecurityWebFilterChain configure(ServerHttpSecurity http) throws Exception {
	// // TODO Check CORS and CSRF
	// http//
	// // Disabling CORS and CSRF makes POST on the graphql URL work properly. Double-check that before
	// // entering in production
	// .cors().and().csrf().disable()//
	// .authorizeExchange()//
	// .pathMatchers("/my/updated/graphql/path").authenticated().and().oauth2Client()
	// .access("hasRole('ROLE_CLIENT')")//
	// .anyExchange().denyAll().and().authorizeRequests(authz -> authz//
	// // The line below should be commented. It's here, for temporary tests with graphiql
	// // (which can't connect to an OAuth2 protected server). If uncommented, the integration test
	// // that checks that OAuth is activated on the server will fail. This insures that these lines
	// // are commented for releases.
	// // .anyRequest().permitAll()//
	// //
	// // The two lines below checks that the use is authenticated, insuring that the OAuth2 token has
	// // been properly read.
	// .antMatchers(HttpMethod.GET, "/my/updated/graphql/path").authenticated()// .access("hasRole('ROLE_CLIENT')")//
	// .antMatchers(HttpMethod.POST, "/my/updated/graphql/path").authenticated()// .access("hasRole('ROLE_CLIENT')")//
	//
	// // The Hello World page, to check that the server is started
	// .antMatchers(HttpMethod.GET, "/helloworld.html").permitAll()//
	// // All other URL accesses are prohibited
	// .anyRequest().denyAll()//
	// //
	// )//
	// .oauth2ResourceServer(oauth2 -> oauth2.opaqueToken(
	// // When all lines below are commented, then a custom OpaqueTokenIntrospector is used. See
	// // CustomAuthoritiesOpaqueTokenIntrospector
	// token -> token//
	// ////////////////////////////////////////////////////
	// // Lines below: taken from
	// // https://docs.spring.io/spring-security/site/docs/current/reference/html5/#oauth2resourceserver-opaque-sansboot
	// // Taken from boot auto configuration:
	// // .oauth2ResourceServer(OAuth2ResourceServerConfigurer::opaqueToken)
	// // Overriding boot auto configuration (But NimbusOpaqueTokenIntrospector can't
	// // read Authorities from the token!)
	// // .introspector(new NimbusOpaqueTokenIntrospector(introspectionUri, clientId,
	// //////////////////////////////////////////////////// clientSecret))
	// /////////////////////////////////////
	// // Two lines below: taken from
	// ////////////////////////////////////////////////////
	// https://www.baeldung.com/spring-security-oauth-resource-server
	// .introspectionUri(this.introspectionUri)//
	// .introspectionClientCredentials(this.clientId, this.clientSecret)//
	// )//
	// );
	// }

}