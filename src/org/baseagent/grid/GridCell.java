package org.baseagent.grid;

public class GridCell<T> implements HasGridPosition<T> {
	private GridLayer<T> layer;
	private int x;
	private int y;
	
	public GridCell(GridLayer<T> layer, int x, int y) {
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
	public GridLayer<T> getGridLayer() {
		return this.layer;
	}
	
	@Override
	public double getHeading() {
		return 0.0D;
	}
	
	public T get() {
		return layer.get(x, y);
	}
	
	public void set(T object) {
		layer.set(x, y, object);
	}
}