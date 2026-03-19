package org.baseagent.grid.ui;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.baseagent.grid.Grid;
import org.baseagent.grid.GridLayer;
import org.baseagent.ui.Toast;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

public class GridCanvas extends Canvas {
    private Grid grid;
    private String id = DEFAULT_GRID_CANVAS_ID;
    private GridCanvasContext gcc;
    private GridLayerRenderer defaultGridCanvasRenderer;
    private List<String> orderedListOfLayerNames;
    private Map<String, GridLayerRenderer> renderersByName;
    private Map<String, Boolean> isRendererVisibleByName;
    private List<GridDrawable> customDrawables;
    private List<Toast> toasts;
    boolean drawCustomDrawables = true;
    // View state in world pixel coordinates
    private double viewOffsetX = 0.0;
    private double viewOffsetY = 0.0;
    private double zoomScale = 1.0;
    private boolean panning = false;
    private double lastMouseX, lastMouseY;
    private boolean pannedSincePress = false;

    public GridCanvas(Grid grid) {
        this(grid, 5, 5, 0, 0);
    }

    public GridCanvas(Grid grid, int cellWidth, int cellHeight) {
        this(grid, cellWidth, cellHeight, 0, 0);
    }

    public GridCanvas(Grid grid, int cellWidth, int cellHeight, int cellXSpacing, int cellYSpacing) {
        super(grid.getWidthInCells() * cellWidth + (grid.getWidthInCells() - 1) * cellXSpacing,
                grid.getHeightInCells() * cellHeight + (grid.getHeightInCells() - 1) * cellYSpacing);
        this.grid = grid;
        this.gcc = new GridCanvasContext(null, grid, this, cellWidth, cellHeight, cellXSpacing, cellYSpacing);
        this.orderedListOfLayerNames = new ArrayList<>();
        this.renderersByName = new HashMap<>();
        this.isRendererVisibleByName = new HashMap<>();
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
                pannedSincePress = false;
            }
        });

        this.setOnMouseReleased(e -> {
            panning = false;
        });

        this.setOnMouseDragged(e -> {
            if (panning) {
                double dx = e.getX() - lastMouseX;
                double dy = e.getY() - lastMouseY;
                // dragging moves the view: subtract deltas from view offset
                viewOffsetX -= dx;
                viewOffsetY -= dy;
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                pannedSincePress = true;
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

        // High-level grid-cell click handling: translates a MouseEvent into a cell
        // coordinate
        // and invokes a registered CellClickHandler with a map of layer contents and
        // agents.
        this.setOnMouseClicked((MouseEvent e) -> {
            if (this.cellClickHandler == null)
                return;
            // suppress dispatch if the user just panned (dragged) the canvas
            if (pannedSincePress) {
                pannedSincePress = false;
                return;
            }
            // Convert scene coords to canvas-local coords to handle any transforms
            javafx.geometry.Point2D local = this.sceneToLocal(e.getSceneX(), e.getSceneY());
            int[] cell = this.gcc.screenToCell(local.getX(), local.getY());
            int cx = cell[0];
            int cy = cell[1];

            // gather layer contents
            java.util.Map<String, java.util.List<Object>> layerContents = new java.util.HashMap<>();
            if (this.grid != null) {
                for (GridLayer<?> layer : this.grid.getGridLayers()) {
                    String lname = layer.getLayerName();
                    java.util.List<Object> things = new java.util.ArrayList<>();
                    Object v = null;
                    try {
                        v = layer.current().get(cx, cy);
                    } catch (Exception ex) {
                        v = null;
                    }
                    if (v != null)
                        things.add(v);
                    layerContents.put(lname, things);
                }
            }

            // gather agents located at this cell (implements HasGridPosition)
            java.util.List<Object> agents = new java.util.ArrayList<>();
            if (this.gcc != null && this.gcc.getSimulation() != null) {
                for (org.baseagent.Agent a : this.gcc.getSimulation().getAgents()) {
                    if (a instanceof org.baseagent.grid.HasGridPosition) {
                        @SuppressWarnings("rawtypes")
                        org.baseagent.grid.HasGridPosition hp = (org.baseagent.grid.HasGridPosition) a;
                        try {
                            if (hp.getCellX() == cx && hp.getCellY() == cy) {
                                agents.add(a);
                            }
                        } catch (Exception ex) {
                            // ignore agents that don't expose cell coords
                        }
                    }
                }
            }

            CellClickEvent ev = new CellClickEvent(cx, cy, e.getButton(), e.isShiftDown(), layerContents, agents, e);
            this.cellClickHandler.handle(ev);
        });
    }

    // Listener registration for higher-level cell click events
    private CellClickHandler cellClickHandler = null;

    public void setOnCellClicked(CellClickHandler h) {
        this.cellClickHandler = h;
    }

    /** High-level event object delivered when a grid cell is clicked. */
    public static class CellClickEvent {
        private final int cellX, cellY;
        private final MouseButton button;
        private final boolean shiftDown;
        private final java.util.Map<String, java.util.List<Object>> layerContents;
        private final java.util.List<Object> agents;
        private final MouseEvent originalEvent;

        public CellClickEvent(int cellX, int cellY, MouseButton button, boolean shiftDown,
                java.util.Map<String, java.util.List<Object>> layerContents, java.util.List<Object> agents,
                MouseEvent originalEvent) {
            this.cellX = cellX;
            this.cellY = cellY;
            this.button = button;
            this.shiftDown = shiftDown;
            this.layerContents = layerContents;
            this.agents = agents;
            this.originalEvent = originalEvent;
        }

        public int getCellX() {
            return cellX;
        }

        public int getCellY() {
            return cellY;
        }

        public MouseButton getButton() {
            return button;
        }

        public boolean isShiftDown() {
            return shiftDown;
        }

        public java.util.Map<String, java.util.List<Object>> getLayerContents() {
            return layerContents;
        }

        public java.util.List<Object> getAgents() {
            return agents;
        }

        public MouseEvent getOriginalEvent() {
            return originalEvent;
        }
    }

    /** Listener interface for cell click events. */
    public interface CellClickHandler {
        void handle(CellClickEvent e);
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
        isRendererVisibleByName.put(layerName, true);
    }

    public void removeGridLayerRenderer(String layerName, GridLayerRenderer r) {
        orderedListOfLayerNames.remove(layerName);
        renderersByName.remove(layerName);
        isRendererVisibleByName.remove(layerName);
    }

    public void setRendererVisibility(String layerName, boolean isVisible) {
        isRendererVisibleByName.put(layerName, isVisible);
    }

    public boolean isRendererVisible(String layerName) {
        return isRendererVisibleByName.getOrDefault(layerName, false);
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

    public List<GridDrawable> getCustomDrawables() {
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

    /**
     * Create a JavaFX panel containing a checkbox for each registered renderer. The
     * checkbox is checked when the renderer is visible and toggling it will update
     * the renderer visibility in this GridCanvas. The returned ScrollPane will have
     * the provided width/height as its preferred size and will show scrollbars when
     * the content exceeds those bounds.
     *
     * Note: the panel is a snapshot of the current renderers; if renderers are
     * added or removed later the panel should be recreated to reflect the changes.
     */
    public ScrollPane createRendererVisibilityPanel(double width, double height) {
        VBox vbox = new VBox(4);
        vbox.setPadding(new Insets(6));

        // Use the ordered list to present renderers in the same order they were added
        for (String layerName : orderedListOfLayerNames) {
            GridLayerRenderer renderer = renderersByName.get(layerName);
            if (renderer == null)
                continue;
            CheckBox cb = new CheckBox(layerName);
            cb.setSelected(isRendererVisible(layerName));
            cb.selectedProperty().addListener((obs, oldV, newV) -> setRendererVisibility(layerName, newV));
            vbox.getChildren().add(cb);
        }

        ScrollPane sp = new ScrollPane(vbox);
        sp.setFitToWidth(true);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sp.setPrefWidth(width);
        sp.setPrefHeight(height);
        return sp;
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
        if (gcc.getColorPalette().size() > 0)
            backgroundColor = gcc.getColorPalette().get(0);
        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, this.getWidth(), this.getHeight());

        // Draw the default renderer for the grid itself
        if (defaultGridCanvasRenderer != null) {
            defaultGridCanvasRenderer.draw(gcc, grid.getGridLayer(Grid.DEFAULT_GRID_LAYER), this.getWidth(),
                    this.getHeight());
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

    protected void update0(GridCanvasContext gcc) {
    }

    public void saveSnapshot(String filenameBeginning) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                File file = new File(generateSnapshotFilename(filenameBeginning));
                WritableImage writableImage = new WritableImage((int) getWidth(), (int) getHeight());
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