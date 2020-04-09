package org.baseagent.examples;

import org.baseagent.Agent;
import org.baseagent.Beacon;
import org.baseagent.Environment;
import org.baseagent.behaviors.Behavior;
import org.baseagent.embodied.effectors.EmbodiedEffector;
import org.baseagent.embodied.sensors.EmbodiedSensor;
import org.baseagent.grid.GridLayer;
import org.baseagent.sim.Simulation;
import org.baseagent.statemachine.StateMachine;
import org.baseagent.ui.GridCellRenderer;
import org.baseagent.ui.GridCanvas;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Sepia extends BaseAgentApplication {
	@Override
	protected String getTitle() { return "SEPIA"; }
	
	@Override
	protected Simulation setupSimulation() {
		Simulation simulation = new Simulation(100, 100);
		
		GridLayer vibrationLayer = simulation.getGrid().createLayer("vibrations");
		GridLayer lampLayer = simulation.getGrid().createLayer("lamps");
		GridLayer vehicleLayer = simulation.getGrid().createLayer("vehicles");

		lampLayer.place(20, 20, new Beacon("red_lamp", "light.red"));
		lampLayer.place(80, 80, new Beacon("blue_lamp", "light.blue"));

		Agent sepia1 = new Agent("sepia1");
		EmbodiedSensor sensor1 = new EmbodiedSensor("s1", "ligt");
		EmbodiedSensor sensor2 = new EmbodiedSensor("s2", "light");
		EmbodiedEffector leftWheel = new EmbodiedEffector("lw", "left_wheel");
		EmbodiedEffector rightWheel = new EmbodiedEffector("rw", "right_wheel");
		// The sensors and effectors can belong to a Network. Once they're assigned to a Network,
		// they can't be assigned to another Network. Or they get the network, if not null, from 
		// the connectee. Lazy network creation.
		sensor1.connectTo(leftWheel, d -> d);
		sensor2.connectTo(rightWheel, d -> 1.0 - d);
		sepia1.getBody().form(
				"s1  .  s2",
				".   .   .",
				"lw  .  rw");
		
		// Should each of these things implement Drawable and just give an empty implementation
		// Plus a setDrawable method
		// NOW, Agent needs to get these sensors/effectors added to itself
		
		vehicleLayer.place(50, 50, sepia1);  // should this automatically add a simulation component?
		
		Environment vibrations = new Environment() {
			@Override
			public void step(Simulation sim) {
				super.step(sim);
				for (Agent vehicle : vehicleLayer.getResidents()) {
					WavePropagation wp = new WavePropagation();
					vibrationLayer.place(wp, vehicle.currentX(), vehicle.curentY());
				}
			}
		};
		
		simulation.endWhen(sim -> sim.getStepTime() == 10000);
		simulation.setDelayAfterEachStep(50);

		return simulation;
	}
	
	@Override
	protected GridCanvas setupSimulationCanvas(Simulation simulation, int width, int height) {
		GridCanvas canvas = new GridCanvas(simulation, width, height);
		canvas.addGridLayerRenderer("woodchips", new GridCellRenderer() {
			@Override
			public void drawCell(GraphicsContext gc, Object woodchip, double x, double y, double width, double height) {
				if (woodchip.equals("woodchip")) {
					gc.setFill(Color.YELLOW);
					gc.fillOval(x, y, width, height);
				}
			}
		});
		return canvas;
	}
	
	private Behavior woodchipStateBehavior() {
		StateMachine woodchipStateMachine = new StateMachine();
		woodchipStateMachine.addState("empty");
		woodchipStateMachine.addState("carrying woodchip");
		woodchipStateMachine.addTransition("empty", "carrying woodship", termite -> ((Agent)termite).currentLocationIn("woodchips").equals("woodchip"), termite -> termite.take("woodchips", "woodchip"));
		woodchipStateMachine.addTransition("carrying woodship", "empty", termite -> termite.currentLocationIn("woodchip"), termite -> termite.drop("woodchips", "woodchip"));
		woodchipStateMachine.setCurrentState("empty");
		return woodchipStateMachine;
	}
}

