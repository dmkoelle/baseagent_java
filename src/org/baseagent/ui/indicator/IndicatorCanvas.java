package org.baseagent.ui.indicator;

import java.util.ArrayList;
import java.util.List;

import org.baseagent.sim.Simulation;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class IndicatorCanvas extends Canvas {
	private Simulation simulation;
	private List<Indicator> indicators;
	
	public IndicatorCanvas(Simulation simulation, int width, int height) {
		super(width, height);
		this.simulation = simulation;
		this.indicators = new ArrayList<>();
		
		AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				IndicatorCanvas.this.update();
			}
		};
		timer.start();
	}
	
	public Simulation getSimulation() {
		return this.simulation;
	}
	
	public void addIndicator(Indicator indicator) {
		indicators.add(indicator);
	}
	
	public void removeIndicator(Indicator indicator) {
		indicators.remove(indicator);
	}
	
	public List<Indicator> getIndicators() {
		return this.indicators;
	}

	public void update() {
		GraphicsContext gc = this.getGraphicsContext2D();
		
		// Clear everything
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		// Draw the indicators
		indicators.stream().forEach(indicator -> indicator.draw(gc));
	}
}
