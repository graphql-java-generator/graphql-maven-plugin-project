package com.graphql_java_generator.oauth_authorization_server;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * This class is a "almost useless" one. It has no use within the Authorization server. It's only use as a "life page",
 * so that we can check that the Authorization server is properly started, when running the integration tests.
 * 
 * @author etienne-sf
 *
 */
@Controller
public class HelloWorldController {

	@RequestMapping(value = "/helloWorld")
	@ResponseBody
	public String helloWorld() {
		return "Hello World!";
	}

}
