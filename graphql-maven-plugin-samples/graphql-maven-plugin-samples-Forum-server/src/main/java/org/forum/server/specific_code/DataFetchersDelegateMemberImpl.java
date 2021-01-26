package org.forum.server.specific_code;

import java.util.List;

import javax.annotation.Resource;

import org.dataloader.BatchLoaderEnvironment;
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
	public List<Member> batchLoader(List<Long> keys, BatchLoaderEnvironment env) {
		logger.debug("Batch loading {} members", keys.size());
		List<Member> ret = memberRepository.findByIds(keys);

		// Let's mark all the entries retrieved here by [BL] (Batch Loader), to check this in integration tests.
		// These tests are in the graphql-maven-plugin-samples-Forum-client project
		for (Member m : ret) {
			m.setName("[BL] " + m.getName());
		}

		return ret;
	}

}
