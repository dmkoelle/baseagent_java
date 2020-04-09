package org.baseagent.behaviors.grid;

import java.util.Random;

import org.baseagent.Agent;
import org.baseagent.behaviors.Behavior;
import org.baseagent.sim.GridAgent;

public class RandomWalkBehavior implements Behavior {
	private Random random;
	
	public RandomWalkBehavior() {
		super();
		this.random = new Random();
	}
	
	@Override
	public void executeBehavior(Agent xagent) {
		GridAgent agent = (GridAgent)xagent;
		int deltaX = 1 - random.nextInt(3);
		int deltaY = 1 - random.nextInt(3);
		agent.moveDelta(deltaX, deltaY);
	}
}
