package ${pluginConfiguration.packageName};

import java.io.IOException;

import org.springframework.boot.json.JsonParseException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.graphql_java_generator.CustomScalarRegistryImpl;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

#foreach($import in $imports)
import $import;
#end

/**
 * This class is a standard Deserializer for Jackson. It uses the {@link CustomScalarConverter} that is implemented by the project for this scalar
 */
public class JacksonDeserializer${object.name} extends StdDeserializer<${object.name}> {

	private static final long serialVersionUID = 1L;
	${object.customScalarConverterClassName} customScalarConverter${object.name} = null;

	protected JacksonDeserializer${object.name}() {
		super(${object.name}.class);
	}

	@Override
	public ${object.name} deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		try {
			return getCustomScalarConverter${object.name}(p).convertFromString(p.getText());
		} catch (GraphQLRequestExecutionException e) {
			throw new JsonParseException(e);
		}
	}

	private ${object.customScalarConverterClassName} getCustomScalarConverter${object.name}(JsonParser p)
			throws com.fasterxml.jackson.core.JsonParseException {
		if (customScalarConverter${object.name} == null) {
			customScalarConverter${object.name} = (${object.customScalarConverterClassName}) CustomScalarRegistryImpl.customScalarRegistry
					.getCustomScalarConverter("${object.name}");
			if (customScalarConverter${object.name} == null) {
				throw new com.fasterxml.jackson.core.JsonParseException(p,
						"No converter has been registered for the type '${object.name}'");
			}
		}
		return customScalarConverter${object.name};
	}

}
