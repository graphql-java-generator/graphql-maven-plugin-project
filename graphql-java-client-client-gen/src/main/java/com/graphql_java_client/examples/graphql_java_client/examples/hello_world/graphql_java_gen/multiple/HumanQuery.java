package com.graphql_java_client.examples.graphql_java_client.examples.hello_world.graphql_java_gen.multiple;

import com.shopify.graphql.support.Query;

public class HumanQuery extends Query<HumanQuery> {
	HumanQuery(StringBuilder _queryBuilder) {
		super(_queryBuilder);
	}

	/**
	* 
	*/
	public HumanQuery id() {
		startField("id");

		return this;
	}

	/**
	* 
	*/
	public HumanQuery name() {
		startField("name");

		return this;
	}

	/**
	* 
	*/
	public HumanQuery friends(CharacterQueryDefinition queryDef) {
		startField("friends");

		_queryBuilder.append('{');
		queryDef.define(new CharacterQuery(_queryBuilder));
		_queryBuilder.append('}');

		return this;
	}

	/**
	* 
	*/
	public HumanQuery appearsIn() {
		startField("appearsIn");

		return this;
	}

	/**
	* 
	*/
	public HumanQuery homePlanet() {
		startField("homePlanet");

		return this;
	}
}