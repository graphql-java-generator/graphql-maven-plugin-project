/**
 * 
 */
package org.forum.server.specific_code;

import java.util.List;
import java.util.Optional;
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
			DataLoader<Long, Member> dataLoader, Post source) {
		return dataLoader.load(source.getAuthorId());
	}

	@Override
	public List<Post> batchLoader(List<Long> keys, BatchLoaderEnvironment env) {
		logger.debug("Batch loading {} posts", keys.size());
		return postRepository.findByIds(keys);
	}

	/**
	 * This method should not be called. The {@link DataFetchersDelegateMemberImpl#batchLoader(List)} should be called
	 * instead. The name returned by this method is marked by "[SL] ", to check that in integration tests.
	 */
	@Override
	public Member author(DataFetchingEnvironment dataFetchingEnvironment, Post origin) {
		logger.debug("Loading author for post ", origin.getId());

		Optional<Member> opt = memberRepository.findById(origin.getAuthorId());

		if (opt.isPresent()) {
			// Let's mark all the entries retrieved here by [SL] (Single Loader), to check this in integration tests
			// These tests are in the graphql-maven-plugin-samples-Forum-client project
			Member m = opt.get();
			m.setName("[SL] " + m.getName());
			return m;
		} else {
			return null;
		}
	}
}
