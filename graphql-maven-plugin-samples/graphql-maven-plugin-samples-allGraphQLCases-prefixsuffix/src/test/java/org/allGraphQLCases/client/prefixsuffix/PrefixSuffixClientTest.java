package org.allGraphQLCases.client.prefixsuffix;

import org.allGraphQLCases.client.prefixsuffix.util.MyQueryTypeExecutor;
import org.allGraphQLCases.client.prefixsuffix.util.MyQueryTypeResponse;
import org.allGraphQLCases.client.prefixsuffix.util.MyQueryTypeRootResponse;

/**
 * Verifies that that prefixes and suffix are used correctly in client code generation.
 */
@SuppressWarnings("unused")
public class PrefixSuffixClientTest {

	// expect configured prefixes and suffixes to be used on inputs, types, interfaces, unions, and enums
	private InputPrefixDroidInputInputSuffix droidInput;
	private TypePrefixDroidTypeSuffix droidType;
	private InterfacePrefixCharacterInterfaceSuffix characterInterface;
	private UnionPrefixAnyCharacterUnionSuffix anyCharacterUnion;
	private EnumPrefixEpisodeEnumSuffix episodeEnum;

	// do not expect type prefix or suffix on classes that already have a suffix
	private MyQueryTypeExecutor myQueryTypeExecutor;
	private MyQueryTypeResponse myQueryTypeResponse;
	private MyQueryTypeRootResponse myQueryTypeRootResponse;

	// No test execution: the simple fact that this class compiles is enough.
}

