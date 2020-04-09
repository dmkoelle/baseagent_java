package org.baseagent.embodied.sensors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.baseagent.Agent;
import org.baseagent.Beacon;
import org.baseagent.embodied.ConnectedComponent;
import org.baseagent.sim.GridAgent;
import org.baseagent.util.BaseAgentMath;

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
public class AllSignalSensor extends EmbodiedSensor {
	private ConnectedComponent<List<Double>> directionPort;
	private ConnectedComponent<List<Double>> intensityPort;
	private ConnectedComponent<List<Object>> valuePort;
	
	public AllSignalSensor(String layerName) {
		super(layerName);
	}

	/** Posts a list of every signal found by the sensor */
	public void sense(Agent xagent)	{
		GridAgent agent = (GridAgent)xagent;

		List<Beacon> beacons = agent.getSimulation().getBeacons().stream().filter(beacon -> beacon.getGridLayer().getLayerName().equals(this.layerName)).collect(Collectors.toList());

		List<Double> intensities = new ArrayList<>();
		List<Double> directions = new ArrayList<>();
		List<Object> values = new ArrayList<>();
		
		for (Beacon beacon : beacons) {
			if (beacon.reaches(agent, this)) {
				intensities.add(beacon.getSignalValueAt(agent, this));
				directions.add(BaseAgentMath.fineDistance(beacon, agent, this));
				values.add(beacon.getSignalValueAt(p2));
			}
		}
		
		this.intensityPort.setOutputValue(intensities);
		this.directionPort.setOutputValue(directions);
		this.valuePort.setOutputValue(values);
	}

	public ConnectedComponent<List<Double>> getDirectionPort() {
		return this.directionPort;
	}
	
	public ConnectedComponent<List<Double>> getIntensityPort() {
		return this.intensityPort;
	}
	
	public ConnectedComponent<List<Object>> getValuePort() {
		return this.valuePort;
	}
}
