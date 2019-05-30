/**
 * 
 */
package org.graphql.maven.plugin.samples.forum.server.specific_code;

import javax.annotation.Resource;

import org.graphql.maven.plugin.samples.forum.server.Member;
import org.graphql.maven.plugin.samples.forum.server.Post;
import org.graphql.maven.plugin.samples.forum.server.PostDataFetchersDelegate;
import org.graphql.maven.plugin.samples.forum.server.jpa.MemberRepository;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author EtienneSF
 */
@Component
public class PostDataFetchersDelegateImpl implements PostDataFetchersDelegate {

	@Resource
	MemberRepository memberRepository;

	@Override
	public Member author(DataFetchingEnvironment dataFetchingEnvironment, Post source) {
		return memberRepository.findById(source.getAuthorId()).get();
	}

}
