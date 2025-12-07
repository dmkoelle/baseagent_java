package org.baseagent.grid;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class GridLayer<T> implements Iterable<GridCell<T>> {
	private String layerName;
	private GridLayerStep<T> current;
	private GridLayerStep<T> next;
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
		this.current = new GridLayerStep<T>(parentGrid);
		this.next = new GridLayerStep<T>(parentGrid);
	}
	
	public String getLayerName() {
		return this.layerName;
	}

	public Grid getParentGrid() {
		return this.parentGrid;
	}
	
	public GridLayerStep<T> current() {
		return this.current;
	}
	
	public GridLayerStep<T> next() {
		return this.next;
	}

	protected void switchToNextStep() {
		if (updateOption == GridLayerUpdateOption.NEXT_BECOMES_CURRENT) {
			this.current = this.next;
			this.next = new GridLayerStep<T>(parentGrid);
		}
	}
	
	public GridLayerUpdateOption getUpdateOption() {
		return this.updateOption;
	}
	
	public void setUpdateOption(GridLayerUpdateOption updateOption) {
		this.updateOption = updateOption;
	}

	public void persist(T value, int x, int y) {
		if (current().get(x, y) == value) {
			next().set(x, y, value);
		}
	}
	
	@Override
	public Iterator<GridCell<T>> iterator() {
		return new Iterator<GridCell<T>>() {
			int x=0, y=0;
			
			@Override
			public boolean hasNext() {
				if (x + 1 < parentGrid.getWidthInCells()) return true;
				if (y + 1 < parentGrid.getHeightInCells()) return true;
				return false;
			}
			
			@Override
			public GridCell<T> next() {
				x = x + 1;
				if (x > parentGrid.getWidthInCells()) {
					x = 0;
					y = y + 1;
				}
				return new GridCell<T>(GridLayer.this, x, y);
			}
		};
	}
	
	private GridLayerStep<T> getWriteLayer() {
		switch (updateOption) {
		case NEXT_BECOMES_CURRENT : return next;
		case NO_SWITCH : 
		default: return current;
		}
	}
	
	private GridLayerStep<T> getReadLayer() {
		return current;
	}
	
	
	//
	// Methods typically seen in GridLayerStep that should be delegated to current()
	//
	
	public void fill(T value) { getWriteLayer().fill(value); }
	public void fill(T value, int x1, int y1, int x2, int y2) { getWriteLayer().fill(value, x1, y1, x2, y2); }
	public double laplacian_3x3(int x, int y, double centerWeight, double adjacentWeight, double diagonalWeight) { return getWriteLayer().laplacian_3x3(x, y, centerWeight, adjacentWeight, diagonalWeight); }
	public void set(int x, int y, T value) { getWriteLayer().set(x, y, value); }
	public void set(GridPosition position, T value) { getWriteLayer().set(position, value); }
	public void clear(int x, int y) { getWriteLayer().clear(x, y); }
	public void clear(GridPosition position) { getWriteLayer().clear(position); }
	public T get(int x, int y) { return getReadLayer().get(x, y); }
	public T get(GridPosition position) { return getReadLayer().get(position); }
	public int count8Neighbors(int x, int y, Predicate<? super T> predicate) { return getReadLayer().count8Neighbors(x, y, predicate); }
	public int count4Neighbors(int x, int y, Predicate<? super T> predicate) { return getReadLayer().count4Neighbors(x, y, predicate); }
	public double average9Neighbors(int x, int y) { return getReadLayer().average9Neighbors(x, y); }
	public double average8Neighbors(int x, int y) { return getReadLayer().average8Neighbors(x, y); }
	public double average4Neighbors(int x, int y) { return getReadLayer().average4Neighbors(x, y); }
	public long count(Predicate<T> predicate) { return getReadLayer().count(predicate); }
	public int getBooleanAsOneOrZero(int x, int y, Predicate<? super T> predicate) { return getReadLayer().getBooleanAsOneOrZero(x, y, predicate); }
	public void scatter(T thing) { getWriteLayer().scatter(thing, 1); }
	public void scatter(T thing, int howMany) { getWriteLayer().scatter(thing, howMany); }
    public void scatter(T thing, int howMany, int x1, int y1, int x2, int y2) { getWriteLayer().scatter(thing, howMany, x1, y1, x2, y2); }
	public void scatter(List<? extends T> things) { getWriteLayer().scatter(things); }
	public void scatter(List<? extends T> things, int x1, int y1, int x2, int y2) { getWriteLayer().scatter(things, x1, y1, x2, y2); }
	public void form(T thing, int x, int y, String... strings) { getWriteLayer().form(thing, x, y, strings); }
	public GridPosition getRandomUnoccupiedPosition() { return getWriteLayer().getRandomUnoccupiedPosition(); }

	public void debug(PrintStream s) {
		s.println(getLayerName());
		for (int x=0; x < this.getParentGrid().getWidthInCells(); x++) {
			for (int y=0; y < this.getParentGrid().getHeightInCells(); y++) {
				Object o = get(x,y);
				if (o == null) {
					s.print("  (null)  ");
				} else {
					String t = o.toString();
					String u = t.substring(0, Math.min(t.length(), 10));
					String v = PADDING[u.length()];
					s.print(u);
					s.print(v);
				}
				s.print(" ");
			}
			s.println();
		}
	}
	
	private static String[] PADDING = new String[] { "          ", "         ", "        ", "       ", "      ", "     ", "    ", "   ", "  ", " ", "" };
}