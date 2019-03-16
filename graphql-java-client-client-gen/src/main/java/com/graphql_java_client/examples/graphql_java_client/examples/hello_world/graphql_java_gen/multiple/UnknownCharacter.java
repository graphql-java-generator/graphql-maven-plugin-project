package com.graphql_java_client.examples.graphql_java_client.examples.hello_world.graphql_java_gen.multiple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shopify.graphql.support.AbstractResponse;
import com.shopify.graphql.support.ID;
import com.shopify.graphql.support.SchemaViolationError;

public class UnknownCharacter extends AbstractResponse<UnknownCharacter> implements Character {
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