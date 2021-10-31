package com.graphql_java_generator.plugin.conf;

/**
 * This class contains all parameters for the <I>generatePOJO</I> goal/task.
 * 
 * @author etienne-sf
 */
public interface GeneratePojoConfiguration extends GraphQLConfiguration {

	// The String constant must be a constant expression, for use in the GraphqlMavenPlugin class.
	// So all these are String, including Boolean and Enum. Boolean are either "true" or "false"
	public final String DEFAULT_GENERATE_JACKSON_ANNOTATIONS = ""; // See isGenerateJacksonAnnotations() javadoc

	/**
	 * The mode is of no interest, here.
	 * 
	 * @return The {@link GeneratePojoConfiguration} implementation of this method always returns
	 *         {@link PluginMode#client}
	 */
	@Override
	default public PluginMode getMode() {
		return PluginMode.client;
	}

	/**
	 * <P>
	 * The <A HREF="https://github.com/FasterXML/jackson">Jackson</A> annotations are necessary to properly deserialize
	 * the json, that is incoming from the GraphQL Server. Setting this property to false allows to not generate them.
	 * </P>
	 * <P>
	 * If this property is set to true, the Jackson annotations are added in the generated GraphQL objects. The
	 * <A HREF="https://github.com/FasterXML/jackson">Jackson</A> dependencies must then be added to the target project,
	 * so that the project compiles.
	 * </P>
	 * <P>
	 * The default value is:
	 * </P>
	 * <UL>
	 * <LI><I>true</I> when in <I>client</I> mode.</LI>
	 * <LI><I>false</I> when in <I>server</I> mode.</LI>
	 * </UL>
	 */
	@Override
	public boolean isGenerateJacksonAnnotations();

	/**
	 * The utility classes are not generated for this goal/task
	 * 
	 * @return The {@link GeneratePojoConfiguration} implementation of this method always returns false
	 */
	@Override
	default public boolean isGenerateUtilityClasses() {
		return false;
	}

	/**
	 * There is no utility classes for this goal.
	 * 
	 * @return The {@link GeneratePojoConfiguration} implementation of this method always returns false
	 */
	@Override
	default public boolean isSeparateUtilityClasses() {
		return true;
	}

}
