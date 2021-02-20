package org.baseagent.apps.exper;

import org.baseagent.behaviors.grid.RandomWanderBehavior;
import org.baseagent.sim.GridAgent;
import org.baseagent.ui.GridCanvasContext;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ExperimentAgent extends GridAgent {
	private Color color;
	
	public ExperimentAgent() {
		super();
		this.color = new Color(Math.random(), Math.random(), Math.random(), 1.0d);
		addBehavior(new RandomWanderBehavior(10));
	}

	@Override
	public void draw(GridCanvasContext gcc) {
		GraphicsContext gc = gcc.getGraphicsContext();
		gc.setStroke(gcc.getColorPalette().get(1));
		gc.strokeLine(getCellX() * gcc.getXFactor() + (gcc.getCellWidth()/ 2.0), getCellY() * gcc.getYFactor() + (gcc.getCellHeight()/ 2.0), getCellX() * gcc.getXFactor() + 10.0 * Math.cos(getHeading()), getCellY() * gcc.getYFactor() + 10.0 * Math.sin(getHeading()));
		gc.setFill(color);
		gc.fillOval(getCellX() * gcc.getXFactor(), getCellY() * gcc.getYFactor(), gcc.getCellWidth(), gcc.getCellHeight());
	}
}
