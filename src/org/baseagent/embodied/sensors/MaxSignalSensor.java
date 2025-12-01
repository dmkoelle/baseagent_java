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
 */
public class MaxSignalSensor extends EmbodiedSensor {
	private ConnectedComponent<Double> directionPort;
	private ConnectedComponent<Double> intensityPort;
	private ConnectedComponent<Signal> signalPort;

	// Search radius (cells) used when querying nearby beacons from Simulation spatial index
	private int searchRadius = 20;
	
	public MaxSignalSensor(String layerName, Signal signal) {
		super(layerName);
		this.directionPort = new ConnectedComponent<>();
		this.intensityPort = new ConnectedComponent<>();
		this.signalPort = new ConnectedComponent<>();
	}

	public void sense(Agent xagent) {
		GridAgent agent = (GridAgent)xagent;
		// Query nearby beacons efficiently using the Simulation spatial index
		List<Beacon> beacons = agent.getSimulation().getBeaconsNear(getLayerName(), agent.getCellX(), agent.getCellY(), this.searchRadius);
   
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

		if (maxBeacon != null) {
			this.intensityPort.setOutputValue(maxBeacon.getSignalValueAt(agent, this));
		} else {
			this.intensityPort.setOutputValue(0.0);
		}
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
 
 public int getSearchRadius() { return this.searchRadius; }
 public void setSearchRadius(int r) { this.searchRadius = Math.max(0, r); }
}