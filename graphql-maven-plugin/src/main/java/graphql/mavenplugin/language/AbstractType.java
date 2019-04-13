package graphql.mavenplugin.language;

import graphql.mavenplugin.PluginMode;
import lombok.Data;

@Data
public abstract class AbstractType implements Type {

	/** The name of the object type */
	private String name;

	/** The current generation mode */
	private PluginMode mode;

	/** The name of the package for this class */
	private String packageName;

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

	@Override
	public boolean isJPAEntity() {
		return mode.equals(PluginMode.SERVER);
	}
}
