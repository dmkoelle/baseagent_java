package org.baseagent.grid.generators;

import org.baseagent.grid.Grid;
import org.baseagent.grid.GridLayer;

public interface GridLayerGenerator {
	public GridLayer generateGridLayer(String layerName, Grid parentGrid);
}
