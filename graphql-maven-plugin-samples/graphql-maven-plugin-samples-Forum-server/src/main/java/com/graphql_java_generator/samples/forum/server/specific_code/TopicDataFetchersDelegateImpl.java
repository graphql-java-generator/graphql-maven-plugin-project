/**
 * 
 */
package com.graphql_java_generator.samples.forum.server.specific_code;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.samples.forum.server.GraphQLUtil;
import com.graphql_java_generator.samples.forum.server.Member;
import com.graphql_java_generator.samples.forum.server.Post;
import com.graphql_java_generator.samples.forum.server.Topic;
import com.graphql_java_generator.samples.forum.server.TopicDataFetchersDelegate;
import com.graphql_java_generator.samples.forum.server.jpa.MemberRepository;
import com.graphql_java_generator.samples.forum.server.jpa.PostRepository;
import com.graphql_java_generator.samples.forum.server.jpa.TopicRepository;

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

	/** The logger for this instance */
	protected Logger logger = LogManager.getLogger();

	@Resource
	MemberRepository memberRepository;
	@Resource
	PostRepository postRepository;
	@Resource
	TopicRepository topicRepository;

	@Resource
	GraphQLUtil graphQLUtil;

	@Override
	public CompletableFuture<Member> author(DataFetchingEnvironment dataFetchingEnvironment,
			DataLoader<UUID, Member> dataLoader, Topic source) {
		return dataLoader.load(source.getAuthorId());
	}

	@Override
	public List<Post> posts(DataFetchingEnvironment dataFetchingEnvironment, Topic source, String since) {
		if (since == null)
			return graphQLUtil.iterableToList(postRepository.findByTopicId(source.getId()));
		else
			return graphQLUtil.iterableToList(postRepository.findByTopicIdAndSince(source.getId(), since));
	}

	@Override
	public List<Topic> batchLoader(List<UUID> keys) {
		logger.debug("Batch loading {} topics", keys.size());
		return topicRepository.findByIds(keys);
	}
}
