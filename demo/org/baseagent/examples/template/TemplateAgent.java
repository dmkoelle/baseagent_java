package org.baseagent.examples.template;

import org.baseagent.Agent;
import org.baseagent.behaviors.grid.RandomWanderBehavior;

public class TemplateAgent extends Agent {
	public TemplateAgent(String layerName) {
		super(layerName);
		addBehavior(new RandomWanderBehavior(10));
	}
}
