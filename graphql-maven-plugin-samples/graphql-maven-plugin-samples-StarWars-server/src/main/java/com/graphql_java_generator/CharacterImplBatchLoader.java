/**
 * 
 */
package com.graphql_java_generator;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.annotation.Resource;

import org.dataloader.BatchLoader;
import org.springframework.stereotype.Component;

/**
 * A batch loader function that will be called with N or more keys for batch loading This can be a singleton object
 * since it's stateless.<BR/>
 * You can find more information on this page:
 * <A HREF="https://www.graphql-java.com/documentation/master/batching/">graphql-java batching</A>
 */
@Component
public class CharacterImplBatchLoader implements BatchLoader<String, Character> {

	/**
	 * The DataFetcherDelegate is specific to the use case. A Spring component must be provided, that implements this
	 * DataFetcherDelegate
	 */
	@Resource
	CharacterImplDataFetchersDelegate characterImplDataFetchersDelegate;

	/**
	 * The load method will load a bunch of keys at once, highly optimizing the number of road trips when retrieving
	 * data along object associations.
	 */
	@Override
	public CompletionStage<List<Character>> load(List<String> keys) {
		// we use supplyAsync() of values here for maximum parellisation
		return CompletableFuture.supplyAsync(() -> characterImplDataFetchersDelegate.characterImplBatchLoader(keys));
	}

}
