package com.graphql_java_generator.client.domain.allGraphQLCases;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.graphql_java_generator.annotation.GraphQLNonScalar;

public class MyQueryTypeResponse {
	@JsonProperty("withEnum")
	@GraphQLNonScalar(graphQLTypeName = "Character", javaClass = Character.class)
	Character withEnum;

	public void setWithEnum(Character withEnum) {
		this.withEnum = withEnum;
	}

	public Character getWithEnum() {
		return withEnum;
	}

	@JsonProperty("aBreak")
	@GraphQLNonScalar(graphQLTypeName = "break", javaClass = _break.class)
	_break aBreak;

	public void setABreak(_break aBreak) {
		this.aBreak = aBreak;
	}

	public _break getABreak() {
		return aBreak;
	}

	@JsonProperty("allFieldCases")
	@GraphQLNonScalar(graphQLTypeName = "AllFieldCases", javaClass = AllFieldCases.class)
	AllFieldCases allFieldCases;

	public void setAllFieldCases(AllFieldCases allFieldCases) {
		this.allFieldCases = allFieldCases;
	}

	public AllFieldCases getAllFieldCases() {
		return allFieldCases;
	}

	@JsonProperty("withList")
	@GraphQLNonScalar(graphQLTypeName = "Character", javaClass = Character.class)
	List<Character> withList;

	public void setWithList(List<Character> withList) {
		this.withList = withList;
	}

	public List<Character> getWithList() {
		return withList;
	}

	@JsonProperty("withOneMandatoryParam")
	@GraphQLNonScalar(graphQLTypeName = "Character", javaClass = Character.class)
	Character withOneMandatoryParam;

	public void setWithOneMandatoryParam(Character withOneMandatoryParam) {
		this.withOneMandatoryParam = withOneMandatoryParam;
	}

	public Character getWithOneMandatoryParam() {
		return withOneMandatoryParam;
	}

	@JsonProperty("withOneOptionalParam")
	@GraphQLNonScalar(graphQLTypeName = "Character", javaClass = Character.class)
	Character withOneOptionalParam;

	public void setWithOneOptionalParam(Character withOneOptionalParam) {
		this.withOneOptionalParam = withOneOptionalParam;
	}

	public Character getWithOneOptionalParam() {
		return withOneOptionalParam;
	}

	@JsonProperty("withoutParameters")
	@GraphQLNonScalar(graphQLTypeName = "Character", javaClass = Character.class)
	List<Character> withoutParameters;

	public void setWithoutParameters(List<Character> withoutParameters) {
		this.withoutParameters = withoutParameters;
	}

	public List<Character> getWithoutParameters() {
		return withoutParameters;
	}
}
