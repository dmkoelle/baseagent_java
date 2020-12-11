package org.baseagent.grid;

import java.util.List;
import java.util.function.Predicate;

import org.baseagent.examples.ui.GameOfRain.GameOfRainThing;

public class GridLayer {
//	private String layerName = "default";
	private String layerName;
	private GridLayerStep current;
	private GridLayerStep next;
	private Grid parentGrid;
	private GridLayerUpdateOption updateOption;
	
	public enum GridLayerUpdateOption { NEXT_BECOMES_CURRENT, NO_SWITCH };
	
	public GridLayer(Grid parentGrid, GridLayerUpdateOption updateOption) { 
		super();
		setParentGrid(parentGrid);
		setUpdateOption(updateOption);
	}

	public GridLayer(String layerName, Grid parentGrid, GridLayerUpdateOption updateOption) { 
		this(parentGrid, updateOption);
		this.layerName = layerName;
		parentGrid.addGridLayer(layerName, this);
	}

	public void setParentGrid(Grid parentGrid) {
		this.parentGrid = parentGrid;
		this.current = new GridLayerStep(parentGrid);
		this.next = new GridLayerStep(parentGrid);
	}
	
	public String getLayerName() {
		return this.layerName;
	}
	
	public Grid getParentGrid() {
		return this.parentGrid;
	}
	
	public GridLayerStep current() {
		return this.current;
	}
	
	public GridLayerStep next() {
		return this.next;
	}

	public void switchToNextStep() {
		if (updateOption == GridLayerUpdateOption.NEXT_BECOMES_CURRENT) {
			this.current = this.next;
			this.next = new GridLayerStep(parentGrid);
		}
	}
	
	public void setUpdateOption(GridLayerUpdateOption updateOption) {
		this.updateOption = updateOption;
	}

	public void persist(Object value, int x, int y) {
		if (current().get(x, y) == value) {
			next().set(x, y, value);
		}
	}
	
	//
	// Methods typically seen in GridLayerStep that should be delegated to current()
	//
	
	public void fill(Object value) { current().fill(value); }
	public void fill(Object value, int x1, int y1, int x2, int y2) { current().fill(value, x1, y1, x2, y2); }
	public double laplacian_3x3(int x, int y, double centerWeight, double adjacentWeight, double diagonalWeight) { return current().laplacian_3x3(x, y, centerWeight, adjacentWeight, diagonalWeight); }
	public void set(int x, int y, Object value) { current().set(x, y, value); }
	public void clear(int x, int y) { current().clear(x, y); }
	public Object get(int x, int y) { return current().get(x, y); }
	public int count8Neighbors(int x, int y, Predicate<Object> predicate) { return current().count8Neighbors(x, y, predicate); }
	public int count4Neighbors(int x, int y, Predicate<Object> predicate) { return current().count4Neighbors(x, y, predicate); }
	public double average9Neighbors(int x, int y) { return current().average9Neighbors(x, y); }
	public double average8Neighbors(int x, int y) { return current().average8Neighbors(x, y); }
	public double average4Neighbors(int x, int y) { return current().average4Neighbors(x, y); }
	public long count(Predicate<Object> predicate) { return current().count(predicate); }
	public int getBooleanAsOneOrZero(int x, int y, Predicate<Object> predicate) { return current().getBooleanAsOneOrZero(x, y, predicate); }
	public void scatter(Object thing, int howMany) { current().scatter(thing, howMany); }
	public void scatter(List<Object> things) { current().scatter(things); }
	public void scatter(List<Object> things, int x1, int y1, int x2, int y2) { current().scatter(things, x1, y1, x2, y2); }
    public void scatter(Object thing, int howMany, int x1, int y1, int x2, int y2) { current().scatter(thing, howMany, x1, y1, x2, y2); }
	public void form(Object thing, int x, int y, String... strings) { current().form(thing, x, y, strings); }
}
