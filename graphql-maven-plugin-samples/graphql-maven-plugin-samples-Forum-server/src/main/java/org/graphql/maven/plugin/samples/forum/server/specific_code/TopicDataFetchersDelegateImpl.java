/**
 * 
 */
package org.graphql.maven.plugin.samples.forum.server.specific_code;

import java.util.ArrayList;

import org.graphql.maven.plugin.samples.forum.server.Member;
import org.graphql.maven.plugin.samples.forum.server.Post;
import org.graphql.maven.plugin.samples.forum.server.TopicDataFetchersDelegate;
import org.springframework.stereotype.Component;

/**
 * This class implements the access to the database : there are so many ways to do this, that the developper has still
 * work to do. But all the GraphQL boilerplate is generated.<BR/>
 * The {@link GraphQLDataFetchersDelegate} interface is generated from the given schema
 * 
 * @author EtienneSF
 */
@Component
public class TopicDataFetchersDelegateImpl implements TopicDataFetchersDelegate {

	@Override
	public Member topicAuthor(String topicId) {
		return null;
	}

	@Override
	public Iterable<Post> topicPosts(String topicId, String since) {
		Iterable<Post> ret = new ArrayList<>();
		return ret;
	}
}
