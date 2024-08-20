/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import com.graphql_java_generator.plugin.generate_code.GenerateCodeDocumentParser;
import com.graphql_java_generator.plugin.generate_code.GenerateCodeGenerator;
import com.graphql_java_generator.plugin.language.BatchLoader;
import com.graphql_java_generator.plugin.language.DataFetcher;
import com.graphql_java_generator.plugin.language.DataFetchersDelegate;
import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.Type;
import com.graphql_java_generator.util.GraphqlUtils;

import lombok.Data;
import lombok.ToString;

/**
 * This class represents a GraphQL Data Fetcher. It's a piece of code which responsability is to read non scalar fields
 * on GraphQL objects, which includes: all fields for queries, mutations and subscriptions, and all non scalar fields
 * for regular GraphQL objects. <BR/>
 * They are grouped into {@link DataFetchersDelegate}s (see {@link DataFetchersDelegate} doc for more information on
 * that).<BR/>
 * Its characteristics are read by {@link GenerateCodeDocumentParser}, and used by {@link GenerateCodeGenerator} and the
 * Velocity templates to generate the code of the DataFechers, and their declaration in the GraphQLProvider.<BR/>
 * The arguments for the data fetcher are the arguments of its source field in the GraphQL schema.
 * 
 * @author etienne-sf
 */
@Data
public class DataFetcherImpl implements DataFetcher {

	private Field field;

	@ToString.Exclude
	private DataFetchersDelegate dataFetchersDelegate;

	/**
	 * Retrieves the origin of this {@link DataFetcher}, that is: the name of the object which contains the field to
	 * fetch.<BR/>
	 * There are two kinds of {@link DataFetcher}:
	 * <UL>
	 * <LI>{@link DataFetcher} for fields of object, interface(...). These {@link DataFetcher} need to have access to
	 * the object instance, that contains the field (or attribute) it fetches. This instance is the origin, and will be
	 * a parameter in the DataFetcher call, that contains the instance of the object, for which this field is
	 * fetched.</LI>
	 * <LI>{@link DataFetcher} for query/mutation/subscription. In these case, the field that is fetched by this
	 * {@link DataFetcher} has no origin: it's the start of the request.</LI>
	 * </UL>
	 */
	private Type graphQLOriginType = null;

	private final boolean batchMapping;
	private final boolean withDataLoader;

	/**
	 * 
	 * @param field
	 *            The field that this data fetcher must fill
	 * @param dataFetcherDelegate
	 *            The {@link DataFetchersDelegate} that contains this data fetcher. This constructor attached the newly
	 *            created data fetcher into the given {@link DataFetchersDelegate}
	 * @param declareInGraphQLProvider
	 *            true if this data fetcher must be declared to the graphql-java framework. This value is generally
	 *            true. It should be false only when two data fetchers of the same exist, for instance when the
	 *            {@link DataFetchersDelegate} should implement one such data fetcher with a data loader and one
	 *            without. In this case, one data fetcher is registered in the GraphQLProvider. And this data fetcher is
	 *            also declared in the GraphQLDataFetchers class, and is responsible to all the relevant method in the
	 *            {@link DataFetchersDelegate}.
	 * @param batchMapping
	 *            Indicates if this DataFetcher should be annotated with the <code>@BatchMapping</code> annotation. This
	 *            is controlled by the <code>generateBatchMappingDataFetchers</code> plugin parameter
	 * @param withDataLoader
	 *            indicates that this DataFetcher will be actually loaded later, with the help of a {@link BatchLoader}.
	 * @param graphQLOriginType
	 *            The origin of this {@link DataFetcher}, that is: the name of the object which contains the field to
	 *            fetch.<BR/>
	 *            There are two kinds of {@link DataFetcher}:
	 *            <UL>
	 *            <LI>{@link DataFetcher} for fields of object, interface(...). These {@link DataFetcher} need to have
	 *            access to the object instance, that contains the field (or attribute) it fetches. This instance is the
	 *            orgin, and will be a parameter in the DataFetcher call, that contains the instance of the object, for
	 *            which this field is fetched.</LI>
	 *            <LI>{@link DataFetcher} for query/mutation/subscription. In these case, the field that is fetched by
	 *            this {@link DataFetcher} has no origin: it's the start of the request.</LI>
	 *            </UL>
	 */
	public DataFetcherImpl(Field field, DataFetchersDelegate dataFetcherDelegate, boolean declareInGraphQLProvider,
			boolean batchMapping, boolean withDataLoader, Type graphQLOriginType) {
		this.field = field;
		this.dataFetchersDelegate = dataFetcherDelegate;
		this.batchMapping = batchMapping;
		this.withDataLoader = withDataLoader;
		this.graphQLOriginType = graphQLOriginType;

		dataFetcherDelegate.getDataFetchers().add(this);
	}

	@Override
	public String getName() {
		return this.field.getName();
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
