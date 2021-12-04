package com.graphql_java_generator.client.graphqlrepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.MultiValueMap;

import com.graphql_java_generator.util.GraphqlUtils;
import com.nimbusds.oauth2.sdk.util.MapUtils;

/**
 * Implementation of {@link ImportBeanDefinitionRegistrar}, that scans the given package for {@link GraphQLRepository}
 * annotated interfaces, and register dynamic proxies as beans.
 * 
 * @see https://stackoverflow.com/questions/39507736/dynamic-proxy-bean-with-autowiring-capability
 */
@Configuration
public class GraphQLRepositoryProxyBeansRegistrar implements ImportBeanDefinitionRegistrar, BeanClassLoaderAware {

	/** Logger for this class */
	private static Logger logger = LoggerFactory.getLogger(GraphQLRepositoryProxyBeansRegistrar.class);

	private ClassPathScanner classpathScanner;
	private ClassLoader classLoader;

	GraphqlUtils graphqlUtils = GraphqlUtils.graphqlUtils;

	public GraphQLRepositoryProxyBeansRegistrar() {
		classpathScanner = new ClassPathScanner(false);
		classpathScanner.addIncludeFilter(new AnnotationTypeFilter(GraphQLRepository.class));
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		String[] basePackages = getBasePackages(importingClassMetadata);
		if (basePackages != null) {
			for (String basePackage : basePackages) {
				registerGraphQLRepositoryProxyBeanFactories(basePackage, registry);
			}
		}
	}

	private String[] getBasePackages(AnnotationMetadata importingClassMetadata) {
		MultiValueMap<String, Object> allAnnotationAttributes = importingClassMetadata
				.getAllAnnotationAttributes(EnableGraphQLRepositories.class.getName());

		if (MapUtils.isNotEmpty(allAnnotationAttributes)) {
			return (String[]) allAnnotationAttributes.getFirst("basePackages");
		} else {
			return null;
		}
	}

	private void registerGraphQLRepositoryProxyBeanFactories(String basePackage, BeanDefinitionRegistry registry) {
		try {

			for (BeanDefinition beanDefinition : classpathScanner.findCandidateComponents(basePackage)) {

				Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());

				GraphQLRepository graphQLRepository = clazz.getAnnotation(GraphQLRepository.class);

				// TODO this should be enhanced, to avoid any bean name collision in the Spring container
				String beanName = (graphQLRepository.value() == null || graphQLRepository.value().equals(""))
						? graphqlUtils.getCamelCase(clazz.getSimpleName())
						: graphQLRepository.value();

				GenericBeanDefinition proxyBeanDefinition = new GenericBeanDefinition();
				proxyBeanDefinition.setBeanClass(clazz);

				ConstructorArgumentValues args = new ConstructorArgumentValues();
				args.addGenericArgumentValue(classLoader);
				args.addGenericArgumentValue(clazz);
				proxyBeanDefinition.setConstructorArgumentValues(args);
				proxyBeanDefinition.setFactoryBeanName("graphQLRepositoryProxyBeanFactory");
				proxyBeanDefinition.setFactoryMethodName("createGraphQLRepositoryInvocationHandler");

				registry.registerBeanDefinition(beanName, proxyBeanDefinition);
			}
		} catch (Exception e) {
			logger.error(
					"Exception while creating proxy: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")");
			throw new RuntimeException(e.getMessage(), e);
		}

	}
}
