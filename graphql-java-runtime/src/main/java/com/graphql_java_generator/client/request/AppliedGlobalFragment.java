/**
 * 
 */
package com.graphql_java_generator.client.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.graphql_java_generator.client.directive.Directive;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * A global Fragment, when applied, has a name and may have one or more directives.
 * 
 * @author etienne-sf
 */
public class AppliedGlobalFragment {

	final String name;

	List<Directive> directives = new ArrayList<>();

	/**
	 * Creates an instance for a global fragment, that has been read in the current {@link QueryTokenizer}. The token of
	 * this {@link QueryTokenizer} that has just been read is the fragment declaration (...fragmentName).
	 * 
	 * @param currentToken
	 *            The fragment declaration that has just been read in <I>qt</I>, for instance <I>...fragmentName</I>
	 * @param qt
	 * @throws GraphQLRequestPreparationException
	 */
	public AppliedGlobalFragment(String currentToken, QueryTokenizer qt) throws GraphQLRequestPreparationException {
		if (!currentToken.startsWith("...")) {
			throw new GraphQLRequestPreparationException(
					"The currentToken should start by \"...\", but is: '" + currentToken + "'");
		}
		name = currentToken.substring(3);

		while (qt.checkNextToken("@")) {
			// We must first read the '@'
			qt.nextToken();
			// The next token is a directive. This directive applies to the current fragment.
			directives.add(new Directive(qt));
		}
	}

	/**
	 * Appends to the given {@link StringBuilder} this fragment usage ("...fragmentName") followed by the directive
	 * declarations, if any
	 * 
	 * @param sb
	 * @param parameters
	 * @throws GraphQLRequestExecutionException
	 */
	public void appendToGraphQLRequests(StringBuilder sb, Map<String, Object> parameters)
			throws GraphQLRequestExecutionException {

		sb.append("...").append(name);

		for (Directive d : directives) {
			d.appendToGraphQLRequests(sb, parameters);
		}

	}

}
