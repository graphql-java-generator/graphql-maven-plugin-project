/**
 * 
 */
package org.starwars.server.datafetchersdelegate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.starwars.server.Character;
import org.starwars.server.Droid;
import org.starwars.server.Episode;
import org.starwars.server.Human;
import org.starwars.server.jpa.DroidRepository;
import org.starwars.server.jpa.HumanRepository;

import jakarta.annotation.Resource;

/**
 * @author etienne-sf
 *
 */
@Component
public class CharacterHelper {

	@Resource
	DroidRepository droidRepository;
	@Resource
	HumanRepository humanRepository;

	/**
	 * Reads one Character, from its id
	 * 
	 * @param id
	 * @return
	 */
	public Character findById(UUID id) {
		Optional<Human> h = humanRepository.findById(id);
		if (h.isPresent()) {
			return h.get();
		} else {
			return droidRepository.findById(id).get();
		}
	}

	/**
	 * Retrieves the list of friends of the character which id is given. This character can be a {@link Human} or a
	 * {@link Droid}.
	 * 
	 * @param id
	 * @return
	 */
	public List<Character> friends(UUID id) {
		List<Character> ret = new ArrayList<Character>();
		ret.addAll(droidRepository.findFriends(id));
		ret.addAll(humanRepository.findFriends(id));
		return ret;
	}

	public List<Character> findAll() {
		List<Character> ret = new ArrayList<Character>();
		ret.addAll(droidRepository.findAll());
		ret.addAll(humanRepository.findAll());
		return ret;
	}

	public List<Character> findByAppearsIn(String episode) {
		List<Character> ret = new ArrayList<Character>();
		ret.addAll(droidRepository.findByAppearsIn(episode));
		ret.addAll(humanRepository.findByAppearsIn(episode));
		return ret;
	}

	public List<Episode> findAppearsInById(UUID id) {
		List<Episode> ret = new ArrayList<Episode>();

		for (String s : droidRepository.findAppearsInById(id)) {
			ret.add(Episode.valueOf(s));
		}
		for (String s : humanRepository.findAppearsInById(id)) {
			ret.add(Episode.valueOf(s));
		}

		return ret;
	}

	public List<Character> batchLoader(List<UUID> keys) {
		List<Character> ret = new ArrayList<Character>();
		ret.addAll(droidRepository.batchLoader(keys));
		ret.addAll(humanRepository.batchLoader(keys));
		return ret;
	}
}
