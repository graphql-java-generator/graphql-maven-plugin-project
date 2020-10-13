/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import com.graphql_java_generator.plugin.CommonConfiguration;
import com.graphql_java_generator.plugin.GraphQLConfiguration;
import com.graphql_java_generator.plugin.language.Field;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author etienne-sf
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScalarType extends AbstractType {

	/** The package of the java class that will hold this value */
	final String packageName;
	/** The simple name (without the package) of the java class that will hold this value */
	final String classSimpleName;

	/**
	 * 
	 * @param name
	 *            The name of the GraphQL type
	 * @param packageName
	 *            The package of the java class that will hold this value
	 * @param classSimpleName
	 *            The simple name (without the package) of the java class that will hold this value
	 * @param configuration
	 *            The current {@link GraphQLConfiguration}
	 */
	public ScalarType(String name, String packageName, String classSimpleName, CommonConfiguration configuration) {
		super(name, GraphQlType.SCALAR, configuration);
		this.packageName = packageName;
		this.classSimpleName = classSimpleName;
	}

	/**
	 * A scalar has no identifier.
	 * 
	 * @return null
	 */
	@Override
	public Field getIdentifier() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isInputType() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isCustomScalar() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isScalar() {
		return true;
	}

}
