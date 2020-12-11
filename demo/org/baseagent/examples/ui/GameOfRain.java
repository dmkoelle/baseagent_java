package org.baseagent.examples.ui;

import org.baseagent.Patch;
import org.baseagent.grid.Grid;
import org.baseagent.grid.GridLayer;
import org.baseagent.ui.defaults.QuickSimulation;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameOfRain extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		QuickSimulation sim = new QuickSimulation(100, 100, 5, 5);
		
		sim.fill(GameOfRainThing.EMPTY, 0, 0, 100, 100);
		sim.fill(GameOfRainThing.CLOUD, 10, 0, 25, 10);
		sim.fill(GameOfRainThing.CLOUD, 40, 0, 55, 10);
		sim.scatter(GameOfRainThing.RAIN, 500, 0, 0, 100, 20); 
		sim.fill(GameOfRainThing.GROUND, 0, 90, 100, 100);
		
		Patch patch = new Patch() {
			@Override
			public void applyPatch(Grid grid, int x, int y) {
				GridLayer layer = sim.getDefaultGridLayer();

				layer.persist(GameOfRainThing.EMPTY, x, y);


				if ((layer.current().get(x, y) == GameOfRainThing.EMPTY) && (layer.current().get(x, y-1) == GameOfRainThing.CLOUD)) {
					if (Math.random() < 0.5) {
						layer.next().set(x, y, GameOfRainThing.RAIN);
					}
				}

				if (layer.current().get(x, y-1) == GameOfRainThing.RAIN) {
					layer.next().set(x, y, GameOfRainThing.RAIN);
				}

				layer.persist(GameOfRainThing.GROUND, x, y);
				layer.persist(GameOfRainThing.CLOUD, x, y);

			}
		};
		sim.add(patch);
		
        stage.setTitle("Game of Life");
        stage.setScene(new Scene(new Group(sim.getGridCanvas()), 500, 500));
        stage.show();
        
		sim.start();
	}
	
	public enum GameOfRainThing { EMPTY, CLOUD, RAIN, GROUND };
	
	public static void main(String[] args) {
		launch(args);
	}
}
