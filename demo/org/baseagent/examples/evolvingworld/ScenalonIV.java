package org.baseagent.examples.evolvingworld;

import org.baseagent.grid.Grid;
import org.baseagent.grid.GridLayer;
import org.baseagent.grid.GridLayer.GridLayerUpdateOption;
import org.baseagent.sim.Simulation;
import org.baseagent.ui.GridCanvas;
import org.baseagent.ui.GridCanvasContext;
import org.baseagent.ui.GridCellRenderer;
import org.baseagent.ui.defaults.VisualizationLibrary;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ScenalonIV extends Application {
	
	public static final int GRID_WIDTH = 100;
	public static final int GRID_HEIGHT = 100;
	
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		int cellWidth = 5;
		int cellHeight = 5;
		int cellXSpacing = 0;
		int cellYSpacing = 0;
		int canvasWidth = GRID_WIDTH * (cellWidth + cellXSpacing);
		int canvasHeight = GRID_HEIGHT * (cellHeight + cellYSpacing);
		
		Simulation simulation = new Simulation();
		simulation.endWhen(sim -> sim.getStepTime() == 15000);
		simulation.setDelayAfterEachStep(10);
		
		Grid grid = new Grid(GRID_WIDTH, GRID_HEIGHT);
		simulation.setUniverse(grid);
		
		GridCanvas gridCanvas = new GridCanvas(simulation, grid, cellWidth, cellHeight);
		gridCanvas.setGridRenderer(new GridCellRenderer() {
			@Override
			public void drawCell(GridCanvasContext gcc, Object value, double xInPixels, double yInPixels, double widthInPixels, double heightInPixels) {
				VisualizationLibrary.fillRect(gcc.getGraphicsContext(), xInPixels, yInPixels, widthInPixels, heightInPixels, Color.GREEN, Color.PALEGREEN);
			}
		});
		
		GridLayer layer = grid.createGridLayer("LANDFORMS", GridLayerUpdateOption.NO_SWITCH);
		

		
		
		simulation.start();
		
		BorderPane pane = new BorderPane();
		pane.setCenter(gridCanvas);
		
		primaryStage.setTitle("Scenalon IV");
		primaryStage.setScene(new Scene(new ScrollPane(pane), pane.getWidth(), pane.getHeight()));
		primaryStage.setX(100);
		primaryStage.setY(100);
		primaryStage.setWidth(canvasWidth + 17);
		primaryStage.setHeight(canvasHeight + 40);
		primaryStage.show();		
	}
	

}
