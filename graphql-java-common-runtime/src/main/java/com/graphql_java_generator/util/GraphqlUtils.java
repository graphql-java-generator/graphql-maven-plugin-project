/**
 * 
 */
package com.graphql_java_generator.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

import graphql.language.ArrayValue;
import graphql.language.BooleanValue;
import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.language.NullValue;
import graphql.language.ObjectField;
import graphql.language.ObjectValue;
import graphql.language.StringValue;
import graphql.language.Value;

/**
 * @author etienne-sf
 */
// Important Note: the Unit tests for this class are in the graphql-java-client-runtime module: the need lots of
// dependencies to be tested (some generated code, and all the client dependencies that go along)
@Component
public class GraphqlUtils {

	/** Logger for this class */
	private static Logger logger = LoggerFactory.getLogger(GraphqlUtils.class);

	/** This singleton is usable in default method, within interfaces */
	public static GraphqlUtils graphqlUtils = new GraphqlUtils();

	Pattern graphqlNamePattern = Pattern.compile("^[_A-Za-z][_0-9A-Za-z]*$");

	/**
	 * The list of Java keywords. This keyword may not be used as java identifier, within java code (for instance for
	 * class name, field name...).<BR/>
	 * If a GraphQL identifier is one of these keyword, it will be prefixed by {@link #JAVA_KEYWORD_PREFIX} in the
	 * generated code.
	 */
	private List<String> javaKeywords = new ArrayList<>();

	/**
	 * The runtime properties, as they are in the graphql-java-runtime.properties file. This includes the version of the
	 * runtime, that is used to check that the runtime's version is the same as the Maven or Gradle plugin's version
	 */
	private Properties properties;
	final static String PROPERTIES_FILE = "graphql-java-runtime.properties";
	final static String PROP_RUNTIME_VERSION = "graphql-java-runtime.version";

	public static Character JAVA_KEYWORD_PREFIX = '_';

	public GraphqlUtils() {
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
	 * Returns the version of the runtime, that is used to check that the runtime's version is the same as the Maven or
	 * Gradle plugin's version.
	 */
	public String getRuntimeVersion() {
		return getProperties().getProperty(PROP_RUNTIME_VERSION);
	}

	/** Loads the runtime properties file, from the graphql-java-runtime.properties file. */
	private Properties getProperties() {
		if (properties == null) {
			properties = new Properties();
			try (InputStream res = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
				properties.load(res);
			} catch (IOException e) {
				String msg = "Error while reading the '" + PROPERTIES_FILE + "' properties file: " + e.getMessage();
				logger.error(msg);
				throw new RuntimeException(msg, e);
			}
		}
		return properties;
	}

	/**
	 * Convert the given name, to a camel case name. Currently very simple : it puts the first character in lower case.
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
	 * Transform an {@link Iterable} (which can be a {@link List}), into a {@link List} of items of the same type. It's
	 * usefull to transform the native type from Spring Data repositories (which needs concrete class to map into) into
	 * the list of relevant GraphQL interface
	 * 
	 * @param <I>
	 * @param iterable
	 * @return
	 */
	public <I> List<I> iterableToList(Iterable<I> iterable) {
		List<I> ret = new ArrayList<I>();
		for (I i : iterable) {
			ret.add(i);
		}
		return ret;
	}

	/**
	 * Transform an {@link Iterable} (which can be a {@link List}) of a concrete class, into a {@link List} of the I
	 * interface or superclass. It's usefull to transform the native type from Spring Data repositories (which needs
	 * concrete class to map into) into the list of relevant GraphQL interface
	 * 
	 * @param <I>
	 * @param iterable
	 * @return
	 */
	public <I> List<I> iterableConcreteClassToListInterface(Iterable<? extends I> iterable) {
		List<I> ret = new ArrayList<I>();
		for (I i : iterable) {
			ret.add(i);
		}
		return ret;
	}

	/**
	 * Transform an {@link Optional}, as returned by Spring Data repositories, into a standard Java, which is null if
	 * there is no value.
	 * 
	 * @param optional
	 * @return
	 */
	public <T> T optionalToObject(Optional<T> optional) {
		return optional.isPresent() ? optional.get() : null;
	}

	/**
	 * Retrieves the class of the fieldName field of the owningClass class.
	 * 
	 * @param owningClass
	 * @param fieldName
	 * @param returnIsMandatory
	 *            If true, a {@link GraphQLRequestPreparationException} is thrown if the field is not found.
	 * @return The class of the field. Or null of the field doesn't exist, and returnIdMandatory is false
	 * @throws GraphQLRequestPreparationException
	 */
	public Class<?> getFieldType(Class<?> owningClass, String fieldName, boolean returnIsMandatory)
			throws GraphQLRequestPreparationException {
		if (owningClass.isInterface()) {
			// We try to get the class of this getter of the field
			try {
				Method method = owningClass.getDeclaredMethod("get" + graphqlUtils.getPascalCase(fieldName));

				// We must manage the type erasure for list. So we use the GraphQL annotations to retrieve types.
				GraphQLNonScalar graphQLNonScalar = method.getAnnotation(GraphQLNonScalar.class);
				GraphQLScalar graphQLScalar = method.getAnnotation(GraphQLScalar.class);

				if (graphQLNonScalar != null)
					return graphQLNonScalar.javaClass();
				else if (graphQLScalar != null)
					return graphQLScalar.javaClass();
				else
					throw new GraphQLRequestPreparationException("Error while looking for the getter for the field '"
							+ fieldName + "' in the interface '" + owningClass.getName()
							+ "': this method should have one of these annotations: GraphQLNonScalar or GraphQLScalar ");
			} catch (NoSuchMethodException e) {
				// Hum, the field doesn't exist.
				if (!returnIsMandatory)
					return null;
				else
					throw new GraphQLRequestPreparationException("Error while looking for the getter for the field '"
							+ fieldName + "' in the class '" + owningClass.getName() + "'", e);
			} catch (SecurityException e) {
				throw new GraphQLRequestPreparationException("Error while looking for the getter for the field '"
						+ fieldName + "' in the class '" + owningClass.getName() + "'", e);
			}
		} else {
			// We try to get the class of this field
			try {
				Field field = owningClass.getDeclaredField(graphqlUtils.getJavaName(fieldName));

				// We must manage the type erasure for list. So we use the GraphQL annotations to retrieve types.
				GraphQLNonScalar graphQLNonScalar = field.getAnnotation(GraphQLNonScalar.class);
				GraphQLScalar graphQLScalar = field.getAnnotation(GraphQLScalar.class);

				if (graphQLNonScalar != null)
					return graphQLNonScalar.javaClass();
				else if (graphQLScalar != null)
					return graphQLScalar.javaClass();
				else
					throw new GraphQLRequestPreparationException("Error while looking for the the field '" + fieldName
							+ "' in the class '" + owningClass.getName()
							+ "': this field should have one of these annotations: GraphQLNonScalar or GraphQLScalar ");
			} catch (NoSuchFieldException e) {
				// Hum, the field doesn't exist.
				if (!returnIsMandatory)
					return null;
				else
					throw new GraphQLRequestPreparationException("Error while looking for the the field '" + fieldName
							+ "' in the class '" + owningClass.getName() + "'", e);
			} catch (SecurityException e) {
				throw new GraphQLRequestPreparationException("Error while looking for the the field '" + fieldName
						+ "' in the class '" + owningClass.getName() + "'", e);
			}
		}
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
			Field field;

			try {
				t = clazz.getConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
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

				invokeMethod(setter, t, value);
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
			Method valueOf = graphqlUtils.getMethod("valueOf", clazz, String.class);
			return graphqlUtils.invokeMethod(valueOf, null, (String) jsonParsedValue);
		} else if (graphQLTypeName.equals("ID")) {
			// ID is particular animal: it's by default managed as a UUID (we're on server side). And this can be
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
	 * Returns a {@link Field} from the given class.
	 * 
	 * @param owningClass
	 *            The class that should contain this field. If the class's name finishes by Response, as an empty
	 *            XxxResponse class is created for each Query/Mutation/Subscription (to be compatible with previsous
	 *            version), then this method also looks in the owningClass's superclass.
	 * @param fieldName
	 *            The name of the searched field
	 * @param mustFindField
	 *            If true and the field is not found, a {@link GraphQLRequestPreparationException} is thrown.<BR/>
	 *            If false an the field is not found, the method returns null
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Field getDeclaredField(Class<?> owningClass, String fieldName, boolean mustFindField)
			throws GraphQLRequestPreparationException {

		try {
			return owningClass.getDeclaredField(fieldName);
		} catch (NoSuchFieldException | SecurityException e1) {
			// If the classname finishes by "Response", we take a look at the superclass, as the XxxResponse classes are
			// built as just inheriting from the query/mutation/subscription class
			if (owningClass.getSimpleName().endsWith("Response")) {
				try {
					return owningClass.getSuperclass().getDeclaredField(fieldName);
				} catch (NoSuchFieldException | SecurityException e2) {
					if (mustFindField)
						throw new GraphQLRequestPreparationException("Could not find fied '" + fieldName + "' in "
								+ owningClass.getName() + ", nor in " + owningClass.getSuperclass().getName(), e1);
				}
			}

			if (mustFindField)
				throw new GraphQLRequestPreparationException(
						"Could not find fied '" + fieldName + "' in " + owningClass.getName(), e1);
		}
		return null;
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
	public Method getGetter(Class<?> clazz, Field field) {
		String getterMethodName = "get" + getPascalCase(field.getName());
		try {
			Method method = null;
			try {
				method = clazz.getMethod(getterMethodName);
			} catch (NoSuchMethodException e) {
				// For the boolean fields, the getter may be named isProperty. Let's try that:
				if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
					getterMethodName = "is" + getPascalCase(field.getName());
					method = clazz.getMethod(getterMethodName);
				} else {
					throw e;
				}
			}
			// The return type must be the same as the field's class
			if (field.getType() != method.getReturnType()) {
				throw new RuntimeException("The getter '" + getterMethodName + "' and the field '" + field.getName()
						+ "' of the class " + clazz.getName() + " should be of the same type");
			}

			return method;
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(
					"The getter '" + getterMethodName + "' is missing in " + clazz.getName() + " class", e);
		} catch (SecurityException e) {
			throw new RuntimeException(
					"Error while accessing to the getter '" + getterMethodName + "' in " + clazz.getName() + " class",
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
			Field field = getField(object, fieldName);
			Method getter = getGetter(object.getClass(), field);
			return getter.invoke(object);
		} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException("Error while invoking to the getter for the field '" + fieldName
					+ "' in the class " + object.getClass().getName() + " class", e);
		}
	}

	/**
	 * Returns the field of the given name, in the objet's class, whether if it's in the object's class, or in one of
	 * its superclass.
	 * 
	 * @param object
	 * @param fieldName
	 * @return
	 */
	private Field getField(Object object, String fieldName) {
		Class<?> clazz = object.getClass();
		while (!clazz.equals(Object.class)) {
			try {
				return clazz.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				// We'll loop, and try in a superclass
			}
			clazz = clazz.getSuperclass();
		}
		throw new RuntimeException("Could not find the field " + fieldName + " in either " + object.getClass().getName()
				+ " or in one of its superclass");
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
	 * Adds, if necessary the import calculated from the given parameters, into the given set of imports.
	 * 
	 * @param imports
	 *            The set of import, in which the import for the given parameters is to be added
	 * @param targetPackageName
	 *            The package in which is the class that will contain this import
	 * @param classname
	 *            the full classname of the class to import
	 * @return
	 */
	public void addImport(Set<String> imports, String targetPackageName, String classname) {

		// For inner class, the classname is "MainClassname$InnerClassname". And the inner class must be imported, even
		// if we are in the same package. So, we replace all $ by dot
		String fullClassname = classname.replace('$', '.');

		int lastDotPos = fullClassname.lastIndexOf('.');
		String packageName = fullClassname.substring(0, lastDotPos);
		String simpleClassName = fullClassname.substring(lastDotPos + 1);

		// No import for java.lang
		// And no import if the class is in the same package.
		if (!packageName.equals("java.lang") && !targetPackageName.equals(packageName)) {
			imports.add(packageName + "." + simpleClassName);
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
	public Object invokeMethod(String methodName, Object object, Object... args) {
		try {
			Method getType = object.getClass().getDeclaredMethod(methodName);
			return getType.invoke(object, args);
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
			StringBuffer msg = new StringBuffer("Error when executing the method '");
			msg.append(method.getName());
			msg.append("(");
			String separator = "";
			for (Object arg : args) {
				msg.append(separator);
				separator = ",";
				msg.append(arg.getClass().getName());
			}
			msg.append(")' is missing in ");
			msg.append(o.getClass().getName());
			msg.append(" class");
			throw new RuntimeException(msg.toString(), e);
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

	/**
	 * Extract the simple name for a class (without the package name), from its full class name (with the package name)
	 * 
	 * @param classFullName
	 *            The full class name, for instance java.util.Date
	 * @return The simple class name (in the above sample: Date)
	 */
	public String getClassSimpleName(String classFullName) {
		int lstPointPosition = classFullName.lastIndexOf('.');
		return classFullName.substring(lstPointPosition + 1);
	}

	/**
	 * Extract the package name for a class, from its full class name (with the package name)
	 * 
	 * @param classFullName
	 *            The full class name, for instance java.util.Date
	 * @return The simple class name (in the above sample: java.util)
	 */
	public String getPackageName(String classFullName) {
		int lstPointPosition = classFullName.lastIndexOf('.');
		return classFullName.substring(0, lstPointPosition);
	}

	/**
	 * Concatenate a non limited number of lists into a stream.
	 * 
	 * @param <T>
	 * @param clazz
	 *            The T class
	 * @param parallelStreams
	 *            true if the returned stream should be a parallel one
	 * @param t1
	 *            An optional item, that'll be added to the returned stream (if not null)
	 * @param t2
	 *            An optional item, that'll be added to the returned stream (if not null)
	 * @param t3
	 *            An optional item, that'll be added to the returned stream (if not null)
	 * @param lists
	 * @return
	 */
	@SafeVarargs
	final public <T> Stream<T> concatStreams(Class<T> clazz, boolean parallelStreams, T t1, T t2, T t3,
			List<? extends T>... lists) {
		Stream.Builder<T> builder = Stream.builder();

		// Let's first add all non list objects
		if (t1 != null) {
			builder.accept(t1);
		}
		if (t2 != null) {
			builder.accept(t2);
		}
		if (t3 != null) {
			builder.accept(t3);
		}

		Stream<T> ret = builder.build();
		for (List<? extends T> list : lists) {
			ret = Stream.concat(ret, list.stream());
		}
		return parallelStreams ? ret.parallel() : ret;
	}

	/**
	 * Get the internal value for a {@link Value} stored in the graphql-java AST.
	 * 
	 * @param value
	 *            The value for which we need to extract the real value
	 * @param graphqlTypeName
	 *            The type name for this value, as defined in the GraphQL schema. This is used when it's an object
	 *            value, to create an instance of the correct java class.
	 * @param action
	 *            The action that is executing, to generated an explicit error message. It can be for instance "Reading
	 *            directive directiveName".
	 * @return
	 */
	Object getValue(Value<?> value, String graphqlTypeName, String action) {
		if (value instanceof StringValue) {
			return ((StringValue) value).getValue();
		} else if (value instanceof BooleanValue) {
			return ((BooleanValue) value).isValue();
		} else if (value instanceof IntValue) {
			return ((IntValue) value).getValue();
		} else if (value instanceof FloatValue) {
			return ((FloatValue) value).getValue();
		} else if (value instanceof graphql.language.EnumValue) {
			// For enums, we can't retrieve an instance of the enum value, as the enum class has not been created yet.
			// So we just return the label of the enum, as a String.
			return ((graphql.language.EnumValue) value).getName();
		} else if (value instanceof NullValue) {
			return null;
		} else if (value instanceof ArrayValue) {
			@SuppressWarnings("rawtypes")
			List<Value> list = ((ArrayValue) value).getValues();
			Object[] ret = new Object[list.size()];
			for (int i = 0; i < list.size(); i += 1) {
				ret[i] = getValue(list.get(i), graphqlTypeName, action + ": ArrayValue(" + i + ")");
			}
			return ret;
			// } else if (value instanceof ObjectValue) {
			// return null;
		} else {
			throw new RuntimeException(
					"Value of type " + value.getClass().getName() + " is not managed (" + action + ")");
		}
	}

	/**
	 * Returns the given value, as text, as it can be written into a generated GraphQL schema.<BR/>
	 * A <I>str</I> string default value will be returned as <I>"str"</I>,a <I>JEDI</I> enum value will be returned as
	 * <I>JEDI</I>, ...
	 * 
	 * @return
	 */
	public String getValueAsText(Value<?> value) {
		if (value == null || value instanceof NullValue) {
			return "null";
		} else if (value instanceof StringValue) {
			return "\"" + ((StringValue) value).getValue() + "\"";
		} else if (value instanceof BooleanValue) {
			return ((BooleanValue) value).isValue() ? "true" : "false";
		} else if (value instanceof IntValue) {
			return ((IntValue) value).getValue().toString();
		} else if (value instanceof FloatValue) {
			return ((FloatValue) value).getValue().toString();
		} else if (value instanceof graphql.language.EnumValue) {
			// For enums, we can't retrieve an instance of the enum value, as the enum class has not been created yet.
			// So we just return the label of the enum, as a String.
			return ((graphql.language.EnumValue) value).getName();
		} else if (value instanceof ObjectValue) {
			StringBuilder sb = new StringBuilder();
			sb.append("{");
			boolean appendSep = false;
			for (ObjectField v : ((ObjectValue) value).getObjectFields()) {
				if (appendSep)
					sb.append(",");
				else
					appendSep = true;
				sb.append(v.getName()).append(":").append(getValueAsText(v.getValue()));
			} // for
			sb.append("}");
			return sb.toString();
		} else if (value instanceof ArrayValue) {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			boolean appendSep = false;
			for (Value<?> v : ((ArrayValue) value).getValues()) {
				if (appendSep)
					sb.append(",");
				else
					appendSep = true;
				sb.append(getValueAsText(v));
			} // for
			sb.append("]");
			return sb.toString();
		} else {
			throw new RuntimeException("Value of type " + value.getClass().getName() + " is not managed");
		}
	}

	/**
	 * Returns the maximum or minimum value for the lastModified of the given file, or of all the files (not folders)
	 * contained into this folder.
	 * 
	 * @param fileOrFolder
	 *            A file or a folder
	 * @param maxValue
	 *            If true and fileOrFolder is a folder, then this method returns the maximum {@link File#lastModified()}
	 *            found for all its files. If false, then the minimum value is returned.
	 * @return if fileOrFolder doesn't exist, then returns null. If fileOrFolder is a file, then returns its
	 *         {@link File#lastModified()} value. Otherwise its a folder. Then it loops into this folder, its subfolders
	 *         (and so on), and returns the maximum or the minimum (depending on the value of maxValue) lastModified
	 *         date found for the files found. The date of the directories are ignored.
	 */
	public Long getLastModified(File fileOrFolder, boolean maxValue) {
		if (fileOrFolder == null || !fileOrFolder.exists()) {
			return null;
		} else if (fileOrFolder.isFile()) {
			return fileOrFolder.lastModified();
		} else if (!fileOrFolder.isDirectory()) {
			throw new RuntimeException("Unknown file type for " + fileOrFolder.getAbsolutePath());
		} else {
			// We have a folder. Let's recurse into its content.
			Long lastModifed = null;
			for (File f : fileOrFolder.listFiles()) {
				Long contentLastModified = getLastModified(f, maxValue);
				if (contentLastModified != null) {
					if (lastModifed == null) {
						lastModifed = contentLastModified;
					} else if (maxValue && contentLastModified > lastModifed) {
						lastModifed = contentLastModified;
					} else if (!maxValue && contentLastModified < lastModifed) {
						lastModifed = contentLastModified;
					}
				}
			} // for
			return lastModifed;
		}
	}

	public String getQuotedScanBasePackages(String scanBasePackages) {

		if (scanBasePackages == null || scanBasePackages.contentEquals("") || scanBasePackages.contentEquals("null")) {
			return "";
		}

		// Let's remove all spaces. It will be easier to insert the good double quotes, afterwards.
		// Let's say scanBasePackages is: a, b, c,d
		scanBasePackages = scanBasePackages.replace(" ", "");// scanBasePackages is now a,b,c,d
		scanBasePackages = scanBasePackages.replace(",", "\",\"");// scanBasePackages is now a","b","c","d
		scanBasePackages = ",\"" + scanBasePackages + "\"";// scanBasePackages is now ,"a","b","c","d"
		return scanBasePackages;
	}

	// /**
	// * Encode a string according to GraphQL specification rules:
	// *
	// * <PRE>
	// StringValue ::
	// "StringCharacter(list,opt)"
	// """BlockStringCharacter(list,opt)"""
	//
	// StringCharacter ::
	// SourceCharacter but not " or \ or LineTerminator
	// \ u EscapedUnicode
	// \ EscapedCharacter
	//
	// EscapedUnicode ::
	// /[0-9 A-Fa-f]{4}/
	//
	// EscapedCharacter :: one of
	// " \ / b f n r t
	//
	// BlockStringCharacter ::
	// SourceCharacter but not""" or \"""
	// \"""
	//
	// LineTerminator::
	// New Line (U+000A)
	// Carriage Return (U+000D)New Line (U+000A)
	// Carriage Return (U+000D)New Line (U+000A)
	// * </PRE>
	// *
	// * @param str
	// * @return
	// */
	// public String graphqlEncodeString(String str) {
	// return str.replace("\\", "\\\\").replace("\"", "\\\"").replace("\b", "\\b").replace("\f", "\\f")
	// .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
	// }
	//
	// /**
	// * De-encode a string according to GraphQL specification rules. See {@link #graphqlEncodeString(String)} for the
	// * GraphQL rules.
	// *
	// * @param str
	// * @return
	// */
	// public String graphqlDeencodeString(String str) {
	// StringBuffer sb = new StringBuffer();
	// for (int i = 0; i < str.length(); i += 1) {
	// char c = str.charAt(i);
	// if (c == '\\') {
	// if (i=str.length()-1) {
	// The last character may not be an anti-slash
	// }
	// next..c.
	// } else {
	// sb.append(c);
	// }
	// }
	// return str.replace("\\\\", "\\").replace("\\\"", "\"").replace("\b", "\\b").replace("\f", "\\f")
	// .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
	// }
}