/**
 * 
 */
package com.graphql_java_generator.plugin.language;

/**
 * A BatchLoader represents a {@link org.dataloader.BatchLoader}, and contains all the information to generate the code
 * to use it. A BatchLoader is actually created for each type in the GraphQL schema that has an ID.
 * 
 * @author EtienneSF
 */
public interface BatchLoader {

	/**
	 * The type that this BatchLoader will return.
	 * 
	 * @return
	 */
	public Type getType();

	/**
	 * The {@link DataFetcherDelegate} which contains the method that will retrieve the data. That is: the
	 * {@link DataFetcherDelegate} that works on the same {@link Type} that this BatchLoader.
	 * 
	 * @return
	 */
	public DataFetcherDelegate getDataFetcherDelegate();
}
