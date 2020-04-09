package org.baseagent.behaviors;

import org.baseagent.Agent;

public class ExecuteAllBehaviorsPolicy implements Behavior {
	public ExecuteAllBehaviorsPolicy() { }
	
	@Override
	public void executeBehavior(Agent agent) {
		for (Behavior behavior : agent.getBehaviors()) {
			behavior.executeBehavior(agent);
		}
	}
}
