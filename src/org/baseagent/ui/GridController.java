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
		this.gridCanvas.addEventHandler(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				mouseMoved(e);
			}
		});
	}
	
	private void mouseClicked(MouseEvent event) {
		Grid grid = gridCanvas.getGrid();
		// TODO the GridController.mouseClicked is not factoring in cell spacing or zoom (getXFactor / getYFactor)
		int cellX = (int)((event.getSceneX() - gridCanvas.getLayoutX()) / gridCanvas.getGridCanvasContext().getCellWidth());
		int cellY = (int)((event.getSceneY() - gridCanvas.getLayoutY()) / gridCanvas.getGridCanvasContext().getCellHeight());
		mouseClickedOnGrid(grid, gridCanvas, event.getSceneX(), event.getSceneY(), cellX, cellY);
	}
	
	private void mouseMoved(MouseEvent event) {
		Grid grid = gridCanvas.getGrid();
		// TODO the GridController.mouseClicked is not factoring in cell spacing or zoom (getXFactor / getYFactor)
		int cellX = (int)((event.getSceneX() - gridCanvas.getLayoutX()) / gridCanvas.getGridCanvasContext().getCellWidth());
		int cellY = (int)((event.getSceneY() - gridCanvas.getLayoutY()) / gridCanvas.getGridCanvasContext().getCellHeight());
		mouseOverGrid(grid, gridCanvas, event.getSceneX(), event.getSceneY(), cellX, cellY);
	}
	
	public abstract void mouseClickedOnGrid(Grid grid, GridCanvas canvas, double sceneX, double sceneY, int cellX, int cellY);
	public abstract void mouseOverGrid(Grid grid, GridCanvas canvas, double sceneX, double sceneY, int cellX, int cellY);
}
