##
## Velocity template for the BatchLoaderDelegateXxxxImpl classes, where Xxxx successively every object in the GraphQL schema
## which has an ID.
##
## This template has these input:
## package
##
/** This template is custom **/
package ${pluginConfiguration.packageName};

import org.dataloader.BatchLoader;
import org.dataloader.DataLoaderRegistry;
import org.springframework.stereotype.Component;

#foreach($import in $imports)
import $import;
#end

/**
 * BatchLoaderDelegate is the interface that identifies Spring Beans that help using
 * <A HREF="https://github.com/graphql-java/java-dataloader">graphql-java java-dataloader</A>. All the
 * BatchLoaderDelegates are stored in a {@link DataLoaderRegistry}. It's actually a map, in which the key is the name
 * for this BatchLoadeDelegate, as returned by {@link #getName()}. <BR/>
 * All BatchLoaderDelegates must be defined as Spring Bean, that is: they must be marked by the {@link Component}
 * annotation. They are discovered by the GraphQLProvider.dataLoaderRegistry() method.<BR/>
 * It is not allowed to have two BatchLoaderDelegates with the same name.<BR/>
 * graphql-java-generator will generate one BatchLoaderDelegate implementation for each object defined in the GrapQL
 * schema, which has an ID as a field.<BR/>
 * You can register your own BatchDataLoader, by just creating a class which implements BatchLoaderDelegate, and mark it
 * as a Spring Beean with the {@link Component} annotation. You just have to check that its name is unique.
 * 
 * @author EtienneSF
 *
 */
public interface BatchLoaderDelegate<K, V> extends BatchLoader<K, V> {

	/**
	 * The name of this BatchLoaderDelegate, as can be retrieved from the {@link DataLoaderRegistry}.
	 * 
	 * @return The (must be) unique name of this BatchLoaderDelegate
	 */
	public String getName();

}
