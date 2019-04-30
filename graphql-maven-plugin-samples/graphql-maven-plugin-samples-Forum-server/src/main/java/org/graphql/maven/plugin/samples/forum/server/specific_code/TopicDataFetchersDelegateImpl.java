/**
 * 
 */
package org.graphql.maven.plugin.samples.forum.server.specific_code;

import javax.annotation.Resource;

import org.graphql.maven.plugin.samples.forum.server.Member;
import org.graphql.maven.plugin.samples.forum.server.Post;
import org.graphql.maven.plugin.samples.forum.server.Topic;
import org.graphql.maven.plugin.samples.forum.server.TopicDataFetchersDelegate;
import org.graphql.maven.plugin.samples.forum.server.jpa.MemberRepository;
import org.graphql.maven.plugin.samples.forum.server.jpa.PostRepository;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

/**
 * This class implements the access to the database : there are so many ways to do this, that the developper has still
 * work to do. But all the GraphQL boilerplate is generated.<BR/>
 * The {@link GraphQLDataFetchersDelegate} interface is generated from the given schema
 * 
 * @author EtienneSF
 */
@Component
public class TopicDataFetchersDelegateImpl implements TopicDataFetchersDelegate {

	@Resource
	MemberRepository memberRepository;
	@Resource
	PostRepository postRepository;

	@Override
	public Member topicAuthor(DataFetchingEnvironment dataFetchingEnvironment, Topic source) {
		return memberRepository.findById(source.getAuthorId()).get();
	}

	@Override
	public Iterable<Post> topicPosts(DataFetchingEnvironment dataFetchingEnvironment, Topic source, String since) {
		if (since == null)
			return postRepository.findByTopicId(source.getId());
		else
			return postRepository.findByTopicIdAndSince(source.getId(), since);
	}
}
