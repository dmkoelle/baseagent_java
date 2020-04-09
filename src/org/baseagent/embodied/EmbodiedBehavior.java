package org.baseagent.embodied;

import org.baseagent.Agent;
import org.baseagent.behaviors.Behavior;
import org.baseagent.embodied.effectors.EmbodiedEffector;
import org.baseagent.embodied.sensors.EmbodiedSensor;
import org.baseagent.grid.DefaultHasGridPosition;
import org.baseagent.grid.GridLayer;

public class EmbodiedBehavior extends DefaultHasGridPosition implements Behavior {
	
	public EmbodiedBehavior() { 
		super();
	}
	
	@Override
	public void executeBehavior(Agent agent) {
		if (!(agent instanceof EmbodiedAgent)) {
			return;
		}
		
		EmbodiedAgent embodiedAgent = (EmbodiedAgent)agent;

		executeSensors(embodiedAgent);
		executeProcessors(embodiedAgent);
		for (Behavior behavior : agent.getBehaviors()) {
			behavior.executeBehavior(agent);
		}
//		agent.getBehaviorPolicy().executeBehavior(agent);
		executeEffectors(embodiedAgent);
	}
	
	private void executeSensors(EmbodiedAgent embodiedAgent) {
		for (EmbodiedSensor sensor : embodiedAgent.getSensors()) {
			sensor.sense(embodiedAgent);
		}
	}

	private void executeProcessors(EmbodiedAgent embodiedAgent) {
		for (Processor processor : embodiedAgent.getProcessors()) {
			processor.process(embodiedAgent);
		}
	}

	private void executeEffectors(EmbodiedAgent embodiedAgent) {
		for (EmbodiedEffector effector : embodiedAgent.getEffectors()) {
			effector.effect(embodiedAgent);
		}
	}

	@Override
	public GridLayer getGridLayer() {
		return null; // TODO: EmbodiedBehavior.getGridLayer
	}

}
