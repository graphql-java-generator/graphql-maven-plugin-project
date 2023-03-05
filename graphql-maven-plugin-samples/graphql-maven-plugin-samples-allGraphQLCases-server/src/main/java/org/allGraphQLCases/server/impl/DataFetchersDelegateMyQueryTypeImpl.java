package org.allGraphQLCases.server.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.allGraphQLCases.server.SEP_EnumWithReservedJavaKeywordAsValues_SES;
import org.allGraphQLCases.server.SEP_Episode_SES;
import org.allGraphQLCases.server.SEP_extends_SES;
import org.allGraphQLCases.server.SINP_AllFieldCasesInput_SINS;
import org.allGraphQLCases.server.SINP_CharacterInput_SINS;
import org.allGraphQLCases.server.SINP_DroidInput_SINS;
import org.allGraphQLCases.server.SINP_HumanInput_SINS;
import org.allGraphQLCases.server.SIP_CharacterConnection_SIS;
import org.allGraphQLCases.server.SIP_Character_SIS;
import org.allGraphQLCases.server.SIP_Client_SIS;
import org.allGraphQLCases.server.STP_AllFieldCases_STS;
import org.allGraphQLCases.server.STP_Droid_STS;
import org.allGraphQLCases.server.STP_Foo140_STS;
import org.allGraphQLCases.server.STP_HumanConnection_STS;
import org.allGraphQLCases.server.STP_Human_STS;
import org.allGraphQLCases.server.STP_MyQueryType_STS;
import org.allGraphQLCases.server.STP_ReservedJavaKeywordAllFieldCases_STS;
import org.allGraphQLCases.server.STP_break_STS;
import org.allGraphQLCases.server.SUP_AnyCharacter_SUS;
import org.allGraphQLCases.server.config.GraphQlException;
import org.allGraphQLCases.server.util.DataFetchersDelegateMyQueryType;
import org.springframework.stereotype.Component;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import graphql.language.Argument;
import graphql.language.Directive;
import graphql.language.EnumValue;
import graphql.language.Field;
import graphql.language.OperationDefinition;
import graphql.language.StringValue;
import graphql.language.VariableReference;
import graphql.schema.DataFetchingEnvironment;

/**
 * @author etienne-sf
 *
 */
@Component
public class DataFetchersDelegateMyQueryTypeImpl implements DataFetchersDelegateMyQueryType {

	@Resource
	DataGenerator generator;

	static Mapper mapper = DozerBeanMapperBuilder.buildDefault();

	@Override
	public List<SIP_Character_SIS> withoutParameters(DataFetchingEnvironment dataFetchingEnvironment) {
		return generator.generateInstanceList(SIP_Character_SIS.class, 10);
	}

	@Override
	public SIP_Character_SIS withOneOptionalParam(DataFetchingEnvironment dataFetchingEnvironment,
			SINP_CharacterInput_SINS character) {
		if (character == null) {
			return generator.generateInstance(STP_Human_STS.class);
		} else {
			SIP_Character_SIS c = mapper.map(character, getClassFromName(SIP_Character_SIS.class, character.getType()));
			c.setId(UUID.randomUUID());

			// Let's fill in the class specific to each class, to test fragments
			if (c instanceof STP_Droid_STS) {
				((STP_Droid_STS) c).setPrimaryFunction("a primary function");
			} else if (c instanceof STP_Human_STS) {
				((STP_Human_STS) c).setHomePlanet("a home planet");
			}

			return c;
		}
	}

	@Override
	public SIP_Character_SIS withOneMandatoryParam(DataFetchingEnvironment dataFetchingEnvironment,
			SINP_CharacterInput_SINS character) {
		SIP_Character_SIS c = mapper.map(character, getClassFromName(SIP_Character_SIS.class, character.getType()));
		c.setId(UUID.randomUUID());
		return c;
	}

	@Override
	public SIP_Character_SIS withEnum(DataFetchingEnvironment dataFetchingEnvironment,
			SEP_Episode_SES SEP_Episode_SES) {
		SIP_Character_SIS c = generator.generateInstance(STP_Droid_STS.class);

		// The SEP_Episode_SES list (appearsIn) will be filled by another call (the
		// graphql manages the joins).
		// To check the given parameter, we put the SEP_Episode_SES name in the returned
		// character's name
		c.setName(SEP_Episode_SES.name());

		return c;
	}

	@Override
	public STP_AllFieldCases_STS withListOfList(DataFetchingEnvironment dataFetchingEnvironment,
			List<List<Double>> matrix) {
		STP_AllFieldCases_STS ret = new STP_AllFieldCases_STS();
		ret.setMatrix(matrix);
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SIP_Character_SIS> withList(DataFetchingEnvironment dataFetchingEnvironment, String name,
			List<SINP_CharacterInput_SINS> characters) {
		List<SIP_Character_SIS> list = new ArrayList<SIP_Character_SIS>(characters.size());
		for (SINP_CharacterInput_SINS input : characters) {
			Class<? extends SIP_Character_SIS> characterClass;
			try {
				characterClass = (Class<? extends SIP_Character_SIS>) getClass().getClassLoader()
						.loadClass(SIP_Character_SIS.class.getPackage().getName() + ".STP_" + input.getType() + "_STS");
			} catch (RuntimeException | ClassNotFoundException e) {
				throw new RuntimeException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
			}
			SIP_Character_SIS c = mapper.map(input, characterClass);
			c.setId(UUID.randomUUID());

			// Let's fill in the class specific to each class, to test fragments
			if (c instanceof STP_Droid_STS) {
				((STP_Droid_STS) c).setPrimaryFunction("a primary function");
			} else if (c instanceof STP_Human_STS) {
				((STP_Human_STS) c).setHomePlanet("a home planet");
			}
			list.add(c);
		}

		list.get(0).setName(name);

		return list;
	}

	@Override
	public SIP_Character_SIS error(DataFetchingEnvironment dataFetchingEnvironment, String errorLabel) {
		// This method is here only to test the error behavior.
		throw new GraphQlException("This is an error: " + errorLabel);
	}

	@Override
	public STP_AllFieldCases_STS allFieldCases(DataFetchingEnvironment dataFetchingEnvironment,
			SINP_AllFieldCasesInput_SINS input) {
		STP_AllFieldCases_STS ret;
		if (input != null) {
			ret = mapper.map(input, STP_AllFieldCases_STS.class);
		} else {
			ret = generator.generateInstance(STP_AllFieldCases_STS.class);
		}

		// If the 'break' field is requested, we add the content of its 'if' parameter to the returned 'break' field
		// As this is a test instance, the dev below is oriented only for this test, not to manage a proper response
		// We do this only when the request is something like: {"query":"query{allFieldCases(input:&input)
		// {break(if:\"if's value\") __typename}}"}
		if (dataFetchingEnvironment.getDocument().getDefinitions() != null
				&& dataFetchingEnvironment.getDocument().getDefinitions().size() == 1) {
			OperationDefinition requestDefinition = (OperationDefinition) dataFetchingEnvironment.getDocument()
					.getDefinitions().get(0);
			if (requestDefinition.getSelectionSet().getSelections() != null
					&& requestDefinition.getSelectionSet().getSelections().size() == 1) {
				Field fieldSelection = (Field) requestDefinition.getSelectionSet().getSelections().get(0);
				if (fieldSelection.getSelectionSet().getSelections() != null
						&& fieldSelection.getSelectionSet().getSelections().size() >= 1) {
					// returnSelection: the list of expected fields in the response
					Field returnSelection = (Field) fieldSelection.getSelectionSet().getSelections().get(0);
					if (returnSelection.getName().equals("break")) {
						// Ok, the 'break' field is expected in the response. Let's add the 'if' parameter to the
						// current break (that has been initialized from the given input)
						StringValue ifValue = (StringValue) returnSelection.getArguments().get(0).getValue();
						ret.setBreak(""//
								+ ((ret.getBreak() == null) ? "" : ret.getBreak())//
								+ " (if=" //
								+ ifValue.getValue() //
								+ ")");
					}
				}
			}
		}

		return ret;
	}

	@Override
	public STP_break_STS aBreak(DataFetchingEnvironment dataFetchingEnvironment) {
		STP_break_STS ret = new STP_break_STS();

		// Let's retrieve the input parameter test, that contains the expected value to
		// return
		Field aBreak = (Field) dataFetchingEnvironment.getOperationDefinition().getSelectionSet().getSelections()
				.get(0);
		Field aCase = (Field) aBreak.getSelectionSet().getSelections().get(0);
		EnumValue enumValue = (EnumValue) aCase.getArguments().get(0).getValue();
		SEP_extends_SES value = SEP_extends_SES.valueOf(enumValue.getName());

		ret.setCase(value);
		return ret;
	}

	@SuppressWarnings("unchecked")
	private <T> Class<? extends T> getClassFromName(Class<T> t, String simpleClassname) {
		Class<? extends T> clazz;
		try {
			clazz = (Class<? extends T>) getClass().getClassLoader()
					.loadClass(t.getPackage().getName() + ".STP_" + simpleClassname + "_STS");
		} catch (RuntimeException | ClassNotFoundException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return clazz;
	}

	@Override
	public List<String> directiveOnQuery(DataFetchingEnvironment dataFetchingEnvironment, Boolean uppercase) {
		List<String> ret = new ArrayList<>();

		// Let's look for the testDirective
		Directive testDirective = getDirectiveForName("testDirective",
				dataFetchingEnvironment.getMergedField().getSingleField().getDirectives());

		if (testDirective != null) {
			for (Argument arg : testDirective.getArguments()) {
				String val;
				if (arg.getValue() instanceof StringValue) {
					val = ((StringValue) arg.getValue()).getValue();
				} else if (arg.getValue() instanceof VariableReference) {
					String varName = ((VariableReference) arg.getValue()).getName();
					val = (String) dataFetchingEnvironment.getVariables().get(varName);
				} else {
					throw new RuntimeException("Non manager value type: " + arg.getValue().getClass().getName());
				}

				if (uppercase) {
					val = val.toUpperCase();
				}
				ret.add(val);
			}
		}

		return ret;
	}

	/**
	 * Retrieves the directive of the given name, from the given list of directives
	 * 
	 * @param directiveName
	 * @param directives
	 * @return the Directive with the given name, or null if it was not found in the given list
	 */
	private Directive getDirectiveForName(String directiveName, List<Directive> directives) {
		for (Directive d : directives) {
			if (d.getName().equals(directiveName))
				return d;
		}
		return null;
	}

	@Override
	public SIP_Character_SIS directiveOnField(DataFetchingEnvironment dataFetchingEnvironment) {
		STP_Human_STS ret = generator.generateInstance(STP_Human_STS.class);
		Field field = (Field) dataFetchingEnvironment.getMergedField().getFields().get(0).getSelectionSet()
				.getSelections().get(1);

		StringBuilder sb = new StringBuilder();

		Directive testDirective = getDirectiveForName("testDirective", field.getDirectives());
		if (testDirective != null) {
			sb.append(((StringValue) testDirective.getArguments().get(0).getValue()).getValue());
		}

		Directive relationDirective = getDirectiveForName("relation", field.getDirectives());
		if (relationDirective != null) {
			sb.append(" (relation: ");
			sb.append(((StringValue) relationDirective.getArguments().get(0).getValue()).getValue());
			sb.append(", direction: ");
			sb.append(((EnumValue) relationDirective.getArguments().get(1).getValue()).getName().toString());
			sb.append(")");
		}

		ret.setName(sb.toString());
		return ret;
	}

	@Override
	public List<SUP_AnyCharacter_SUS> unionTest(DataFetchingEnvironment dataFetchingEnvironment,
			SINP_HumanInput_SINS human1, SINP_HumanInput_SINS human2, SINP_DroidInput_SINS droid1,
			SINP_DroidInput_SINS droid2) {
		List<SUP_AnyCharacter_SUS> ret = new ArrayList<>();

		if (human1 != null) {
			ret.add(mapFromHumanInput(human1));
		}
		if (droid1 != null) {
			ret.add(mapFromDroidInput(droid1));
		}
		if (human2 != null) {
			ret.add(mapFromHumanInput(human2));
		}
		if (droid2 != null) {
			ret.add(mapFromDroidInput(droid2));
		}

		return ret;
	}

	private STP_Human_STS mapFromHumanInput(SINP_HumanInput_SINS input) {
		STP_Human_STS STP_Human_STS = mapper.map(input, STP_Human_STS.class);
		STP_Human_STS.setId(UUID.randomUUID());
		STP_Human_STS.setHomePlanet("a home planet");
		return STP_Human_STS;
	}

	private STP_Droid_STS mapFromDroidInput(SINP_DroidInput_SINS input) {
		STP_Droid_STS droid = mapper.map(input, STP_Droid_STS.class);
		droid.setId(UUID.randomUUID());
		droid.setPrimaryFunction("a primary function");
		return droid;
	}

	@Override
	public Integer withOneMandatoryParamDefaultValue(DataFetchingEnvironment dataFetchingEnvironment, Integer result) {
		return result;
	}

	@Override
	public STP_Droid_STS withTwoMandatoryParamDefaultVal(DataFetchingEnvironment dataFetchingEnvironment,
			SINP_DroidInput_SINS theHero, Integer num) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public STP_MyQueryType_STS relay(DataFetchingEnvironment dataFetchingEnvironment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SIP_CharacterConnection_SIS connectionWithoutParameters(DataFetchingEnvironment dataFetchingEnvironment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public STP_HumanConnection_STS connectionOnHuman(DataFetchingEnvironment dataFetchingEnvironment, String planet,
			SEP_Episode_SES SEP_Episode_SES) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * No action. It just returns the date parameter, to check serialization and deserialization on both sides
	 */
	@Override
	public Date issue53(DataFetchingEnvironment dataFetchingEnvironment, Date date) {
		return date;
	}

	@Override
	public Double issue82Float(DataFetchingEnvironment dataFetchingEnvironment, Double aFloat) {
		return aFloat;
	}

	@Override
	public UUID issue82ID(DataFetchingEnvironment dataFetchingEnvironment, UUID id) {
		return id;
	}

	@Override
	public SIP_Client_SIS issue128(DataFetchingEnvironment dataFetchingEnvironment) {
		return null;
	}

	@Override
	public STP_Foo140_STS foo140(DataFetchingEnvironment dataFetchingEnvironment) {
		return new STP_Foo140_STS();
	}

	@Override
	public SEP_EnumWithReservedJavaKeywordAsValues_SES enumWithReservedJavaKeywordAsValues(
			DataFetchingEnvironment dataFetchingEnvironment) {
		return SEP_EnumWithReservedJavaKeywordAsValues_SES._if;
	}

	@Override
	public List<SEP_EnumWithReservedJavaKeywordAsValues_SES> listOfEnumWithReservedJavaKeywordAsValues(
			DataFetchingEnvironment dataFetchingEnvironment, SEP_EnumWithReservedJavaKeywordAsValues_SES param1,
			List<SEP_EnumWithReservedJavaKeywordAsValues_SES> param2) {
		List<SEP_EnumWithReservedJavaKeywordAsValues_SES> ret = new ArrayList<>();

		if (param1 != null)
			ret.add(param1);
		if (param2 != null)
			for (SEP_EnumWithReservedJavaKeywordAsValues_SES e : param2)
				ret.add(e);

		return ret;
	}

	@Override
	public String _if(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _if";
	}

	@Override
	public String _implements(DataFetchingEnvironment dataFetchingEnvironment, String _if) {
		return _if;
	}

	@Override
	public String _import(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _import";
	}

	@Override
	public String _instanceof(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _instanceof";
	}

	@Override
	public String _int(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _int";
	}

	@Override
	public String _interface(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _interface";
	}

	@Override
	public String _long(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _long";
	}

	@Override
	public String _native(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _native";
	}

	@Override
	public String _new(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _new";
	}

	@Override
	public String _package(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _package";
	}

	@Override
	public String _private(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _private";
	}

	@Override
	public String _protected(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _protected";
	}

	@Override
	public String _public(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _public";
	}

	@Override
	public String _return(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _return";
	}

	@Override
	public String _short(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _short";
	}

	@Override
	public String _static(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _static";
	}

	@Override
	public String _strictfp(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _strictfp";
	}

	@Override
	public String _super(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _super";
	}

	@Override
	public String _switch(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _switch";
	}

	@Override
	public String _synchronized(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _synchronized";
	}

	@Override
	public String _this(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _this";
	}

	@Override
	public String _throw(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _throw";
	}

	@Override
	public String _throws(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _throws";
	}

	@Override
	public String _transient(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _transient";
	}

	@Override
	public String _try(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _try";
	}

	@Override
	public String _void(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _void";
	}

	@Override
	public String _volatile(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _volatile";
	}

	@Override
	public String _while(DataFetchingEnvironment dataFetchingEnvironment) {
		return "a value for _while";
	}

	@Override
	public STP_ReservedJavaKeywordAllFieldCases_STS reservedJavaKeywordAllFieldCases(
			DataFetchingEnvironment dataFetchingEnvironment) {
		return generator.generateInstance(STP_ReservedJavaKeywordAllFieldCases_STS.class);
	}

	@Override
	public byte[] testBase64String(DataFetchingEnvironment dataFetchingEnvironment, byte[] input) {
		return input;
	}

	@Override
	public SEP_EnumWithReservedJavaKeywordAsValues_SES returnEnum(DataFetchingEnvironment dataFetchingEnvironment) {
		return null;
	}

	@Override
	public SEP_EnumWithReservedJavaKeywordAsValues_SES returnMandatoryEnum(
			DataFetchingEnvironment dataFetchingEnvironment) {
		return SEP_EnumWithReservedJavaKeywordAsValues_SES._assert;
	}

	@Override
	public List<SEP_EnumWithReservedJavaKeywordAsValues_SES> returnListOfEnums(
			DataFetchingEnvironment dataFetchingEnvironment) {
		return null;
	}

	@Override
	public List<List<SEP_EnumWithReservedJavaKeywordAsValues_SES>> returnListOfListOfEnums(
			DataFetchingEnvironment dataFetchingEnvironment) {
		return Arrays.asList(//
				Arrays.asList(SEP_EnumWithReservedJavaKeywordAsValues_SES._boolean, null,
						SEP_EnumWithReservedJavaKeywordAsValues_SES._break), //
				null, //
				Arrays.asList(SEP_EnumWithReservedJavaKeywordAsValues_SES._default, null,
						SEP_EnumWithReservedJavaKeywordAsValues_SES._implements));
	}

	@Override
	public List<SEP_EnumWithReservedJavaKeywordAsValues_SES> returnListOfMandatoryEnums(
			DataFetchingEnvironment dataFetchingEnvironment) {
		return null;
	}

	@Override
	public List<SEP_EnumWithReservedJavaKeywordAsValues_SES> returnMandatoryListOfEnums(
			DataFetchingEnvironment dataFetchingEnvironment) {
		return Arrays.asList(SEP_EnumWithReservedJavaKeywordAsValues_SES._boolean, null,
				SEP_EnumWithReservedJavaKeywordAsValues_SES._break);
	}

	@Override
	public List<SEP_EnumWithReservedJavaKeywordAsValues_SES> returnMandatoryListOfMandatoryEnums(
			DataFetchingEnvironment dataFetchingEnvironment) {
		return Arrays.asList(SEP_EnumWithReservedJavaKeywordAsValues_SES._byte,
				SEP_EnumWithReservedJavaKeywordAsValues_SES._case);
	}

}
