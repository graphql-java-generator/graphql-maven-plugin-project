/**
 * 
 */
package com.graphql_java_generator.samples.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.graphql_java_generator.samples.server.jpa.DroidRepository;
import com.graphql_java_generator.samples.server.jpa.HumanRepository;

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
