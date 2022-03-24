/**
 * 
 */
package org.forum.server.specific_code;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Resource;

import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.DataLoader;
import org.forum.server.graphql.DataFetchersDelegatePost;
import org.forum.server.graphql.Member;
import org.forum.server.graphql.Post;
import org.forum.server.jpa.MemberRepository;
import org.forum.server.jpa.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
			DataLoader<Long, Member> dataLoader, Post origin) {
		return dataLoader.load(origin.getAuthorId());
	}

	@Override
	public Member author(DataFetchingEnvironment dataFetchingEnvironment, Post origin) {
		return memberRepository.findById(origin.getAuthorId()).orElseGet(() -> {
			return null;
		});
	}

	@Override
	public List<Post> unorderedReturnBatchLoader(List<Long> keys, BatchLoaderEnvironment env) {
		logger.debug("Batch loading {} posts", keys.size());
		return postRepository.findByIds(keys);
	}

}
