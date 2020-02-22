package com.graphql_java_generator.samples.forum.server.specific_code;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.samples.forum.server.DataFetchersDelegateMember;
import com.graphql_java_generator.samples.forum.server.Member;
import com.graphql_java_generator.samples.forum.server.jpa.MemberRepository;

@Component
public class DataFetchersDelegateMemberImpl implements DataFetchersDelegateMember {

	/** The logger for this instance */
	protected Logger logger = LoggerFactory.getLogger(DataFetchersDelegateMemberImpl.class);

	@Resource
	MemberRepository memberRepository;

	@Override
	public List<Member> batchLoader(List<UUID> keys) {
		logger.debug("Batch loading {} members", keys.size());
		return memberRepository.findByIds(keys);
	}

}
