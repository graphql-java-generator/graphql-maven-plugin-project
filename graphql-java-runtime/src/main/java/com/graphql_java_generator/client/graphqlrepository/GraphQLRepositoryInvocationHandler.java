/**
 * 
 */
package com.graphql_java_generator.client.graphqlrepository;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.GraphQLMutationExecutor;
import com.graphql_java_generator.client.GraphQLQueryExecutor;
import com.graphql_java_generator.client.GraphQLSubscriptionExecutor;
import com.graphql_java_generator.client.request.AbstractGraphQLRequest;
import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.util.GraphqlUtils;

/**
 * This class is responsible to execute the method call to {@link GraphQLRepository}.
 * 
 * @author etienne-sf
 */
public class GraphQLRepositoryInvocationHandler<T> implements InvocationHandler {

	/** Logger for this class */
	private static Logger logger = LoggerFactory.getLogger(GraphQLRepositoryInvocationHandler.class);

	static GraphqlUtils graphqlUtils = GraphqlUtils.graphqlUtils;

	/** This class contains the data for each method of the T interface */
	class RegisteredMethod {
		/** The method that is registered into this object */
		Method method;

		/** True if this request is a Full Request, and False if it's a Partial Request */
		boolean fullRequest;

		/**
		 * The request string, as it has been provided in the {@link PartialRequest} or {@link FullRequest} annotation
		 */
		String request;

		/**
		 * The request name, as it has been provided in the {@link PartialRequest} or {@link FullRequest} annotation, or
		 * by the method name if it wasn't provided in the annotation.
		 */
		String requestName;

		/** The kind of request: Query, Mutation or Subscription */
		RequestType requestType;

		/** The method name, useful for logging when an error occurs */
		String executorMethodName;

		/** The name of the method that returns the GraphQLRequest. It depends if this is a partial or a full request */
		String executorGetGraphQLRequestMethodName;

		/** The query/mutation/subscription executor instance that will be called to execute this method */
		Object executor;

		/** The query/mutation/subscription executor method that must be will be called to execute this method */
		Method executorMethod;

		/**
		 * The GraphQLRequest, calculated from the request provided in the {@link PartialRequest} or {@link FullRequest}
		 * annotation
		 */
		AbstractGraphQLRequest graphQLRequest;

		/** The list of parameters of the executor method */
		public Class<?>[] executorParameterTypes;
	}

	final Class<T> repositoryInterface;
	final T proxyInstance;
	final GraphQLQueryExecutor queryExecutor;
	final GraphQLMutationExecutor mutationExecutor;
	final GraphQLSubscriptionExecutor subscriptionExecutor;

	Map<Method, RegisteredMethod> registeredMethods = new HashMap<>();

	/**
	 * This constructor provides the query, mutation and subscription that have been defined in the GraphQL schema. The
	 * mutation and subscription are optional in the GraphQL schema, so these two executors may be null.
	 * 
	 * @param repositoryInterface
	 *            The {@link GraphQLRepository} interface, that this {@link InvocationHandler} has to manage. It is
	 *            mandatory.
	 * @param queryExecutor
	 *            The GraphQL query executor. It is mandatory.
	 * @param mutationExecutor
	 *            The GraphQL mutation executor. It may be null (only if no mutation type is provided in the GraphQL
	 *            schema).
	 * @param subscriptionExecutor
	 *            The GraphQL subscription executor. It may be null (only if no subscription type is provided in the
	 *            GraphQL schema).
	 * @throws GraphQLRequestPreparationException
	 */
	@Autowired
	public GraphQLRepositoryInvocationHandler(Class<T> repositoryInterface, GraphQLQueryExecutor queryExecutor,
			GraphQLMutationExecutor mutationExecutor, GraphQLSubscriptionExecutor subscriptionExecutor)
			throws GraphQLRequestPreparationException {
		if (repositoryInterface == null) {
			throw new NullPointerException("'repositoryInterface' may not be null");
		}
		if (!repositoryInterface.isInterface()) {
			throw new NullPointerException("The 'repositoryInterface' (" + repositoryInterface.getName()
					+ ") must be an interface, but it is not");
		}
		if (repositoryInterface.getAnnotation(GraphQLRepository.class) == null) {
			throw new GraphQLRequestPreparationException(
					"This InvocationHandler may only be called for GraphQL repositories. "
							+ "GraphQL repositories must be annotated with the 'com.graphql_java_generator.annotation.GraphQLRepository' annotation. "
							+ "But the '" + repositoryInterface.getName() + "' is not.");
		}
		if (queryExecutor == null) {
			throw new NullPointerException("'queryExecutor' may not be null");
		}

		// All basic tests are Ok. Let's go
		this.repositoryInterface = repositoryInterface;
		this.queryExecutor = queryExecutor;
		this.mutationExecutor = mutationExecutor;
		this.subscriptionExecutor = subscriptionExecutor;

		////////////////////////////////////////////////////////////////////////////////////////////////////////
		// CREATION IF THE PROXY INSTANCE
		@SuppressWarnings("unchecked")
		Class<T>[] classes = (Class<T>[]) new Class<?>[] { repositoryInterface };
		@SuppressWarnings("unchecked")
		T t = (T) Proxy.newProxyInstance(repositoryInterface.getClassLoader(), classes, this);
		// Assignment is done in two step, so that the @SuppressWarnings is not on the whole method.
		this.proxyInstance = t;

		for (Method method : repositoryInterface.getDeclaredMethods()) {
			registeredMethods.put(method, registerMethod(method));
		}
	}

	/**
	 * Getter for the proxy instance that has been created from this invocation handler.
	 * 
	 * @return
	 */
	T getProxyInstance() {
		return proxyInstance;
	}

	/**
	 * Register the given method of the repository interface, and returns its characteristics
	 * 
	 * @throws GraphQLRequestPreparationException
	 */
	private RegisteredMethod registerMethod(Method method) throws GraphQLRequestPreparationException {
		RegisteredMethod registeredMethod = new RegisteredMethod();

		// Some checks, to begin with:
		if (!Arrays.asList(method.getExceptionTypes()).contains(GraphQLRequestExecutionException.class)) {
			throw new GraphQLRequestPreparationException("Error while preparing the GraphQL Repository, on the method '"
					+ method.getDeclaringClass().getName() + "." + method.getName()
					+ "(..). Every method of GraphQL repositories must throw the 'com.graphql_java_generator.exception.GraphQLRequestExecutionException' exception, but the '"
					+ method.getName() + "' doesn't");
		}

		PartialRequest partialRequest = method.getAnnotation(PartialRequest.class);
		FullRequest fullRequest = method.getAnnotation(FullRequest.class);

		if (partialRequest != null) {
			registerRequest(registeredMethod, method, false, partialRequest.requestName(), partialRequest.requestType(),
					partialRequest.request());
		} else if (fullRequest != null) {
			registerRequest(registeredMethod, method, true, null, fullRequest.requestType(), fullRequest.request());
		} else {
			throw new GraphQLRequestPreparationException("Error while preparing the GraphQL Repository, on the method '"
					+ method.getDeclaringClass().getName() + "." + method.getName()
					+ "(..). Every method of GraphQL repositories must be annotated by either @PartialRequest or @FullRequest. But the '"
					+ method.getName() + "()' isn't.");
		}
		return registeredMethod;
	}

	/**
	 * Register the given method with the given parameters
	 * 
	 * @param registeredMethod
	 *            The class where all the registering info should be stored
	 * @param method
	 *            The method that is being registered
	 * @throws GraphQLRequestPreparationException
	 */
	private void registerRequest(RegisteredMethod registeredMethod, Method method, boolean fullRequest,
			String requestName, RequestType requestType, String request) throws GraphQLRequestPreparationException {
		registeredMethod.method = method;
		registeredMethod.fullRequest = fullRequest;
		registeredMethod.requestName = requestName;
		registeredMethod.requestType = requestType;
		registeredMethod.executor = getExecutor(method, requestType);
		registeredMethod.executorParameterTypes = getParameterTypes(method);

		if (fullRequest) {
			registeredMethod.executorMethodName = "exec";
			registeredMethod.executorGetGraphQLRequestMethodName = "getGraphQLRequest";
		} else {
			registeredMethod.executorMethodName = (registeredMethod.requestName == null
					|| registeredMethod.requestName.equals("")) ? method.getName() : registeredMethod.requestName;
			registeredMethod.executorGetGraphQLRequestMethodName = "get"
					+ graphqlUtils.getPascalCase(registeredMethod.executorMethodName) + "GraphQLRequest";
		}
		if (registeredMethod.executorParameterTypes[registeredMethod.executorParameterTypes.length - 1] == Map.class) {
			// The executor method name should finish by WithBindValues
			if (!registeredMethod.executorMethodName.endsWith("WithBindValues")) {
				registeredMethod.executorMethodName += "WithBindValues";
			}
		}

		try {
			registeredMethod.executorMethod = registeredMethod.executor.getClass()
					.getMethod(registeredMethod.executorMethodName, registeredMethod.executorParameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new GraphQLRequestPreparationException("Error while preparing the GraphQL Repository, on the method '"
					+ method.getDeclaringClass().getName() + "." + method.getName()
					+ "(..). Couldn't find the matching executor method '" + registeredMethod.executorMethodName
					+ "' for executor class '" + registeredMethod.executor.getClass().getName()
					+ "' with these parameters: " + registeredMethod.executorParameterTypes, e);
		}
		if (registeredMethod.executorMethod.getReturnType() != method.getReturnType()) {
			// Hum, that sounds bad.
			// But it is tolerated when this is a full request: the QueryType and QueryTypeResponse are the same type
			// QueryTypeResponse is an empty class that just inherits from QueryType. It should not exist: it's a
			// misconception in the early ages of the plugin.
			// It's the same story for MutationType.
			//
			// The issue here is that the removing the QueryTypeResponse would make the code cleaner, but this would
			// break the existing code for some of the plugin users.
			// So we deal with it, until the v2.0, where the XxxxResponse classes won't be generated anymore.
			boolean thisIsBad = true;

			if (registeredMethod.fullRequest //
					&& (registeredMethod.requestType == RequestType.query
							|| registeredMethod.requestType == RequestType.mutation)
					&& (method.getReturnType().getName() + "Response")
							.equals(registeredMethod.executorMethod.getReturnType().getName())) {
				// XxxResponse and Xxx are the same class content, when Xxx is either a Mutation or a Subscription and
				// it's a Full Query
				thisIsBad = false;
			}

			if (thisIsBad) {
				throw new GraphQLRequestPreparationException(
						"Error while preparing the GraphQL Repository, on the method '"
								+ method.getDeclaringClass().getName() + "." + method.getName()
								+ "(..). This method should return "
								+ registeredMethod.executorMethod.getReturnType().getName() + " but returns "
								+ method.getReturnType().getName());
			}
		}
		registeredMethod.request = request;
		registeredMethod.graphQLRequest = getGraphQLRequest(registeredMethod);
	}

	/**
	 * Retrieves the GraphQlRequest object from the data of the given {@link RegisteredMethod}.
	 * 
	 * @param registeredMethod
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	private AbstractGraphQLRequest getGraphQLRequest(RegisteredMethod registeredMethod)
			throws GraphQLRequestPreparationException {
		Method getGraphQlMethod;
		try {
			getGraphQlMethod = registeredMethod.executor.getClass()
					.getDeclaredMethod(registeredMethod.executorGetGraphQLRequestMethodName, String.class);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new GraphQLRequestPreparationException("Error while preparing the GraphQL Repository, on the method '"
					+ registeredMethod.method.getDeclaringClass().getName() + "." + registeredMethod.method.getName()
					+ "(..). Could not find the '" + registeredMethod.executorGetGraphQLRequestMethodName
					+ " method of the '" + registeredMethod.executor.getClass().getName() + "' class.");
		}

		try {
			return (AbstractGraphQLRequest) getGraphQlMethod.invoke(registeredMethod.executor,
					registeredMethod.request);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new GraphQLRequestPreparationException("Error while preparing the GraphQL Repository, on the method '"
					+ registeredMethod.method.getDeclaringClass().getName() + "." + registeredMethod.method.getName()
					+ "(..): " + e.getClass().getSimpleName() + " when trying to invoke the '"
					+ registeredMethod.executorGetGraphQLRequestMethodName + "' method of the '"
					+ registeredMethod.executor.getClass().getName() + "' class", e);
		}
	}

	/**
	 * Invocation of the {@link InvocationHandler}. This method is called when a method of the T interface is called.
	 * This call is delegated to the relevant Query/Mutation/Subscription executor. <BR/>
	 * {@inheritDoc}
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		RegisteredMethod registeredMethod = registeredMethods.get(method);

		if (registeredMethod == null) {
			throw new GraphQLRequestExecutionException("The method '" + method.getDeclaringClass().getName() + "."
					+ method.getName()
					+ "' has not been stored in initialization phase of this InvocationHandler. Is this method coming from the right interface? (that is, the same as the one this InvocationHandler has been created with?)");
		}

		// Let's build the argument array: we have the GraphQLRequest, then the provided arguments
		// If the given args doesn't contain enough parameters for the executor method, it means that we have to add the
		// trailing Object[] parameter
		int nbArgs = (args == null ? 0 : args.length);
		boolean addNullArg = registeredMethod.executorParameterTypes.length > nbArgs + 1;
		Object[] params = new Object[nbArgs + 1 + (addNullArg ? 1 : 0)];
		params[0] = registeredMethod.graphQLRequest;
		for (int i = 0; i < nbArgs; i += 1) {
			params[i + 1] = args[i];
		}
		if (addNullArg) {
			params[params.length - 1] = new Object[0];
		}

		logger.debug("The argument list to call the '{}' method is: {}", method.getName(), params);

		return registeredMethod.executorMethod.invoke(registeredMethod.executor, params);
	}

	/**
	 * Returns the array of expected parameters for the method name in the executor, from the parameters of the given
	 * method
	 * 
	 * @param method
	 *            The method of the {@link GraphQLRepository}
	 * @return
	 */
	private Class<?>[] getParameterTypes(Method method) {
		Class<?>[] params = method.getParameterTypes();
		boolean bMapBindParameters = params.length > 0 && params[params.length - 1].equals(Map.class);
		boolean bObjectsBindParameters = params.length > 0 && params[params.length - 1].isArray();
		// The expected params are: ObjectResponse, the parameters of the given method, and possibly an object array (if
		// the given method has no parameter to receive the bind parameters)
		int nbExpectedParams = 1 + params.length + ((!bMapBindParameters && !bObjectsBindParameters) ? 1 : 0);

		Class<?>[] expectedParams = new Class<?>[nbExpectedParams];
		expectedParams[0] = ObjectResponse.class;
		System.arraycopy(params, 0, expectedParams, 1, params.length);
		if (!bMapBindParameters && !bObjectsBindParameters) {
			expectedParams[expectedParams.length - 1] = Object[].class;
		}
		logger.debug("The expected parameter types for the '{}.{}' method are: {}",
				method.getDeclaringClass().getName(), method.getName(), expectedParams);
		return expectedParams;
	}

	/**
	 * Retrieves the executor that matches the given {@link RequestType}
	 * 
	 * @param method
	 * @param requestType
	 * @return
	 * @throws GraphQLRequestPreparationException
	 *             If no executor of this {@link RequestType} has been provided
	 */
	private Object getExecutor(Method method, RequestType requestType) throws GraphQLRequestPreparationException {
		switch (requestType) {
		case query:
			return queryExecutor;
		case mutation:
			return mutationExecutor;
		case subscription:
			return subscriptionExecutor;
		}

		throw new GraphQLRequestPreparationException("The '" + method.getDeclaringClass().getName() + "."
				+ method.getName() + "()' method refers to a '" + requestType.toString()
				+ "', but there is no such executor found. Check if the GraphQL has such a request type defined.");
	}

}
