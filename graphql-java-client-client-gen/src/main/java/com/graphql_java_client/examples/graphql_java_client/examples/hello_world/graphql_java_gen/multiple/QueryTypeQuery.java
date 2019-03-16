package com.graphql_java_client.examples.graphql_java_client.examples.hello_world.graphql_java_gen.multiple;

import com.shopify.graphql.support.Arguments;
import com.shopify.graphql.support.ID;
import com.shopify.graphql.support.Query;

/**
* 
*/
public class QueryTypeQuery extends Query<QueryTypeQuery> {
	QueryTypeQuery(StringBuilder _queryBuilder) {
		super(_queryBuilder);
	}

	public class HeroArguments extends Arguments {
		HeroArguments(StringBuilder _queryBuilder) {
			super(_queryBuilder, true);
		}

		/**
		* 
		*/
		public HeroArguments episode(Episode value) {
			if (value != null) {
				startArgument("episode");
				_queryBuilder.append(value.toString());
			}
			return this;
		}
	}

	public interface HeroArgumentsDefinition {
		void define(HeroArguments args);
	}

	/**
	* 
	*/
	public QueryTypeQuery hero(CharacterQueryDefinition queryDef) {
		return hero(args -> {
		}, queryDef);
	}

	/**
	* 
	*/
	public QueryTypeQuery hero(HeroArgumentsDefinition argsDef, CharacterQueryDefinition queryDef) {
		startField("hero");

		HeroArguments args = new HeroArguments(_queryBuilder);
		argsDef.define(args);
		HeroArguments.end(args);

		_queryBuilder.append('{');
		queryDef.define(new CharacterQuery(_queryBuilder));
		_queryBuilder.append('}');

		return this;
	}

	public class HumanArguments extends Arguments {
		HumanArguments(StringBuilder _queryBuilder) {
			super(_queryBuilder, true);
		}

		/**
		* 
		*/
		public HumanArguments id(String value) {
			if (value != null) {
				startArgument("id");
				Query.appendQuotedString(_queryBuilder, value.toString());
			}
			return this;
		}
	}

	public interface HumanArgumentsDefinition {
		void define(HumanArguments args);
	}

	/**
	* 
	*/
	public QueryTypeQuery human(HumanQueryDefinition queryDef) {
		return human(args -> {
		}, queryDef);
	}

	/**
	* 
	*/
	public QueryTypeQuery human(HumanArgumentsDefinition argsDef, HumanQueryDefinition queryDef) {
		startField("human");

		HumanArguments args = new HumanArguments(_queryBuilder);
		argsDef.define(args);
		HumanArguments.end(args);

		_queryBuilder.append('{');
		queryDef.define(new HumanQuery(_queryBuilder));
		_queryBuilder.append('}');

		return this;
	}

	/**
	* 
	*/
	public QueryTypeQuery droid(ID id, DroidQueryDefinition queryDef) {
		startField("droid");

		_queryBuilder.append("(id:");
		Query.appendQuotedString(_queryBuilder, id.toString());

		_queryBuilder.append(')');

		_queryBuilder.append('{');
		queryDef.define(new DroidQuery(_queryBuilder));
		_queryBuilder.append('}');

		return this;
	}

	public String toString() {
		return _queryBuilder.toString();
	}
}