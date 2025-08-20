/**
 * 
 */
package org.allGraphQLCases.server.oauth2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This URI is used in maven build, to check that the server is properly started.
 * 
 * @author etienne-sf
 */
@RestController
public class HelloWorld {

	@GetMapping("/helloworld.html")
	public String get() {
		return "Hello World, I'm started!";
	}

}
