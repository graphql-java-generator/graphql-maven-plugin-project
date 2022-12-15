/**
 * 
 */
package com.graphql_java_generator.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import graphql.language.Value;

/**
 * This annotation contains the information for a directive that was applied to a GraphQL item in the GraphQL schema
 * used to generate the code. It allows to retrieve at runtime the directives that were defined in the GraphQL schema.
 * 
 * @author etienne-sf
 */
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(value = GraphQLDirectives.class)
@Target({ TYPE, FIELD, METHOD, PARAMETER })
public @interface GraphQLDirective {

	/** The name of the Directive, as defined in the GraphQL schema. */
	public String name();

	/**
	 * The list of parameter names for each parameter of this directive applied to the current item. It may be null or
	 * empty.
	 * 
	 * @return
	 */
	public String[] parameterNames() default {};

	/**
	 * The list of parameter types for each parameter of this directive applied to the current item. These types much
	 * match exactly the {@link #parameterNames()} content: same parameters, in the exact same order. <br/>
	 * If {@link #parameterNames()} is null or empty, it may be null or empty.
	 * 
	 * @return
	 */

	public String[] parameterTypes() default {};

	/**
	 * The list of values types for each parameter of this directive applied to the current item, in their string
	 * representation. These values much match exactly the {@link #parameterNames()} content: same parameters, in the
	 * exact same order. <br/>
	 * If {@link #parameterNames()} is null or empty, it may be null or empty.<br/>
	 * The value is the string representation of the {@link Value} class that match this value. For instance:
	 * 
	 * <pre>
	&#64;GraphQLDirective(
		name = "@testDirective", 
		parameterNames = {"aBoolean", "aCustomScalarDate", "anID", "anArray", "anInt", "aFloat", "anObject", "anEnum", "value", "anotherValue"}, 
		parameterTypes = {"Boolean", "Date", "ID", "[String!]", "Int", "Float", "CharacterInput", "Episode", "String!", "String"}, 
		parameterValues = {
			"BooleanValue{value=true}", 
			"StringValue{value='2001-02-28'}", 
			"StringValue{value='00000000-0000-0000-0000-000000000002'}", 
			"ArrayValue{values=[StringValue{value='str1'}, StringValue{value='str2'}]}", 
			"IntValue{value=666}", 
			"FloatValue{value=666.666}", 
			"ObjectValue{objectFields=[ObjectField{name='name', value=StringValue{value='specific name'}}, ObjectField{name='appearsIn', value=ArrayValue{values=[EnumValue{name='NEWHOPE'}, EnumValue{name='EMPIRE'}]}}, ObjectField{name='type', value=StringValue{value='Human'}}]}", 
			"EnumValue{name='NEWHOPE'}", 
			"StringValue{value='on Enum'}", 
			"StringValue{value='69'}"})
	 * </pre>
	 * 
	 * @return
	 */
	public String[] parameterValues() default {};

}
