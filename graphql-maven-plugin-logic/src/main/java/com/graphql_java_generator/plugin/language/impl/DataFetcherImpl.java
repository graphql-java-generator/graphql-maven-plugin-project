/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import com.graphql_java_generator.plugin.language.DataFetcher;
import com.graphql_java_generator.plugin.language.DataFetcherDelegate;
import com.graphql_java_generator.plugin.language.Field;

import lombok.Data;

/**
 * @author EtienneSF
 */
@Data
public class DataFetcherImpl implements DataFetcher {

	private Field field;

	private DataFetcherDelegate dataFetcherDelegate;

	private String sourceName = null;

	/**
	 * 
	 * @param field
	 *            The field that this data fetcher must fill
	 */
	public DataFetcherImpl(Field field) {
		this.field = field;
	}

	@Override
	public String getName() {
		return field.getCamelCaseName();
	}

}
