/**
 * 
 */
package com.graphql_java_generator.client.graphqlrepository;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(ElementType.TYPE)
/**
 * This annotation marks an Interface as being a GraphQL Repository. That is: its methods are all marked with either the
 * {@link FullRequest} or the {@link PartialRequest} annotation. A Java runtime proxy is created at runtime, that will
 * execute the relevant GraphQL request, when one of its methods is executed.<BR/>
 * A GraphQL Repository may be used either in a Spring app or a non-Spring app.<BR/>
 * <B>When used in a Spring app</B>, a GraphQL Repository must respect these rules: <DIR>
 * <LI>The {@link EnableGraphQLRepositories} annotation must be specified in a spring Configuration class</LI>
 * <LI>The <I>value</I> or <I>basePackages</I> parameter of the annotation must give the package where to search for
 * GraphQL Repositories.</LI>
 * <LI>The {@link GraphQLRepository} must annotate each GraphQL repository interface</LI> </DIR> In <B>all cases</B>, a
 * GraphQL Repository must respect these rules: <DIR>
 * <LI>The GraphQL repository is an interface: the runtime code will dynamically create a proxy
 * {@link InvocationHandler} that will execute the GraphQL request</LI>
 * <LI>No implementation would be provided for this interface</LI>
 * <LI>Each of its methods must be marked either by the {@link PartialRequest} or the {@link FullRequest} annotation: it
 * is not allowed that a GraphQL repository's method is not a GraphQL request.</LI>
 * <LI>Queries, Mutations and Subscriptions may be mixed in the same GraphQL Repository</I>
 * 
 * <LI>The method parameters must respect these rules: <DIR>
 * 
 * <LI>For Partial Requests:<DIR>
 * <LI>The first parameters must be the parameters of the query/mutation/subscription as defined in the GraphQL schema.
 * Theses parameters, if any, are the parameters that follow the GraphQLRequest in the executor's matching method (see
 * the samples below, for more clarity)</LI>
 * <LI>There first parameters may not be marked with the {@link BindParameter} annotation</LI>
 * <LI>Then the method may add the Bind Parameter or GraphQL Variable values. These values must be marked with the
 * {@link BindParameter} annotation</LI> *
 * <LI>All the Bind Parameters and GraphQL Variables must have a matching parameter, in the GraphQL repository
 * method.</LI>
 * <LI>Every method of this interface must throw the {@link GraphQLRequestExecutionException}</LI> </DIR></LI>
 * 
 * <LI>For Full Requests:<DIR>
 * <LI>Each parameter (if any) must be Bind Parameter or GraphQL Variable values. They must be marked with the
 * {@link BindParameter} annotation</LI>
 * <LI>All the Bind Parameters and GraphQL Variables must have a matching parameter, in the GraphQL repository
 * method.</LI>
 * <LI>A Full Request may have no parameter. This occurs only if the given GraphQL query contains no Bind Parameter, nor
 * GraphQL variable.</LI>
 * <LI>Every method of this interface must throw the {@link GraphQLRequestExecutionException}</LI></DIR></LI>
 * 
 * </DIR></LI> </DIR>
 * 
 * @author etienne-sf
 */
public @interface GraphQLRepository {

	/** The name of the Spring bean to build. Default is to use the interface name as the bean name */
	String value() default "";

}
