package org.baseagent.test_examples;

import org.baseagent.Agent;
import org.baseagent.behaviors.Behavior;
import org.baseagent.comms.Message;
import org.baseagent.examples.BaseAgentApplication;
import org.baseagent.sim.Simulation;
import org.baseagent.statemachine.StateMachine;
import org.baseagent.ui.GridCellRenderer;
import org.baseagent.ui.GridCanvas;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CommunicatingAgents extends BaseAgentApplication {

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	protected String getTitle() { return "Comunicating Agents Test Example"; }
	
	@Override
	protected Simulation setupSimulation() {
		Simulation simulation = new Simulation(100, 100);
		
		simulation.getGrid().createGridLayer("agents");

		for (int i=0; i < 1000; i++) {
			Agent agent = createBroadcastingAgent();
			simulation.addSimulationComponent(agent);
		}
		
		simulation.endWhen(sim -> sim.getStepTime() == 5000);
		simulation.setDelayAfterEachStep(50);

		return simulation;
	}
	
	@Override
	protected GridCanvas setupSimulationCanvas(Simulation simulation, int width, int height) {
		GridCanvas canvas = new GridCanvas(simulation, width, height);
		canvas.addGridLayerRenderer("color", new GridCellRenderer() {
			@Override
			public void drawCell(GraphicsContext gc, Object color, double x, double y, double width, double height) {
				gc.setFill((Color)color);
				gc.fillRect(x, y, width, height);
			}
		});
		return canvas;
	}
	
	private Agent createBroadcastingAgent() {
		Agent agent = new Agent();
		agent.addBehavior(new Behavior() {
			@Override
			public void executeBehavior(Agent agent) {
				if (agent.getSimulation().getStepTime() % 60 == 0) {
					agent.sendMessage(new Message(agent, agent.getSimulation().getStepTime()+ " and all is well!"));
				}
			}
		});
		return agent;
	}
}
