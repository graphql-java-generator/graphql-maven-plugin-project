package org.allGraphQLCases.server.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.allGraphQLCases.server.AllFieldCases;
import org.allGraphQLCases.server.AllFieldCasesInput;
import org.allGraphQLCases.server.AnyCharacter;
import org.allGraphQLCases.server.Character;
import org.allGraphQLCases.server.CharacterConnection;
import org.allGraphQLCases.server.CharacterInput;
import org.allGraphQLCases.server.Droid;
import org.allGraphQLCases.server.DroidInput;
import org.allGraphQLCases.server.Episode;
import org.allGraphQLCases.server.Human;
import org.allGraphQLCases.server.HumanConnection;
import org.allGraphQLCases.server.HumanInput;
import org.allGraphQLCases.server.MyQueryType;
import org.allGraphQLCases.server._break;
import org.allGraphQLCases.server._extends;
import org.allGraphQLCases.server.util.DataFetchersDelegateMyQueryType;
import org.springframework.stereotype.Component;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import graphql.language.Argument;
import graphql.language.Directive;
import graphql.language.EnumValue;
import graphql.language.Field;
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
	public List<Character> withoutParameters(DataFetchingEnvironment dataFetchingEnvironment) {
		return generator.generateInstanceList(Character.class, 10);
	}

	@Override
	public Character withOneOptionalParam(DataFetchingEnvironment dataFetchingEnvironment, CharacterInput character) {
		if (character == null) {
			return generator.generateInstance(Human.class);
		} else {
			Character c = mapper.map(character, getClassFromName(Character.class, character.getType()));
			c.setId(UUID.randomUUID());

			// Let's fill in the class specific to each class, to test fragments
			if (c instanceof Droid) {
				((Droid) c).setPrimaryFunction("a primary function");
			} else if (c instanceof Human) {
				((Human) c).setHomePlanet("a home planet");
			}

			return c;
		}
	}

	@Override
	public Character withOneMandatoryParam(DataFetchingEnvironment dataFetchingEnvironment, CharacterInput character) {
		Character c = mapper.map(character, getClassFromName(Character.class, character.getType()));
		c.setId(UUID.randomUUID());
		return c;
	}

	@Override
	public Character withEnum(DataFetchingEnvironment dataFetchingEnvironment, Episode episode) {
		Character c = generator.generateInstance(Droid.class);

		// The episode list (appearsIn) will be filled by another call (the graphql manages the joins).
		// To check the given parameter, we put the episode name in the returned character's name
		c.setName(episode.name());

		return c;
	}

	@Override
	public AllFieldCases withListOfList(DataFetchingEnvironment dataFetchingEnvironment, List<List<Double>> matrix) {
		AllFieldCases ret = new AllFieldCases();
		ret.setMatrix(matrix);
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Character> withList(DataFetchingEnvironment dataFetchingEnvironment, String name,
			List<CharacterInput> characters) {
		List<Character> list = new ArrayList<Character>(characters.size());
		for (CharacterInput input : characters) {
			Class<? extends Character> characterClass;
			try {
				characterClass = (Class<? extends Character>) getClass().getClassLoader()
						.loadClass(Character.class.getPackage().getName() + "." + input.getType());
			} catch (RuntimeException | ClassNotFoundException e) {
				throw new RuntimeException(e.getClass().getSimpleName() + ": " + e.getMessage(), e);
			}
			Character c = mapper.map(input, characterClass);
			c.setId(UUID.randomUUID());

			// Let's fill in the class specific to each class, to test fragments
			if (c instanceof Droid) {
				((Droid) c).setPrimaryFunction("a primary function");
			} else if (c instanceof Human) {
				((Human) c).setHomePlanet("a home planet");
			}
			list.add(c);
		}

		list.get(0).setName(name);

		return list;
	}

	@Override
	public Character error(DataFetchingEnvironment dataFetchingEnvironment, String errorLabel) {
		// This method is here only to test the error behavior.
		throw new RuntimeException("This is an error: " + errorLabel);
	}

	@Override
	public AllFieldCases allFieldCases(DataFetchingEnvironment dataFetchingEnvironment, AllFieldCasesInput input) {
		AllFieldCases ret;
		if (input != null) {
			ret = mapper.map(input, AllFieldCases.class);
		} else {
			ret = generator.generateInstance(AllFieldCases.class);
		}
		return ret;
	}

	@Override
	public _break aBreak(DataFetchingEnvironment dataFetchingEnvironment) {
		_break ret = new _break();

		// Let's retrieve the input parameter test, that contains the expected value to return
		Field aBreak = (Field) dataFetchingEnvironment.getOperationDefinition().getSelectionSet().getSelections()
				.get(0);
		Field aCase = (Field) aBreak.getSelectionSet().getSelections().get(0);
		EnumValue enumValue = (EnumValue) aCase.getArguments().get(0).getValue();
		_extends value = _extends.valueOf(enumValue.getName());

		ret.setCase(value);
		return ret;
	}

	@SuppressWarnings("unchecked")
	private <T> Class<? extends T> getClassFromName(Class<T> t, String simpleClassname) {
		Class<? extends T> clazz;
		try {
			clazz = (Class<? extends T>) getClass().getClassLoader()
					.loadClass(t.getPackage().getName() + "." + simpleClassname);
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
	public Character directiveOnField(DataFetchingEnvironment dataFetchingEnvironment) {
		Human ret = generator.generateInstance(Human.class);
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
	public List<AnyCharacter> unionTest(DataFetchingEnvironment dataFetchingEnvironment, HumanInput human1,
			HumanInput human2, DroidInput droid1, DroidInput droid2) {
		List<AnyCharacter> ret = new ArrayList<>();

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

	private Human mapFromHumanInput(HumanInput input) {
		Human human = mapper.map(input, Human.class);
		human.setId(UUID.randomUUID());
		human.setHomePlanet("a home planet");
		return human;
	}

	private Droid mapFromDroidInput(DroidInput input) {
		Droid droid = mapper.map(input, Droid.class);
		droid.setId(UUID.randomUUID());
		droid.setPrimaryFunction("a primary function");
		return droid;
	}

	@Override
	public Integer withOneMandatoryParamDefaultValue(DataFetchingEnvironment dataFetchingEnvironment, Integer result) {
		return result;
	}

	@Override
	public Droid withTwoMandatoryParamDefaultVal(DataFetchingEnvironment dataFetchingEnvironment, DroidInput theHero,
			Integer num) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MyQueryType relay(DataFetchingEnvironment dataFetchingEnvironment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CharacterConnection connectionWithoutParameters(DataFetchingEnvironment dataFetchingEnvironment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HumanConnection connectionOnHuman(DataFetchingEnvironment dataFetchingEnvironment, String planet,
			Episode episode) {
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

}
