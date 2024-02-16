package org.baseagent.ui.defaults;

import org.baseagent.ui.GridCanvasContext;

import javafx.scene.paint.Color;

public class NiceVisualizationLibrary {
	public static void glowCircleForCell(GridCanvasContext gcc, int cellX, int cellY, Color stroke, Color fill, Color startColor, double margin) {
		VisualizationLibrary.fillCircle(gcc.getGraphicsContext(), cellX * (gcc.getCellWidth() + gcc.getCellXSpacing()) + gcc.getCellWidth()/2.0, cellY * (gcc.getCellHeight() + gcc.getCellYSpacing()) + gcc.getCellHeight()/2.0, gcc.getCellWidth() / 2.0 - margin, gcc.getCellHeight() / 2.0 - margin, stroke, fill);
		
	}

}
