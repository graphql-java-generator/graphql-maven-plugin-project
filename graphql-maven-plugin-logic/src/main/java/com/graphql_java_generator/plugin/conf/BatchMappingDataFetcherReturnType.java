/**
 * 
 */
package com.graphql_java_generator.plugin.conf;

/**
 * This enumeration controls the allowed return type that can be generated for the data fetchers, when the
 * <code>@BatchMapping</code> annotation is used, that is, when the <code>generateBatchMappingDataFetchers</code> plugin
 * parameter is set to true
 * 
 * @author etienne-sf
 */
public enum BatchMappingDataFetcherReturnType {

	MONO_MAP_K_V("Mono<Map<K, V>>"), MAP_K_V("Map<K, V>"), FLUX_V("Flux<V>"), COLLECTION_V("Collection<V>");

	BatchMappingDataFetcherReturnType(String value) {
		this.value = value;
	}

	private final String value;

	public String value() {
		return this.value;
	}
}
