package org.baseagent.examples.vehicles;

import org.baseagent.embodied.EmbodiedAgent;
import org.baseagent.embodied.effectors.ForceEffector;
import org.baseagent.embodied.sensors.MaxSignalSensor;
import org.baseagent.signals.Signal;

import javafx.scene.paint.Color;

public class VehiclesAgent extends EmbodiedAgent {
	public VehiclesAgent(String layerName, Color color, Signal signal, int loveOrHateLight) {
		super(layerName, 3, 3);
		
		this.setColor(color);

		MaxSignalSensor sensor1 = new MaxSignalSensor(layerName, signal);
		MaxSignalSensor sensor2 = new MaxSignalSensor(layerName, signal);
		ForceEffector wheel1 = new ForceEffector(); 
		ForceEffector wheel2 = new ForceEffector();
		
		sensor1.getDirectionPort().connectTo(wheel1.getDirectionPort()); 
		sensor1.getIntensityPort().connectTo(wheel1.getIntensityPort()); 

		this.place(0, 0, sensor1); 
		this.place(2, 0, sensor2); 
		this.place(0, 2, wheel1); 
		this.place(2, 2, wheel2); 
	}
	
	public static final int ATTRACTED_TO_LIGHT = 1;
	public static final int SCARED_OF_LIGHT = -1;
}
