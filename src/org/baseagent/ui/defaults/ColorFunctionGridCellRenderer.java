package org.baseagent.ui.defaults;

import java.util.function.Function;

import org.baseagent.ui.GridCellRenderer;
import org.baseagent.ui.GridCanvasContext;

import javafx.scene.paint.Color;

public class ColorFunctionGridCellRenderer implements GridCellRenderer {
	private Function<Object, Color> colorFunction;
	
	public ColorFunctionGridCellRenderer(Function<Object, Color> colorFunction) {
		this.colorFunction = colorFunction;
	}
	
	@Override
	public void drawCell(GridCanvasContext gcc, Object object, double xInPixels, double yInPixels, double widthInPixels, double heightInPixels) {
		gcc.getGraphicsContext().setFill(this.colorFunction.apply(object));
		gcc.getGraphicsContext().fillRect(xInPixels, yInPixels, widthInPixels, heightInPixels);
	}
}
