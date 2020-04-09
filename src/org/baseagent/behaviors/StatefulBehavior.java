package org.baseagent.behaviors;

import org.baseagent.statemachine.State;

public abstract class StatefulBehavior implements Behavior {
	private State state;
	
	public void setState(State state) {
		this.state = state;
	}
	
	public State getState() {
		return this.state;
	}
	
	public boolean isState(State state) {
		return (state.equals(getState()));
	}
}
