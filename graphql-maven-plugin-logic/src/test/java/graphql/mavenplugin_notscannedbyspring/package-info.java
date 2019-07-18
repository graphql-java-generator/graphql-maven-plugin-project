/**
 * This package contains classes that should not be scanned by Spring. It contains mainly the different
 * SpringConfiguration classes, so that only one is loaded, according to the unit test to run. <BR/>
 * This package has been created, as all @Configuration in subpackages of com.graphql_java_generator.plugin where loaded. This put a
 * mess in context, and made junit test all be KO.
 * 
 * @author EtienneSF
 */
package graphql.mavenplugin_notscannedbyspring;