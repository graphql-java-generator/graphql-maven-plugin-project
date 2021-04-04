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
	ENUM(CodeTemplateScope.COMMON, "templates/enum_type.vm.java"), //
	INTERFACE(CodeTemplateScope.COMMON, "templates/interface_type.vm.java"), //
	OBJECT(CodeTemplateScope.COMMON, "templates/object_type.vm.java"), //
	UNION(CodeTemplateScope.COMMON, "templates/union_type.vm.java"),

	// Client files (alphabetic order)
	CUSTOM_SCALAR_REGISTRY_INITIALIZER(CodeTemplateScope.CLIENT,
			"templates/client_CustomScalarRegistryInitializer.vm.java"), //
	DIRECTIVE_REGISTRY_INITIALIZER(CodeTemplateScope.CLIENT, "templates/client_DirectiveRegistryInitializer.vm.java"), //
	GRAPHQL_REQUEST(CodeTemplateScope.CLIENT, "templates/client_GraphQLRequest.vm.java"), //
	JACKSON_DESERIALIZERS(CodeTemplateScope.CLIENT, "templates/client_jackson_deserializers.vm.java"), //
	QUERY_MUTATION(CodeTemplateScope.CLIENT, "templates/client_query_mutation_type.vm.java"), //
	QUERY_MUTATION_EXECUTOR(CodeTemplateScope.CLIENT, "templates/client_query_mutation_executor.vm.java"), //
	QUERY_RESPONSE(CodeTemplateScope.CLIENT, "templates/client_query_mutation_subscription_response.vm.java"), //
	ROOT_RESPONSE(CodeTemplateScope.CLIENT, "templates/client_query_mutation_subscription_rootResponse.vm.java"), //
	SUBSCRIPTION(CodeTemplateScope.CLIENT, "templates/client_subscription_type.vm.java"), //
	SUBSCRIPTION_EXECUTOR(CodeTemplateScope.CLIENT, "templates/client_subscription_executor.vm.java"), //

	// Server files (alphabetic order)
	BATCH_LOADER_DELEGATE_IMPL(CodeTemplateScope.SERVER, "templates/server_BatchLoaderDelegateImpl.vm.java"), //
	DATA_FETCHER(CodeTemplateScope.SERVER, "templates/server_GraphQLDataFetchers.vm.java"), //
	DATA_FETCHER_DELEGATE(CodeTemplateScope.SERVER, "templates/server_GraphQLDataFetchersDelegate.vm.java"), //
	SERVER(CodeTemplateScope.SERVER, "templates/server_GraphQLServerMain.vm.java"), //
	WEB_SOCKET_CONFIG(CodeTemplateScope.SERVER, "templates/server_WebSocketConfig.vm.java"), //
	WEB_SOCKET_HANDLER(CodeTemplateScope.SERVER, "templates/server_WebSocketHandler.vm.java"), //
	WIRING(CodeTemplateScope.SERVER, "templates/server_GraphQLWiring.vm.java"), //

	// Template for the GraphQL relay schema generation
	RELAY_SCHEMA(CodeTemplateScope.GENERATE_RELAY_SCHEMA, "templates/generateRelaySchema.vm.graphqls");

	/**
	 * The scope for this template
	 */
	@NonNull
	private CodeTemplateScope scope;

	/**
	 * The default value to use
	 */
	@NonNull
	private String defaultValue;

}
