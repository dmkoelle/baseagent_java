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
	private long duration = 1L;
	private Predicate<Simulation> activeCondition;
	private Predicate<Simulation> removeCondition;
	private int graphicX;
	private int graphicY;
	private int width;
	private int height;
	private String text;
	private boolean needsTimes = true;
	
	public Toast(String text, int graphicX, int graphicY, int width, int height) {
		this.text = text;
		this.graphicX = graphicX;
		this.graphicY = graphicY;
		this.width = width;
		this.height = height;
	}

	public Toast(String text, int graphicX, int graphicY, int width, int height, long duration) {
		this(text, graphicX, graphicY, width, height);
		setDuration(duration);
	}

	public Toast(long startTime, long endTime, String text, int graphicX, int graphicY, int width, int height) {
		this(text, graphicX, graphicY, width, height);
		this.startTime = startTime;
		this.endTime = endTime;
		this.duration = endTime - startTime;
		this.needsTimes = false;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
		if (getDuration() != 0) {
			this.endTime = startTime + getDuration();
		} 
		needsTimes = false;
	}

	public long getStartTime() {
		return this.startTime;
	}
	
	public void setEndTime(long endTime) {
		this.endTime = endTime;
		if (getDuration() != 0) {
			this.startTime = endTime - getDuration();
		} 
		needsTimes = false;
	}
	
	public long getEndTime() {
		return this.endTime;
	}
	
	public void setDuration(long duration) {
		if (this.getStartTime() != -1) {
			setEndTime(getStartTime() + duration);
			needsTimes = false;
		}
		else if (this.getEndTime() != -1) {
			setStartTime(getEndTime() - duration);
			needsTimes = false;
		}
		this.duration = duration;
	}
	
	public long getDuration() {
		return this.duration;
	}
	
	public boolean needsTimes() {
		return this.needsTimes;
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
		System.out.print("Toast: Testing if this toast is active. At timestep "+simulation.getStepTime()+" it...");
		if ((getStartTime() > -1) && (getEndTime() > -1)) {
			System.out.println("might be if its time is current: " + ((simulation.getStepTime() > getStartTime()) && (simulation.getStepTime() < getEndTime())));
			return (simulation.getStepTime() > getStartTime()) && (simulation.getStepTime() < getEndTime());
		}
		else if (getActiveCondition() != null) {
			System.out.println("might be if its active condition tests: " +getActiveCondition().test(simulation));
			return getActiveCondition().test(simulation);
		} 
		else {
			System.out.println("is not.");
			return false;
		}
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
		gc.setFill(Color.YELLOW);
		gc.fillRect(graphicX, graphicY, width, height);
		gc.setFill(Color.BLACK);
		gc.setFont(Font.font(null, FontWeight.BOLD, 16));
		gc.fillText(text, graphicX+5, graphicY+20);
		System.out.println("Living toast! "+gcc.getSimulation().getStepTime());
	}
}
