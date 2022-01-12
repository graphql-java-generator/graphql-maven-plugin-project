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
#if ($field.comments.size() > 0)
	/**
#end	
#foreach ($comment in $field.comments)
	 * $comment
#end
#if ($field.comments.size() > 0)
	 */
#end
	${field.annotation}
	${field.javaType} ${field.javaName};


#end

#foreach ($field in $object.fields)
##
####################################################################################################################################################
###########  Case 1 (standard case): the field's type is NOT a type that implements an interface defined in the GraphQL schema  ####################
####################################################################################################################################################
#if ($field.fieldJavaTypeNamesFromImplementedInterface.size() == 0)
##
#if ($field.comments.size() > 0)
	/**
#end	
#foreach ($comment in $field.comments)
	 * $comment
#end
#if ($field.comments.size() > 0)
	 */
#end
	public void set${field.pascalCaseName}(${field.javaType} ${field.javaName}) {
		this.${field.javaName} = ${field.javaName};
	}

#if ($field.comments.size() > 0)
	/**
#end	
#foreach ($comment in $field.comments)
	 * $comment
#end
#if ($field.comments.size() > 0)
	*/
#end
	public ${field.javaType} get${field.pascalCaseName}() {
		return ${field.javaName};
	}
		
#else
####################################################################################################################################################
###########  Case 2: the field's type is implements an interface defined in the GraphQL schema  ####################################################
####################################################################################################################################################
##
#foreach ($type in $field.fieldJavaTypeNamesFromImplementedInterface)
##
## The field inherited from an interface field, and the field's type has been narrowed (it's not the type defined in the interface, but a subclass or subinterface of it) 
## See IFoo and TFoo sample in the allGraphQLCases schema, used to check the #114 issue (search for 114 in allGraphQLCases.graphqls)

	/**
#foreach ($comment in $field.comments)
	 * $comment
#end
	 */
	@Override
#if ($field.javaType.startsWith("List<"))
	@SuppressWarnings("unchecked")
#end
	public void set${field.pascalCaseName}($type ${field.javaName}) {
#if ($field.javaType.startsWith("List<"))
		if (${field.javaName} instanceof List) {
#if ($field.javaType != $type)
			// ${field.javaName} is an instance of $type. Let's check that this can be copied into a ${field.javaType} 
			for (Object item : ${field.javaName}) {
				if (! (item instanceof ${field.graphQLTypeSimpleName}))
					throw new IllegalArgumentException("The given ${field.javaName} should be a list of instances of ${field.graphQLTypeSimpleName}, but at least one item is an instance of "
							+ item.getClass().getName());
			}
#end
			this.${field.javaName} = (${field.javaType}) (Object) ${field.javaName};
#else
		if (${field.javaName} instanceof ${field.javaType}) {
			this.${field.javaName} = (${field.javaType}) ${field.javaName};
#end
		} else {
			throw new IllegalArgumentException("The given ${field.javaName} should be an instance of ${field.javaType}, but is an instance of "
					+ ${field.javaName}.getClass().getName());
		}
	}
#end ##(foreach ($type in $field.fieldJavaTypeNamesFromImplementedInterface))

#if (!$field.fieldJavaTypeNamesFromImplementedInterface.contains($field.javaType))

	/** 
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
	public void set${field.pascalCaseName}${field.graphQLTypeSimpleName}(${field.javaType} ${field.javaName}) {
#else
	 * 
	 * @param
#foreach ($comment in $field.comments)
	 * $comment
#end
	 */
	public void set${field.pascalCaseName}(${field.javaType} ${field.javaName}) {
#end
		this.${field.javaName} = ${field.javaName};
	}
#end

#if ($field.fieldJavaTypeNamesFromImplementedInterface.size()>1 && $field.javaType.startsWith("List<"))
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
## So, in this case (!$field.fieldJavaTypeNamesFromImplementedInterface.size()>1 && $field.javaType.startsWith("List<")), we throw an exception:
##
${exceptionThrower.throwRuntimeException("For fields which type are a list, the GraphQL type may not be a GraphQl type that implements an interface that itself implements an interface. Only one level of inheritance is accepted, due to java syntax limitation")}
#end
##
##
##
## There is only one item in the fieldJavaTypeNamesFromImplementedInterface.
## If this field is not a list, only the normal getter is enough. It will override the getters from the implemented interface(s)
## But if this field is a list, and the field's type is not the same as in the implemented interface (for instance [TFoo2] versus [IFoo2] as in the above sample), then
## we need separate getters. In this case, we need these getters:
##
## List<IFoo> getList();  // This one overrides the getter from the implemented interface.
## List<TFoo> getListTFoo();  // This one returns the list with the good type, as defined for the current field, of the current object we're generated the code for.
##
##
#if ($field.javaType.startsWith("List<"))
#foreach ($supertype in $field.fieldJavaTypeNamesFromImplementedInterface) ##This is a Set with one item. As it is a Set, we can not do a get(0), so we iterate over it.
	/**
#foreach ($comment in $field.comments)
	 * $comment
#end
	 */
	@Override
	@SuppressWarnings("unchecked")
	public $supertype get${field.pascalCaseName}() {
		return ($supertype) (Object) ${field.javaName};
	}

#end
#end
	/**
#foreach ($comment in $field.comments)
	 * $comment
#end
	 */
#if (!$field.javaType.startsWith("List<"))
	@Override
#end
	public ${field.javaType} get${field.pascalCaseName}#if ($field.javaType.startsWith("List<"))${field.graphQLTypeSimpleName}#end() {
		return ${field.javaName};
	}
#end

#end ##end of test "if ($field.fieldJavaTypeNamesFromImplementedInterface.size() == 0)"
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
		aliasValues.put(aliasName, aliasDeserializedValue);
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
		return aliasValues.get(alias);
	}

#end
    public String toString() {
        return "${object.javaName} {"
#foreach ($field in $object.fields)
				+ "${field.javaName}: " + ${field.javaName}
#if($foreach.hasNext)
				+ ", "
#end 
#end
        		+ "}";
    }

    /**
	 * Enum of field names
	 */
	 public static enum Field implements GraphQLField {
#foreach ($field in $object.fields)
		${field.pascalCaseName}("${field.name}")#if($foreach.hasNext),
#end
#end;

		private String fieldName;

		Field(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getFieldName() {
			return fieldName;
		}

		public Class<?> getGraphQLType() {
			return this.getClass().getDeclaringClass();
		}

	}

	public static Builder builder() {
			return new Builder();
		}



	/**
	 * Builder
	 */
	public static class Builder {
#foreach ($field in $object.fields)
#if(${field.javaName} != '__typename')
		private ${field.javaType} ${field.javaName};
#end
#end


#foreach ($field in $object.fields)
#if(${field.javaName} != '__typename')
		public Builder with${field.pascalCaseName}(${field.javaType} ${field.javaName}) {
			this.${field.javaName} = ${field.javaName};
			return this;
		}
#end
#end

		public ${targetFileName} build() {
			${targetFileName} _object = new ${targetFileName}();
#foreach ($field in $object.fields)
#if(${field.javaName} == '__typename')
			_object.set__typename("${object.javaName}");
#else
#if ($field.fieldJavaTypeNamesFromImplementedInterface.size()>0 && !$field.fieldJavaTypeNamesFromImplementedInterface.contains($field.javaType) && $field.javaType.startsWith("List<"))
			_object.set${field.pascalCaseName}${field.graphQLTypeSimpleName}(${field.javaName});
#else
			_object.set${field.pascalCaseName}(${field.javaName});
#end
#end
#end
			return _object;
		}
	}
