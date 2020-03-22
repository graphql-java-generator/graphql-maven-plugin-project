package com.graphql_java_generator.plugin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Enumeration that defines the available templates for code generation
 * @author ggomez
 *
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum CodeTemplate {
	
	
	OBJECT(CodeTemplateScope.COMMON, "templates/object_type.vm.java"),
	INTERFACE(CodeTemplateScope.COMMON, "templates/interface_type.vm.java"),
	ENUM(CodeTemplateScope.COMMON, "templates/enum_type.vm.java"),
	UNION(CodeTemplateScope.COMMON, "templates/union_type.vm.java"),
	CUSTOM_SCALAR_REGISTRY_INITIALIZER(CodeTemplateScope.CLIENT, "templates/client_CustomScalarRegistryInitializer.vm.java"),
	QUERY_MUTATION_SUBSCRIPTION(CodeTemplateScope.CLIENT, "templates/client_query_mutation_subscription_type.vm.java"),
	QUERY_TARGET_TYPE(CodeTemplateScope.CLIENT, "templates/client_query_target_type.vm.java"),
	JACKSON_DESERIALIZER(CodeTemplateScope.CLIENT, "templates/client_jackson_deserialize.vm.java"),
	BATCHLOADERDELEGATE(CodeTemplateScope.SERVER, "templates/server_BatchLoaderDelegate.vm.java"),
	BATCHLOADERDELEGATEIMPL(CodeTemplateScope.SERVER, "templates/server_BatchLoaderDelegateImpl.vm.java"),
	DATAFETCHER(CodeTemplateScope.SERVER, "templates/server_GraphQLDataFetchers.vm.java"),
	DATAFETCHERDELEGATE(CodeTemplateScope.SERVER, "templates/server_GraphQLDataFetchersDelegate.vm.java"),
	GRAPHQLUTIL(CodeTemplateScope.SERVER, "templates/server_GraphQLUtil.vm.java"),
	PROVIDER(CodeTemplateScope.SERVER, "templates/server_GraphQLProvider.vm.java"),
	SERVER(CodeTemplateScope.SERVER, "templates/server_GraphQLServerMain.vm.java");	
	
	/**
	 * The de
	 */
	@NonNull
	private CodeTemplateScope scope;
	
	/**
	 * The default value to use
	 */
	@NonNull
	private String defaultValue;

	
	

}
