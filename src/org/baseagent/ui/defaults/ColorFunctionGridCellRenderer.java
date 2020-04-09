package org.baseagent.ui.defaults;

import java.util.function.Function;

import org.baseagent.ui.GridCellRenderer;
import org.baseagent.ui.SimulationCanvasContext;

import javafx.scene.paint.Color;

public class ColorFunctionGridCellRenderer implements GridCellRenderer {
	private Function<Object, Color> colorFunction;
	
	public ColorFunctionGridCellRenderer(Function<Object, Color> colorFunction) {
		this.colorFunction = colorFunction;
	}
	
	@Override
	public void drawCell(SimulationCanvasContext sc, Object object, double xInPixels, double yInPixels, double widthInPixels, double heightInPixels) {
		sc.getGraphicsContext().setFill(this.colorFunction.apply(object));
		sc.getGraphicsContext().fillRect(xInPixels, yInPixels, widthInPixels, heightInPixels);
	}
}
