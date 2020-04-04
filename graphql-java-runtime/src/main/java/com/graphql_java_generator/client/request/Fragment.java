/**
 * 
 */
package com.graphql_java_generator.client.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.client.directive.Directive;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * @author etienne-sf
 *
 */
public class Fragment {

	/** The name of this fragment */
	final String name;

	/** The GraphQL type on which the fragment is based (on clause of the fragment definition) */
	final String typeName;

	/** The content of the GraphQL fragment, as defined in the GraphQL request */
	QueryField content = null;

	/** The directive that applies on this fragment (only for inline fragments) */
	List<Directive> directives = new ArrayList<>();

	/**
	 * Reads a Fragment definition, from the current {@link QueryTokenizer}.
	 * 
	 * @param qt
	 *            The {@link QueryTokenizer}, that just read the "fragment" keyword, or the "..." for inline fragment
	 * @param packageName
	 *            The package name is used to load the java class that has been generated for the given fragment's
	 *            GraphQL type
	 * @param inlineFragment
	 *            true if this fragment is an inline fragment. In this case, there is no fragment name to read.
	 * @param clazz
	 *            The owning class is mandatory for inlineFragment: if the "on Type" clause is not give, we need it to
	 *            load the proper java class (that represents the proper GraphQL type)
	 * @throws GraphQLRequestPreparationException
	 */
	public Fragment(QueryTokenizer qt, String packageName, boolean inlineFragment, Class<?> clazz)
			throws GraphQLRequestPreparationException {

		// We expect a string like this: " fragmentName on fragmentTargetType"
		// Let's read these three tokens
		if (inlineFragment) {
			name = null;
			// For inline fragment, the "on Type" part of the definition is optional.
			if (qt.checkNextToken("on")) {
				qt.readNextRealToken("on", "looking for the 'on' token of the fragment definition");
				typeName = qt.readNextRealToken(null, "reading fragment name");
			} else {
				typeName = null;
			}
		} else {
			name = qt.readNextRealToken(null, "reading fragment name");
			qt.readNextRealToken("on", "looking for the 'on' token of the fragment definition");
			typeName = qt.readNextRealToken(null, "reading fragment name");
		}

		///////////////////////////////////////////////////////////////////////////////////
		// The content of the fragment is the same as reading the response for the given type.

		// So, we wait for the first {
		while (qt.hasMoreTokens()) {
			String token = qt.nextToken();

			if (token.equals("@")) {
				// This Fragment contains a (or more) directive
				directives.add(new Directive(qt));
				// Let's iterate once more
				continue;
			}
			if (token.equals("{")) {
				// We've found the object response start, let's go to the next part.
				break;
			}

			// Hum, hum. We should not arrive here
			throw new GraphQLRequestPreparationException("Unexpected token '" + token
					+ "' while searching for the starting '{' in fragment '" + getName() + "'");
		}

		// Ok, we're ready to read the fragment content
		if (typeName != null) {
			// If the typeName was provided in the fragment definition, then we load the clas that represents the
			// GraphQL type on which the fragment applies. This allows to check the input parameters, and their type
			String classname = packageName + "." + GraphqlUtils.graphqlUtils.getJavaName(typeName);
			try {
				clazz = getClass().getClassLoader().loadClass(classname);
			} catch (ClassNotFoundException e) {
				throw new GraphQLRequestPreparationException(
						"Could not load class '" + classname + "' for type '" + typeName + "'", e);
			}
		}

		content = new QueryField(clazz);
		content.readTokenizerForResponseDefinition(qt);
	}

	public String getName() {
		return name;
	}

	public String getTypeName() {
		return typeName;
	}

	public void appendToGraphQLRequests(StringBuilder sb, Map<String, Object> params)
			throws GraphQLRequestExecutionException {

		// For inline fragment, we write neither "fragment", nor the name
		if (name != null) {
			sb.append("fragment ");
			sb.append(name);
		}
		if (typeName != null) {
			sb.append(" on ");
			sb.append(typeName);
		}

		for (Directive d : directives) {
			d.appendToGraphQLRequests(sb, params);
		}
		content.appendToGraphQLRequests(sb, params, false);
	}

	/**
	 * Adds the <I>__typename</I> field into this fragment, and all the subojects it contains.
	 * 
	 * @throws GraphQLRequestPreparationException
	 */
	public void addTypenameFields() throws GraphQLRequestPreparationException {
		content.addTypenameFields();
	}

}
