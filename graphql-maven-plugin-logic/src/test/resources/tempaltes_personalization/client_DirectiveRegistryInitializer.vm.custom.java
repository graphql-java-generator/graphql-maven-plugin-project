/** This template is custom **/
package ${pluginConfiguration.packageName};

import com.graphql_java_generator.client.directive.Directive;
import com.graphql_java_generator.client.directive.DirectiveLocation;
import com.graphql_java_generator.client.directive.DirectiveRegistry;
import com.graphql_java_generator.client.directive.DirectiveRegistryImpl;
import com.graphql_java_generator.client.request.InputParameter;
import com.graphql_java_generator.customscalars.CustomScalarRegistry;
import com.graphql_java_generator.customscalars.CustomScalarRegistryImpl;

public class DirectiveRegistryInitializer {
	
	
	CustomScalarRegistry customScalarRegistry = new CustomScalarRegistryImpl();
	
	/**
	 * Initialization of the {@link DirectiveRegistry} with all known custom scalars, that is with all custom scalars
	 * defined in the project pom
	 */
	public void initDirectiveRegistry() {
		DirectiveRegistry directiveRegistry = new DirectiveRegistryImpl();
		Directive directive;
		InputParameter param;

#foreach ($directive in $directives)
		directive = new Directive();
		directive.setName("${directive.name}");
#foreach ($argument in $directive.arguments)
		param = InputParameter.newHardCodedParameter("${argument.name}", null, ${argument.mandatory},
#if ($argument.graphQLTypeName == "BigDecimal" || 
	$argument.graphQLTypeName == "BigInteger" || 
	$argument.graphQLTypeName == "Boolean" || 
	$argument.graphQLTypeName == "Byte" || 
	$argument.graphQLTypeName == "Char" || 
	$argument.graphQLTypeName == "Float" || 
	$argument.graphQLTypeName == "ID" || 
	$argument.graphQLTypeName == "Int" || 
	$argument.graphQLTypeName == "Long" || 
	$argument.graphQLTypeName == "Short" || 
	$argument.graphQLTypeName == "String"
	)
					graphql.Scalars.GraphQL${argument.graphQLTypeName}
#else
## It must be a custom scalar
					customScalarRegistry.getGraphQLScalarType(${argument.graphQLTypeName})
#end
				);
		directive.getArguments().add(param);
#end
#foreach ($location in $directive.directiveLocations)
		directive.getDirectiveLocations().add(DirectiveLocation.${location.name()});
#end
		directiveRegistry.registerDirective(directive);

#end

		DirectiveRegistryImpl.directiveRegistry = directiveRegistry;
	}

}
