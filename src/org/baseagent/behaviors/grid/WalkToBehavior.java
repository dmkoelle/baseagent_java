package org.baseagent.behaviors.grid;

import java.util.ArrayList;
import java.util.List;

import org.baseagent.Agent;
import org.baseagent.behaviors.LifecycleBehavior;
import org.baseagent.grid.GridPosition;
import org.baseagent.sim.GridAgent;
import org.baseagent.util.BaseAgentMath;

public class WalkToBehavior extends LifecycleBehavior {
	private List<GridPosition> destinationList;
	private int currentDestinationIndex = -1;
	private boolean pausedBecauseNoMoreDestinations;
	private double speed = 1.0;
	private double movingX = Double.MAX_VALUE;
	private double movingY = Double.MAX_VALUE;

	public WalkToBehavior() { 
		super();
		this.destinationList = new ArrayList<>();
	}
	
	public WalkToBehavior(GridAgent agent, int destinationX, int destinationY, double speed) { 
		this();
		addDestination(agent, destinationX, destinationY);
		setSpeed(speed);
	}
	
	@Override
	public void startBehavior(Agent agent) {
		if (destinationList.size() > 0) {
			super.startBehavior(agent);
			currentDestinationIndex = 0;
		}
	}
	
	@Override
	public void executeBehavior(Agent agent) {
		if (isPaused()) return;
		
		if (!isStarted()) {
			startBehavior(agent);
		}
		
		if (isStarted()) {
			GridAgent gridAgent = (GridAgent)agent;
	
			if (movingX == Double.MAX_VALUE) {
				movingX = gridAgent.getCellX();
			}
			if (movingY == Double.MAX_VALUE) {
				movingY = gridAgent.getCellY();
			}
	
			GridPosition currentDestination = destinationList.get(currentDestinationIndex);
			double distance = BaseAgentMath.distance(gridAgent, currentDestination);
			double direction = BaseAgentMath.direction(gridAgent, currentDestination);
			
			if (speed >= distance) {
				gridAgent.moveTo(destinationList.get(currentDestinationIndex));
				selectNextDestination(agent);
			} else {
				movingX += speed * Math.cos(direction);
				movingY += speed * Math.sin(direction);
				gridAgent.setHeading(direction);
				gridAgent.moveTo((int)movingX, (int)movingY);

				// These checks update movingX and movingY in case the 
				// agent goes out of its bounds 
				if (gridAgent.getCellX() != (int)movingX) {
					this.movingX = gridAgent.getCellX() + (movingX - gridAgent.getCellX());
				}
				if (gridAgent.getCellY() != (int)movingY) {
					this.movingX = gridAgent.getCellX() + (movingY - gridAgent.getCellY());
				}
			}
		}
	}

	private void selectNextDestination(Agent agent) {
		if (destinationList.size()-1 > currentDestinationIndex) {
			currentDestinationIndex++;
			if (pausedBecauseNoMoreDestinations) {
				resumeBehavior(agent);
				pausedBecauseNoMoreDestinations = false;
			}
		} else {
			pauseBehavior(agent);
			pausedBecauseNoMoreDestinations = true;
		}
	}
	
	public void addDestination(GridAgent gridAgent, int destinationX, int destinationY) {
		int boundedDestinationX = gridAgent.getGrid().getBoundsPolicy().boundX(destinationX);
		int boundedDestinationY = gridAgent.getGrid().getBoundsPolicy().boundY(destinationY);
		this.destinationList.add(new GridPosition(boundedDestinationX, boundedDestinationY));
		if (pausedBecauseNoMoreDestinations) {
			selectNextDestination(gridAgent); 
		}
	}
	
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	public GridPosition getCurrentDestination() {
		return this.destinationList.get(currentDestinationIndex);
	}
	
	public double getSpeed() {
		return this.speed;
	}
}
