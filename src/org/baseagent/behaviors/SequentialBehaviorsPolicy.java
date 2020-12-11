package org.baseagent.behaviors;

import org.baseagent.Agent;

public class SequentialBehaviorsPolicy implements Behavior {
	private LifecycleBehavior currentBehavior;
	private int nextBehaviorIndex;
	
	public SequentialBehaviorsPolicy() { 
		currentBehavior = null;
		nextBehaviorIndex = 0;
	}
	
	@Override
	public void executeBehavior(Agent agent) {
		if (((currentBehavior == null) || (currentBehavior.isEnded())) && (agent.getBehaviors().size() > nextBehaviorIndex)) {
			currentBehavior = (LifecycleBehavior)agent.getBehaviors().get(nextBehaviorIndex);
			nextBehaviorIndex++;
		}
		currentBehavior.executeBehavior(agent);
	}
}
