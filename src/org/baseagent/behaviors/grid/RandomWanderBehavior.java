package org.baseagent.behaviors.grid;

import java.util.function.Predicate;

import org.baseagent.Agent;
import org.baseagent.sim.GridAgent;
import org.baseagent.sim.Simulation;

public class RandomWanderBehavior extends WalkToBehavior {
	private int distance;
	private Predicate<Simulation> endCondition;
	
	public RandomWanderBehavior() {
		super();
	}
	
	public RandomWanderBehavior(int distance) {
		this();
		setDistance(distance);
	}

	public RandomWanderBehavior(int distance, double speed) {
		this(distance);
		setSpeed(speed);
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	public void endWhen(Predicate<Simulation> endCondition) {
		this.endCondition = endCondition;
	}
	
	@Override
	public void startBehavior(Agent agent) {
		super.startBehavior(agent);
		selectNextPoint((GridAgent)agent);
	}
	
	private void selectNextPoint(GridAgent gridAgent) {
		double randomDirection = Math.random() * 2.0 * Math.PI;
		int nextX = gridAgent.getCellX() + (int)(distance * Math.cos(randomDirection));
		int nextY = gridAgent.getCellY() + (int)(distance * Math.sin(randomDirection));
		addDestination(gridAgent, nextX, nextY);
	}
	
	@Override
	public void executeBehavior(Agent agent) {
		if (isPaused()) return;
		
		if (!isStarted()) {
			startBehavior(agent);
		}
		
		super.executeBehavior(agent);
		
		GridAgent gridAgent = (GridAgent)agent;
		if (gridAgent.isAt(getCurrentDestination())) {
			selectNextPoint(gridAgent);
		}
		
		if ((endCondition != null) && (endCondition.test(gridAgent.getSimulation()))) {
			endBehavior(agent);
			return;
		}
	}
}
