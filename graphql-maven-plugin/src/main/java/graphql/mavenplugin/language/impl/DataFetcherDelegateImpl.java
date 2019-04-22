/**
 * 
 */
package graphql.mavenplugin.language.impl;

import java.util.ArrayList;
import java.util.List;

import graphql.mavenplugin.language.DataFetcher;
import graphql.mavenplugin.language.DataFetcherDelegate;
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
