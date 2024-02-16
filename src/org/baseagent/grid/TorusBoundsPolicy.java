package org.baseagent.grid;

public class TorusBoundsPolicy implements GridBoundsPolicy {
	private int widthInCells;
	private int heightInCells;
	
	public TorusBoundsPolicy(Grid grid) {
		this.widthInCells = grid.getWidthInCells();
		this.heightInCells = grid.getHeightInCells();
	}

	@Override
	public int boundX(int x) {
		if (x < 0) {
			return (widthInCells - (-x % widthInCells));
		}
		else if (x > widthInCells-1) {
			return x % widthInCells;
		} 
		else return x;
	}

	@Override
	public int boundY(int y) {
		if (y < 0) {
			return (heightInCells - (-y % heightInCells));
		}
		else if (y > heightInCells-1) {
			return y % heightInCells;
		} 
		else return y;
	}

	@Override
	public double boundX(double x) {
		if (x < 0.0) {
			return (widthInCells - (-x % widthInCells));
		}
		else if (x > widthInCells-0.1) {
			return x % widthInCells;
		} 
		else return x;
	}

	@Override
	public double boundY(double y) {
		if (y < 0.0) {
			return (heightInCells - (-y % heightInCells));
		}
		else if (y > heightInCells-0.1) {
			return y % heightInCells;
		} 
		else return y;
	}

}
