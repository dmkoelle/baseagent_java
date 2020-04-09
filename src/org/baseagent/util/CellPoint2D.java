package org.baseagent.util;

import org.baseagent.grid.GridLayer;
import org.baseagent.grid.HasGridPosition;

public class CellPoint2D implements HasGridPosition {
	private int cellX;
	private int cellY;
	
	public CellPoint2D(int cellX, int cellY) {
		this.cellX = cellX;
		this.cellY = cellY;
	}
	
	public void setCellX(int x) {
		this.cellX = x;
	}

	public int getCellX() {
		return this.cellX;
	}
	
	public void setCellY(int y) {
		this.cellY = y;
	}

	public int getCellY() {
		return this.cellY;
	}
	
	public void add(Vector2D vector) {
		this.cellX = this.cellX + (int)(vector.getMagnitude() * Math.cos(vector.getDirection()));
		this.cellY = this.cellY + (int)(vector.getMagnitude() * Math.sin(vector.getDirection()));
	}
	
	@Override
	public boolean equals(Object o) {
		if ((o == null) || (!(o instanceof CellPoint2D))) return false;
		CellPoint2D p2 = (CellPoint2D)o;
		return ((p2.cellX == this.cellX) && (p2.cellY == this.cellY));
	}
	
	@Override
	public int hashCode() {
		return this.cellX * 37 + this.cellY * 43;
	}
	
	@Override
	public String toString() {
		String objectId = super.toString();
		return objectId+"(cellX="+cellX+", cellY="+cellX+")";
	}

	@Override
	public GridLayer getGridLayer() {
		return null;
	}

	@Override
	public double getHeading() {
		return 0;
	}
}
