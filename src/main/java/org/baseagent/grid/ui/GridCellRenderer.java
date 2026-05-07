package org.baseagent.grid.ui;

import org.baseagent.grid.GridLayer;

public interface GridCellRenderer extends GridLayerRenderer {
    @Override
    default void draw(GridCanvasContext gcc, GridLayer layer, double canvasWidth, double canvasHeight) {
        int gridSizeWidth = layer.getParentGrid().getWidthInCells();
        int gridSizeHeight = layer.getParentGrid().getHeightInCells();
        double xPixelsPerCell = (canvasWidth - (gridSizeWidth - 1) * gcc.getCellXSpacing()) / gridSizeWidth;
        double yPixelsPerCell = (canvasHeight - (gridSizeHeight - 1) * gcc.getCellYSpacing()) / gridSizeHeight;

        for (int i = 0; i < gridSizeWidth; i++) {
            for (int u = 0; u < gridSizeHeight; u++) {
                drawCell(gcc, layer, layer.current().get(i, u), i * xPixelsPerCell + i * gcc.getCellXSpacing(),
                        u * yPixelsPerCell + u * gcc.getCellYSpacing(), xPixelsPerCell, yPixelsPerCell);
            }
        }
    }

    void drawCell(GridCanvasContext gcc, GridLayer layer, Object value, double xInPixels, double yInPixels,
            double widthInPixels, double heightInPixels);
}
