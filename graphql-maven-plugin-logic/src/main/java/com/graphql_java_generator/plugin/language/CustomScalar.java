/**
 * 
 */
package com.graphql_java_generator.plugin.language;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * All types found in the GraphQL schema(s), and discovered during the GraphQL parsing, are instance of
 * {@link CustomScalar}.
 * 
 * @author EtienneSF
 */
public interface CustomScalar extends Type {

	public enum KindOfValue {
		/** An array of values */
		// ARRAY,
		// BOOLEAN
		// ENUM,
		/** Any decimal number that can be stored into a {@link BigDecimal} */
		FLOAT,
		/** Any integer value that can be stored in a {@link BigInteger} type */
		INT,
		/** A scalar serialized a an object */
		OBJECT,
		/** A value that is propagated surrounded by double quotes */
		STRING
	};

	/** The full class name for this custom scalar converter */
	String getCustomScalarConvertClassName();

	/** The kind of values that is written in the GraphQL request and response */
	KindOfValue getKindOfValue();

	default String getSerializedClassName() {

	}
}
