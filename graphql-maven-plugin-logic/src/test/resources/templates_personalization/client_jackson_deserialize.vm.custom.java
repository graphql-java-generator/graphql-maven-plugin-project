/** This template is custom **/
package ${pluginConfiguration.packageName};

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.graphql_java_generator.customscalars.CustomScalarRegistryImpl;
import com.graphql_java_generator.client.response.AbstractCustomScalarDeserializer;
import com.graphql_java_generator.customscalars.GraphQLScalarTypeDate;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

import graphql.schema.GraphQLScalarType;

#foreach($import in $imports)
import $import;
#end

/**
 * This class is a standard Deserializer for Jackson. It uses the {@link GraphQLScalarType} that is implemented by the project for this scalar
 */
public class CustomScalarDeserializer${object.name}  extends AbstractCustomScalarDeserializer<${object.classFullName}> {

	private static final long serialVersionUID = 1L;

	protected CustomScalarDeserializer${object.name}() {
		super(${object.classFullName}.class,
#if (${object.graphQLScalarTypeClass})
				new ${object.graphQLScalarTypeClass}()
#elseif (${object.graphQLScalarTypeStaticField})
				${object.graphQLScalarTypeStaticField}
#elseif (${object.graphQLScalarTypeGetter})
				${object.graphQLScalarTypeGetter}
#else
			${object.javaName} : you must define one of graphQLScalarTypeClass, graphQLScalarTypeStaticField or graphQLScalarTypeGetter (in the POM parameters for CustomScalars)
#end
				);
	}
}
