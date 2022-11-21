package org.baseagent.ui.defaults;

import org.baseagent.grid.GridLayer;
import org.baseagent.ui.GridCanvasContext;
import org.baseagent.ui.GridCellRenderer;

import javafx.scene.paint.Color;

public class ColorGridCellRenderer implements GridCellRenderer {
	@Override
	public void drawCell(GridCanvasContext gcc, GridLayer layer, Object color, double xInPixels, double yInPixels, double widthInPixels, double heightInPixels) {
		gcc.getGraphicsContext().setFill((Color)color);
		gcc.getGraphicsContext().fillRect(xInPixels, yInPixels, widthInPixels, heightInPixels);
	}
}
