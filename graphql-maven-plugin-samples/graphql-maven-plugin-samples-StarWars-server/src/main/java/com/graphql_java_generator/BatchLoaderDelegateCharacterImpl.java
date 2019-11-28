package com.graphql_java_generator;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.annotation.Resource;

import org.dataloader.DataLoaderRegistry;
import org.springframework.stereotype.Component;

/**
 * 
 * <PRE>
 * &#64;Override
 * public CompletableFuture<List<CharacterImpl>> friends(DataFetchingEnvironment environment, CharacterImpl source) {
 * 	logger.debug("Executing characterImpl.friends, with this character: {}", source.getId().toString());
 * 	List<UUID> friendIds = graphQLUtil
 * 			.convertListByteArrayToListUUID(characterRepository.findFriendsId(source.getId()));
 * 	DataLoader<UUID, CharacterImpl> dataLoader = environment.getDataLoader("Character");
 * 	return dataLoader.loadMany(friendIds);
 * }
 * </PRE>
 * 
 * @author EtienneSF
 */
@Component
public class BatchLoaderDelegateCharacterImpl implements BatchLoaderDelegate<UUID, Character> {

	@Resource
	CharacterDataFetchersDelegate characterImplDataFetchersDelegate;

	/**
	 * A batch loader function that will be called with N or more keys for batch loading. This can be a singleton object
	 * since it's stateless.
	 * 
	 * @param keys
	 *            the list of keys, for which we want to retrieve the instances.
	 * @return the list of Characters corresponding to the given keys
	 */
	@Override
	public CompletionStage<List<Character>> load(List<UUID> keys) {
		// We use supplyAsync() of values here for maximum parellisation
		return CompletableFuture.supplyAsync(() -> characterImplDataFetchersDelegate.characterBatchLoader(keys));
	}

	/**
	 * The name for this {@link BatchLoaderDelegate}, as ot is stored in the {@link DataLoaderRegistry}. <BR/>
	 * The BatchLoader can then be retrieved by this command, in a DataFetchDelegate implementation:<BR/>
	 */
	@Override
	public String getName() {
		return "Character";
	}

}
