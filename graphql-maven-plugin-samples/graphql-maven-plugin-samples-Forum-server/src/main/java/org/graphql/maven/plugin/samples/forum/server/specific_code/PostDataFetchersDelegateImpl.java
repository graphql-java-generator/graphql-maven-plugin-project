/**
 * 
 */
package org.graphql.maven.plugin.samples.forum.server.specific_code;

import org.graphql.maven.plugin.samples.forum.server.Member;
import org.graphql.maven.plugin.samples.forum.server.PostDataFetchersDelegate;
import org.springframework.stereotype.Component;

/**
 * @author EtienneSF
 */
@Component
public class PostDataFetchersDelegateImpl implements PostDataFetchersDelegate {

	@Override
	public Member postAuthor(String postId) {
		// TODO Auto-generated method stub
		return null;
	}

}
