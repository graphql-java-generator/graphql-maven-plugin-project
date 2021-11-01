/**
 * 
 */
package com.graphql_java_generator.client;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ShortNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

/**
 * This class is a wrapper around an {@link ObjectMapper}. It allows the GraphQL plugin generated code to use its own
 * {@link ObjectMapper}, without interfering with the containing app. This insures that the containing app can configure
 * and use "its" {@link ObjectMapper} as it wants, and that the GraphQL plugin can use its own {@link ObjectMapper} with
 * its own configuration.<BR/>
 * This class is not Spring bean, as it is configured for each request, with the list of alias for this GraphQL request.
 * 
 * @author etienne-sf
 */
public class GraphQLObjectMapper {

	@Autowired
	ApplicationContext ctx;

	/** The Jackson {@link ObjectMapper} that is specific to the GraphQL response deserialization */
	final private ObjectMapper objectMapper;

	/**
	 * This maps contains the {@link Field}, that matches each alias, of each GraphQL type. This allows a proper
	 * deserialization of each alias value returned in the json response
	 */
	final private Map<Class<?>, Map<String, Field>> aliasFields;

	/** The package where the GraphQL objects have been generated */
	String graphQLObjectsPackage;

	/**
	 * This class handles various deserialization problems. It's used to manage unknown properties coming in the
	 * response JSON. These unknown properties are alias defined in the GraphQL query.
	 * 
	 * @author etienne-sf
	 */
	public class GraphQLDeserializationProblemHandler extends DeserializationProblemHandler {
		private Logger logger = LoggerFactory.getLogger(GraphQLDeserializationProblemHandler.class);

		@Override
		public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser p,
				JsonDeserializer<?> deserializer, Object beanOrClass, String propertyName) throws IOException {
			Map<String, Field> aliases = null;
			Field targetField = null;
			JsonDeserialize jsonDeserialize = null;
			Object value = null;

			if (logger.isTraceEnabled()) {
				logger.trace("Reading alias '" + propertyName + "' for " + beanOrClass.getClass().getName());
			}

			// Let's check if there is a CustomDeserializer for the field that this alias maps to
			if (aliasFields != null) {
				aliases = aliasFields.get(beanOrClass.getClass());
			}
			if (aliases != null) {
				targetField = aliases.get(propertyName);
			}
			if (targetField != null) {
				jsonDeserialize = targetField.getAnnotation(JsonDeserialize.class);
			}

			// If the plugin defined a CustomDeserializer, let's use it
			try {
				if (jsonDeserialize != null) {
					JsonDeserializer<?> graphQLDeserializer = jsonDeserialize.using().getDeclaredConstructor()
							.newInstance();
					value = graphQLDeserializer.deserialize(p, ctxt);
				} else {
					value = getAliasValue(p, targetField, p.readValueAsTree());
				}
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException
					| GraphQLRequestExecutionException e) {
				throw new RuntimeException(e.getMessage(), e);
			}

			// Let's call the setAliasValue of the target object, to set the alias's value we've just read
			String methodName = "setAliasValue";
			try {
				Method setAliasValue = beanOrClass.getClass().getMethod(methodName, String.class, Object.class);
				setAliasValue.invoke(beanOrClass, propertyName, value);
			} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new RuntimeException("Could not find or invoke the method '" + methodName + "' in the "
						+ beanOrClass.getClass().getName() + " class", e);
			}

			return true;
		}
	}

	/**
	 * Standard creator for the GraphQL {@link ObjectMapper}
	 * 
	 * @param graphQLObjectsPackage
	 *            The package where the GraphQL objects have been generated
	 */
	public GraphQLObjectMapper(String graphQLObjectsPackage, Map<Class<?>, Map<String, Field>> aliasFields) {
		objectMapper = new ObjectMapper();
		objectMapper.addHandler(new GraphQLDeserializationProblemHandler());

		this.graphQLObjectsPackage = graphQLObjectsPackage;
		this.aliasFields = aliasFields;
	}

	/**
	 * Parse a TreeNode, and return it as a value, according to the given classes
	 * 
	 * @param parser
	 *            The current json parser
	 * @param targetField
	 *            The field on which an alias has been set. This allows to retrieve the annotation on this field, to
	 *            know everything about it's properties, as defined in the GraphQL schema.<BR/>
	 *            It may be null, in which case enumeration values won't be properly deserialized.
	 * @param value
	 *            The value to parse
	 * @return The parsed value. That is, according to the above sample: a String, a List<String> or a
	 *         List<List<String>>
	 * @throws IOException
	 * @throws GraphQLRequestExecutionException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object getAliasValue(JsonParser parser, Field targetField, TreeNode value)
			throws IOException, GraphQLRequestExecutionException {
		if (value instanceof ArrayNode) {
			// value is a list. Let's do a recursive call for each of its item.
			List<Object> list = new ArrayList<>(((ArrayNode) value).size());
			for (TreeNode o : (ArrayNode) value) {
				list.add(getAliasValue(parser, targetField, o));
			}
			return list;
		} else if (value instanceof ObjectNode) {
			// This node is an object. In the GraphQL request, we asked for the __typename. Let's read it, to find out
			// which object of the GraphQL schema should be created.
			String typename = ((TextNode) value.get("__typename")).asText();
			Class<?> clazz;
			try {
				clazz = Class.forName(graphQLObjectsPackage + '.' + typename);
			} catch (ClassNotFoundException e) {
				throw new JsonMappingException(parser, e.getMessage(), e);
			}
			return objectMapper.treeToValue(value, clazz);

		}
		// Null
		else if (value instanceof NullNode) {
			return null;
		}
		// Enumerations
		else if (targetField != null && targetField.getType().isEnum()) {
			if (!(value instanceof TextNode)) {
				return new GraphQLRequestExecutionException(
						"The '" + targetField + "' is an enum, so the encoded json should be a TextNode. But it's a '"
								+ value.getClass().getName() + "'");
			}
			return Enum.valueOf((Class<? extends Enum>) targetField.getType(), ((TextNode) value).textValue());
		}
		// Booleans
		else if (value instanceof BooleanNode) {
			return ((BooleanNode) value).booleanValue();
		}
		// Decimal values
		else if (value instanceof DecimalNode) {
			return ((DecimalNode) value).decimalValue();
		}
		// Float and Double: GraphQL Float is mapped into a java Double
		else if (value instanceof DoubleNode) {
			return ((DoubleNode) value).doubleValue();
		} else if (value instanceof FloatNode) {
			return ((FloatNode) value).asDouble();
		}
		// Integers
		else if (value instanceof BigIntegerNode) {
			return ((BigIntegerNode) value).bigIntegerValue();
		} else if (value instanceof IntNode) {
			return ((IntNode) value).intValue();
		} else if (value instanceof LongNode) {
			return ((LongNode) value).longValue();
		} else if (value instanceof ShortNode) {
			return ((ShortNode) value).shortValue();
		}
		// Text
		else if (value instanceof TextNode) {
			return ((TextNode) value).textValue();
		} else {
			// For non managed type, we store an exception. This exception will be sent back to the reader, when it
			// tries to use this value. These non managed types can be:
			// 1) A custom scalar that returns a specific json type
			// 2) A bug
			throw new GraphQLRequestExecutionException(
					"Non managed json type. This can happen in two cases: the value for this alias is a GraphQL custom scalar (in which case you should use the getAliasCustomScalarValue method) or a bug");
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	////////////////// Below are the method that comes from the Jackson ObjectMapper
	////////////////////////////////////////////////////////////////////////////////////////////

	/** @See {@link ObjectMapper#convertValue(Object, TypeReference)} */
	public Map<String, JsonNode> convertValue(JsonNode extensions, TypeReference<Map<String, JsonNode>> typeReference) {
		return objectMapper.convertValue(extensions, typeReference);
	}

	/** @See {@link ObjectMapper#convertValue(Object, Class)} */
	public <T> T convertValue(Object o, Class<T> clazz) {
		return objectMapper.convertValue(o, clazz);
	}

	/** @See {@link ObjectMapper#readValue(String, Class)} */
	public <T> T readValue(String msg, Class<T> subscriptionType) throws JsonMappingException, JsonProcessingException {
		return objectMapper.readValue(msg, subscriptionType);
	}

	/** @See {@link ObjectMapper#treeToValue(TreeNode, Class)} */
	public <T> T treeToValue(TreeNode value, Class<T> clazz) throws JsonProcessingException {
		return objectMapper.treeToValue(value, clazz);
	}

	/** @See {@link ObjectMapper#writeValueAsString(Object)} */
	public String writeValueAsString(Object o) throws JsonProcessingException {
		return objectMapper.writeValueAsString(o);
	}
}
