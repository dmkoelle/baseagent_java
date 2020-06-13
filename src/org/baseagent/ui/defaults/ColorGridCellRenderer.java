package org.baseagent.ui.defaults;

import org.baseagent.ui.GridCellRenderer;
import org.baseagent.ui.GridCanvasContext;

import javafx.scene.paint.Color;

public class ColorGridCellRenderer implements GridCellRenderer {
	@Override
	public void drawCell(GridCanvasContext gcc, Object color, double xInPixels, double yInPixels, double widthInPixels, double heightInPixels) {
		gcc.getGraphicsContext().setFill((Color)color);
		gcc.getGraphicsContext().fillRect(xInPixels, yInPixels, widthInPixels, heightInPixels);
	}
}
