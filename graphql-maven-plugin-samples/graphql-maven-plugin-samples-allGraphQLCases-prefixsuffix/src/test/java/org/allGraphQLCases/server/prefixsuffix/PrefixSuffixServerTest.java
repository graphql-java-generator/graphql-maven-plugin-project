package org.allGraphQLCases.server.prefixsuffix;

import org.allGraphQLCases.server.prefixsuffix.util.*;

/**
 * Verifies that that prefixes and suffix are used correctly in server code generation.
 */
@SuppressWarnings("unused")
public class PrefixSuffixServerTest {

	// expect configured prefixes and suffixes to be used on inputs, types, interfaces, unions, and enums
	private InputPrefixDroidInputInputSuffix droidInput;
	private TypePrefixDroidTypeSuffix droidType;
	private InterfacePrefixCharacterInterfaceSuffix characterInterface;
	private UnionPrefixAnyCharacterUnionSuffix anyCharacterUnion;
	private EnumPrefixEpisodeEnumSuffix episodeEnum;

	// expect type prefix and suffix to be used on relay types
	private TypePrefixHumanConnectionTypeSuffix humanConnection;
	private TypePrefixHumanEdgeTypeSuffix humanEdge;

	// do not expect type prefix or suffix to be used on data fetchers and batch loaders
	private DataFetchersDelegateCharacter dataFetchersDelegateCharacter;
	private DataFetchersDelegateCharacterConnection dataFetchersDelegateCharacterConnection;
	private DataFetchersDelegateCharacterEdge dataFetchersDelegateCharacterEdge;
	private BatchLoaderDelegateCharacterImpl batchLoaderDelegateCharacter;

	// No test execution: the simple fact that this class compiles is enough.
}

