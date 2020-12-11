package org.baseagent.grid.generators;

import org.baseagent.grid.Grid;
import org.baseagent.grid.GridLayer;
import org.baseagent.grid.GridLayer.GridLayerUpdateOption;
import org.baseagent.grid.GridLayerStep;

public class HillyDoubleGridLayerGenerator implements GridLayerGenerator {
	private int numSpots;
	private int numIter;
	
	public HillyDoubleGridLayerGenerator(int numSpots, int numIter) {
		this.numSpots = numSpots;
		this.numIter = numIter;
	}
	
	@Override
	public GridLayer generateGridLayer(String name, Grid parentGrid) {
		GridLayer g = new GridLayer(name, parentGrid, GridLayerUpdateOption.NO_SWITCH);

		g.current().fill(0.0D);
		g.current().scatter(1.0D, numSpots);
	
		
		for (int i=0; i < numIter; i++) {
			((GridLayerStep)g.next()).fill(0.0D);
			for (int x=0; x < parentGrid.getWidthInCells(); x++) {
				for (int y=0; y < parentGrid.getHeightInCells(); y++) {
					if (g.current().average8Neighbors(x, y) >= (1.0 / 8.0)) {
						if (Math.random() > 0.25) {
							((GridLayerStep)g.next()).set(x, y, 1.0);
						}
					}
					if ((double)g.current().get(x, y) > 0.0) {
						if (Math.random() > 0.25) {
							g.next().set(x, y, (double)g.current().get(x, y) * Math.random());
					
						}
					}
				}
			}
			g.switchToNextStep();
		}
		
		return g;
	}

}
