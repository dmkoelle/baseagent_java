package org.baseagent.behaviors.grid;

import org.baseagent.Agent;
import org.baseagent.behaviors.Behavior;
import org.baseagent.grid.HasGridPosition;
import org.baseagent.signals.Signal;

public class FollowGradientBehavior implements Behavior {
	private Signal signal;
	private Direction direction;
	private double previousHeading = 0.0D;
	
	public FollowGradientBehavior(Signal signal, FollowGradientBehavior.Direction direction) {
		this.signal = signal;
		this.direction = direction;
		throw new UnsupportedOperationException("FollowGradientBehavior has not been fully implemented");
	}
	
	@Override
	public void executeBehavior(Agent agent) {
		if (direction == Direction.UP) {
			step_up((HasGridPosition)agent);
		}
//		else if (direction == Direction.DOWN) {
//			step_down(agent);
//		}
//		else {
//			step_level(agent);
//		}
	}
		
	public void step_up(HasGridPosition hasPos) {
//		int x = hasPos.currentX();
//		int y = hasPos.currentY();
//		double signalValueAtCurrentPosition = simulationComponent.getSimulation().getGrid().get(simulationComponent.getX(), simulationComponent.getY()).getValueOfItem(signal);
//		double nextSignalValue = 0.0D; // Default value works for Direction.UP
//		
//		// Find the next value
//		positions.clear();		
//		for (int i=-1; i < 2; i++) {
//			for (int u=-1; u < 2; u++) {
//				if (!((i==0) && (u==0))) {
//					double signalValueAtThisPosition = simulationComponent.getSimulation().getGrid().get(x+i, y+u).getValueOfItem(signal);
//					if (signalValueAtThisPosition >= nextSignalValue) {
//						positions.add(new Position(x+i, y+u));
//						nextSignalValue = signalValueAtCurrentPosition;
//					}
//				}
//			}
//		} 
//		
//		Position nextPosition = positions.get((int)(Math.random() * positions.size()));
//		simulationComponent.setX(nextPosition.getX());
//		simulationComponent.setX(nextPosition.getY());
	}

	public static enum Direction { UP, DOWN, LEVEL }
}
