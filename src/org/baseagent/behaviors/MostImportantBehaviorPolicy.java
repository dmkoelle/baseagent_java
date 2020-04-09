package org.baseagent.behaviors;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.baseagent.Agent;
import org.baseagent.util.Pair;


public class MostImportantBehaviorPolicy implements Behavior {
	private List<Pair<Predicate<Agent>, Behavior>> behaviors;
	
	public MostImportantBehaviorPolicy() {
		behaviors = new ArrayList<>();
	}
	
	public void addBehavior(Predicate<Agent> applies, Behavior behavior) {
		behaviors.add(new Pair<>(applies, behavior));
	}

	@Override
	public void executeBehavior(Agent agent) {
		for (Pair<Predicate<Agent>, Behavior> pair : behaviors) {
			if (pair.getFirst().test(agent)) {
				pair.getSecond().executeBehavior(agent);
				return;
			}
		}
	}
}
