/**
 * 
 */
package graphql.mavenplugin.language;

import java.util.List;

import graphql.mavenplugin.CodeGenerator;
import graphql.mavenplugin.DocumentParser;
import graphql.mavenplugin.language.impl.TypeUtil;

/**
 * This class represents a GraphQL Data Fetcher Delegate. When in server mode, the GraphQL maven plugin generates on
 * data fetcher delegate for each objet whose fields need at least one data fetcher. This helps to limit the impact on
 * the specific code, when the GraphQL schema changes. Teir characteristics are read by {@link DocumentParser}, and used
 * by {@link CodeGenerator} and the Velocity templates to generate the code of the DataFechers, and their declaration in
 * the GraphQLProvider.<BR/>
 * The arguments for the data fetcher delegates are the arguments of its field.
 * 
 * @author EtienneSF
 */
public interface DataFetcherDelegate {

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
	 * Retrieves the {@link DataFetcher} that are to be implemented by this Data Fetcher Delegate.
	 * 
	 * @return
	 */
	public List<DataFetcher> getDataFetchers();

}
