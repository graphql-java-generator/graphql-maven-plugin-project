/**
 * 
 */
package org.forum.server.specific_code;

import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.springframework.context.ApplicationContext;

import com.graphql_java_generator.server.util.BatchLoaderDelegateWithContext;

/**
 * This context is created for each request execution. Its main usage is to provide a per request
 * {@link DataLoaderRegistry}.
 * 
 * @author etienne-sf
 */
public class CustomExecutionContext {
	;

	final DataLoaderRegistry registry;

	public CustomExecutionContext(ApplicationContext applicationContext) {
		this.registry = new DataLoaderRegistry();
		DataLoader<Object, Object> dl;

		for (BatchLoaderDelegateWithContext<?, ?> batchLoaderDelegate : applicationContext
				.getBeansOfType(BatchLoaderDelegateWithContext.class).values()) {
			// Let's check that we didn't already register a BatchLoaderDelegate with this name
			if ((dl = registry.getDataLoader(batchLoaderDelegate.getName())) != null) {
				throw new RuntimeException(
						"Only one BatchLoaderDelegate with a given name is allows, but two have been found: "
								+ dl.getClass().getName() + " and " + batchLoaderDelegate.getClass().getName());
			}
			// Ok, let's register this new one.
			registry.register(batchLoaderDelegate.getName(), DataLoader.newDataLoader(batchLoaderDelegate));
		}
	}

	public DataLoaderRegistry getDataLoaderRegistry() {
		return registry;
	}

	public DataLoader<String, Object> getCharacterDataLoader() {
		return registry.getDataLoader("characters");
	}

}
