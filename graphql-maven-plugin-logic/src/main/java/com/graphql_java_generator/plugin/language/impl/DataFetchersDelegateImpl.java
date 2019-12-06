/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import java.util.ArrayList;
import java.util.List;

import com.graphql_java_generator.plugin.language.BatchLoader;
import com.graphql_java_generator.plugin.language.DataFetcher;
import com.graphql_java_generator.plugin.language.DataFetchersDelegate;
import com.graphql_java_generator.plugin.language.Type;

import lombok.Data;

/**
 * @author EtienneSF
 */
@Data
public class DataFetchersDelegateImpl implements DataFetchersDelegate {

	private Type type;
	private List<DataFetcher> dataFetchers = new ArrayList<>();
	private List<BatchLoader> batchLoaders = new ArrayList<>();

	public DataFetchersDelegateImpl(Type type) {
		if (type == null)
			throw new NullPointerException("type may not be null");
		this.type = type;
	}

	@Override
	public String getName() {
		return "DataFetchersDelegate" + type.getClassSimpleName();
	}
}
