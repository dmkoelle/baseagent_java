package org.baseagent.grid;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.baseagent.HasStep;
import org.baseagent.grid.GridLayer.GridLayerUpdateOption;
import org.baseagent.sim.Simulation;
import org.baseagent.sim.SimulationComponent;
import org.baseagent.sim.Universe;

public class Grid extends SimulationComponent implements Universe {
	public static String DEFAULT_GRID_LAYER = "DEFAULT_GRID_LAYER";
	private int widthInCells, heightInCells;
	private Map<String, GridLayer> layers;
	private GridBoundsPolicy boundsPolicy;
	private GridStepPolicy stepPolicy;
	
	public Grid(int widthInCells, int heightInCells) {
		super();
		
		this.widthInCells = widthInCells;
		this.heightInCells = heightInCells;
	
		this.layers = new HashMap<>();
		this.boundsPolicy = new TorusBoundsPolicy(this);
		this.stepPolicy = new FullGridStepPolicy(this);
		createGridLayer(DEFAULT_GRID_LAYER, GridLayerUpdateOption.NO_SWITCH);  
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
	
	public GridLayer getOrCreateGridLayer(String name, GridLayerUpdateOption updateOption) {
		GridLayer retVal = layers.get(name);
		if (retVal == null) {
			return createGridLayer(name, updateOption);
		} else {
			return retVal;
		}
	}
	
	public void removeGridLayer(String name) {
		this.layers.remove(name);
	}
	
	//
	// Operations on the default layer
	//

	// TODO: Operations on the default layer
	
//	@Override
//	public void step(Simulation simulation) { }
	
	public void step0(Simulation simulation) {
		stepPolicy.step(simulation);
	}

	public Collection<GridLayer> getGridLayers() {
		return layers.values();
	}
	
	boolean hasBeenInitialized = false;
	
	public void init() {
		if (!hasBeenInitialized) {
			for (GridLayer layer : getGridLayers()) {
				layer.switchToNextStep();
			}
			hasBeenInitialized = true;
		}
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
		init();
		step0(simulation);
	}

	@Override
	public void onBeforeStepEnded(Simulation simulation) {
		swap();
	}
	
	public void debug(PrintStream s) {
		for (GridLayer layer : getGridLayers()) {
			layer.debug(s);
			s.println();
		}
	}
	
	//
	// Delegates for the DEFAULT_GRID_LAYER
	//
	
	public String getLayerName() { return Grid.DEFAULT_GRID_LAYER; }
	public GridLayerStep current() { return getGridLayer(Grid.DEFAULT_GRID_LAYER).current(); }
	public GridLayerStep next() { return getGridLayer(Grid.DEFAULT_GRID_LAYER).next(); }
	public GridLayerUpdateOption getUpdateOption() { return getGridLayer(Grid.DEFAULT_GRID_LAYER).getUpdateOption(); }
	public void setUpdateOption(GridLayerUpdateOption updateOption) { getGridLayer(Grid.DEFAULT_GRID_LAYER).setUpdateOption(updateOption); }
	public void persist(Object value, int x, int y) { getGridLayer(Grid.DEFAULT_GRID_LAYER).persist(value, x, y); }
	public Iterator<GridCell> iterator() { return getGridLayer(Grid.DEFAULT_GRID_LAYER).iterator(); }
	public void fill(Object value) { getGridLayer(Grid.DEFAULT_GRID_LAYER).fill(value); }
	public void fill(Object value, int x1, int y1, int x2, int y2) { getGridLayer(Grid.DEFAULT_GRID_LAYER).fill(value, x1, y1, x2, y2); }
	public double laplacian_3x3(int x, int y, double centerWeight, double adjacentWeight, double diagonalWeight) { return getGridLayer(Grid.DEFAULT_GRID_LAYER).laplacian_3x3(x, y, centerWeight, adjacentWeight, diagonalWeight); }
	public void set(int x, int y, Object value) { getGridLayer(Grid.DEFAULT_GRID_LAYER).set(x, y, value); }
	public void set(GridPosition position, Object value) { getGridLayer(Grid.DEFAULT_GRID_LAYER).set(position, value); }
	public void clear(int x, int y) { getGridLayer(Grid.DEFAULT_GRID_LAYER).clear(x, y); }
	public void clear(GridPosition position) { getGridLayer(Grid.DEFAULT_GRID_LAYER).clear(position); }
	public Object get(int x, int y) { return getGridLayer(Grid.DEFAULT_GRID_LAYER).get(x, y); }
	public Object get(GridPosition position) { return getGridLayer(Grid.DEFAULT_GRID_LAYER).get(position); }
	public int count8Neighbors(int x, int y, Predicate<? super Object> predicate) { return getGridLayer(Grid.DEFAULT_GRID_LAYER).count8Neighbors(x, y, predicate); }
	public int count4Neighbors(int x, int y, Predicate<? super Object> predicate) { return getGridLayer(Grid.DEFAULT_GRID_LAYER).count4Neighbors(x, y, predicate); }
	public double average9Neighbors(int x, int y) { return getGridLayer(Grid.DEFAULT_GRID_LAYER).average9Neighbors(x, y); }
	public double average8Neighbors(int x, int y) { return getGridLayer(Grid.DEFAULT_GRID_LAYER).average8Neighbors(x, y); }
	public double average4Neighbors(int x, int y) { return getGridLayer(Grid.DEFAULT_GRID_LAYER).average4Neighbors(x, y); }
	public long count(Predicate<Object> predicate) { return getGridLayer(Grid.DEFAULT_GRID_LAYER).count(predicate); }
	public int getBooleanAsOneOrZero(int x, int y, Predicate<? super Object> predicate) { return getGridLayer(Grid.DEFAULT_GRID_LAYER).getBooleanAsOneOrZero(x, y, predicate); }
	public void scatter(Object thing) { getGridLayer(Grid.DEFAULT_GRID_LAYER).scatter(thing, 1); }
	public void scatter(Object thing, int howMany) { getGridLayer(Grid.DEFAULT_GRID_LAYER).scatter(thing, howMany); }
    public void scatter(Object thing, int howMany, int x1, int y1, int x2, int y2) { getGridLayer(Grid.DEFAULT_GRID_LAYER).scatter(thing, howMany, x1, y1, x2, y2); }
	public void scatter(List<? super Object> things) { getGridLayer(Grid.DEFAULT_GRID_LAYER).scatter(things); }
	public void scatter(List<? super Object> things, int x1, int y1, int x2, int y2) { getGridLayer(Grid.DEFAULT_GRID_LAYER).scatter(things, x1, y1, x2, y2); }
	public void form(Object thing, int x, int y, String... strings) { getGridLayer(Grid.DEFAULT_GRID_LAYER).form(thing, x, y, strings); }
	public GridPosition getRandomUnoccupiedPosition() { return getGridLayer(Grid.DEFAULT_GRID_LAYER).getRandomUnoccupiedPosition(); }
}
