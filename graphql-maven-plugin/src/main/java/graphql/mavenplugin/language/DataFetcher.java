/**
 * 
 */
package graphql.mavenplugin.language;

import graphql.mavenplugin.CodeGenerator;
import graphql.mavenplugin.DocumentParser;
import graphql.mavenplugin.language.impl.TypeUtil;

/**
 * This class represents a GraphQL Data Fetcher. Its characteristics are read by {@link DocumentParser}, and used by
 * {@link CodeGenerator} and the Velocity templates to generate the code of the DataFechers, and their declaration in
 * the GraphQLProvider.<BR/>
 * The arguments for the data fetcher are the arguments of its field.
 * 
 * @author EtienneSF
 */
public interface DataFetcher {

	/**
	 * The name of the DataFetcher. This name is a valid java classname identifier, and is the name to use as a method
	 * name.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * The name of the DataFetcher, in camelCase. This name is a valid for a java variable identifier, and is the name
	 * to use as a method name.
	 * 
	 * @return
	 */
	default public String getCamelCaseName() {
		return TypeUtil.getCamelCase(getName());
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
