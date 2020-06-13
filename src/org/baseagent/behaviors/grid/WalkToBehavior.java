package org.baseagent.behaviors.grid;

import org.baseagent.Agent;
import org.baseagent.behaviors.StatefulBehavior;
import org.baseagent.sim.GridAgent;
import org.baseagent.statemachine.State;
import org.baseagent.util.BaseAgentMath;

public class WalkToBehavior extends StatefulBehavior {
	private int destinationX;
	private int destinationY;
	private double movingX = Double.MAX_VALUE;
	private double movingY = Double.MAX_VALUE;
	private double speed = 1.0;
	
	public WalkToBehavior() {
		setState(STARTING_STATE);
	}
	
	@Override
	public void executeBehavior(Agent xagent) {
//		System.out.println("WTB State is "+getState().toString());

		GridAgent agent = (GridAgent)xagent;

		if (movingX == Double.MAX_VALUE) {
			movingX = agent.getCellX();
		}
		if (movingY == Double.MAX_VALUE) {
			movingY = agent.getCellY();
		}

		double distance = BaseAgentMath.distance(agent, destinationX, destinationY);
		double direction = BaseAgentMath.direction(agent, destinationX, destinationY);
		
		if (isState(WALKING_STATE)) {
			if (speed > distance) {
				agent.moveTo(destinationX, destinationY);
				setState(ARRIVED_STATE);
			} else {
				movingX += speed * Math.cos(direction);
				movingY += speed * Math.sin(direction);
				agent.setHeading(direction);
				agent.moveTo((int)movingX, (int)movingY);
			}
		}
	}
	
	public void setDestination(int destinationX, int destinationY) {
		this.destinationX = destinationX;
		this.destinationY = destinationY;
		setState(WALKING_STATE);
	}
	
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	public double getSpeed() {
		return this.speed;
	}
	
	public void pause() {
		if (isState(WALKING_STATE)) {
			setState(PAUSED_STATE);
		}
	}
	
	public void resume() {
		if (isState(PAUSED_STATE)) {
			setState(WALKING_STATE);
		}
	}
	
	public static State STARTING_STATE = new State("Not yet started");
	public static State WALKING_STATE = new State("Walking");
	public static State PAUSED_STATE = new State("Paused");
	public static State ARRIVED_STATE = new State("Arrived");

}
