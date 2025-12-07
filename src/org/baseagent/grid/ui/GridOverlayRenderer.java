package org.baseagent.grid.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import org.baseagent.grid.GridLayer;
import org.baseagent.util.GeoUtils;
import org.baseagent.worldmap.WorldMapGridLayer;
import org.baseagent.worldmap.ui.WorldMapLayerRenderer;

public class GridOverlayRenderer implements WorldMapLayerRenderer {
    private Color fillColor = Color.color(1.0, 0.0, 0.0, 0.15);
    private Color strokeColor = Color.color(1.0, 0.0, 0.0, 0.5);

    public GridOverlayRenderer() {}

    @Override
    public void draw(GridCanvasContext gcc, WorldMapGridLayer layer, double canvasWidth, double canvasHeight) {
        if (layer == null) return;
        if (!layer.isGridLayer()) return;

        Object zProp = gcc.getProperties().get("slippyZoom");
        Object offXProp = gcc.getProperties().get("viewOffsetX");
        Object offYProp = gcc.getProperties().get("viewOffsetY");
        Object zoomProp = gcc.getProperties().get("zoomScale");
        int z = (zProp instanceof Number) ? ((Number)zProp).intValue() : 2;
        double viewOffsetX = (offXProp instanceof Number) ? ((Number)offXProp).doubleValue() : 0.0;
        double viewOffsetY = (offYProp instanceof Number) ? ((Number)offYProp).doubleValue() : 0.0;
        double zoomScale = (zoomProp instanceof Number) ? ((Number)zoomProp).doubleValue() : 1.0;
        int tileSize = 256;

        int rows = layer.getGridRows();
        int cols = layer.getGridCols();
        double topLat = layer.getTopLat();
        double leftLon = layer.getLeftLon();
        double bottomLat = layer.getBottomLat();
        double rightLon = layer.getRightLon();

        GraphicsContext gc = gcc.getGraphicsContext();

        // If this MapLayer is backed by a GridLayer, read cell values from that instead
        GridLayer backing = layer.getBackingGridLayer();

        for (int r = 0; r < rows; r++) {
            double latTop = topLat + ( (double)r / rows) * (bottomLat - topLat);
            double latBottom = topLat + ( (double)(r+1) / rows) * (bottomLat - topLat);
            for (int c = 0; c < cols; c++) {
                double lonLeft = leftLon + ((double)c / cols) * (rightLon - leftLon);
                double lonRight = leftLon + ((double)(c+1) / cols) * (rightLon - leftLon);

                double[] topLeft = GeoUtils.latLonToPixelXY(latTop, lonLeft, z, tileSize);
                double[] bottomRight = GeoUtils.latLonToPixelXY(latBottom, lonRight, z, tileSize);

                // Apply fractional zoomScale to pixel coordinates so the overlay is anchored
                // to geographic coordinates while its cell screen size changes.
                double screenX = topLeft[0] * zoomScale - viewOffsetX;
                double screenY = topLeft[1] * zoomScale - viewOffsetY;
                double width = (bottomRight[0] - topLeft[0]) * zoomScale;
                double height = (bottomRight[1] - topLeft[1]) * zoomScale;

                Object cellValue = null;
                if (backing != null) {
                    try { cellValue = backing.get(c, r); } catch (Exception ex) { cellValue = null; }
                } else {
                    cellValue = layer.current().get(c, r);
                }
                if (cellValue != null) {
                    gc.setFill(fillColor);
                    gc.fillRect(screenX, screenY, width, height);
                }
                gc.setStroke(strokeColor);
                gc.strokeRect(screenX, screenY, width, height);
            }
        }
    }
}