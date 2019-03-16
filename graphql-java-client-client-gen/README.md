# graphql-java-client-client-gen

This project is a sample of what the https://github.com/Shopify/graphql_java_gen project can generate. This tool is nice, but it id developped in Ruby. 
I fought a lot, just to generate a piece of code, because it's full Ruby, and not easy to use. It's for Ruby experts, and I'm not.

client-gen has two generation modes :
- One file. See the result in com.graphql_java_client.examples.graphql_java_client.examples.hello_world.graphql_java_gen.single
- Several files. See the result in com.graphql_java_client.examples.graphql_java_client.examples.hello_world.graphql_java_gen.multiple
(note that the save_several command doesn't work. I had to split the whole thing... :( )  

I could not find any remote call. So I guess this is only about managing the string query, and parsing the response. But, with this package,
it still is up to you to do the server call.