/**
 * 
 */
package graphql.mavenplugin_notscannedbyspring;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import com.graphql_java_generator.plugin.CustomScalarDefinition;
import com.graphql_java_generator.plugin.PluginMode;

/**
 * The Spring configuration used for JUnit tests
 * 
 * @author etienne-sf
 */
@Configuration
@ComponentScan(basePackages = "com.graphql_java_generator", excludeFilters = @Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateRelaySchema.*"))
public class Shopify_Server_SpringConfiguration extends AbstractSpringConfiguration {

	static List<CustomScalarDefinition> customScalars;
	static {
		customScalars = new ArrayList<>();
		customScalars.add(new CustomScalarDefinition("Date", "java.util.Date",
				"com.graphql_java_generator.customscalars.GraphQLScalarTypeDate", null, null));
		customScalars.add(new CustomScalarDefinition("DateTime", "java.util.Date",
				"com.graphql_java_generator.customscalars.GraphQLScalarTypeDateTime", null, null));
		customScalars.add(new CustomScalarDefinition("Decimal", "java.math.BigDecimal", null,
				"graphql.Scalars.GraphQLBigDecimal", null));
		customScalars.add(new CustomScalarDefinition("FormattedString", "java.lang.String",
				"com.graphql_java_generator.customscalars.GraphQLScalarTypeString", null, null));
		customScalars.add(new CustomScalarDefinition("HTML", "java.lang.String",
				"com.graphql_java_generator.customscalars.GraphQLScalarTypeString", null, null));
		customScalars.add(new CustomScalarDefinition("JSON", "java.lang.String",
				"com.graphql_java_generator.customscalars.GraphQLScalarTypeString", null, null));
		customScalars.add(
				new CustomScalarDefinition("Money", "java.lang.Float", null, "graphql.Scalars.GraphQLFloat", null));
		customScalars.add(new CustomScalarDefinition("StorefrontID", "java.lang.String",
				"com.graphql_java_generator.customscalars.GraphQLScalarTypeString", null, null));
		customScalars.add(new CustomScalarDefinition("UnsignedInt64", "java.math.BigInteger", null,
				"graphql.Scalars.GraphQLBigInteger", null));
		customScalars.add(new CustomScalarDefinition("URL", "java.lang.String",
				"com.graphql_java_generator.customscalars.GraphQLScalarTypeString", null, null));
		customScalars.add(new CustomScalarDefinition("UtcOffset", "java.lang.String",
				"com.graphql_java_generator.customscalars.GraphQLScalarTypeString", null, null));
	}

	public Shopify_Server_SpringConfiguration() {
		super("shopify.graphqls", PluginMode.server, customScalars);
	}
}
