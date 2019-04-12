package org.graphql.maven.plugin.samples.server.graphql;

import java.util.List;

public interface Character {

	public String getId();

	public void setId(String id);

	public String getName();

	public void setName(String name);

	public List<Character> getFriends();

	public void setFriends(List<Character> friends);

	public List<Episode> getAppearsIn();

	public void setAppearsIn(List<Episode> appearsIn);

}