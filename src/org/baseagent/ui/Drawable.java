package org.baseagent.ui;

public interface Drawable {
	/**
	 * Draw to the canvas *before* drawing the agents or beacons or other drawables.
	 * Use this to draw a component's view of the world without overwriting the components.
	 */
	public default void drawBefore(GridCanvasContext gcc) { };
	
	/**
	 * Draw component to the canvas.
	 * @param sc
	 */
	public void draw(GridCanvasContext gcc);
	
	/**
	 * Draw to the canvas *after* drawing the agents or beacons or other drawables.
	 * Use this to draw overlays on top of the components.
	 */
	public default void drawAfter(GridCanvasContext gcc) { };
}
