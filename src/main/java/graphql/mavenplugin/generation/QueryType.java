/**
 * 
 */
package graphql.mavenplugin.generation;

import java.util.List;

import lombok.Data;

/**
 * This class contains all the information about each query defined in the Query graphql type. It is then sent to the
 * Velocity template to generate the Query class.
 * 
 * @author EtienneSF
 */
@Data
@Deprecated
public class QueryType {

	/** Name of the query type. It's used to generate a classe with the correct name... which can be the default one */
	private String name;

	/** The queries contained in this QueryType. Queries are actually defined as standard object fields */
	private List<Field> queries;
}
