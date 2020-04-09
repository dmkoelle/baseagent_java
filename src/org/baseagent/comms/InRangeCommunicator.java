package org.baseagent.comms;

import java.util.ArrayList;
import java.util.List;

import org.baseagent.Agent;
import org.baseagent.grid.HasGridPosition;
import org.baseagent.sim.Simulation;
import org.baseagent.util.BaseAgentMath;

/**
 * Sends the message to all listeners who are within the range specified in the constructor.
 * 
 */
public class InRangeCommunicator extends Communicator {
	private double range;
	
	public InRangeCommunicator(Simulation simulation, double range) {
		super(simulation);
		this.range = range;
	}
	
	public void setRange(double range) {
		this.range = range;
	}
	
	public double getRange() {
		return this.range;
	}
	
	@Override
	public List<MessageListener> getExpectedRecipients(Message message) {
		if (!(message.getOriginalSender() instanceof HasGridPosition)) throw new IllegalArgumentException("InRangeCommunicator.sendMessage expects the sender implement HasPosition.");

		List<MessageListener> recipients = new ArrayList<>();
		Agent fromAgent = (Agent)message.getSender();
		for (MessageListener messageListener : getSimulation().getMessageListeners()) {
			if ((messageListener instanceof HasGridPosition) && !(messageListener.equals(fromAgent))) {
				double distance = BaseAgentMath.distance(((HasGridPosition)messageListener), (HasGridPosition)fromAgent);
				if (distance <= range) {
					recipients.add(messageListener);
				}
			}
		}
		return recipients;
	}
}
