/**
 * 
 */
package com.graphql_java_generator.plugin.language;

import java.util.List;

import com.graphql_java_generator.GraphqlUtils;

/**
 * This class represents a GraphQL Data Fetcher Delegate. A DataFetchersDelegate aggregates all {@link DataFetcher} for
 * a GraphQL type. It is only used when in server mode, the GraphQL maven plugin generates on data fetcher delegate for
 * each object whose fields need at least one data fetcher. This helps to limit the impact on the specific code, when
 * the GraphQL schema changes. <BR/>
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
 * @author EtienneSF
 */
public interface DataFetchersDelegate {

	/**
	 * The name of the DataFetcher. This name is a valid java classname identifier, and is the name to use as a method
	 * name.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Returns the {@link Type}, for which the DataFetchersDelegate aggregates the access methods.
	 * 
	 * @return
	 */
	public Type getType();

	/**
	 * The name of the DataFetcher, in camelCase. This name is a valid for a java variable identifier, and is the name
	 * to use as a method name.
	 * 
	 * @return
	 */
	default public String getCamelCaseName() {
		return GraphqlUtils.graphqlUtils.getCamelCase(getName());
	}

	/**
	 * The name of the DataFetcher, in PascalCase. This name is a valid for a java type identifier.
	 * 
	 * @return
	 */
	default public String getPascalCaseName() {
		return GraphqlUtils.graphqlUtils.getPascalCase(getName());
	}

	/**
	 * Retrieves the {@link DataFetcher}s that are to be implemented by this Data Fetcher Delegate.
	 * 
	 * @return
	 */
	public List<DataFetcher> getDataFetchers();

	/**
	 * Retrieves the {@link BatchLoader}s that are to be implemented by this Data Fetcher Delegate.
	 * 
	 * @return
	 */
	public List<BatchLoader> getBatchLoaders();

}
