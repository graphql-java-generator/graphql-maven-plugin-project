/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.plugin.CodeGenerator;
import com.graphql_java_generator.plugin.DocumentParser;
import com.graphql_java_generator.plugin.language.BatchLoader;
import com.graphql_java_generator.plugin.language.DataFetcher;
import com.graphql_java_generator.plugin.language.DataFetchersDelegate;
import com.graphql_java_generator.plugin.language.Field;

import lombok.Data;

/**
 * This class represents a GraphQL Data Fetcher. It's a piece of code which responsability is to read non scalar fields
 * on GraphQL objects, which includes: all fields for queries, mutations and subscriptions, and all non scalar fields
 * for regular GraphQL objects. <BR/>
 * They are grouped into {@link DataFetchersDelegate}s (see {@link DataFetchersDelegate} doc for more information on
 * that).<BR/>
 * Its characteristics are read by {@link DocumentParser}, and used by {@link CodeGenerator} and the Velocity templates
 * to generate the code of the DataFechers, and their declaration in the GraphQLProvider.<BR/>
 * The arguments for the data fetcher are the arguments of its source field in the GraphQL schema.
 * 
 * @author etienne-sf
 */
@Data
public class DataFetcherImpl implements DataFetcher {

	private Field field;

	private DataFetchersDelegate dataFetcherDelegate;

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
	 */
	private String graphQLOriginType = null;

	private boolean completableFuture = false;

	/**
	 * 
	 * @param field
	 *            The field that this data fetcher must fill
	 * @param completableFuture
	 *            indicates that this DataFetcher will be actually loaded later, with the help of a {@link BatchLoader}.
	 */
	public DataFetcherImpl(Field field, boolean completableFuture) {
		this.field = field;
		this.completableFuture = completableFuture;
	}

	@Override
	public String getName() {
		return field.getCamelCaseName();
	}

	/** {@inheritDoc} */
	@Override
	public String getCamelCaseName() {
		return GraphqlUtils.graphqlUtils.getCamelCase(getName());
	}

	/** {@inheritDoc} */
	@Override
	public String getPascalCaseName() {
		return GraphqlUtils.graphqlUtils.getPascalCase(getName());
	}
}
