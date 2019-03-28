package org.graphql.maven.plugin.samples.server.generated;

import java.util.List;

public interface Character {

	public String getId();

	public void setId(String id);

	public String getName();

	public void setName(String name);

	public CharacterImpl getBestFriend();

	public void setBestFriend(CharacterImpl bestFriend);

	public List<CharacterImpl> getFriends();

	public void setFriends(List<CharacterImpl> friends);

	public Episode getFirstEpisode();

	public void setFirstEpisode(Episode firstEpisode);

	public List<Episode> getAppearsIn();

	public void setAppearsIn(List<Episode> appearsIn);

}