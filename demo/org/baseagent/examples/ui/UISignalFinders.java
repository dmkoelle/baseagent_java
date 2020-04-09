package org.baseagent.examples.ui;

import java.awt.event.MouseEvent;

import org.baseagent.Agent;
import org.baseagent.Container;
import org.baseagent.behaviors.Behavior;
import org.baseagent.behaviors.grid.FollowGradientBehavior;
import org.baseagent.behaviors.grid.RandomWalkBehavior;
import org.baseagent.comms.Message;
import org.baseagent.signals.Signal;
import org.baseagent.sim.Simulation;
import org.baseagent.sim.SimulationComponent;


/**
 * In this simulation, a group of agents are searching the grid for a signal,
 * which the user places. Once one of the agents starts to smell the signal, 
 * it sends a broadcast message to the others so they'll converge on the area.
 * 
 * BaseAgent Capabilities Demonstrated:
 * - Behaviors
 * - State
 * - Messages
 * 
 * @author David Koelle
 *
 */
public class UiSignalFinders {
	public static void main(String[] args) {
		Simulation simulation = new Simulation(100, 100);
		
		GridLayer<SignalFinder> signalFinderLayer = new GridLayer<>();
		signalFinderLayer.scatter(SignalFinder.class, 100);
		simulation.getGrid().addLayer(signalFinderLayer);

		GridLayer<Signal> signalLayer = new GridLayer<>();
		signalLayer.addHumanInteraction(new SignalDropper());
		simulation.getGrid().addLayer(signalLayer);
	}
	
	class SignalFinder extends Agent {
		public SignalFinder() {
			super();
			EmbodiedSensor alphiumSensor = new EmbodiedSensor(new Signal("alphium"), s -> onAlphiumFound());
			Behavior followGradientBehavior = new FollowGradientBehavior(alphiumSensor);
			Behavior randomWalkBehavior = new RandomWalkBehavior();
			Behavior goToBehavior = new GoToBehavior();
			Behavior plantFlagBehavior = new Behavior() {
				@Override
				public void step(SimulationComponent simulationComponent) {
					simulationComponent.getSimulation().getGrid().place(simulationComponent.get("X"), simulationComponent.get("Y"), "flag");
				}
			};
			
			addStateBehavior("search", randomWalkBehavior);
			addStateBehavior("follow gradient", followGradientBehavior);
			addStateBehavior("go to", goToBehavior);
			addStateCompletionTransition("go to", "follow gradient");
			addStateCompletionTransition("follow gradient", "plant flag");
			addStateCompletionTransition("plant flag", "search");
			setState("search");
		}

		private void onAlphiumFound() {
			sendMessage(new Message(I_FOUND_SOMETHING, Container.create(this, "X", "Y")));
		}
		
		@Override
		public void onMessageReceived(Message message) {
			if (message.is(I_FOUND_SOMETHING)) {
				setState("go to", message.getPayload()); // State and parameters for behavior
			}
		}
		
		public static final String I_FOUND_SOMETHING = "I_FOUND_SOMETHING";
	}

	class SignalDropper extends HumanInteraction {
		public SignalDropper() {
			super();
			
		}
		
		@Override
		public void onMouseClicked(MouseEvent event) {
//			place("alphium", )
		}
	}
}
