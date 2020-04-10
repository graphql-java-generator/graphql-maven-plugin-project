/**
 * 
 */
package org.allGraphQLCases.server.impl;

import java.util.List;

import javax.annotation.Resource;

import org.allGraphQLCases.server.AllFieldCases;
import org.allGraphQLCases.server.AllFieldCasesInput;
import org.allGraphQLCases.server.DataFetchersDelegateAnotherMutationType;
import org.allGraphQLCases.server.Human;
import org.allGraphQLCases.server.HumanInput;
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
}
