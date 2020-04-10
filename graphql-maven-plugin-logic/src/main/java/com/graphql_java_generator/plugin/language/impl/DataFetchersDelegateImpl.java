/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import java.util.ArrayList;
import java.util.List;

import com.graphql_java_generator.plugin.CodeGenerator;
import com.graphql_java_generator.plugin.DocumentParser;
import com.graphql_java_generator.plugin.language.BatchLoader;
import com.graphql_java_generator.plugin.language.DataFetcher;
import com.graphql_java_generator.plugin.language.DataFetchersDelegate;
import com.graphql_java_generator.plugin.language.Type;

import lombok.Data;

/**
 * * This class represents a GraphQL Data Fetcher Delegate. A DataFetchersDelegate aggregates all {@link DataFetcher}
 * for a GraphQL type. It is only used when in server mode, the GraphQL maven plugin generates on data fetcher delegate
 * for each object whose fields need at least one data fetcher. This helps to limit the impact on the specific code,
 * when the GraphQL schema changes. Their characteristics are read by {@link DocumentParser}, and used by
 * {@link CodeGenerator} and the Velocity templates to generate the code of the DataFechers, and their declaration in
 * the GraphQLProvider.<BR/>
 * Thus there are two kinds of {@link DataFetchersDelegate}:
 * <UL>
 * <LI>The {@link DataFetchersDelegate} for regular GraphQL objects. These {@link DataFetcher}s are used to read non
 * scalar fields, that is: fields that are subjects, like friends of the human types, in the StarWars schema.</LI>
 * <LI>The {@link DataFetchersDelegate} for queries, mutations and subscriptions type. These
 * {@link DataFetchersDelegate} are actually the entry points of the GraphQL server. For such
 * {@link DataFetchersDelegate}s, each field is actually a {@link DataFetcher}.</LI>
 * </UL>
 * <BR/>
 * The arguments for the data fetcher delegates are the arguments of its field.
 * 
 * @author etienne-sf
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
