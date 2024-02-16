package org.baseagent.ui;

import java.util.HashMap;
import java.util.Map;

import org.baseagent.grid.GridPosition;

import javafx.scene.canvas.Canvas;

/**
 * A Field is a collection of GridCanvas and Controller objects
 * used to provide a user with a single user interface consisting
 * of all of these components.
 * 
 * There is no layout manager associated with a Field. It uses
 * absolute positioning, and it does not expect the interface 
 * to be resized.
 * 
 * Future work may involve creating Fields for different devices
 * and orientations. 
 *
 */
public class Field extends Canvas {
	private Map<String, GridCanvas> namedCanvases;
	private Map<String, GridPosition> canvasPosition;
	
	public Field(int width, int height) {
		super(width, height);
		namedCanvases = new HashMap<>();
	}

	public void add(String canvasName, GridCanvas canvas, int x, int y) {
		namedCanvases.put(canvasName, canvas);
		canvasPosition.put(canvasName, new GridPosition(x, y));
		getScene().getRoot().getChildren().add(canvas);
	}
	
}
