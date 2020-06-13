package org.baseagent.examples.ui;

import org.baseagent.Agent;
import org.baseagent.behaviors.Behavior;
import org.baseagent.grid.Grid;
import org.baseagent.grid.GridLayer;
import org.baseagent.sim.GridAgent;
import org.baseagent.sim.Simulation;
import org.baseagent.ui.GridCanvas;
import org.baseagent.ui.defaults.ColorFunctionGridCellRenderer;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class UiTermites extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		Simulation simulation = new Simulation();
		Grid grid = new Grid(500, 500);
		simulation.setUniverse(grid);
		simulation.endWhen(sim -> sim.getStepTime() == 5000000);
		
		GridLayer woodchipLayer = grid.createGridLayer("woodchips");
		woodchipLayer.fill("");
		woodchipLayer.scatter("woodchip", 10000);
		woodchipLayer.setShouldSwitch(false);
		
		for (int i=0; i < 1000; i++) {
			GridAgent termite = new GridAgent(new TermiteBehavior());
			simulation.addSimulationComponent(termite);
			termite.placeRandomly();
		}
		
		GridCanvas canvas =  new GridCanvas(simulation, grid, 1, 1);
		canvas.addGridLayerRenderer("woodchips", new ColorFunctionGridCellRenderer(object -> ((String)object).equals("woodchip") ? Color.YELLOW : Color.BLACK));

		int width = 500;
		int height = 500;
        stage.setTitle("Termites");
        stage.setScene(new Scene(new Group(canvas), width, height));
        stage.show();

        simulation.start();
	}
	
	class TermiteBehavior implements Behavior {
		private Task currently = Task.WANDERING;
		int wanderingTimer = 0;
		
		@Override
		public void executeBehavior(Agent agent) {
			GridAgent termite = (GridAgent)agent;

			// If the termite is wandering with mouth empty and there's a woodchip where it's located, take the woodchip
			if ((currently == Task.WANDERING) && termite.getObjectFromLayer("woodchips").equals("woodchip")) {
				termite.take("woodchips", "woodchip", "");
				currently = Task.CARRYING;
				termite.setColor(Color.PINK);
			}
			
			// Otherwise, if the termite is carrying a woodchip and there's also a woodchip where the termite is located, be ready to drop the woodchip
			else if ((currently == Task.CARRYING) && termite.getObjectFromLayer("woodchips").equals("woodchip")) {
				currently = Task.READY_TO_DROP;
				termite.setColor(Color.RED);
			}
			
			// Otherwise, if the termite is ready to drop the woodchip and the space where the termite is located is empty, drop the woodchip and go back to wandering
			else if ((currently == Task.READY_TO_DROP) && termite.getObjectFromLayer("woodchips").equals("")) {
				termite.drop("woodchips", "woodchip");
				currently = Task.START_WANDERING;
				wanderingTimer = 5;
				termite.setColor(Color.GREEN);
			}
			
			else if ((currently == Task.START_WANDERING) ) {
				wanderingTimer--;
				if (wanderingTimer == 0) {
					currently = Task.WANDERING;
					termite.setColor(Color.CYAN);
				}
			}
			
			termite.moveRandomly();
		}
	}

	enum Task { WANDERING, CARRYING, READY_TO_DROP, START_WANDERING };
}
