package org.baseagent.embodied.effectors;

import org.baseagent.Agent;
import org.baseagent.Beacon;
import org.baseagent.embodied.ConnectedComponent;
import org.baseagent.signals.Signal;
import org.baseagent.util.Vector2D;

/**
 * A ForceEffector applies a force to the agent
 *
 */
public class SignalEffector extends EmbodiedEffector {
	private ConnectedComponent<Signal> signalPort;

	public SignalEffector(String layerName, Signal signal) {
		super(layerName);
		this.signalPort = new ConnectedComponent<>();
		this.signalPort.setOutputValue(signal);
	}

	@Override
	public void effect(Agent agent) {
		Beacon beacon = new Beacon(this.getOutputValue());
		
	}

	public ConnectedComponent<Signal> getSignalPort() {
		return this.signalPort;
	}
}
