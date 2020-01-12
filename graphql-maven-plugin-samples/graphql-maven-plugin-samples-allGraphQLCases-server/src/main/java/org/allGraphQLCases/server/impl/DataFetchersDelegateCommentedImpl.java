/**
 * 
 */
package org.allGraphQLCases.server.impl;

import java.util.List;

import org.allGraphQLCases.server.Commented;
import org.allGraphQLCases.server.DataFetchersDelegateCommented;
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
