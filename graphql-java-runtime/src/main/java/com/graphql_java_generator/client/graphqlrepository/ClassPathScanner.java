/**
 * 
 */
package com.graphql_java_generator.client.graphqlrepository;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;

/**
 * scan all {@link GraphQLRepositoryFactory} annotated interfaces from specified packages. Spring provides
 * {@link ClassPathScanningCandidateComponentProvider} for package scanning but it does not detect interfaces. Please
 * see <A HREF="https://stackoverflow.com/q/17477255/1307229">this question</A> and
 * <A HREF="https://stackoverflow.com/a/17521752/1307229">its answer</A> for more details. So I extended
 * {@link ClassPathScanningCandidateComponentProvider} and overrode isCandidateComponent method.
 * 
 * @see https://stackoverflow.com/questions/39507736/dynamic-proxy-bean-with-autowiring-capability
 */
public class ClassPathScanner extends ClassPathScanningCandidateComponentProvider {

	public ClassPathScanner(final boolean useDefaultFilters) {
		super(useDefaultFilters);
	}

	@Override
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		return beanDefinition.getMetadata().isIndependent();
	}

}