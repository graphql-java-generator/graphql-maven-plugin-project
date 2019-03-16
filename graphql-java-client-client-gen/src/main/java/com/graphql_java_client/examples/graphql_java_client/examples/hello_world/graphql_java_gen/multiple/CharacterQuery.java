package com.graphql_java_client.examples.graphql_java_client.examples.hello_world.graphql_java_gen.multiple;

import com.shopify.graphql.support.Query;

/**
* 
*/
public class CharacterQuery extends Query<CharacterQuery> {
	CharacterQuery(StringBuilder _queryBuilder) {
		super(_queryBuilder);

		startField("__typename");
	}

	/**
	* 
	*/
	public CharacterQuery id() {
		startField("id");

		return this;
	}

	/**
	* 
	*/
	public CharacterQuery name() {
		startField("name");

		return this;
	}

	/**
	* 
	*/
	public CharacterQuery friends(CharacterQueryDefinition queryDef) {
		startField("friends");

		_queryBuilder.append('{');
		queryDef.define(new CharacterQuery(_queryBuilder));
		_queryBuilder.append('}');

		return this;
	}

	/**
	* 
	*/
	public CharacterQuery appearsIn() {
		startField("appearsIn");

		return this;
	}

	public CharacterQuery onDroid(DroidQueryDefinition queryDef) {
		startInlineFragment("Droid");
		queryDef.define(new DroidQuery(_queryBuilder));
		_queryBuilder.append('}');
		return this;
	}

	public CharacterQuery onHuman(HumanQueryDefinition queryDef) {
		startInlineFragment("Human");
		queryDef.define(new HumanQuery(_queryBuilder));
		_queryBuilder.append('}');
		return this;
	}
}