/**
 *
 */
package com.graphql_java_generator.plugin.language.impl;

import java.util.ArrayList;
import java.util.List;

import com.graphql_java_generator.plugin.DocumentParser;
import com.graphql_java_generator.plugin.conf.CommonConfiguration;
import com.graphql_java_generator.plugin.conf.GenerateCodeCommonConfiguration;
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
	 * @param documentParser
	 *            The {@link DocumentParser} that has parsed the schema, and so that contains the whole schema
	 *            definition
	 */
	public EnumType(String name, CommonConfiguration configuration, DocumentParser documentParser) {
		super(name, GraphQlType.ENUM, configuration, documentParser);
	}

	/**
	 * An enum has no identifier.
	 * 
	 * @return null
	 */
	@Override
	public List<Field> getIdentifiers() {
		return new ArrayList<>();
	}

	@Override
	public String getPackageName() {
		return ((GenerateCodeCommonConfiguration) configuration).getPackageName();
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
	public boolean isEnum() {
		return true;
	}

	@Override
	public boolean isScalar() {
		return true;
	}

	@Override
	protected String getPrefix() {
		return configuration.getEnumPrefix();
	}

	@Override
	protected String getSuffix() {
		return configuration.getEnumSuffix();
	}

}
