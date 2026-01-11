/**
 * 
 */
package com.graphql_java_generator.client.graphqlrepository;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.GraphQLMutationExecutor;
import com.graphql_java_generator.client.GraphQLMutationReactiveExecutor;
import com.graphql_java_generator.client.GraphQLQueryExecutor;
import com.graphql_java_generator.client.GraphQLQueryReactiveExecutor;
import com.graphql_java_generator.client.GraphQLSubscriptionExecutor;
import com.graphql_java_generator.client.GraphQLSubscriptionReactiveExecutor;
import com.graphql_java_generator.client.SubscriptionCallback;
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

		/**
		 * The list of registered parameters from the repository method. This list allows to manage the BindParameter
		 * and BindVariable annotated parameters
		 */
		List<RegisteredParameter> registeredParameters = new ArrayList<>();
	}

	class RegisteredParameter {
		/**
		 * The name of the parameter, as it appears in the definition of the method, in the {@link GraphQLRepository}
		 * interface
		 */
		String name;

		/** The Java type of this parameter, as defined in the {@link GraphQLRepository} interface */
		public Class<?> type;

		/**
		 * The name of the bind parameter or GraphQL variable, as it appears in the GraphQL request, either as $name,
		 * ?name or $name
		 */
		String bindParameterName;
	}

	final Class<T> repositoryInterface;
	final T proxyInstance;
	final Object queryExecutor;
	final Object mutationExecutor;
	final Object subscriptionExecutor;

	Map<Method, RegisteredMethod> registeredMethods = new HashMap<>();

	/**
	 * Builds the instance from the given Spring {@link ApplicationContext}: it extracts the query, mutation and
	 * subscription executors that have been generated from the GraphQL schema. The mutation and subscription are
	 * optional in the GraphQL schema, so these two executors may be null. <BR/>
	 * Note: when more than one GraphQL schema are used, a GraphQL Repository requests may be relative to only one
	 * GraphQL schema. The {@link GraphQLRepository}s annotation must provide the queryExecutor of this GraphQL
	 * schema.<BR/>
	 * This constructor can only be used on Spring applications, because of its ctx parameter.
	 * 
	 * @param repositoryInterface
	 *            The {@link GraphQLRepository} interface, that this {@link InvocationHandler} has to manage. It is
	 *            mandatory.
	 * @param ctx
	 *            The Spring {@link ApplicationContext} that allows to retrieve the query, mutation and subscription
	 *            executors
	 * @throws GraphQLRequestPreparationException
	 */
	public GraphQLRepositoryInvocationHandler(Class<T> repositoryInterface, ApplicationContext ctx)
			throws GraphQLRequestPreparationException {
		Package executorPackage;
		logger.trace("Creating a new GraphQLRepositoryInvocationHandler for the GraphQL Repository {}", //$NON-NLS-1$
				repositoryInterface.getName());
		this.repositoryInterface = repositoryInterface;
		String graphQLQueryExecutorClass; 

		// Does the GraphQLRepository annotation define a queryExecutor ?
		GraphQLRepository graphQLRepoAnnotation = repositoryInterface.getAnnotation(GraphQLRepository.class);
		GraphQLReactiveRepository graphQLReactiveRepoAnnotation = repositoryInterface
				.getAnnotation(GraphQLReactiveRepository.class);

		if (graphQLRepoAnnotation != null) {
			executorPackage = (graphQLRepoAnnotation.queryExecutor() == GraphQLQueryExecutor.class) ?
			// No value has been provided for the queryExecutor annotation field. There should be only one GraphQL
			// schema generated code (that is: it's the standard case)
					null :
					// else: let's retrieve the package of the executor given in the GraphQLRepository annotation
					graphQLRepoAnnotation.queryExecutor().getPackage();

			graphQLQueryExecutorClass = "GraphQLQueryExecutor"; // Used below if no QueryExecutor is found
			queryExecutor = getBeanOfTypeAndPackage(ctx, executorPackage, GraphQLQueryExecutor.class, true);
			mutationExecutor = getBeanOfTypeAndPackage(ctx, executorPackage, GraphQLMutationExecutor.class, false);
			subscriptionExecutor = getBeanOfTypeAndPackage(ctx, executorPackage, GraphQLSubscriptionExecutor.class,
					false);
		} else if (graphQLReactiveRepoAnnotation != null) {
			executorPackage = (graphQLReactiveRepoAnnotation.queryExecutor() == GraphQLQueryReactiveExecutor.class) ?
			// No value has been provided for the queryExecutor annotation field. There should be only one GraphQL
			// schema generated code (that is: it's the standard case)
					null :
					// else: let's retrieve the package of the executor given in the GraphQLRepository
					// annotation
					graphQLReactiveRepoAnnotation.queryExecutor().getPackage();

			graphQLQueryExecutorClass = "GraphQLQueryReactiveExecutor"; // Used below if no QueryExecutor is found
			queryExecutor = getBeanOfTypeAndPackage(ctx, executorPackage, GraphQLQueryReactiveExecutor.class, true);
			mutationExecutor = getBeanOfTypeAndPackage(ctx, executorPackage, GraphQLMutationReactiveExecutor.class,
					false);
			subscriptionExecutor = getBeanOfTypeAndPackage(ctx, executorPackage,
					GraphQLSubscriptionReactiveExecutor.class, false);
		} else {
			throw new GraphQLRequestPreparationException("The '" + repositoryInterface.getName() //$NON-NLS-1$
					+ "' class has been passed to the GraphQLRepositoryInvocationHandler constructor as a GraphQLRepository. It should be annotated by one of these annotations: '" //$NON-NLS-1$
					+ GraphQLRepository.class.getName() + "' or '" + GraphQLReactiveRepository.class.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		logger.trace("The executors found are: queryExecutor={}, mutationExecutor={}, subscriptionExecutor={}", //$NON-NLS-1$
				(queryExecutor == null) ? null : queryExecutor.getClass().getName(),
				(mutationExecutor == null) ? null : mutationExecutor.getClass().getName(),
				(subscriptionExecutor == null) ? null : subscriptionExecutor.getClass().getName());

		// queryExecutor may not be null: every GraphQL schema must at least contain a Query type
		if (queryExecutor == null) {
			if (executorPackage == null) {
				throw new RuntimeException("Error while preparing the GraphQL Repository '" //$NON-NLS-1$
						+ repositoryInterface.getName()
						+ "': found no Spring Bean of type "+graphQLQueryExecutorClass+". Please check the Spring component scan path."); //$NON-NLS-1$
			} else {
				throw new IllegalArgumentException("Error while preparing the GraphQL Repository '" //$NON-NLS-1$
						+ repositoryInterface.getName()
						+ "': found no Spring Bean of type "+graphQLQueryExecutorClass+" in the same package as the provided QueryExecutor (" //$NON-NLS-1$
						+ graphQLRepoAnnotation.queryExecutor().getName()
						+ "). Please check the Spring component scan path."); //$NON-NLS-1$
			}
		}

		proxyInstance = createProxyInstance();
	}

	/**
	 * Do some checks on the instance attribute, then create the dynamic proxy
	 * 
	 * @return
	 * @throws GraphQLRequestPreparationException
	 *             Thrown if the given parameter for the constructor where not correct.
	 */
	private T createProxyInstance() throws GraphQLRequestPreparationException {
		if (repositoryInterface == null) {
			throw new NullPointerException("'repositoryInterface' may not be null"); //$NON-NLS-1$
		}
		if (!repositoryInterface.isInterface()) {
			throw new RuntimeException("The 'repositoryInterface' (" + repositoryInterface.getName() //$NON-NLS-1$
					+ ") must be an interface, but it is not"); //$NON-NLS-1$
		}
		if (repositoryInterface.getAnnotation(GraphQLRepository.class) == null
				&& repositoryInterface.getAnnotation(GraphQLReactiveRepository.class) == null) {
			throw new GraphQLRequestPreparationException(
					"This InvocationHandler may only be called for GraphQL repositories. " //$NON-NLS-1$
							+ "GraphQL repositories must be annotated with one of the '" //$NON-NLS-1$
							+ GraphQLRepository.class.getName() + "' or '" + GraphQLReactiveRepository.class.getName() //$NON-NLS-1$
							+ "' annotations. But the '" + repositoryInterface.getName() + "' is not."); //$NON-NLS-1$//$NON-NLS-2$
		}

		// All basic tests are Ok. Let's go

		////////////////////////////////////////////////////////////////////////////////////////////////////////
		// CREATION OF THE PROXY INSTANCE
		@SuppressWarnings("unchecked")
		Class<T>[] classes = (Class<T>[]) new Class<?>[] { repositoryInterface };
		@SuppressWarnings("unchecked")
		T t = (T) Proxy.newProxyInstance(repositoryInterface.getClassLoader(), classes, this);

		for (Method method : repositoryInterface.getDeclaredMethods()) {
			registeredMethods.put(method, registerMethod(method));
		}

		return t;
	}

	/**
	 * Retrieve a Spring bean from the given context, that is of (or extends/implements) the provided class, and is in
	 * the provided package. This method is used to find the query, mutation and subscription executors that are in the
	 * provided package, that is: that where generated from the good GraphQL schema.
	 * 
	 * @param ctx
	 *            The Spring {@link ApplicationContext} that contains the beans
	 * @param pack
	 *            The package where beans are searched
	 * @param clazz
	 *            The class of the bean. The found bean can either be of this class, implement or extend it
	 */
	<C> C getBeanOfTypeAndPackage(ApplicationContext ctx, Package pack, Class<? extends C> clazz, boolean mandatory) {
		logger.trace("[getBeanOfTypeAndPackage] Starting execution"); //$NON-NLS-1$
		if (pack == null) {
			Collection<? extends C> beans = ctx.getBeansOfType(clazz).values();
			logger.trace("[getBeanOfTypeAndPackage] pack is null, and beans size is ", Integer.valueOf(beans.size())); //$NON-NLS-1$
			if (beans.size() == 0) {
				if (mandatory) {
					throw new RuntimeException("Error while preparing the GraphQL Repository, on the method '" //$NON-NLS-1$
							+ repositoryInterface.getName() + ": at least one Spring Bean of type '" //$NON-NLS-1$
							+ clazz.getName() + "' is expected, but none have been found"); //$NON-NLS-1$
				} else {
					return null;
				}
			} else if (beans.size() == 1) {
				return beans.iterator().next();
			} else {
				throw new RuntimeException("Error while preparing the GraphQL Repository, on the method '" //$NON-NLS-1$
						+ repositoryInterface.getName() + ": one Spring Bean of type '" + clazz.getName() //$NON-NLS-1$
						+ "' is expected, but " + beans.size() //$NON-NLS-1$
						+ " have been found. This usely occurs when you have more than one GraphQL schemas, " //$NON-NLS-1$
						+ "and you are using GraphQL Repositories, " //$NON-NLS-1$
						+ "and at least one of your GraphQLRepository annotation didn't provide the QueryExecutor (through its queryExecutor parameter)"); //$NON-NLS-1$
			}
		} else {
			Collection<? extends C> beans = ctx.getBeansOfType(clazz).values();
			logger.trace("[getBeanOfTypeAndPackage] pack is not null, iterating through the '{}' beans (size={})", //$NON-NLS-1$
					clazz.getName(), Integer.valueOf(beans.size()));
			for (C bean : beans) {
				logger.trace("[getBeanOfTypeAndPackage]    iterating on bean {}", bean.getClass()); //$NON-NLS-1$
				if (bean.getClass().getPackage() == pack) {
					// Ok, we've found the bean of the good package.
					logger.trace("[getBeanOfTypeAndPackage]      found bean {}: we're done", bean.getClass()); //$NON-NLS-1$
					return bean;
				}
			} // for
			logger.trace("[getBeanOfTypeAndPackage]    No bean found"); //$NON-NLS-1$
			return null;
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
			throw new GraphQLRequestPreparationException("Error while preparing the GraphQL Repository, on the method '" //$NON-NLS-1$
					+ method.getDeclaringClass().getName() + "." + method.getName() //$NON-NLS-1$
					+ "(..). Every method of GraphQL repositories must throw the 'com.graphql_java_generator.exception.GraphQLRequestExecutionException' exception, but the '" //$NON-NLS-1$
					+ method.getName() + "' doesn't"); //$NON-NLS-1$
		}

		PartialRequest partialRequest = method.getAnnotation(PartialRequest.class);
		FullRequest fullRequest = method.getAnnotation(FullRequest.class);

		if (partialRequest != null) {
			registerMethod(registeredMethod, method, false, partialRequest.requestName(), partialRequest.requestType(),
					partialRequest.request());
		} else if (fullRequest != null) {
			registerMethod(registeredMethod, method, true, null, fullRequest.requestType(), fullRequest.request());
		} else {
			throw new GraphQLRequestPreparationException("Error while preparing the GraphQL Repository, on the method '" //$NON-NLS-1$
					+ method.getDeclaringClass().getName() + "." + method.getName() //$NON-NLS-1$
					+ "(..). Every method of GraphQL repositories must be annotated by either @PartialRequest or @FullRequest. But the '" //$NON-NLS-1$
					+ method.getName() + "()' isn't."); //$NON-NLS-1$
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
	 * @param fullRequest
	 *            True if this method is marked with the {@link FullRequest} annotation, false otherwise, that is: the
	 *            method is marked with the {@link PartialRequest} annotation
	 * @param requestName
	 *            The name of the request, as read in the {@link PartialRequest} annotation, null for full requests
	 * @param requestType
	 *            The type of request. Query is the default.
	 * @param request
	 *            The string of the request. For {@link FullRequest}, it must be a valid GraphQL request. For more
	 *            information, have a look at the <A HREF=
	 *            "https://github.com/graphql-java-generator/graphql-maven-plugin-project/wiki/client_exec_graphql_requests">Client
	 *            wiki</A>
	 * @throws GraphQLRequestPreparationException
	 */
	private void registerMethod(RegisteredMethod registeredMethod, Method method, boolean fullRequest,
			String requestName, RequestType requestType, String request) throws GraphQLRequestPreparationException {
		registeredMethod.method = method;
		registeredMethod.fullRequest = fullRequest;
		registeredMethod.requestName = requestName;
		registeredMethod.requestType = requestType;
		registeredMethod.executor = getExecutor(method, requestType);
		registerParameters(registeredMethod, method);

		if (fullRequest) {
			// It's a full request
			registeredMethod.executorMethodName = "execWithBindValues"; //$NON-NLS-1$
			registeredMethod.executorGetGraphQLRequestMethodName = "getGraphQLRequest"; //$NON-NLS-1$
		} else if (registeredMethod.requestName == null || registeredMethod.requestName.equals("")) { //$NON-NLS-1$
			// It's a partial request, with no given requestName
			registeredMethod.executorMethodName = method.getName() + "WithBindValues"; //$NON-NLS-1$
			registeredMethod.executorGetGraphQLRequestMethodName = "get" + graphqlUtils.getPascalCase(method.getName()) //$NON-NLS-1$
					+ "GraphQLRequest"; //$NON-NLS-1$
		} else {
			// It's a partial request, with a given requestName
			registeredMethod.executorMethodName = registeredMethod.requestName + "WithBindValues"; //$NON-NLS-1$
			registeredMethod.executorGetGraphQLRequestMethodName = "get" //$NON-NLS-1$
					+ graphqlUtils.getPascalCase(registeredMethod.requestName) + "GraphQLRequest"; //$NON-NLS-1$
		}

		try {
			registeredMethod.executorMethod = registeredMethod.executor.getClass()
					.getMethod(registeredMethod.executorMethodName, registeredMethod.executorParameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			StringBuilder parameters = new StringBuilder();
			String separator = ""; //$NON-NLS-1$
			for (Class<?> clazz : registeredMethod.executorParameterTypes) {
				parameters.append(separator);
				parameters.append(clazz.getName());
				separator = ","; //$NON-NLS-1$
			}
			throw new GraphQLRequestPreparationException("Error while preparing the GraphQL Repository, on the method '" //$NON-NLS-1$
					+ method.getDeclaringClass().getName() + "." + method.getName() //$NON-NLS-1$
					+ "(..). Couldn't find the matching executor method '" + registeredMethod.executorMethodName //$NON-NLS-1$
					+ "' for executor class '" + registeredMethod.executor.getClass().getName() //$NON-NLS-1$
					+ "' with these parameters: [" + parameters //$NON-NLS-1$
					+ "]. Consider marking bind parameters and GraphQL variables with the @BindParameter annotation.", //$NON-NLS-1$
					e);
		}

		// The returned value by the executor method must be assignable to the returned value for the GraphQL Repository
		// method
		if (!method.getReturnType().isAssignableFrom(registeredMethod.executorMethod.getReturnType())) {
			throw new GraphQLRequestPreparationException("Error while preparing the GraphQL Repository, on the method '" //$NON-NLS-1$
					+ method.getDeclaringClass().getName() + "." + method.getName() + "(..). This method should return " //$NON-NLS-1$ //$NON-NLS-2$
					+ registeredMethod.executorMethod.getReturnType().getName() + " but returns " //$NON-NLS-1$
					+ method.getReturnType().getName() + " (and the later can not be assigned to the former)"); //$NON-NLS-1$
		}

		registeredMethod.request = request;
		registeredMethod.graphQLRequest = getGraphQLRequest(registeredMethod);
	}

	/**
	 * This method scans the method parameters, and register its parameters in the
	 * {@link RegisteredMethod#registeredParameters} attribute. It also fills the
	 * {@link RegisteredMethod#executorParameterTypes} attribute.
	 *
	 * @param registeredMethod
	 *            The record for the method that is being registered
	 * @param method
	 *            The method of the {@link GraphQLRepository}
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	private void registerParameters(RegisteredMethod registeredMethod, Method method)
			throws GraphQLRequestPreparationException {
		List<Class<?>> executorParameterTypes = new ArrayList<>();
		// The first parameter of the executor is the GraphQL Request, given as an ObjectResponse
		executorParameterTypes.add(ObjectResponse.class);

		// We need to know if some previous parameter has the BindParameter annotation: it is not allowed to have
		// parameters without the 'BindParameter' annotation, after parameters that have this annotation
		boolean foundBindParameterAnnotation = false;

		// Let's go through all the parameters of the method that we're registering
		for (Parameter param : method.getParameters()) {
			RegisteredParameter regParam = new RegisteredParameter();
			regParam.name = param.getName();

			// Some particular "changes" with the GraphQL schema are tolerated:
			switch (param.getType().getName()) {
			case "boolean": //$NON-NLS-1$
				regParam.type = Boolean.class;
				break;
			case "double": //$NON-NLS-1$
				// The GraphQL Float type maps actually into a java Double class
				regParam.type = Double.class;
				break;
			case "float": //$NON-NLS-1$
			case "java.lang.Float": //$NON-NLS-1$
				// Float is not a valid type for a GraphQL parameter.
				throw new GraphQLRequestPreparationException(
						"Error while preparing the GraphQL Repository, on the method '" //$NON-NLS-1$
								+ registeredMethod.method.getDeclaringClass().getName() + "." //$NON-NLS-1$
								+ registeredMethod.method.getName()
								+ "(..). Float and float parameter types are not allowed. Please note that the GraphQL Float type maps to the Double java type."); //$NON-NLS-1$
			case "int": //$NON-NLS-1$
				regParam.type = Integer.class;
				break;
			default:
				regParam.type = param.getType();
			}

			// Map parameter and vararg (object[]) are not accepted
			if (Map.class.isAssignableFrom(regParam.type) || Object[].class.isAssignableFrom(regParam.type)) {
				throw new GraphQLRequestPreparationException(
						"Error while preparing the GraphQL Repository, on the method '" //$NON-NLS-1$
								+ registeredMethod.method.getDeclaringClass().getName() + "." //$NON-NLS-1$
								+ registeredMethod.method.getName()
								+ "(..). Map and vararg (Object[]) are not allowed. But the '" + param.getName() //$NON-NLS-1$
								+ "' is an instance of '" + param.getType().getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			BindParameter bindParameter = param.getAnnotation(BindParameter.class);
			if (bindParameter == null) {
				if (registeredMethod.fullRequest) {
					if (param.getAnnotatedType().getType().getTypeName()
							.startsWith(SubscriptionCallback.class.getName())) {
						// Ok, it's the callback parameter. It may not have the BindParameter annotation (and if he has,
						// we ignore it)
						executorParameterTypes.add(regParam.type);
					} else {
						// There is no BindParameter annotation, and it's not the callback parameter
						throw new GraphQLRequestPreparationException(
								"Error while preparing the GraphQL Repository, on the method '" //$NON-NLS-1$
										+ registeredMethod.method.getDeclaringClass().getName() + "." //$NON-NLS-1$
										+ registeredMethod.method.getName()
										+ "(..). This request is a full request: all its parameters must be marked with the '" //$NON-NLS-1$
										+ BindParameter.class.getSimpleName() + "' annotation. But the '" //$NON-NLS-1$
										+ param.getName() + "' isn't marked with this annotation."); //$NON-NLS-1$
					}
				} else if (foundBindParameterAnnotation) {
					throw new GraphQLRequestPreparationException(
							"Error while preparing the GraphQL Repository, on the method '" //$NON-NLS-1$
									+ registeredMethod.method.getDeclaringClass().getName() + "." //$NON-NLS-1$
									+ registeredMethod.method.getName()
									+ "(..). It is not allowed to have parameters without the '" //$NON-NLS-1$
									+ BindParameter.class.getSimpleName()
									+ "' annotation, after parameters that have this annotation. The '" //$NON-NLS-1$
									+ param.getName() + "' parameter lacks the '" + BindParameter.class.getSimpleName() //$NON-NLS-1$
									+ "' annotation."); //$NON-NLS-1$
				} else {
					// It's a partial request, and we didn't find any parameter with the BindParameter yet.
					// So we've found a parameter that should be a parameter of the executor method.
					executorParameterTypes.add(regParam.type);
				}
			} else {
				// This parameter is annotated with the BindParameter. Let's store the bind parameter name that is is
				// associated to.
				foundBindParameterAnnotation = true;
				regParam.bindParameterName = bindParameter.name();
			}

			registeredMethod.registeredParameters.add(regParam);
		} // for

		// The last parameter of executorParameterTypes is the Map for the Bind Parameter, which may not be a parameter
		// of the GraphQL repository method
		executorParameterTypes.add(Map.class);

		// The next lien generates a runtime error ! :(
		// registeredMethod.executorParameterTypes = (Class<?>[]) (executorParameterTypes.toArray());
		// So, let's loop manually
		registeredMethod.executorParameterTypes = new Class<?>[executorParameterTypes.size()];
		for (

				int i = 0; i < registeredMethod.executorParameterTypes.length; i += 1) {
			registeredMethod.executorParameterTypes[i] = executorParameterTypes.get(i);
		}

		logger.debug("The expected parameter types for the '{}.{}' method are: {}", //$NON-NLS-1$
				method.getDeclaringClass().getName(), method.getName(), registeredMethod.executorParameterTypes);
	}

	/**
	 * Retrieves the GraphQlRequest object from the data of the given {@link RegisteredMethod}.
	 * 
	 * @param registeredMethod
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	@SuppressWarnings("static-method")
	private AbstractGraphQLRequest getGraphQLRequest(RegisteredMethod registeredMethod)
			throws GraphQLRequestPreparationException {
		Method getGraphQlMethod;
		try {
			getGraphQlMethod = registeredMethod.executor.getClass()
					.getDeclaredMethod(registeredMethod.executorGetGraphQLRequestMethodName, String.class);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new GraphQLRequestPreparationException("Error while preparing the GraphQL Repository, on the method '" //$NON-NLS-1$
					+ registeredMethod.method.getDeclaringClass().getName() + "." + registeredMethod.method.getName() //$NON-NLS-1$
					+ "(..). Could not find the '" + registeredMethod.executorGetGraphQLRequestMethodName //$NON-NLS-1$
					+ " method of the '" + registeredMethod.executor.getClass().getName() + "' class."); //$NON-NLS-1$ //$NON-NLS-2$
		}

		try {
			return (AbstractGraphQLRequest) getGraphQlMethod.invoke(registeredMethod.executor,
					registeredMethod.request);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new GraphQLRequestPreparationException("Error while preparing the GraphQL Repository, on the method '" //$NON-NLS-1$
					+ registeredMethod.method.getDeclaringClass().getName() + "." + registeredMethod.method.getName() //$NON-NLS-1$
					+ "(..): " + e.getClass().getSimpleName() + " when trying to invoke the '" //$NON-NLS-1$ //$NON-NLS-2$
					+ registeredMethod.executorGetGraphQLRequestMethodName + "' method of the '" //$NON-NLS-1$
					+ registeredMethod.executor.getClass().getName() + "' class", e); //$NON-NLS-1$
		}
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
		default:
			throw new GraphQLRequestPreparationException("The '" + method.getDeclaringClass().getName() + "." //$NON-NLS-1$ //$NON-NLS-2$
					+ method.getName() + "()' method refers to a '" + requestType.toString() //$NON-NLS-1$
					+ "', but there is no such executor found. Check if the GraphQL has such a request type defined."); //$NON-NLS-1$
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
			// Spring calls some Object methods. If it's the case, let's execute it.
			if (method.getName().equals("hashCode")) { //$NON-NLS-1$
				return Integer.valueOf(1);
			}
			if (method.getDeclaringClass().equals(Object.class)) {
				return method.invoke(proxy, args);
			}

			// Otherwise, there is something incorrect that is going on
			throw new GraphQLRequestExecutionException("The method '" + method.getDeclaringClass().getName() + "." //$NON-NLS-1$ //$NON-NLS-2$
					+ method.getName()
					+ "' has not been stored in initialization phase of this InvocationHandler. Is this method coming from the right interface? (that is, the same as the one this InvocationHandler has been created with?)"); //$NON-NLS-1$
		}

		List<Object> params = new ArrayList<>();
		Map<String, Object> bindParameters = new HashMap<>();
		// The first parameter is the GraphQL request
		params.add(registeredMethod.graphQLRequest);
		// The we loop the given args match the registeredParameters.

		// A first check, to begin with
		if ((args == null ? 0 : args.length) != registeredMethod.registeredParameters.size()) {
			throw new GraphQLRequestExecutionException("Error while invoking the '" + method.getDeclaringClass() + "." //$NON-NLS-1$ //$NON-NLS-2$
					+ method.getName() + "': the proxy invocation handler receives " + (args == null ? 0 : args.length) //$NON-NLS-1$
					+ ", but it has registered " + registeredMethod.registeredParameters.size() + " parameters"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (args != null) {
			for (int i = 0; i < args.length; i += 1) {
				RegisteredParameter regParam = registeredMethod.registeredParameters.get(i);

				if (regParam.bindParameterName == null) {
					// This is regular parameter, that we must just add to the argument list
					params.add(args[i]);
				} else {
					bindParameters.put(regParam.bindParameterName, args[i]);
				}
			} // for
		}
		params.add(bindParameters);

		// We must hide any InvocationTargetException exception, so that only GraphQLRequestExecutionException are
		// received by the caller
		try {
			return registeredMethod.executorMethod.invoke(registeredMethod.executor, params.toArray());
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}
}
