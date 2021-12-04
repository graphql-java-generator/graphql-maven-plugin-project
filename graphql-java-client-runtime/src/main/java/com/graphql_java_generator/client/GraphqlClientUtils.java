/**
 * 
 */
package com.graphql_java_generator.client;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.graphql_java_generator.annotation.GraphQLEnumType;
import com.graphql_java_generator.annotation.GraphQLInterfaceType;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLObjectType;
import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.annotation.GraphQLUnionType;
import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.customscalars.CustomScalar;
import com.graphql_java_generator.customscalars.CustomScalarRegistryImpl;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.util.GraphqlUtils;

import graphql.schema.GraphQLScalarType;

/**
 * @author etienne-sf
 */
public class GraphqlClientUtils {

	/** A singleton without Spring */
	public static GraphqlClientUtils graphqlClientUtils = new GraphqlClientUtils();

	Pattern graphqlNamePattern = Pattern.compile("^[_A-Za-z][_0-9A-Za-z]*$");

	GraphqlUtils graphqlUtils = new GraphqlUtils();

	/**
	 * maps for all scalers, when they are mandatory. The key is the type name. The value is the class to use in the
	 * java code
	 */
	List<Class<?>> scalars = new ArrayList<>();

	public GraphqlClientUtils() {
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
	 * Checks that the given GraphQL name is valid.
	 * 
	 * @param graphqlIdentifier
	 * @throws NullPointerException
	 *             If name is null
	 * @throws GraphQLRequestPreparationException
	 *             If the given graphqlIdentifier is not a valid identifier
	 */
	public void checkName(String graphqlIdentifier) throws GraphQLRequestPreparationException {
		if (graphqlIdentifier == null) {
			throw new NullPointerException("A GraphQL identifier may not be null");
		}
		Matcher m = graphqlNamePattern.matcher(graphqlIdentifier);
		if (!m.matches()) {
			throw new GraphQLRequestPreparationException("'" + graphqlIdentifier + "' is not a valid GraphQL name");
		}
	}

	/**
	 * This method checks whether the given field (as an attribute) of the given class is a GraphQL scalar, or not,
	 * depending on shouldBeScalar.
	 * 
	 * @param field
	 *            The field whose type should be (or not) a scalar
	 * @param shouldBeScalar
	 *            if true, this method checks that field's type is a scalar (if false, checks that it is not a scalar)
	 * @return Returns the Class indicated as the value for the graphqlType attribute of the GraphQLScalar or
	 *         GraphQLNonScalar annotation
	 * @throws GraphQLRequestPreparationException
	 */
	public Class<?> checkIsScalar(java.lang.reflect.Field field, Boolean shouldBeScalar)
			throws GraphQLRequestPreparationException {
		// All types are generated in the same package. So, if the package for the field type is the same as the
		// package for our class, this means that the field is not a scalar
		// Note: we'll perhaps have to change that, depending on the way we manage scalar

		boolean isScalar = isScalar(field);

		if (shouldBeScalar != null) {
			if (shouldBeScalar & !isScalar) {
				throw new GraphQLRequestPreparationException("The field '" + field.getName() + "' of the GraphQL type '"
						+ field.getDeclaringClass().getName()
						+ "' is not a GraphQLScalar. At least one field must be defined for the server response.");
			}
			if (!shouldBeScalar & isScalar) {
				throw new GraphQLRequestPreparationException("The field '" + field.getName() + "' of the GraphQL type '"
						+ field.getDeclaringClass().getName()
						+ "' is not a GraphQLScalar. At least one field must be defined for the server response.");
			}
		}

		return getGraphQLType(field);
	}

	/**
	 * This method checks whether the given field (as a method: getter, query...) of the given class is a GraphQL
	 * scalar, or not, depending on shouldBeScalar.
	 * 
	 * @param fieldName
	 *            the name of the field represented by the given method.
	 * @param method
	 *            The method whose return should be (or not) a scalar. This method can be a setter, a getter (in which
	 *            case its name is different from the fieldName), or a query/mutation/subscription (in which case its
	 *            name is the fieldName)
	 * @param shouldBeScalar
	 *            if true, this method checks that method return type is a scalar (if false, checks that it is not a
	 *            scalar)
	 * @throws GraphQLRequestPreparationException
	 */
	public Class<?> checkIsScalar(String fieldName, Method method, Boolean shouldBeScalar)
			throws GraphQLRequestPreparationException {
		// All types are generated in the same package. So, if the package for the field type is the same as the
		// package for our class, this means that the field is not a scalar
		// Note: we'll perhaps have to change that, depending on the way we manage scalar
		boolean isScalar = isScalar(method);

		if (method.getReturnType() == null) {
			throw new GraphQLRequestPreparationException("There is a method of name '" + fieldName
					+ "' in the GraphQL type '" + method.getDeclaringClass().getName()
					+ "', but this method is a void method: it can't represent the '" + fieldName + "' GraphQL field");
		}

		if (shouldBeScalar != null) {
			if (shouldBeScalar && !isScalar) {
				throw new GraphQLRequestPreparationException(
						"The field '" + fieldName + "' (accessed through its getter: " + method.getName()
								+ "') of the GraphQL type '" + method.getDeclaringClass().getName()
								+ "' should be a scalar. But is is actually not a GraphQLScalar");
			}
			if (!shouldBeScalar && isScalar) {
				throw new GraphQLRequestPreparationException(
						"The field '" + fieldName + "' (accessed through its getter: '" + method.getName()
								+ "') of the GraphQL type '" + method.getDeclaringClass().getName()
								+ "' should not be a scalar. But is is actually a GraphQLScalar");
			}
		}

		return getGraphQLType(method);
	}

	/**
	 * Indicates whether the given class is a scalar or not
	 * 
	 * @param fieldOrMethod
	 * @return true if clazz is a scalar type
	 * @throws GraphQLRequestPreparationException
	 */
	public boolean isScalar(AccessibleObject fieldOrMethod) throws GraphQLRequestPreparationException {
		if (fieldOrMethod.getAnnotation(GraphQLScalar.class) != null
				|| fieldOrMethod.getAnnotation(GraphQLNonScalar.class) != null) {
			// Ok, at least on of GraphQLScalar and GraphQLNonScalar annotation is set.
			return fieldOrMethod.getAnnotation(GraphQLScalar.class) != null;
		} else {
			// No GraphQLScalar or GraphQLNonScalar annotation: let's throw an internal error.
			if (fieldOrMethod instanceof Field) {
				Field field = (Field) fieldOrMethod;
				throw new GraphQLRequestPreparationException("The field '" + field.getName() + "' of the class '"
						+ field.getDeclaringClass().getName()
						+ "' has none of the GraphQLCustomScalar, GraphQLScalar or GraphQLNonScalar annotation");
			} else {
				Method method = (Method) fieldOrMethod;
				throw new GraphQLRequestPreparationException("The method '" + method.getName() + "' of the class '"
						+ method.getDeclaringClass().getName()
						+ "' has none of the GraphQLCustomScalar, GraphQLScalar or GraphQLNonScalar annotation");
			}
		}
	}

	/**
	 * Retrieves a class for a given classname. For standard GraphQL types (Int, Boolean...) the good package is used
	 * (java.lang, java.lang, java.util...). For others, the class is searched in the given package name.
	 * 
	 * @param packageName
	 *            The name of the package, where the code has been generated.
	 * @param graphQLTypeName
	 *            The name of the class
	 * @return
	 */
	public Class<?> getClass(String packageName, String graphQLTypeName) {

		// First case, the simplest: standard GraphQL type
		if ("Boolean".equals(graphQLTypeName) || "boolean".equals(graphQLTypeName))
			return Boolean.class;
		else if ("Integer".equals(graphQLTypeName) || "Int".equals(graphQLTypeName))
			return Integer.class;
		else if ("String".equals(graphQLTypeName) || "UUID".equals(graphQLTypeName))
			return String.class;
		else if ("Float".equals(graphQLTypeName) || "Double".equals(graphQLTypeName))
			return Double.class;

		// Then custom scalars
		CustomScalar customScalar = CustomScalarRegistryImpl.customScalarRegistry.getCustomScalar(graphQLTypeName);
		if (customScalar != null) {
			return customScalar.getValueClazz();
		}

		// Then other GraphQL types. This types should be linked to a generated java class. So we search for a class of
		// this name in the given package.
		String parameterClassname = packageName + "." + graphqlUtils.getJavaName(graphQLTypeName);
		try {
			return Class.forName(parameterClassname);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(
					"Couldn't find the class (" + parameterClassname + ") of the type '" + graphQLTypeName + "'", e);
		}
	}

	/**
	 * Returns the Class indicated as the value for the graphqlType attribute of the GraphQLScalar or GraphQLNonScalar
	 * annotation
	 * 
	 * @param fieldOrMethod
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Class<?> getGraphQLType(AccessibleObject fieldOrMethod) throws GraphQLRequestPreparationException {
		if (fieldOrMethod.getAnnotation(GraphQLScalar.class) != null) {
			return fieldOrMethod.getAnnotation(GraphQLScalar.class).javaClass();
		} else if (fieldOrMethod.getAnnotation(GraphQLNonScalar.class) != null) {
			return fieldOrMethod.getAnnotation(GraphQLNonScalar.class).javaClass();
		} else {
			// No GraphQLScalar or GraphQLNonScalar annotation: let's thrown an internal error.
			if (fieldOrMethod instanceof Field) {
				Field field = (Field) fieldOrMethod;
				throw new GraphQLRequestPreparationException("The field '" + field.getName() + "' of the class '"
						+ field.getDeclaringClass().getName()
						+ "' has none of the GraphQLCustomScalar, GraphQLScalar or GraphQLNonScalar annotation");
			} else {
				Method method = (Method) fieldOrMethod;
				throw new GraphQLRequestPreparationException("The method '" + method.getName() + "' of the class '"
						+ method.getDeclaringClass().getName()
						+ "' has none of the GraphQLCustomScalar, GraphQLScalar or GraphQLNonScalar annotation");
			}
		}
	}

	/**
	 * Check if the given field is owned by the class of this {@link ObjectResponse}. This method returns the class for
	 * this field.
	 * 
	 * @param name
	 *            The name of the field we want to check
	 * @param shouldBeScalar
	 *            if true: also checks that the field is a scalar (throws a GraphQLRequestPreparationException if not).
	 *            If false: also checks that the field is not a scalar (throws a GraphQLRequestPreparationException if
	 *            not). If null: no check whether the field is a scalar or not
	 * @param owningClass
	 *            The class in which will search for name as a GraphQL field
	 * @return the class of this field
	 * @throws NullPointerException
	 *             if name is null
	 * @throws GraphQLRequestPreparationException
	 *             if the check is KO
	 */
	public Class<?> checkFieldOfGraphQLType(String name, Boolean shouldBeScalar, Class<?> owningClass)
			throws GraphQLRequestPreparationException {

		// Let's be sure that the identifier is a valid GraphQL identifier (also checks that it's not null)
		checkName(name);

		// Let's check that this fieldName is either a method name or a field of the class for this ObjectResponse.
		Class<?> fieldClass = null;

		Field field = graphqlUtils.getDeclaredField(owningClass, graphqlUtils.getJavaName(name), false);
		if (field != null) {
			// If we need to check that this field is (or is not) a scalar
			fieldClass = checkIsScalar(field, shouldBeScalar);
		}
		if (fieldClass == null && !owningClass.isInterface()) {
			// This class is a concrete class (not an interface). As the search field is not an attribute, the
			// owningClass should be a Query, a Mutation or a Subscription
			for (Method method : owningClass.getMethods()) {
				if (method.getName().equals(name)) {
					// If we need to check that this field is (or is not) a scalar
					fieldClass = checkIsScalar(name, method, shouldBeScalar);
					break;
				}
			}
		}
		if (fieldClass == null && owningClass.isInterface()) {
			// The class is an interface. So it's logical we didn't find this field as an attribute. Let's search for
			// the relevant setter
			String expectedMethodName = "get" + graphqlUtils.getPascalCase(name);
			for (Method method : owningClass.getDeclaredMethods()) {
				if (method.getName().equals(expectedMethodName)) {
					// If we need to check that this field is (or is not) a scalar
					fieldClass = checkIsScalar(name, method, shouldBeScalar);
					break;
				}
			}
		}

		if (fieldClass == null) {
			throw new GraphQLRequestPreparationException("The GraphQL type '" + owningClass.getSimpleName() + "' ("
					+ owningClass.getName() + ") has no field of name '" + name + "'");
		}

		return fieldClass;
	}

	/**
	 * This method retrieves the couple of name and values given in these parameters, stores them in a map where the key
	 * is the param name, and the value is the value of the {@link Map}.
	 * 
	 * @param paramsAndValues
	 *            A series of name and values : (paramName1, paramValue1, paramName2, paramValue2...). So there must be
	 *            an even number of items in this array. Empty arrays are allowed (that is no parameter name and
	 *            value).<BR/>
	 *            This series is sent by the developer's code, when it calls the request methods.
	 * @return The map with paramName1, paramName2 (...) are the keys, and paramValue1, paramValue2 (...) are the
	 *         associated content.
	 * @throws GraphQLRequestExecutionException
	 *             When a non-even number of parameters is sent to this method
	 * @throws ClassCastException
	 *             When a parameter name is not a String
	 */
	public Map<String, Object> generatesBindVariableValuesMap(Object[] paramsAndValues)
			throws GraphQLRequestExecutionException, ClassCastException {
		Map<String, Object> map = new HashMap<String, Object>();

		// If we get parameters and values, let's put them into the map
		if (paramsAndValues != null) {
			if (paramsAndValues.length % 2 != 0) {
				throw new GraphQLRequestExecutionException("An even number of parameters is expected, but "
						+ paramsAndValues.length
						+ " parameters where sent. This method expects a series of name and values : (paramName1, paramValue1, paramName2, paramValue2...)");
			}
			for (int i = 0; i < paramsAndValues.length; i += 2) {
				map.put((String) paramsAndValues[i], paramsAndValues[i + 1]);
			}
		}

		return map;
	}

	/**
	 * Parse a value, depending on the parameter type.
	 *
	 * @param parameterValue
	 * @param parameterType
	 * @param packageName
	 * @return
	 * @throws RuntimeException
	 *             When the value could be parsed
	 */
	public Object parseValueForInputParameter(Object parameterValue, String parameterType, Class<?> parameterClass) {

		// Let's check if this type is a Custom Scalar
		GraphQLScalarType graphQLScalarType = CustomScalarRegistryImpl.customScalarRegistry
				.getGraphQLCustomScalarType(parameterType);

		if (graphQLScalarType != null) {
			// This type is a Custom Scalar. Let's ask the CustomScalar implementation to translate this value.
			// Note: the GraphqQL ID is managed by specific CustomScalars, which is specific to the client or the server
			// mode (ID are String for the client, and UUID for the server)
			return graphQLScalarType.getCoercing().parseValue(parameterValue);
		} else if (parameterType.equals("Boolean")) {
			if (parameterValue instanceof Boolean) {
				// This should not occur
				return parameterValue;
			} else if (parameterValue instanceof String) {
				if (parameterValue.equals("true"))
					return Boolean.TRUE;
				else if (parameterValue.equals("false"))
					return Boolean.FALSE;
			}
			throw new RuntimeException(
					"Bad boolean value '" + parameterValue + "' for the parameter type '" + parameterType + "'");
		} else if (parameterType.equals("Float")) {
			// GraphQL Float are double precision numbers
			return Double.parseDouble((String) parameterValue);
		} else if (parameterType.equals("Int")) {
			return Integer.parseInt((String) parameterValue);
		} else if (parameterType.equals("Long")) {
			return Long.parseLong((String) parameterValue);
		} else if (parameterType.equals("String")) {
			return parameterValue;
		} else {
			// This type is not a Custom Scalar, so it must be a standard Scalar. Let's manage it
			if (parameterClass.isEnum()) {
				// This parameter is an enum. The parameterValue is one of its elements
				Method valueOf = graphqlUtils.getMethod("valueOf", parameterClass, String.class);
				return graphqlUtils.invokeMethod(valueOf, null, parameterValue);
			} else if (parameterClass.isAssignableFrom(Boolean.class)) {
				// This parameter is a boolean. Only true and false are valid boolean.
				if (!"true".equals(parameterValue) && !"false".equals(parameterValue)) {
					throw new RuntimeException("Only true and false are allowed values for booleans, but the value is '"
							+ parameterValue + "'");
				}
				return "true".equals(parameterValue);
			} else if (parameterClass.isAssignableFrom(Integer.class)) {
				return Integer.parseInt((String) parameterValue);
			} else if (parameterClass.isAssignableFrom(Float.class)) {
				return Float.parseFloat((String) parameterValue);
			}
		} // else (scalarType != null)

		// Too bad...
		throw new RuntimeException("Couldn't parse the value'" + parameterValue + "' for the parameter type '"
				+ parameterType + "': non managed GraphQL type (maybe a custom scalar is not properly registered?)");
	}

	/**
	 * Retrieves the GraphQL type name (as defined in the GraphQL schema), from the GraphQL annotation added in the
	 * generated code by the plugin.
	 * 
	 * @param clazz
	 * @return
	 */
	public String getGraphQLTypeNameFromClass(Class<?> clazz) {
		// Object
		GraphQLObjectType graphQLObjectType = clazz.getAnnotation(GraphQLObjectType.class);
		if (graphQLObjectType != null) {
			return graphQLObjectType.value();
		}

		// Interface
		GraphQLInterfaceType graphQLInterfaceType = clazz.getAnnotation(GraphQLInterfaceType.class);
		if (graphQLInterfaceType != null) {
			return graphQLInterfaceType.value();
		}

		// Union
		GraphQLUnionType graphQLUnionType = clazz.getAnnotation(GraphQLUnionType.class);
		if (graphQLUnionType != null) {
			return graphQLUnionType.value();
		}

		// Enum
		GraphQLEnumType graphQLEnumType = clazz.getAnnotation(GraphQLEnumType.class);
		if (graphQLEnumType != null) {
			return graphQLEnumType.value();
		}

		throw new RuntimeException("Could not find the GraphQL type for the class " + clazz.getName());
	}

	/**
	 * This method retrieves the {@link GraphQLScalarType} for a custom scalar field or method. {@link GraphQLScalar} is
	 * used in generated InputType and response type POJOs and {@link GraphQLCustomScalar} is used in some legacy
	 * generated response type POJOs.
	 *
	 * @param fieldOrMethod
	 *            The field or method of the generated POJO class
	 * @return the {@link GraphQLScalarType}
	 */
	public GraphQLScalarType getGraphQLCustomScalarType(AccessibleObject fieldOrMethod) {
		String graphQLTypeName;
		if (fieldOrMethod.getAnnotation(GraphQLScalar.class) != null) {
			graphQLTypeName = fieldOrMethod.getAnnotation(GraphQLScalar.class).graphQLTypeSimpleName();
		} else {
			graphQLTypeName = null;
		}
		if (graphQLTypeName != null) {
			return CustomScalarRegistryImpl.customScalarRegistry.getGraphQLCustomScalarType(graphQLTypeName);
		} else {
			return null;
		}
	}

	/**
	 * Returns the GraphQL scalar type for the given Standard or Custom Scalar name, as defined in the GraphQL schema.
	 * The {@link GraphQLScalarType} contains the method that allows to parse a String value, parse an AST value, or
	 * serialize the value (for instance to write it into a JSON string)
	 * 
	 * @param typeName
	 * @return The GraphQL type. Or null if not found (enum, object, input type, interface, union)
	 */
	public GraphQLScalarType getGraphQLScalarTypeFromName(String typeName) {

		// Is it a known type ?
		if (typeName.equals("String")) {
			return graphql.Scalars.GraphQLString;
		} else if (typeName.equals("Boolean")) {
			return graphql.Scalars.GraphQLBoolean;
		} else if (typeName.equals("Float")) {
			return graphql.Scalars.GraphQLFloat;
		} else if (typeName.equals("Int")) {
			return graphql.Scalars.GraphQLInt;
		} else if (typeName.equals("ID")) {
			return graphql.Scalars.GraphQLID;
		}

		return CustomScalarRegistryImpl.customScalarRegistry.getGraphQLCustomScalarType(typeName);
	}

}