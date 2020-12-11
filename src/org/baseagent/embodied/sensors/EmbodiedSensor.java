package org.baseagent.embodied.sensors;

import org.baseagent.Agent;
import org.baseagent.Sensor;
import org.baseagent.grid.GridPosition;
import org.baseagent.grid.GridLayer;

public abstract class EmbodiedSensor extends GridPosition implements Sensor {
	private String layerName;
	
	public EmbodiedSensor(String layerName) {
		super();
		this.layerName = layerName;
	}

	@Override
	public GridLayer getGridLayer() {
		return null;
	}
	
	public String getLayerName() {
		return this.layerName;
	}
	
	@Override
	public abstract void sense(Agent agent);
}
