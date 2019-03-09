/**
 * 
 */
package org.graphql.maven.plugin.samples.StarWars.server;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.generated.graphql.Character;
import com.generated.graphql.Droid;
import com.generated.graphql.Episode;
import com.generated.graphql.Human;
import com.generated.graphql.QueryType;

/**
 * The graphql maven plugin doesn't generate eveything. <B>You still have to work!</B> :-) <BR/>
 * This class is the concrete classe based on the generated query type: {@link QueryType}. The generated code handles
 * the request, logs it. But it's up to you to code the actual server job.
 * 
 * @author EtienneSF
 */
@Component
public class Query extends QueryType {

	@Override
	protected Character doHero(Episode episode) {
		List<Episode> episodes = new ArrayList<>();
		episodes.add(Episode.NEWHOPE);
		episodes.add(Episode.JEDI);

		Human h = new Human();
		h.setAppearsIn(episodes);
		h.setFriends(null);
		h.setId("An id");
		h.setName("A hero's name");
		h.setHomePlanet("His/her planet");
		return h;
	}

	@Override
	protected Human doHuman(String id) {
		List<Episode> episodes = new ArrayList<>();
		episodes.add(Episode.EMPIRE);

		Human h = new Human();
		h.setAppearsIn(episodes);
		h.setFriends(null);
		h.setId(id);
		h.setName("Another name");
		h.setHomePlanet("A planet");
		return h;
	}

	@Override
	protected Droid doDroid(String id) {
		List<Episode> episodes = new ArrayList<>();
		episodes.add(Episode.EMPIRE);

		Droid h = new Droid();
		h.setAppearsIn(episodes);
		h.setFriends(null);
		h.setId(id);
		h.setName("Another name");
		h.setPrimaryFunction("Its function");
		return h;
	}

}
