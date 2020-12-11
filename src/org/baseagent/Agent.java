package org.baseagent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.baseagent.behaviors.Behavior;
import org.baseagent.behaviors.ParallelBehaviorsPolicy;
import org.baseagent.comms.Message;
import org.baseagent.comms.MessageListener;
import org.baseagent.sim.Simulation;
import org.baseagent.sim.SimulationComponent;

/**
 * An important aspect about this general Agent is that the only thing it maintains is
 * internal state and knowledge. It does not specifically include those things that 
 * connect it to the world, whether that world is a Universe inside within BaseAgent 
 * 
 * @author David Koelle
 *
 */
public class Agent extends SimulationComponent implements MessageListener, HasStep {
	public static int SIMPLE_ID_COUNTER = 0;
	
	private int simpleId;
	private Inventory inventory;
	private Map<String, Object> knowledge;
	private Map<String, Object> properties;
	private Behavior currentBehavior; // TODO: Replace this with a BehaviorExecutor, which can select behaviors?
	private String simpleState;
	private Behavior behaviorPolicy;
	private List<Behavior> behaviors;

	public Agent() {
		super();
		initAgent();
	}
	
	public Agent(Behavior... behaviors) {
		this();
		
		for (Behavior behavior : behaviors) {
			this.addBehavior(behavior);
		}
	}

	public Agent(List<Behavior> behaviors) {
		this();
		
		for (Behavior behavior : behaviors) {
			this.addBehavior(behavior);
		}
	}

	private void initAgent() {
		this.simpleId = Agent.SIMPLE_ID_COUNTER++;
		this.inventory = new Inventory();
		this.knowledge = new HashMap<>();
		this.properties = new HashMap<>();
		this.behaviorPolicy = new ParallelBehaviorsPolicy();
		this.behaviors = new ArrayList<>();
	}
	
	public int getSimpleID() {
		return this.simpleId;
	}
	
	@Override
	public SimulationComponent.Type getType() {
		return SimulationComponent.Type.AGENT;
	}
	
	/**
	 *  Step can be overridden, but if it's not, it will do the current behavior
	 */
	@Override
	public void step(Simulation simulation) {
		behaviorPolicy.executeBehavior(this);
	}
	
	//
	// Properties subsystem
	//
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	
	public Map<String, Object> getProperties() {
		return this.properties;
	}
	

	//
	// Knowledge subsystem
	//
	public void setKnowledge(Map<String, Object> knowledge) {
		this.knowledge = knowledge;
	}
	
	public Map<String, Object> getKnowledge() {
		return this.knowledge;
	}

	//
	// State system
	//

	public Inventory getInventory() {
		return this.inventory;
	}

	public void give(Agent recipient, Object thing) {
		getInventory().remove(thing);
		recipient.receive(thing);
	}
	
	public void receive(Object thing) {
		getInventory().add(thing);
	}
	
	public void take(Agent takee, Object thing) {
		takee.getInventory().remove(thing);
		getInventory().add(thing);
	}

	//
	// State system
	//
	
	public boolean isState(String state) {
		return state.equals(simpleState);
	}
	
	public void setState(String state) {
		this.simpleState = state;
	}
	
	public String getState() {
		return this.simpleState;
	}
	
	//
	// Behaviors
	//
	
	public void addBehavior(Behavior behavior) {
		this.behaviors.add(behavior);
	}
	
	public void removeBehavior(Behavior behavior) {
		this.behaviors.remove(behavior);
	}
	
	public List<Behavior> getBehaviors() {
		return this.behaviors;
	}
	
	public void setBehaviorPolicy(Behavior behaviorPolicy) {
		this.behaviorPolicy = behaviorPolicy;
	}
	
	public Behavior getBehaviorPolicy() {
		return this.behaviorPolicy;
	}
	
	//
	// Message subsystem
	//
	
	/**
	 * By default, the Agent will be sure to pass along any messages
	 * to its behaviors that are also listening for messages.
	 * When you create a subclass of Agent, you may want to call
	 * super.onMessageReceived() or passMessageToBehaviors() 
	 * to continue this behavior.
	 */
	@Override
	public void onMessageReceived(Message message) { 
		passMessageToBehaviors(message);
	}
	
	protected void passMessageToBehaviors(Message message) {
		for (Behavior behavior : getBehaviors()) {
			if (behavior instanceof MessageListener) {
				((MessageListener)behavior).onMessageReceived(message);
			}
		}
	}

	/** Convenience method to access the communicator */
	public void sendMessage(Message message) {
		getSimulation().getCommunicator().sendMessage(message);
	}

	/** Convenience method to access the communicator */
	public void sendDirectedMessage(MessageListener recipient, Message message) {
		getSimulation().getCommunicator().sendDirectedMessage(recipient, message);
	}
}
