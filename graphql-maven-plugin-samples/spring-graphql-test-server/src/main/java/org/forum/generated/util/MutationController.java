/**
 * 
 */
package org.forum.generated.util;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import graphql.schema.DataFetchingEnvironment;

@Controller
public class MutationController {

	@Autowired
	DataFetchersDelegateMutation dataFetchersDelegateMutation;

	/**
	 * This method loads the data for Mutation.createBoard. <BR/>
	 * 
	 * @param dataFetchingEnvironment
	 *            The GraphQL {@link DataFetchingEnvironment}. It gives you access to the full GraphQL context for this
	 *            DataFetcher
	 * @param name
	 *            The input parameter sent in the query by the GraphQL consumer, as defined in the GraphQL schema.
	 * @param publiclyAvailable
	 *            The input parameter sent in the query by the GraphQL consumer, as defined in the GraphQL schema.
	 * @throws NoSuchElementException
	 *             This method may return a {@link NoSuchElementException} exception. In this case, the exception is
	 *             trapped by the calling method, and the return is consider as null. This allows to use the
	 *             {@link Optional#get()} method directly, without caring of whether or not there is a value. The
	 *             generated code will take care of the {@link NoSuchElementException} exception.
	 */
	@MutationMapping("createBoard")
	public org.forum.generated.Board createBoard(DataFetchingEnvironment dataFetchingEnvironment,
			@Argument("name") java.lang.String name,
			@Argument("publiclyAvailable") java.lang.Boolean publiclyAvailable) {
		return dataFetchersDelegateMutation.createBoard(dataFetchingEnvironment, name, publiclyAvailable);
	}

	/**
	 * This method loads the data for Mutation.createTopic. <BR/>
	 * 
	 * @param dataFetchingEnvironment
	 *            The GraphQL {@link DataFetchingEnvironment}. It gives you access to the full GraphQL context for this
	 *            DataFetcher
	 * @param topic
	 *            The input parameter sent in the query by the GraphQL consumer, as defined in the GraphQL schema.
	 * @throws NoSuchElementException
	 *             This method may return a {@link NoSuchElementException} exception. In this case, the exception is
	 *             trapped by the calling method, and the return is consider as null. This allows to use the
	 *             {@link Optional#get()} method directly, without caring of whether or not there is a value. The
	 *             generated code will take care of the {@link NoSuchElementException} exception.
	 */
	@MutationMapping("createTopic")
	public org.forum.generated.Topic createTopic(DataFetchingEnvironment dataFetchingEnvironment,
			@Argument("topic") org.forum.generated.TopicInput topic) {
		return dataFetchersDelegateMutation.createTopic(dataFetchingEnvironment, topic);
	}

	/**
	 * This method loads the data for Mutation.createPost. <BR/>
	 * 
	 * @param dataFetchingEnvironment
	 *            The GraphQL {@link DataFetchingEnvironment}. It gives you access to the full GraphQL context for this
	 *            DataFetcher
	 * @param post
	 *            The input parameter sent in the query by the GraphQL consumer, as defined in the GraphQL schema.
	 * @throws NoSuchElementException
	 *             This method may return a {@link NoSuchElementException} exception. In this case, the exception is
	 *             trapped by the calling method, and the return is consider as null. This allows to use the
	 *             {@link Optional#get()} method directly, without caring of whether or not there is a value. The
	 *             generated code will take care of the {@link NoSuchElementException} exception.
	 */
	@MutationMapping("createPost")
	public org.forum.generated.Post createPost(DataFetchingEnvironment dataFetchingEnvironment,
			@Argument("post") org.forum.generated.PostInput post) {
		return dataFetchersDelegateMutation.createPost(dataFetchingEnvironment, post);
	}

	/**
	 * This method loads the data for Mutation.createPosts. <BR/>
	 * 
	 * @param dataFetchingEnvironment
	 *            The GraphQL {@link DataFetchingEnvironment}. It gives you access to the full GraphQL context for this
	 *            DataFetcher
	 * @param spam
	 *            The input parameter sent in the query by the GraphQL consumer, as defined in the GraphQL schema.
	 * @throws NoSuchElementException
	 *             This method may return a {@link NoSuchElementException} exception. In this case, the exception is
	 *             trapped by the calling method, and the return is consider as null. This allows to use the
	 *             {@link Optional#get()} method directly, without caring of whether or not there is a value. The
	 *             generated code will take care of the {@link NoSuchElementException} exception.
	 */
	@MutationMapping("createPosts")
	public List<org.forum.generated.Post> createPosts(DataFetchingEnvironment dataFetchingEnvironment,
			@Argument("spam") List<org.forum.generated.PostInput> spam) {
		return dataFetchersDelegateMutation.createPosts(dataFetchingEnvironment, spam);
	}

	/**
	 * This method loads the data for Mutation.createMember. <BR/>
	 * 
	 * @param dataFetchingEnvironment
	 *            The GraphQL {@link DataFetchingEnvironment}. It gives you access to the full GraphQL context for this
	 *            DataFetcher
	 * @param input
	 *            The input parameter sent in the query by the GraphQL consumer, as defined in the GraphQL schema.
	 * @throws NoSuchElementException
	 *             This method may return a {@link NoSuchElementException} exception. In this case, the exception is
	 *             trapped by the calling method, and the return is consider as null. This allows to use the
	 *             {@link Optional#get()} method directly, without caring of whether or not there is a value. The
	 *             generated code will take care of the {@link NoSuchElementException} exception.
	 */
	@MutationMapping("createMember")
	public org.forum.generated.Member createMember(DataFetchingEnvironment dataFetchingEnvironment,
			@Argument("input") org.forum.generated.MemberInput input) {
		return dataFetchersDelegateMutation.createMember(dataFetchingEnvironment, input);
	}

}
