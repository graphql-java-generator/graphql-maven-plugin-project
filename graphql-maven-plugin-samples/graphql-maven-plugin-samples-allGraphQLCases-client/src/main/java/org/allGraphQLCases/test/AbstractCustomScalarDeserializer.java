/**
 * 
 */
package org.allGraphQLCases.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * Jackson Deserializer for Custom Scalar.
 * 
 * @author etienne-sf
 */
public abstract class AbstractCustomScalarDeserializer<T> extends StdDeserializer<T> {

	private static final long serialVersionUID = 1L;

	/** Logger for this class */
	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * The class that can deserialize the items in the list. This recursion allows to deserialize list of lists.<BR/>
	 * This field must be null to deserialize non list objects. And it's mandatory, to deserialize lists.
	 */
	AbstractCustomScalarDeserializer<?> itemDeserializer;

	// final int nbListLevels;

	/**
	 * The {@link GraphQLScalarType} instance that manages this Custom Scalar. It's used to deserialize the value read
	 * on the Jackson response from the server
	 */
	final GraphQLScalarType graphQLScalarType;

	final Class<?> handledType;

	/**
	 * The constructor, for non list field. This constructor builds a custom jackson deserialize, for field that can't
	 * be deserialize by jackson alone. Typically: GraphQL custom scalars.
	 * 
	 * @param handledType
	 *            The java type, that will be sent to the jackson {@link StdDeserializer}
	 * @param graphQLScalarType
	 *            The GraphQL Type, as it has been defined in the schema. It can be a custom scalar (with any value of
	 *            nbListLevels), or any GraphQL scalar, custom scalar, object, input type or interface (if
	 *            nbListLevels>0)
	 */
	protected AbstractCustomScalarDeserializer(Class<?> handledType, GraphQLScalarType graphQLScalarType) {
		this(null, handledType, graphQLScalarType);
	}

	/**
	 * @param itemDeserializer
	 *            The class that can deserialize the items in the list. This recursion allows to deserialize list of
	 *            lists.<BR/>
	 *            This field must be null to deserialize non list objects. And it's mandatory, to deserialize lists.
	 * @param handledType
	 *            The java type, that will be sent to the jackson {@link StdDeserializer}
	 * @param graphQLScalarType
	 *            The GraphQL Type, as it has been defined in the schema. It can be a custom scalar (with any value of
	 *            nbListLevels), or any GraphQL scalar, custom scalar, object, input type or interface (if
	 *            nbListLevels>0)
	 */
	protected AbstractCustomScalarDeserializer(AbstractCustomScalarDeserializer<?> itemDeserializer,
			Class<?> handledType, GraphQLScalarType graphQLScalarType) {
		super(handledType);

		// if (nbListLevels <= 0) {
		// throw new IllegalArgumentException("nbListLevels must be 1 or more");
		// }
		// graphQLScalarType is mandatory
		if (graphQLScalarType == null) {
			throw new NullPointerException("[internal error] graphQLScalarType is mandatory");
		}

		// this.nbListLevels = nbListLevels;
		this.itemDeserializer = itemDeserializer;
		this.handledType = handledType;
		this.graphQLScalarType = graphQLScalarType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		if (itemDeserializer != null) {

			logger.trace("Reading a {} token", p.currentToken());
			JsonToken currentToken = p.currentToken();

			// This deserializer manages lists.
			List<Object> list = new ArrayList<>();
			// Let's loop until we find the end of the array
			while (!p.nextToken().equals(JsonToken.END_ARRAY)) {
				currentToken = p.currentToken();
				// If the current token is a list, we should advance to its first item.
				if (p.currentToken().equals(JsonToken.START_ARRAY)) {
					// We're starting a sublist. The itemDeserializer should manage sublist. So its own itemDeserializer
					// must be non-null
					if (itemDeserializer.itemDeserializer == null) {
						throw new JsonParseException(p, "Found a " + p.currentToken().asString()
								+ " token, but the itemDeserializer can't manage a sublist. The number of embedded list doesn't match the defined deserializer for the GraphQL field.");
					}
					// Ok. Let's deserialize the sublist. We have to advance to the first data token.
					logger.trace("Sending deserialization for a sublist to {} with token {}",
							itemDeserializer.getClass().getName(), p.currentToken());
					list.add(itemDeserializer.deserialize(p, ctxt));
					currentToken = p.currentToken();
				} else {
					// We've found a value. The itemDeserializer should not manage sublist. So its own itemDeserializer
					// must be null
					if (itemDeserializer.itemDeserializer != null) {
						throw new JsonParseException(p, "Found a " + p.currentToken().asString()
								+ " token, but the itemDeserializer expects a sublist. The number of embedded list doesn't match the defined deserializer for the GraphQL field.");
					}
					logger.trace("Sending deserialization for a value to {} with token {}",
							itemDeserializer.getClass().getName(), p.currentToken());
					list.add(itemDeserializer.deserialize(p, ctxt));
					currentToken = p.currentToken();
				}
			} // while
			logger.trace("The read list contains {} items", list.size());
			return (T) list;

		} else {

			// This deserializer manages custom scalars (not list).
			Value<?> value;
			switch (p.currentToken()) {
			case VALUE_FALSE:
			case VALUE_TRUE:
				value = new BooleanValue(p.getBooleanValue());
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
				throw new JsonParseException(p, "Non managed JSON token: " + p.currentToken());
			}
			if (value == null) {
				return null;
			} else {
				return (T) graphQLScalarType.getCoercing().parseLiteral(value);
			}

		}
	}

}
