/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import com.graphql_java_generator.plugin.language.BatchLoader;
import com.graphql_java_generator.plugin.language.DataFetchersDelegate;
import com.graphql_java_generator.plugin.language.Type;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author etienne-sf
 *
 */
@Data
@EqualsAndHashCode
public class BatchLoaderImpl implements BatchLoader {

	/** The GraphQL type that is loaded by this {@link BatchLoader}. */
	Type type;

	/** The {@link DataFetchersDelegate} that will contain the actual request for this batch loaded */
	final DataFetchersDelegate dataFetchersDelegate;

	public BatchLoaderImpl(Type type, DataFetchersDelegate dataFetchersDelegate) {
		this.type = type;
		this.dataFetchersDelegate = dataFetchersDelegate;

		dataFetchersDelegate.getBatchLoaders().add(this);
	}

}
