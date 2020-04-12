/** This template is custom **/
package ${packageUtilName};

#if (${pluginConfiguration.separateUtilityClasses})
import ${pluginConfiguration.packageName}.${object.javaName};
#end

/**
 * This class is deprecated. Please use the #if(${pluginConfiguration.separateUtilityClasses})${pluginConfiguration.packageName}.#end${object.javaName} instead
 * 
 * @author etienne-sf
 */
@Deprecated
public class ${object.javaName}Response extends ${object.javaName} {

}
