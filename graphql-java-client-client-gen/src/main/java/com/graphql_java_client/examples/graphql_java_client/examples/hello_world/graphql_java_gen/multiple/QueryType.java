package com.graphql_java_client.examples.graphql_java_client.examples.hello_world.graphql_java_gen.multiple;

import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.SchemaViolationError;

/**
* 
*/
public class QueryType extends AbstractResponse<QueryType> {
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