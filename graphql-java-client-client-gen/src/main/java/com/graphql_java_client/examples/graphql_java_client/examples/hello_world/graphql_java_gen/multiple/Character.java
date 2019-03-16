package com.graphql_java_client.examples.graphql_java_client.examples.hello_world.graphql_java_gen.multiple;

import java.util.List;

import com.shopify.graphql.support.ID;

public interface Character {
	String getGraphQlTypeName();

	ID getId();

	String getName();

	List<Character> getFriends();

	List<Episode> getAppearsIn();
}