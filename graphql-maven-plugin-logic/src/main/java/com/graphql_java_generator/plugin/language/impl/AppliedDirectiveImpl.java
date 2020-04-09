/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import java.util.HashMap;
import java.util.Map;

import com.graphql_java_generator.plugin.language.AppliedDirective;
import com.graphql_java_generator.plugin.language.Directive;

import graphql.language.Value;
import lombok.Data;

/**
 * @author etienne-sf
 */
@Data
public class AppliedDirectiveImpl implements AppliedDirective {

	/** The Directive definition of this applied directive */
	private Directive directive;

	/** The map with all arguments values. It may not be null. */
	Map<String, Value<?>> argumentValues = new HashMap<>();

}
