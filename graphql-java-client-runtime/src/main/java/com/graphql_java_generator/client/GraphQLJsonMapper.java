package com.graphql_java_generator.client;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import tools.jackson.core.JacksonException;
import tools.jackson.core.TreeNode;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * This class is a wrapper around an {@link ObjectMapper}. It allows the GraphQL plugin generated code to use its own
 * {@link ObjectMapper}, without interfering with the containing app. This insures that the containing app can configure
 * and use "its" {@link ObjectMapper} as it wants, and that the GraphQL plugin can use its own {@link ObjectMapper} with
 * its own configuration.<BR/>
 * This class is not Spring bean, as it is configured for each request, with the list of alias for this GraphQL request.
 * 
 * @author etienne-sf
 */
public class GraphQLJsonMapper {

	@Autowired
	ApplicationContext ctx;

	/** The Jackson {@link JsonMapper} that is specific to the GraphQL response deserialization */
	final private JsonMapper objectMapper;

	/**
	 * Standard creator for the GraphQL {@link ObjectMapper}
	 * 
	 * @param graphQLObjectsPackage
	 *            The package where the GraphQL objects have been generated
	 * @param aliasFields
	 * @param schema
	 *            The schema name, for which this object mapper is created. This allows to retrieve the class, possibly
	 *            generated, that applies to a type
	 */
	public GraphQLJsonMapper(String graphQLObjectsPackage, Map<Class<?>, Map<String, Field>> aliasFields,
			String schema) {
		if (schema == null) {
			throw new NullPointerException("The schema parameter may bot be null");
		}

		GraphQLDeserializationProblemHandler pbHandler = new GraphQLDeserializationProblemHandler(graphQLObjectsPackage,
				aliasFields, schema);
		objectMapper = JsonMapper.builder()//
				.addHandler(pbHandler)//
				.build();
		pbHandler.setObjectMapper(objectMapper);
	}

	// //////////////////////////////////////////////////////////////////////////////////////////
	// Below are the method that comes from the Jackson ObjectMapper
	// //////////////////////////////////////////////////////////////////////////////////////////

	/** @See {@link ObjectMapper#convertValue(Object, TypeReference)} */
	public Map<String, JsonNode> convertValue(JsonNode extensions, TypeReference<Map<String, JsonNode>> typeReference) {
		return objectMapper.convertValue(extensions, typeReference);
	}

	/** @See {@link ObjectMapper#convertValue(Object, Class)} */
	public <T> T convertValue(Object o, Class<T> clazz) {
		return objectMapper.convertValue(o, clazz);
	}

	/** @See {@link ObjectMapper#readValue(String, Class)} */
	public <T> T readValue(String msg, Class<T> subscriptionType) throws DatabindException, JacksonException {
		return objectMapper.readValue(msg, subscriptionType);
	}

	/** @See {@link ObjectMapper#readTree(String)} */
	public JsonNode readTree(String content) throws DatabindException, JacksonException {
		return objectMapper.readTree(content);
	}

	/** @See {@link ObjectMapper#treeToValue(TreeNode, Class)} */
	public <T> T treeToValue(TreeNode value, Class<T> clazz) throws JacksonException {
		return objectMapper.treeToValue(value, clazz);
	}

	/** @See {@link ObjectMapper#treeToValue(TreeNode, Class)} */
	public <T> T treeToValue(Map<?, ?> map, Class<T> clazz) throws JacksonException {
		// TODO Find a better way than map to json string, then json string to POJO object
		JsonNode node = objectMapper.valueToTree(map);
		return objectMapper.treeToValue(node, clazz);
	}

	/** @See {@link ObjectMapper#treeToValue(TreeNode, Class)} */
	public <T> T treeToValue(List<?> list, Class<T> clazz) throws JacksonException {
		// TODO Find a better way than list to json string, then json string to POJO object
		JsonNode node = objectMapper.valueToTree(list);
		return objectMapper.treeToValue(node, clazz);
	}

	/** @See {@link ObjectMapper#writeValueAsString(Object)} */
	public String writeValueAsString(Object o) throws JacksonException {
		return objectMapper.writeValueAsString(o);
	}

	/** @see ObjectMapper#valueToTree(Object) */
	public JsonNode valueToTree(Object o) {
		return objectMapper.valueToTree(o);
	}
}
