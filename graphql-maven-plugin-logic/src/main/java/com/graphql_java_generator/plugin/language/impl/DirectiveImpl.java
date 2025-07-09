/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import java.util.ArrayList;
import java.util.List;

import com.graphql_java_generator.plugin.language.Description;
import com.graphql_java_generator.plugin.language.Directive;
import com.graphql_java_generator.plugin.language.DirectiveLocation;
import com.graphql_java_generator.plugin.language.Field;

import graphql.language.Comment;
import lombok.Data;

/**
 * @author etienne-sf
 *
 */
@Data
public class DirectiveImpl implements Directive {

	/** The name of the object type */
	private String name;

	/** A directive may have arguments. An argument is actually a field. */
	private List<Field> arguments = new ArrayList<>();

	/** Returns the list of location that this directive may have */
	private List<DirectiveLocation> directiveLocations = new ArrayList<>();

	/** The comments that have been found before this object, in the provided GraphQL schema */
	private List<String> comments = new ArrayList<>();

	/** The description of this directive, in the provided GraphQL schema */
	private Description description = null;

	/** Indicates whether this directive is repeatable or not */
	private boolean repeatable = false;

	/**
	 * True if this directive is a standard GraphQL directive, or if it has been defined in the GraphQL schema. Default
	 * value is false (non standard)
	 */
	private boolean standard = false;

	public void setComments(List<Comment> comments) {
		this.comments = new ArrayList<>(comments.size());
		for (Comment c : comments) {
			this.comments.add(c.getContent());
		}
	}

	@Override
	public boolean isRepeatable() {
		return repeatable;
	}
}
