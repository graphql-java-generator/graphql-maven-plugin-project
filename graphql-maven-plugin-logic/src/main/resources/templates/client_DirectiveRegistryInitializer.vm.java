package ${pluginConfiguration.packageName};

import com.graphql_java_generator.client.request.InputParameter;
import com.graphql_java_generator.directive.Directive;
import com.graphql_java_generator.directive.DirectiveRegistry;
import com.graphql_java_generator.directive.DirectiveRegistryImpl;

public class DirectiveRegistryInitializer {

	/**
	 * Initialization of the {@link DirectiveRegistry} with all known custom scalars, that is with all custom scalars
	 * defined in the project pom
	 */
	public void initDirectiveRegistry() {
		DirectiveRegistry directiveRegistry = new DirectiveRegistryImpl();
		Directive directive;

#foreach ($directive in $directives)
		directive = new Directive();
		directive.setName("${directive.name}");
#foreach ($argument in $directive.arguments)
		directive.getArguments().add(InputParameter.newHardCodedParameter("${argument.name}", null));
#end
#foreach ($location in $directive.locations)
		directive.getDirectiveLocations().add(DirectiveLocation.${location.name()});
#end
		directiveRegistry.registerDirective(directive);

#end

		DirectiveRegistryImpl.directiveRegistry = directiveRegistry;
	}

}
