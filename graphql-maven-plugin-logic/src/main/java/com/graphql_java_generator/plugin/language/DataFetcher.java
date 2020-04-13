/**
 * 
 */
package com.graphql_java_generator.plugin.language;

import java.util.concurrent.CompletableFuture;

import com.graphql_java_generator.GraphqlUtils;

/**
 * This class represents a GraphQL Data Fetcher. It's a piece of code that reads non scalar fields on GraphQL objects,
 * which includes: all fields for queries, mutations and subscriptions, and all non scalar fields for regular GraphQL
 * objects. <BR/>
 * They are grouped into {@link DataFetchersDelegate}s (see {@link DataFetchersDelegate} doc for more information on
 * that).
 * 
 * @author etienne-sf
 */
public interface DataFetcher {

	/**
	 * The name of the DataFetcher: it's actually the field name, as read in the GraphQL schema. This name is a valid
	 * java classname identifier, and is the name to use as a method name. For instance, for the field human.friends,
	 * the DataFetcher name is Human.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * The name of the DataFetcher, as it can be used in the Java code. If the name is a java keyword (class, default,
	 * break...), the java name it prefixed by an underscore.
	 * 
	 * @return The name of the DataFetcher, as it can be used in Java code
	 */
	default public String getJavaName() {
		return GraphqlUtils.graphqlUtils.getJavaName(getName());
	}

	/**
	 * The name of the DataFetcher, in camelCase.
	 * 
	 * @return
	 * @see #getName()
	 */
	public String getCamelCaseName();

	/**
	 * The name of the DataFetcher, in PascalCase.
	 * 
	 * @return
	 * @see #getName()
	 */
	public String getPascalCaseName();

	/**
	 * Retrieves the {@link Field} that this data fetcher fills. The arguments for the data fetcher are the arguments of
	 * its field.
	 * 
	 * @return
	 */
	public Field getField();

	/**
	 * Retrieves the {@link DataFetchersDelegate} in which this Data Fetcher is implemented
	 * 
	 * @return
	 */
	public DataFetchersDelegate getDataFetcherDelegate();

	/**
	 * Retrieves the origin of this {@link DataFetcher}, that is: the name of the object which contains the field to
	 * fetch.<BR/>
	 * There are two kinds of {@link DataFetcher}:
	 * <UL>
	 * <LI>{@link DataFetcher} for fields of object, interface(...). These {@link DataFetcher} need to have access to
	 * the object instance, that contains the field (or attribute) it fetches. This instance is the orgin, and will be a
	 * parameter in the DataFetcher call, that contains the instance of the object, for which this field is
	 * fetched.</LI>
	 * <LI>{@link DataFetcher} for query/mutation/subscription. In these case, the field that is fetched by this
	 * {@link DataFetcher} has no origin: it's the start of the request.</LI>
	 * </UL>
	 * 
	 * @return the GraphQL name of the type that contains this field, or null if this is a request ()
	 */
	public String getGraphQLOriginType();

	/**
	 * Returns true if this DataFetcher returns a {@link CompletableFuture}, which will be used within a
	 * <A HREF="https://github.com/graphql-java/java-dataloader">graphql-java java-dataloader</A> to optimize the
	 * accesses to the database. <BR/>
	 * For instance, the DataDetcher would return CompletableFuture<List<Human>> (if completableFuture is true) or
	 * List<Human> (if completableFuture is false).
	 * 
	 * @return
	 */
	public boolean isCompletableFuture();

}
