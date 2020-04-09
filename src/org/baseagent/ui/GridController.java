package org.baseagent.ui;

import org.baseagent.grid.Grid;

import javafx.scene.input.MouseEvent;

public abstract class GridController {
	private GridCanvas simulationCanvas;
	
	public GridController(GridCanvas simulationCanvas) {
		this.simulationCanvas = simulationCanvas;
		throw new UnsupportedOperationException("GridController has not been fully implemented.");
//		this.simulationCanvas.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(event -> mouseClicked(event)));
		
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
