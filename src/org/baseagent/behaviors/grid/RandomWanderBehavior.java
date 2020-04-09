package org.baseagent.behaviors.grid;

import java.util.function.Predicate;

import org.baseagent.Agent;
import org.baseagent.behaviors.StatefulBehavior;
import org.baseagent.sim.GridAgent;
import org.baseagent.sim.Simulation;
import org.baseagent.statemachine.State;

public class RandomWanderBehavior extends StatefulBehavior {
	private int distance;
	private WalkToBehavior walkToBehavior;
	private Predicate<Simulation> endCondition;
	
	public RandomWanderBehavior() {
		this.walkToBehavior = new WalkToBehavior();
		setState(CHOOSE_POINT_STATE);
	}
	
	public RandomWanderBehavior(int distance) {
		this();
		setDistance(distance);
	}
	
	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	public void endWhen(Predicate<Simulation> endCondition) {
		this.endCondition = endCondition;
	}
	
	@Override
	public void executeBehavior(Agent xagent) {
//		System.out.println("RWB State is "+getState().toString());
		GridAgent agent = (GridAgent)xagent;

		if ((endCondition != null) && (endCondition.test(agent.getSimulation()))) {
			setState(ENDED_STATE);
			return;
		}
	
		if (isState(CHOOSE_POINT_STATE)) {
			double randomDirection = Math.random() * 2.0 * Math.PI;
			int newX = agent.getCellX() + (int)(distance * Math.cos(randomDirection));
			int newY = agent.getCellY() + (int)(distance * Math.sin(randomDirection));
//			System.out.println("WARNING: RandomWanderBehavior is keeping y as-is for testing purposes");
//			int newY = agent.currentY();
			walkToBehavior.setDestination(newX, newY);
			agent.setHeading(randomDirection);
			setState(WALKING_TO_STATE);
		}
		
		if (isState(WALKING_TO_STATE)) {
			walkToBehavior.executeBehavior(agent);
			if (walkToBehavior.isState(WalkToBehavior.ARRIVED_STATE)) {
				setState(CHOOSE_POINT_STATE);
			}
		}
	}
	
	public static State ENDED_STATE = new State("Ended");
	public static State CHOOSE_POINT_STATE = new State("Choose point");
	public static State WALKING_TO_STATE = new State("Walking to");

}
