package org.baseagent.ui.defaults;

import java.util.List;

import org.baseagent.grid.GridPosition;
import org.baseagent.ui.Drawable;
import org.baseagent.ui.GridCanvasContext;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

public class ConnectGridPositionsCustomDrawable implements Drawable {
	protected List<GridPosition> points;
	protected Paint stroke;
	protected double width;

	public ConnectGridPositionsCustomDrawable(List<GridPosition> points, Paint stroke, double width) {
		this.points = points;
		this.stroke = stroke;
		this.width = width;
	}
	
	@Override
	public void draw(GridCanvasContext gcc) {
		GraphicsContext gc = gcc.getGraphicsContext();
		gc.setStroke(stroke);
		gc.setLineWidth(width);
		for (int i=1; i < points.size(); i++) {
			gc.strokeLine(points.get(i-1).getCellX() * gcc.getXFactor() + (gcc.getCellWidth() / 2.0), points.get(i-1).getCellY() * gcc.getYFactor() + (gcc.getCellHeight() / 2.0), points.get(i).getCellX() * gcc.getXFactor() + (gcc.getCellWidth() / 2.0), points.get(i).getCellY() * gcc.getYFactor() + (gcc.getCellHeight() / 2.0));
		}
	}
}
