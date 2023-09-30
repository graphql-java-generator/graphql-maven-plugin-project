/**
 * 
 */
package com.graphql_java_generator.client.response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import graphql.language.BooleanValue;
import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.GraphQLScalarType;

/**
 * Jackson Deserializer for lists and Custom Scalars.
 * 
 * @author etienne-sf
 */
public abstract class AbstractCustomJacksonDeserializer<T> extends StdDeserializer<T> {

	private static final long serialVersionUID = 1L;

	/**
	 * The class that can deserialize the items in the list. This recursion allows to deserialize list of lists.<BR/>
	 * This field must be null to deserialize non list objects. And it's mandatory, to deserialize lists.
	 */
	AbstractCustomJacksonDeserializer<?> itemDeserializer;

	// final int nbListLevels;

	/**
	 * The {@link GraphQLScalarType} instance that manages this Custom Scalar. It's used to deserialize the value read
	 * on the Jackson response from the server It is mandatory for custom scalars, and must null for other data types.
	 */
	final GraphQLScalarType graphQLScalarType;

	/**
	 * This indicates if this deserializer manages list of GraphQL values, or a GraphQL scalar. We can't just read the
	 * kind of JSON token, as it would be possible (very unlikely, but not impossible) that a custom scalar stores its
	 * content as a JSON list. So we need our particular way of knowing of this deserialize expects a list or final
	 * value (from a GraphQL or java view point).
	 */
	final boolean list;

	/**
	 * The java type that contains the value of the final items, in the list. For instance for a GraphQL type
	 * [[Boolean]], the handle type is {@link Boolean}. It is sent to the jackson {@link StdDeserializer}. It is also
	 * used when the recursion of the nested list is at the deepest level, and the read token is the "real" value.
	 */
	final Class<?> handledType;

	/**
	 * The constructor, for non list field. This constructor builds a custom jackson deserialize, for field that can't
	 * be deserialize by jackson alone. Typically: GraphQL custom scalars.
	 * 
	 * @param handledType
	 *            The java type that contains the value of the final items, in the list. For instance for a GraphQL type
	 *            [[Boolean]], the handle type is {@link Boolean}. It is sent to the jackson {@link StdDeserializer}. It
	 *            is also used when the recursion of the nested list is at the deepest level, and the read token is the
	 *            "real" value.
	 * @param graphQLScalarType
	 *            The {@link GraphQLScalarType} instance that manages this Custom Scalar. It's used to deserialize the
	 *            value read on the Jackson response from the server It is mandatory for custom scalars, and must null
	 *            for other data types.
	 */
	protected AbstractCustomJacksonDeserializer(Class<?> handledType, GraphQLScalarType graphQLScalarType) {
		this(null, false, handledType, graphQLScalarType);
	}

	/**
	 * @param itemDeserializer
	 *            The class that can deserialize the items in the list. This recursion allows to deserialize list of
	 *            lists.<BR/>
	 *            This field must be null to deserialize non list objects. And it's mandatory, to deserialize lists.
	 * @param handledType
	 *            The java type that contains the value of the final items, in the list. For instance for a GraphQL type
	 *            [[Boolean]], the handle type is {@link Boolean}. It is sent to the jackson {@link StdDeserializer}. It
	 *            is also used when the recursion of the nested list is at the deepest level, and the read token is the
	 *            "real" value.
	 * @param graphQLScalarType
	 *            The {@link GraphQLScalarType} instance that manages this Custom Scalar. It's used to deserialize the
	 *            value read on the Jackson response from the server It is mandatory for custom scalars, and must null
	 *            for other data types.
	 */
	protected AbstractCustomJacksonDeserializer(AbstractCustomJacksonDeserializer<?> itemDeserializer, boolean list,
			Class<?> handledType, GraphQLScalarType graphQLScalarType) {
		super(handledType);
		this.itemDeserializer = itemDeserializer;
		this.list = list;
		this.handledType = handledType;
		this.graphQLScalarType = graphQLScalarType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {

		if (p.currentToken().equals(JsonToken.VALUE_NULL)) {
			return null;
		} else if (this.list) {

			if (!p.currentToken().equals(JsonToken.START_ARRAY)) {
				// Oups
				throw new JsonParseException(p, "Found a " + p.currentToken().asString() //$NON-NLS-1$
						+ " token, but the current deserializer expects a list"); //$NON-NLS-1$
			}

			// We're at the beginning of a list. Let's read it
			List<Object> returnedList = new ArrayList<>();
			// Let's loop until we find the end of the array
			while (!p.nextToken().equals(JsonToken.END_ARRAY)) {
				if (p.currentToken().equals(JsonToken.START_ARRAY)) {
					// We're starting a sublist.
					if (this.itemDeserializer == null) {
						throw new JsonParseException(p, "Found a " + p.currentToken().asString() //$NON-NLS-1$
								+ " JSON token, but the itemDeserializer is not defined. This JSON token can not be handled."); //$NON-NLS-1$
					} else if (!this.itemDeserializer.list) {
						throw new JsonParseException(p, "Found a " + p.currentToken().asString() //$NON-NLS-1$
								+ " JSON token, but the itemDeserializer doesn't manage list. Hint: The number of embedded lists doesn't match the defined deserializer for the GraphQL field."); //$NON-NLS-1$
					} else {
						// Ok. Let's deserialize the sublist.
						returnedList.add(this.itemDeserializer.deserialize(p, ctxt));
					}
				} else if (this.itemDeserializer != null) {
					// We've found a final value (not a list).
					if (p.currentToken().equals(JsonToken.VALUE_NULL)) {
						returnedList.add(null);
					} else if (this.itemDeserializer.list) {
						throw new JsonParseException(p, "Found a " + p.currentToken().asString() //$NON-NLS-1$
								+ " JSON token, but the itemDeserializer expects a list. Hint: the number of embedded lists doesn't match the defined deserializer for the GraphQL field."); //$NON-NLS-1$
					} else {
						returnedList.add(this.itemDeserializer.deserialize(p, ctxt));
					}
				} else {
					// It's a final value, and it is not a custom scalar (otherwise, itemDeserializer would be defined)
					// Let's let Jackson parse this value.
					Object o = p.readValueAs(this.handledType);
					returnedList.add(o);
				}
			} // while
			return (T) returnedList;

		} else if (this.itemDeserializer != null) {

			// We're not in a list, and a deserializer has been defined. Let's use it to deserialize this value
			return (T) this.itemDeserializer.deserialize(p, ctxt);

		} else if (this.graphQLScalarType == null) {

			// Too bad
			throw new JsonParseException(p,
					"Having to parse a " + p.currentToken() + ", but there is no graphQLScalarType defined"); //$NON-NLS-1$ //$NON-NLS-2$

		} else {

			// This deserializer manages custom scalars (not list).
			Value<?> value;
			switch (p.currentToken()) {
			case VALUE_FALSE:
			case VALUE_TRUE:
				value = new BooleanValue(p.getBooleanValue());
				break;
			case VALUE_NUMBER_FLOAT:
				value = new FloatValue(p.getDecimalValue());
				break;
			case VALUE_NUMBER_INT:
				value = new IntValue(p.getBigIntegerValue());
				break;
			case VALUE_STRING:
				value = new StringValue(p.getText());
				break;
			case VALUE_NULL:
				value = null;
				break;
			default:
				throw new JsonParseException(p, "Non managed JSON token: " + p.currentToken()); //$NON-NLS-1$
			}
			if (value == null) {
				return null;
			} else {
				return (T) this.graphQLScalarType.getCoercing().parseLiteral(value);
			}

		}
	}

}
