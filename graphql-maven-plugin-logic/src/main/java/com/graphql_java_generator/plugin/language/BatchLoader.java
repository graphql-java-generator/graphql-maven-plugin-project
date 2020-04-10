/**
 * 
 */
package com.graphql_java_generator.plugin.language;

/**
 * A BatchLoader represents a {@link org.dataloader.BatchLoader}, and contains all the information to generate the code
 * to use it. A BatchLoader is actually created for each type in the GraphQL schema that has an ID.
 * 
 * @author etienne-sf
 */
public interface BatchLoader {

	/**
	 * The type that this BatchLoader will return.
	 * 
	 * @return
	 */
	public Type getType();

	/**
	 * The {@link DataFetchersDelegate} which contains the method that will retrieve the data. That is: the
	 * {@link DataFetchersDelegate} that works on the same {@link Type} that this BatchLoader.
	 * 
	 * @return
	 */
	public DataFetchersDelegate getDataFetchersDelegate();
}
