/**
 * 
 */
package graphql.java.client.response;

/**
 * Thrown when an internal error of the GraphQL generator occurs
 * 
 * @author EtienneSF
 */
public class GraphQLInternalErrorException extends GraphQLRequestPreparationException {

	private static final long serialVersionUID = 1L;

	public GraphQLInternalErrorException(String msg) {
		super(msg);
	}

	public GraphQLInternalErrorException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
