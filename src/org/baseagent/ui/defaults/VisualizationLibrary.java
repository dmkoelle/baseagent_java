package org.baseagent.ui.defaults;


import java.util.List;

import org.baseagent.embodied.ConnectedComponent;
import org.baseagent.embodied.EmbodiedAgent;
import org.baseagent.embodied.effectors.EmbodiedEffector;
import org.baseagent.embodied.sensors.EmbodiedSensor;
import org.baseagent.grid.Grid;
import org.baseagent.grid.HasFineGridPosition;
import org.baseagent.grid.HasGridPosition;
import org.baseagent.ui.GridCanvasContext;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

public class VisualizationLibrary {
	public static void drawTriangleWithHeading(GraphicsContext graphics, double x, double y, int cellWidth, int cellHeight, double headingInRadians, Color fill, Color stroke) {
		double centerX = x + cellWidth * 0.5;
		double centerY = y + cellHeight * 0.5;
		double tipX = centerX + cellWidth * 0.5 * Math.cos(headingInRadians);
		double tipY = centerY + cellHeight * 0.5 * Math.sin(headingInRadians);
		
		double shortestExtent = Math.min(cellWidth / 2.0, cellHeight / 2.0);
		double backLeftX = centerX + shortestExtent * Math.cos(headingInRadians + Math.toRadians(155));
		double backLeftY = centerY + shortestExtent * Math.sin(headingInRadians + Math.toRadians(155));
		double backRightX = centerX + shortestExtent * Math.cos(headingInRadians + Math.toRadians(205));
		double backRightY = centerY + shortestExtent * Math.sin(headingInRadians + Math.toRadians(205));
		double[] xPoints = new double[] { tipX, backLeftX, backRightX };
		double[] yPoints = new double[] { tipY, backLeftY, backRightY };
		
		graphics.setFill(fill);
		graphics.setStroke(stroke);
		graphics.fillPolygon(xPoints, yPoints, 3);
		graphics.strokePolygon(xPoints, yPoints, 3);
	}
	
	public static void drawCircle(GraphicsContext graphics, double centerX, double centerY, double xRadius, double yRadius, Color stroke) {
		graphics.setStroke(stroke);
		graphics.strokeOval(centerX - xRadius, centerY - yRadius, xRadius * 2, yRadius * 2);
	}

	public static void drawCircleForCell(GridCanvasContext gcc, int cellX, int cellY, Color stroke) {
		VisualizationLibrary.drawCircle(gcc.getGraphicsContext(), cellX * (gcc.getCellWidth() + gcc.getCellXSpacing()), cellY * (gcc.getCellHeight() + gcc.getCellYSpacing()), gcc.getCellWidth() / 2.0, gcc.getCellHeight() / 2.0, stroke);
	}
	
	public static void drawCircleForCell(GridCanvasContext gcc, int cellX, int cellY, double magnifier, Color stroke) {
		VisualizationLibrary.drawCircle(gcc.getGraphicsContext(), cellX * (gcc.getCellWidth() + gcc.getCellXSpacing()), cellY * (gcc.getCellHeight() + gcc.getCellYSpacing()), (gcc.getCellWidth() / 2.0) * magnifier, (gcc.getCellHeight() / 2.0) * magnifier, stroke);
	}

	public static void fillCircle(GraphicsContext graphics, double centerX, double centerY, double xRadius, double yRadius, Color stroke, Color fill) {
		graphics.setFill(fill);
		graphics.fillOval(centerX - xRadius, centerY - yRadius, xRadius * 2, yRadius * 2);
		graphics.setStroke(stroke);
		graphics.strokeOval(centerX - xRadius, centerY - yRadius, xRadius * 2, yRadius * 2);
	}

	public static void fillCircleForCell(GridCanvasContext gcc, int cellX, int cellY, Color stroke, Color fill, double margin) {
		VisualizationLibrary.fillCircle(gcc.getGraphicsContext(), cellX * (gcc.getCellWidth() + gcc.getCellXSpacing()) + gcc.getCellWidth()/2.0, cellY * (gcc.getCellHeight() + gcc.getCellYSpacing()) + gcc.getCellHeight()/2.0, gcc.getCellWidth() / 2.0 - margin, gcc.getCellHeight() / 2.0 - margin, stroke, fill);
	}
	
	public static void fillCircleForCell(GridCanvasContext gcc, int cellX, int cellY, double magnifier, Color stroke, Color fill, double margin) {
		VisualizationLibrary.fillCircle(gcc.getGraphicsContext(), cellX * (gcc.getCellWidth() + gcc.getCellXSpacing()), cellY * (gcc.getCellHeight() + gcc.getCellYSpacing()), (gcc.getCellWidth() / 2.0) * magnifier - margin, (gcc.getCellHeight() / 2.0) * magnifier - margin, stroke, fill);
	}

	public static void fillRect(GraphicsContext graphics, double x, double y, double width, double height, Color stroke, Color fill) {
		graphics.setFill(fill);
		graphics.fillRect(x, y, width, height);
		graphics.setStroke(stroke);
		graphics.setLineWidth(0.15);
		graphics.strokeRect(x, y, width, height);
	}

	public static void fillRectForCell(GridCanvasContext gcc, int cellX, int cellY, Color stroke, Color fill, double margin) {
		VisualizationLibrary.fillRect(gcc.getGraphicsContext(), cellX * (gcc.getCellWidth() + gcc.getCellXSpacing()) + margin/2.0, cellY * (gcc.getCellHeight() + gcc.getCellYSpacing()) + margin/2.0, gcc.getCellWidth() - margin/2.0, gcc.getCellHeight() - margin/2.0, stroke, fill);
	}
	
	public static void fillRectForCell(GridCanvasContext gcc, int cellX, int cellY, double magnifier, Color stroke, Color fill, double margin) {
		VisualizationLibrary.fillRect(gcc.getGraphicsContext(), cellX * (gcc.getCellWidth() + gcc.getCellXSpacing()) + margin/2.0, cellY * (gcc.getCellHeight() + gcc.getCellYSpacing()) + margin/2.0, gcc.getCellWidth() * magnifier - margin/2.0, gcc.getCellHeight() * magnifier - margin/2.0, stroke, fill);
	}
	
	public static void drawEmbodiedAgent(GraphicsContext gcc, int x, int y, int width, int height, EmbodiedAgent embodied, Color color) {
		gcc.save(); // saves the current state on stack, including the current transform
        Rotate r = new Rotate(Math.toDegrees(embodied.getHeading()), x + width / 2.0, y + height / 2.0);
        gcc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());

		Grid grid = embodied.getBody();
		double miniWidth = width * 1.0D / grid.getWidthInCells();
		double miniHeight = height * 1.0D / grid.getHeightInCells();

		// First, draw each cell of the body
		for (int i=0; i < grid.getWidthInCells(); i++) {
			for (int u=0; u < grid.getHeightInCells(); u++) {
				gcc.setFill(color);
				gcc.fillRect(x + i*miniWidth, y + u*miniHeight, miniWidth, miniHeight);
			}
		}

		// Then draw all wires 
		for (int i=0; i < grid.getWidthInCells(); i++) {
			for (int u=0; u < grid.getHeightInCells(); u++) {
				Object o = grid.getGridLayer("body").get(i, u);
				if ((o instanceof ConnectedComponent) && (o instanceof HasGridPosition)) {
					gcc.setStroke(Color.MEDIUMVIOLETRED);
					ConnectedComponent cc = (ConnectedComponent)o;
					HasGridPosition pos = (HasGridPosition)o;
					List<ConnectedComponent> outgoingConnections = cc.getOutgoingConnections();
					for (ConnectedComponent outgoingConnection : outgoingConnections) {
						if (outgoingConnection instanceof HasGridPosition) {
							HasGridPosition pos2 = (HasGridPosition)outgoingConnection;
							gcc.strokeLine(x + pos.getCellX() * miniWidth + (miniWidth / 2.0), y + pos.getCellY() * miniHeight + (miniHeight / 2.0), x + pos2.getCellX() * miniWidth + (miniWidth / 2.0), y + pos2.getCellY() * miniHeight + (miniHeight / 2.0));
						}
					}
				}
			}
		}
		
		// Then draw all sen/effs
		for (int i=0; i < grid.getWidthInCells(); i++) {
			for (int u=0; u < grid.getHeightInCells(); u++) {
				Object o = grid.getGridLayer("body").get(i, u);
				if (o instanceof EmbodiedSensor) {
					gcc.setFill(Color.DARKSLATEGRAY);
					gcc.fillPolygon(new double[] { x + i*miniWidth + miniWidth / 2.0, x + i*miniWidth, x + (i+1)*miniWidth-1.0 }, new double[] { y + u*miniHeight, y + (u+1)*miniHeight - 1.0, y + (u+1)*miniHeight - 1.0 }, 3);
				}
				else if (o instanceof EmbodiedEffector) {
					gcc.setFill(Color.DARKSLATEGRAY);
					gcc.fillRect(x + i*miniWidth + 1.0, y + u*miniHeight + 1.0, miniWidth - 2.0, miniHeight - 2.0);
				}
			}
		}
        gcc.restore(); 
	}
	
	public static void drawArrow(GridCanvasContext gcc, HasGridPosition a, HasGridPosition b, Color fill, Color stroke, int thickness, boolean drawPointAtOrigin, boolean drawPointAtDestination) {
		drawArrow(gcc.getGraphicsContext(), 
				(int)Math.round(a.getCellX() * gcc.getXFactor() + gcc.getCellWidth()/2.0), (int)Math.round(a.getCellY() * gcc.getYFactor() + gcc.getCellHeight()/2.0),
				(int)Math.round(b.getCellX() * gcc.getXFactor() + gcc.getCellWidth()/2.0), (int)Math.round(b.getCellY() * gcc.getYFactor() + gcc.getCellHeight()/2.0),
				fill, stroke, thickness, drawPointAtOrigin, drawPointAtDestination); 
	}
	
	public static void drawArrow(GridCanvasContext gcc, HasFineGridPosition a, HasFineGridPosition b, Color fill, Color stroke, int thickness, boolean drawPointAtOrigin, boolean drawPointAtDestination) {
		drawArrow(gcc.getGraphicsContext(), 
				a.getFineX() * gcc.getXFactor() + gcc.getCellWidth()/2.0, a.getFineY() * gcc.getYFactor() + gcc.getCellHeight()/2.0,
				b.getFineX() * gcc.getXFactor() + gcc.getCellWidth()/2.0, b.getFineY() * gcc.getYFactor() + gcc.getCellHeight()/2.0,
				fill, stroke, thickness, drawPointAtOrigin, drawPointAtDestination); 
	}
	
	public static void drawArrow(GridCanvasContext gcc, double ax, double ay, double bx, double by, Color fill, Color stroke, int thickness, boolean drawPointAtOrigin, boolean drawPointAtDestination) {
		drawArrow(gcc.getGraphicsContext(), 
				ax * gcc.getXFactor() + gcc.getCellWidth()/2.0, ay * gcc.getYFactor() + gcc.getCellHeight()/2.0,
				bx * gcc.getXFactor() + gcc.getCellWidth()/2.0, by * gcc.getYFactor() + gcc.getCellHeight()/2.0,
				fill, stroke, thickness, drawPointAtOrigin, drawPointAtDestination); 
	}
	
	public static void drawArrow(GraphicsContext graphics, double x1, double y1, double x2, double y2, Color fill, Color stroke, int thickness, boolean drawPointAtOrigin, boolean drawPointAtDestination) {
		graphics.setStroke(stroke);
		graphics.strokeLine(x1, y1, x2, y2);
	}

	public static void drawArrow(GraphicsContext graphics, int x1, int y1, int x2, int y2, Color fill, Color stroke, int thickness, boolean drawPointAtOrigin, boolean drawPointAtDestination) {
		graphics.setStroke(stroke);
		graphics.strokeLine(x1, y1, x2, y2);
//		int numPoints = 4;
//		if (drawPointAtOrigin) numPoints += 3;
//		if (drawPointAtDestination) numPoints += 3;
//		double[] xPoints = new double[numPoints];
//		double[] yPoints = new double[numPoints];
//
//		double arrowHeadHeight = 5.0;
//		double angle = 0.0;
//		if (drawPointAtOrigin) {
//			// The point
//			xPoints[0] = x1;
//			yPoints[0] = y2;
//			
//			// The extent of arrow on left/top side
//			xPoints[1] = x1 + arrowHeadHeight * Math.cos(angle);
//			yPoints[1] = y1 + arrowHeadHeight * Math.sin(angle);
//			
//			// The extent of arrow on left/top side
//
//		}
//		// First, draw the main
//		graphics.setFill(fill);
//		graphics.fillPolygon(xPoints, yPoints, numPoints);
	}
	
	public static int getGraphicXForCellX(GridCanvasContext gcc, int cellX) {
		return cellX * (gcc.getCellWidth() + gcc.getCellXSpacing());
	}
	
	public static int getGraphicYForCellY(GridCanvasContext gcc, int cellY) {
		return cellY * (gcc.getCellHeight() + gcc.getCellYSpacing());
	}
	
	public static int getCellXForGraphicX(GridCanvasContext gcc, double graphicX) {
		return (int)(graphicX / (gcc.getCellWidth() + gcc.getCellXSpacing()));
	}

	public static int getCellYForGraphicY(GridCanvasContext gcc, double graphicY) {
		return (int)(graphicY / (gcc.getCellHeight() + gcc.getCellYSpacing()));
	}

}
