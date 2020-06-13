package org.baseagent.comms;

import java.util.ArrayList;
import java.util.List;

import org.baseagent.sim.Simulation;

public class BroadcastCommunicator extends Communicator {
	public BroadcastCommunicator(Simulation simulation) {
		super(simulation);
	}

	@Override
	public List<MessageListener> getExpectedRecipients(Message message) {
		List<MessageListener> recipients = new ArrayList<>();
		recipients.addAll(getSimulation().getMessageListeners());
		recipients.remove(message.getSender());
		return recipients;
	}
}
