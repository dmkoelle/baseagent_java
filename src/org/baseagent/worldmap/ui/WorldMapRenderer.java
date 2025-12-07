package org.baseagent.worldmap.ui;

import org.baseagent.grid.ui.GridCanvasContext;
import org.baseagent.worldmap.WorldMap;

public interface WorldMapRenderer {
    public void draw(GridCanvasContext gcc, WorldMap map, double canvasWidth, double canvasHeight);
}
