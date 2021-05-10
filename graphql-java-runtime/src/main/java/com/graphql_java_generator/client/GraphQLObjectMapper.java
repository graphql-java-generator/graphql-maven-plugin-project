/**
 * 
 */
package com.graphql_java_generator.client;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * its own configuration.
 * 
 * @author etienne-sf
 */
@Component
public class GraphQLObjectMapper {

	@Autowired
	ApplicationContext ctx;

	/** The Jackson {@link ObjectMapper} that is specific to the GraphQL response deserialization */
	private ObjectMapper objectMapper;

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
			if (logger.isTraceEnabled()) {
				logger.trace("Reading alias '" + propertyName + "' for " + beanOrClass.getClass().getName());
			}
			TreeNode tree = p.readValueAsTree();
			invokerSetter("setAliasParsedValue", beanOrClass, propertyName, getAliasValue(p, tree), Object.class);
			invokerSetter("setAliasTreeNodeValue", beanOrClass, propertyName, tree, TreeNode.class);
			return true;
		}

		private void invokerSetter(String methodName, Object bean, String propertyName, Object value, Class<?> clazz) {
			try {
				Method setAliasParsedValue = bean.getClass().getDeclaredMethod(methodName, String.class, clazz);
				setAliasParsedValue.invoke(bean, propertyName, value);

			} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new RuntimeException("Could not find or invoke the method '" + methodName + "' in the "
						+ bean.getClass().getName() + " class", e);
			}
		}
	}

	@PostConstruct
	public void init() {
		// This method code is split in two calls, as the graphQLPackage (only) is overridden in unit test
		initObjectMapper();
		initGraphQLPackage();
	}

	/**
	 * create and configure the Jackson {@link ObjectMapper} that is specific to the GraphQL response deserialization
	 */
	void initObjectMapper() {
		objectMapper = new ObjectMapper();
		objectMapper.addHandler(new GraphQLDeserializationProblemHandler());
	}

	/** Determine the package, where the GraphQL objects have been generated */
	void initGraphQLPackage() {
		Map<String, GraphQLRequestObject> requestObjects = ctx.getBeansOfType(GraphQLRequestObject.class);
		graphQLObjectsPackage = requestObjects.values().iterator().next().getClass().getPackage().getName();
	}

	/**
	 * Parse a TreeNode, and return it as a value, according to the given classes
	 * 
	 * @param parser
	 *            The current json parser
	 * @param value
	 *            The value to parse
	 * @return The parsed value. That is, according to the above sample: a String, a List<String> or a
	 *         List<List<String>>
	 * @throws IOException
	 */
	public Object getAliasValue(JsonParser parser, TreeNode value) throws IOException {
		if (value instanceof ArrayNode) {
			// value is a list. Let's do a recursive call for each of its item.
			List<Object> list = new ArrayList<>(((ArrayNode) value).size());
			for (TreeNode o : (ArrayNode) value) {
				list.add(getAliasValue(parser, o));
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
			return new GraphQLRequestExecutionException(
					"Non managed json type. This can happen in two cases: the value for this alias is a GraphQL custom scalar (in which case you should use the getAliasCustomScalarValue method) or a bug");
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	////////////////// Below are the method that comes from the Jackson ObjectMapper
	////////////////////////////////////////////////////////////////////////////////////////////

	/** @See {@link ObjectMapper#convertValue(Object, Class)} */
	public Map<String, JsonNode> convertValue(JsonNode extensions, TypeReference<Map<String, JsonNode>> typeReference) {
		return objectMapper.convertValue(extensions, typeReference);
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
	public Object writeValueAsString(Object o) throws JsonProcessingException {
		return objectMapper.writeValueAsString(o);
	}
}
