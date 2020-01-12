/**
 * 
 */
package org.allGraphQLCases.server.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Resource;

import org.allGraphQLCases.server.Character;
import org.allGraphQLCases.server.CharacterImpl;
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

	private static final Random RANDOM = new Random();

	/**
	 * Contains for each java interface that has to be instanciate by {@link #generateInstance(Class)}, the concrete
	 * class the must be instanciated for it. For instance: for the key {@link org.allGraphQLCases.server.Character},
	 * there is the concrete class {@link CharacterImpl}.
	 */
	final Map<Class<?>, Class<?>> interfaceImplementations;

	public DataGenerator() {
		interfaceImplementations = new HashMap<>();
		interfaceImplementations.put(Character.class, CharacterImpl.class);
	}

	/**
	 * Generates a new instance of the given class, with all fields containing a random value. If a field is itself a
	 * GraphQL Type (and not a Scalar), then all its field are recursively filled.
	 * 
	 * @param <T>
	 *            Any GraphQL type (scalar or not)
	 * @param clazz
	 *            A class of a Scalar GraphQL type, or a class of a GraphQL type, as defined in a GraphQL schema.
	 * @param maxSubLevels
	 *            The maximum number of items to embed. For instance if 0, not subobject will be created (only scalar
	 *            fields will be filled). With maxSubLevels set to 1, all field that are GraphQL type are created. But
	 *            if this GraphQL types contain themselves subobjects (field that are GraphQL type and not scalar), then
	 *            these fields are left empty.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	<T> T generateInstance(Class<T> clazzToReturn, int maxSubLevels) {
		Class<? extends T> clazzToInstanciate = (interfaceImplementations.containsKey(clazzToReturn))
				? (Class<? extends T>) interfaceImplementations.get(clazzToReturn)
				: clazzToReturn;
		T t;

		try {
			t = clazzToInstanciate.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Could not create a new instance of " + clazzToInstanciate.getName());
		}

		for (Field f : clazzToInstanciate.getDeclaredFields()) {
			Object val;

			Class<?>[] interfaces = f.getType().getInterfaces();
			if (f.getType() == List.class || (interfaces != null && Arrays.asList(interfaces).contains(List.class))) {
				// Hum, it's a list. Let's generate a list of ten items
				List<Object> list = new ArrayList<>(10);
				for (int i = 0; i < 10; i += 1) {
					list.add(generateFieldValue(f, maxSubLevels));
				}
				val = list;
			} else {
				val = generateFieldValue(f, maxSubLevels);
			}

			graphqlUtils.invokeSetter(t, f, val);
		}
		return t;
	}

	/**
	 * Returns a list of instances of the given type.
	 * 
	 * @param <T>
	 *            Any GraphQL type (scalar or not)
	 * @param clazz
	 * @param maxSubLevels
	 *            The maximum number of items to embed. For instance if 0, not subobject will be created (only scalar
	 *            fields will be filled). With maxSubLevels set to 1, all field that are GraphQL type are created. But
	 *            if this GraphQL types contain themselves subobjects (field that are GraphQL type and not scalar), then
	 *            these fields are left empty.
	 * @param nbItems
	 *            The number of items expected in the returned list
	 * @return
	 */
	<T> List<T> generateInstanceList(Class<T> clazz, int maxSubLevels, int nbItems) {
		List<T> list = new ArrayList<T>();

		for (int i = 0; i <= nbItems; i += 1) {
			list.add(generateInstance(clazz, maxSubLevels));
		} // for

		return list;
	}

	/**
	 * Returns a value valid for the given field. The type of value is indicated by the [@link GraphQLScalar} or
	 * {@link GraphQLNonScalar} annotation that MUST be on the field. It's up to the caller to manage field that are
	 * lists (by calling these method once for each item to be created in the list).. If this value is a GraphQL type
	 * (input type or standard type), then all its fields are also filled.
	 * 
	 * @param f
	 * @param maxSubLevels
	 *            The maximum number of items to embed. For instance if 0, not subobject will be created (only scalar
	 *            fields will be filled). With maxSubLevels set to 1, all field that are GraphQL type are created. But
	 *            if this GraphQL types contain themselves subobjects (field that are GraphQL type and not scalar), then
	 *            these fields are left empty.
	 * @return
	 */
	private Object generateFieldValue(Field f, int maxSubLevels) {

		if (f.getAnnotation(GraphQLScalar.class) != null) {

			// This is a scalar. Let's create a new value for it.
			return generateValue(f.getAnnotation(GraphQLScalar.class).graphqlType());

		} else if (f.getAnnotation(GraphQLNonScalar.class) != null) {

			if (maxSubLevels == 0) {
				return null;
			} else {
				// This is a non scalar object. And at least one level of subojects is expected. We need to create a new
				// instance, and fill its fields.
				// Let's recurse once:
				return generateInstance(f.getAnnotation(GraphQLNonScalar.class).graphqlType(), maxSubLevels - 1);
			}

		} else {
			throw new RuntimeException("Non managed type, when generating data: " + f.getType().getName()
					+ ". It's not a Scalar, nor a non Scalar (missing both GraphQLScalar and GraphQLNonScalar annotation)");
		}
	}

	private Object generateValue(Class<?> type) {
		if (type == String.class) {
			return "Random String (" + RANDOM.nextInt(99999999) + ")";
		} else if (type == UUID.class) {
			return UUID.randomUUID();
		} else if (type == Integer.class) {
			return RANDOM.nextInt();
		} else if (type == Float.class) {
			return (float) (Math.random() * Float.MAX_VALUE);
		} else if (type == Boolean.class) {
			return RANDOM.nextBoolean();
		} else if (type.isEnum()) {
			int x = RANDOM.nextInt(type.getEnumConstants().length);
			return type.getEnumConstants()[x];
		} else {
			throw new RuntimeException("Non managed Scalar type, when generating data: " + type.getName());
		}

	}

}
