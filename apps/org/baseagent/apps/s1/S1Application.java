package org.baseagent.apps.s1;

import org.baseagent.grid.Grid;
import org.baseagent.sim.GridAgent;
import org.baseagent.sim.Simulation;
import org.baseagent.sim.SimulationListener;
import org.baseagent.ui.GridCanvas;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class S1Application extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
    public void start(Stage primaryStage) {
		int width = 500;
		int height = 500;
		
        Simulation simulation = setupSimulation();

        final GridCanvas canvas = setupSimulationCanvas(simulation);

        primaryStage.setTitle("S1");
        primaryStage.setScene(new Scene(new Group(canvas), width, height));
        primaryStage.show();

        simulation.start();
	}
	
	protected Simulation setupSimulation() {
		Simulation simulation = new Simulation();
        Grid grid = new Grid(500, 500);
        simulation.setUniverse(grid);
		
		for (int i=0; i < 100; i++) {
			GridAgent drone = new S1Agent();
			simulation.addSimulationComponent(drone);
			drone.placeRandomly();
		}
		
		simulation.endWhen(sim -> sim.getStepTime() == 5000);
		simulation.setDelayAfterEachStep(100);

		return simulation;
	}
	
	protected GridCanvas setupSimulationCanvas(Simulation simulation) {
		final GridCanvas canvas = new GridCanvas(simulation);
//		simulation.addSimulationListener(new SimulationListener() {
//			@Override
//			public void onStepEnded(Simulation simulation) {
//				takeSnapshot(canvas);
//			}
//		});
		canvas.getSimulationCanvasContext().setColorPalette(Color.SEASHELL, Color.BLUEVIOLET, Color.MAGENTA);
		return canvas;
	}
	
	private void takeSnapshot(GridCanvas canvas) {
//		canvas.saveSnapshot("S1App");
	}
}
