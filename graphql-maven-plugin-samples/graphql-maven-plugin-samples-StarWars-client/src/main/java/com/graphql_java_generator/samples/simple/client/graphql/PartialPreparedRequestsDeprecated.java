/**
 * 
 */
package com.graphql_java_generator.samples.simple.client.graphql;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.annotation.PostConstruct;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.stereotype.Component;

import com.generated.graphql.Character;
import com.generated.graphql.Droid;
import com.generated.graphql.Episode;
import com.generated.graphql.Human;
import com.generated.graphql.MutationType;
import com.generated.graphql.QueryType;
import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.simple.client.Queries;

/**
 * This class implements the away to call GraphQl queries, where all queries are prepared before execution.<BR/>
 * The advantages are:
 * <UL>
 * <LI>Performance: this avoid to build an {@link ObjectResponse} for each response. This {@link ObjectResponse} is
 * useful, to help control at runtime if a field has been queried or not. It allows to throw an exception when your code
 * tries to use a field that was not queried</LI>
 * <LI>Security: as all request have been prepared at startup, this make sure at startup that your queries are
 * valid.</LI>
 * </UL>
 * 
 * @author etienne-sf
 */
@Component
public class PartialPreparedRequestsDeprecated implements Queries {

	final static String GRAPHQL_ENDPOINT = "https://localhost:8443/starwars/graphql";

	// QueryType can not be autowired by Spring. Only QueryTypeExecutor can. It's built in the constructor
	QueryType queryType;
	// MutationType can not be autowired by Spring. Only MutationTypeExecutor can. It's built in the constructor
	MutationType mutationType;

	ObjectResponse heroFullResponse;
	ObjectResponse heroPartialResponse;
	ObjectResponse heroFriendsFriendsFriendsResponse;

	ObjectResponse humanFullResponse;
	ObjectResponse humanPartialResponse;
	ObjectResponse humanFriendsFriendsFriendsResponse;

	ObjectResponse droidFullResponse;
	ObjectResponse droidPartialResponse;
	ObjectResponse droidFriendsFriendsFriendsResponse;

	ObjectResponse createHuman;
	ObjectResponse addFriend;

	public PartialPreparedRequestsDeprecated() throws KeyManagementException, NoSuchAlgorithmException {
		queryType = new QueryType(GRAPHQL_ENDPOINT, getNoCheckSslContext(), new NoOpHostnameVerifier());
		mutationType = new MutationType(GRAPHQL_ENDPOINT, getNoCheckSslContext(), new NoOpHostnameVerifier());
	}

	/**
	 * This constructor expects the URI of the GraphQL server. This constructor works only for http servers, not for
	 * https ones.<BR/>
	 * For example: https://my.server.com/graphql
	 * 
	 * @param graphqlEndpoint
	 *            the https URI for the GraphQL endpoint
	 * @param sslContext
	 * @param hostnameVerifier
	 * @throws GraphQLRequestPreparationException
	 */
	@PostConstruct
	public void init() throws GraphQLRequestPreparationException {

		// The easiest way: don't precise which fields you want, and all known scalar fields are queried
		heroFullResponse = queryType.getHeroResponseBuilder().build();

		// Of course, you can precise the fields you want
		heroPartialResponse = queryType.getHeroResponseBuilder().withQueryResponseDef("{appearsIn name}").build();
		heroFriendsFriendsFriendsResponse = queryType.getHeroResponseBuilder()
				.withQueryResponseDef("{id appearsIn friends {name friends {friends{id name appearsIn}}}}").build();

		// The easiest way: don't precise which fields you want, and all known scalar fields are queried
		humanFullResponse = queryType.getHumanResponseBuilder().build();

		// Of course, you can precise the fields you want
		humanPartialResponse = queryType.getHumanResponseBuilder().withQueryResponseDef("{appearsIn homePlanet name}")
				.build();
		humanFriendsFriendsFriendsResponse = queryType.getHumanResponseBuilder()
				.withQueryResponseDef("{id appearsIn name friends {name friends {friends{id name appearsIn}}}}")
				.build();

		// The easiest way: don't precise which fields you want, and all known scalar fields are queried
		droidFullResponse = queryType.getDroidResponseBuilder().build();

		// Of course, you can precise the fields you want
		droidPartialResponse = queryType.getDroidResponseBuilder()
				.withQueryResponseDef("{appearsIn primaryFunction name}").build();
		droidFriendsFriendsFriendsResponse = queryType.getDroidResponseBuilder()
				.withQueryResponseDef(
						"{id appearsIn name friends {name friends {friends{id name appearsIn}}} primaryFunction }")
				.build();

		// A demo of a wrong query. The preparation below should fail.
		// In real code, this would throw an exception to the caller
		// But here, we want the code to go on. So we just check to confirm that the exception occurs, and actually hide
		// it.
		try {
			queryType.getDroidResponseBuilder().withQueryResponseDef(
					"{id appearsIn NON_EXISTING_FIELD friends {name friends {friends{id name appearsIn}}} primaryFunction }")
					.build();

			// Oups, if we got there, the expected exception was not thrown. Which means that the sample failed.
			// Let's throw an exception to block the execution (as the Maven build executes this sample, this will hang
			// the build)
			throw new RuntimeException("The query with the NON_EXISTING_FIELD should have thrown a "
					+ GraphQLRequestPreparationException.class.getName()
					+ " exception, but no exception was thrown (in " + this.getClass().getName() + ")");

		} catch (GraphQLRequestPreparationException e) {
			// This what's expected. So, no further action ... as we're in a sample !
		}

		// Mutations
		createHuman = mutationType.getCreateHumanResponseBuilder()
				.withQueryResponseDef("{id name homePlanet appearsIn friends {id name}}").build();
		addFriend = mutationType.getAddFriendResponseBuilder()
				.withQueryResponseDef("{id name appearsIn friends {id name}}").build();
	}

	@Override
	public Character heroFull() throws GraphQLRequestExecutionException {
		return queryType.hero(heroFullResponse, null);
	}

	@Override
	public Character heroPartial(Episode episode) throws GraphQLRequestExecutionException {
		return queryType.hero(heroPartialResponse, episode);
	}

	@Override
	public Character heroFriendsFriendsFriends(Episode episode) throws GraphQLRequestExecutionException {
		return queryType.hero(heroFriendsFriendsFriendsResponse, episode);
	}

	@Override
	public Human humanFull(String id) throws GraphQLRequestExecutionException {
		return queryType.human(humanFullResponse, id);
	}

	@Override
	public Human humanPartial(String id) throws GraphQLRequestExecutionException {
		return queryType.human(humanPartialResponse, id);
	}

	@Override
	public Human humanFriendsFriendsFriends(String id) throws GraphQLRequestExecutionException {
		return queryType.human(humanFriendsFriendsFriendsResponse, id);
	}

	@Override
	public Droid droidFull(String id) throws GraphQLRequestExecutionException {
		return queryType.droid(droidFullResponse, id);
	}

	@Override
	public Droid droidPartial(String id) throws GraphQLRequestExecutionException {
		return queryType.droid(droidPartialResponse, id);
	}

	@Override
	public Droid droidFriendsFriendsFriends(String id) throws GraphQLRequestExecutionException {
		return queryType.droid(droidFriendsFriendsFriendsResponse, id);
	}

	@Override
	public Droid droidDoesNotExist() throws GraphQLRequestExecutionException {
		return queryType.droid(droidFriendsFriendsFriendsResponse, "00000000-0000-0000-0000-000000001111");
	}

	@Override
	public Human createHuman(String name, String homePlanet)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return mutationType.createHuman(createHuman, name, homePlanet);
	}

	@Override
	public Character addFriend(String idCharacter, String idNewFriend)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return mutationType.addFriend(addFriend, idCharacter, idNewFriend);
	}

	/**
	 * Returns a Dummy SSLContext, that won't check the server certificate. With a real check, an exception is thrown,
	 * as it is self signed.<BR/>
	 * DON'T USE IT IN PRODUCTION.
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public SSLContext getNoCheckSslContext() throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext sslContext = SSLContext.getInstance("TLSv1");

		// Very, very bad. Don't do that in production !
		KeyManager[] keyManagers = null;
		TrustManager[] trustManager = { new NoOpTrustManager() };
		SecureRandom secureRandom = new SecureRandom();

		sslContext.init(keyManagers, trustManager, secureRandom);

		return sslContext;
	}

	/**
	 * A dummy checker. DON'T USE IT IN PRODUCTION. But we can't buy a real certificat for this project.
	 * 
	 * @return
	 */
	public HostnameVerifier getHostnameVerifier() {
		// Very, very bad. Don't do that in production !
		return new NoOpHostnameVerifier();
	}

	public class NoOpTrustManager implements X509TrustManager {
		@Override
		public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}
	}

	public class NoOpHostnameVerifier implements HostnameVerifier {
		@Override
		public boolean verify(String s, SSLSession sslSession) {
			return true;
		}
	}
}