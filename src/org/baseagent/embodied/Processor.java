package org.baseagent.embodied;

import java.util.ArrayList;
import java.util.List;

import org.baseagent.Agent;

// TODO: Is a Processor really just an Embodied Behavior? Should a Behavior be a ConnectedComponent? Or should a Processor implement Behavior?
public class Processor<T, U> extends ConnectedComponent {
	public Processor() {
		super();
	}
	
	public void process(EmbodiedAgent agent) {
		this.step(agent.getSimulation());
	}
}