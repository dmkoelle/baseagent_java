package org.baseagent.particles;

import java.util.Map;

import org.baseagent.HasStep;
import org.baseagent.grid.GridLayer;
import org.baseagent.grid.HasFineGridPosition;
import org.baseagent.grid.HasGridPosition;
import org.baseagent.sim.Simulation;

import javafx.scene.paint.Color;

public class Particle implements HasFineGridPosition, HasGridPosition {
	private double fineX;
	private double fineY;
	private double velocity;
	private double direction;
	private double size;
	private Color color;
	private Map<String, Object> knowledge;

	public Particle(double x, double y, double size, Color color) {
		this.fineX = x;
		this.fineY = y;
		this.velocity = 0.0;
		this.direction = 0.0;
		this.size = size;
		this.color = color;
	}
	

	@Override
	public void setCellX(int x) {
		this.cellX = x;
		this.fineX = x;
	}
	
	@Override
	public void setCellY(int y) {
		this.cellY = y;
		this.fineY = y;
	}
	
	@Override
	public void setFineX(double x) {
		this.fineX = x;
		this.cellX = (int)Math.round(x);
	}
	
	@Override
	public void setFineY(double y) {
		this.fineY = y;
		this.cellY = (int)Math.round(y);
	}
	
	public void setHeading(double heading) {
		this.heading = heading;
	}

	@Override
	public int getCellX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCellY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public GridLayer getGridLayer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getHeading() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getFineX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getFineY() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
