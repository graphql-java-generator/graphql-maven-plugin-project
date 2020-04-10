/**
 * 
 */
package com.graphql_java_generator.samples.forum.server.specific_code;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.samples.forum.server.DataFetchersDelegatePost;
import com.graphql_java_generator.samples.forum.server.Member;
import com.graphql_java_generator.samples.forum.server.Post;
import com.graphql_java_generator.samples.forum.server.jpa.MemberRepository;
import com.graphql_java_generator.samples.forum.server.jpa.PostRepository;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author etienne-sf
 */
@Component
public class DataFetchersDelegatePostImpl implements DataFetchersDelegatePost {

	/** The logger for this instance */
	protected Logger logger = LoggerFactory.getLogger(DataFetchersDelegatePostImpl.class);

	@Resource
	MemberRepository memberRepository;
	@Resource
	PostRepository postRepository;

	@Override
	public CompletableFuture<Member> author(DataFetchingEnvironment dataFetchingEnvironment,
			DataLoader<UUID, Member> dataLoader, Post source) {
		return dataLoader.load(source.getAuthorId());
	}

	@Override
	public List<Post> batchLoader(List<UUID> keys) {
		logger.debug("Batch loading {} posts", keys.size());
		return postRepository.findByIds(keys);
	}
}
