package org.baseagent.comms;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Message {
	private UUID uuid;
	private MessageListener originalSender;
	private MessageListener currentSender;
	private String messageType;
	private Map<String, Object> payload;
	
	public Message() {
		this.uuid = UUID.randomUUID();
	}

	public Message(MessageListener currentSender, String messageType) {
		this();
		this.originalSender = currentSender;
		this.currentSender = currentSender;
		this.messageType = messageType;
	}

	public Message(MessageListener currentSender, String messageType, Map<String, Object> payload) {
		this(currentSender, messageType);
		setPayload(payload);
	}

	public Message(MessageListener originalSender, MessageListener currentSender, String messageType, Map<String, Object> payload) {
		this(currentSender, messageType, payload);
		this.originalSender = originalSender;
	}

	public void setSender(MessageListener sender) {
		this.currentSender = sender;
	}
	
	public MessageListener getSender() {
		return this.currentSender;
	}
	
	public void setOriginalSender(MessageListener sender) {
		this.originalSender = sender;
	}
	
	public MessageListener getOriginalSender() {
		return this.originalSender;
	}
	
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	
	public String getMessageType() {
		return this.messageType;
	}
	
	public UUID getUUID() {
		return this.uuid;
	}
	
	public void setPayload(Map<String, Object> payload) {
		this.payload = payload;
	}
	
	public Map<String, Object> getPayload() {
		return this.payload;
	}
	
	@Override 
	public int hashCode() {
		return uuid.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof Message)) return false;
		Message m2 = (Message)o;
		return (this.uuid.equals(m2.uuid));
	}
	
	@Override
	public String toString() {
		return super.toString() + " (Sender: "+originalSender.toString()+", Message Type: "+messageType+")";
	}
	
	/** 
	 * Generates a new Message object that is a fresh Message object but looks the same as an existing message. 
	 * (You are responsible for updating the Payload - the returned message will have an empty payload)
	 */
	public Message createCopy() {
		Message message = new Message();
		message.uuid = this.uuid;
		message.originalSender = this.originalSender;
		message.currentSender = this.currentSender;
		message.messageType = this.messageType;
		message.payload = new HashMap<>();
		return message;
	}
}
