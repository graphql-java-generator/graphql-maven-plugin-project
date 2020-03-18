/**
 * 
 */
package com.graphql_java_generator.client.request;

import java.util.StringTokenizer;

import com.graphql_java_generator.GraphqlUtils;
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

	final ObjectResponse objetResponse;

	public Fragment(StringTokenizer st, String packageName) throws GraphQLRequestPreparationException {

		// We expect a string like this: " fragmentName on fragmentTargetType"
		// Let's read these three tokens
		name = readNextRealToken(st, "reading fragment name", null);
		readNextRealToken(st, "looking for the 'on' token of the fragment definition", "on");
		typeName = readNextRealToken(st, "reading fragment name", null);

		///////////////////////////////////////////////////////////////////////////////////
		// The content of the fragment is the same as reading the response for the given type.

		// So, we wait for the first {
		while (st.hasMoreElements()) {
			String token = st.nextToken();

			if (token.equals(" ") || token.equals("\n") || token.equals("\r")) {
				// Ok, let's go to the next token
				continue;
			}
			if (token.equals("{")) {
				// We've found the object response start
				break;
			}

			// Hum, hum. We should not arrive here
			throw new GraphQLRequestPreparationException("Unexpected token '" + token
					+ "' while searching for the starting '{' in fragment '" + getName() + "'");
		}

		// Ok, we're ready to read the fragment content
		String classname = packageName + "." + GraphqlUtils.graphqlUtils.getJavaName(typeName);

		try {
			getClass().getClassLoader().loadClass(classname);
		} catch (ClassNotFoundException e) {
			throw new GraphQLRequestPreparationException(
					"Could not load class '" + classname + "' for type '" + typeName + "'", e);
		}

		// Builder.QueryField queryField = new Builder.QueryField(null, clazz, null);
		// queryField.readTokenizerForResponseDefinition(st);
		objetResponse = null;

		throw new RuntimeException("Reading of the ObjectResponse is not implemented");
	}

	/**
	 * Reads the next real token, that is the next token that is not a separator
	 * 
	 * @param st
	 * @param action
	 *            The action for which the real token is needed (use to get some context in a the exception message, if
	 *            any). The exception message will be: <I>"error occurs while " + action</I>
	 * @param expected
	 *            If expected is not null, this method will check that the real token read is equal to this expected
	 *            value
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	private String readNextRealToken(StringTokenizer st, String action, String expected)
			throws GraphQLRequestPreparationException {
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (Builder.STRING_TOKENIZER_DELIMITER.contains(token))
				continue;

			// We found a non null token
			if (expected != null && !expected.equals(token))
				throw new GraphQLRequestPreparationException("The token read is '" + token
						+ "', but the expected one is '" + expected + "' while " + action);
			// Ok, we're done
			return token;
		}

		throw new GraphQLRequestPreparationException("End of string found while " + action);
	}

	public String getName() {
		return name;
	}

	public String getTypeName() {
		return typeName;
	}

}
