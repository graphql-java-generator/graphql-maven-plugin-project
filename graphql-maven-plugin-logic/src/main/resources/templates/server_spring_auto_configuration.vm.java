package ${configuration.springAutoConfigurationPackage};

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.execution.BatchLoaderRegistry;

/**
 * This Spring autoconfiguration class is used to declare default beans, that can then be overridden, thanks to the
 * &amp;Primary spring annotation. 
 */
@AutoConfiguration
public class ${targetFileName} {
##
#foreach ($dataFetchersDelegate in $dataFetchersDelegates)

	/**
	 * Default declaration of the spring controller for the entity <code>${dataFetcherDelegate.type.classSimpleName}</code>. This
	 * default spring can be overridden by declaring a Spring Bean of same type and name, that has the &amp;Primary spring annotation.<br/>
	 * The <code>${dataFetchersDelegate.type.packageName}.#if($configuration.separateUtilityClasses)util.#end${graphqlUtils.getJavaName($dataFetchersDelegate.type.name)}Controller</code> bean must be a valid bean that can be discovered by
	 * the <code>AnnotatedControllerConfigurer</code> spring configurer, for this configurer to work. But it must not be discovered. So it 
	 * is excluded in the {@link GraphQLServerMain} configuration.
	 */
	@Bean
	@ConditionalOnMissingBean(name = "${dataFetchersDelegate.type.camelCaseName}Controller")
	@SuppressWarnings("static-method")
	${dataFetchersDelegate.type.packageName}.#if($configuration.separateUtilityClasses)util.#end${graphqlUtils.getJavaName($dataFetchersDelegate.type.name)}Controller ${dataFetchersDelegate.type.camelCaseName}Controller(BatchLoaderRegistry registry) {
## The constructor is only used to declare the relevant data loader... if any (0 or 1 for each class)
#set ($found=false)
#foreach ($batchLoader in $batchLoaders)
#if ($dataFetchersDelegate.type.name == $batchLoader.type.name)
#set ($found=true)
		return new ${dataFetchersDelegate.type.packageName}.#if($configuration.separateUtilityClasses)util.#end${graphqlUtils.getJavaName($dataFetchersDelegate.type.name)}Controller(registry);
#end
#end
##
## Did we find a batch loader for this controller ?
#if (! $found)
		return new ${dataFetchersDelegate.type.packageName}.#if($configuration.separateUtilityClasses)util.#end${graphqlUtils.getJavaName($dataFetchersDelegate.type.name)}Controller();
#end
	}
#end
}
