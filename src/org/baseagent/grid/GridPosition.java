package org.baseagent.grid;

public class GridPosition implements HasGridPosition {
	private int cellX, cellY;
	
	public GridPosition() { }

	public GridPosition(int cellX, int cellY) {
		this.cellX = cellX;
		this.cellY = cellY;
	}
	
	public void setCellX(int cellX) {
		this.cellX = cellX;
	}
	
	@Override
	public int getCellX() {
		return this.cellX;
	}

	public void setCellY(int cellY) {
		this.cellY = cellY;
	}
	
	@Override
	public int getCellY() {
		return this.cellY;
	}

	@Override
	public double getHeading() {
		return 0.0D;
	}

	@Override
	public GridLayer getGridLayer() {
		return null;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("(");
		b.append(getCellX());
		b.append(",");
		b.append(getCellY());
		b.append(")");
		return b.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if ((o == null) || (!(o instanceof GridPosition))) return false;
		GridPosition pos2 = (GridPosition)o;
		
		return (getCellX() == pos2.getCellX()) && (getCellY() == pos2.getCellY());
	}
	
	@Override
	public int hashCode() {
		return getCellX() * 37 + getCellY() * 31;
	}
}
