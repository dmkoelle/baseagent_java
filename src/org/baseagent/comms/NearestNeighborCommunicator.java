package org.baseagent.comms;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.baseagent.grid.HasGridPosition;
import org.baseagent.sim.Simulation;
import org.baseagent.util.BaseAgentMath;
import org.baseagent.util.Pair;


public class NearestNeighborCommunicator extends Communicator {
	private int numNeighbors;
	
	public NearestNeighborCommunicator(Simulation simulation, int numNeighbors) {
		super(simulation);
		this.numNeighbors = numNeighbors;
	}
	
	@Override
	public List<MessageListener> getExpectedRecipients(Message message) {
		if (!(message.getOriginalSender() instanceof HasGridPosition)) throw new IllegalArgumentException("NearestNeighborCommunicator.sendMessage expects the sender implement HasPosition.");
		List<Pair<MessageListener, Double>> nearestNeighbors = findNearestNeighbors((HasGridPosition)message.getOriginalSender());

		// Step 3. Send message to only the closest N agents 
		return nearestNeighbors.stream().limit(numNeighbors).map(neighbor -> neighbor.getFirst()).collect(Collectors.toList());
	}

	/**
	 * This calculates the distance between agents based on each agent's knowledge of
	 * its location. Instead, you might want to calculate the distance based on what
	 * each agent *THINKS* its distance is from other agents. That will require that
	 * the calculation uses the fromAgent's internal knowledge rather than the agent
	 * data from the simulation.
	 * 
	 * @param fromAgent
	 * @return
	 */
	public List<Pair<MessageListener, Double>> findNearestNeighbors(HasGridPosition fromAgent) {
		List<Pair<MessageListener, Double>> messageListenersAndDistancesSortedByDistance = new ArrayList<>();
		
		// Step 1. Calculate the distances
		// TODO: I DON'T KNOW IF THESE SHOULD BE MESSAGELISTENERS OR AGENTS. Like, what about a Beacon?
		for (MessageListener messageListener : getSimulation().getMessageListeners()) {
			if ((messageListener instanceof HasGridPosition) && !(messageListener.equals(fromAgent))) {
				double distance = BaseAgentMath.distance(((HasGridPosition)messageListener), fromAgent);
				messageListenersAndDistancesSortedByDistance.add(new Pair<MessageListener, Double>(messageListener, distance));
			}
		}
		
		// Step 2. Sort the list based on distance
		messageListenersAndDistancesSortedByDistance.sort((Pair<MessageListener, Double> p1, Pair<MessageListener, Double> p2) -> p1.getSecond() > p2.getSecond() ? 1 : p1.getSecond() < p2.getSecond() ? -1 : 0);

		return messageListenersAndDistancesSortedByDistance;
	}
}
