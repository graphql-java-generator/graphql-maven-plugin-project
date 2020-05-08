package com.graphql_java_generator.server.util;

import java.util.Collections;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * This example code chose to use GSON as its JSON parser. Any JSON parser should be fine
 */
public class JsonKit {
	private static final Gson GSON = new GsonBuilder()
			//
			// This is important because the graphql spec says that null values should be present
			//
			.serializeNulls().create();

	public static Map<String, Object> toMap(String jsonStr) {
		if (jsonStr == null || jsonStr.trim().length() == 0) {
			return Collections.emptyMap();
		}
		// gson uses type tokens for generic input like Map<String,Object>
		TypeToken<Map<String, Object>> typeToken = new TypeToken<Map<String, Object>>() {
		};
		Map<String, Object> map = GSON.fromJson(jsonStr, typeToken.getType());
		return map == null ? Collections.emptyMap() : map;
	}

	public static String toJsonString(Object obj) {
		return GSON.toJson(obj);
	}
}
