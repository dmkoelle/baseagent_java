package org.baseagent.grid;

public interface GridBoundsPolicy {
	public int boundX(int x);
	public int boundY(int y);
	public double boundX(double x);
	public double boundY(double y);
}
