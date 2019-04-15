package graphql.mavenplugin;

import javax.annotation.Resource;

import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import graphql.mavenplugin.test.helper.MavenTestHelper;
import graphql.mavenplugin_notscannedbyspring.Forum_Server_SpringConfiguration;

@ExtendWith(SpringExtension.class)
@SpringJUnitConfig(classes = { Forum_Server_SpringConfiguration.class })
class CodeGeneratorTest_Forum {

	@Resource
	String basePackage;
	@Resource
	Log log;
	@Resource
	MavenTestHelper mavenTestHelper;

	@javax.annotation.Resource
	protected DocumentParser documentParser;
	@javax.annotation.Resource
	protected CodeGenerator codeGenerator;

	@BeforeEach
	void setUp() throws Exception {
		documentParser.parseDocuments();
	}

}
