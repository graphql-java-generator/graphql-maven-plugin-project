/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import java.util.ArrayList;
import java.util.List;

import com.graphql_java_generator.plugin.language.DataFetcher;
import com.graphql_java_generator.plugin.language.DataFetcherDelegate;

import lombok.Data;

/**
 * @author EtienneSF
 */
@Data
public class DataFetcherDelegateImpl implements DataFetcherDelegate {

	private String name;

	private List<DataFetcher> dataFetchers = new ArrayList<>();

	public DataFetcherDelegateImpl(String name) {
		this.name = name;
	}
}
