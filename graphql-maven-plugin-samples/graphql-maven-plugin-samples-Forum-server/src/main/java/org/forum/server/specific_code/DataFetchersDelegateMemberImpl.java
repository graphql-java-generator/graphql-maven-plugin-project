package org.forum.server.specific_code;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.forum.server.graphql.DataFetchersDelegateMember;
import org.forum.server.graphql.Member;
import org.forum.server.jpa.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
