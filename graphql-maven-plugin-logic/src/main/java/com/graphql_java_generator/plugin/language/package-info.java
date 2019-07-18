/**
 * All interfaces that contain the description of the graphql schema definition, but in a structured way. The aim of
 * these interfaces is to be a stable format to be usde in the Velocity templates, for the code generation. This has two
 * objectives:
 * <UL>
 * <LI>A structured format is necessary for any template engine, like Velocity. And the default GraphQL AST can not be
 * used in the simple scripts of template engines.</LI>
 * <LI>This format <B><U>MUST BE STABLE</U></B> (this remark is for contributors), as it is (or will be, at the time I
 * write these lines) possible that a developper specify his/her own templates, specific to his/her use case. This is
 * personalization is very important to allow the plugin in a wide range of cases, as it is impossible to have a generic
 * code generation that cover all existing use cases, data structure model, techonology...</LI>
 * </UL>
 * 
 * @author EtienneSF
 */
package com.graphql_java_generator.plugin.language;