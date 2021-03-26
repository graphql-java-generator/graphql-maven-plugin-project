
# Questions about GraphQL request execution


## HowTo retrieve the _extensions_ GraphQL response field

This field is an optional field, described in the [GraphQL spec](https://spec.graphql.org/June2018/#sec-Response). It contains a Map, and the values for this map is free, and may be anything, as choosed by the GraphQL server implementation.

To retrieve its value, you can do, for instance:

```
@Component
class AClass {
	
	@Autowired
	MyQueryTypeExecutor myQuery;
		
	public void doSomething() {
		// Retrieve the result as a full query
		MyQueryType resp = myQuery.exec("{directiveOnQuery}"); 
		
		// You can then retrieve the whole extensions field as a map
		Map<String, JsonNode> map = resp.getExtensionsAsMap();
		
		// Or retrieve just a value, from a key. This uses Jackson to deserialize 
		// the jsonNode into the target class for this key
		YouClass value = resp.getExtensionsField("YourKey", YourClass.class);
		
		... Do something useful
	}
}
```

## Execution of a request with all parameters set in the String request

If you provide a full string, that contains all the parameters, you can do this:

```
public class MyClass {

	@Autowired
	AnotherMutationTypeExecutor mutationType;
	
	public void myMethod() {
		GraphQLRequest graphQLRequest = new GraphQLRequest(//
				"mutation {createHuman (human:  {name: \\\"a name with a string that contains a \\\\\\\", two { { and a } \\\", friends: [], appearsIn: [JEDI,NEWHOPE]} )"
						+ "@testDirective(value:?value, anotherValue:?anotherValue, "
						+ "anArray  : [  \\\"a string that contains [ [ and ] that should be ignored\\\" ,  \\\"another string\\\" ] , \r\n"
						+ "anObject:{    name: \\\"a name\\\" , appearsIn:[],friends : [{name:\\\"subname\\\",appearsIn:[],type:\\\"\\\"}],type:\\\"type\\\"})   "//
						+ "{id name appearsIn friends {id name}}}"//
		);

		// You can can execute the full query, without providing any parameter (as everything is set in the provided request
		Human human = mutationType.execWithBindValues(graphQLRequest, null).getCreateHuman();
	}
}
```
