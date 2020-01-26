package ${pluginConfiguration.packageName};

import org.allGraphQLCases.server.GraphQLProvider;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

public class CustomScalars {

#foreach ($customScalar in $customScalars)
/**
 * Returns the {@link GraphQLScalarType} for registering into the graphql-java framework.
 * 
 * @see GraphQLProvider#buildWiring()
 */
public static GraphQLScalarType ${customScalar.name}() {
	return GraphQLScalarType.newScalar().name("${customScalar.name}")
			.description("A custom scalar that handles ${customScalar.name} GraphQL Custom Scalar")
			.coercing(new Coercing<${customScalar.classFullName}, String>() {

				${customScalar.customScalarConvertClassName} converter = new ${customScalar.customScalarConvertClassName}();

				@Override
				public ${customScalar.classFullName} parseLiteral(Object arg0) throws CoercingParseLiteralException {
					if (arg0 == null)
						return null;
					if (!(arg0 instanceof StringValue)) {
						throw new CoercingParseLiteralException(
								"Value should be a String Value but is '" + arg0.getClass().getName() + "'");
					}
					try {
						return converter.convertFromString(((StringValue) arg0).getValue());
					} catch (GraphQLRequestExecutionException e) {
						throw new CoercingParseLiteralException(e.getMessage(), e);
					}
				}

				@Override
				public ${customScalar.classFullName} parseValue(Object arg0) throws CoercingParseValueException {
					try {
						return converter.convertFromString(arg0.toString());
					} catch (GraphQLRequestExecutionException e) {
						throw new CoercingParseLiteralException(e.getMessage(), e);
					}
				}

				@Override
				public String serialize(Object input) throws CoercingSerializeException {
					if (input == null) {
						return null;
					} else if (input instanceof ${customScalar.classFullName}) {
						try {
							return converter.convertToString(input);
						} catch (GraphQLRequestExecutionException e) {
							throw new CoercingParseLiteralException(e.getMessage(), e);
						}
					}
					throw new CoercingParseLiteralException(
							"Value should be a StringValue but is '" + input.getClass().getName() + "'");
				}
			}).build();
	}


#end
}
