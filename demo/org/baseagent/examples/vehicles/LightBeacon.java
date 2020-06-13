package org.baseagent.examples.vehicles;

import org.baseagent.Beacon;
import org.baseagent.signals.Signal;
import org.baseagent.ui.GridCanvasContext;
import org.baseagent.ui.defaults.VisualizationLibrary;

import javafx.scene.paint.Color;

public class LightBeacon extends Beacon {
	public LightBeacon(String gridLayerName, int x, int y) {
		super(gridLayerName);
		setCellX(x);
		setCellY(y);
	}

	public LightBeacon(String gridLayerName, Signal signal, int x, int y) {
		super(gridLayerName);
		setCellX(x);
		setCellY(y);
	}

	@Override
	public void draw(GridCanvasContext sc) {
		VisualizationLibrary.fillCircle(sc.getGraphicsContext(), getCellX(), getCellY(), sc.getCellWidth(), sc.getCellHeight(), Color.YELLOW, Color.ORANGE);
	}
}
