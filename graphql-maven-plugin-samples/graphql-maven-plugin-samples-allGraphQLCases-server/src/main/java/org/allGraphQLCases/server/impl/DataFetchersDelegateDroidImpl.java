/**
 * 
 */
package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;

import org.allGraphQLCases.server.DataFetchersDelegateDroid;
import org.allGraphQLCases.server.SEP_Episode_SES;
import org.allGraphQLCases.server.SIP_Character_SIS;
import org.allGraphQLCases.server.STP_Droid_STS;
import org.dataloader.BatchLoaderEnvironment;
import org.springframework.stereotype.Component;

import graphql.execution.MergedField;
import graphql.language.Directive;
import graphql.language.StringValue;
import graphql.schema.DataFetchingEnvironment;
import jakarta.annotation.Resource;

/**
 * @author etienne-sf
 *
 */
@Component
public class DataFetchersDelegateDroidImpl implements DataFetchersDelegateDroid {

	@Resource
	DataGenerator generator;

	@Override
	public List<SIP_Character_SIS> friends(DataFetchingEnvironment dataFetchingEnvironment, STP_Droid_STS source) {
		List<SIP_Character_SIS> chars = generator.generateInstanceList(SIP_Character_SIS.class, 5);

		// For the OverriddenControllerIT.checkThatTheCharacterControllerIsOverridden() integration test, we check if
		// the testDirective directive has been set with the relevant value
		MergedField field = dataFetchingEnvironment.getExecutionStepInfo().getField();
		List<Directive> directives = field.getSingleField().getDirectives();
		if (directives.size() == 1 && directives.get(0).getName().equals("testDirective")) {
			// Let's check the @testDirective arguments
			Directive dir = directives.get(0);
			StringValue value = (StringValue) dir.getArguments().get(0).getValue();
			String s = value.getValue();
			if (dir.getArguments().size() == 1 && dir.getArguments().get(0).getName().equals("value")
					&& ((StringValue) dir.getArguments().get(0).getValue()).getValue()
							.equals("checkThatTheCharacterControllerIsOverridden")) {
				chars.stream()
						.forEach(c -> c.setName(c.getName() + " overriden by DataFetchersDelegateDroidImpl.friends()"));
			}
		}
		return chars;
	}

	@Override
	public List<SEP_Episode_SES> appearsIn(DataFetchingEnvironment dataFetchingEnvironment, STP_Droid_STS source) {
		return generator.generateInstanceList(SEP_Episode_SES.class, 2);
	}

	@Override
	public List<STP_Droid_STS> batchLoader(List<UUID> keys, BatchLoaderEnvironment environment) {
		return generator.generateInstanceList(STP_Droid_STS.class, keys.size());
	}

	/** Custom field data fetchers are available since release 2.5 */
	@Override
	public String name(DataFetchingEnvironment dataFetchingEnvironment, STP_Droid_STS origin, Boolean uppercase) {
		return ((uppercase != null && origin.getName() != null && uppercase) ? origin.getName().toUpperCase()
				: origin.getName());
	}

}
