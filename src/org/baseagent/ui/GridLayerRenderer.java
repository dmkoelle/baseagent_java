package org.baseagent.ui;

import org.baseagent.grid.GridLayer;

public interface GridLayerRenderer {
	public void draw(SimulationCanvasContext sc, GridLayer layer, double canvasWidth, double canvasHeight);
}
