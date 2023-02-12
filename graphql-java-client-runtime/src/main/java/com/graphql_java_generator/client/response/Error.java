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

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

/**
 * 
 * The error POJO, mapped from the GraphQL server response, when an error occurs
 * 
 * @author etienne-sf
 */
public class Error implements GraphQLError {

	private static final long serialVersionUID = 1L;

	public String message;

	// Using SourceLocation in the Location field prevent proper deserialization. So we have to map at runtime, to
	// implement the GraphQLError interface. Another solution would be to create a custom deserializer.
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
	 * one of these methods: {@link #getExtensions()}, {@link #getExtensionsAsJsonNode()},
	 * {@link #getExtensionsAsMapOfJsonNode()}, {@link #getExtensionsField(String, Class)}
	 */
	public JsonNode extensions;

	private Map<String, Object> extensionsAsMapOfObject = null;
	private Map<String, JsonNode> extensionsAsMapOfJsonNode = null;

	private ObjectMapper localObjectMapper = null;

	/**
	 * Logs this error to the given {@link Logger}
	 * 
	 * @param logger
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

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

	public JsonNode getExtensionsAsJsonNode() {
		return extensions;
	}

	public void setExtensions(JsonNode extensions) {
		this.extensions = extensions;
	}

	@Override
	public Map<String, Object> getExtensions() {
		if (extensionsAsMapOfObject == null) {
			extensionsAsMapOfObject = getMapper().convertValue(extensions, new TypeReference<Map<String, Object>>() {
			});
		}
		return extensionsAsMapOfObject;
	}

	/**
	 * Returns the extensions as a map. The values can't be deserialized, as their type is unknown.
	 * 
	 * @return
	 */
	public Map<String, JsonNode> getExtensionsAsMapOfJsonNode() {
		if (extensionsAsMapOfJsonNode == null) {
			extensionsAsMapOfJsonNode = getMapper().convertValue(extensions,
					new TypeReference<Map<String, JsonNode>>() {
					});
		}
		return extensionsAsMapOfJsonNode;
	}

	/**
	 * Returns the extensions as a map. The values can't be deserialized, as their type is unknown.
	 * 
	 * @return
	 */
	public Map<String, String> getExtensionsAsMapStringString() {
		getExtensionsAsMapOfJsonNode();
		Map<String, String> extensionsAsMapStringString = new HashMap<>();

		for (String key : extensionsAsMapOfJsonNode.keySet()) {
			extensionsAsMapStringString.put(key, extensionsAsMapOfJsonNode.get(key).toString());
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
		JsonNode node = getExtensionsAsMapOfJsonNode().get(key);
		return (node == null) ? null : getMapper().treeToValue(node, t);
	}

	/**
	 * Allows to retrieve a Jackson {@link ObjectMapper} as a singleton, and only of needed
	 * 
	 * @return
	 */
	private ObjectMapper getMapper() {
		if (localObjectMapper == null) {
			localObjectMapper = new ObjectMapper();
		}
		return localObjectMapper;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public List<SourceLocation> getLocations() {
		// Using SourceLocation in the Location field prevent proper deserialization. So we have to map at runtime, to
		// implement the GraphQLError interface. Another solution would be to create a custom deserializer.
		return (locations == null) ? null
				: locations.stream().map(l -> new SourceLocation(l.line, l.column, l.sourceName))
						.collect(Collectors.toList());
	}

	@Override
	public ErrorClassification getErrorType() {
		return new ErrorClassification() {
			@Override
			public Object toSpecification(GraphQLError error) {
				return errorType;
			}
		};
	}
}
