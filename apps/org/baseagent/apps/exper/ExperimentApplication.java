package org.baseagent.apps.exper;

import org.baseagent.sim.GridAgent;
import org.baseagent.sim.Simulation;
import org.baseagent.ui.GridCanvas;
import org.baseagent.ui.Toast;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ExperimentApplication extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
    public void start(Stage primaryStage) {
		int width = 500;
		int height = 500;
		
        Simulation simulation = setupSimulation();
        final GridCanvas canvas = setupSimulationCanvas(simulation, width, height);

        primaryStage.setTitle("S1");
        primaryStage.setScene(new Scene(new Group(canvas), width, height));
        primaryStage.show();

        simulation.start();
	}
	
	protected Simulation setupSimulation() {
		Simulation simulation = new Simulation(100, 100);
		
		for (int i=0; i < 100; i++) {
			GridAgent drone = new ExperimentAgent();
			simulation.addSimulationComponent(drone);
			drone.placeRandomly();
		}

		simulation.endWhen(sim -> sim.getStepTime() == 50);
		simulation.setDelayAfterEachStep(100);

		return simulation;
	}
	
	protected GridCanvas setupSimulationCanvas(Simulation simulation, int width, int height) {
		final GridCanvas canvas = new GridCanvas(simulation, width, height);
		canvas.getSimulationCanvasContext().setColorPalette(Color.SEASHELL, Color.BLUEVIOLET, Color.MAGENTA);
		canvas.addToast(new Toast(10, 50, "You there!", 80, 100, 40, 20));
		return canvas;
	}
}
