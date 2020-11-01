/**
 * 
 */
package org.allGraphQLCases.server.impl;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.allGraphQLCases.server.AllFieldCases;
import org.allGraphQLCases.server.AllFieldCasesInput;
import org.allGraphQLCases.server.Human;
import org.allGraphQLCases.server.HumanInput;
import org.allGraphQLCases.server.util.DataFetchersDelegateAnotherMutationType;
import org.springframework.stereotype.Component;

import graphql.language.Argument;
import graphql.language.Directive;
import graphql.language.StringValue;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author etienne-sf
 *
 */
@Component
public class DataFetchersDelegateAnotherMutationTypeImpl implements DataFetchersDelegateAnotherMutationType {

	@Resource
	DataGenerator generator;

	@Override
	public Human createHuman(DataFetchingEnvironment dataFetchingEnvironment, HumanInput human) {
		Human ret = generator.generateInstance(Human.class);
		ret.setName(human.getName());
		ret.setAppearsIn(human.getAppearsIn());

		// Let's look for the testDirective
		Directive testDirective = getTestDirective(
				dataFetchingEnvironment.getMergedField().getSingleField().getDirectives());

		if (testDirective != null) {
			for (Argument arg : testDirective.getArguments()) {
				String val = ((StringValue) arg.getValue()).getValue();
				ret.setName(val);
			}
		}

		return ret;
	}

	@Override
	public AllFieldCases createAllFieldCases(DataFetchingEnvironment dataFetchingEnvironment,
			AllFieldCasesInput input) {
		// TODO Auto-generated method stub
		return null;
	}

	private Directive getTestDirective(List<Directive> directives) {
		for (Directive d : directives) {
			if (d.getName().equals("testDirective"))
				return d;
		}
		return null;
	}

	/**
	 * To check the serialization and deserialization of the requests, we check the content of the given list.<BR/>
	 * Expected: a list that contains these three items:<BR/>
	 * "11111111-1111-1111-1111-111111111111"<BR/>
	 * "22222222-2222-2222-2222-222222222222"<BR/>
	 * "33333333-3333-3333-3333-333333333333"
	 * 
	 * @return Returns true, if this condition is true
	 */
	@Override
	public Boolean deleteSnacks(DataFetchingEnvironment dataFetchingEnvironment, List<UUID> id) {
		if (id != null && id.size() == 3) {
			return id.get(0).toString().equals("11111111-1111-1111-1111-111111111111")
					&& id.get(1).toString().equals("22222222-2222-2222-2222-222222222222")
					&& id.get(2).toString().equals("33333333-3333-3333-3333-333333333333");
		} else {
			// The list doesn't contain the expected size
			return false;
		}
	}
}
