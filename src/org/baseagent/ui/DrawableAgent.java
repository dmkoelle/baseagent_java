package org.baseagent.ui;

import org.baseagent.Agent;
import org.baseagent.ui.defaults.VisualizationLibrary;

import javafx.scene.paint.Color;

public class DrawableAgent extends Agent implements Drawable {
	private Color color;
	private Drawable drawable;
	private double drawX;
	private double drawY;

	public DrawableAgent() {
		super();
		setDrawable(new Drawable() {
			@Override
			public void draw(GridCanvasContext gcc) {
				VisualizationLibrary.drawCircleForCell(gcc, (int)Math.round(drawX), (int)Math.round(drawY), getColorOrUse(Color.CADETBLUE));
			}
		});	
	}
	
	public double getDrawX() {
		return drawX;
	}

	public void setDrawX(double drawX) {
		this.drawX = drawX;
	}

	public double getDrawY() {
		return drawY;
	}

	public void setDrawY(double drawY) {
		this.drawY = drawY;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public Color getColorOrUse(Color defaultColor) {
		return (color == null) ? defaultColor : this.color;
	}
	
	//
	// Drawable
	//
	
	@Override
	public void draw(GridCanvasContext gcc) {
		if (drawable != null) {
			drawable.draw(gcc);
		}
	}

	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}
	
	public Drawable getDrawable() {
		return this.drawable;
	}
	
	public void removeDrawable() {
		this.drawable = null;
	}

}
