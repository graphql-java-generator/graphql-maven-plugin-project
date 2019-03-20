package graphql.java.client.domain;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import graphql.java.client.ID;

public class CharacterImpl implements Character {

	ID id;

	String name;

	@JsonDeserialize(contentAs = CharacterImpl.class)
	List<Character> friends;

	@JsonDeserialize(contentAs = Episode.class)
	List<Episode> appearsIn;

	public CharacterImpl() {

	}

	public void setId(ID id) {
		this.id = id;
	}

	public ID getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setFriends(List<Character> friends) {
		this.friends = friends;
	}

	public List<Character> getFriends() {
		return friends;
	}

	public void setAppearsIn(List<Episode> appearsIn) {
		this.appearsIn = appearsIn;
	}

	public List<Episode> getAppearsIn() {
		return appearsIn;
	}

	public String toString() {
		return "Human {" + "id: " + id + ", " + "name: " + name + ", " + "friends: " + friends + ", " + "appearsIn: "
				+ appearsIn + "}";
	}

}
