/**
 * 
 */
package com.graphql_java_generator.client.request;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * This builder is provided for compatibility of existing code, that would have been developed before the 1.6 release.
 * It allows to create {@link ObjectResponse}, that will define the content of the GraphQL request toward the server.
 * 
 * @author etienne-sf
 */
public class Builder {

	GraphqlUtils graphqlUtils = GraphqlUtils.graphqlUtils;

	/** The class that owns the requested field. This class is either a query type or a mutation type */
	final Class<?> owningClass;
	/** The name of the query or mutation, in the query type or the mutation type */
	final String fieldName;
	/** The list of input types for this query or mutation */
	final List<InputParameter> inputParams;

	/** The {@link objectResponse} that is built by this Builder */
	ObjectResponse objectResponse = null;

	public Builder(Class<?> owningClass, String fieldName, InputParameter... inputParams) {
		this.owningClass = owningClass;
		this.fieldName = fieldName;
		this.inputParams = Arrays.asList(inputParams);
	}

	/**
	 * Builds a {@link ObjectResponse} from a part of a GraphQL query. This part define what's expected as a response
	 * for the field of the current {@link ObjectResponse} for this builder.
	 * 
	 * @param queryResponseDef
	 *            A part of a response, for instance (for the hero query of the Star Wars GraphQL schema): "{ id name
	 *            friends{name}}"<BR/>
	 *            No special character are allowed (linefeed...).<BR/>
	 *            This parameter can be a null or an empty string. In this case, all scalar fields are added.
	 * @param episode
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder withQueryResponseDef(String queryResponseDef) throws GraphQLRequestPreparationException {
		String packageName = owningClass.getClass().getPackage().getName();

		try {
			Class<?> graphQLRequestClass = getClass().getClassLoader().loadClass(packageName + ".GraphQLRequest");
			objectResponse = (ObjectResponse) graphQLRequestClass
					.getConstructor(String.class, Class.class, String.class)
					.newInstance(queryResponseDef, graphqlUtils.getFieldType(owningClass, fieldName, true), fieldName);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			throw new GraphQLRequestPreparationException("Could not create an instance of GraphQLRequest", e);
		}

		return this;
	}

	/**
	 * Returns the built {@link ObjectResponse}. If no field (either scalar or suboject) has been added, then all scalar
	 * fields are added.
	 * 
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public ObjectResponse build() throws GraphQLRequestPreparationException {
		return objectResponse;
	}

}
