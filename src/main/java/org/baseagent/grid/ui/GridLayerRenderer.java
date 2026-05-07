package org.baseagent.grid.ui;

import org.baseagent.grid.GridLayer;

public interface GridLayerRenderer {
    void draw(GridCanvasContext gcc, GridLayer<?> layer, double canvasWidth, double canvasHeight);
}
