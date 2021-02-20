package org.baseagent.apps.exper;

import java.util.ArrayList;
import java.util.List;

import org.baseagent.grid.Grid;
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
        Simulation simulation = setupSimulation();
        final GridCanvas canvas = setupGridCanvas(simulation);

        primaryStage.setTitle("S1");
        primaryStage.setScene(new Scene(new Group(canvas), 500, 500));
        primaryStage.show();

        simulation.start();
	}
	
	protected Simulation setupSimulation() {
		Simulation simulation = new Simulation();
		Grid grid = new Grid(100, 100);
		simulation.setUniverse(grid);
		
		for (int i=0; i < 100; i++) {
			GridAgent drone = new ExperimentAgent();
			simulation.addSimulationComponent(drone);
			drone.placeRandomly();
		}

		simulation.endWhen(sim -> sim.getStepTime() == 500);
		simulation.setDelayAfterEachStep(100);

		return simulation;
	}
	
	protected GridCanvas setupGridCanvas(Simulation simulation) {
		final GridCanvas canvas = new GridCanvas(simulation, (Grid)simulation.getUniverse(), 5, 5);
		List<Color> colors = new ArrayList<>();
		colors.add(Color.SEASHELL);
		colors.add(Color.BLUEVIOLET);
		colors.add(Color.MAGENTA);
		canvas.getGridCanvasContext().setColorPalette(colors);
		canvas.addToast(new Toast(10, 100, "Hello there!", 80, 100, 40, 10));
		return canvas;
	}
}
