package com.graphql_java_client.examples.graphql_java_client.examples.hello_world.graphql_java_gen.multiple;

import com.shopify.graphql.support.Query;

/**
* 
*/
public class DroidQuery extends Query<DroidQuery> {
	DroidQuery(StringBuilder _queryBuilder) {
		super(_queryBuilder);
	}

	/**
	* 
	*/
	public DroidQuery id() {
		startField("id");

		return this;
	}

	/**
	* 
	*/
	public DroidQuery name() {
		startField("name");

		return this;
	}

	/**
	* 
	*/
	public DroidQuery friends(CharacterQueryDefinition queryDef) {
		startField("friends");

		_queryBuilder.append('{');
		queryDef.define(new CharacterQuery(_queryBuilder));
		_queryBuilder.append('}');

		return this;
	}

	/**
	* 
	*/
	public DroidQuery appearsIn() {
		startField("appearsIn");

		return this;
	}

	/**
	* 
	*/
	public DroidQuery primaryFunction() {
		startField("primaryFunction");

		return this;
	}
}