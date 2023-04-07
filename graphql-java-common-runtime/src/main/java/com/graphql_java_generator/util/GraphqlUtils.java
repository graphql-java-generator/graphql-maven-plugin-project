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
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.annotation.GraphQLDirective;
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
import reactor.core.publisher.Flux;

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

	/**
	 * Loads the runtime properties file, from the graphql-java-runtime.properties file.
	 */
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
	 * Reads a non ordered list, and return the same content sorted according the <i>keys</i> list. This method is used
	 * for batch loader method: they must return their result in the exact same order as the provided keys, so that the
	 * returned values are properly dispatched in the server's response.
	 * 
	 * @param <T>
	 *            The type of items in these list
	 * @param keys
	 *            The list which ordered must be respected.
	 * @param unorderedList
	 *            A list of items in any order. Each item in this list must have a key which is in the <i>keys</i>
	 *            list.<br/>
	 *            There may be missing values (for instance if a key doesn't match an item in the database). In this
	 *            case, this value is replaced by a null value.
	 * @param keyFieldName
	 *            The name of the field, that contain the key, that is: the T's attribute the can be matched against the
	 *            <i>keys</i> list. For instance: "id"
	 * @return A list of T instances coming from the <i>unorderedList</i>, where the key (retrieved by the <i>getter</i>
	 *         method) of these instances is in the exact same order as the <i>keys</i> list. Missing values in the
	 *         <i>unorderedList</i> list are replaced by null.
	 */
	public <T> List<T> orderList(List<?> keys, List<T> unorderedList, String keyFieldName) {
		Map<Object, T> map = new HashMap<>();
		for (T t : unorderedList) {
			map.put(invokeGetter(t, keyFieldName), t);
		}
		List<T> ret = new ArrayList<>(keys.size());
		for (Object id : keys) {
			if (map.containsKey(id))
				ret.add(map.get(id));
			else
				ret.add(null);

		}
		return ret;
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

				// We must manage the type erasure for list. So we use the GraphQL annotations
				// to retrieve types.
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

				// We must manage the type erasure for list. So we use the GraphQL annotations
				// to retrieve types.
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
	 * Retrieves the parameter value on the given Java object for the given directive.<br/>
	 * The Java object should be a class or an interface generated by the plugin, based on a GraphQL schema. For
	 * instance, it can be:
	 * <ul>
	 * <li>A class generated from a GraphQL object type</li>
	 * <li>An interface generated from a GraphQL interface or union</li>
	 * <li>A field of a GraphQL object</li>
	 * <li>A getter or setter method of a class or interface generated from a GraphQL object or an interface</li>
	 * <li>A parameter of a query, mutation or subscription executor that matches a parameter of a query, a mutation or
	 * a subscription</li>
	 * </ul>
	 * 
	 * 
	 * @param o
	 *            The object that has been annotated by the {@link GraphQLDirective} annotation
	 * @param parameterName
	 *            The name of parameter for which the Directive is search. <br/>
	 *            It is mandatory if the call looks a directive that was set on a parameter (typically a field's
	 *            parameter). In this case, o must be a query, mutation or subscription executor's {@link Method}<br/>
	 *            Otherwise it must be null.
	 * @param directiveName
	 *            The name of the directive which value must be returned.
	 * @return A Map of the parameters for the given directive's name, on the given object (o) or parameter (method o
	 *         and parameter's name parameterName). The value is given as its String representation.<br/>
	 *         If this directive has no parameters, an empty map is returned.
	 */
	Map<String, String> getDirectiveParameters(Object o, String parameterName, String directiveName) {
		if (directiveName == null || directiveName.equals(""))
			throw new RuntimeException("directiveName may not be null, nor an empty string");

		GraphQLDirective[] directives = null;
		GraphQLDirective directive = null;
		String oName = null;
		if (parameterName != null) {
			// A parameter name has been given: o must be a method. And t=we should return the GraphQLDirective content
			// for the parameterName parameters's of the o method
			if (!(o instanceof Method)) {
				throw new RuntimeException("parameterName is not null. It contains \"" + parameterName
						+ "\". So o must be a Method, but it is a " + o.getClass().getName());
			}
			Parameter[] methodParameters = ((Method) o).getParameters();
			oName = ((Method) o).getName();

			for (int i = 0; i < methodParameters.length; i += 1) {
				if (methodParameters[i].getName().equals(parameterName)) {
					directives = methodParameters[i].getAnnotationsByType(GraphQLDirective.class);
				}
			} // for

			if (directives == null) {
				throw new RuntimeException(
						"The method " + ((Method) o).getName() + " has no parameter of name \"" + parameterName + "\"");
			}
		} else if (o instanceof Class) {
			directives = ((Class<?>) o).getAnnotationsByType(GraphQLDirective.class);
			oName = ((Class<?>) o).getName();
		} else if (o instanceof Method) {
			directives = ((Method) o).getAnnotationsByType(GraphQLDirective.class);
			oName = ((Method) o).getName();
		} else if (o instanceof Field) {
			directives = ((Field) o).getAnnotationsByType(GraphQLDirective.class);
			oName = ((Field) o).getName();
		} else
			throw new RuntimeException("non managed object type: " + o.getClass().getName());

		// Ok, we've found the directive annotations. Let's find the one that match the given name
		for (GraphQLDirective d : directives) {
			if (directiveName.equals(d.name())) {
				directive = d;
			}
		}
		if (directive == null) {
			throw new RuntimeException("No directive of name \"" + directiveName + "\" where found on the "
					+ o.getClass().getName() + " of name \"" + oName + "\" (parameterName=" + parameterName + ")");
		}

		// Ok, we've found the asked directive. Let's build and return the map of its parameter names and values
		Map<String, String> values = new HashMap<>();

		if (directive.parameterNames() != null) {
			for (int i = 0; i < directive.parameterNames().length; i += 1) {
				values.put(directive.parameterNames()[i], directive.parameterValues()[i]);
			}
		}

		return values;
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
						setter = getSetter(clazz, field);
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
			// If the classname finishes by "Response", we take a look at the superclass, as
			// the XxxResponse classes are
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
		// The default setter's name is based on the field's name.
		// But for GraphQL fields that are actual java reserved keyword, the field's
		// name is not the one that was defined in the GraphQL schema (whereas the
		// setter uses the original name).
		String setterMethodName;

		// Is this field a GraphQL scalar?
		GraphQLScalar graphQLScalar = field.getAnnotation(GraphQLScalar.class);
		GraphQLNonScalar graphQLUnionType = field.getAnnotation(GraphQLNonScalar.class);
		if (graphQLUnionType != null) {
			setterMethodName = "set" + getPascalCase(graphQLUnionType.fieldName());
		} else if (graphQLScalar != null) {
			setterMethodName = "set" + getPascalCase(graphQLScalar.fieldName());
		} else {
			setterMethodName = "set" + getPascalCase(field.getName());
		}

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
		String fieldName;
		GraphQLScalar graphQLScalar = field.getAnnotation(GraphQLScalar.class);
		GraphQLNonScalar graphQLNonScalar = field.getAnnotation(GraphQLNonScalar.class);

		if (graphQLScalar != null)
			fieldName = graphQLScalar.fieldName();
		else if (graphQLNonScalar != null)
			fieldName = graphQLNonScalar.fieldName();
		else
			fieldName = field.getName();

		String getterMethodName = "get" + getPascalCase(fieldName);
		try {
			Method method = null;
			try {
				method = clazz.getMethod(getterMethodName);
			} catch (NoSuchMethodException e) {
				// For the boolean fields, the getter may be named isProperty. Let's try that:
				if (field.getType().equals(boolean.class)
						|| field.getType().equals(Boolean.class) /* && fieldName.startsWith("is") */) {
					getterMethodName = "is" + getPascalCase(fieldName);
					method = clazz.getMethod(getterMethodName);
					// }
					// // For the fields conflicting with reserved keywords and generated with an underscore (_), the
					// getter
					// // may be named _Property. Let's try that:
					// else if (field.getName().startsWith("_")) {
					// String sanitizedField = field.getName().substring(1);
					// getterMethodName = "get" + getPascalCase(sanitizedField);
					// method = clazz.getMethod(getterMethodName);
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
			throw new RuntimeException("The " + clazz.getName() + " class should have a getter '" + getterMethodName
					+ "', but this getter is missing", e);
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
	 * @param useJakartaEE9
	 *            If true, the<code>javax</code> imports must be replaced by <code>jakarta</code> imports
	 * @return
	 */
	public void addImport(Set<String> imports, String targetPackageName, String classname, boolean useJakartaEE9) {

		if (useJakartaEE9 && classname.startsWith("javax.")) {
			classname = "jakarta" + classname.substring(5);
		}

		// For inner class, the classname is "MainClassname$InnerClassname". And the
		// inner class must be imported, even
		// if we are in the same package. So, we replace all $ by dot
		String fullClassname = classname.replace('$', '.');

		int lastDotPos = fullClassname.lastIndexOf('.');
		String packageName = lastDotPos < 0 ? "" : fullClassname.substring(0, lastDotPos);
		String simpleClassName = fullClassname.substring(lastDotPos + 1);

		// No import for primitive types and java.lang
		// And no import if the class is in the same package.
		if (!packageName.isEmpty() && !packageName.equals("java.lang") && !targetPackageName.equals(packageName)) {
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
	 *            The given object, on which the 'methodName' method is to be called
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
			StringBuilder msg = new StringBuilder("Error when executing the method '");
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
	 * Calls the 'methodName' method on the given class.
	 * 
	 * @param methodName
	 *            The name of the method. This method should have no parameter
	 * @param clazz
	 *            The given class, on which the 'methodName' method is to be called
	 * @return
	 */
	public Object invokeStaticMethod(String methodName, Class<?> clazz) {
		try {
			Method method = clazz.getDeclaredMethod(methodName);
			return method.invoke(method);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new RuntimeException("Error when trying to execute the static method '" + methodName + "' on '"
					+ clazz.getName() + "': " + e.getMessage(), e);
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
	public String getClassSimpleName(String classFullNameParam) {
		String classFullName = (classFullNameParam.endsWith("[]"))
				? classFullNameParam.substring(0, classFullNameParam.length() - 2)
				: classFullNameParam;

		int lstPointPosition = classFullName.lastIndexOf('.');
		if (lstPointPosition > 0) {
			return classFullName.substring(lstPointPosition + 1);
		} else if (isPrimitiveType(classFullName)) {
			return classFullName;
		} else {
			throw new IllegalArgumentException(
					"The class full name should contain at least one point, or be a primitive type, but '"
							+ classFullName + "' doesn't");
		}
	}

	boolean isPrimitiveType(String type) {
		final List<String> primitiveTypes = Arrays.asList("boolean", "byte", "short", "int", "long", "char", "float",
				"double");
		return primitiveTypes.contains(type);
	}

	/**
	 * Extract the package name for a class, from its full class name (with the package name)
	 * 
	 * @param classFullName
	 *            The full class name, for instance java.util.Date
	 * @return The simple class name (in the above sample: java.util)
	 */
	public String getPackageName(String classFullNameParam) {
		String classFullName = (classFullNameParam.endsWith("[]"))
				? classFullNameParam.substring(0, classFullNameParam.length() - 2)
				: classFullNameParam;

		if (isPrimitiveType(classFullName)) {
			return null; // No package for primitive types
		}

		try {
			Class<?> cls = Class.forName(classFullName);
			return cls.getPackage().getName();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(
					"Could not find the package for the class '" + classFullNameParam + "', due to: " + e.getMessage(),
					e);
		}
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
			// For enums, we can't retrieve an instance of the enum value, as the enum class
			// has not been created yet.
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
			return "\"" + ((StringValue) value).getValue()//
					.replace("\\", "\\\\")//
					.replace("\"", "\\\"")//
					.replace("\n", "\\n")//
					.replace("\r", "")//
					.replace("\t", "\\t")//
					+ "\"";
		} else if (value instanceof BooleanValue) {
			return ((BooleanValue) value).isValue() ? "true" : "false";
		} else if (value instanceof IntValue) {
			return ((IntValue) value).getValue().toString();
		} else if (value instanceof FloatValue) {
			return ((FloatValue) value).getValue().toString();
		} else if (value instanceof graphql.language.EnumValue) {
			// For enums, we can't retrieve an instance of the enum value, as the enum class
			// has not been created yet.
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
	 * Returns the given value, as string, as it can be written into the {@link GraphQLDirective#parameterValues()} of
	 * the {@link GraphQLDirective} java annotation.<BR/>
	 * A <I>str</I> string default value will be returned as <I>"str"</I>,a <I>JEDI</I> enum value will be returned as
	 * <I>"JEDI"</I>, an object will be returned as <I>"{name:\"specific
	 * name\",appearsIn:[NEWHOPE,EMPIRE],type:\"Human\"}"</I>...
	 * 
	 * @return
	 */
	public String getValueAsString(Value<?> value) {
		// Depending on the kind of value, the returned String may be encapsulated by double quotes or not. This is
		// mandatory, to allow proper recursion in the getValueAsStringIterative method.

		// But every value returned by this method must encapsulated by double quotes. Let's check that
		String str = getValueAsStringIterative(value, 0);

		if (str.startsWith("\""))
			return str;
		else
			return "\"" + str + "\"";
	}

	/**
	 * Called by {@link #getValueAsString(Value)}. It return the String representation of the given value. It iterates
	 * (by calling recursively itself) for object fields and arrays.
	 * 
	 * @param value
	 * @param depth
	 *            0 means that it's the first call to this method. 1 means that getValueAsStringIterative() called
	 *            itself for the first time...
	 * @return
	 */
	private String getValueAsStringIterative(Value<?> value, int depth) {
		if (value == null || value instanceof NullValue) {
			return "null";
		} else if (value instanceof StringValue) {
			// The StringValue MUST BE encapsulated by double quotes here, so that it can be used in recursive calls for
			// objects and arrays.
			return "\"" //
					+ ((StringValue) value).getValue()//
							.replace("\\", "\\\\")//
							.replace("\"", "\\\"")//
							.replace("\n", "\\n")//
							.replace("\r", "")//
							.replace("\t", "\\t")//
					+ "\"";
		} else if (value instanceof BooleanValue) {
			return ((BooleanValue) value).isValue() ? "true" : "false";
		} else if (value instanceof IntValue) {
			return ((IntValue) value).getValue().toString();
		} else if (value instanceof FloatValue) {
			return ((FloatValue) value).getValue().toString();
		} else if (value instanceof graphql.language.EnumValue) {
			// For enums, we can't retrieve an instance of the enum value, as the enum class
			// has not been created yet.
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
				sb.append(v.getName()).append(":").append(getValueAsStringIterative(v.getValue(), depth + 1));
			} // for
			sb.append("}");

			// If it's the main call (not a recursive one), we must encapsulate the object String by double quotes
			if (depth == 0) {
				return "\"" + sb.toString().replace("\"", "\\\"") + "\"";
			} else {
				return sb.toString();
			}
		} else if (value instanceof ArrayValue) {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			boolean appendSep = false;
			for (Value<?> v : ((ArrayValue) value).getValues()) {
				if (appendSep)
					sb.append(",");
				else
					appendSep = true;
				sb.append(getValueAsStringIterative(v, depth + 1));
			} // for
			sb.append("]");

			// The whole array (that is: the one returned on the first call to this method, not the recursive ones that
			// may follow) must be encapsulated by
			if (depth == 0) {
				return "\"" + sb.toString().replace("\"", "\\\"") + "\"";
			} else {
				return sb.toString();
			}
		} else {
			throw new RuntimeException("Value of type " + value.getClass().getName() + " is not managed");
		}
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
		} else if (enumValue instanceof Flux) {
			// For a flux, we must transform each returned item
			return ((Flux<?>) enumValue).map(v -> enumValueToString(v));
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
	 * Returns the given enumValue given as a String, into the enum value from the generated POJO of the given
	 * class.<br/>
	 * This can not be a parameterized method as the return value may be either an enum, a list of enums...
	 * 
	 * @param enumValue
	 *            May be null, a String, a list of String, a list of lists of String...
	 * @param enumClass
	 *            The POJO class of the enum
	 * @return The same kind of list, but all String representations are replaced by the relevant enum value, based on
	 *         the GraphQL schema.
	 */
	public Object stringToEnumValue(Object enumValue, Class<?> enumClass) {
		if (enumValue == null) {
			return null;
		} else if (enumValue instanceof List) {
			return ((List<?>) enumValue).stream().map(v -> stringToEnumValue(v, enumClass))
					.collect(Collectors.toList());
		} else if (enumValue instanceof String) {
			Method method;
			try {
				method = enumClass.getMethod("fromGraphQlValue", String.class);
			} catch (NoSuchMethodException | SecurityException e) {
				throw new RuntimeException("Could not find the 'fromGraphQlValue' method to bind the '" + enumValue
						+ "' value to the " + enumClass.getName() + " class");
			}
			try {
				return method.invoke(null, enumValue);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException("Error when binding the '" + enumValue + "' value to the "
						+ enumClass.getName() + " class: " + e.getMessage());
			}
		} else {
			throw new RuntimeException("Non managed type when trying to bind the '" + enumValue + "' value to the "
					+ enumClass.getName() + " class");
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
		} else if (fileOrFolder.isDirectory()) {
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
		} else {
			throw new RuntimeException("Non managed file type for " + fileOrFolder.getAbsolutePath());
		}
	}

	public String getQuotedScanBasePackages(String scanBasePackages) {

		if (scanBasePackages == null || scanBasePackages.contentEquals("") || scanBasePackages.contentEquals("null")) {
			return "";
		}

		// Let's remove all spaces. It will be easier to insert the good double quotes,
		// afterwards.
		// Let's say scanBasePackages is: a, b, c,d
		scanBasePackages = scanBasePackages.replace(" ", "");// scanBasePackages is now a,b,c,d
		scanBasePackages = scanBasePackages.replace(",", "\",\"");// scanBasePackages is now a","b","c","d
		scanBasePackages = ",\"" + scanBasePackages + "\"";// scanBasePackages is now ,"a","b","c","d"
		return scanBasePackages;
	}

}