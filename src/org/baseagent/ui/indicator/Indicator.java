package org.baseagent.ui.indicator;

import org.baseagent.sim.SimulationComponent;

import javafx.scene.canvas.GraphicsContext;

public abstract class Indicator {
	private int x;
	private int y;
	private int width;
	private int height;
	private SimulationComponent associatedComponent;
	
	public Indicator(SimulationComponent associatedComponent) {
		this.associatedComponent = associatedComponent;
	}
	
	public Indicator(SimulationComponent associatedComponent, int x, int y, int width, int height) {
		this(associatedComponent);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public SimulationComponent getAssociatedComponent() {
		return associatedComponent;
	}
	
	public abstract void draw(GraphicsContext gc);

}
