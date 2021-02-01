package graphql.spring.web.servlet;

import org.dataloader.DataLoaderRegistry;

/**
 * This interface allows to create a Spring Bean that will provide a {@link DataLoaderRegistry}, on demand. It can be
 * used by the {@link GraphQLInvocation} bean to associate a new {@link DataLoaderRegistry} for each request.
 * 
 * @author etienne-sf
 */
public interface OnDemandDataLoaderRegistry {

	/**
	 * Retrieves a new DataLoaderRegistry, that can be associated to each request.
	 * 
	 * @return
	 */
	public DataLoaderRegistry getNewDataLoaderRegistry();

}
