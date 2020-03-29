/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import java.util.ArrayList;
import java.util.List;

import com.graphql_java_generator.plugin.language.Directive;
import com.graphql_java_generator.plugin.language.DirectiveLocation;
import com.graphql_java_generator.plugin.language.Field;

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
}
