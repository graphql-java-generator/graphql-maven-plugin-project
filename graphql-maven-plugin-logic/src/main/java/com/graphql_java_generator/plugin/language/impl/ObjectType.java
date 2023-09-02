/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import java.util.ArrayList;
import java.util.List;

import com.graphql_java_generator.plugin.DocumentParser;
import com.graphql_java_generator.plugin.conf.CommonConfiguration;
import com.graphql_java_generator.plugin.conf.GenerateCodeCommonConfiguration;
import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.Type;
import com.graphql_java_generator.util.GraphqlUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * This class describes one object type, as found in a graphql schema. It aims to be simple enough, so that the Velocity
 * template can easily generated the fields from it.<BR/>
 * An ObjectType would for instance describe:
 * 
 * <PRE>
 * type Character {
 *   name: String!
 *   appearsIn: [Episode!]!
 * }
 * </PRE>
 * 
 * @author etienne-sf
 */
@Getter
@Setter
// @Data
// @EqualsAndHashCode(callSuper = true)
public class ObjectType extends AbstractType {

	/** List of the names of GraphQL types (interface or enum or unions), that are implemented by this object */
	private List<String> implementz = new ArrayList<>();

	/** List of the names of unions, that are this object is member of */
	private List<UnionType> memberOfUnions = new ArrayList<>();

	/** The fields for this object type */
	@ToString.Exclude
	private List<Field> fields = new ArrayList<>();

	/**
	 * One of null, "query", "mutation" or "subscription". This is used for queries, mutations and subscriptions as the
	 * string to put in the JSON query toward the GraphQL server
	 */
	String requestType;

	/** Indicated whether this type is an InputObjectType or not. Default value is false */
	private boolean inputType = false;

	/**
	 * 
	 * @param name
	 *            The name of this object type
	 * @param configuration
	 *            The current plugin configuration, which is accessible through an interface that extends
	 *            {@link CommonConfiguration}
	 * @param documentParser
	 *            The {@link DocumentParser} that has parsed the schema, and so that contains the whole schema
	 *            definition
	 */
	public ObjectType(String name, CommonConfiguration configuration, DocumentParser documentParser) {
		super(name, GraphQlType.OBJECT, configuration, documentParser);
	}

	/**
	 * This constructor is especially intended for subclasses, like {@link InterfaceType}
	 * 
	 * @param name
	 *            the name for this type
	 * @param type
	 *            the kind of object
	 * @param configuration
	 *            The current plugin configuration, which is accessible through an interface that extends
	 *            {@link CommonConfiguration}
	 * @param documentParser
	 *            The {@link DocumentParser} that has parsed the schema, and so that contains the whole schema
	 *            definition
	 */
	protected ObjectType(String name, GraphQlType type, CommonConfiguration configuration,
			DocumentParser documentParser) {
		super(name, type, configuration, documentParser);
	}

	@Override
	public String getPackageName() {
		return ((GenerateCodeCommonConfiguration) this.configuration).getPackageName();
	}

	@Override
	public Field getIdentifier() {
		List<Field> identifiers = new ArrayList<>();
		for (Field f : getFields()) {
			if (f.isId()) {
				identifiers.add(f);
			}
		}

		switch (identifiers.size()) {
		case 0:
			return null;
		case 1:
			return identifiers.get(0);
		default:
			throw new RuntimeException("Only one identifier per object is expected. But " + identifiers.size() //$NON-NLS-1$
					+ " were found for " + getName()); //$NON-NLS-1$
		}
	}

	@Override
	public boolean isCustomScalar() {
		return false;
	}

	@Override
	public boolean isScalar() {
		return false;
	}

	public String getRequestTypePascalCase() {
		return GraphqlUtils.graphqlUtils.getPascalCase(getRequestType());
	}

	@Override
	protected String getPrefix() {
		return isInputType() ? getConfiguration().getInputPrefix() : getConfiguration().getTypePrefix();
	}

	@Override
	protected String getSuffix() {
		return isInputType() ? getConfiguration().getInputSuffix() : getConfiguration().getTypeSuffix();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		boolean addSeparator;

		sb.append(getClass().getSimpleName() + " {name:").append(getName()); //$NON-NLS-1$

		sb.append(", fields:{"); //$NON-NLS-1$
		addSeparator = false;
		for (Field f : getFields()) {
			if (addSeparator)
				sb.append(","); //$NON-NLS-1$
			else
				addSeparator = true;
			sb.append(f.toString());
		}
		sb.append("}"); //$NON-NLS-1$

		if (getImplementz().size() > 0) {
			sb.append(", implements "); //$NON-NLS-1$
			sb.append(String.join(",", getImplementz())); //$NON-NLS-1$
		}

		if (getComments() == null) {
			sb.append(", comments=null"); //$NON-NLS-1$
		} else if (getComments().size() > 0) {
			sb.append(", comments=empty"); //$NON-NLS-1$
		} else {
			sb.append(", comments \""); //$NON-NLS-1$
			sb.append(String.join("\\n", getComments())); //$NON-NLS-1$
			sb.append("\""); //$NON-NLS-1$
		}

		return sb.toString();
	}

	/**
	 * Returns the list of {@link Type}s that this object implements. These types may interfaces or unions.
	 * 
	 * @return
	 */
	public List<ObjectType> getImplementedTypes() {
		List<ObjectType> ret = new ArrayList<>();

		for (String typeName : getImplementz()) {
			ret.add((ObjectType) this.documentParser.getType(typeName));
		}

		return ret;
	}

}
