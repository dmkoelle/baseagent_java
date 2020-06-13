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
	public void draw(GridCanvasContext sc) {
		GraphicsContext gc = sc.getGraphicsContext();
		gc.setStroke(sc.getColorPalette().get(1));
		gc.strokeLine(getCellX() + (sc.getCellWidth()/ 2.0), getCellY() + (sc.getCellHeight()/ 2.0), getCellX() + 10.0 * Math.cos(getHeading()), getCellY() + 10.0 * Math.sin(getHeading()));
		gc.setFill(color);
		gc.fillOval(getCellX(), getCellY(), sc.getCellWidth(), sc.getCellHeight());
	}
}
