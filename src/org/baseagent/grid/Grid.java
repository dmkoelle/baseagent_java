package org.baseagent.grid;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.baseagent.grid.GridLayer.GridLayerUpdateOption;
import org.baseagent.sim.Simulation;
import org.baseagent.sim.SimulationComponent;
import org.baseagent.sim.Universe;

public class Grid extends SimulationComponent implements Universe {
	public static String DEFAULT_GRID = "DEFAULT_GRID";
	private int widthInCells, heightInCells;
	private String id;
	private Map<String, GridLayer> layers;
	private GridBoundsPolicy boundsPolicy;
	private GridStepPolicy stepPolicy;
	
	public Grid(int widthInCells, int heightInCells) {
		this(Grid.DEFAULT_GRID, widthInCells, heightInCells);
	}
	
	public Grid(String id, int widthInCells, int heightInCells) {
		super();
		
		this.id = id;
		this.widthInCells = widthInCells;
		this.heightInCells = heightInCells;
	
		this.layers = new HashMap<>();
		this.boundsPolicy = new TorusBoundsPolicy(widthInCells, heightInCells);
		this.stepPolicy = new FullGridStepPolicy(this);
//		addGridLayer("default", new GridLayer(this));  // TODO - Should a Grid have a default layer?
	}
	
	public int getWidthInCells() {
		return this.widthInCells;
	}
	
	public int getHeightInCells() {
		return this.heightInCells;
	}
	
	public void setBoundsPolicy(GridBoundsPolicy boundsPolicy) {
		this.boundsPolicy = boundsPolicy;
	}

	public GridBoundsPolicy getBoundsPolicy() {
		return this.boundsPolicy;
	}
	
	public void setStepPolicy(GridStepPolicy stepPolicy) {
		this.stepPolicy = stepPolicy;
	}

	public GridStepPolicy getStepPolicy() {
		return this.stepPolicy;
	}
	
	public GridLayer createGridLayer(String name, GridLayerUpdateOption updateOption) {
		GridLayer layer = new GridLayer(name, this, updateOption);
		this.addGridLayer(name, layer);
		return layer;
	}
	
	public void addGridLayer(String name, GridLayer layer) {
		this.layers.put(name, layer);
	}
	
	public GridLayer getGridLayer(String name) {
		return layers.get(name);
	}
	
	public void removeGridLayer(String name) {
		this.layers.remove(name);
	}
	
	//
	// Operations on the default layer
	//

	// TODO: Operations on the default layer
	
	public void step() {
		stepPolicy.step();
	}

	public Collection<GridLayer> getGridLayers() {
		return layers.values();
	}
	
	public void swap() {
		for (GridLayer layer : getGridLayers()) {
			layer.switchToNextStep();
		}
	}
	
	@Override
	public Type getType() {
		return SimulationComponent.Type.GRID;
	}

	@Override
	public void onAfterStepStarted(Simulation simulation) {
		step();
	}

	@Override
	public void onBeforeStepEnded(Simulation simulation) {
		swap();
	}
}
