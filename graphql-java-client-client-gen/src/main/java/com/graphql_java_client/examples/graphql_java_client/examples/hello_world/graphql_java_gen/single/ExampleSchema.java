// Generated from graphql_java_gen gem

package com.graphql_java_client.examples.graphql_java_client.examples.hello_world.graphql_java_gen.single;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.Arguments;
import com.shopify.graphql.support.Error;
import com.shopify.graphql.support.ID;
import com.shopify.graphql.support.Query;
import com.shopify.graphql.support.SchemaViolationError;
import com.shopify.graphql.support.TopLevelResponse;

public class ExampleSchema {
	public static QueryTypeQuery query(QueryTypeQueryDefinition queryDef) {
		StringBuilder queryString = new StringBuilder("{");
		QueryTypeQuery query = new QueryTypeQuery(queryString);
		queryDef.define(query);
		queryString.append('}');
		return query;
	}

	public static class QueryResponse {
		private TopLevelResponse response;
		private QueryType data;

		public QueryResponse(TopLevelResponse response) throws SchemaViolationError {
			this.response = response;
			this.data = response.getData() != null ? new QueryType(response.getData()) : null;
		}

		public QueryType getData() {
			return data;
		}

		public List<Error> getErrors() {
			return response.getErrors();
		}

		public String toJson() {
			return new Gson().toJson(response);
		}

		public String prettyPrintJson() {
			final Gson gson = new GsonBuilder().setPrettyPrinting().create();
			return gson.toJson(response);
		}

		public static QueryResponse fromJson(String json) throws SchemaViolationError {
			final TopLevelResponse response = new Gson().fromJson(json, TopLevelResponse.class);
			return new QueryResponse(response);
		}
	}

	public interface CharacterQueryDefinition {
		void define(CharacterQuery _queryBuilder);
	}

	/**
	* 
	*/
	public static class CharacterQuery extends Query<CharacterQuery> {
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

	public interface Character {
		String getGraphQlTypeName();

		ID getId();

		String getName();

		List<Character> getFriends();

		List<Episode> getAppearsIn();
	}

	/**
	* 
	*/
	public static class UnknownCharacter extends AbstractResponse<UnknownCharacter> implements Character {
		public UnknownCharacter() {
		}

		public UnknownCharacter(JsonObject fields) throws SchemaViolationError {
			for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
				String key = field.getKey();
				String fieldName = getFieldName(key);
				switch (fieldName) {
				case "id": {
					responseData.put(key, new ID(jsonAsString(field.getValue(), key)));

					break;
				}

				case "name": {
					responseData.put(key, jsonAsString(field.getValue(), key));

					break;
				}

				case "friends": {
					List<Character> optional1 = null;
					if (!field.getValue().isJsonNull()) {
						List<Character> list1 = new ArrayList<>();
						for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
							Character optional2 = null;
							if (!element1.isJsonNull()) {
								optional2 = UnknownCharacter.create(jsonAsObject(element1, key));
							}

							list1.add(optional2);
						}

						optional1 = list1;
					}

					responseData.put(key, optional1);

					break;
				}

				case "appearsIn": {
					List<Episode> list1 = new ArrayList<>();
					for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
						Episode optional2 = null;
						if (!element1.isJsonNull()) {
							optional2 = Episode.fromGraphQl(jsonAsString(element1, key));
						}

						list1.add(optional2);
					}

					responseData.put(key, list1);

					break;
				}

				case "__typename": {
					responseData.put(key, jsonAsString(field.getValue(), key));
					break;
				}
				default: {
					throw new SchemaViolationError(this, key, field.getValue());
				}
				}
			}
		}

		public static Character create(JsonObject fields) throws SchemaViolationError {
			String typeName = fields.getAsJsonPrimitive("__typename").getAsString();
			switch (typeName) {
			case "Droid": {
				return new Droid(fields);
			}

			case "Human": {
				return new Human(fields);
			}

			default: {
				return new UnknownCharacter(fields);
			}
			}
		}

		public String getGraphQlTypeName() {
			return (String) get("__typename");
		}

		/**
		* 
		*/

		public ID getId() {
			return (ID) get("id");
		}

		public UnknownCharacter setId(ID arg) {
			optimisticData.put(getKey("id"), arg);
			return this;
		}

		/**
		* 
		*/

		public String getName() {
			return (String) get("name");
		}

		public UnknownCharacter setName(String arg) {
			optimisticData.put(getKey("name"), arg);
			return this;
		}

		/**
		* 
		*/

		public List<Character> getFriends() {
			return (List<Character>) get("friends");
		}

		public UnknownCharacter setFriends(List<Character> arg) {
			optimisticData.put(getKey("friends"), arg);
			return this;
		}

		/**
		* 
		*/

		public List<Episode> getAppearsIn() {
			return (List<Episode>) get("appearsIn");
		}

		public UnknownCharacter setAppearsIn(List<Episode> arg) {
			optimisticData.put(getKey("appearsIn"), arg);
			return this;
		}

		public boolean unwrapsToObject(String key) {
			switch (getFieldName(key)) {
			case "id":
				return false;

			case "name":
				return false;

			case "friends":
				return false;

			case "appearsIn":
				return false;

			default:
				return false;
			}
		}
	}

	public interface DroidQueryDefinition {
		void define(DroidQuery _queryBuilder);
	}

	/**
	* 
	*/
	public static class DroidQuery extends Query<DroidQuery> {
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

	/**
	* 
	*/
	public static class Droid extends AbstractResponse<Droid> implements Character {
		public Droid() {
		}

		public Droid(JsonObject fields) throws SchemaViolationError {
			for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
				String key = field.getKey();
				String fieldName = getFieldName(key);
				switch (fieldName) {
				case "id": {
					responseData.put(key, new ID(jsonAsString(field.getValue(), key)));

					break;
				}

				case "name": {
					responseData.put(key, jsonAsString(field.getValue(), key));

					break;
				}

				case "friends": {
					List<Character> optional1 = null;
					if (!field.getValue().isJsonNull()) {
						List<Character> list1 = new ArrayList<>();
						for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
							Character optional2 = null;
							if (!element1.isJsonNull()) {
								optional2 = UnknownCharacter.create(jsonAsObject(element1, key));
							}

							list1.add(optional2);
						}

						optional1 = list1;
					}

					responseData.put(key, optional1);

					break;
				}

				case "appearsIn": {
					List<Episode> list1 = new ArrayList<>();
					for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
						Episode optional2 = null;
						if (!element1.isJsonNull()) {
							optional2 = Episode.fromGraphQl(jsonAsString(element1, key));
						}

						list1.add(optional2);
					}

					responseData.put(key, list1);

					break;
				}

				case "primaryFunction": {
					String optional1 = null;
					if (!field.getValue().isJsonNull()) {
						optional1 = jsonAsString(field.getValue(), key);
					}

					responseData.put(key, optional1);

					break;
				}

				case "__typename": {
					responseData.put(key, jsonAsString(field.getValue(), key));
					break;
				}
				default: {
					throw new SchemaViolationError(this, key, field.getValue());
				}
				}
			}
		}

		public String getGraphQlTypeName() {
			return "Droid";
		}

		/**
		* 
		*/

		public ID getId() {
			return (ID) get("id");
		}

		public Droid setId(ID arg) {
			optimisticData.put(getKey("id"), arg);
			return this;
		}

		/**
		* 
		*/

		public String getName() {
			return (String) get("name");
		}

		public Droid setName(String arg) {
			optimisticData.put(getKey("name"), arg);
			return this;
		}

		/**
		* 
		*/

		public List<Character> getFriends() {
			return (List<Character>) get("friends");
		}

		public Droid setFriends(List<Character> arg) {
			optimisticData.put(getKey("friends"), arg);
			return this;
		}

		/**
		* 
		*/

		public List<Episode> getAppearsIn() {
			return (List<Episode>) get("appearsIn");
		}

		public Droid setAppearsIn(List<Episode> arg) {
			optimisticData.put(getKey("appearsIn"), arg);
			return this;
		}

		/**
		* 
		*/

		public String getPrimaryFunction() {
			return (String) get("primaryFunction");
		}

		public Droid setPrimaryFunction(String arg) {
			optimisticData.put(getKey("primaryFunction"), arg);
			return this;
		}

		public boolean unwrapsToObject(String key) {
			switch (getFieldName(key)) {
			case "id":
				return false;

			case "name":
				return false;

			case "friends":
				return false;

			case "appearsIn":
				return false;

			case "primaryFunction":
				return false;

			default:
				return false;
			}
		}
	}

	/**
	* 
	*/
	public enum Episode {
		EMPIRE,

		JEDI,

		NEWHOPE,

		UNKNOWN_VALUE;

		public static Episode fromGraphQl(String value) {
			if (value == null) {
				return null;
			}

			switch (value) {
			case "EMPIRE": {
				return EMPIRE;
			}

			case "JEDI": {
				return JEDI;
			}

			case "NEWHOPE": {
				return NEWHOPE;
			}

			default: {
				return UNKNOWN_VALUE;
			}
			}
		}

		public String toString() {
			switch (this) {
			case EMPIRE: {
				return "EMPIRE";
			}

			case JEDI: {
				return "JEDI";
			}

			case NEWHOPE: {
				return "NEWHOPE";
			}

			default: {
				return "";
			}
			}
		}
	}

	public interface HumanQueryDefinition {
		void define(HumanQuery _queryBuilder);
	}

	/**
	* 
	*/
	public static class HumanQuery extends Query<HumanQuery> {
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

	/**
	* 
	*/
	public static class Human extends AbstractResponse<Human> implements Character {
		public Human() {
		}

		public Human(JsonObject fields) throws SchemaViolationError {
			for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
				String key = field.getKey();
				String fieldName = getFieldName(key);
				switch (fieldName) {
				case "id": {
					responseData.put(key, new ID(jsonAsString(field.getValue(), key)));

					break;
				}

				case "name": {
					responseData.put(key, jsonAsString(field.getValue(), key));

					break;
				}

				case "friends": {
					List<Character> optional1 = null;
					if (!field.getValue().isJsonNull()) {
						List<Character> list1 = new ArrayList<>();
						for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
							Character optional2 = null;
							if (!element1.isJsonNull()) {
								optional2 = UnknownCharacter.create(jsonAsObject(element1, key));
							}

							list1.add(optional2);
						}

						optional1 = list1;
					}

					responseData.put(key, optional1);

					break;
				}

				case "appearsIn": {
					List<Episode> list1 = new ArrayList<>();
					for (JsonElement element1 : jsonAsArray(field.getValue(), key)) {
						Episode optional2 = null;
						if (!element1.isJsonNull()) {
							optional2 = Episode.fromGraphQl(jsonAsString(element1, key));
						}

						list1.add(optional2);
					}

					responseData.put(key, list1);

					break;
				}

				case "homePlanet": {
					String optional1 = null;
					if (!field.getValue().isJsonNull()) {
						optional1 = jsonAsString(field.getValue(), key);
					}

					responseData.put(key, optional1);

					break;
				}

				case "__typename": {
					responseData.put(key, jsonAsString(field.getValue(), key));
					break;
				}
				default: {
					throw new SchemaViolationError(this, key, field.getValue());
				}
				}
			}
		}

		public String getGraphQlTypeName() {
			return "Human";
		}

		/**
		* 
		*/

		public ID getId() {
			return (ID) get("id");
		}

		public Human setId(ID arg) {
			optimisticData.put(getKey("id"), arg);
			return this;
		}

		/**
		* 
		*/

		public String getName() {
			return (String) get("name");
		}

		public Human setName(String arg) {
			optimisticData.put(getKey("name"), arg);
			return this;
		}

		/**
		* 
		*/

		public List<Character> getFriends() {
			return (List<Character>) get("friends");
		}

		public Human setFriends(List<Character> arg) {
			optimisticData.put(getKey("friends"), arg);
			return this;
		}

		/**
		* 
		*/

		public List<Episode> getAppearsIn() {
			return (List<Episode>) get("appearsIn");
		}

		public Human setAppearsIn(List<Episode> arg) {
			optimisticData.put(getKey("appearsIn"), arg);
			return this;
		}

		/**
		* 
		*/

		public String getHomePlanet() {
			return (String) get("homePlanet");
		}

		public Human setHomePlanet(String arg) {
			optimisticData.put(getKey("homePlanet"), arg);
			return this;
		}

		public boolean unwrapsToObject(String key) {
			switch (getFieldName(key)) {
			case "id":
				return false;

			case "name":
				return false;

			case "friends":
				return false;

			case "appearsIn":
				return false;

			case "homePlanet":
				return false;

			default:
				return false;
			}
		}
	}

	public interface QueryTypeQueryDefinition {
		void define(QueryTypeQuery _queryBuilder);
	}

	/**
	* 
	*/
	public static class QueryTypeQuery extends Query<QueryTypeQuery> {
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

	/**
	* 
	*/
	public static class QueryType extends AbstractResponse<QueryType> {
		public QueryType() {
		}

		public QueryType(JsonObject fields) throws SchemaViolationError {
			for (Map.Entry<String, JsonElement> field : fields.entrySet()) {
				String key = field.getKey();
				String fieldName = getFieldName(key);
				switch (fieldName) {
				case "hero": {
					Character optional1 = null;
					if (!field.getValue().isJsonNull()) {
						optional1 = UnknownCharacter.create(jsonAsObject(field.getValue(), key));
					}

					responseData.put(key, optional1);

					break;
				}

				case "human": {
					Human optional1 = null;
					if (!field.getValue().isJsonNull()) {
						optional1 = new Human(jsonAsObject(field.getValue(), key));
					}

					responseData.put(key, optional1);

					break;
				}

				case "droid": {
					Droid optional1 = null;
					if (!field.getValue().isJsonNull()) {
						optional1 = new Droid(jsonAsObject(field.getValue(), key));
					}

					responseData.put(key, optional1);

					break;
				}

				case "__typename": {
					responseData.put(key, jsonAsString(field.getValue(), key));
					break;
				}
				default: {
					throw new SchemaViolationError(this, key, field.getValue());
				}
				}
			}
		}

		public String getGraphQlTypeName() {
			return "QueryType";
		}

		/**
		* 
		*/

		public Character getHero() {
			return (Character) get("hero");
		}

		public QueryType setHero(Character arg) {
			optimisticData.put(getKey("hero"), arg);
			return this;
		}

		/**
		* 
		*/

		public Human getHuman() {
			return (Human) get("human");
		}

		public QueryType setHuman(Human arg) {
			optimisticData.put(getKey("human"), arg);
			return this;
		}

		/**
		* 
		*/

		public Droid getDroid() {
			return (Droid) get("droid");
		}

		public QueryType setDroid(Droid arg) {
			optimisticData.put(getKey("droid"), arg);
			return this;
		}

		public boolean unwrapsToObject(String key) {
			switch (getFieldName(key)) {
			case "hero":
				return false;

			case "human":
				return true;

			case "droid":
				return true;

			default:
				return false;
			}
		}
	}
}
