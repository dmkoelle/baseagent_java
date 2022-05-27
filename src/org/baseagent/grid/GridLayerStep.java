package org.baseagent.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class GridLayerStep {
	List<List<Object>> cells;
	Grid parentGrid;
	
	public GridLayerStep(Grid parentGrid) { 
		this.parentGrid = parentGrid;
		this.cells = initNewLayer(parentGrid.getWidthInCells(), parentGrid.getHeightInCells());
	}

	private List<List<Object>> initNewLayer(int width, int height) {
		List<List<Object>> retVal;
		
		retVal = new ArrayList<>();
		for (int i=0; i < height; i++) {
			List<Object> row = new ArrayList<>();
			retVal.add(row);
			for (int u=0; u < width; u++) {
				Object cell = null;
				row.add(cell);
			}
		}
		return retVal;
	}

	public void fill(Object value) {
		fill(value, 0, 0, parentGrid.getWidthInCells(), parentGrid.getHeightInCells());
	}

	public void fill(Object value, int x1, int y1, int x2, int y2) {
		for (int i=y1; i < y2; i++) {
			for (int u=x1; u < x2; u++) {
				cells.get(i).set(u, value);
			}
		}
	}
	
	public double laplacian_3x3(int x, int y, double centerWeight, double adjacentWeight, double diagonalWeight) {
		if (!(get(x,y) instanceof Double)) {
			throw new IllegalArgumentException("Cannot call Laplacian transform on a grid layer that does not consist of Double values.");
		}
		
		Double xy = (Double)get(x,y);
		Double a0 = (Double)get(x,y-1);
		Double a1 = (Double)get(x+1,y);
		Double a2 = (Double)get(x,y+1);
		Double a3 = (Double)get(x,y-1);
		Double d0 = (Double)get(x-1,y-1);
		Double d1 = (Double)get(x+1,y-1);
		Double d2 = (Double)get(x+1,y+1);
		Double d3 = (Double)get(x-1,y+1);
		
		return (centerWeight * xy +
			    adjacentWeight * a0 +
			    adjacentWeight * a1 +
			    adjacentWeight * a2 +
			    adjacentWeight * a3 +
			    diagonalWeight * d0 +
			    diagonalWeight * d1 +
			    diagonalWeight * d2 +
			    diagonalWeight * d3) ;
	}
	
	public void set(int x, int y, Object value) {
		cells.get(parentGrid.getBoundsPolicy().boundY(y)).set(parentGrid.getBoundsPolicy().boundX(x), value);
	}

	public void set(GridPosition position, Object value) {
		set(position.getCellX(), position.getCellY(), value);
	}

	public void clear(int x, int y) {
		cells.get(parentGrid.getBoundsPolicy().boundY(y)).set(parentGrid.getBoundsPolicy().boundX(x), null);
	}
	
	public void clear(GridPosition position) {
		clear(position.getCellX(), position.getCellY());
	}
	
	public Object get(int x, int y) {
		return cells.get(parentGrid.getBoundsPolicy().boundY(y)).get(parentGrid.getBoundsPolicy().boundX(x));
	}
	
	public Object get(GridPosition position) {
		return get(position.getCellX(), position.getCellY());
	}
	
	public void setEachCell(GridLayerStep conditionalLayer, Function<Object, Object> f) {
		if ((this.parentGrid.getWidthInCells() != conditionalLayer.parentGrid.getWidthInCells()) || (this.parentGrid.getHeightInCells() != conditionalLayer.parentGrid.getHeightInCells())) {
			throw new IllegalArgumentException("The parent grid of this layer and the conditional layer must have the same width and height.");
		}
		for (int x=0; x < parentGrid.getWidthInCells(); x++) {
			for (int y=0; y < parentGrid.getHeightInCells(); y++) {
				set(x, y, f.apply(conditionalLayer.get(x, y)));
			}
		}
	}

	public void setEachCell(BiFunction<Integer, Integer, Object> f) {
		for (int x=0; x < parentGrid.getWidthInCells(); x++) {
			for (int y=0; y < parentGrid.getHeightInCells(); y++) {
				set(x, y, f.apply(x, y));
			}
		}
	}

	/**
	 * Returns a count of the neighbors surrounding
	 * the given cell that match the predicate.
	 */
	public int count8Neighbors(int x, int y, Predicate<? super Object> predicate) {
		int retVal = 0;
		if (predicate.test(get(x-1, y-1))) retVal++;
		if (predicate.test(get(x,   y-1))) retVal++;
		if (predicate.test(get(x+1, y-1))) retVal++;
		if (predicate.test(get(x-1, y  ))) retVal++;
		if (predicate.test(get(x+1, y  ))) retVal++;
		if (predicate.test(get(x-1, y+1))) retVal++;
		if (predicate.test(get(x,   y+1))) retVal++;
		if (predicate.test(get(x+1, y+1))) retVal++;
		return retVal;
	}

	/**
	 * Returns a count of the neighbors to the north, south, east, and west
	 * of the given cell that match the predicate.
	 */
	public int count4Neighbors(int x, int y, Predicate<? super Object> predicate) {
		int retVal = 0;
		if (predicate.test(get(x,   y-1))) retVal++;
		if (predicate.test(get(x-1, y  ))) retVal++;
		if (predicate.test(get(x+1, y  ))) retVal++;
		if (predicate.test(get(x,   y+1))) retVal++;
		return retVal;
	}

	public double average9Neighbors(int x, int y) {
		if (!(get(x,y) instanceof Double)) {
			throw new IllegalArgumentException("Cannot call 'average' method on a grid layer that does not consist of Double values.");
		}

		double total = 0.0D;
		total += (Double)this.get(x-1, y-1);
		total += (Double)this.get(x, y-1);
		total += (Double)this.get(x+1, y-1);
		total += (Double)this.get(x-1, y);
		total += (Double)this.get(x, y);
		total += (Double)this.get(x+1, y);
		total += (Double)this.get(x-1, y+1);
		total += (Double)this.get(x, y+1);
		total += (Double)this.get(x+1, y+1);
		return total / 9.0;
	}

	public double average8Neighbors(int x, int y) {
		if (!(get(x,y) instanceof Double)) {
			throw new IllegalArgumentException("Cannot call 'average' method on a grid layer that does not consist of Double values.");
		}

		double total = 0.0D;
		total += (Double)this.get(x-1, y-1);
		total += (Double)this.get(x, y-1);
		total += (Double)this.get(x+1, y-1);
		total += (Double)this.get(x-1, y);
		total += (Double)this.get(x+1, y);
		total += (Double)this.get(x-1, y+1);
		total += (Double)this.get(x, y+1);
		total += (Double)this.get(x+1, y+1);
		return total / 8.0;
	}

	public double average4Neighbors(int x, int y) {
		if (!(get(x,y) instanceof Double)) {
			throw new IllegalArgumentException("Cannot call 'average' method on a grid layer that does not consist of Double values.");
		}

		double total = 0.0D;
		total += (Double)this.get(x, y-1);
		total += (Double)this.get(x-1, y);
		total += (Double)this.get(x+1, y);
		total += (Double)this.get(x, y+1);
		return total / 4.0;
	}

	/**
	 * Returns a count of the number of times the predicate is true
	 * across the full layer.
	 */
	public long count(Predicate<Object> predicate) {
		long retVal = 0;
		
		for (int i=0; i < parentGrid.getHeightInCells(); i++) {
			for (int u=0; u < parentGrid.getWidthInCells(); u++) {
				if (predicate.test(cells.get(i).get(u))) {
					retVal++;
				}
			}
		}
		
		return retVal;
	}

	/**
	 * Test the give predicate. If the predicate is true, return 1,
	 * otherwise return 0.
	 */
	public int getBooleanAsOneOrZero(int x, int y, Predicate<Object> predicate) {
		return (predicate.test(get(x, y)) ? 1 : 0);
	}
	
	/**
	 * Randomly places the given thing throughout the GridLayerStep, making sure
	 * to not place a thing on a space that already has the same thing.
	 */
	public void scatter(Object thing, int howMany) {
		scatter(thing, howMany, 0, 0, parentGrid.getWidthInCells(), parentGrid.getHeightInCells());
	}
	
	public void scatter(List<?> things) {
		scatter(things, 0, 0, parentGrid.getWidthInCells(), parentGrid.getHeightInCells());
	}

	public void scatter(List<?> things, int x1, int y1, int x2, int y2) {
		for (Object thing : things) {
			scatter(thing, 1, x1, y1, x2, y2);
		}
	}

    public void scatter(Object thing, int howMany, int x1, int y1, int x2, int y2) {
    	Random rand = new Random();
    	for (int i=0; i < howMany; i++) {
			int x = 0;
			int y = 0;
			do {
				x = x1 + rand.nextInt(x2 - x1);
				y = y1 + rand.nextInt(y2 - y1);
			} while (get(x, y) == thing); // TODO - or 'unoccupied'
			set(x, y, thing);
		}
	}
	
    
	/**
	 * Defines a visual arrangement for playing the given thing on the grid.
	 * Key: . = empty space, O = Thing
	 */
	public void form(Object thing, int x, int y, String... strings) {
		for (int sy = 0; sy < strings.length; sy++) {
			for (int sx = 0; sx < strings[sy].length(); sx++) {
				char ch = strings[sy].charAt(sx);
				if (ch == 'O') {
					set(x + sx, y + sy, thing);
				}
			}
		}
	}
	
	public void form(String... strings) {
		throw new IllegalArgumentException("form not implemented");
	}
	
	public GridPosition getRandomUnoccupiedPosition() {
		int x, y = 0;
		int numTries = (int)(this.parentGrid.getWidthInCells() * this.parentGrid.getHeightInCells() * 0.25);
		do {
			numTries--;
			x = (int)Math.random() * this.parentGrid.getWidthInCells();
			y = (int)Math.random() * this.parentGrid.getHeightInCells();
		} while ((get(x, y) != null) && (numTries > 0));
		if (numTries == 0) {
			throw new IllegalArgumentException("Not implemented: Second strategy in getRandomUnoccupiedPosition that starts methodically scanning rows for an empty position, since the random search didn't work for 25% of the cells in the grid.");
		}
		return new GridPosition(x, y);
	}
}
