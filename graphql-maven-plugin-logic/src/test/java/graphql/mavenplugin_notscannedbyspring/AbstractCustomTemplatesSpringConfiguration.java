package graphql.mavenplugin_notscannedbyspring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.springframework.context.annotation.Bean;

import com.graphql_java_generator.plugin.CodeTemplate;
import com.graphql_java_generator.plugin.CodeTemplateScope;
import com.graphql_java_generator.plugin.CustomScalarDefinition;
import com.graphql_java_generator.plugin.PluginConfiguration;
import com.graphql_java_generator.plugin.PluginMode;
import com.graphql_java_generator.plugin.test.helper.MavenTestHelper;
import com.graphql_java_generator.plugin.test.helper.PluginConfigurationTestHelper;

/**
 * Base Spring configuration for Custom template test
 * Extends {@link AbstractSpringConfiguration} to configure {@link PluginConfigurationTestHelper} with custom templates located in src/test/resources/templates_personalization
 * @author ggomez
 *
 */
public abstract class AbstractCustomTemplatesSpringConfiguration extends AbstractSpringConfiguration {
	

	/**
	 * Pattern for default templates
	 */
	
	
	protected static final Pattern templatePattern = Pattern.compile("templates\\/([a-zA-Z_]*)\\.vm\\.java");
	protected static List<CustomScalarDefinition> customScalars;

	static {
		customScalars = new ArrayList<>();
		customScalars.add(new CustomScalarDefinition("Date", "java.util.Date", null,
				"com.graphql_java_generator.customscalars.GraphQLScalarTypeDate.Date", null));
		customScalars
				.add(new CustomScalarDefinition("Long", "java.lang.Long", null, "graphql.Scalars.GraphQLLong", null));
		customScalars.add(new CustomScalarDefinition("else", "java.lang.String", null,
				"com.graphql_java_generator.customscalars.GraphQLScalarTypeString.String", null));
	}
	
	
	protected CodeTemplateScope codeTemplateScope;
	
	protected AbstractCustomTemplatesSpringConfiguration(String schemaFilePattern, PluginMode mode,
			List<CustomScalarDefinition> customScalars) {
		super(schemaFilePattern, mode, customScalars);
		this.codeTemplateScope = mode == PluginMode.client? CodeTemplateScope.CLIENT: CodeTemplateScope.SERVER;
	}

	/**
	 * Helper method to build customize templates map for given scope
	 * The customized templaes are located at src/test/resources/templates_personalization 
	 * Also {@link CodeTemplateScope#COMMON} tempaltes are addedd
	 * @param scope
	 * @return
	 */
	protected Map<String, String> buildTempaltes(CodeTemplateScope scope) {
		// Setup custom templates for client and common scope
		return Arrays.stream(CodeTemplate.values())
			.filter(codeTempalte -> codeTempalte.getScope() == scope || codeTempalte.getScope() == CodeTemplateScope.COMMON)
			.map(codeTemplate ->  {
				Matcher matcher = templatePattern.matcher(codeTemplate.getDefaultValue());
				if(matcher.matches()) {
					return new Pair<CodeTemplate, String>(codeTemplate, 
							String.format("templates_personalization/%s.vm.custom.java", matcher.group(1)));
				} else {
					throw new RuntimeException(
							String.format( "Template does not match expected pattenr: %s - %s", codeTemplate, codeTemplate.getDefaultValue()));
				}
				
			}
			).collect(Collectors.toMap(pair-> pair.getValue0().name(), pair -> pair.getValue1()));
	}

	/**
	 * Overrirdes {@link AbstractSpringConfiguratio#pluginConfigurationTestHelper} by configured custom templates localted in src/test/resources/templates_personalization
	 */
	@Override
	@Bean
	PluginConfiguration pluginConfigurationTestHelper(MavenTestHelper mavenTestHelper) {
		PluginConfigurationTestHelper pluginConfiguration = (PluginConfigurationTestHelper)super.pluginConfigurationTestHelper(mavenTestHelper);
		pluginConfiguration.templates = buildTempaltes(this.codeTemplateScope);
		return pluginConfiguration;		
	}	
}
