package org.baseagent.ui.defaults;

import java.util.Map;

import org.baseagent.grid.GridLayer;
import org.baseagent.ui.GridCanvasContext;
import org.baseagent.ui.GridCellRenderer;

import javafx.scene.paint.Color;

public class ColorGridCellRenderer implements GridCellRenderer {
	private Map<Object, Color> objectToColor;
	
	public ColorGridCellRenderer() { }
	
	public ColorGridCellRenderer(Map<Object, Color> objectToColor) {
		this.objectToColor = objectToColor;
	}
	
	@Override
	public void drawCell(GridCanvasContext gcc, GridLayer layer, Object object, double xInPixels, double yInPixels, double widthInPixels, double heightInPixels) {
		if (objectToColor != null) {
			if (objectToColor.containsKey(object)) {
				gcc.getGraphicsContext().setFill(objectToColor.get(object));
			} else {
				gcc.getGraphicsContext().setFill(Color.RED);
			}
		} else {
			gcc.getGraphicsContext().setFill(objectToColor.get((Color)object));
		}
		gcc.getGraphicsContext().fillRect(xInPixels, yInPixels, widthInPixels, heightInPixels);
	}
}
