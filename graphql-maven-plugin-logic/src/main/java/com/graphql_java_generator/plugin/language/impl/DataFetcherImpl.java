/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import com.graphql_java_generator.plugin.language.BatchLoader;
import com.graphql_java_generator.plugin.language.DataFetcher;
import com.graphql_java_generator.plugin.language.DataFetchersDelegate;
import com.graphql_java_generator.plugin.language.Field;

import lombok.Data;

/**
 * Represents a {@link DataFetcher}, that is a request to be implemented by the project. The {@link DataFetcher}s are
 * grouped into one {@link DataFetchersDelegate} par GraphQL type.
 * 
 * @author EtienneSF
 */
@Data
public class DataFetcherImpl implements DataFetcher {

	private Field field;

	private DataFetchersDelegate dataFetcherDelegate;

	private String sourceName = null;

	private boolean completableFuture = false;

	/**
	 * 
	 * @param field
	 *            The field that this data fetcher must fill
	 * @param completableFuture
	 *            indicates that this DataFetcher will be actually loaded later, with the help of a {@link BatchLoader}.
	 */
	public DataFetcherImpl(Field field, boolean completableFuture) {
		this.field = field;
		this.completableFuture = completableFuture;
	}

	@Override
	public String getName() {
		return field.getCamelCaseName();
	}

}
