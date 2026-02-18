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

import com.graphql_java_generator.annotation.GraphQLDeprecatedResponseForRequestObject;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.TreeNode;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.BigIntegerNode;
import tools.jackson.databind.node.BooleanNode;
import tools.jackson.databind.node.DecimalNode;
import tools.jackson.databind.node.DoubleNode;
import tools.jackson.databind.node.FloatNode;
import tools.jackson.databind.node.IntNode;
import tools.jackson.databind.node.LongNode;
import tools.jackson.databind.node.NullNode;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.node.ShortNode;
import tools.jackson.databind.node.StringNode;

/**
 * This class handles various deserialization problems. It's used to manage unknown properties coming in the response
 * JSON. These unknown properties are alias defined in the GraphQL query.
 * 
 * @author etienne-sf
 */
public class GraphQLDeserializationProblemHandler extends DeserializationProblemHandler {
	private Logger logger = LoggerFactory.getLogger(GraphQLDeserializationProblemHandler.class);

	/** The Jackson {@link JsonMapper} that is specific to the GraphQL response deserialization */
	private JsonMapper objectMapper;

	/**
	 * This maps contains the {@link Field}, that matches each alias, of each GraphQL type. This allows a proper
	 * deserialization of each alias value returned in the json response
	 */
	final Map<Class<?>, Map<String, Field>> aliasFields;

	/** The package where the GraphQL objects have been generated */
	final String graphQLObjectsPackage;
	/**
	 * The schema name, for which this object mapper is created. This allows to retrieve the class, possibly generated,
	 * that applies to a type
	 */
	private String schema;

	public GraphQLDeserializationProblemHandler(String graphQLObjectsPackage,
			Map<Class<?>, Map<String, Field>> aliasFields, String schema) {

		this.graphQLObjectsPackage = graphQLObjectsPackage;
		this.aliasFields = aliasFields;
		this.schema = schema;
	}

	public void setObjectMapper(JsonMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser p, ValueDeserializer<?> deserializer,
			Object beanOrClass, String propertyName) {
		Map<String, Field> aliases = null;
		Field targetField = null;
		JsonDeserialize jsonDeserialize = null;
		Object value = null;

		if (logger.isTraceEnabled()) {
			logger.trace("Reading alias '" + propertyName + "' for " + beanOrClass.getClass().getName()); //$NON-NLS-1$ //$NON-NLS-2$
		}

		// Let's check if there is a CustomDeserializer for the field that this alias maps to
		if (aliasFields != null) {
			// If the deprecated response type is generated, then the bean's class is this deprecated response type,
			// instead of the query, mutation or subscription type. We have to check that, to retrieve the real
			// GraphQL type, and find its custom deserializer, if one was defined.
			Class<?> clazz = beanOrClass.getClass();
			GraphQLDeprecatedResponseForRequestObject annotation = clazz
					.getAnnotation(GraphQLDeprecatedResponseForRequestObject.class);
			if (annotation != null) {
				try {
					clazz = clazz.getClassLoader().loadClass(annotation.value());
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
			aliases = aliasFields.get(clazz);
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
				ValueDeserializer<?> graphQLDeserializer = jsonDeserialize.using().getDeclaredConstructor()
						.newInstance();
				value = graphQLDeserializer.deserialize(p, ctxt);
			} else {
				value = getAliasValue(p, targetField, p.readValueAsTree());
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | GraphQLRequestExecutionException | JacksonException
				| IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		// Let's call the setAliasValue of the target object, to set the alias's value we've just read
		String methodName = "setAliasValue"; //$NON-NLS-1$
		try {
			Method setAliasValue = beanOrClass.getClass().getMethod(methodName, String.class, Object.class);
			setAliasValue.invoke(beanOrClass, propertyName, value);
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException("Could not find or invoke the method '" + methodName + "' in the " //$NON-NLS-1$ //$NON-NLS-2$
					+ beanOrClass.getClass().getName() + " class", e); //$NON-NLS-1$
		}

		return true;
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
			String typename = ((StringNode) value.get("__typename")).asString(); //$NON-NLS-1$
			Class<?> clazz = GraphqlClientUtils.graphqlClientUtils.getClass(graphQLObjectsPackage, typename, schema);
			return objectMapper.treeToValue(value, clazz);

		}
		// Null
		else if (value instanceof NullNode) {
			return null;
		}
		// Enumerations
		else if (targetField != null && targetField.getType().isEnum()) {
			if (!(value instanceof StringNode)) {
				return new GraphQLRequestExecutionException(
						"The '" + targetField + "' is an enum, so the encoded json should be a StringNode. But it's a '" //$NON-NLS-1$ //$NON-NLS-2$
								+ value.getClass().getName() + "'"); //$NON-NLS-1$
			}
			return Enum.valueOf((Class<? extends Enum>) targetField.getType(), ((StringNode) value).stringValue());
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
		else if (value instanceof StringNode) {
			return ((StringNode) value).stringValue();
		} else {
			// For non managed type, we store an exception. This exception will be sent back to the reader, when it
			// tries to use this value. These non managed types can be:
			// 1) A custom scalar that returns a specific json type
			// 2) A bug
			throw new GraphQLRequestExecutionException(
					"Non managed json type. This can happen in two cases: the value for this alias is a GraphQL custom scalar (in which case you should use the getAliasCustomScalarValue method) or a bug"); //$NON-NLS-1$
		}
	}
}
