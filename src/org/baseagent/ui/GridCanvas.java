package org.baseagent.ui;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.baseagent.grid.Grid;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

public class GridCanvas extends Canvas {
    private Grid grid;
    private String id = DEFAULT_GRID_CANVAS_ID;
    private GridCanvasContext gcc;
    private GridLayerRenderer defaultGridCanvasRenderer;
    private List<String> orderedListOfLayerNames;
    private Map<String, GridLayerRenderer> renderersByName;
    private List<Drawable> customDrawables;
    private List<Toast> toasts;
    boolean drawCustomDrawables = true;
    // View state in world pixel coordinates
    private double viewOffsetX = 0.0;
    private double viewOffsetY = 0.0;
    private double zoomScale = 1.0;
    private boolean panning = false;
    private double lastMouseX, lastMouseY;

    public GridCanvas(Grid grid) {
        this(grid, 5, 5, 0, 0);
    }

    public GridCanvas(Grid grid, int cellWidth, int cellHeight) {
        this(grid, cellWidth, cellHeight, 0, 0);
    }

    public GridCanvas(Grid grid, int cellWidth, int cellHeight, int cellXSpacing, int cellYSpacing) {
        super(grid.getWidthInCells() * cellWidth + (grid.getWidthInCells()-1) * cellXSpacing, 
              grid.getHeightInCells() * cellHeight + (grid.getHeightInCells()-1) * cellYSpacing);
        this.grid = grid;
        this.gcc = new GridCanvasContext(null, grid, this, cellWidth, cellHeight, cellXSpacing, cellYSpacing);
        this.orderedListOfLayerNames = new ArrayList<>();
        this.renderersByName = new HashMap<>();
        this.customDrawables = new ArrayList<>();
        this.toasts = new ArrayList<>();
        
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                GridCanvas.this.update();
            }
        };
        timer.start();

        // Mouse handlers for pan/zoom
        this.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.MIDDLE || (e.getButton() == MouseButton.PRIMARY && e.isShiftDown())) {
                panning = true;
                lastMouseX = e.getX();
                lastMouseY = e.getY();
            }
        });

        this.setOnMouseReleased(e -> { panning = false; });

        this.setOnMouseDragged(e -> {
            if (panning) {
                double dx = e.getX() - lastMouseX;
                double dy = e.getY() - lastMouseY;
                // dragging moves the view: subtract deltas from view offset
                viewOffsetX -= dx;
                viewOffsetY -= dy;
                lastMouseX = e.getX();
                lastMouseY = e.getY();
            }
        });

        this.setOnScroll((ScrollEvent e) -> {
            if (e.isControlDown()) {
                // zoom keeping cursor anchored
                double mouseX = e.getX();
                double mouseY = e.getY();
                double prevScale = zoomScale;
                double factor = Math.pow(2.0, e.getDeltaY() / 360.0);
                zoomScale = zoomScale * factor;

                // compute world pixel under cursor at previous scale
                double worldX = viewOffsetX + mouseX;
                double worldY = viewOffsetY + mouseY;
                double pxBase = worldX / prevScale;
                double pyBase = worldY / prevScale;

                // compute new world pixel for same point at new scale
                double newWorldX = pxBase * zoomScale;
                double newWorldY = pyBase * zoomScale;
                viewOffsetX = newWorldX - mouseX;
                viewOffsetY = newWorldY - mouseY;
            } else {
                // plain wheel pans vertically
                viewOffsetY -= e.getDeltaY();
            }
            e.consume();
        });
    }

    public GridCanvas(String id, Grid grid) {
        this(id, grid, 5, 5, 0, 0);
    }

    public GridCanvas(String id, Grid grid, int cellWidth, int cellHeight) {
        this(id, grid, cellWidth, cellHeight, 0, 0);
    }

    public GridCanvas(String id, Grid grid, int cellWidth, int cellHeight, int cellXSpacing, int cellYSpacing) {
        this(grid, cellWidth, cellHeight, cellXSpacing, cellYSpacing);
        this.id = id;
    }

    public String getGridCanvasId() {
        return this.id;
    }
    
    public Grid getGrid() {
        return this.grid;
    }
    
    public void setGrid(Grid grid) {
        this.grid = grid;
    }
    
    public GridCanvasContext getGridCanvasContext() {
        return this.gcc;
    }
    
    public void addGridLayerRenderer(GridLayerRenderer r) {
        addGridLayerRenderer(Grid.DEFAULT_GRID_LAYER, r);
    }

    public void addGridLayerRenderer(String layerName, GridLayerRenderer r) {
        orderedListOfLayerNames.add(layerName);
        renderersByName.put(layerName, r);
    }

    public void removeGridLayerRenderer(String layerName, GridLayerRenderer r) {
        orderedListOfLayerNames.remove(layerName);
        renderersByName.remove(layerName);
    }
    
    public void addToast(Toast toast) {
        this.toasts.add(toast);
    }
    
    public void removeToast(Toast toast) {
        this.toasts.remove(toast);
    }
    
    public List<Toast> getToasts() {
        return this.toasts;
    }
    
    public List<Drawable> getCustomDrawables() {
        return this.customDrawables;
    }
    
    public boolean willDrawCustomDrawables() {
        return this.drawCustomDrawables;
    }

    public void setDrawCustomDrawables(boolean drawCustom) {
        this.drawCustomDrawables = drawCustom;
    }
    
    public void setGridRenderer(GridLayerRenderer defaultGridCanvasRenderer) {
        this.defaultGridCanvasRenderer = defaultGridCanvasRenderer;
    }
    
    public GridLayerRenderer getGridRenderer() {
        return this.defaultGridCanvasRenderer;
    }
    
    public void update() {
        GraphicsContext gc = this.getGraphicsContext2D();
        gcc.setGraphicsContext(gc);
        // expose view state to renderers
        gcc.setZoom(zoomScale);
        gcc.setViewOffsetX(viewOffsetX);
        gcc.setViewOffsetY(viewOffsetY);
        // also place these in properties map for backward compatibility
        gcc.getProperties().put("viewOffsetX", viewOffsetX);
        gcc.getProperties().put("viewOffsetY", viewOffsetY);
        gcc.getProperties().put("zoomScale", zoomScale);

        // compute visible cell bounds and expose to renderers
        int[] vis = gcc.getVisibleCellBounds(this.getWidth(), this.getHeight());
        // minCol,minRow,maxCol,maxRow
        gcc.getProperties().put("visibleCellBounds", vis);
         
        // Clear everything
        Color backgroundColor = Color.WHITE;
        if (gcc.getColorPalette().size() > 0) backgroundColor = gcc.getColorPalette().get(0);
        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, this.getWidth(), this.getHeight());
        
        // Draw the default renderer for the grid itself
        if (defaultGridCanvasRenderer != null) {
            defaultGridCanvasRenderer.draw(gcc, grid.getGridLayer(Grid.DEFAULT_GRID_LAYER), this.getWidth(), this.getHeight());
        }
        
        // Then, draw the grid layers
        for (String layerName : orderedListOfLayerNames) {
            GridLayerRenderer renderer = renderersByName.get(layerName);
            if ((renderer != null) && (grid.getGridLayer(layerName) != null)) {
                renderer.draw(gcc, grid.getGridLayer(layerName), this.getWidth(), this.getHeight());
            }
        }

        update0(gcc);
        
        // Then draw any custom art
        if (drawCustomDrawables) {
            customDrawables.stream().forEach(drawable -> drawable.drawBefore(gcc));
            customDrawables.stream().forEach(drawable -> drawable.draw(gcc));
            customDrawables.stream().forEach(drawable -> drawable.drawAfter(gcc));
        }
    }
    
    protected void update0(GridCanvasContext gcc) { }
    
    public void saveSnapshot(String filenameBeginning) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                File file = new File(generateSnapshotFilename(filenameBeginning));
                WritableImage writableImage = new WritableImage((int)getWidth(), (int)getHeight());
                GridCanvas.this.snapshot(null, writableImage);
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                try {
                    ImageIO.write(renderedImage, "png", file);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }
    
    protected String generateSnapshotFilename(String filenameBeginning) {
        return filenameBeginning + ".png";
    }
    
    public static final String DEFAULT_GRID_CANVAS_ID = "DEFAULT_GRID_CANVAS_ID";
}