package com.graphql_java_generator.client.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * 
 * The error POJO, mapped from the GraphQL server response, when an error occurs
 * 
 * @author etienne-sf
 */
public class Error {

	public String message;

	@JsonDeserialize(contentAs = Location.class)
	public List<Location> locations;

	public String description;

	public String validationErrorType;

	@JsonDeserialize(contentAs = String.class)
	public List<String> queryPath;

	public String errorType;

	public List<String> path;

	/**
	 * The extensions field of errors, stored as is from the incoming GraphQL response. It can be retrieved thanks to
	 * one of these methods: {@link #getExtensions()}, {@link #getExtensionsAsMap()},
	 * {@link #getExtensionsField(String, Class)}
	 */
	public JsonNode extensions;

	private Map<String, JsonNode> extensionsAsMap = null;

	private ObjectMapper mapper = null;

	/**
	 * Logs this error to the given {@link Logger}
	 * 
	 * @param logger
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		if (validationErrorType != null) {
			sb.append("[{").append(validationErrorType).append("}]");
		}
		if (errorType != null) {
			sb.append("{").append(errorType).append("}");
		}

		sb.append("{").append(message).append("}");

		if (queryPath != null) {
			sb.append(" path: {").append(queryPath.stream().collect(Collectors.joining(","))).append("}");
		}
		if (locations != null) {
			sb.append(" - locations: {")
					.append(locations.stream().map(Object::toString).collect(Collectors.joining(","))).append("}");
		}
		if (description != null) {
			sb.append(" - ({").append(description).append("})");
		}

		return sb.toString();
	}

	public JsonNode getExtensions() {
		return extensions;
	}

	public void setExtensions(JsonNode extensions) {
		this.extensions = extensions;
	}

	/**
	 * Returns the extensions as a map. The values can't be deserialized, as their type is unknown.
	 * 
	 * @return
	 */
	public Map<String, JsonNode> getExtensionsAsMap() {
		if (extensionsAsMap == null) {
			ObjectMapper mapper = new ObjectMapper();
			extensionsAsMap = mapper.convertValue(extensions, new TypeReference<Map<String, JsonNode>>() {
			});
		}
		return extensionsAsMap;
	}

	/**
	 * Returns the extensions as a map. The values can't be deserialized, as their type is unknown.
	 * 
	 * @return
	 */
	public Map<String, String> getExtensionsAsMapStringString() {
		getExtensionsAsMap();
		Map<String, String> extensionsAsMapStringString = new HashMap<>();

		for (String key : extensionsAsMap.keySet()) {
			extensionsAsMapStringString.put(key, extensionsAsMap.get(key).toString());
		}
		return extensionsAsMapStringString;
	}

	/**
	 * Parse the value for the given _key_, as found in the <I>extensions</I> field of the GraphQL server's response,
	 * into the given _t_ class.
	 * 
	 * @param <T>
	 * @param key
	 * @param t
	 * @return null if the key is not in the <I>extensions</I> map. Otherwise: the value for this _key_, as a _t_
	 *         instance
	 * @throws JsonProcessingException
	 *             When there is an error when converting the key's value into the _t_ class
	 */
	public <T> T getExtensionsField(String key, Class<T> t) throws JsonProcessingException {
		JsonNode node = getExtensionsAsMap().get(key);
		return (node == null) ? null : getMapper().treeToValue(node, t);
	}

	/**
	 * Allows to retrieve a Jackson {@link ObjectMapper} as a singleton, and only of needed
	 * 
	 * @return
	 */
	private ObjectMapper getMapper() {
		if (mapper == null) {
			mapper = new ObjectMapper();
		}
		return mapper;
	}
}
