package org.baseagent.embodied.sensors;

import java.util.List;
import java.util.stream.Collectors;

import org.baseagent.Agent;
import org.baseagent.Beacon;
import org.baseagent.embodied.ConnectedComponent;
import org.baseagent.embodied.effectors.ForceEffector;
import org.baseagent.signals.Signal;
import org.baseagent.sim.GridAgent;

/**
 * A SignalSensor detects a Signal.
 *
 * Example sensors:
 *  - Light sensor
 *  - Sound sensor
 *  - Pheromone sensor
 *  - Vision sensor
 *  
 * You can really sense a bunch of things:
 *  - A signal in the environment
 *  - Something physical about the environment, like the surface you're standing on, or whether you're near another agent
 *  - Internal states
 *  
 * @author David Koelle
 */
public class MaxSignalSensor extends EmbodiedSensor {
	private ConnectedComponent<Double> directionPort;
	private ConnectedComponent<Double> intensityPort;
	private ConnectedComponent<Signal> signalPort;
	
	public MaxSignalSensor(String layerName, Signal signal) {
		super(layerName);
		this.directionPort = new ConnectedComponent<>();
		this.intensityPort = new ConnectedComponent<>();
		this.signalPort = new ConnectedComponent<>();
	}

	public void sense(Agent xagent)	{
		GridAgent agent = (GridAgent)xagent;
		System.out.println("NUMBER OF BEACONS = "+agent.getSimulation().getBeacons().size());
		System.out.println("BEACON'S GRID LAYER = "+agent.getSimulation().getBeacons().get(0).getGridLayer());
		System.out.println("BEACON'S LAYER NAME = "+agent.getSimulation().getBeacons().get(0).getGridLayer().getLayerName());
		System.out.println("THIS LAYER = "+getLayerName());
		List<Beacon> beacons = agent.getSimulation().getBeacons().stream().filter(beacon -> beacon.getGridLayer().getLayerName().equals(getGridLayer().getLayerName())).collect(Collectors.toList());
   
		Beacon maxBeacon = null;
		double maxIntensity = 0.0d;
		
		for (Beacon beacon : beacons) {
			if (beacon.reaches(agent)) {
				double candidateIntensityValue = beacon.getSignalValueAt(agent);
				if (candidateIntensityValue > maxIntensity) {
					maxIntensity = candidateIntensityValue;
					maxBeacon = beacon;
				}
			}
		}

		this.intensityPort.setOutputValue(maxBeacon.getSignalValueAt(agent, this));
//		this.directionPort.setOutputValue(BaseAgentMath.fineDistance(maxBeacon, agent, this));
//		this.signalPort.setOutputValue(maxBeacon.getSignal());
	}

	public ConnectedComponent<Double> getDirectionPort() {
		return this.directionPort;
	}
	
	public ConnectedComponent<Double> getIntensityPort() {
		return this.intensityPort;
	}
	
	public ConnectedComponent<Signal> getSignalPort() {
		return this.signalPort;
	}
	
	public void connectTo(ForceEffector forceEffector) {
		getDirectionPort().connectTo(forceEffector.getDirectionPort());
		getIntensityPort().connectTo(forceEffector.getIntensityPort());
	}
	
}
