/** This template is custom **/
/**
 * 
 */
package com.graphql_java_generator.client.domain.forum;

import com.graphql_java_generator.client.response.AbstractCustomScalarDeserializer;

/**
 * @author EtienneSF
 */
public class CustomScalarDeserializer${customScalar.name} extends AbstractCustomScalarDeserializer<${customScalar.classFullName}> {

	private static final long serialVersionUID = 1L;

	protected CustomScalarDeserializerDate() {
		super(${customScalar.classFullName}.class, new ${customScalar.customScalarConvertClassName});
	}

}
