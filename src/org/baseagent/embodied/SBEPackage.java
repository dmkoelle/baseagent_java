package org.baseagent.embodied;

import java.util.ArrayList;
import java.util.List;

import org.baseagent.embodied.effectors.EmbodiedEffector;
import org.baseagent.embodied.sensors.EmbodiedSensor;

public class SBEPackage {
	private List<EmbodiedSensor> sensors;
	private List<EmbodiedBehavior> behaviors;
	private List<EmbodiedEffector> effectors;
	
	public SBEPackage() {
		this.sensors = new ArrayList<>();
		this.behaviors = new ArrayList<>();
		this.effectors = new ArrayList<>();
	}
	
	public List<EmbodiedSensor> getSensors() {
		return this.sensors;
	}
	
	public List<EmbodiedBehavior> getBehaviors() {
		return this.behaviors;
	}
	
	public List<EmbodiedEffector> getEffectors() {
		return this.effectors;
	}

}
