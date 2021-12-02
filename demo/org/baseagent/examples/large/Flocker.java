package org.baseagent.examples.large;

import org.baseagent.behaviors.grid.RandomWanderBehavior;
import org.baseagent.sim.GridAgent;
import org.baseagent.ui.Drawable;
import org.baseagent.ui.GridCanvasContext;
import org.baseagent.ui.defaults.VisualizationLibrary;

import javafx.scene.paint.Color;

public class Flocker extends GridAgent {
	private int id;
	
	public Flocker(int id) {
		super();
		
		this.id = id;
		
		addBehavior(new RandomWanderBehavior(10));
		
		setDrawable(new Drawable() {
			@Override
			public void draw(GridCanvasContext sc) {
				VisualizationLibrary.drawTriangleWithHeadingForCell(sc.getGraphicsContext(), getCellX(), getCellY(), sc.getCellWidth(), sc.getCellHeight(), getHeading(), Color.BLACK, Color.BLUE);
			}
		});	
	}
	
}
