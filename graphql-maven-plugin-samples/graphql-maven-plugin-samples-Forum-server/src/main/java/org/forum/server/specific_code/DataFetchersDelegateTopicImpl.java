/**
 * 
 */
package org.forum.server.specific_code;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
	public CompletableFuture<List<Post>> posts(DataFetchingEnvironment dataFetchingEnvironment,
			DataLoader<Long, Post> dataLoader, Topic origin, Long memberId, String memberName, Date since) {
		// When the data is modeled this way (that is: in a relational database), using Data Loader is not an
		// optimization.
		// But this is used here for integration tests
		List<Long> ids = new ArrayList<>();

		for (Post post : posts(dataFetchingEnvironment, origin, memberId, memberName, since)) {
			ids.add(post.getId());
		}

		if (logger.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Retrieving posts for topic {id=");
			sb.append(origin.getId());
			sb.append(", autorId=");
			sb.append(memberId);
			sb.append(", since=");
			sb.append(since);
			sb.append("}. The requested ids of posts are:");
			for (Long id : ids) {
				sb.append(" ");
				sb.append(id);
			}
			logger.debug(sb.toString());
		}

		return dataLoader.loadMany(ids);
	}

	@Override
	public List<Topic> unorderedReturnBatchLoader(List<Long> keys, BatchLoaderEnvironment env) {
		logger.debug("Batch loading {} topics", keys.size());
		return topicRepository.findByIds(keys);
	}

	@Override
	public Member author(DataFetchingEnvironment dataFetchingEnvironment, Topic origin) {
		return memberRepository.findById(origin.getAuthorId()).orElseGet(() -> null);
	}

}
