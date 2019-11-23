package com.graphql_java_generator;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.annotation.Resource;

import org.dataloader.DataLoaderRegistry;
import org.springframework.stereotype.Component;

@Component
public class BatchLoaderDelegateDroidImpl implements BatchLoaderDelegate<UUID, Droid> {

	@Resource
	DroidDataFetchersDelegate droidDataFetchersDelegate;

	/**
	 * A batch loader function that will be called with N or more keys for batch loading. This can be a singleton object
	 * since it's stateless.
	 * 
	 * @param keys
	 *            the list of keys, for which we want to retrieve the instances.
	 * @return the list of Droids corresponding to the given keys
	 */
	@Override
	public CompletionStage<List<Droid>> load(List<UUID> keys) {
		// We use supplyAsync() of values here for maximum parellisation
		return CompletableFuture.supplyAsync(() -> droidDataFetchersDelegate.droidBatchLoader(keys));
	}

	/**
	 * The name for this {@link BatchLoaderDelegate}, as ot is stored in the {@link DataLoaderRegistry}. <BR/>
	 * The BatchLoader can then be retrieved by this command, in a DataFetchDelegate implementation:<BR/>
	 * 
	 * <PRE>
	 * &#64;Override
	 * public CompletableFuture<List<Droid>> friends(DataFetchingEnvironment environment, Droid source) {
	 * 	logger.debug("Executing characterImpl.friends, with this character: {}", source.getId().toString());
	 * 	List<UUID> friendIds = graphQLUtil
	 * 			.convertListByteArrayToListUUID(characterRepository.findFriendsId(source.getId()));
	 * 	DataLoader<UUID, Droid> dataLoader = environment.getDataLoader("Droid");
	 * 	return dataLoader.loadMany(friendIds);
	 * }
	 * </PRE>
	 */
	@Override
	public String getName() {
		return "Droid";
	}

}
