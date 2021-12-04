/**
 * 
 */
package com.graphql_java_generator.client.request;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.util.GraphqlUtils;

/**
 * This builder is provided for compatibility of existing code, that would have been developed before the 1.6 release.
 * It allows to create {@link ObjectResponse}, that will define the content of the GraphQL request toward the server.
 * 
 * @author etienne-sf
 */
public class Builder {

	GraphqlUtils graphqlUtils = GraphqlUtils.graphqlUtils;

	/**
	 * The graphQLRequestClass inherits from {@link AbstractGraphQLRequest}, and contains the generated context that
	 * allows proper GraphQL request executions
	 */
	final Class<? extends AbstractGraphQLRequest> graphQLRequestClass;
	/** The name of the query or mutation, in the query type or the mutation type */
	final String fieldName;
	/** The request type, that will be sent when creating the {@link AbstractGraphQLRequest} */
	final RequestType requestType;
	/** True if this build will build a Full Request, false for Partial Request. */
	final boolean fullRequest;
	/** The list of input types for this query or mutation */
	final InputParameter[] inputParams;

	/** The {@link objectResponse} that is built by this Builder */
	ObjectResponse objectResponse = null;

	/**
	 * This Builder allows to build a Full request, that is request as you can execute in the graphiql interface. <BR/>
	 * When calling the {@link #withQueryResponseDef(String)}, a new {@link AbstractGraphQLRequest} is created by
	 * calling its {@link AbstractGraphQLRequest#AbstractGraphQLRequest(String)} constructor.
	 * 
	 * @param graphQLRequestClass
	 *            The graphQLRequestClass inherits from {@link AbstractGraphQLRequest}, and contains the generated
	 *            context that allows proper GraphQL request executions
	 */
	public Builder(Class<? extends AbstractGraphQLRequest> graphQLRequestClass) {
		this.graphQLRequestClass = graphQLRequestClass;
		this.fieldName = null;
		this.requestType = null; // It will be calculated by the QLRequest instance, from the request
		this.fullRequest = true;
		this.inputParams = null;
	}

	/**
	 * This Builder allows to build a Partial request, that is a request for only one query/mutation/subscription. <BR/>
	 * When calling the {@link #withQueryResponseDef(String)}, the query request can be something like the one below,
	 * based on the <I>hero</I> query of the star wars schema:
	 * 
	 * <PRE>
	 * {id appearsIn friends {name friends {friends{id name appearsIn}}}}
	 * </PRE>
	 * 
	 * This defines only the part of the GraphQL request that defines the expected response content from the GraphQL
	 * server.
	 * 
	 * @param graphQLRequestClass
	 *            The graphQLRequestClass inherits from {@link AbstractGraphQLRequest}, and contains the generated
	 *            context that allows proper GraphQL request executions
	 * @param fieldName
	 *            The query/mutation/subscription name, as defined in the GraphQL schema
	 * @param requestType
	 *            The request type allows to search <I>fieldName</I> in the query or in the mutation or the subscription
	 * @param inputParams
	 *            The input parameters for this query/mutation/subscription
	 */
	public Builder(Class<? extends AbstractGraphQLRequest> graphQLRequestClass, String fieldName,
			RequestType requestType, InputParameter... inputParams) {
		this.graphQLRequestClass = graphQLRequestClass;
		this.fieldName = fieldName;
		this.requestType = requestType;
		this.fullRequest = false;
		this.inputParams = (inputParams == null) ? new InputParameter[0] : inputParams;

		if (requestType == null) {
			throw new NullPointerException("The requestType is mandatory");
		}
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
		if (queryResponseDef == null) {
			queryResponseDef = "";
		}

		String genericErrorMessage = null;

		try {
			// Is it a full request ?
			if (fullRequest) {
				genericErrorMessage = "Could not create an instance of GraphQLRequest (for a Full request)";
				objectResponse = (ObjectResponse) graphQLRequestClass.getConstructor(String.class)
						.newInstance(queryResponseDef);
			} else {
				// No, it's a Partial request
				genericErrorMessage = "Could not create an instance of GraphQLRequest (for a Partial request)";

				Constructor<? extends AbstractGraphQLRequest> constructor = graphQLRequestClass
						.getConstructor(String.class, RequestType.class, String.class, InputParameter[].class);
				objectResponse = (ObjectResponse) constructor.newInstance(queryResponseDef, requestType, fieldName,
						inputParams);
			}

		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException
				| SecurityException e) {
			throw new GraphQLRequestPreparationException(genericErrorMessage + ": " + e.getMessage(), e);
		} catch (InvocationTargetException e) {
			if (e.getTargetException() == null) {
				throw new GraphQLRequestPreparationException(genericErrorMessage, e);
			} else if (e.getTargetException() instanceof GraphQLRequestPreparationException) {
				throw (GraphQLRequestPreparationException) e.getTargetException();
			} else if (e.getTargetException() instanceof RuntimeException) {
				throw (RuntimeException) e.getTargetException();
			} else {
				throw new GraphQLRequestPreparationException(genericErrorMessage, e);
			}
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
		if (objectResponse == null) {
			// Is it a full request ?
			if (fullRequest) {
				// No query has been defined. That's not allowed for Full Request (we can't guess what to do)
				throw new GraphQLRequestPreparationException(
						"Empty request are not allowed for Full Request. Please call the Builder.withQueryResponseDef(String) method to defined the GraphQL request");
			} else {
				// We parse an empty request. It's valid for query/mutation/subscription that are scalar. And for non
				// scalar response, all scalar fields will be added.
				withQueryResponseDef("");
			}
		}
		return objectResponse;
	}

}
