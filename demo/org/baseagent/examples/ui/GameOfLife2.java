package org.baseagent.examples.ui;

import org.baseagent.Patch;
import org.baseagent.grid.Grid;
import org.baseagent.grid.GridLayer;
import org.baseagent.ui.defaults.QuickSimulation;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameOfLife2 extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		QuickSimulation sim = new QuickSimulation(100, 100, 5, 5);
		
		// Create a Glider, with the shape given below, where the top-left position is 20,20
		sim.form(Boolean.TRUE, 20, 20, new String[] { 
				"OOO",
				"O..",
				".O." });
		
		// Scatter 500 TRUE cells in a rectangular area from 50,50 to 100,100
		sim.scatter(Boolean.TRUE, 500, 50, 50, 100, 100); 

		Patch patch = new Patch() {
			@Override
			public void applyPatch(Grid grid, int x, int y) {
				GridLayer layer = sim.getDefaultGridLayer();
				
				// Count the number of alive neighbors (not including self) in this step. 
				int count = layer.current().count8Neighbors(x, y, cellValue -> (cellValue == Boolean.TRUE));
				
				// A cell that alive in this step stays alive in the next step if there are 2 or 3 alive neighbors in this step.
				// A cell that is dead in this step becomes alive in the next step if there are 3 alive neighbors in this step.
				// (Otherwise, by default, the cell in the next step will be dead.)
				if (layer.current().get(x, y) == Boolean.TRUE) {
					layer.next().set(x, y, (count == 2) || (count == 3));
				} else {
					layer.next().set(x, y, count == 3);
				}
			}
		};
		sim.add(patch);
		
        stage.setTitle("Game of Life");
        stage.setScene(new Scene(new Group(sim.getGridCanvas()), 500, 500));
        stage.show();
        
		sim.start();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
