package org.baseagent.worldmap.ui;

import org.baseagent.grid.ui.GridCanvasContext;
import org.baseagent.worldmap.WorldMapGridLayer;

public interface WorldMapLayerRenderer {
    void draw(GridCanvasContext gcc, WorldMapGridLayer layer, double canvasWidth, double canvasHeight);
}
