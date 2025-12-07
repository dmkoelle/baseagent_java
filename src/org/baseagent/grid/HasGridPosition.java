package org.baseagent.grid;

public interface HasGridPosition<T> {
	public void setCellX(int x);
	public int getCellX();
	public void setCellY(int y);
	public int getCellY();
	public GridLayer<T> getGridLayer();
	public double getHeading();
}