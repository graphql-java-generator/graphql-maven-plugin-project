/**
 * 
 */
package org.forum.generated.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.dataloader.DataLoader;
import org.forum.generated.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.execution.BatchLoaderRegistry;
import org.springframework.stereotype.Controller;

import graphql.schema.DataFetchingEnvironment;
import reactor.core.publisher.Mono;

/**
 * @author etienne-sf
 *
 */
@Controller
@SchemaMapping(typeName = "Post")
public class PostController {

	@Autowired
	DataFetchersDelegatePost dataFetchersDelegatePost;

	public PostController(BatchLoaderRegistry registry) {
		// Registering the data loaders is useless if the @BatchMapping is used. But we need it here, for backward
		// compatibility with code developed against the previous plugin versions
		registry.forTypePair(Long.class, Post.class).registerMappedBatchLoader((keysSet, env) -> {
			List<Long> keys = new ArrayList<>(keysSet.size());
			keys.addAll(keysSet);
			return Mono.fromCallable(() -> {
				Map<Long, Post> map = new HashMap<>();
				// Values are returned in the same order as the keys list
				List<Post> values = dataFetchersDelegatePost.batchLoader(keys, env);
				for (int i = 0; i < keys.size(); i += 1) {
					map.put(keys.get(i), values.get(i));
				}
				return map;
			});
		});
	}

	/**
	 * This method loads the data for Post.author. <BR/>
	 * For optimization, this method returns a CompletableFuture. This allows to use
	 * <A HREF="https://github.com/graphql-java/java-dataloader">graphql-java java-dataloader</A> to highly optimize the
	 * number of requests to the server.<BR/>
	 * The principle is this one: The data loader collects all the data to load, avoid to load several times the same
	 * data, and allows parallel execution of the queries, if multiple queries are to be run.<BR/>
	 * You can implements this method like this:
	 * 
	 * <PRE>
	 * &#64;Override
	 * public CompletableFuture<List<Character>> friends(DataFetchingEnvironment environment,
	 * 		DataLoader<Long, Member> dataLoader, Human origin) {
	 * 	List<java.lang.Long> friendIds = origin.getFriendIds();
	 * 	DataLoader<java.lang.Long, CharacterImpl> dataLoader = environment.getDataLoader("Character");
	 * 	return dataLoader.loadMany(friendIds);
	 * }
	 * </PRE>
	 * 
	 * <BR/>
	 * 
	 * @param dataFetchingEnvironment
	 *            The GraphQL {@link DataFetchingEnvironment}. It gives you access to the full GraphQL context for this
	 *            DataFetcher
	 * @param dataLoader
	 *            The {@link DataLoader} allows to load several data in one query. It allows to solve the (n+1) queries
	 *            issues, and greatly optimizes the response time.<BR/>
	 *            You'll find more informations here: <A HREF=
	 *            "https://github.com/graphql-java/java-dataloader">https://github.com/graphql-java/java-dataloader</A>
	 * @param origin
	 *            The object from which the field is fetch. In other word: the aim of this data fetcher is to fetch the
	 *            author attribute of the <I>origin</I>, which is an instance of {ObjectType {name:Post,
	 *            fields:{Field{name:id, type:ID!, params:[]},Field{name:date, type:Date!, params:[]},Field{name:author,
	 *            type:Member, params:[]},Field{name:publiclyAvailable, type:Boolean, params:[]},Field{name:title,
	 *            type:String!, params:[]},Field{name:content, type:String!, params:[]},Field{name:authorId, type:ID,
	 *            params:[]},Field{name:topicId, type:ID, params:[]}}, comments ""}. It depends on your data modle, but
	 *            it typically contains the id to use in the query.
	 * @throws NoSuchElementException
	 *             This method may return a {@link NoSuchElementException} exception. In this case, the exception is
	 *             trapped by the calling method, and the return is consider as null. This allows to use the
	 *             {@link Optional#get()} method directly, without caring of whether or not there is a value. The
	 *             generated code will take care of the {@link NoSuchElementException} exception.
	 */
	@SchemaMapping(field = "author") // This annotation is used to maintain compatibility with earlier version of the
	// plugin. Code that uses Spring Boot annotations should use remove this method
	// and use the @BatchMapping annotation instead
	public CompletableFuture<org.forum.generated.Member> author(DataFetchingEnvironment dataFetchingEnvironment,
			DataLoader<java.lang.Long, org.forum.generated.Member> dataLoader, org.forum.generated.Post origin) {
		return dataFetchersDelegatePost.author(dataFetchingEnvironment, dataLoader, origin);
	}

}
