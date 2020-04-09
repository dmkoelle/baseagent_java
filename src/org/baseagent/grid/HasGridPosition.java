package org.baseagent.grid;

public interface HasGridPosition {
	public void setCellX(int x);
	public int getCellX();
	public void setCellY(int y);
	public int getCellY();
	public GridLayer getGridLayer();
	public double getHeading();
}
