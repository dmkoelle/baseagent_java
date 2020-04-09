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
		Grid termiteGrid = new Grid(100, 100);
		simulation.setUniverse(termiteGrid);
		simulation.endWhen(sim -> sim.getStepTime() == 5000000);
//		simulation.setDelayAfterEachStep(100);
		
		GridLayer woodchipLayer = termiteGrid.createGridLayer("woodchips");
		woodchipLayer.fill("");
		woodchipLayer.scatter("woodchip", 1000);
		woodchipLayer.setShouldSwitch(false);
		
		for (int i=0; i < 1000; i++) {
			GridAgent termite = new GridAgent(new TermiteBehavior());
			simulation.addSimulationComponent(termite);
			termite.placeRandomly();
		}
		
		GridCanvas canvas =  new GridCanvas(simulation, 5, 5);
		canvas.addGridLayerRenderer("woodchips", new ColorFunctionGridCellRenderer(object -> ((String)object).equals("woodchip") ? Color.YELLOW : Color.BLACK));

		int width = 500;
		int height = 500;
        stage.setTitle("Termites");
        stage.setScene(new Scene(new Group(canvas), width, height));
        stage.show();

        simulation.start();
		
	}
	
	class TermiteBehavior implements Behavior {
		private String currently = "wandering";
		
		@Override
		public void executeBehavior(Agent agent) {
			GridAgent termite = (GridAgent)agent;
			if (currently.equals("wandering") && termite.getObjectFromLayer("woodchips").equals("woodchip")) {
				termite.take("woodchips", "woodchip", "");
				currently = "carrying";
				termite.setColor(Color.PINK);
			}
			else if (currently.equals("carrying") && termite.getObjectFromLayer("woodchips").equals("woodchip")) {
				currently = "ready_to_drop";
				termite.setColor(Color.RED);
			}
			else if (currently.equals("ready_to_drop") && termite.getObjectFromLayer("woodchips").equals("")) {
				termite.drop("woodchips", "woodchip");
				currently = "wandering";
				termite.moveRandomly();
				termite.setColor(Color.CYAN);
			} 
			termite.moveRandomly();
		}
	}
}
