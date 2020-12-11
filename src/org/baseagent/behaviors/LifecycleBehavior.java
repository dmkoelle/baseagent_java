package org.baseagent.behaviors;

import org.baseagent.Agent;

public abstract class LifecycleBehavior implements Behavior {
	private boolean started;
	private boolean paused;
	private boolean ended;
	
	public LifecycleBehavior() { }
	
	public void startBehavior(Agent agent) {
		this.started = true;
	}

	public void pauseBehavior(Agent agent) {
		this.paused = true;
	}
	
	public void resumeBehavior(Agent agent) {
		this.paused = false;
	}
	
	public void endBehavior(Agent agent) {
		this.ended = true;
	}
	
	public boolean isStarted() {
		return this.started;
	}

	public boolean isEnded() {
		return this.ended;
	}

	public boolean isPaused() {
		return this.paused;
	}
	
	public void restartBehavior(Agent agent) {
		this.ended = false;
		this.started = false;
	}
}
