package org.baseagent.grid;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class GridLayer implements Iterable<GridCell> {
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
	
	public GridLayerUpdateOption getUpdateOption() {
		return this.updateOption;
	}
	
	public void setUpdateOption(GridLayerUpdateOption updateOption) {
		this.updateOption = updateOption;
	}

	public void persist(Object value, int x, int y) {
		if (current().get(x, y) == value) {
			next().set(x, y, value);
		}
	}
	
	@Override
	public Iterator<GridCell> iterator() {
		return new Iterator<GridCell>() {
			int x=0, y=0;
			
			@Override
			public boolean hasNext() {
				if (x + 1 < parentGrid.getWidthInCells()) return true;
				if (y + 1 < parentGrid.getHeightInCells()) return true;
				return false;
			}
			
			@Override
			public GridCell next() {
				x = x + 1;
				if (x > parentGrid.getWidthInCells()) {
					x = 0;
					y = y + 1;
				}
				return new GridCell(GridLayer.this, x, y);
			}
		};
	}
	
	//
	// Methods typically seen in GridLayerStep that should be delegated to current()
	//
	
	public void fill(Object value) { current().fill(value); }
	public void fill(Object value, int x1, int y1, int x2, int y2) { current().fill(value, x1, y1, x2, y2); }
	public double laplacian_3x3(int x, int y, double centerWeight, double adjacentWeight, double diagonalWeight) { return current().laplacian_3x3(x, y, centerWeight, adjacentWeight, diagonalWeight); }
	public void set(int x, int y, Object value) { current().set(x, y, value); }
	public void set(GridPosition position, Object value) { current().set(position, value); }
	public void clear(int x, int y) { current().clear(x, y); }
	public void clear(GridPosition position) { current().clear(position); }
	public Object get(int x, int y) { return current().get(x, y); }
	public Object get(GridPosition position) { return current().get(position); }
	public int count8Neighbors(int x, int y, Predicate<? super Object> predicate) { return current().count8Neighbors(x, y, predicate); }
	public int count4Neighbors(int x, int y, Predicate<? super Object> predicate) { return current().count4Neighbors(x, y, predicate); }
	public double average9Neighbors(int x, int y) { return current().average9Neighbors(x, y); }
	public double average8Neighbors(int x, int y) { return current().average8Neighbors(x, y); }
	public double average4Neighbors(int x, int y) { return current().average4Neighbors(x, y); }
	public long count(Predicate<Object> predicate) { return current().count(predicate); }
	public int getBooleanAsOneOrZero(int x, int y, Predicate<? super Object> predicate) { return current().getBooleanAsOneOrZero(x, y, predicate); }
	public void scatter(Object thing) { current().scatter(thing, 1); }
	public void scatter(Object thing, int howMany) { current().scatter(thing, howMany); }
    public void scatter(Object thing, int howMany, int x1, int y1, int x2, int y2) { current().scatter(thing, howMany, x1, y1, x2, y2); }
	public void scatter(List<? super Object> things) { current().scatter(things); }
	public void scatter(List<? super Object> things, int x1, int y1, int x2, int y2) { current().scatter(things, x1, y1, x2, y2); }
	public void form(Object thing, int x, int y, String... strings) { current().form(thing, x, y, strings); }
	public GridPosition getRandomUnoccupiedPosition() { return current().getRandomUnoccupiedPosition(); }

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
