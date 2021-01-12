package org.baseagent.ui;

import org.baseagent.grid.Grid;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public abstract class GridController {
	private GridCanvas gridCanvas;
	
	public GridController(GridCanvas gridCanvas) {
		this.gridCanvas = gridCanvas;
		this.gridCanvas.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				mouseClicked(e);
			}
		});
	}
	
	private void mouseClicked(MouseEvent event) {
		Grid grid = gridCanvas.getGrid();
		// TODO the GridController.mouseClicked is not factoring in cell spacing or zoom (getXFactor / getYFactor)
		int cellX = (int)(event.getSceneX() / gridCanvas.getGridCanvasContext().getCellWidth());
		int cellY = (int)(event.getSceneY() / gridCanvas.getGridCanvasContext().getCellHeight());
		mouseClickedOnGrid(grid, event.getSceneX(), event.getSceneY(), cellX, cellY);
	}
	
	public abstract void mouseClickedOnGrid(Grid grid, double sceneX, double sceneY, int cellX, int cellY);
}
