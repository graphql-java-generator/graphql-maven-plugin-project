/**
 * 
 */
package org.allGraphQLCases.server;

import java.util.List;

import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author EtienneSF
 *
 */
@Component
public class DataFetchersDelegateCommentedImpl implements DataFetchersDelegateCommented {

	@Override
	public List<String> comments(DataFetchingEnvironment dataFetchingEnvironment, Commented source) {
		// TODO Auto-generated method stub
		return null;
	}

}
