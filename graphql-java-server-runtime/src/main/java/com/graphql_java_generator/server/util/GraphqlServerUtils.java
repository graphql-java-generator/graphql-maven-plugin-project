/**
 * 
 */
package com.graphql_java_generator.server.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.reactivestreams.Publisher;
import org.springframework.graphql.execution.ClassNameTypeResolver;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.annotation.GraphQLEnumType;
import com.graphql_java_generator.annotation.GraphQLIgnore;
import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLInterfaceType;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLObjectType;
import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.annotation.GraphQLUnionType;
import com.graphql_java_generator.util.GraphqlUtils;

import reactor.core.publisher.Flux;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

/**
 * A class that contains utility method for the server mode
 * 
 * @author etienne-sf
 */
@Component
public class GraphqlServerUtils {

	public static GraphqlServerUtils graphqlServerUtils = new GraphqlServerUtils();
	private static GraphqlUtils graphqlUtils = new GraphqlUtils();

	/**
	 * Implementation of a {@link ClassNameTypeResolver} to manage the possible prefix and suffix on the generated
	 * POJOs.
	 * 
	 * @param cls
	 *            The class which name must be retrieved
	 * @return The GraphQL type name that matches this class
	 */
	public String classNameExtractor(Class<?> cls) {

		GraphQLEnumType graphQLEnumType = cls.getAnnotation(GraphQLEnumType.class);
		if (graphQLEnumType != null) {
			return graphQLEnumType.value();
		}

		GraphQLInterfaceType graphQLInterfaceType = cls.getAnnotation(GraphQLInterfaceType.class);
		if (graphQLInterfaceType != null) {
			return graphQLInterfaceType.value();
		}

		GraphQLObjectType graphQLObjectType = cls.getAnnotation(GraphQLObjectType.class);
		if (graphQLObjectType != null) {
			return graphQLObjectType.value();
		}

		GraphQLUnionType graphQLUnionType = cls.getAnnotation(GraphQLUnionType.class);
		if (graphQLUnionType != null) {
			return graphQLUnionType.value();
		}

		// The default is to return the simple class name
		return cls.getSimpleName();
	}

	/**
	 * This method returns a GraphQL argument into the relevant Java object, within a data fetcher, from what has been
	 * parsed by the graphql-java engine from the incoming JSON request
	 * 
	 * @param <T>
	 *            The class expected to be returned
	 * @param jsonParsedValue
	 *            The value, read from the JSON in the GraphQL request. Only the part of the JSON map, related to the
	 *            expected class is sent. It can be:
	 *            <UL>
	 *            <LI>A {@link Map}. This map will be transformed into an input object, as defined in the GraphQL
	 *            schema, from the Map that has been read from the JSON object sent to the server.</LI>
	 *            <LI>A {@link List}. In this case, returns a list of instances of the given clazz type.</LI>
	 *            <LI>Otherwise, the value is a scalar. At this stage, Custom Scalars have already been transformed into
	 *            the relevant Java Type. So it must be a standard scalar. It is then mapped to the asked java type</LI>
	 *            </UL>
	 * @param graphQLTypeName
	 *            The name of the GraphQL type, as defined in the GraphQL schema. This can be guessed from the given
	 *            class for input types and objects, but not for scalars. So it must be provided.
	 * @param javaTypeForIDType
	 *            Value of the plugin parameter of the same name. This is necessary to properly manage fields of the ID
	 *            GraphQL type, which must be transformed to this java type. This is useful only when mapping into input
	 *            types.
	 * @param clazz
	 *            The class of the expected type. A new instance of this type will be returned, with its fields having
	 *            been set by this method from the value in the map
	 * @return An instance of the expected class. If the map is null, null is returned. Of the map is empty, anew
	 *         instance is returned, with all its fields are left empty
	 */
	@SuppressWarnings("unchecked")
	public Object getArgument(Object jsonParsedValue, String graphQLTypeName, String javaTypeForIDType,
			Class<?> clazz) {
		if (jsonParsedValue == null) {
			return null;
		} else if (jsonParsedValue instanceof List<?>) {
			// We've a list. Let's loop inside its items
			List<Object> objects = new ArrayList<>();
			for (Object o : (List<Object>) jsonParsedValue) {
				objects.add(getArgument(o, graphQLTypeName, javaTypeForIDType, clazz));
			}
			return objects;
		} else if (jsonParsedValue instanceof Map<?, ?>) {
			// We have a Map. Its keys MUST be the attributes for the given class (clazz)
			Map<String, Object> map = (Map<String, Object>) jsonParsedValue;
			Object t;

			try {
				t = clazz.getConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new RuntimeException("Error while creating a new instance of  '" + clazz.getName() + " class", e);
			}

			for (String key : map.keySet()) {
				GraphQLScalar graphQLScalar = null;
				GraphQLNonScalar graphQLNonScalar = null;
				Method setter = null;

				for (Field field : clazz.getDeclaredFields()) {
					graphQLScalar = field.getAnnotation(GraphQLScalar.class);
					graphQLNonScalar = field.getAnnotation(GraphQLNonScalar.class);
					if ((graphQLScalar != null && graphQLScalar.fieldName().equals(key))
							|| (graphQLNonScalar != null && graphQLNonScalar.fieldName().equals(key))) {
						setter = graphqlUtils.getSetter(clazz, field);
						break;
					}
				}
				if (graphQLScalar == null && graphQLNonScalar == null) {
					throw new RuntimeException(
							"Found no GraphQL field of name '" + key + "' in class " + clazz.getName());
				}

				Object value;

				if (graphQLScalar != null) {
					value = getArgument(map.get(key), graphQLScalar.graphQLTypeSimpleName(), javaTypeForIDType,
							graphQLScalar.javaClass());
				} else if (graphQLNonScalar != null) {
					value = getArgument(map.get(key), graphQLNonScalar.graphQLTypeSimpleName(), javaTypeForIDType,
							graphQLNonScalar.javaClass());
				} else {
					throw new RuntimeException("Internal error: the field '" + clazz.getName() + "." + key
							+ "' should have one of these annotations: GraphQLScalar or GraphQLScalar");
				}

				graphqlUtils.invokeMethod(setter, t, value);
			}
			return t;
		}
		// We don't have a collection, so we have a single value to get
		else if (clazz.isEnum()) {
			if (!(jsonParsedValue instanceof String)) {
				throw new RuntimeException("The " + clazz.getName() + " class is an enum, but the provided value is '"
						+ jsonParsedValue + "' which should be a String, to be mapped to the relevant enum value");
			}
			// This object is a String, that we must map to its enum value
			Method fromGraphQlValue = graphqlUtils.getMethod("fromGraphQlValue", clazz, String.class);
			return graphqlUtils.invokeMethod(fromGraphQlValue, null, (String) jsonParsedValue);
		} else if (graphQLTypeName.equals("ID")) {
			// ID is particular animal: it's by default managed as a UUID (we're on server
			// side). And this can be
			// overridden by the javaTypeForIDType plugin parameter.
			// If the type is unknown, the String value is returned.
			if (javaTypeForIDType == null || javaTypeForIDType.equals("")) {
				return UUID.fromString((String) jsonParsedValue);
			} else if (javaTypeForIDType.equals("java.util.UUID")) {
				return UUID.fromString((String) jsonParsedValue);
			} else if (javaTypeForIDType.equals("java.lang.String")) {
				return jsonParsedValue;
			} else if (javaTypeForIDType.equals("java.lang.Long")) {
				return Long.parseLong((String) jsonParsedValue);
			} else {
				throw new RuntimeException(
						"Non managed value for the plugin parameter 'javaTypeForIDType': '" + javaTypeForIDType + "'");
			}
		} else if (clazz.isInstance(jsonParsedValue)) {
			// The job is already done
			return jsonParsedValue;
		} else if (jsonParsedValue instanceof String) {
			// If the given value is a String, let's try to map it to the target class
			if (clazz == String.class) {
				return jsonParsedValue;
			} else if (clazz == UUID.class) {
				return UUID.fromString((String) jsonParsedValue);
			} else if (clazz == Boolean.class) {
				return jsonParsedValue.equals("true");
			} else if (clazz == Long.class) {
				return Long.parseLong((String) jsonParsedValue);
			} else if (clazz == Integer.class) {
				return Integer.parseInt((String) jsonParsedValue);
			} else if (clazz == Double.class) {
				return Double.parseDouble((String) jsonParsedValue);
			} else if (clazz == Float.class) {
				return Float.parseFloat((String) jsonParsedValue);
			}
		}

		// Too bad...
		throw new RuntimeException("Can't transform the jsonParsedValue (" + jsonParsedValue.getClass().getName()
				+ ") into a " + clazz.getName());
	}

	/**
	 * Returns the given enumValue transformed a String, based on the String representation for this enumValues.<br/>
	 * This method is based on the <code>graphQlValue()</code> generated for each enum POJO.
	 * 
	 * @param enumValue
	 *            May be null, a value of an enum POJO generated from the GraphQL schema, a list of values of any depth
	 *            (for instance [[[Episode]]] would be a list of Episode enums of depth 3). The item of the list may be
	 *            either enums (generated by the plugin) or Optional<? extends Enum> (where the content of the Optional
	 *            is an enum generated by the plugin)
	 * @return The same kind of list, but all enum values are replaced by the relevant String representation, based on
	 *         the GraphQL schema.
	 */
	public Object enumValueToString(Object enumValue) {
		if (enumValue == null) {
			// Case : enumValue is a null value
			return null;
		} else if (enumValue instanceof Optional && !((Optional<?>) enumValue).isPresent()) {
			// Case : enumValue is an empty Optional
			return Optional.empty();
		} else if (enumValue instanceof Optional && ((Optional<?>) enumValue).isPresent()) {
			// Case : enumValue is a non empty Optional
			return Optional.of(enumValueToString(((Optional<?>) enumValue).get()));
		} else if (enumValue instanceof List) {
			// Case : enumValue is a list
			return ((List<?>) enumValue).stream().map(v -> enumValueToString(v)).collect(Collectors.toList());
		} else if (enumValue instanceof Publisher) {
			// For a flux, we must transform each returned item
			return Flux.from((Publisher<?>) enumValue).map(v -> enumValueToString(v));
		} else {
			// Case : enumValue is a value. It must be a value of an enum generated by the plugin. That is: it must have
			// a graphQlValue method.
			Method method = null;
			try {
				method = enumValue.getClass().getMethod("graphQlValue");
			} catch (NoSuchMethodException | SecurityException e) {
				throw new IllegalArgumentException(
						"The given value may be either null, an Optional, a List or an enum that has the 'graphQlValue' method'",
						e);
			}
			try {
				return method.invoke(enumValue);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}

	/**
	 * Maps an object received from spring-graphql to an ObjectNode (or a list of ObjectNode of any depth). This is used
	 * to manage the JSON custom scalar: it's mapped to the java type {@link ObjectNode}. But spring-graphql can't map
	 * an incoming parameter to {@link ObjectNode}, as it has no default constructor. One has to use an
	 * {@link ObjectMapper} and read the incoming object to do so: this is the aim of this method.<br/>
	 * This method expects that the provided data is correct. For instance, if the expected return type is in GraphQL
	 * [Type1], then:
	 * <ul>
	 * <li>o is an array.</li>
	 * <li>Each item of the array is a {@link Map} that maps to the GraphQL <code>Type1</code>.</li>
	 * <li>The custom scalar fields, if any, have been properly resolved by spring-graphql before, and the relevant
	 * value is available in the received Object</li>
	 * </ul>
	 * Note: this method manages GraphQL field arguments. So the given <code>clazz</code> parameter is expected to a
	 * list, an enum, a scalar or an input type. It may not be an interface, an output type or a union.
	 * 
	 * @param o
	 *            The object (or subobject, if we've already recursed into this method) that should be mapped into the
	 *            given GraphQL type, with the correct listDepth
	 * @param clazz
	 *            The target class for the o object. If it maps to a GraphQL type defined in the GraphQL schema, this
	 *            class should be one generated by the plugin so that the {@link GraphQLInputType} annotation is
	 *            properly set.
	 * @param listDepth
	 *            The depth of the list for the given <code>o</code> object: 0 if this field is not a list, and 2, for
	 *            instance, if the field's type is "[[Int]]"
	 * @param entityName
	 *            The name of the entity which field is initially expected (may be different from the clazz in case of
	 *            nested types). It is used only to display a more explicit error message.
	 * @param fieldName
	 *            The name of the field is initially expected. It is one of the entityName fields. It is used only to
	 *            display a more explicit error message.
	 * @return
	 */
	public Object mapArgumentToRelevantPojoOrScalar(Object o, Class<?> clazz, int listDepth, String entityName,
			String argumentName) {
		final JsonMapper jsonMapper = JsonMapper.builder().build();
		Field classField;
		Object value;
		int fieldListDepth;

		GraphQLInputType graphQLInputType = clazz.getAnnotation(GraphQLInputType.class);

		if (o == null) {
			return null;
		} else if (listDepth > 0) {
			// We expect a list
			if (o instanceof List) {
				return ((List<?>) o).stream().map(
						item -> mapArgumentToRelevantPojoOrScalar(item, clazz, listDepth - 1, entityName, argumentName))
						.collect(Collectors.toList());
			} else {
				throw new RuntimeException(
						"The received object has not the proper level of listDepth: the expected listDepth is higher than the received one, when trying to map to "
								+ clazz.getName() + " (exception raised while managing the " + entityName + "."
								+ argumentName + " parameter).");
			}
		} else if (graphQLInputType != null) {
			// A GraphQL type is expected
			if (!(o instanceof Map)) {
				throw new RuntimeException("Could not map the received object to a " + clazz.getName()
						+ ", as th received object is of type: " + o.getClass().getName()
						+ " (exception raised while managing the " + entityName + "." + argumentName + " parameter).");
			}

			// Let's create an instance for this object
			Object targetObject;
			try {
				targetObject = clazz.getConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e1) {
				throw new RuntimeException("Error while creating an instance of " + clazz.getName() + ": "
						+ e1.getMessage() + " (exception raised while managing the " + entityName + "." + argumentName
						+ " parameter)", e1);
			}

			// Then set the received data into the relevant fields for this new object
			for (Object key : ((Map<?, ?>) o).keySet()) {
				if (!(key instanceof String)) {
					throw new RuntimeException("The '" + key.toString() + "' is of type " + key.getClass().getName()
							+ " instead of String (exception raised while managing the " + entityName + "."
							+ argumentName + " parameter)");
				}

				try {
					classField = clazz.getDeclaredField(graphqlUtils.getJavaName((String) key));
				} catch (NoSuchFieldException | SecurityException e2) {
					throw new RuntimeException("Error while searching for the clazz field " + clazz.getName() + "."
							+ (String) key + ": " + e2.getMessage() + " (exception raised while managing the "
							+ entityName + "." + argumentName + " parameter)", e2);
				}

				Object receivedValue = ((Map<?, ?>) o).get(key);

				if (receivedValue != null) {

					GraphQLIgnore ignoreAnnotation = classField.getAnnotation(GraphQLIgnore.class);
					GraphQLScalar scalarAnnotation = classField.getAnnotation(GraphQLScalar.class);
					GraphQLNonScalar nonScalarAnnotation = classField.getAnnotation(GraphQLNonScalar.class);

					if (scalarAnnotation != null) {
						fieldListDepth = scalarAnnotation.listDepth();
					} else if (nonScalarAnnotation != null) {
						fieldListDepth = nonScalarAnnotation.listDepth();
					} else if (ignoreAnnotation != null) {
						// Strange: we received a field that is ignored (and should not transit in a GraphQL message)
						throw new RuntimeException("Can't map the " + clazz.getName() + "." + classField.getName()
								+ " as it is marked with one of the GraphQLIgnore annotation (exception raised while managing the "
								+ entityName + "." + argumentName + " parameter)");
					} else {
						// The field is neither a scalar, nor non scalar, and is not ignored. This should not happen.
						throw new RuntimeException("[Internal error] The " + clazz.getName() + "."
								+ classField.getName()
								+ " should be marked with one of these annotations: GraphQLScalar, GraphQLNonScalar or GraphQLIgnore (exception raised while managing the "
								+ entityName + "." + argumentName + " parameter)");
					}

					// Let's recurse once, to get the value to set to this field.
					value = mapArgumentToRelevantPojoOrScalar(receivedValue, classField.getType(), fieldListDepth,
							entityName, argumentName);

					GraphqlUtils.graphqlUtils.invokeSetter(targetObject, classField, value);
				}
			} // for(key)

			// Let's return the built object
			return targetObject;

		} //
		else
		// List and Object are managed here above. Starting from here, we manage scalars.
		// This method has been done to manage the Object and JSON scalars, that need special management ways. Let's do
		// that.
		if (clazz.equals(ObjectNode.class)) {
			// ObjectNode has no default setter. So spring-graphql can't map incoming data to an ObjectNode. We have to
			// do it 'manually':
			return jsonMapper.valueToTree(o);
		} else if (clazz.isEnum()) {
			// Special treatment for enums, as they are received as string
			if (o instanceof String) {
				try {
					return clazz.getMethod("valueOf", String.class).invoke(null, (String) o);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					throw new RuntimeException(
							"Erreur when mapping the received value (" + o + ") to the class " + clazz.getName()
									+ " as an enum: " + e.getMessage() + " (exception raised while managing the "
									+ entityName + "." + argumentName + " parameter)",
							e);
				}
			} else {
				throw new RuntimeException("The received value (" + o + ") should be a string, to be mapped to"
						+ clazz.getName() + "but is an instance of " + o.getClass().getName()
						+ " (exception raised while managing the " + entityName + "." + argumentName + " parameter)");
			}
		} else {
			// For other cases (including Object), there is no transformation to do: we directly send what's been
			// received
			return o;
		}
	}
}
