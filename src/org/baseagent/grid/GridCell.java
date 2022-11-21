package org.baseagent.grid;

public class GridCell implements HasGridPosition {
	private GridLayer layer;
	private int x;
	private int y;
	
	public GridCell(GridLayer layer, int x, int y) {
		this.layer = layer;
		this.x = x;
		this.y = y;
	}
	
	public GridPosition getGridPosition() {
		return new GridPosition(this.x, this.y);
	}
	
	@Override
	public void setCellX(int x) {
		this.x = x;
	}
	
	@Override 
	public int getCellX() {
		return this.x;
	}

	@Override
	public void setCellY(int y) {
		this.y = y;
	}
	
	@Override 
	public int getCellY() {
		return this.y;
	}
	
	@Override
	public GridLayer getGridLayer() {
		return this.layer;
	}
	
	@Override
	public double getHeading() {
		return 0.0D;
	}
	
	public Object get() {
		return layer.get(x, y);
	}
	
	public void set(Object object) {
		layer.set(x, y, object);
	}
}
