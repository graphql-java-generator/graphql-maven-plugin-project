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

	/** This singleton is usable in default method, within interfaces */
	public static GraphqlUtils graphqlUtils = new GraphqlUtils();

	Pattern graphqlNamePattern = Pattern.compile("^[_A-Za-z][_0-9A-Za-z]*$");

	/**
	 * maps for all scalers, when they are mandatory. The key is the type name. The value is the class to use in the
	 * java code
	 */
	List<Class<?>> scalars = new ArrayList<>();

	/**
	 * The list of Java keywords. This keyword may not be used as java identifier, within java code (for instance for
	 * class name, field name...).<BR/>
	 * If a GraphQL identifier is one of these keyword, it will be prefixed by {@link #JAVA_KEYWORD_PREFIX} in the
	 * generated code.
	 */
	private List<String> javaKeywords = new ArrayList<>();

	public static Character JAVA_KEYWORD_PREFIX = '_';

	public GraphqlUtils() {
		// Add of all predefined scalars
		scalars.add(String.class);
		scalars.add(int.class);
		scalars.add(Integer.class);
		scalars.add(float.class);
		scalars.add(Float.class);
		scalars.add(boolean.class);
		scalars.add(Boolean.class);

		// List all java reserved keywords.
		javaKeywords.add("abstract");
		javaKeywords.add("assert");
		javaKeywords.add("boolean");
		javaKeywords.add("break");
		javaKeywords.add("byte");
		javaKeywords.add("case");
		javaKeywords.add("catch");
		javaKeywords.add("char");
		javaKeywords.add("class");
		javaKeywords.add("const");
		javaKeywords.add("continue");
		javaKeywords.add("default");
		javaKeywords.add("do");
		javaKeywords.add("double");
		javaKeywords.add("else");
		javaKeywords.add("enum");
		javaKeywords.add("extends");
		javaKeywords.add("final");
		javaKeywords.add("finally");
		javaKeywords.add("float");
		javaKeywords.add("for");
		javaKeywords.add("goto");
		javaKeywords.add("if");
		javaKeywords.add("implements");
		javaKeywords.add("import");
		javaKeywords.add("instanceof");
		javaKeywords.add("int");
		javaKeywords.add("interface");
		javaKeywords.add("long");
		javaKeywords.add("native");
		javaKeywords.add("new");
		javaKeywords.add("package");
		javaKeywords.add("private");
		javaKeywords.add("protected");
		javaKeywords.add("public");
		javaKeywords.add("return");
		javaKeywords.add("short");
		javaKeywords.add("static");
		javaKeywords.add("strictfp");
		javaKeywords.add("super");
		javaKeywords.add("switch");
		javaKeywords.add("synchronized");
		javaKeywords.add("this");
		javaKeywords.add("throw");
		javaKeywords.add("throws");
		javaKeywords.add("transient");
		javaKeywords.add("try");
		javaKeywords.add("void");
		javaKeywords.add("volatile");
		javaKeywords.add("while");
	}

	/**
	 * Convert the given name, to a camel case name. Currenly very simple : it puts the first character in lower case.
	 * 
	 * @return
	 */
	public String getCamelCase(String name) {
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}

	/**
	 * Convert the given name, which is supposed to be in camel case (for instance: thisIsCamelCase) to a pascal case
	 * string (for instance: ThisIsCamelCase).
	 * 
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
	 * @return An instance of the expected class. If the map is null, null is returned. Of the map is empty, anew
	 *         instance is returned, with all its fields are left empty
	 */
	public <T> T getInputObject(Map<String, Object> map, Class<T> clazz) {
		if (map == null) {
			return null;
		} else {
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
					if (graphQLScalar.javaClass() == UUID.class) {
						invokeMethod(setter, t, UUID.fromString((String) map.get(key)));
					} else {
						invokeMethod(setter, t, map.get(key));
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
					invokeMethod(setter, t, getInputObject(subMap, graphQLNonScalar.javaClass()));
				} else {
					throw new RuntimeException("Internal error: the field '" + clazz.getName() + "." + key
							+ "' should have one of these annotations: GraphQLScalar or GraphQLScalar");
				}
			}
			return t;
		}
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
	 * Invoke the setter for the given field, on the given object. All check exceptions are hidden in a
	 * {@link RuntimeException}
	 *
	 * @param object
	 * @param field
	 * @param value
	 * @throws RuntimeException
	 *             If any exception occurs
	 */
	public void invokeSetter(Object object, Field field, Object value) {
		try {
			Method setter = getSetter(object.getClass(), field);
			setter.invoke(object, value);
		} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException("Error while invoking to the setter for the field '" + field.getName()
					+ "' in the class " + object.getClass().getName() + " class", e);
		}
	}

	/**
	 * Invoke the setter for the {@link Field} of the given name, on the given object. All check exceptions are hidden
	 * in a {@link RuntimeException}
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
			invokeSetter(object, field, value);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException e) {
			throw new RuntimeException("Error while invoking to the setter for the field '" + fieldName
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
	public Method getMethod(String methodName, Class<?> clazz, Class<?>... parameterTypes) {
		try {
			return clazz.getDeclaredMethod(methodName, parameterTypes);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(
					"Could not get the method '" + methodName + "' in the " + clazz.getName() + " class", e);
		}
	}

	/**
	 * Calls the 'methodName' method on the given object.
	 * 
	 * @param methodName
	 *            The name of the method. This method should have no parameter
	 * @param object
	 *            The given node, on which the 'methodName' method is to be called
	 * @return
	 */
	public Object invokeMethod(String methodName, Object object) {
		try {
			Method getType = object.getClass().getDeclaredMethod(methodName);
			return getType.invoke(object);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException("Error when trying to execute '" + methodName + "' on '"
					+ object.getClass().getName() + "': " + e.getMessage(), e);
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
	public Object invokeMethod(Method method, Object o, Object... args) {
		try {
			return method.invoke(o, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException("Error when executing the method '" + method.getName() + "' is missing in "
					+ o.getClass().getName() + " class", e);
		}
	}

	/**
	 * Returns a valid java identifier for the given name.
	 * 
	 * @param name
	 * @return If name is a default java keyword (so it is not a valid java identifier), then the return prefixed by a
	 *         {@link #JAVA_KEYWORD_PREFIX}. Otherwise (which is generally the case), the name is valid, and returned as
	 *         is the given name
	 */
	public String getJavaName(String name) {
		return isJavaReservedWords(name) ? JAVA_KEYWORD_PREFIX + name : name;
	}

	/**
	 * Returns true if name is a reserved java keyword
	 * 
	 * @param name
	 * @return
	 */
	public boolean isJavaReservedWords(String name) {
		return javaKeywords.contains(name);
	}

}