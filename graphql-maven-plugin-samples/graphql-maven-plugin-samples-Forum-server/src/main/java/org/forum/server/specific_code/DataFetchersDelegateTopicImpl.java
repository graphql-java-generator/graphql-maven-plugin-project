/**
 * 
 */
package org.forum.server.specific_code;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Resource;

import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.DataLoader;
import org.forum.server.graphql.DataFetchersDelegateTopic;
import org.forum.server.graphql.Member;
import org.forum.server.graphql.Post;
import org.forum.server.graphql.Topic;
import org.forum.server.jpa.MemberRepository;
import org.forum.server.jpa.PostRepository;
import org.forum.server.jpa.TopicRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.util.GraphqlUtils;

import graphql.schema.DataFetchingEnvironment;

/**
 * This class implements the access to the database : there are so many ways to do this, that the developper has still
 * work to do. But all the GraphQL boilerplate is generated.<BR/>
 * The {@link GraphQLDataFetchersDelegate} interface is generated from the given schema
 * 
 * @author etienne-sf
 */
@Component
public class DataFetchersDelegateTopicImpl implements DataFetchersDelegateTopic {

	/** The logger for this instance */
	protected Logger logger = LoggerFactory.getLogger(DataFetchersDelegateTopicImpl.class);

	static final String DATE_FORMAT = "yyyy-MM-dd";
	static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

	@Resource
	MemberRepository memberRepository;
	@Resource
	PostRepository postRepository;
	@Resource
	TopicRepository topicRepository;

	@Resource
	GraphqlUtils graphqlUtils;

	@Override
	public CompletableFuture<Member> author(DataFetchingEnvironment dataFetchingEnvironment,
			DataLoader<Long, Member> dataLoader, Topic source) {
		return dataLoader.load(source.getAuthorId());
	}

	/**
	 * This method should not be called. The {@link DataFetchersDelegateMemberImpl#batchLoader(List)} should be called
	 * instead. The name returned by this method is marked by "[SL] ", to check that in integration tests.
	 */
	@Override
	public Member author(DataFetchingEnvironment dataFetchingEnvironment, Topic origin) {
		logger.debug("Loading author of topic {}", origin.getId());
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

	@Override
	public List<Post> posts(DataFetchingEnvironment dataFetchingEnvironment, Topic source, Long memberId,
			String memberName, Date since) {

		logger.debug("Loading posts of topic {}, with memberId={}, memberName={} and since={}", source.getId(),
				memberId, memberName, since);

		if (since == null) {
			// This should not happen, as since is mandatory
			throw new NullPointerException("since may not be null");
		} else {

			// The memberId and memberName are Optional. The since param is mandatory.
			// So there are 4 combinations for the request:

			// since
			if (memberId == null && memberName == null) {
				logger.debug("Loading posts of topic {}, with since={}", source.getId(), since);
				return graphqlUtils.iterableToList(postRepository.findByTopicIdAndSince(source.getId(), since));
			}
			// memberId, since
			else if (memberName == null) {
				logger.debug("Loading posts of topic {}, with memberId={} and since={}", source.getId(), memberId,
						since);
				return graphqlUtils.iterableToList(
						postRepository.findByTopicIdAndMemberIdAndSince(source.getId(), memberId, since));
			}
			// memberName,since
			else if (memberId == null) {
				logger.debug("Loading posts of topic {}, with memberName={} and since={}", source.getId(), memberName,
						since);
				return graphqlUtils.iterableToList(
						postRepository.findByTopicIdAndMemberNameAndSince(source.getId(), memberName, since));
			}
			// memberId, memberName, since
			else {
				logger.debug("Loading posts of topic {}, with memberId={}, memberName={} and since={}", source.getId(),
						memberId, memberName, since);
				return graphqlUtils.iterableToList(postRepository
						.findByTopicIdAndMemberIdAndMemberNameAndSince(source.getId(), memberId, memberName, since));
			}
		}
	}

	@Override
	public List<Topic> batchLoader(List<Long> keys, BatchLoaderEnvironment env) {
		logger.debug("Batch loading {} topics", keys.size());
		return topicRepository.findByIds(keys);
	}

}
