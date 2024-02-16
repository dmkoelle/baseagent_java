package org.baseagent.particles;

import org.baseagent.grid.Grid;
import org.baseagent.ui.GridLayerRenderer;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ParticleCanvas extends Canvas {
	private ParticleSpace particleSpace;
	
	public ParticleCanvas(ParticleSpace particleSpace) {
		this.particleSpace = particleSpace;
	}
	
	public void update() {
		GraphicsContext gc = this.getGraphicsContext2D();
		
		// Clear everything
		Color backgroundColor = Color.BLACK;
		gc.setFill(backgroundColor);
		gc.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		for (Particle particle : particleSpace.getParticles()) {
			gc.setFill(particle.getColor());
			gc.fillOval(particle.getX(), particle.getY(), particle.getSize(), particle.getSize());
		}
	}
}
