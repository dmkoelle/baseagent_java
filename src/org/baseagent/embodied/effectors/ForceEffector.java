package org.baseagent.embodied.effectors;

import org.baseagent.Agent;
import org.baseagent.embodied.ConnectedComponent;
import org.baseagent.embodied.EmbodiedAgent;
import org.baseagent.embodied.sensors.MaxSignalSensor;
import org.baseagent.util.Vector2D;

/**
 * A ForceEffector applies a force to the agent
 *
 */
public class ForceEffector extends EmbodiedEffector {
	private ConnectedComponent<Double> directionPort;
	private ConnectedComponent<Double> intensityPort;

	public ForceEffector(String layerName) {
		super(layerName);
		this.directionPort = new ConnectedComponent<>();
		this.intensityPort = new ConnectedComponent<>();
	}

	@Override
	public void effect(Agent xagent) {
		EmbodiedAgent agent = (EmbodiedAgent)xagent;
		agent.addForce(new Vector2D(getIntensityPort().getInputValue(), getDirectionPort().getInputValue()));
	}

	public ConnectedComponent<Double> getDirectionPort() {
		return this.directionPort;
	}
	
	public ConnectedComponent<Double> getIntensityPort() {
		return this.intensityPort;
	}
	
	public void connectTo(MaxSignalSensor signalSensor) {
		signalSensor.getDirectionPort().connectTo(getDirectionPort());
		signalSensor.getIntensityPort().connectTo(getIntensityPort());
	}
}
