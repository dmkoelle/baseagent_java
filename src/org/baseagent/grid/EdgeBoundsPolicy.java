package org.baseagent.grid;

public class EdgeBoundsPolicy implements GridBoundsPolicy {
	private int widthInCells;
	private int heightInCells;
	
	public EdgeBoundsPolicy(int widthInCells, int heightInCells) {
		this.widthInCells = widthInCells;
		this.heightInCells = heightInCells;
	}
	
	@Override
	public int boundX(int x) {
		if (x < 0) {
			return 0;
		}
		else if (x > widthInCells-1) {
			return widthInCells-1;
		} 
		else return x;
	}

	@Override
	public int boundY(int y) {
		if (y < 0) {
			return 0;
		}
		else if (y > heightInCells-1) {
			return heightInCells-1;
		} 
		else return y;
	}

	@Override
	public double boundX(double x) {
		if (x < 0.0) {
			return 0.0;
		}
		else if (x > widthInCells-0.1) {
			return widthInCells-0.1;
		} 
		else return x;
	}

	@Override
	public double boundY(double y) {
		if (y < 0.0) {
			return 0.0;
		}
		else if (y > heightInCells-1.0) {
			return heightInCells-1.0;
		} 
		else return y;
	}

}
