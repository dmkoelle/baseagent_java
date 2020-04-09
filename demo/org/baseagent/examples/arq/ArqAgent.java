package org.baseagent.examples.arq;

import org.baseagent.Agent;
import org.baseagent.behaviors.grid.RandomWanderBehavior;

public class ArqAgent extends Agent {
	public ArqAgent(String layerName) {
		super(layerName);
		addBehavior(new RandomWanderBehavior(5));
		// TODO  - Agents look like they're all flying to the right. with some downwards. No one going to the left or up. Radians vs. degres problem?
	}
	
//	@Override
//	public void draw(SimulationCanvasContext scc, it x, int y, int width, int height) {
//		
//	}

}
