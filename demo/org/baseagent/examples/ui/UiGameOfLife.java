package org.baseagent.examples.ui;

import java.awt.Color;
import java.awt.Graphics2D;

import org.baseagent.Environment;
import org.baseagent.grid.GridLayer;
import org.baseagent.sim.Simulation;
import org.baseagent.ui.GridLayerRenderer;
import org.baseagent.ui.GridCanvas;

import javafx.application.Application;
import javafx.stage.Stage;

public class UiGameOfLife extends Application {
	private GridLayer<Boolean> layer;

	@Override
	public void start(Stage stage) throws Exception {
		Simulation simulation = new Simulation(100, 100);
		this.layer = new GridLayer<>();
		layer.fill(Boolean.FALSE);  // It's like it's an unnamed layer
		layer.scatter(Boolean.TRUE, 100); // We don't want to scatter that single instance, but we need to parameterize it...
		GridCanvas canvas = new GridCanvas(simulation);
		canvas.setGridLayerRenderer(layer, new GridLayerRenderer<Boolean>() {
			@Override
			public void draw(Graphics2D g2, int width, int height, Boolean value) {
				if (value == Boolean.TRUE) {
					g2.setColor(Color.YELLOW);
					g2.fillRect(1, 1, width-1, height-1);
				}
			}
		});
		
		Environment env = new GameOfLifeEnvironment();
		simulation.addSimulationComponent(env);
	}

	class GameOfLifeEnvironment extends Environment {
		public GameOfLifeEnvironment() {
			super();
		}
		
		@Override
		public void step() {
			layer.forEachCellIn(0, 0, getWidth(), getHeight(), f -> {
				int count = getSimulation().getGrid().countInArea(i-1, u-1, i+1, u+1);
			    if ((count == 2) || (count == 3)) {
			    	getSimulation().getNextGrid().put(i, u, 1);
			    } else {
			    	getSimulation().getNextGrid().put(i, u, 0);
			    }
			} );
		}
	}
}
