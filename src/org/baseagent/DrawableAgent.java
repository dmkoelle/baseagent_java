package org.baseagent;

import org.baseagent.ui.Drawable;
import org.baseagent.ui.SimulationCanvasContext;
import org.baseagent.ui.defaults.VisualizationLibrary;

import javafx.scene.paint.Color;

public class DrawableAgent extends Agent implements Drawable {
	private Color color;
	private Drawable drawable;
	private int drawX;
	private int drawY;

	public DrawableAgent() {
		super();
		setDrawable(new Drawable() {
			@Override
			public void draw(SimulationCanvasContext sc) {
				VisualizationLibrary.drawCircleForCell(sc, drawX, drawY, getColorOrUse(Color.CADETBLUE));
			}
		});	
	}
	
	public int getDrawX() {
		return drawX;
	}

	public void setDrawX(int drawX) {
		this.drawX = drawX;
	}

	public int getDrawY() {
		return drawY;
	}

	public void setDrawY(int drawY) {
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
	public void draw(SimulationCanvasContext sc) {
		if (drawable != null) {
			drawable.draw(sc);
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
