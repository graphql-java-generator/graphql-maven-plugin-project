package com.graphql_java_generator.mavenplugin;

import org.apache.maven.plugins.annotations.Parameter;

import com.graphql_java_generator.plugin.conf.GeneratePojoConfiguration;
import com.graphql_java_generator.plugin.conf.PluginMode;

public abstract class AbstractGeneratePojoMojo extends AbstractGraphQLMojo implements GeneratePojoConfiguration {

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
	@Parameter(property = "com.graphql_java_generator.mavenplugin.generateJacksonAnnotations", defaultValue = GeneratePojoConfiguration.DEFAULT_GENERATE_JACKSON_ANNOTATIONS)
	public Boolean generateJacksonAnnotations;

	@Override
	public boolean isGenerateJacksonAnnotations() {
		if (generateJacksonAnnotations != null) {
			return generateJacksonAnnotations;
		} else
			return PluginMode.client.equals(getMode());
	}

	protected AbstractGeneratePojoMojo(Class<?> springConfigurationClass) {
		super(springConfigurationClass);
	}

}
