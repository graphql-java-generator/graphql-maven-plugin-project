/**
 * 
 */
package com.graphql_java_generator.server;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLScalar;

/**
 * @author EtienneSF
 */
@Component
public class GraphqlUtils {

	/**
	 * Returns the given name in PascalCase. For instance: theName -> TheName
	 * 
	 * @param name
	 * @return
	 */
	String getPascalCase(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	/**
	 * This method returns a GraphQL input object, as defined in the GraphQL schema, from the Map that has been read
	 * from the JSON object sent to the server.
	 * 
	 * @param <T>
	 *            The class expected to be returned
	 * @param map
	 *            The map, read from the JSON in the GraphQL request. Only the part of the map, related to the expected
	 *            class is sent.
	 * @param t
	 *            An empty instance of the expected type. This instance's fields will be set by this method, from the
	 *            value in the map
	 * @return An instance of the expected class. If the map is null or empty, all the fields are left empty
	 */
	<T> T getInputObject(Map<String, Object> map, T t) {
		Field field;

		for (String key : map.keySet()) {
			try {
				field = t.getClass().getDeclaredField(key);
			} catch (NoSuchFieldException | SecurityException e) {
				throw new RuntimeException(
						"Error while reading '" + key + "' field for the " + t.getClass().getName() + " class", e);
			}

			Method setter = getSetter(t, field);

			GraphQLScalar graphQLScalar = field.getAnnotation(GraphQLScalar.class);
			GraphQLNonScalar graphQLNonScalar = field.getAnnotation(GraphQLNonScalar.class);

			if (graphQLScalar != null) {
				// We have a Scalar, here. Let's look at all known scalars
				if (graphQLScalar.graphqlType() == UUID.class) {
					invokeMethod(setter, t, UUID.fromString((String) map.get(key)));
				} else if (graphQLScalar.graphqlType() == String.class || graphQLScalar.graphqlType() == Boolean.class
						|| graphQLScalar.graphqlType() == Integer.class || graphQLScalar.graphqlType() == Float.class
						|| graphQLScalar.graphqlType().isEnum()) {
					invokeMethod(setter, t, map.get(key));
				} else {
					throw new RuntimeException(
							"Non managed type when reading the input map: '" + graphQLScalar.graphqlType().getName());
				}
			} else if (graphQLNonScalar != null) {
				// We got a non scalar field. So we expect a map, which content will map to the fields of the target
				// field.
				if (!(map.get(key) instanceof Map<?, ?>)) {
					throw new RuntimeException(
							"The value for the field '" + t.getClass().getName() + "." + key + " should be a map");
				}
				@SuppressWarnings("unchecked")
				Map<String, Object> subMap = (Map<String, Object>) map.get(key);

				Object value;
				try {
					value = graphQLNonScalar.graphqlType().newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					throw new RuntimeException("Error when trying to create an instance of '"
							+ graphQLNonScalar.graphqlType().getName() + "'");
				}
				invokeMethod(setter, t, getInputObject(subMap, value));
			} else {
				throw new RuntimeException("Internal error: the field '" + t.getClass().getName() + "." + key
						+ "' should have one of these annotations: GraphQLScalar or GraphQLScalar");
			}
		}
		return t;
	}

	/**
	 * Retrieves the setter for the given field on the given field
	 * 
	 * @param <T>
	 * @param t
	 * @param field
	 * @return
	 */
	<T> Method getSetter(T t, Field field) {
		String setterMethodName = "set" + getPascalCase(field.getName());
		try {
			return t.getClass().getDeclaredMethod(setterMethodName, field.getType());
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(
					"The setter '" + setterMethodName + "' is missing in " + t.getClass().getName() + " class", e);
		} catch (SecurityException e) {
			throw new RuntimeException("Error while accessing to the setter '" + setterMethodName + "' in "
					+ t.getClass().getName() + " class", e);
		}
	}

	/**
	 * Retrieves the asked method, from its name, class and parameters. This method hides the exception that could be
	 * thrown, into a {@link RuntimeException}
	 * 
	 * @param <T>
	 * @param t
	 * @param field
	 * @return
	 * @throws RuntimeException
	 *             When an exception occurs while getting the method
	 */
	Method getMethod(String methodName, Class<?> clazz, Class<?>... parameterTypes) {
		try {
			return clazz.getDeclaredMethod(methodName, parameterTypes);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(
					"Could not get the method '" + methodName + "' in the " + clazz.getName() + " class", e);
		}
	}

	/**
	 * Invoke the given setter on the given object, with the given value. This method hides the exception that could be
	 * thrown, into a {@link RuntimeException}
	 * 
	 * @param method
	 * @param o
	 * @param value
	 * @throws RuntimeException
	 *             When an exception occurs while accessing the setter
	 */
	Object invokeMethod(Method method, Object o, Object... args) {
		try {
			return method.invoke(o, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException("Error when executing the method '" + method.getName() + "' is missing in "
					+ o.getClass().getName() + " class", e);
		}
	}
}