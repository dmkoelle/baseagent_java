package org.baseagent.grid;

public class DefaultHasGridPosition implements HasGridPosition {
	private int cellX, cellY;
	
	public DefaultHasGridPosition() { }

	public DefaultHasGridPosition(int cellX, int cellY) {
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
}
