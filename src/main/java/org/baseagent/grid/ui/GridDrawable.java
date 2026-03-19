package org.baseagent.grid.ui;

public interface GridDrawable {
    /**
     * Draw to the canvas *before* drawing the agents or beacons or other drawables.
     * Use this to draw a component's view of the world without overwriting the
     * components.
     */
    default void drawBefore(GridCanvasContext gcc) { }

    /**
     * Draw component to the canvas.
     * 
     * @param sc
     */
    void draw(GridCanvasContext gcc);

    /**
     * Draw to the canvas *after* drawing the agents or beacons or other drawables.
     * Use this to draw overlays on top of the components.
     */
    default void drawAfter(GridCanvasContext gcc) { }
}
