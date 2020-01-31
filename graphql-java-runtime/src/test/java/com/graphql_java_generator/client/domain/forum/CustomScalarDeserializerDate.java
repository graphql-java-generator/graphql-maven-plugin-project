/**
 * 
 */
package com.graphql_java_generator.client.domain.forum;

import com.graphql_java_generator.client.response.AbstractCustomScalarDeserializer;
import com.graphql_java_generator.customscalars.GraphQLScalarTypeDate;

/**
 * @author EtienneSF
 */
public class CustomScalarDeserializerDate extends AbstractCustomScalarDeserializer<java.util.Date> {

	private static final long serialVersionUID = 1L;

	protected CustomScalarDeserializerDate() {
		super(java.util.Date.class, new GraphQLScalarTypeDate());
	}

}
