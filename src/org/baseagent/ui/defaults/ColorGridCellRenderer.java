package org.baseagent.ui.defaults;

import org.baseagent.ui.GridCellRenderer;
import org.baseagent.ui.SimulationCanvasContext;

import javafx.scene.paint.Color;

public class ColorGridCellRenderer implements GridCellRenderer {
	@Override
	public void drawCell(SimulationCanvasContext sc, Object color, double xInPixels, double yInPixels, double widthInPixels, double heightInPixels) {
		sc.getGraphicsContext().setFill((Color)color);
		sc.getGraphicsContext().fillRect(xInPixels, yInPixels, widthInPixels, heightInPixels);
	}
}
