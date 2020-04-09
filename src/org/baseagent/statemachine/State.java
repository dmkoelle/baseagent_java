package org.baseagent.statemachine;

import java.util.function.Consumer;

import org.baseagent.Agent;
import org.baseagent.behaviors.Behavior;

public class State implements Behavior {
	private String stateName;
	private Consumer<Agent> thingToDoWhileInState;
	
	public State(String stateName) {
		this.stateName = stateName;
	}
	
	public State(String stateName, Consumer<Agent> thingToDoWhileInState) {
		this(stateName);
		this.thingToDoWhileInState = thingToDoWhileInState;
	}

	public String getStateName() {
		return this.stateName;
	}
	
	@Override
	public void executeBehavior(Agent agent) {
		if (thingToDoWhileInState != null) {
			thingToDoWhileInState.accept(agent);
		}
	}
}
