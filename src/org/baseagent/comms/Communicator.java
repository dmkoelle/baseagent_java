package org.baseagent.comms;

import java.util.List;

import org.baseagent.sim.Simulation;
import org.baseagent.sim.SimulationComponent;

public abstract class Communicator extends SimulationComponent {
	public Communicator(Simulation simulation) {
		super(simulation);
	}
	
	@Override
	public SimulationComponent.Type getType() {
		return SimulationComponent.Type.COMMUNICATOR;
	}
	
	public abstract List<MessageListener> getExpectedRecipients(Message message);
	
	public void sendMessage(Message message) {
		for (MessageListener recipient : getExpectedRecipients(message)) {
			recipient.onMessageReceived(message);
		}
	}
	
	public void sendDirectedMessage(MessageListener recipient, Message message) {
		recipient.onMessageReceived(message);
	}
}
