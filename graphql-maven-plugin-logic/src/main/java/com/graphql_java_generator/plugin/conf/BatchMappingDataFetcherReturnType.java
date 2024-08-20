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

	MONO_MAP("Mono<Map<K,V>>"), //
	MAP("Map<K, V>"), //
	FLUX("Flux<V>"), //
	COLLECTION("Collection<V>");

	final private String value;

	private BatchMappingDataFetcherReturnType(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}

}
