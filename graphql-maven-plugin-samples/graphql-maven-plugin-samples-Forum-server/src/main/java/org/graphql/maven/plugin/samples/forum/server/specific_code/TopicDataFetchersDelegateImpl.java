/**
 * 
 */
package org.graphql.maven.plugin.samples.forum.server.specific_code;

import java.util.List;

import javax.annotation.Resource;

import org.graphql.maven.plugin.samples.forum.server.GraphQLUtil;
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

	@Resource
	GraphQLUtil graphQLUtil;

	@Override
	public Member author(DataFetchingEnvironment dataFetchingEnvironment, Topic source) {
		return memberRepository.findById(source.getAuthorId()).get();
	}

	@Override
	public List<Post> posts(DataFetchingEnvironment dataFetchingEnvironment, Topic source, String since) {
		if (since == null)
			return graphQLUtil.iterableToList(postRepository.findByTopicId(source.getId()));
		else
			return graphQLUtil.iterableToList(postRepository.findByTopicIdAndSince(source.getId(), since));
	}
}
