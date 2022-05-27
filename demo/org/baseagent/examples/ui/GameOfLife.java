package org.baseagent.examples.ui;

import org.baseagent.Patch;
import org.baseagent.grid.Grid;
import org.baseagent.grid.GridLayer;
import org.baseagent.grid.GridLayer.GridLayerUpdateOption;
import org.baseagent.sim.Simulation;
import org.baseagent.ui.GridCanvas;
import org.baseagent.ui.GridCanvasContext;
import org.baseagent.ui.GridCellRenderer;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GameOfLife extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		// Set up a Simulation, which runs the simulation itself.
		Simulation simulation = new Simulation();
		simulation.endWhen(sim -> sim.getStepTime() == 10000);

		// Set up a 100x100 grid, and tell the Simulation that this Grid
		// is the Universe in which the contents of the simulation will run
		Grid grid = new Grid(100, 100);
		simulation.setUniverse(grid);
		simulation.setDelayAfterEachStep(100);

		// Create a GridLayer and fill it to be mostly empty,
		// but throw in a couple of cells marked True. 
		GridLayer layer = grid.createGridLayer(GRIDLAYER_NAME, GridLayerUpdateOption.NEXT_BECOMES_CURRENT);
		layer.fill(Boolean.FALSE);  
		
		// Create a Glider, with the shape given below, where the top-left position is 20,20
		layer.form(Boolean.TRUE, 20, 20, new String[] { 
				"OOO",
				"O..",
				".O." });
		
		// Scatter 500 TRUE cells in a rectangular area from 50,50 to 100,100
		layer.scatter(Boolean.TRUE, 500, 50, 50, 100, 100); 
		
		Patch patch = new Patch() {
			@Override
			public void applyPatch(Grid grid, int x, int y) {
				GridLayer layer = grid.getGridLayer(GRIDLAYER_NAME);
				int count = layer.current().count8Neighbors(x, y, cellValue -> (cellValue == Boolean.TRUE));
				if (layer.current().get(x, y) == Boolean.TRUE) {
					layer.next().set(x, y, (count == 2) || (count == 3));
				} else {
					layer.next().set(x, y, count == 3);
				}
			}
		};
		simulation.add(patch);
		
		// Create a GridCanvas, which visualizes the contents of the grid
		GridCanvas canvas = new GridCanvas(simulation, grid, 5, 5);
		canvas.addGridLayerRenderer(GRIDLAYER_NAME, new GridCellRenderer() {
			@Override
			public void drawCell(GridCanvasContext gcc, Object value, double xInPixels, double yInPixels, double widthInPixels, double heightInPixels) {
				if (value == Boolean.TRUE) {
					GraphicsContext gc = (GraphicsContext)gcc.getGraphicsContext();
					gc.setFill(Color.RED);
					gc.fillRect(xInPixels+1, yInPixels+1, widthInPixels-2, heightInPixels-2);
				}
			}
		});
		
        stage.setTitle("Game of Life");
        stage.setScene(new Scene(new Group(canvas), 500, 500));
        stage.show();
        
		simulation.start();
	}
	
	public static final String GRIDLAYER_NAME = "layer";
	
	public static void main(String[] args) {
		launch(args);
	}
}
