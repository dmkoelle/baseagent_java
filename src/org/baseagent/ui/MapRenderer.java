package org.baseagent.ui;

import org.baseagent.map.Map;

public interface MapRenderer {
    public void draw(GridCanvasContext gcc, Map map, double canvasWidth, double canvasHeight);
}
