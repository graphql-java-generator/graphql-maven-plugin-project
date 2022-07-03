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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import graphql.language.Argument;
import graphql.language.Directive;
import graphql.language.Field;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.language.VariableReference;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author etienne-sf
 *
 */
@Component
public class DataFetchersDelegateAnotherMutationTypeImpl implements DataFetchersDelegateAnotherMutationType {

	static protected Logger logger = LoggerFactory.getLogger(DataFetchersDelegateAnotherMutationTypeImpl.class);

	static Mapper mapper = DozerBeanMapperBuilder.buildDefault();

	@Resource
	DataGenerator generator;

	@Override
	public Human createHuman(DataFetchingEnvironment dataFetchingEnvironment, HumanInput human) {
		logger.trace("createHuman: received this list of appearsIn: {}", human.getAppearsIn());

		Human ret = generator.generateInstance(Human.class);
		ret.setName(human.getName());
		ret.setAppearsIn(human.getAppearsIn());

		//////////////////////////////////////////////////////////////////////////////////////
		// The code below works only if the uppercase value is given in a GraphQL variable
		// Let's check if we should return the name in uppercase
		Field f = (Field) dataFetchingEnvironment.getMergedField().getFields().get(0).getSelectionSet().getSelections()
				.get(1);
		if (!f.getName().equals("name")) {
			throw new RuntimeException(
					"Internal Error while trying to retrieve the name field. The field retrieved is: '" + f.getName()
							+ "'");
		}
		if (f.getArguments().size() > 0) {
			Value<?> argVal = f.getArguments().get(0).getValue();
			if (argVal instanceof VariableReference) {
				String varName = ((VariableReference) argVal).getName();
				Boolean uppercase = (Boolean) dataFetchingEnvironment.getVariables().get(varName);
				if (uppercase) {
					ret.setName(human.getName().toUpperCase());
				}
			}
		}
		//////////////////////////////////////////////////////////////////////////////////////

		// Let's look for the testDirective
		Directive testDirective = getTestDirective(
				dataFetchingEnvironment.getMergedField().getSingleField().getDirectives());

		if (testDirective != null) {
			for (Argument arg : testDirective.getArguments()) {
				if (arg.getValue() instanceof StringValue) {
					String val = ((StringValue) arg.getValue()).getValue();
					ret.setName(val);
				}
			}
		}

		logger.trace("createHuman: sending back this list of appearsIn: {}", ret.getAppearsIn());

		return ret;
	}

	@Override
	public AllFieldCases createAllFieldCases(DataFetchingEnvironment dataFetchingEnvironment,
			AllFieldCasesInput input) {
		return mapper.map(input, AllFieldCases.class);
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

	@Override
	public String _if(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _if";
	}

	@Override
	public String _implements(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _implements";
	}
}
