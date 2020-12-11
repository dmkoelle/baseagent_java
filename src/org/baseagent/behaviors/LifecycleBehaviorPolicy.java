package org.baseagent.behaviors;

import org.baseagent.Agent;

public class LifecycleBehaviorPolicy implements Behavior {
	private LifecycleBehavior currentBehavior;
	
	public LifecycleBehaviorPolicy() {
	}
	
	public void transitionToBehavior(LifecycleBehavior newBehavior, Agent agent) {
		if (this.currentBehavior != null) this.currentBehavior.endBehavior(agent);
		this.currentBehavior = newBehavior;
		if (this.currentBehavior != null) this.currentBehavior.startBehavior(agent);
	}

	@Override
	public void executeBehavior(Agent agent) {
		this.currentBehavior.executeBehavior(agent);
	}
}
