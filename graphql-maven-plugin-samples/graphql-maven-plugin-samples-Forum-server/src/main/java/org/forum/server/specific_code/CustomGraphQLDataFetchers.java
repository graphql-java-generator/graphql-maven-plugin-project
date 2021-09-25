/**
 * 
 */
package org.forum.server.specific_code;

import java.util.NoSuchElementException;

import org.forum.server.graphql.Board;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import graphql.schema.DataFetcher;

/**
 * A Data Fetcher customization, that override some of the generated data fetchers. This bean is marked as
 * {@link Primary}, and will replace the generated bean. As this class inherits from the
 * {@link org.forum.server.graphql.GraphQLDataFetchers}, all non override data fetchers will work as expected by the
 * generated. Only tyhe overriden method will change the generated behavior, for these data fetchers.
 * 
 * @author etienne-sf
 */
@Component("overridenGraphQLDataFetchers")
@Primary
public class CustomGraphQLDataFetchers extends org.forum.server.graphql.GraphQLDataFetchers {

	@Override
	public DataFetcher<Board> dataFetchersDelegateMutationCreateBoard() {
		return dataFetchingEnvironment -> {
			String name = (String) graphqlUtils.getArgument(dataFetchingEnvironment.getArgument("name"),
					"${argument.type.graphQLTypeSimpleName}", "java.lang.Long", String.class);
			Boolean publiclyAvailable = (Boolean) graphqlUtils.getArgument(
					dataFetchingEnvironment.getArgument("publiclyAvailable"), "${argument.type.graphQLTypeSimpleName}",
					"java.lang.Long", Boolean.class);

			Board ret = null;
			try {
				// HERE IS WHAT's CHANGED IN THIS OVERRIDDEN VERSION:
				// We add " (Overridden DataFetcher)" to the name
				ret = dataFetchersDelegateMutation.createBoard(dataFetchingEnvironment,
						name + " (Overriden DataFetcher)", publiclyAvailable);
			} catch (NoSuchElementException e) {
				// There was no items in the Optional
			}

			if (ret != null)
				logger.debug("createBoard (Overridden DataFetcher): 1 result found");
			else
				logger.debug("createBoard (Overridden DataFetcher): no result found");

			return ret;
		};
	}
}
