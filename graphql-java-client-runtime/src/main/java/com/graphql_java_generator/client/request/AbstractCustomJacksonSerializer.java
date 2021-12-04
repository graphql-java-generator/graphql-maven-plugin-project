package com.graphql_java_generator.client.request;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import graphql.schema.GraphQLScalarType;

/**
 * Jackson Deserializer for lists and Custom Scalars.
 * 
 * @author etienne-sf
 */
public abstract class AbstractCustomJacksonSerializer<T> extends StdSerializer<T> {

	private static final long serialVersionUID = 1L;

	/** The depth of the GraphQL list. 0 means it's not a list. 1 is a standard list. 2 is a list of list... */
	final int listLevel;

	/**
	 * The {@link GraphQLScalarType} instance that manages this Custom Scalar. It's used to deserialize the value read
	 * on the Jackson response from the server It is mandatory for custom scalars, and must null for other data types.
	 */
	final GraphQLScalarType graphQLScalarType;

	/**
	 * @param graphQLScalarType
	 *            The {@link GraphQLScalarType} instance that manages this Custom Scalar. It's used to deserialize the
	 *            value read on the Jackson response from the server It is mandatory for custom scalars, and must null
	 *            for other data types.
	 * @param clazz
	 *            The <T> class. It's a <code>Class<?></code> because of issues with generics.
	 * @param listLevel
	 *            The depth of the GraphQL list. 0 means it's not a list. 1 is a standard list. 2 is a list of list...
	 */
	public AbstractCustomJacksonSerializer(Class<?> clazz, int listLevel, GraphQLScalarType graphQLScalarType) {
		super(clazz, false);
		this.listLevel = listLevel;
		this.graphQLScalarType = graphQLScalarType;
	}

	@Override
	public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		execSerialization(value, listLevel, gen);
	}

	/**
	 * Recursive method to manage lists serialization
	 * 
	 * @param value
	 * @param fieldTypeParam
	 * @param listLevelParam
	 * @param gen
	 * @throws IOException
	 */
	private void execSerialization(Object value, int listLevelParam, JsonGenerator gen) throws IOException {
		if (listLevelParam == 0) {
			if (graphQLScalarType != null)
				gen.writeObject(graphQLScalarType.getCoercing().serialize(value));
			else
				gen.writeObject(value);
		} else if (!(value instanceof List)) {
			throw new IllegalArgumentException("Expecting a list with depth (number of level of list inclusion) of "
					+ listLevel + ", but the provided value's depth is " + (listLevel - listLevelParam)
					+ " (a level of 0 means: it's not a list)");
		} else {
			gen.writeStartArray();
			for (Object v : (List<?>) value) {
				execSerialization(v, listLevelParam - 1, gen);
			}
			gen.writeEndArray();
		}
	}

	// /**
	// * Write a unitary value on the given {@link JsonGenerator}, with the relevant writeXxx() methods
	// *
	// * @param value
	// * @param gen
	// * @throws IOException
	// */
	// protected abstract void writeValue(T value, JsonGenerator gen) throws IOException;

}
