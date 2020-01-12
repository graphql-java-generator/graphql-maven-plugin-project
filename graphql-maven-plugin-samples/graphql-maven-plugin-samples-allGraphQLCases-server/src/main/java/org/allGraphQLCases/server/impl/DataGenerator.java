/**
 * 
 */
package org.allGraphQLCases.server.impl;

import java.lang.reflect.Field;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLScalar;

/**
 * This sample is not connected to a database : its aim is just to check all the GraphQL schema possibilities, to insure
 * that everything works correctly, from the client to the server.<BR/>
 * This class generates the needed data on the fly.
 * 
 * @author EtienneSF
 */
@Component
public class DataGenerator {

	@Resource
	GraphqlUtils graphqlUtils;

	/**
	 * Generates a new instance of the given class, with all fields containing a random value. If a field is itself a
	 * GraphQL Type (and not a Scalar), then all its field are recursively filled.
	 * 
	 * @param <T>
	 * @param clazz
	 *            A class of a Scalar GraphQL type, or a class of a GraphQL type, as defined in a GraphQL schema.
	 * @return
	 */
	<T> T generateInstance(Class<T> clazz) {
		T t;
		try {
			t = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Could not create a new instance of " + clazz.getName());
		}

		for (Field f : clazz.getDeclaredFields()) {
			Object val = generateFieldValue(f);
			graphqlUtils.invokeSetter(t, f, val);
		}
		return t;
	}

	private Object generateFieldValue(Field f) {
		if (f.getAnnotation(GraphQLScalar.class) != null) {

			// This is a scalar. Let's create a new value for it.
			if (f.getType() == String.class) {
				return "Random String (" + ((int) Math.random() * 1000000) + ")";
			} else if (f.getType() == UUID.class) {
				return UUID.randomUUID();
			} else if (f.getType() == Integer.class) {
				return (int) (Math.random() * Integer.MAX_VALUE);
			} else if (f.getType() == Float.class) {
				return (float) (Math.random() * Float.MAX_VALUE);
			} else if (f.getType() == Boolean.class) {
				return Math.random() > 0.5;
			} else {
				throw new RuntimeException("Non managed Scalar type, when generating data: " + f.getType().getName());
			}

		} else if (f.getAnnotation(GraphQLNonScalar.class) != null) {

			// This is a non scalar object. We need to create a new instance, and fill its fields.
			// Let's recurse once:
			return generateInstance(f.getType());

		} else {
			throw new RuntimeException("Non managed type, when generating data: " + f.getType().getName()
					+ ". It's not a Scalar, nor a non Scalar (missing both GraphQLScalar and GraphQLNonScalar annotation)");
		}
	}

}
