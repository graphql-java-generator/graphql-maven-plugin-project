# Explanation

This template is a copy/paste of the 'client_query_mutation_type.vm.java' template, with this only modification:
* Add of a boolean field: thisIsADummyFieldToCheckThatThisTemplateIsUsed. It is checked in the IT test, to check that the custom templates is actually used.


# Internal note

# To update it :

* Copy/paste the `client_query_mutation_type.vm.java` template, from the plugin-logic module.
* Add the thisIsADummyFieldToCheckThatThisTemplateIsUsed just after the class declaration, like this :

```Java
${object.annotation}
@SuppressWarnings("unused")
public class ${object.classSimpleName} extends ${object.name}Executor${springBeanSuffix} #if(!${configuration.separateUtilityClasses} && ${object.requestType})implements com.graphql_java_generator.client.GraphQLRequestObject #end{

	/** 
	 * The field below is the only change from the original template. It is here only to check that 
	 * this template is actually used
	 */ 
	public boolean thisIsADummyFieldToCheckThatThisTemplateIsUsed = true;
	

```

## To document it

A good idea is to add these lines at the beginning of the file, for documentation:

```md
##
## This template is a copy/paste of the 'client_query_mutation_type.vm.java' template, with this only modification:
##  - Add of a boolean field: thisIsADummyFieldToCheckThatThisTemplateIsUsed. It is checked in the IT test, 
##    to check that the custom templates is actually used.
##
```

## To test it

The `com.graphql_java_generator.samples.basic.client.ValidateCustomQueryIT` integration test checks that the thisIsADummyFieldToCheckThatThisTemplateIsUsed is true, which implies that the field exists, and so that the custom templates has been used to generate the code.