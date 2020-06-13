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
		System.out.println("TODO: GridController.mouseClicked");
//		Grid grid = simulationCanvas.getSimulation().getGrid();
//		// TODO The math below clearly isn't including the cellspacing!
//		int cellX = (int)(event.getSceneX() / grid.getWidthInCells());
//		int cellY = (int)(event.getSceneY() / grid.getHeightInCells());
//		mouseClickedOnGrid(grid, event.getSceneX(), event.getSceneY(), cellX, cellY);
	}
	
	public abstract void mouseClickedOnGrid(Grid grid, double sceneX, double sceneY, int cellX, int cellY);
}
