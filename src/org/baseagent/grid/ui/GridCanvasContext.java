package org.baseagent.grid.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.baseagent.grid.Grid;
import org.baseagent.sim.Simulation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GridCanvasContext {
    private GraphicsContext graphicsContext;
    private List<Color> colorPalette;
    private Map<String, Object> properties;
    private Grid grid;
    private GridCanvas gridCanvas;
    private Simulation simulation;
    private int cellWidth, cellHeight;
    private int cellXSpacing, cellYSpacing;
    private double zoom;
    private double viewOffsetX = 0.0;
    private double viewOffsetY = 0.0;

    public GridCanvasContext(Simulation simulation, Grid grid, GridCanvas gridCanvas, int cellWidth, int cellHeight, int cellXSpacing, int cellYSpacing) {
        this.simulation = simulation;
        this.grid = grid;
        this.gridCanvas = gridCanvas;
        this.colorPalette = new ArrayList<>();
        this.properties = new HashMap<>();
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.cellXSpacing = cellXSpacing;
        this.cellYSpacing = cellYSpacing;
        this.zoom = 1.0;
    }
    
    public Simulation getSimulation() {
        return this.simulation;
    }
    
    public Grid getGrid() {
        return this.grid;
    }
    
    public GridCanvas getGridCanvas() {
        return this.gridCanvas;
    }
    
    public int getCellWidth() {
        return cellWidth;
    }

    public void setCellWidth(int cellWidth) {
        this.cellWidth = cellWidth;
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public void setCellHeight(int cellHeight) {
        this.cellHeight = cellHeight;
    }

    public int getCellXSpacing() {
        return cellXSpacing;
    }

    public void setCellXSpacing(int cellXSpacing) {
        this.cellXSpacing = cellXSpacing;
    }

    public int getCellYSpacing() {
        return cellYSpacing;
    }

    public void setCellYSpacing(int cellYSpacing) {
        this.cellYSpacing = cellYSpacing;
    }

    /** Returns a factor of (the cell width plus the cell X spacing) times the zoom. */ 
    public double getXFactor() {
        return (getCellWidth() + getCellXSpacing()) * getZoom();
    }
    
    /** Returns a factor of (the cell height plus the cell Y spacing) times the zoom. */ 
    public double getYFactor() {
        return (getCellHeight() + getCellYSpacing()) * getZoom();
    }
    
    public void setGraphicsContext(GraphicsContext gc) {
        this.graphicsContext = gc;
    }
    
    public GraphicsContext getGraphicsContext() {
        return this.graphicsContext;
    }
    
    public void setColorPalette(List<Color> colors) {
        this.colorPalette = colors;
    }
    
//    public void setColorPalette(Color... colors) {
//        this.colorPalette = List.of(colors);
//    }

    public List<Color> getColorPalette() {
        return this.colorPalette;
    }

    public void setProperties(Map<String, Object> props) {
        this.properties = props;
    }
    
    public Map<String, Object> getProperties() {
        return this.properties;
    }
    
    public void setZoom(double zoom) {
        this.zoom = zoom;
    }
    
    public double getZoom() {
        return this.zoom;
    }
    
    public void changeZoom(double delta) {
        setZoom(getZoom() + delta);
    }
    
    /** View offset in world pixels (top-left world pixel shown in the canvas). */
    public double getViewOffsetX() { return this.viewOffsetX; }
    public double getViewOffsetY() { return this.viewOffsetY; }
    public void setViewOffsetX(double x) { this.viewOffsetX = x; }
    public void setViewOffsetY(double y) { this.viewOffsetY = y; }

    /**
     * Compute the inclusive bounds of visible cells for the current view parameters.
     * Returns int[]{minCol, minRow, maxCol, maxRow} clamped to the grid extents.
     */
    public int[] getVisibleCellBounds(double canvasWidth, double canvasHeight) {
        double xFactor = getXFactor();
        double yFactor = getYFactor();
        int cols = (grid == null) ? 0 : grid.getWidthInCells();
        int rows = (grid == null) ? 0 : grid.getHeightInCells();

        double leftWorld = viewOffsetX;
        double topWorld = viewOffsetY;
        double rightWorld = viewOffsetX + canvasWidth;
        double bottomWorld = viewOffsetY + canvasHeight;

        int minCol = (int)Math.floor(leftWorld / xFactor);
        int maxCol = (int)Math.floor(rightWorld / xFactor);
        int minRow = (int)Math.floor(topWorld / yFactor);
        int maxRow = (int)Math.floor(bottomWorld / yFactor);

        if (minCol < 0) minCol = 0; if (minRow < 0) minRow = 0;
        if (maxCol >= cols) maxCol = Math.max(0, cols - 1);
        if (maxRow >= rows) maxRow = Math.max(0, rows - 1);

        return new int[] { minCol, minRow, maxCol, maxRow };
    }

    /**
     * Convert a cell upper-left to world pixel coordinates (untranslated, scaled by zoom).
     * Returns {worldX, worldY} where worldX/worldY are in world pixels.
     */
    public double[] cellToWorld(int col, int row) {
        double wx = col * getXFactor();
        double wy = row * getYFactor();
        return new double[] { wx, wy };
    }

    /**
     * Convert the center of a cell to world pixel coordinates.
     * Returns {worldX, worldY} for the center point in world pixels.
     */
    public double[] cellCenterToWorld(int col, int row) {
        double[] ul = cellToWorld(col, row);
        double centerX = ul[0] + (getCellWidth() * getZoom()) / 2.0;
        double centerY = ul[1] + (getCellHeight() * getZoom()) / 2.0;
        return new double[] { centerX, centerY };
    }

    /**
     * Convert world pixel coordinates to screen pixel coordinates using the current view offset.
     * Screen coords are what should be passed to GraphicsContext drawing calls.
     */
    public double[] worldToScreen(double worldX, double worldY) {
        return new double[] { worldX - viewOffsetX, worldY - viewOffsetY };
    }

    /** Convert screen pixel coordinates to world pixel coordinates. */
    public double[] screenToWorld(double screenX, double screenY) {
        return new double[] { screenX + viewOffsetX, screenY + viewOffsetY };
    }

    /**
     * Convert a cell to screen pixel coordinates for its center. Returns {screenX, screenY}.
     */
    public double[] cellToScreen(int col, int row) {
        double[] world = cellCenterToWorld(col, row);
        return worldToScreen(world[0], world[1]);
    }

    /** Convert cell upper-left to screen coords. */
    public double[] cellUpperLeftToScreen(int col, int row) {
        double[] world = cellToWorld(col, row);
        return worldToScreen(world[0], world[1]);
    }

    /** Convert screen coords to the cell indices (col,row) that contain that screen point. */
    public int[] screenToCell(double screenX, double screenY) {
        double[] world = screenToWorld(screenX, screenY);
        double xFactor = getXFactor();
        double yFactor = getYFactor();
        int col = (int)Math.floor(world[0] / xFactor);
        int row = (int)Math.floor(world[1] / yFactor);
        int cols = (grid == null) ? 0 : grid.getWidthInCells();
        int rows = (grid == null) ? 0 : grid.getHeightInCells();
        if (col < 0) col = 0; if (col >= cols) col = Math.max(0, cols-1);
        if (row < 0) row = 0; if (row >= rows) row = Math.max(0, rows-1);
        return new int[] { col, row };
    }

    /**
     * Create an EmbeddedContext for an embedded grid that is drawn inside the given parent cell
     * (parentCol,parentRow). The embedded grid has embeddedCols x embeddedRows cells. extraScale
     * is a multiplier: 1.0 means the embedded grid exactly fills the parent cell; >1.0 zooms in;
     * <1.0 makes the embedded grid smaller than the parent cell and centers it.
     */
    public EmbeddedContext createEmbeddedContext(int parentCol, int parentRow, int embeddedCols, int embeddedRows, double extraScale) {
        return new EmbeddedContext(this, parentCol, parentRow, embeddedCols, embeddedRows, extraScale);
    }

    /** Helper that represents an embedded-grid coordinate space inside a parent cell. */
    public static class EmbeddedContext {
        private GridCanvasContext parent;
        private int parentCol, parentRow;
        private int embeddedCols, embeddedRows;
        private double extraScale;
        private double parentULWorldX, parentULWorldY; // world coords of parent cell upper-left
        private double parentWidthWorld, parentHeightWorld;
        private double embeddedCellWorldW, embeddedCellWorldH;
        private double offsetWorldX, offsetWorldY; // centering offset inside parent

        EmbeddedContext(GridCanvasContext parent, int parentCol, int parentRow, int embeddedCols, int embeddedRows, double extraScale) {
            this.parent = parent;
            this.parentCol = parentCol; this.parentRow = parentRow;
            this.embeddedCols = Math.max(1, embeddedCols); this.embeddedRows = Math.max(1, embeddedRows);
            this.extraScale = (extraScale <= 0.0) ? 1.0 : extraScale;

            double[] ul = parent.cellToWorld(parentCol, parentRow);
            this.parentULWorldX = ul[0]; this.parentULWorldY = ul[1];
            this.parentWidthWorld = parent.getXFactor();
            this.parentHeightWorld = parent.getYFactor();

            double totalEmbeddedWidth = parentWidthWorld * this.extraScale;
            double totalEmbeddedHeight = parentHeightWorld * this.extraScale;
            this.embeddedCellWorldW = totalEmbeddedWidth / this.embeddedCols;
            this.embeddedCellWorldH = totalEmbeddedHeight / this.embeddedRows;

            // center the embedded grid inside the parent cell if extraScale != 1
            this.offsetWorldX = (parentWidthWorld - totalEmbeddedWidth) / 2.0;
            this.offsetWorldY = (parentHeightWorld - totalEmbeddedHeight) / 2.0;
        }

        /** Convert embedded cell upper-left to world coordinates. */
        public double[] embeddedCellUpperLeftToWorld(int subCol, int subRow) {
            double wx = parentULWorldX + offsetWorldX + subCol * embeddedCellWorldW;
            double wy = parentULWorldY + offsetWorldY + subRow * embeddedCellWorldH;
            return new double[] { wx, wy };
        }

        /** Convert embedded cell center to world coordinates. */
        public double[] embeddedCellCenterToWorld(int subCol, int subRow) {
            double[] ul = embeddedCellUpperLeftToWorld(subCol, subRow);
            double cx = ul[0] + embeddedCellWorldW / 2.0;
            double cy = ul[1] + embeddedCellWorldH / 2.0;
            return new double[] { cx, cy };
        }

        /** Convert embedded cell center to screen coordinates. */
        public double[] embeddedCellCenterToScreen(int subCol, int subRow) {
            double[] world = embeddedCellCenterToWorld(subCol, subRow);
            return parent.worldToScreen(world[0], world[1]);
        }

        /** Convert embedded cell upper-left to screen coordinates. */
        public double[] embeddedCellUpperLeftToScreen(int subCol, int subRow) {
            double[] world = embeddedCellUpperLeftToWorld(subCol, subRow);
            return parent.worldToScreen(world[0], world[1]);
        }

        /** Screen pixel width/height of an embedded cell. */
        public double getEmbeddedCellScreenWidth() {
            // embeddedCellWorldW is in world pixels; convert to screen by subtracting offsets already used via worldToScreen
            return this.embeddedCellWorldW;
        }

        public double getEmbeddedCellScreenHeight() { return this.embeddedCellWorldH; }

        /** Which embedded cell contains the given screen point? Returns {subCol, subRow}. */
        public int[] screenToEmbeddedCell(double screenX, double screenY) {
            double[] world = parent.screenToWorld(screenX, screenY);
            double localX = world[0] - (parentULWorldX + offsetWorldX);
            double localY = world[1] - (parentULWorldY + offsetWorldY);
            int subCol = (int)Math.floor(localX / embeddedCellWorldW);
            int subRow = (int)Math.floor(localY / embeddedCellWorldH);
            if (subCol < 0) subCol = 0; if (subCol >= embeddedCols) subCol = embeddedCols-1;
            if (subRow < 0) subRow = 0; if (subRow >= embeddedRows) subRow = embeddedRows-1;
            return new int[] { subCol, subRow };
        }
    }
}