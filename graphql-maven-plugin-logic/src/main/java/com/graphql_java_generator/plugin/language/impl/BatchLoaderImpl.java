/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import com.graphql_java_generator.plugin.language.BatchLoader;
import com.graphql_java_generator.plugin.language.DataFetcherDelegate;
import com.graphql_java_generator.plugin.language.Type;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author EtienneSF
 *
 */
@Data
@EqualsAndHashCode
public class BatchLoaderImpl implements BatchLoader {

	/** The GraphQL type that is loaded by this {@link BatchLoader}. */
	Type type;

	/** The {@link DataFetcherDelegate} that will contain the actual request for this batch loaded */
	final DataFetcherDelegate dataFetcherDelegate;

	public BatchLoaderImpl(Type type, DataFetcherDelegate dataFetcherDelegate) {
		this.type = type;
		this.dataFetcherDelegate = dataFetcherDelegate;

		dataFetcherDelegate.getBatchLoaders().add(this);
	}

}
