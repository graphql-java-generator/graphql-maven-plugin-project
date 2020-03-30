/** This template is custom **/
package ${pluginConfiguration.packageName};

import com.graphql_java_generator.customscalars.CustomScalarRegistry;
import com.graphql_java_generator.customscalars.CustomScalarRegistryImpl;

public class CustomScalarRegistryInitializer {

	/**
	 * Initialization of the {@link CustomScalarRegistry} with all known custom scalars, that is with all custom scalars
	 * defined in the project pom
	 */
	public void initCustomScalarRegistry() {
		CustomScalarRegistry customScalarRegistry = new CustomScalarRegistryImpl();

#foreach ($customScalar in $customScalars)
#if (${customScalar.graphQLScalarTypeClass})
		customScalarRegistry.registerGraphQLScalarType(new ${customScalar.graphQLScalarTypeClass}());
#elseif (${customScalar.graphQLScalarTypeStaticField})
		customScalarRegistry.registerGraphQLScalarType(${customScalar.graphQLScalarTypeStaticField});
#elseif (${customScalar.graphQLScalarTypeGetter})
		customScalarRegistry.registerGraphQLScalarType(${customScalar.graphQLScalarTypeGetter});
#else
		customScalarRegistry.registerGraphQLScalarType: ${customScalar.javaName} : you must define one of graphQLScalarTypeClass, graphQLScalarTypeStaticField or graphQLScalarTypeGetter (in the POM parameters for CustomScalars)
#end
#end

		CustomScalarRegistryImpl.customScalarRegistry = customScalarRegistry;
	}

}
