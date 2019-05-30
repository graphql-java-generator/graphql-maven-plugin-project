/**
 * 
 */
package graphql.mavenplugin.language;

import graphql.mavenplugin.CodeGenerator;
import graphql.mavenplugin.DocumentParser;
import graphql.mavenplugin.language.impl.TypeUtil;

/**
 * This class represents a GraphQL Data Fetcher. It's a piece of code which responsability is to read non scalar fields
 * on GraphQL objects, which includes: all fields for queries, mutations and subscriptions, and all non scalar fields
 * for regular GraphQL objects. <BR/>
 * They are grouped into {@link DataFetcherDelegate}s (see {@link DataFetcherDelegate} doc for more information on
 * that).<BR/>
 * Its characteristics are read by {@link DocumentParser}, and used by {@link CodeGenerator} and the Velocity templates
 * to generate the code of the DataFechers, and their declaration in the GraphQLProvider.<BR/>
 * The arguments for the data fetcher are the arguments of its source field in the GraphQL schema.
 * 
 * @author EtienneSF
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
	 * The name of the DataFetcher, in camelCase.
	 * 
	 * @return
	 * @see #getName()
	 */
	default public String getCamelCaseName() {
		return TypeUtil.getCamelCase(getName());
	}

	/**
	 * The name of the DataFetcher, in PascalCase.
	 * 
	 * @return
	 * @see #getName()
	 */
	default public String getPascalCaseName() {
		return TypeUtil.getPascalCase(getName());
	}

	/**
	 * Retrieves the {@link Field} that this data fetcher fills. The arguments for the data fetcher are the arguments of
	 * its field.
	 * 
	 * @return
	 */
	public Field getField();

	/**
	 * Retrieves the {@link DataFetcherDelegate} in which this Data Fetcher is implemented
	 * 
	 * @return
	 */
	public DataFetcherDelegate getDataFetcherDelegate();

	/**
	 * Retrieves the source name, that is: the name of the object which contains the field to fetch (if it is a GraphQL
	 * object), or null if the field is a field of a query, a mutation or a subscription.
	 * 
	 * @return the source name, or null if there is no source
	 */
	public String getSourceName();

}
