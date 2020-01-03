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

import org.springframework.stereotype.Component;

import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.client.response.GraphQLRequestExecutionException;
import com.graphql_java_generator.client.response.GraphQLRequestPreparationException;

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
			throw new GraphQLRequestPreparationException("<" + graphqlIdentifier + "> is not a valid GraphQL name");
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
		for (java.lang.reflect.Field field : owningClass.getDeclaredFields()) {
			if (field.getName().equals(name)) {
				// If we need to check that this field is (or is not) a scalar
				fieldClass = checkIsScalar(field, shouldBeScalar);
				break;
			}
		}
		if (fieldClass == null && !owningClass.isInterface()) {
			// This class is a concrete class (not an interface). As the search field is not an attribute, the
			// owningClass should be a Query, a Mutation or a Subscription
			for (Method method : owningClass.getDeclaredMethods()) {
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
			String expectedMethodName = "get" + getPascalCase(name);
			for (Method method : owningClass.getDeclaredMethods()) {
				if (method.getName().equals(expectedMethodName)) {
					// If we need to check that this field is (or is not) a scalar
					fieldClass = checkIsScalar(name, method, shouldBeScalar);
					break;
				}
			}
		}

		if (fieldClass == null) {
			throw new GraphQLRequestPreparationException(
					"The GraphQL type <" + owningClass.getSimpleName() + "> has no field of name <" + name + ">");
		}

		return fieldClass;
	}

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
	 * This method checks whether the given field (as an attribute) of the given class is a GraphQL scalar, or not,
	 * depending on shouldBeScalar.
	 * 
	 * @param field
	 *            The field whose type should be (or not) a scalar
	 * @param shouldBeScalar
	 *            if true, this method checks that field's type is a scalar (if false, checks that it is not a scalar)
	 * @throws GraphQLRequestPreparationException
	 */
	Class<?> checkIsScalar(java.lang.reflect.Field field, Boolean shouldBeScalar)
			throws GraphQLRequestPreparationException {
		// All types are generated in the same package. So, if the package for the field type is the same as the
		// package for our class, this means that the field is not a scalar
		// Note: we'll perhaps have to change that, depending on the way we manage scalar

		boolean isScalar = isScalar(field);

		if (shouldBeScalar != null) {
			if (shouldBeScalar & !isScalar) {
				throw new GraphQLRequestPreparationException("The field <" + field.getName() + "> of the GraphQL type <"
						+ field.getDeclaringClass().getName()
						+ "> is not a GraphQLScalar. At least one field must be defined for the server response.");
			}
			if (!shouldBeScalar & isScalar) {
				throw new GraphQLRequestPreparationException("The field <" + field.getName() + "> of the GraphQL type <"
						+ field.getDeclaringClass().getName()
						+ "> is not a GraphQLScalar. At least one field must be defined for the server response.");
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
	Class<?> checkIsScalar(String fieldName, Method method, Boolean shouldBeScalar)
			throws GraphQLRequestPreparationException {
		// All types are generated in the same package. So, if the package for the field type is the same as the
		// package for our class, this means that the field is not a scalar
		// Note: we'll perhaps have to change that, depending on the way we manage scalar
		boolean isScalar = isScalar(method);

		if (method.getReturnType() == null) {
			throw new GraphQLRequestPreparationException("There is a method of name <" + fieldName
					+ "> in the GraphQL type <" + method.getDeclaringClass().getName()
					+ ">, but this method is a void method: it can't represent the <" + fieldName + "> GraphQL field");
		}

		if (shouldBeScalar != null) {
			if (shouldBeScalar && !isScalar) {
				throw new GraphQLRequestPreparationException(
						"The field <" + fieldName + "> (accessed through its getter: " + method.getName()
								+ ">) of the GraphQL type <" + method.getDeclaringClass().getName()
								+ "> should be a scalar. But is is actually not a GraphQLScalar");
			}
			if (!shouldBeScalar && isScalar) {
				throw new GraphQLRequestPreparationException(
						"The field <" + fieldName + "> (accessed through its getter: <" + method.getName()
								+ ">) of the GraphQL type <" + method.getDeclaringClass().getName()
								+ "> should not be a scalar. But is is actually a GraphQLScalar");
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
			// No GraphQLScalar or GraphQLNonScalar annotation: let's thrown an internal error.
			if (fieldOrMethod instanceof Field) {
				Field field = (Field) fieldOrMethod;
				throw new GraphQLRequestPreparationException(
						"The field <" + field.getName() + "> of the class <" + field.getDeclaringClass().getName()
								+ "> has none of the GraphQLScalar and GraphQLNonScalar annotation");
			} else {
				Method method = (Method) fieldOrMethod;
				throw new GraphQLRequestPreparationException(
						"The method <" + method.getName() + "> of the class <" + method.getDeclaringClass().getName()
								+ "> has none of the GraphQLScalar and GraphQLNonScalar annotation");
			}
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
			// Ok, at least on of GraphQLScalar and GraphQLNonScalar annotation is set.
			return fieldOrMethod.getAnnotation(GraphQLScalar.class).graphqlType();
		} else if (fieldOrMethod.getAnnotation(GraphQLNonScalar.class) != null) {
			// Ok, at least on of GraphQLScalar and GraphQLNonScalar annotation is set.
			return fieldOrMethod.getAnnotation(GraphQLNonScalar.class).graphqlType();
		} else {
			// No GraphQLScalar or GraphQLNonScalar annotation: let's thrown an internal error.
			if (fieldOrMethod instanceof Field) {
				Field field = (Field) fieldOrMethod;
				throw new GraphQLRequestPreparationException(
						"The field <" + field.getName() + "> of the class <" + field.getDeclaringClass().getName()
								+ "> has none of the GraphQLScalar and GraphQLNonScalar annotation");
			} else {
				Method method = (Method) fieldOrMethod;
				throw new GraphQLRequestPreparationException(
						"The method <" + method.getName() + "> of the class <" + method.getDeclaringClass().getName()
								+ "> has none of the GraphQLScalar and GraphQLNonScalar annotation");
			}
		}
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
}
