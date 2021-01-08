/**
 * 
 */
package org.allGraphQLCases.server.oauth2;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionClaimNames;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;

/**
 * It seems that our custom OAuth authorization server is not totally compliant with OAuth spec. So we need to manually
 * extract the authorities. This code is based on the issue, which link is below.
 * 
 * @author etienne-sf
 * @see https://github.com/spring-projects/spring-security/issues/7563
 */
@Component
public class CustomAuthoritiesOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

	private OpaqueTokenIntrospector delegate;

	public CustomAuthoritiesOpaqueTokenIntrospector(
			@Value("${security.oauth2.resource.introspection-uri}") String introspectionUri,
			@Value("${security.oauth2.client.client-id}") String clientId,
			@Value("${security.oauth2.client.client-secret}") String clientSecret) {
		delegate = new NimbusOpaqueTokenIntrospector(introspectionUri, clientId, clientSecret);
	}

	@Override
	public OAuth2AuthenticatedPrincipal introspect(String token) {
		OAuth2AuthenticatedPrincipal principal = this.delegate.introspect(token);
		return new DefaultOAuth2AuthenticatedPrincipal(principal.getName(), principal.getAttributes(),
				extractAuthorities(principal));
	}

	private Collection<GrantedAuthority> extractAuthorities(OAuth2AuthenticatedPrincipal principal) {
		List<String> scopes = principal.getAttribute(OAuth2IntrospectionClaimNames.SCOPE);
		return scopes.stream().map(scope -> "SCOPE_" + scope).map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}
}