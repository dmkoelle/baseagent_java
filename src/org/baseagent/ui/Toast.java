package org.baseagent.ui;

import java.util.function.Predicate;

import org.baseagent.sim.Simulation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Toast {
	private long startTime = -1;
	private long endTime = -1;
	private Predicate<Simulation> activeCondition;
	private Predicate<Simulation> removeCondition;
	private int graphicX;
	private int graphicY;
	private int width;
	private int height;
	private String text;

	public Toast(String text, int graphicX, int graphicY, int width, int height) {
		this.text = text;
		this.graphicX = graphicX;
		this.graphicY = graphicY;
		this.width = width;
		this.height = height;
	}

	public Toast(long startTime, long endTime, String text, int graphicX, int graphicY, int width, int height) {
		this(text, graphicX, graphicY, width, height);
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public long getStartTime() {
		return this.startTime;
	}
	
	public long getEndTime() {
		return this.endTime;
	}
	
	public void activeWhen(Predicate<Simulation> activeCondition) {
		this.activeCondition = activeCondition;
	}
	
	public Predicate<Simulation> getActiveCondition() {
		return this.activeCondition;
	}
	
	public void removeWhen(Predicate<Simulation> removeCondition) {
		this.removeCondition = removeCondition;
	}
	
	public Predicate<Simulation> getRemoveCondition() {
		return this.removeCondition;
	}
	
	public boolean isActive(Simulation simulation) {
		if ((getStartTime() > -1) && (getEndTime() > -1)) {
			return (simulation.getStepTime() > getStartTime()) && (simulation.getStepTime() < getEndTime());
		}
		else if (getActiveCondition() != null) {
			return getActiveCondition().test(simulation);
		} 
		else return false;
	}

	public boolean readyToRemove(Simulation simulation) {
		if (getEndTime() > -1) {
			return (simulation.getStepTime() > getEndTime());
		}
		else if (getRemoveCondition() != null) {
			return getRemoveCondition().test(simulation);
		} 
		else return true;
	}

	public void draw(GridCanvasContext gcc) {
		GraphicsContext gc = gcc.getGraphicsContext();
		gc.setFill(Color.BLACK);
		gc.fillRect(graphicX+2, graphicY+2, graphicX+width+2,graphicY+height+2);
		gc.setFill(Color.GOLDENROD);
		gc.fillRect(graphicX, graphicY, graphicX+width,graphicY+height);
		gc.setFill(Color.BLACK);
		gc.setFont(Font.font(null, FontWeight.BOLD, 16));
		gc.fillText(text, graphicX+5, graphicY+20);
		System.out.println("Living toast! "+gcc.getSimulation().getStepTime());
	}
}
