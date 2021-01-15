package com.graphql_java_generator.samples.forum.client.graphql;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.forum.client.Queries;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Board;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Member;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.MemberInput;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.MutationTypeExecutor;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Post;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.PostInput;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.QueryTypeExecutor;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.Topic;
import com.graphql_java_generator.samples.forum.client.graphql.forum.client.TopicInput;

/**
 * This class implements the simplest way to call GraphQl queries, with the GraphQL Java Generator
 * 
 * @author etienne-sf
 */
@Component
public class PartialDirectRequests implements Queries {

	@Autowired
	QueryTypeExecutor queryType;

	@Autowired
	MutationTypeExecutor mutationType;

	/** The constructor used, when this class is loaded as a Spring bean (useless if there is no other constructor) */
	@Autowired
	public PartialDirectRequests() {
		// No action.
	}

	/**
	 * The constructor to use, when not in a Spring context. To remove, when in a Spring app
	 * 
	 * @param url
	 *            The url for the GraphQL endpoint
	 */
	public PartialDirectRequests(String url) {
		queryType = new QueryTypeExecutor(url);
		mutationType = new MutationTypeExecutor(url);
	}

	@Override
	public List<Board> boardsSimple() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		return queryType.boards("");
	}

	@Override
	public List<Board> boardsAndTopicsWithFieldParameter(Date since)
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		String formatedDate = dateFormat.format(since);
		return queryType.boards("{id name publiclyAvailable topics(since: \"" + formatedDate + "\"){id}}");
	}

	@Override
	public List<Topic> topicAuthorPostAuthor(String boardName, Date since)
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		String formatedDate = dateFormat.format(since);
		return queryType.topics("{id date author{name email alias id type} nbPosts title content posts(since:\""
				+ formatedDate + "\"){id date author{name email alias} title content}}", boardName);

	}

	@Override
	public List<Topic> findTopics(String boardName, List<String> keyword)
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		return queryType.findTopics("{id date title content}", boardName, keyword);
	}

	@Override
	public Board createBoard(String name, boolean publiclyAvailable)
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		return mutationType.createBoard("{id name publiclyAvailable}", name, publiclyAvailable);
	}

	@Override
	public Topic createTopic(TopicInput input)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return mutationType.createTopic(
				"{id date author{id} nbPosts title content posts(since: \"1900-01-01\"){id}  publiclyAvailable}",
				input);
	}

	@Override
	public Member createMember(MemberInput input)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return mutationType.createMember("{id name alias email type}", input);
	}

	@Override
	public Post createPost(PostInput input)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return mutationType.createPost("{id date author{id} title content publiclyAvailable}", input);
	}

	@Override
	public List<Post> createPosts(List<PostInput> input)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return mutationType.createPosts("{id date author{id} title content publiclyAvailable}", input);
	}

}
