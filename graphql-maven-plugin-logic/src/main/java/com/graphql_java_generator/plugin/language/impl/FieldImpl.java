/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.plugin.DocumentParser;
import com.graphql_java_generator.plugin.GenerateCodeDocumentParser;
import com.graphql_java_generator.plugin.language.AppliedDirective;
import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.FieldTypeAST;
import com.graphql_java_generator.plugin.language.Relation;
import com.graphql_java_generator.plugin.language.Type;

import graphql.language.Value;
import lombok.Builder;
import lombok.Data;

/**
 * @author etienne-sf
 */
@Data
@Builder(toBuilder = true)
public class FieldImpl implements Field {

	/**
	 * The {@link GenerateCodeDocumentParser} instance, which will allow to get the {@link Type} of the field, from its
	 * typeName, after the whole parsing is finished
	 */
	final DocumentParser documentParser;

	/** The name of the field */
	private String name;

	/**
	 * Returns the GraphQL type information, as it has been read from the AST. See {@link FieldTypeAST} for more
	 * information, here.
	 * 
	 * @return
	 */
	private FieldTypeAST fieldTypeAST;

	/**
	 * Is this field an identifier? <BR/>
	 * By default, a field is an identifier if its GraphQL type is "ID". But this may be overridden with the
	 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/schema_personalization.html">Schema
	 * Personalization</A>
	 */
	@Builder.Default // Allows the default value to be used with the Lombok @Builder annotation on the class
	Boolean id = null;

	/** The {@link Type} which contains this field */
	private Type owningType;

	/** All fields in an object may have parameters. A parameter is actually a field. */
	@Builder.Default // Allows the default value to be used with the Lombok @Builder annotation on the class
	private List<Field> inputParameters = new ArrayList<>();

	/**
	 * Contains the default value, as defined in the GraphQL schema. For enums, it contains the label of the enum, not
	 * the value of the enum.<BR/>
	 * We store the graphql.language.Value as we receive it. We may not have parsed the relevant Object to check its
	 * field, and obviously, we can't instanciate any object or enum yet, as we dont't even generated any code.
	 */
	@Builder.Default
	private Value<?> defaultValue = null;

	/** Contains the description of the relation that this field holds */
	@Builder.Default
	private Relation relation = null;

	/**
	 * The Java annotation to add to this type, ready to be added by the Velocity template. That is: one annotation per
	 * line, each line starting at the beginning of the line
	 */
	@Builder.Default
	private String annotation = "";

	/** All directives that have been defined in the GraphQL schema for this field */
	@Builder.Default
	private List<AppliedDirective> appliedDirectives = new ArrayList<>();

	@Override
	public Type getType() {
		return documentParser.getType(getGraphQLTypeSimpleName());
	}

	@Override
	public String getAnnotation() {
		return (this.annotation == null) ? "" : this.annotation;
	}

	@Override
	public boolean isId() {
		if (id == null) {
			return getGraphQLTypeSimpleName().equals("ID");
		} else {
			return id;
		}
	}

	/**
	 * The annotation setter should be used. Please use the {@link #addAnnotation(String))} instead
	 * 
	 * @param annotation
	 *            The annotation, that will replace the current one
	 */
	@Deprecated
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	/**
	 * The annotation setter should be added. This method allows to properly manage indentation in the generated source
	 * code
	 * 
	 * @param annotationToAdd
	 *            The annotation, that will be added to the current one
	 */
	public void addAnnotation(String annotationToAdd) {
		if (this.annotation == null || this.annotation.contentEquals("")) {
			this.annotation = annotationToAdd;
		} else {
			// We add this annotation on a next line.
			this.annotation = this.annotation + "\n\t" + annotationToAdd;
		}
	}

	/**
	 * The annotation setter should be added. This method allows to properly manage indentation in the generated source
	 * code
	 * 
	 * @param annotationToAdd
	 *            The annotation, that will be added to the current one
	 * @param replace
	 *            if true, any existing annotation is first removed
	 */
	public void addAnnotation(String annotationToAdd, boolean replace) {
		if (replace)
			this.annotation = "";

		addAnnotation(annotationToAdd);
	}

	@Override
	public String getPascalCaseName() {
		String name = getName();
		if ("Boolean".equals(name)) {
			String[] camelSplittedProperty = name.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
			if ("is".equals(camelSplittedProperty[0]) && camelSplittedProperty.length > 1) {
				name = GraphqlUtils.graphqlUtils
						.getCamelCase(StringUtils.join(ArrayUtils.remove(camelSplittedProperty, 0)));
			}
		}
		return GraphqlUtils.graphqlUtils.getPascalCase(name);
	}

	/**
	 * Returns the default value, as text, as it can be written into a generated GraphQL schema.<BR/>
	 * A <I>str</I> string default value will be returned as <I>"str"</I>,a <I>JEDI</I> enum value will be returned as
	 * <I>JEDI</I>, ...
	 * 
	 * @return
	 */
	public String getDefaultValueAsText() {
		return GraphqlUtils.graphqlUtils.getValueAsText(defaultValue);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		// Field's name
		sb.append("Field{name:");
		sb.append(getName());

		// Field's type
		sb.append(", type:");
		appendType(sb, getFieldTypeAST());

		// Field's parameters
		sb.append(", params:[");
		boolean appendSeparator = false;
		for (Field param : inputParameters) {
			if (appendSeparator)
				sb.append(",");
			else
				appendSeparator = true;
			sb.append(param.getName()).append(":");
			appendType(sb, param.getFieldTypeAST());
		} // for
		sb.append("]}");

		return sb.toString();
	}

	private void appendType(StringBuilder sb, FieldTypeAST fieldTypeAST) {
		if (fieldTypeAST.isList()) {
			sb.append("[");
			appendType(sb, fieldTypeAST.getListItemFieldTypeAST());
			sb.append("]");
		} else {
			sb.append(fieldTypeAST.getGraphQLTypeSimpleName());
		}

		if (fieldTypeAST.isMandatory())
			sb.append("!");
	}

}
