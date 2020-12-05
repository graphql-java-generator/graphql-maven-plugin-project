/**
 * 
 */
package org.allGraphQLCases.server.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Resource;

import org.allGraphQLCases.server.AllFieldCases;
import org.allGraphQLCases.server.Character;
import org.allGraphQLCases.server.Commented;
import org.allGraphQLCases.server.Droid;
import org.allGraphQLCases.server.Human;
import org.allGraphQLCases.server.WithID;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.annotation.GraphQLScalar;

/**
 * This sample is not connected to a database : its aim is just to check all the GraphQL schema possibilities, to insure
 * that everything works correctly, from the client to the server.<BR/>
 * This class generates the needed data on the fly.
 * 
 * @author etienne-sf
 */
@Component
public class DataGenerator {

	@Resource
	GraphqlUtils graphqlUtils;

	public static final Random RANDOM = new Random();
	private static final int NB_ITEM_PER_LIST = 0;

	/**
	 * Contains for each java interface that has to be instanciate by {@link #generateInstance(Class)}, the concrete
	 * class the must be instanciated for it. For instance: for the key {@link org.allGraphQLCases.server.Character},
	 * there is the concrete class {@link CharacterImpl}.
	 */
	final Map<Class<?>, Class<?>> interfaceImplementations;

	public DataGenerator() {
		interfaceImplementations = new HashMap<>();
		interfaceImplementations.put(Character.class, Human.class);
		interfaceImplementations.put(Character.class, Droid.class);
		interfaceImplementations.put(Commented.class, Human.class);
		interfaceImplementations.put(WithID.class, AllFieldCases.class);
		interfaceImplementations.put(WithID.class, Human.class);
		interfaceImplementations.put(WithID.class, Droid.class);
	}

	/**
	 * Generates a new instance of the given class, with all fields containing a random value. If a field is itself a
	 * GraphQL Type (and not a Scalar), then all its field are recursively filled.
	 * 
	 * @param <T>
	 *            Any GraphQL type (scalar or not)
	 * @param clazz
	 *            A class of a Scalar GraphQL type, or a class of a GraphQL type, as defined in a GraphQL schema.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T generateInstance(Class<T> clazzToReturn) {
		Class<? extends T> clazzToInstanciate = (interfaceImplementations.containsKey(clazzToReturn))
				? (Class<? extends T>) interfaceImplementations.get(clazzToReturn)
				: clazzToReturn;

		if (clazzToReturn.isEnum()) {
			// enum are a special case
			int x = RANDOM.nextInt(clazzToReturn.getEnumConstants().length);
			return clazzToReturn.getEnumConstants()[x];

		} else if (clazzToReturn.equals(Boolean.class)) {
			return (T) (Boolean) (RANDOM.nextBoolean());

		} else {

			// Standard case
			T t;

			try {
				t = clazzToInstanciate.getDeclaredConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new RuntimeException("Could not create a new instance of " + clazzToInstanciate.getName(), e);
			}

			for (Field f : clazzToInstanciate.getDeclaredFields()) {

				// We fill only Scalar fields.
				if (f.getAnnotation(GraphQLScalar.class) != null) {
					Object val;
					Class<?>[] interfaces = f.getType().getInterfaces();
					if (f.getType() == List.class
							|| (interfaces != null && Arrays.asList(interfaces).contains(List.class))) {
						// Hum, it's a list. Let's generate a list of ten items
						List<Object> list = new ArrayList<>(NB_ITEM_PER_LIST);
						for (int i = 0; i < NB_ITEM_PER_LIST; i += 1) {
							list.add(generateValue(f.getType()));
						}
						val = list;
					} else {
						val = generateValue(f.getType());
					}

					graphqlUtils.invokeSetter(t, f, val);
				}
			}
			return t;
		}
	}

	/**
	 * Returns a list of instances of the given type. The instances are filled by {@link #generateInstance(Class)}
	 * 
	 * @param <T>
	 *            Any GraphQL type (scalar or not)
	 * @param clazz
	 * @param nbItems
	 *            The number of items expected in the returned list
	 * @return
	 */
	public <T> List<T> generateInstanceList(Class<T> clazz, int nbItems) {
		List<T> list = new ArrayList<T>();

		for (int i = 0; i < nbItems; i += 1) {
			list.add(generateInstance(clazz));
		} // for

		return list;
	}

	@SuppressWarnings("deprecation")
	private Object generateValue(Class<?> type) {
		if (type == Boolean.class) {
			return RANDOM.nextBoolean();
		} else if (type == Date.class) {
			return new Date(RANDOM.nextInt(3000), RANDOM.nextInt(12), RANDOM.nextInt(29));
		} else if (type == Double.class) {
			return (double) (Math.random() * Double.MAX_VALUE);
		} else if (type.isEnum()) {
			int x = RANDOM.nextInt(type.getEnumConstants().length);
			return type.getEnumConstants()[x];
		} else if (type == Float.class) {
			return (float) (Math.random() * Float.MAX_VALUE);
		} else if (type == Integer.class) {
			return RANDOM.nextInt();
		} else if (type == Long.class) {
			return RANDOM.nextLong();
		} else if (type == String.class) {
			return "Random String (" + RANDOM.nextInt(99999999) + ")";
		} else if (type == UUID.class) {
			return UUID.randomUUID();
		} else {
			throw new RuntimeException("Non managed Scalar type, when generating data: " + type.getName());
		}

	}

}
