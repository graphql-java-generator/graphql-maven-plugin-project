/**
 * 
 */
package graphql.java.client.request;

/**
 * @author EtienneSF
 */
public interface ResponseDefinition {

	/**
	 * Add a scalar field to the response for the current graphql type.
	 * 
	 * @param fieldName
	 *            The field name to add, as defined in the graphql schema
	 */
	public void addResponseField(String fieldName);

	/**
	 * Add a scalar field to the response for the current graphql type.
	 * 
	 * @param fieldName
	 *            The field name to add, as defined in the graphql schema
	 * @param alias
	 *            The alias for this field
	 */
	public void addResponseFieldWithAlias(String fieldName, String alias);

	/**
	 * Add a field (which is itself a graphql type) to the response for the current graphql type.
	 * 
	 * @param fieldName
	 *            The field name to add, as defined in the graphql schema
	 */
	public ResponseDefinition addResponseEntity(String fieldName);

	/**
	 * Add a field (which is itself a graphql type) to the response for the current graphql type.
	 * 
	 * @param fieldName
	 *            The field name to add, as defined in the graphql schema
	 * @param alias
	 *            The alias for this field
	 */
	public ResponseDefinition addResponseEntityWithAlias(String fieldName, String alias);

	/**
	 * Retrieves the part of the query, which describes the fields that the graphql server should return.<BR/>
	 * For instance, for the query: <I>{hero(episode: NEWHOPE) {id name}}</I>, the response definition is <I>{id
	 * name}</I>
	 * 
	 * @param sb
	 *            The {@link StringBuilder} where the response must be appended
	 * 
	 * @return
	 */
	public void appendResponseQuery(StringBuilder sb);

}
