/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import java.util.ArrayList;
import java.util.List;

import com.graphql_java_generator.plugin.CommonConfiguration;
import com.graphql_java_generator.plugin.language.EnumValue;
import com.graphql_java_generator.plugin.language.Field;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class is the image for a graphql Enum
 * 
 * @author etienne-sf
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EnumType extends AbstractType {

	/** The list of values */
	List<EnumValue> values = new ArrayList<>();

	/**
	 * 
	 * @param name
	 *            The name of this enum type
	 * @param configuration
	 *            The current plugin configuration, which is accessible through an interface that extends
	 *            {@link CommonConfiguration}
	 */
	public EnumType(String name, CommonConfiguration configuration) {
		super(name, GraphQlType.ENUM, configuration);
	}

	/**
	 * An enum has no identifier.
	 * 
	 * @return null
	 */
	@Override
	public Field getIdentifier() {
		return null;
	}

	@Override
	public String getPackageName() {
		return configuration.getPackageName();
	}

	@Override
	public boolean isInputType() {
		return false;
	}

	@Override
	public boolean isCustomScalar() {
		return false;
	}

	@Override
	public boolean isScalar() {
		return true;
	}

}
