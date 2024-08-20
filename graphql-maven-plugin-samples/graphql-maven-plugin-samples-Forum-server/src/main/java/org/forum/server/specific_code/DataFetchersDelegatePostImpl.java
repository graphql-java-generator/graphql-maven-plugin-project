/**
 * 
 */
package org.forum.server.specific_code;

import java.util.List;
import java.util.stream.Collectors;

import org.dataloader.BatchLoaderEnvironment;
import org.forum.server.graphql.Member;
import org.forum.server.graphql.Post;
import org.forum.server.graphql.util.DataFetchersDelegatePost;
import org.forum.server.jpa.MemberRepository;
import org.forum.server.jpa.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import graphql.GraphQLContext;
import jakarta.annotation.Resource;
import reactor.core.publisher.Flux;

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
	public Flux<Member> author(BatchLoaderEnvironment batchLoaderEnvironment, GraphQLContext graphQLContext,
			List<Post> keys) {
		if (this.logger.isDebugEnabled()) {
			List<String> idList = keys.stream().map(p -> p.getAuthorId().toString()).collect(Collectors.toList());
			this.logger.debug("Before returning Flux to load this list of posts authors: {}", String.join(",", idList));
		}
		return Flux.fromIterable(keys).map(obj -> {
			this.logger.debug("Before loading Member {} ", obj.getAuthorId());
			Member ret = this.memberRepository.findById(obj.getAuthorId()).orElse(null);

			// To check that this member is loaded from the @BatchMapping controller method, we prefix the member's name
			if (!ret.getName().startsWith("[BM] ")) {
				ret.setName("[BM] " + ret.getName());
			}

			this.logger.debug("After loading Member {}: ", obj.getAuthorId(), ret);
			return ret;
		});
	}

	@Override
	public List<Post> unorderedReturnBatchLoader(List<Long> keys, BatchLoaderEnvironment env) {
		this.logger.debug("Batch loading {} posts", keys.size());
		return this.postRepository.findByIds(keys);
	}

}
