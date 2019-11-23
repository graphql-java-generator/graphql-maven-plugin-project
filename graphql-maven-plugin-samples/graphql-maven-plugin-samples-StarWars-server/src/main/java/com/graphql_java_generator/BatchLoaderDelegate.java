/**
 * 
 */
package com.graphql_java_generator;

import org.dataloader.BatchLoader;
import org.dataloader.DataLoaderRegistry;

/**
 * @author EtienneSF
 *
 */
public interface BatchLoaderDelegate<K, V> extends BatchLoader<K, V> {

	/**
	 * All the BatchLoaderDelegates are stored in a {@link DataLoaderRegistry}. It's actually a map, where the key is
	 * the name for this BatchLoadeDelegate, as returned by this method. <BR/>
	 * All BatchLoaderDelegates must be defined as Java component. They are discovered by the
	 * GraphQLProvide.dataLoaderRegistry() method.<BR/>
	 * It is not allowed to have two BatchLoaderDelegates with the same name.<BR/>
	 * graphql-java-generator will generate one BatchLoaderDelegate implementation for each object defined in the GrapQL
	 * schema, which has an ID as a field.
	 * 
	 * @return
	 */
	public String getName();

}
