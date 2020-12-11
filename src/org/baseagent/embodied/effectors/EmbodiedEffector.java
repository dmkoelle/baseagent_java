package org.baseagent.embodied.effectors;

import org.baseagent.Agent;
import org.baseagent.grid.GridPosition;
import org.baseagent.grid.GridLayer;

/**
 * An Effector can emit a signal or change the physical world or change the agent's place in the world by applying a force
 * or update the agent's knowledge or...
 *  
 * @author David Koelle
 */
public abstract class EmbodiedEffector extends GridPosition implements Effector {
	private String layerName;
	
	public EmbodiedEffector(String layerName) {
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

	public abstract void effect(Agent agent);
}
