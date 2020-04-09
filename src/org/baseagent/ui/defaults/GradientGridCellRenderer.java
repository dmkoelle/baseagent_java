package org.baseagent.ui.defaults;

import org.baseagent.ui.GridCellRenderer;
import org.baseagent.ui.SimulationCanvasContext;

import javafx.scene.paint.Color;

public class GradientGridCellRenderer implements GridCellRenderer {
	private Color color1;
	private Color color2;
	
	public GradientGridCellRenderer(Color color1, Color color2) {
		this.color1 = color1;
		this.color2 = color2;
	}
	
	@Override
	public void drawCell(SimulationCanvasContext sc, Object value, double xInPixels, double yInPixels, double widthInPixels, double heightInPixels) {
		if (value == null) return;
		if (!(value instanceof Double)) {
			System.out.println("value is "+value.getClass());
			throw new IllegalArgumentException("Cannot use GradientGridCellRenderer on a grid layer that does not consist of Double values");
		}
		Double doubleVal = (Double)value;
//		System.out.println("dv is "+doubleVal);
		
		double cr = color1.getRed() + (color2.getRed() - color1.getRed()) * doubleVal;
		double cg = color1.getGreen() + (color2.getGreen() - color1.getGreen()) * doubleVal;
		double cb = color1.getBlue() + (color2.getBlue() - color1.getGreen()) * doubleVal;
		double ca = color1.getOpacity() + (color2.getOpacity() - color1.getOpacity()) * doubleVal;
		
		sc.getGraphicsContext().setFill(new Color(cr, cg, cb, ca));
		sc.getGraphicsContext().fillRect(xInPixels, yInPixels, widthInPixels, heightInPixels);
	}
}
