package com.graphql_java_generator.client.graphqlrepository;

import java.lang.annotation.Annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.MultiValueMap;

import com.graphql_java_generator.util.GraphqlUtils;

/**
 * Implementation of {@link ImportBeanDefinitionRegistrar}, that scans the given package for {@link GraphQLRepository}
 * annotated interfaces, and register dynamic proxies as beans.
 * 
 * @param T
 *            Either {@link GraphQLRepository} or {@link GraphQLReactiveRepository}: the kind of GraphQL repository that
 *            this class must manage.
 * @see https://stackoverflow.com/questions/39507736/dynamic-proxy-bean-with-autowiring-capability
 */
abstract class AbstractGraphQLRepositoryProxyBeansRegistrar<T extends Annotation>
		implements ImportBeanDefinitionRegistrar, BeanClassLoaderAware {

	/** Logger for this class */
	private static Logger logger = LoggerFactory.getLogger(AbstractGraphQLRepositoryProxyBeansRegistrar.class);

	private final Class<T> annotationClass;

	private ClassPathScanner classpathScanner;
	private ClassLoader classLoader;

	GraphqlUtils graphqlUtils = GraphqlUtils.graphqlUtils;

	public AbstractGraphQLRepositoryProxyBeansRegistrar(Class<T> annotationClass) {
		this.annotationClass = annotationClass;
		classpathScanner = new ClassPathScanner(false);
		classpathScanner.addIncludeFilter(new AnnotationTypeFilter(annotationClass));
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

	private static String[] getBasePackages(AnnotationMetadata importingClassMetadata) {
		MultiValueMap<String, Object> allAnnotationAttributes = importingClassMetadata
				.getAllAnnotationAttributes(EnableGraphQLRepositories.class.getName());

		if (allAnnotationAttributes != null && allAnnotationAttributes.size() > 0) {
			// Retrieves all values of the basePackages list (in an array)
			return (String[]) allAnnotationAttributes.getFirst("basePackages"); //$NON-NLS-1$
		} else {
			return null;
		}
	}

	private void registerGraphQLRepositoryProxyBeanFactories(String basePackage, BeanDefinitionRegistry registry) {
		try {

			for (BeanDefinition beanDefinition : classpathScanner.findCandidateComponents(basePackage)) {

				Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());

				T graphQLRepository = clazz.getAnnotation(annotationClass);

				String value;
				if (graphQLRepository instanceof GraphQLRepository) {
					value = ((GraphQLRepository) graphQLRepository).value();
				} else if (graphQLRepository instanceof GraphQLReactiveRepository) {
					value = ((GraphQLReactiveRepository) graphQLRepository).value();
				} else {
					throw new IllegalArgumentException(
							"The annotationClass class should be either sddqs or GraphQLReactiveRepository, but is " //$NON-NLS-1$
									+ annotationClass.getClass().getName());
				}
				// TODO this should be enhanced, to avoid any bean name collision in the Spring container
				String beanName = (value == null || value.equals("")) //$NON-NLS-1$
						? graphqlUtils.getCamelCase(clazz.getSimpleName())
						: value;

				GenericBeanDefinition proxyBeanDefinition = new GenericBeanDefinition();
				proxyBeanDefinition.setBeanClass(clazz);

				ConstructorArgumentValues args = new ConstructorArgumentValues();
				args.addGenericArgumentValue(classLoader);
				args.addGenericArgumentValue(clazz);
				proxyBeanDefinition.setConstructorArgumentValues(args);
				proxyBeanDefinition.setFactoryBeanName("graphQLRepositoryProxyBeanFactory"); //$NON-NLS-1$
				proxyBeanDefinition.setFactoryMethodName("createGraphQLRepositoryInvocationHandler"); //$NON-NLS-1$

				registry.registerBeanDefinition(beanName, proxyBeanDefinition);
			}
		} catch (Exception e) {
			logger.error(
					"Exception while creating proxy: " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			throw new RuntimeException(e.getMessage(), e);
		}

	}
}
