package org.baseagent.ui;

import org.baseagent.grid.GridLayer;

import javafx.scene.paint.Color;

public interface GridCellRenderer extends GridLayerRenderer {
	@Override
	public default void draw(SimulationCanvasContext sc, GridLayer layer, double canvasWidth, double canvasHeight) {
		int gridSizeWidth = layer.getParentGrid().getWidthInCells();
		int gridSizeHeight = layer.getParentGrid().getHeightInCells();
		double xPixelsPerCell = (canvasWidth - (gridSizeWidth-1) * sc.getCellXSpacing()) / gridSizeWidth;
		double yPixelsPerCell = (canvasHeight - (gridSizeHeight-1) * sc.getCellYSpacing()) / gridSizeHeight;

		for (int i=0; i < gridSizeWidth; i++) {
			for (int u=0; u < gridSizeHeight; u++) {
				drawCell(sc, layer.current().get(i, u), i * xPixelsPerCell + i * sc.getCellXSpacing(), u * yPixelsPerCell + u * sc.getCellYSpacing(), xPixelsPerCell, yPixelsPerCell);
			}
		}
	}
	
	public void drawCell(SimulationCanvasContext sc, Object value, double xInPixels, double yInPixels, double widthInPixels, double heightInPixels);
}
