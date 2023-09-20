package com.graphql_java_generator.plugin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Enumeration that defines the available templates for code generation
 * 
 * @author ggomez
 *
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum CodeTemplate {

	// Common files (alphabetic order)
	ENUM(CodeTemplateScope.COMMON, "templates/enum_type.vm.java"), // //$NON-NLS-1$
	INTERFACE(CodeTemplateScope.COMMON, "templates/interface_type.vm.java"), // //$NON-NLS-1$
	OBJECT(CodeTemplateScope.COMMON, "templates/object_type.vm.java"), // //$NON-NLS-1$
	UNION(CodeTemplateScope.COMMON, "templates/union_type.vm.java"), //$NON-NLS-1$

	// Client files (alphabetic order)
	CUSTOM_SCALAR_REGISTRY_INITIALIZER(CodeTemplateScope.CLIENT,
			"templates/client_CustomScalarRegistryInitializer.vm.java"), // //$NON-NLS-1$
	DIRECTIVE_REGISTRY_INITIALIZER(CodeTemplateScope.CLIENT, "templates/client_DirectiveRegistryInitializer.vm.java"), // //$NON-NLS-1$
	GRAPHQL_REQUEST(CodeTemplateScope.CLIENT, "templates/client_GraphQLRequest.vm.java"), // //$NON-NLS-1$
	GRAPHQL_REACTIVE_REQUEST(CodeTemplateScope.CLIENT, "templates/client_GraphQLReactiveRequest.vm.java"), // //$NON-NLS-1$
	JACKSON_DESERIALIZERS(CodeTemplateScope.CLIENT, "templates/client_jackson_deserializers.vm.java"), // //$NON-NLS-1$
	JACKSON_SERIALIZERS(CodeTemplateScope.CLIENT, "templates/client_jackson_serializers.vm.java"), // //$NON-NLS-1$
	QUERY_MUTATION(CodeTemplateScope.CLIENT, "templates/client_query_mutation_type.vm.java"), // //$NON-NLS-1$
	QUERY_MUTATION_EXECUTOR(CodeTemplateScope.CLIENT, "templates/client_query_mutation_executor.vm.java"), // //$NON-NLS-1$
	QUERY_MUTATION_REACTIVE_EXECUTOR(CodeTemplateScope.CLIENT,
			"templates/client_query_mutation_reactive_executor.vm.java"), // //$NON-NLS-1$
	QUERY_RESPONSE(CodeTemplateScope.CLIENT, "templates/client_query_mutation_subscription_response.vm.java"), // //$NON-NLS-1$
	ROOT_RESPONSE(CodeTemplateScope.CLIENT, "templates/client_query_mutation_subscription_rootResponse.vm.java"), // //$NON-NLS-1$
	SPRING_AUTOCONFIGURATION_DEFINITION_FILE(CodeTemplateScope.CLIENT,
			"templates/client_spring_autoconfiguration_definition.vm.properties"), // //$NON-NLS-1$
	CLIENT_SPRING_AUTO_CONFIGURATION_CLASS(CodeTemplateScope.CLIENT,
			"templates/client_spring_auto_configuration.vm.java"), // //$NON-NLS-1$
	SUBSCRIPTION(CodeTemplateScope.CLIENT, "templates/client_subscription_type.vm.java"), // //$NON-NLS-1$
	SUBSCRIPTION_EXECUTOR(CodeTemplateScope.CLIENT, "templates/client_subscription_executor.vm.java"), // //$NON-NLS-1$
	SUBSCRIPTION_REACTIVE_EXECUTOR(CodeTemplateScope.CLIENT, "templates/client_subscription_reactive_executor.vm.java"), // //$NON-NLS-1$
	TYPE_MAPPING(CodeTemplateScope.CLIENT, "templates/client_type_mapping.vm.java"), // //$NON-NLS-1$
	TYPE_MAPPING_CSV(CodeTemplateScope.CLIENT, "templates/client_type_mapping.vm.csv"), // //$NON-NLS-1$

	// Server files (alphabetic order)
	DATA_FETCHER_DELEGATE(CodeTemplateScope.SERVER, "templates/server_GraphQLDataFetchersDelegate.vm.java"), // //$NON-NLS-1$
	ENTITY_CONTROLLER(CodeTemplateScope.SERVER, "templates/server_EntityController.vm.java"), // //$NON-NLS-1$
	SERVER(CodeTemplateScope.SERVER, "templates/server_GraphQLServerMain.vm.java"), // //$NON-NLS-1$
	SERVER_SPRING_AUTO_CONFIGURATION_CLASS(CodeTemplateScope.SERVER,
			"templates/server_spring_auto_configuration.vm.java"), // //$NON-NLS-1$
	WIRING(CodeTemplateScope.SERVER, "templates/server_GraphQLWiring.vm.java"), // //$NON-NLS-1$

	// Template for the GraphQL relay schema generation
	RELAY_SCHEMA(CodeTemplateScope.GENERATE_RELAY_SCHEMA, "templates/generateRelaySchema.vm.graphqls"); //$NON-NLS-1$

	/**
	 * The scope for this template
	 */
	@NonNull
	private CodeTemplateScope scope;

	/**
	 * The default value to use
	 */
	@NonNull
	private String defaultPath;

}
