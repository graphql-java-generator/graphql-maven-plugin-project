/**
 * 
 */
package com.graphql_java_generator.samples.forum.server.specific_code;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.graphql_java_generator.samples.forum.server.Member;
import com.graphql_java_generator.samples.forum.server.Post;
import com.graphql_java_generator.samples.forum.server.PostDataFetchersDelegate;
import com.graphql_java_generator.samples.forum.server.jpa.MemberRepository;

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
