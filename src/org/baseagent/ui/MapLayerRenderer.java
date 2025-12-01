package org.baseagent.ui;

import org.baseagent.map.MapLayer;

public interface MapLayerRenderer {
    public void draw(GridCanvasContext gcc, MapLayer layer, double canvasWidth, double canvasHeight);
}
