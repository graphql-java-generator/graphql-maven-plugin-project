/**
 * 
 */
package org.allGraphQLCases.server.impl;

import java.util.List;

import org.allGraphQLCases.server.DataFetchersDelegateCommented;
import org.allGraphQLCases.server.SIP_Commented_SIS;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetchingEnvironment;
import jakarta.annotation.Resource;

/**
 * @author etienne-sf
 *
 */
@Component
public class DataFetchersDelegateCommentedImpl implements DataFetchersDelegateCommented {

	@Resource
	DataGenerator generator;

	@Override
	public List<String> comments(DataFetchingEnvironment dataFetchingEnvironment, SIP_Commented_SIS source) {
		return generator.generateInstanceList(String.class, 10);
	}

}
