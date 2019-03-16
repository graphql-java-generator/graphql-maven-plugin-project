// Generated from graphql_java_gen gem

package com.graphql_java_client.examples.graphql_java_client.examples.hello_world.graphql_java_gen.multiple;

public class ExampleSchema {

	public static QueryTypeQuery query(QueryTypeQueryDefinition queryDef) {
		StringBuilder queryString = new StringBuilder("{");
		QueryTypeQuery query = new QueryTypeQuery(queryString);
		queryDef.define(query);
		queryString.append('}');
		return query;
	}

}
