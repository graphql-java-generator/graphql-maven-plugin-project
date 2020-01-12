/**
 * 
 */
package com.graphql_java_generator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLScalar;

/**
 * @author EtienneSF
 */
@Component
public class GraphqlUtils {

	Pattern graphqlNamePattern = Pattern.compile("^[_A-Za-z][_0-9A-Za-z]*$");

	/**
	 * maps for all scalers, when they are mandatory. The key is the type name. The value is the class to use in the
	 * java code
	 */
	List<Class<?>> scalars = new ArrayList<>();

	public GraphqlUtils() {
		// Add of all predefined scalars
		scalars.add(String.class);
		scalars.add(int.class);
		scalars.add(Integer.class);
		scalars.add(float.class);
		scalars.add(Float.class);
		scalars.add(boolean.class);
		scalars.add(Boolean.class);
	}

	/**
	 * Returns the given name in PascalCase. For instance: theName -> TheName
	 * 
	 * @param name
	 * @return
	 */
	public String getPascalCase(String name) {
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
	public <T> T getInputObject(Map<String, Object> map, Class<T> clazz) {
		T t;
		Field field;

		try {
			t = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Error while creating a new instance of  '" + clazz.getName() + " class", e);
		}

		for (String key : map.keySet()) {
			try {
				field = clazz.getDeclaredField(key);
			} catch (NoSuchFieldException | SecurityException e) {
				throw new RuntimeException(
						"Error while reading '" + key + "' field for the " + clazz.getName() + " class", e);
			}

			Method setter = getSetter(clazz, field);

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
							"The value for the field '" + clazz.getName() + "." + key + " should be a map");
				}
				@SuppressWarnings("unchecked")
				Map<String, Object> subMap = (Map<String, Object>) map.get(key);
				invokeMethod(setter, t, getInputObject(subMap, graphQLNonScalar.graphqlType()));
			} else {
				throw new RuntimeException("Internal error: the field '" + clazz.getName() + "." + key
						+ "' should have one of these annotations: GraphQLScalar or GraphQLScalar");
			}
		}
		return t;
	}

	/**
	 * This method returns a list of instances of the given class, from a list of {@link Map}. This is used on
	 * server-side, to map the input read from the JSON into the InputType that have been declared in the GraphQL
	 * schema.
	 * 
	 * @param <T>
	 * @param list
	 * @param clazz
	 * @return
	 */
	public <T> List<T> getListInputObjects(List<Map<String, Object>> list, Class<T> clazz) {
		List<T> ret = new ArrayList<>(list.size());

		for (Map<String, Object> map : list) {
			ret.add(getInputObject(map, clazz));
		}

		return ret;
	}

	/**
	 * Retrieves the setter for the given field on the given field
	 * 
	 * @param <T>
	 * @param t
	 * @param field
	 * @return
	 */
	public <T> Method getSetter(Class<T> clazz, Field field) {
		String setterMethodName = "set" + getPascalCase(field.getName());
		try {
			return clazz.getDeclaredMethod(setterMethodName, field.getType());
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(
					"The setter '" + setterMethodName + "' is missing in " + clazz.getName() + " class", e);
		} catch (SecurityException e) {
			throw new RuntimeException(
					"Error while accessing to the setter '" + setterMethodName + "' in " + clazz.getName() + " class",
					e);
		}
	}

	/**
	 * Retrieves the getter for the given field on the given field
	 * 
	 * @param <T>
	 * @param t
	 * @param field
	 * @return
	 */
	public <T> Method getGetter(Class<T> clazz, Field field) {
		String setterMethodName = "get" + getPascalCase(field.getName());
		try {
			Method method = clazz.getDeclaredMethod(setterMethodName);

			// The return type must be the same as the field's class
			if (field.getType() != method.getReturnType()) {
				throw new RuntimeException("The getter '" + setterMethodName + "' and the field '" + field.getName()
						+ "' of the class " + clazz.getName() + " should be of the same type");
			}

			return method;
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(
					"The getter '" + setterMethodName + "' is missing in " + clazz.getName() + " class", e);
		} catch (SecurityException e) {
			throw new RuntimeException(
					"Error while accessing to the getter '" + setterMethodName + "' in " + clazz.getName() + " class",
					e);
		}
	}

	/**
	 * Invoke the getter for the given field name, on the given object. All check exceptions are hidden in a
	 * {@link RuntimeException}
	 * 
	 * @param object
	 * @param fieldName
	 * @return the field's value for the given object
	 * @throws RuntimeException
	 *             If any exception occurs
	 */
	public Object invokeGetter(Object object, String fieldName) {
		try {
			Field field = object.getClass().getDeclaredField(fieldName);
			Method getter = getGetter(object.getClass(), field);
			return getter.invoke(object);
		} catch (NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException("Error while invoking to the getter for the field '" + fieldName
					+ "' in the class " + object.getClass().getName() + " class", e);
		}
	}

	/**
	 * Invoke the setter for the given field name, on the given object. All check exceptions are hidden in a
	 * {@link RuntimeException}
	 *
	 * @param object
	 * @param fieldName
	 * @param value
	 * @throws RuntimeException
	 *             If any exception occurs
	 */
	public void invokeSetter(Object object, String fieldName, Object value) {
		try {
			Field field = object.getClass().getDeclaredField(fieldName);
			Method setter = getSetter(object.getClass(), field);
			setter.invoke(object, value);
		} catch (NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException("Error while invoking to the getter for the field '" + fieldName
					+ "' in the class " + object.getClass().getName() + " class", e);
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