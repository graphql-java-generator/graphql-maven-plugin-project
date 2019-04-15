package graphql.mavenplugin.language.impl;

import graphql.mavenplugin.PluginMode;
import graphql.mavenplugin.language.Type;
import lombok.Data;

@Data
public abstract class AbstractType implements Type {

	/** The name of the object type */
	private String name;

	/** The current generation mode */
	private PluginMode mode;

	/** The name of the package for this class */
	private String packageName;

	/**
	 * Tha Java annotationto add to this type, ready to be added by the Velocity template. That is: one annotation per
	 * line, each line starting at the beginning of the line
	 */
	private String annotation;

	/** The GraphQL type for this type */
	final private GraphQlType graphQlType;

	public AbstractType(String packageName, PluginMode mode, GraphQlType graphQlType) {
		this.packageName = packageName;
		this.mode = mode;
		this.graphQlType = graphQlType;
	}

	public GraphQlType getGraphQlType() {
		return graphQlType;
	}

	/** {@inheritDoc} */
	@Override
	public String getClassSimpleName() {
		return name;
	}

	/** {@inheritDoc} */
	@Override
	public String getConcreteClassSimpleName() {
		return getClassSimpleName();
	}

	@Override
	public String getClassFullName() {
		return packageName + "." + getClassSimpleName();
	}

}
