/**
 * 
 */
package graphql.mavenplugin.language;

import graphql.mavenplugin.CodeGenerator;
import graphql.mavenplugin.DocumentParser;

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
	 * The name of the DataFetcher. This name is a valid java identifier, and is the name to use as a method name.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Retrieves the {@link Field} that this data fetcher fills. The arguments for the data fetcher are the arguments of
	 * its field.
	 * 
	 * @return
	 */
	public Field getField();

}
