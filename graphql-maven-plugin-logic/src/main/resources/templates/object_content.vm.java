#################################################################################################################
## Import of common.vm  (commons Velocity macro and definitions)
#################################################################################################################
#parse ("templates/common.vm")
##
##

##
## When in client mode, we add the capability to receive unknown JSON attributes, which includes returned values for GraphQL aliases
##
#if(${configuration.mode}=="client")

	/**
	 * This map contains the deserialized values for the alias, as parsed from the json response from the GraphQL
	 * server. The key is the alias name, the value is the deserialiazed value (taking into account custom scalars,
	 * lists, ...)
	 */
	@com.graphql_java_generator.annotation.GraphQLIgnore
	Map<String, Object> aliasValues = new HashMap<>();
#end
##

	public ${targetFileName}(){
		// No action
	}

#foreach ($field in $object.fields)
#foreach ($comment in $field.comments)
	// $comment
#end
#if ($field.description)
	/**
#foreach ($line in $field.description.lines)
	 * $line
#end
	 */
#end
	${field.annotation}
#appliedDirectives(${field.appliedDirectives}, "	")
	${field.javaTypeFullClassname} ${field.javaName};


#end

#foreach ($field in $object.fields)
##
####################################################################################################################################################
###########  Case 1 (standard case): the field's type is NOT a type that implements an interface defined in the GraphQL schema  ####################
####################################################################################################################################################
#if ($field.fieldJavaFullClassnamesFromImplementedInterface.size() == 0)
##
#foreach ($comment in $field.comments)
	// $comment
#end
#if ($field.description)
	/**
#foreach ($line in $field.description.lines)
	  * $line
#end
 	 */
#end
#appliedDirectives(${field.appliedDirectives}, "	")
#if ($configuration.isGenerateJacksonAnnotations())
	@JsonProperty("${field.name}")
#end
	public void set${field.pascalCaseName}(${field.javaTypeFullClassname} ${field.javaName}) {
		this.${field.javaName} = ${field.javaName};
	}

#foreach ($comment in $field.comments)
	// $comment
#end
#if ($field.description)
	/**
#foreach ($line in $field.description.lines)
	 * $line
#end
	 */
#end
#appliedDirectives(${field.appliedDirectives}, "	")
#if ($configuration.isGenerateJacksonAnnotations())
	@JsonProperty("${field.name}")
#end
	public ${field.javaTypeFullClassname} get${field.pascalCaseName}() {
		return this.${field.javaName};
	}
		
#else
####################################################################################################################################################
###########  Case 2: the field's type implements an interface defined in the GraphQL schema  #######################################################
####################################################################################################################################################
##
#foreach ($type in $field.fieldJavaFullClassnamesFromImplementedInterface)
##
## The field inherited from an interface field, and the field's type has been narrowed (it's not the type defined in the interface, but a subclass or subinterface of it) 
## See IFoo and TFoo sample in the allGraphQLCases schema, used to check the #114 issue (search for 114 in allGraphQLCases.graphqls)

#foreach ($comment in $field.comments)
	// $comment
#end
#if ($field.description)
	/**
#foreach ($line in $field.description.lines)
	  * $line
#end
	  */
#end
	@Override
#if ($configuration.isGenerateJacksonAnnotations())
	@JsonIgnore
#end
#appliedDirectives(${field.appliedDirectives}, "	")
################
## Standard case: the given type is the same as the field's type. It's useless to control anything at runtime.
#if ($type==${field.javaTypeFullClassname})
	public void set${field.pascalCaseName}($type ${field.javaName}) {
		this.${field.javaName} = ${field.javaName};
	}
################
## Complex case: the given type is NOT the same as the field's type. Let's check at runtime that everything is ok
## (see issue #15 in the Gradle project for a sample of this)
#else
#if ($field.javaType.startsWith("List<"))
	@SuppressWarnings("unchecked")
#end
	public void set${field.pascalCaseName}($type ${field.javaName}) {
#if ($field.javaType.startsWith("List<"))
		if (${field.javaName} == null || ${field.javaName} instanceof List) {
#if ($field.javaTypeFullClassname != $type)
			// ${field.javaName} is an instance of $type. Let's check that this can be copied into a ${field.javaType} 
			for (Object item : ${field.javaName}) {
				if (! (item instanceof ${field.type.classFullName}))
					throw new IllegalArgumentException("The given ${field.javaName} should be a list of instances of ${field.type.classFullName}, but at least one item is an instance of "  //$NON-NLS-1$
							+ item.getClass().getName());
			}
#end
			this.${field.javaName} = (${field.javaTypeFullClassname}) (Object) ${field.javaName};
#else
		if (${field.javaName} == null || ${field.javaName} instanceof ${field.javaTypeFullClassname}) {
			this.${field.javaName} = (${field.javaTypeFullClassname}) ${field.javaName};
#end
		} else {
			throw new IllegalArgumentException("The given ${field.javaName} should be an instance of ${field.javaTypeFullClassname}, but is an instance of " //$NON-NLS-1$
					+ ${field.javaName}.getClass().getName());
		}
	}
################
#end ##if ($type==${field.javaTypeFullClassname})
#end ##(foreach ($type in $field.fieldJavaFullClassnamesFromImplementedInterface))

#if (!$field.fieldJavaFullClassnamesFromImplementedInterface.contains($field.javaTypeFullClassname))

#foreach ($comment in $field.comments)
	// $comment
#end
	/** 
#foreach ($line in $field.description.lines)
	  * $line
#end
	 * <br/>
	 * As the type declared in the class is not inherited from one of the implemented interfaces, we need a dedicated setter.
#if ($field.javaType.startsWith("List<"))
	 * <br/>
	 * As the GraphQL type of this field is a list of items that are not of the same type as the field defined in the implemented interface, 
	 * we need to have a dedicated setter with a specific name. This is due to Java that does type erasure on parameterized types (for 
	 * compatibility reasons with older java versions). As Java can't detect at runtime the type of the items of the list, it can't 
	 * decide which setter to call. To overcome this issue, this setter has a dedicated name.
	 * 
	 * @param
#foreach ($comment in $field.comments)
	 * $comment
#end
	 */
#if ($configuration.isGenerateJacksonAnnotations())
	@JsonIgnore
#end
#appliedDirectives(${field.appliedDirectives}, "	")
	public void set${field.pascalCaseName}${field.graphQLTypeSimpleName}(${field.javaTypeFullClassname} ${field.javaName}) {
#else
	 * 
	 * @param
#foreach ($comment in $field.comments)
	 * $comment
#end
	 */
#if ($configuration.isGenerateJacksonAnnotations())
	@JsonIgnore
#end
#appliedDirectives(${field.appliedDirectives}, "	")
	public void set${field.pascalCaseName}(${field.javaTypeFullClassname} ${field.javaName}) {
#end
		this.${field.javaName} = ${field.javaName};
	}
#end

#if ($field.fieldJavaFullClassnamesFromImplementedInterface.size()>1 && $field.javaType.startsWith("List<"))
##
## We are in the complex case: the type is a list. And because of java's type erasure, we need to have different methods, for each possible return type.
## So we need to have more than one getter for this field. And these getters must have different name.
## Please note that this works for one level inheritance, for instance like in the allGraphQLCases test case, with TList implementing IList.
## For multiple levels like the one below, there is no java solution:
## 
## interface IFoo1 { 
##   ...
## }
## 
## interface IFoo2 implements IFoo1 { 
##   ...
## }
## 
## type TFoo2 implements IFoo2 { 
##   ...
## }
## 
## interface IList1 {
##   list: [IFoo1]
## }
## 
## interface IList2 implements IList1 {
##   list: [IFoo2]
## }
## 
## type TList implements IList {
## 	list: [TFoo2]
## }
## 
## For this to work, the java interface IList2 must have these two methods (which is not possible) :
## List<IFoo2> getList();
## List<IFoo1> getList();
##
## So, in this case (!$field.fieldJavaFullClassnamesFromImplementedInterface.size()>1 && $field.javaType.startsWith("List<")), we throw an exception:
##
${exceptionThrower.throwRuntimeException("For fields which type are a list, the GraphQL type may not be a GraphQl type that implements an interface that itself implements an interface. Only one level of inheritance is accepted, due to java syntax limitation. Sample: TList implements IList2 that itself implements IList1. TList contains an attribute 'list' that is a list of TFoo, where TFoo implements IFoo1 that itself implements IFoo2. It comes from IList2.list (list of IFoo2), which itself comes from IList1 (list of IFoo1). In this case, TList must implement these two methods: 'List<IFoo2> getList()' and ' List<IFoo1> getList()', which is not possible.")}
#end
##
##
##
## There is only one item in the fieldJavaFullClassnamesFromImplementedInterface.
## If this field is not a list, only the normal getter is enough. It will override the getters from the implemented interface(s)
## But if this field is a list, and the field's type is not the same as in the implemented interface (for instance [TFoo2] versus [IFoo2] as in the above sample), then
## we need separate getters. In this case, we need these getters:
##
## List<IFoo> getList();  // This one overrides the getter from the implemented interface.
## List<TFoo> getListTFoo();  // This one returns the list with the good type, as defined for the current field, of the current object we're generated the code for.
##
##
#if ($field.javaType.startsWith("List<"))
#foreach ($supertype in $field.fieldJavaFullClassnamesFromImplementedInterface) ##This is a Set with one item. As it is a Set, we can not do a get(0), so we iterate over it.
	/**
#foreach ($comment in $field.comments)
	 * $comment
#end
	 */
	@Override
#appliedDirectives(${field.appliedDirectives}, "	")
	@SuppressWarnings("unchecked")
	public $supertype get${field.pascalCaseName}() {
		return ($supertype) (Object) this.${field.javaName};
	}

#end
#end
#foreach ($comment in $field.comments)
	// $comment
#end
#if ($field.description)
	/**
#foreach ($line in $field.description.lines)
	  * $line
#end
	  */
#end
#if (!$field.javaType.startsWith("List<"))
	@Override
#end
#if ($configuration.isGenerateJacksonAnnotations())
	@JsonIgnore
#end
#appliedDirectives(${field.appliedDirectives}, "	")
	public ${field.javaTypeFullClassname} get${field.pascalCaseName}#if($field.javaType.startsWith("List<"))${field.graphQLTypeSimpleName}#end() {
		return this.${field.javaName};
	}
#end

#end ##end of test "if ($field.fieldJavaFullClassnamesFromImplementedInterface.size() == 0)"
####################################################################################################################################################
####################################################################################################################################################
##
## When in client mode, we add the capability to receive unknown JSON attributes, which includes returned values for GraphQL aliases
##
#if(${configuration.mode}=="client")

	/**
	 * This method is called during the json deserialization process, by the {@link GraphQLObjectMapper}, each time an
	 * alias value is read from the json.
	 * 
	 * @param aliasName
	 * @param aliasDeserializedValue
	 */
	public void setAliasValue(String aliasName, Object aliasDeserializedValue) {
		this.aliasValues.put(aliasName, aliasDeserializedValue);
	}

	/**
	 * Retrieves the value for the given alias, as it has been received for this object in the GraphQL response. <BR/>
	 * This method <B>should not be used for Custom Scalars</B>, as the parser doesn't know if this alias is a custom
	 * scalar, and which custom scalar to use at deserialization time. In most case, a value will then be provided by
	 * this method with a basis json deserialization, but this value won't be the proper custom scalar value.
	 * 
	 * @param alias
	 * @return
	 */
	public Object getAliasValue(String alias) {
		return this.aliasValues.get(alias);
	}

#end
    public String toString() {
        return "${object.javaName} {" //$NON-NLS-1$
#foreach ($field in $object.fields)
				+ "${field.javaName}: " + this.${field.javaName} //$NON-NLS-1$
#if($foreach.hasNext)
				+ ", " //$NON-NLS-1$
#end 
#end
        		+ "}"; //$NON-NLS-1$
    }

## Issue 130: if the GraphQL type's name is Builder, the inner static class may not be named Builder. So we prefix it with '_'
	public static#if($targetFileName=="Builder") _Builder#else Builder#end builder() {
		return new#if($targetFileName=="Builder") _Builder#else Builder#end();
	}

	/**
	 * The Builder that helps building instance of this POJO. You can get an instance of this class, by calling the
	 * {@link #builder()}
#if($targetFileName=="Builder")
	 * <br/>As this GraphQL type's name is Builder, the inner Builder class is renamed to _Builder, to avoid name 
	 * collision during Java compilation.
#end 
	 */
	public static class#if($targetFileName=="Builder") _Builder#else Builder#end {
#foreach ($field in $object.fields)
#if(${field.javaName} != '__typename')
		private ${field.javaTypeFullClassname} ${field.javaName};
#end
#end

#foreach ($field in $object.fields)
#if(${field.javaName} != '__typename')
#foreach ($comment in $field.comments)
	// $comment
#end
#if ($field.description)
	/**
#foreach ($line in $field.description.lines)
	  * $line
#end
	  */
#end
		public#if($targetFileName=="Builder") _Builder#else Builder#end with${field.pascalCaseName}(${field.javaTypeFullClassname} ${field.javaName}Param) {
			this.${field.javaName} = ${field.javaName}Param;
			return this;
		}
#end

#end

		public ${targetFileName} build() {
			${targetFileName} _object = new ${targetFileName}();
#foreach ($field in $object.fields)
#if(${field.javaName} == '__typename')
			_object.set__typename("${object.javaName}"); //$NON-NLS-1$
#else
#if ($field.fieldJavaFullClassnamesFromImplementedInterface.size()>0 && !$field.fieldJavaFullClassnamesFromImplementedInterface.contains($field.javaTypeFullClassname) && $field.javaType.startsWith("List<"))
			_object.set${field.pascalCaseName}${field.graphQLTypeSimpleName}(this.${field.javaName});
#else
			_object.set${field.pascalCaseName}(this.${field.javaName});
#end
#end
#end
			return _object;
		}
	}
