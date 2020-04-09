package org.baseagent.behaviors;

import java.util.HashMap;
import java.util.Map;

import org.baseagent.Agent;
import org.baseagent.comms.Message;
import org.baseagent.comms.MessageListener;

public class SwitchingBehavior implements Behavior, MessageListener {
	Map<String, Map<String, Behavior>> allBehaviors;
	Map<String, String> activeIdentifiers;
	
	public SwitchingBehavior() {
		this.allBehaviors = new HashMap<>();
		this.activeIdentifiers = new HashMap<>();
	}
	
	public void addBehavior(String category, String identifier, Behavior behavior) {
		Map<String, Behavior> categoryBehaviors = allBehaviors.get(category);
		if (categoryBehaviors == null) {
			categoryBehaviors = new HashMap<String, Behavior>();
			allBehaviors.put(category, categoryBehaviors);
		}
		categoryBehaviors.put(identifier, behavior);
		
		// Add a default active identifier
		if (activeIdentifiers.get(category) == null) {
			activeIdentifiers.put(category, identifier);
		}
	}
	
	public void switchBehavior(String category, String identifier) {
		activeIdentifiers.put(category, identifier);
	}

	@Override
	public void executeBehavior(Agent agent) {
		for (Map.Entry<String, String> activeIdentifiers : activeIdentifiers.entrySet()) {
			Behavior behavior = allBehaviors.get(activeIdentifiers.getKey()).get(activeIdentifiers.getValue());
			behavior.executeBehavior(agent);
		}
	}

	@Override
	public void onMessageReceived(Message message) {
		for (Map.Entry<String, String> activeIdentifiers : activeIdentifiers.entrySet()) {
			Behavior behavior = allBehaviors.get(activeIdentifiers.getKey()).get(activeIdentifiers.getValue());
			if (behavior instanceof MessageListener) {
				((MessageListener)behavior).onMessageReceived(message);
			}
		}	
	}
}
