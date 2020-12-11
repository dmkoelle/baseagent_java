package org.baseagent.behaviors;

import org.baseagent.Agent;

public class ParallelBehaviorsPolicy implements Behavior {
	public ParallelBehaviorsPolicy() { }
	
	@Override
	public void executeBehavior(Agent agent) {
		for (Behavior behavior : agent.getBehaviors()) {
			behavior.executeBehavior(agent);
		}
	}
}
